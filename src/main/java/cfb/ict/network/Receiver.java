package cfb.ict.network;

import cfb.ict.Properties;
import cfb.ict.cryptography.Hash;
import cfb.ict.tangle.Tangle;
import cfb.ict.tangle.Transaction;
import cfb.ict.tangle.Vertex;
import cfb.ict.utilities.Converter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.SortedMap;

import static cfb.ict.network.Node.PACKET_LENGTH;
import static cfb.ict.network.Node.PACKET_SIZE_IN_BYTES;

public class Receiver extends Thread {

    final Properties properties;

    final SortedMap<Neighbor, Neighbor> neighbors;
    final DatagramSocket socket;

    final Tangle tangle;

    Receiver(final Properties properties,
             final SortedMap<Neighbor, Neighbor> neighbors, final DatagramSocket socket,
             final Tangle tangle) {

        this.properties = properties;

        this.neighbors = neighbors;
        this.socket = socket;

        this.tangle = tangle;
    }

    @Override
    public void run() {

        final DatagramPacket packet = new DatagramPacket(new byte[PACKET_SIZE_IN_BYTES], PACKET_SIZE_IN_BYTES);
        final byte[] packetTrits = new byte[Converter.lengthInTrits(PACKET_SIZE_IN_BYTES)];

        while (!properties.shuttingDown) {

            try {

                socket.receive(packet);

                final Neighbor sender = new Neighbor((InetSocketAddress) packet.getSocketAddress(), properties.neighborTransactionQueueCapacity);

                Neighbor neighbor = neighbors.get(sender);

                if (neighbor == null) {

                    neighbor = sender;

                } else {

                    if (neighbor.isBlacklisted) {

                        continue;
                    }
                }

                if (packet.getLength() % Hash.SIZE_IN_BYTES == 0) { // The packet length must be a multiple of 243 trits

                    int offset;
                    for (offset = 0; offset < PACKET_LENGTH - Converter.lengthInTrits(packet.getLength()); offset++) {

                        packetTrits[offset] = 0;
                    }
                    Converter.convertBytesToTrits(packet.getData(), 0, packet.getLength(), packetTrits, offset);

                    try {

                        final Transaction transaction = new Transaction(packetTrits);

                        if (tangle.put(transaction)) {

                            neighbor.pushToQueue(transaction.hash);
                        }

                        final Hash requestedTransactionHash = new Hash(packetTrits, Transaction.LENGTH, Hash.LENGTH);
                        if (!requestedTransactionHash.equals(Hash.NULL)) {

                            final Vertex requestedTransactionVertex = tangle.get(requestedTransactionHash);
                            if (requestedTransactionHash.equals(transaction.hash)) {

                                final Hash bestReferrerHash = tangle.bestReferrerHash(requestedTransactionVertex);
                                if (bestReferrerHash != null) {

                                    neighbor.pushToQueue(bestReferrerHash);
                                }

                            } else {

                                if (requestedTransactionVertex != null && requestedTransactionVertex.transaction != null) {

                                    neighbor.pushToQueue(requestedTransactionVertex.transaction.hash);
                                }
                            }
                        }

                    } catch (final RuntimeException e) {

                        neighbor.isBlacklisted = true;
                    }

                } else {

                    neighbor.isBlacklisted = true;
                }

            } catch (final IOException e) {

                e.printStackTrace();
            }
        }
    }
}
