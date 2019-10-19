package cfb.ict.cryptography;

import cfb.ict.utilities.Converter;

import java.util.Arrays;

public class Hash {

    public static final int LENGTH = Curl_729_27.HASH_LENGTH;
    public static final int SIZE_IN_BYTES = Converter.sizeInBytes(LENGTH);
    public static final Hash NULL = new Hash(new byte[LENGTH], 0, LENGTH);

    public final byte[] trits;

    private final int hashCode;

    public Hash(final byte[] trits, final int offset, final int length) {

        this.trits = new byte[LENGTH];
        System.arraycopy(trits, offset, this.trits, 0, Math.min(LENGTH, length));

        hashCode = Arrays.hashCode(this.trits);
    }

    @Override
    public boolean equals(final Object obj) {

        return Arrays.equals(trits, ((Hash) obj).trits);
    }

    @Override
    public int hashCode() {

        return hashCode;
    }

    @Override
    public String toString() {

        return Converter.trytes(trits, 0, trits.length);
    }
}
