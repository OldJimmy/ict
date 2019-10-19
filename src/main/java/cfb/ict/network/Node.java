package cfb.ict.network;

import cfb.ict.Properties;
import cfb.ict.cryptography.Hash;
import cfb.ict.tangle.Tangle;
import cfb.ict.tangle.Transaction;
import cfb.ict.utilities.Converter;

import java.io.IOException;
import java.net.*;
import java.util.Comparator;
import java.util.Queue;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Node {

    static final int PACKET_LENGTH = Transaction.LENGTH + Hash.LENGTH;
    static final int PACKET_SIZE_IN_BYTES = Converter.sizeInBytes(PACKET_LENGTH);

    final Properties properties;

    public final SortedMap<Neighbor, Neighbor> neighbors = new ConcurrentSkipListMap<Neighbor, Neighbor>(new Comparator<Neighbor>() {

        @Override
        public int compare(final Neighbor neighbor1, final Neighbor neighbor2) {

            if (neighbor1.address.equals(neighbor2.address)) {

                return 0;

            } else {

                if (neighbor1.latestActivityTime == neighbor2.latestActivityTime) {

                    return neighbor1.tiebreakerValue < neighbor2.tiebreakerValue ? -1 : 1;

                } else {

                    return neighbor1.latestActivityTime > neighbor2.latestActivityTime ? -1 : 1;
                }
            }
        }

    });
    DatagramSocket socket;

    final Tangle tangle;
    final Queue<Hash> transactionHashes;

    Sender sender;
    Receiver receiver;

    public Node(final Properties properties, final Tangle tangle) {

        this.properties = properties;

        this.tangle = tangle;
        transactionHashes = new LinkedBlockingQueue<Hash>();
    }

    public void launch() {

        try {

            for (final String neighborUri : properties.neighbors.split(";")) {

                try {

                    final URI uri = new URI(neighborUri);
                    if (uri.getScheme().equals("udp")) {

                        final Neighbor neighbor = new Neighbor(uri.getHost(), uri.getPort(), properties.neighborTransactionQueueCapacity);
                        neighbors.put(neighbor, neighbor);
                    }

                } catch (final URISyntaxException e) {

                    throw new RuntimeException(e);
                }
            }

            socket = new DatagramSocket(properties.port, InetAddress.getByName(properties.host));

            sender = new Sender(properties, neighbors, socket, tangle, transactionHashes);
            receiver = new Receiver(properties, neighbors, socket, tangle);
            sender.start();
            receiver.start();

        } catch (final IOException e) {

            throw new RuntimeException(e);
        }
    }

    public void shutDown() {

        socket.close();
    }

    public void pushToQueue(final Hash transactionHash) {

        transactionHashes.offer(transactionHash);
    }
}
