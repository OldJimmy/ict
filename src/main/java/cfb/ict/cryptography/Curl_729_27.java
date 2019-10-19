package cfb.ict.cryptography;

public class Curl_729_27 {

    static final int HASH_LENGTH = 243;
    private static final int STATE_LENGTH = 3 * HASH_LENGTH;
    private static final byte[] LUT_0 = {1, 0, 0, 0, 1, 2, 2, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 1, 2, 0, 1, 2, 1, 0, 0, 1, 2, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 1, 0, 0, 2, 0};
    private static final byte[] LUT_1 = {1, 0, 2, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 0, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 2, 1, 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 2, 0, 1, 2, 0};
    private static final byte[] LUT_2 = {1, 1, 2, 0, 0, 1, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 1, 2, 1, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 2, 1, 0, 2, 1, 2, 0, 2, 1, 0};
    private static final int NUMBER_OF_ROUNDS = 27;
    private static final byte[] INITIAL_STATE = new byte[STATE_LENGTH];

    private final byte[] state = new byte[STATE_LENGTH];
    private final byte[] scratchpad = new byte[STATE_LENGTH];

    static {

        for (int i = 0; i < STATE_LENGTH; i++) {

            INITIAL_STATE[i] = (byte) ((i + 1) % 3);
        }
    }

    public Curl_729_27() {

        reset();
    }

    public void reset() {

        System.arraycopy(INITIAL_STATE, 0, state, 0, STATE_LENGTH);
    }

    public void absorb(final byte[] trits, int offset, int length) {

        do {

            for (int i = 0; i < (length < HASH_LENGTH ? length : HASH_LENGTH); i++) {

                state[i] = (byte) (trits[offset++] + 1);
            }
            transform();

        } while ((length -= HASH_LENGTH) > 0);
    }

    public void squeeze(final byte[] trits, int offset, int length) {

        do {

            for (int i = 0; i < (length < HASH_LENGTH ? length : HASH_LENGTH); i++) {

                trits[offset++] = (byte) (state[i] - 1);
            }
            transform();

        } while ((length -= HASH_LENGTH) > 0);
    }

    private void transform() {

        for (int i = NUMBER_OF_ROUNDS; i-- > 0; ) {

            for (int a = 0; a < HASH_LENGTH; a++) {

                final int index, b, c;
                scratchpad[a] = LUT_0[index = state[a] | (state[b = a + 243] << 2) | (state[c = a + 486] << 4)];
                scratchpad[b] = LUT_1[index];
                scratchpad[c] = LUT_2[index];
            }
            for (int a = 0, j = 81; a < STATE_LENGTH; a++) {

                final int index, b, c;
                state[a] = LUT_0[index = scratchpad[a] | (scratchpad[b = a + 81] << 2) | (scratchpad[c = a + 162] << 4)];
                state[b] = LUT_1[index];
                state[c] = LUT_2[index];

                if (--j == 0) {

                    j = 81;
                    a = c;
                }
            }
            for (int a = 0, j = 27; a < STATE_LENGTH; a++) {

                final int index, b, c;
                scratchpad[a] = LUT_0[index = state[a] | (state[b = a + 27] << 2) | (state[c = a + 54] << 4)];
                scratchpad[b] = LUT_1[index];
                scratchpad[c] = LUT_2[index];

                if (--j == 0) {

                    j = 27;
                    a = c;
                }
            }
            for (int a = 0, j = 9; a < STATE_LENGTH; a++) {

                final int index, b, c;
                state[a] = LUT_0[index = scratchpad[a] | (scratchpad[b = a + 9] << 2) | (scratchpad[c = a + 18] << 4)];
                state[b] = LUT_1[index];
                state[c] = LUT_2[index];

                if (--j == 0) {

                    j = 9;
                    a = c;
                }
            }
            for (int a = 0, j = 3; a < STATE_LENGTH; a++) {

                final int index, b, c;
                scratchpad[a] = LUT_0[index = state[a] | (state[b = a + 3] << 2) | (state[c = a + 6] << 4)];
                scratchpad[b] = LUT_1[index];
                scratchpad[c] = LUT_2[index];

                if (--j == 0) {

                    j = 3;
                    a = c;
                }
            }
            for (int a = 0; a < STATE_LENGTH; a += 3) {

                final int index;
                state[a] = LUT_0[index = scratchpad[a] | (scratchpad[a + 1] << 2) | (scratchpad[a + 2] << 4)];
                state[a + 1] = LUT_1[index];
                state[a + 2] = LUT_2[index];
            }
        }
    }
}
