package cfb.ict.network;

import cfb.ict.cryptography.Hash;

import java.net.*;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Neighbor {

    static final AtomicInteger tiebreaker = new AtomicInteger();

    final InetSocketAddress address;
    boolean isBlacklisted;
    public long latestActivityTime;
    final int tiebreakerValue;
    final Queue<Hash> transactionHashes;

    Neighbor(final String host, final int port, final int queueCapacity) {

        address = new InetSocketAddress(host, port);
        latestActivityTime = System.currentTimeMillis();
        tiebreakerValue = tiebreaker.getAndIncrement();
        transactionHashes = new LinkedBlockingQueue<Hash>(queueCapacity);
    }

    Neighbor(final InetSocketAddress address, final int queueCapacity) {

        this.address = address;
        latestActivityTime = System.currentTimeMillis();
        tiebreakerValue = tiebreaker.getAndIncrement();
        transactionHashes = new LinkedBlockingQueue<Hash>(queueCapacity);
    }

    public void pushToQueue(final Hash transactionHash) {

        transactionHashes.offer(transactionHash);
    }

    public Hash pullFromQueue() {

        return transactionHashes.poll();
    }

    public void clearQueue() {

        transactionHashes.clear();
    }

    void send(final DatagramSocket socket, final DatagramPacket packet) {

        try {

            packet.setSocketAddress(address);
            socket.send(packet);

        } catch (final Exception e) {

            e.printStackTrace();
        }
    }
}
