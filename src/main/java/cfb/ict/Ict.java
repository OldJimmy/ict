package cfb.ict;

import java.io.FileInputStream;
import java.io.IOException;

import cfb.ict.ixi.IXI;
import cfb.ict.network.Neighbor;
import cfb.ict.network.Node;
import cfb.ict.tangle.Tangle;
import cfb.ict.utilities.Logger;

public class Ict extends Thread {

    static final String VERSION = "0.9.2";

    private final java.util.Properties properties;
    private Properties extendedProperties;
    private Node node;
    private IXI ixi;

    public Ict(final java.util.Properties properties) {

        this.properties = properties;
    }

    public void launch() {

        start();
    }

    public void shutDown() {

        extendedProperties.shuttingDown = true;
        node.shutDown();

        Logger.log("Ict " + VERSION + " is shut down.");
    }

    @Override
    public void run() {

        Logger.log("Ict " + VERSION + " is launched.");

        extendedProperties = new Properties(properties);

        final Tangle tangle = new Tangle(extendedProperties);

        node = new Node(extendedProperties, tangle);

        node.launch();

        ixi = new IXI(extendedProperties, node, tangle);

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {

                if (!extendedProperties.shuttingDown) {

                    shutDown();
                }
            }

        });

        try {

            while (!extendedProperties.shuttingDown) {

                Logger.log("----------");

                int numberOfActiveNeighbors = 0;
                final long lowestAcceptableLatestActivityTime = System.currentTimeMillis() - extendedProperties.neighborCooldownDuration * 1000L;
                for (final Neighbor neighbor : node.neighbors.keySet()) {

                    if (neighbor.latestActivityTime < lowestAcceptableLatestActivityTime) {

                        break;
                    }

                    numberOfActiveNeighbors++;
                }
                Logger.log("Number of active neighbors = " + numberOfActiveNeighbors + " / " + node.neighbors.size());

                Logger.log("Tangle size = " + tangle.verticesByHash.size());

                Logger.log("----------");

                for (int i = 0; i < 60 && !extendedProperties.shuttingDown; i++) {

                    Thread.sleep(1000);
                }
            }

        } catch (final Exception e) {

            e.printStackTrace();
        }
    }

    public IXI ixi() {

        return ixi;
    }

    public static void main(final String[] args) {

        final java.util.Properties properties = new java.util.Properties();

        try {

            final FileInputStream propertiesInputStream = new FileInputStream(args[0]);
            properties.load(propertiesInputStream);
            propertiesInputStream.close();

        } catch (final IOException e) {

            throw new RuntimeException(e);
        }

        final Ict ict = new Ict(properties);
        ict.launch();
    }
}
