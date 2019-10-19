package cfb.ict.utilities;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Converter {

    public static final byte UNKNOWN = 0, TRUE = 1, FALSE = -1;

    private static final String TRYTES = "9ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final byte[][] TRYTES_TRITS = new byte[][] {

            {0, 0, 0},
            {1, 0, 0},
            {-1, 1, 0},
            {0, 1, 0},
            {1, 1, 0},
            {-1, -1, 1},
            {0, -1, 1},
            {1, -1, 1},
            {-1, 0, 1},
            {0, 0, 1},
            {1, 0, 1},
            {-1, 1, 1},
            {0, 1, 1},
            {1, 1, 1},
            {-1, -1, -1},
            {0, -1, -1},
            {1, -1, -1},
            {-1, 0, -1},
            {0, 0, -1},
            {1, 0, -1},
            {-1, 1, -1},
            {0, 1, -1},
            {1, 1, -1},
            {-1, -1, 0},
            {0, -1, 0},
            {1, -1, 0},
            {-1, 0, 0}
    };

    public static void copy(final long value, final byte[] trits, int offset, int length) {

        long absoluteValue = Math.abs(value);
        while (length-- > 0) {

            int remainder = (int) (absoluteValue % 3);
            absoluteValue /= 3;
            if (remainder > 1) {

                remainder = -1;
                absoluteValue++;

            }
            trits[offset++] = (byte) (value < 0 ? -remainder : remainder);
        }
    }

    public static void copy(final BigInteger value, final byte[] trits, int offset, int length) {

        BigInteger absoluteValue = value.abs();
        while (length-- > 0) {

            final BigInteger[] quotientAndRemainder = absoluteValue.divideAndRemainder(BigInteger.valueOf(3));
            if (quotientAndRemainder[1].compareTo(BigInteger.ONE) > 0) {

                trits[offset++] = (byte) (value.signum() < 0 ? 1 : -1);
                absoluteValue = quotientAndRemainder[0].add(BigInteger.ONE);

            } else {

                trits[offset++] = (byte) (value.signum() < 0 ? -quotientAndRemainder[1].byteValue() : quotientAndRemainder[1].byteValue());
                absoluteValue = quotientAndRemainder[0];
            }
        }
    }

    public static String trytes(final byte[] trits, final int offset, final int length) { // length must be a multiple of 3

        final StringBuilder trytes = new StringBuilder();

        for (int i = 0; i < length / 3; i++) {

            int j = trits[offset + i * 3] + trits[offset + i * 3 + 1] * 3 + trits[offset + i * 3 + 2] * 9;
            if (j < 0) {

                j += TRYTES.length();
            }
            trytes.append(TRYTES.charAt(j));
        }

        return trytes.toString();
    }

    public static byte[] trits(final String trytes) {

        final byte[] trits = new byte[trytes.length() * 3];

        for (int i = 0; i < trytes.length(); i++) {

            System.arraycopy(TRYTES_TRITS[TRYTES.indexOf(trytes.charAt(i))], 0, trits, i * 3, 3);
        }

        return trits;
    }

    public static long longValue(final byte[] trits, final int offset, final int length) {

        long value = 0;

        for (int i = length; i-- > 0; ) {

            value = value * 3 + trits[offset + i];
        }

        return value;
    }

    public static BigInteger bigIntegerValue(final byte[] trits, final int offset, final int length) {

        BigInteger value = BigInteger.ZERO;

        for (int i = length; i-- > 0; ) {

            value = value.multiply(BigInteger.valueOf(3)).add(BigInteger.valueOf(trits[offset + i]));
        }

        return value;
    }

    public static void convertTritsToBytes(final byte[] trits, int tritsOffset, int tritsLength, // tritsLength must be a multiple of 9
                                                 final byte[] bytes, final int bytesOffset) {

        final ByteBuffer bytesBuffer = (ByteBuffer) ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).position(bytesOffset);

        do {

            int value = 0;

            for (int i = 9; i-- > 0; ) {

                value = value * 3 + trits[tritsOffset + i];
            }
            tritsOffset += 9;

            bytesBuffer.putShort((short) value);

        } while ((tritsLength -= 9) > 0);
    }

    public static void convertBytesToTrits(final byte[] bytes, final int bytesOffset, int bytesLength, // bytesLength must be a multiple of 2
                                                 final byte[] trits, int tritsOffset) {

        final ByteBuffer bytesBuffer = (ByteBuffer) ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).position(bytesOffset);

        do {

            final int value = bytesBuffer.getShort();

            int absoluteValue = value < 0 ? -value : value;
            for (int i = 0; i < 9; i++) {

                int remainder = absoluteValue % 3;
                absoluteValue /= 3;
                if (remainder > 1) {

                    remainder = -1;
                    absoluteValue++;
                }
                trits[tritsOffset++] = (byte) (value < 0 ? -remainder : remainder);
            }

        } while ((bytesLength -= 2) > 0);
    }

    public static int sizeInBytes(final int lengthInTrits) { // lengthInTrits must be a multiple of 9

        return (lengthInTrits / 9) * 2;
    }

    public static int lengthInTrits(final int sizeInBytes) { // sizeInBytes must be a multiple of 2

        return (sizeInBytes / 2) * 9;
    }
}
