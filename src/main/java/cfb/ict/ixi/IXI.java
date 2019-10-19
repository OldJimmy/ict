package cfb.ict.ixi;

import cfb.ict.Properties;
import cfb.ict.network.Node;
import cfb.ict.tangle.Tangle;
import cfb.ict.tangle.Transaction;
import cfb.ict.utilities.Converter;

public class IXI {

    final Properties properties;
    final Node node;
    final Tangle tangle;

    public IXI(final Properties properties, final Node node, final Tangle tangle) {

        this.properties = properties;
        this.node = node;
        this.tangle = tangle;
    }

    public byte entangle(final byte transactionTrits[]) {

        try {

            final Transaction transaction = new Transaction(transactionTrits);
            tangle.put(transaction);
            node.pushToQueue(transaction.hash);

            return Converter.TRUE;

        } catch (final RuntimeException e) {

            return Converter.FALSE;
        }
    }
}
