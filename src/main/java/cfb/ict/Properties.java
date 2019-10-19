package cfb.ict;

public class Properties {

    private static final String PREFIX = "cfb.ict.";

    public String host = "localhost";
    public int port = 14265;
    public String neighbors;
    public int neighborTransactionQueueCapacity = 27;
    public int neighborCooldownDuration = 60; // In seconds

    public int tangleCapacity = 1000000;
    public int tanglePruningScale = 10000;
    public long timestampLowerBoundDelta = 90, timestampUpperBoundDelta = 90; // In seconds

    public volatile boolean shuttingDown;

    Properties(final java.util.Properties properties) {

        host = properties.getProperty(PREFIX + "host", host).trim();
        port = Integer.parseInt(properties.getProperty(PREFIX + "port", Integer.valueOf(port).toString()).trim());
        neighbors = properties.getProperty(PREFIX + "neighbors", neighbors).trim();
        neighborTransactionQueueCapacity = Integer.parseInt(properties.getProperty(PREFIX + "neighborTransactionQueueCapacity", Integer.valueOf(neighborTransactionQueueCapacity).toString()).trim());
        neighborCooldownDuration = Integer.parseInt(properties.getProperty(PREFIX + "neighborCooldownDuration", Integer.valueOf(neighborCooldownDuration).toString()).trim());

        tangleCapacity = Integer.parseInt(properties.getProperty(PREFIX + "tangleCapacity", Integer.valueOf(tangleCapacity).toString()).trim());
        tanglePruningScale = Integer.parseInt(properties.getProperty(PREFIX + "tanglePruningScale", Integer.valueOf(tanglePruningScale).toString()).trim());
        timestampLowerBoundDelta = Long.parseLong(properties.getProperty(PREFIX + "timestampLowerBoundDelta", Long.valueOf(timestampLowerBoundDelta).toString()).trim());
        timestampUpperBoundDelta = Long.parseLong(properties.getProperty(PREFIX + "timestampUpperBoundDelta", Long.valueOf(timestampUpperBoundDelta).toString()).trim());
    }
}
