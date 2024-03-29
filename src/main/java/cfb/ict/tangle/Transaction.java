package cfb.ict.tangle;

import cfb.ict.cryptography.Curl_729_27;
import cfb.ict.cryptography.Hash;
import cfb.ict.utilities.Converter;

import java.math.BigInteger;
import java.util.Arrays;

public class Transaction {

    static final int MESSAGE_SIGNATURE_OFFSET = 0, MESSAGE_SIGNATURE_LENGTH = 6561;
    static final int EXTRA_DATA_DIGEST_OFFSET = MESSAGE_SIGNATURE_OFFSET + MESSAGE_SIGNATURE_LENGTH, EXTRA_DATA_DIGEST_LENGTH = 243;
    static final int ADDRESS_OFFSET = EXTRA_DATA_DIGEST_OFFSET + EXTRA_DATA_DIGEST_LENGTH, ADDRESS_LENGTH = 243;
    static final int VALUE_OFFSET = ADDRESS_OFFSET + ADDRESS_LENGTH, VALUE_LENGTH = 81;
    static final int ISSUANCE_TIMESTAMP_OFFSET = VALUE_OFFSET + VALUE_LENGTH, ISSUANCE_TIMESTAMP_LENGTH = 27;
    static final int TIMELOCK_LOWER_BOUND_OFFSET = ISSUANCE_TIMESTAMP_OFFSET + ISSUANCE_TIMESTAMP_LENGTH, TIMELOCK_LOWER_BOUND_LENGTH = 27;
    static final int TIMELOCK_UPPER_BOUND_OFFSET = TIMELOCK_LOWER_BOUND_OFFSET + TIMELOCK_LOWER_BOUND_LENGTH, TIMELOCK_UPPER_BOUND_LENGTH = 27;
    static final int BUNDLE_NONCE_OFFSET = TIMELOCK_UPPER_BOUND_OFFSET + TIMELOCK_UPPER_BOUND_LENGTH, BUNDLE_NONCE_LENGTH = 81;
    static final int TRUNK_TRANSACTION_HASH_OFFSET = BUNDLE_NONCE_OFFSET + BUNDLE_NONCE_LENGTH, TRUNK_TRANSACTION_HASH_LENGTH = 243;
    static final int BRANCH_TRANSACTION_HASH_OFFSET = TRUNK_TRANSACTION_HASH_OFFSET + TRUNK_TRANSACTION_HASH_LENGTH, BRANCH_TRANSACTION_HASH_LENGTH = 243;
    static final int TAG_OFFSET = BRANCH_TRANSACTION_HASH_OFFSET + BRANCH_TRANSACTION_HASH_LENGTH, TAG_LENGTH = 81;
    static final int ATTACHMENT_TIMESTAMP_OFFSET = TAG_OFFSET + TAG_LENGTH, ATTACHMENT_TIMESTAMP_LENGTH = 27;
    static final int ATTACHMENT_TIMESTAMP_LOWER_BOUND_OFFSET = ATTACHMENT_TIMESTAMP_OFFSET + ATTACHMENT_TIMESTAMP_LENGTH, ATTACHMENT_TIMESTAMP_LOWER_BOUND_LENGTH = 27;
    static final int ATTACHMENT_TIMESTAMP_UPPER_BOUND_OFFSET = ATTACHMENT_TIMESTAMP_LOWER_BOUND_OFFSET + ATTACHMENT_TIMESTAMP_LOWER_BOUND_LENGTH, ATTACHMENT_TIMESTAMP_UPPER_BOUND_LENGTH = 27;
    static final int TRANSACTION_NONCE_OFFSET = ATTACHMENT_TIMESTAMP_UPPER_BOUND_OFFSET + ATTACHMENT_TIMESTAMP_UPPER_BOUND_LENGTH, TRANSACTION_NONCE_LENGTH = 81;

    public static final int LENGTH = TRANSACTION_NONCE_OFFSET + TRANSACTION_NONCE_LENGTH;
    static final int BUNDLE_ESSENCE_OFFSET = EXTRA_DATA_DIGEST_OFFSET, BUNDLE_ESSENCE_LENGTH = EXTRA_DATA_DIGEST_LENGTH + ADDRESS_LENGTH + VALUE_LENGTH + ISSUANCE_TIMESTAMP_LENGTH + TIMELOCK_LOWER_BOUND_LENGTH + TIMELOCK_UPPER_BOUND_LENGTH + BUNDLE_NONCE_LENGTH;

    static final int TYPE_OFFSET = 0, HEAD_FLAG_OFFSET = 1, TAIL_FLAG_OFFSET = 2;

    final byte[] messageSignature;
    final Hash extraDataDigest;
    final Hash address;
    final BigInteger value;
    final long issuanceTimestamp;
    final long timelockLowerBound, timelockUpperBound;
    final byte[] bundleNonce;
    final Hash trunkTransactionHash, branchTransactionHash;
    final Hash tag;
    final long attachmentTimestamp, attachmentTimestampLowerBound, attachmentTimestampUpperBound;
    final byte[] transactionNonce;

    public final Hash hash;

    public final byte type, headFlag, tailFlag;

    public Transaction(final byte[] trits) {

        messageSignature = Arrays.copyOfRange(trits, MESSAGE_SIGNATURE_OFFSET, MESSAGE_SIGNATURE_OFFSET + MESSAGE_SIGNATURE_LENGTH);
        extraDataDigest = new Hash(trits, EXTRA_DATA_DIGEST_OFFSET, EXTRA_DATA_DIGEST_LENGTH);
        address = new Hash(trits, ADDRESS_OFFSET, ADDRESS_LENGTH);
        value = Converter.bigIntegerValue(trits, VALUE_OFFSET, VALUE_LENGTH);
        issuanceTimestamp = Converter.longValue(trits, ISSUANCE_TIMESTAMP_OFFSET, ISSUANCE_TIMESTAMP_LENGTH);
        timelockLowerBound = Converter.longValue(trits, TIMELOCK_LOWER_BOUND_OFFSET, TIMELOCK_LOWER_BOUND_LENGTH);
        timelockUpperBound = Converter.longValue(trits, TIMELOCK_UPPER_BOUND_OFFSET, TIMELOCK_UPPER_BOUND_LENGTH);
        bundleNonce = Arrays.copyOfRange(trits, BUNDLE_NONCE_OFFSET, BUNDLE_NONCE_OFFSET + BUNDLE_NONCE_LENGTH);
        trunkTransactionHash = new Hash(trits, TRUNK_TRANSACTION_HASH_OFFSET, TRUNK_TRANSACTION_HASH_LENGTH);
        branchTransactionHash = new Hash(trits, BRANCH_TRANSACTION_HASH_OFFSET, BRANCH_TRANSACTION_HASH_LENGTH);
        tag = new Hash(trits, TAG_OFFSET, TAG_LENGTH);
        attachmentTimestamp = Converter.longValue(trits, ATTACHMENT_TIMESTAMP_OFFSET, ATTACHMENT_TIMESTAMP_LENGTH);
        attachmentTimestampLowerBound = Converter.longValue(trits, ATTACHMENT_TIMESTAMP_LOWER_BOUND_OFFSET, ATTACHMENT_TIMESTAMP_LOWER_BOUND_LENGTH);
        attachmentTimestampUpperBound = Converter.longValue(trits, ATTACHMENT_TIMESTAMP_UPPER_BOUND_OFFSET, ATTACHMENT_TIMESTAMP_UPPER_BOUND_LENGTH);
        transactionNonce = Arrays.copyOfRange(trits, TRANSACTION_NONCE_OFFSET, TRANSACTION_NONCE_OFFSET + TRANSACTION_NONCE_LENGTH);

        if (timelockLowerBound > timelockUpperBound
                || attachmentTimestamp < attachmentTimestampLowerBound || attachmentTimestamp > attachmentTimestampUpperBound) {

            throw new RuntimeException("Invalid transaction");
        }

        final byte[] hashTrits = new byte[Hash.LENGTH];
        final Curl_729_27 curl = new Curl_729_27();
        curl.absorb(trits, 0, LENGTH);
        curl.squeeze(hashTrits, 0, hashTrits.length);
        hash = new Hash(hashTrits, 0, hashTrits.length);

        type = hash.trits[TYPE_OFFSET];
        headFlag = hash.trits[HEAD_FLAG_OFFSET];
        tailFlag = hash.trits[TAIL_FLAG_OFFSET];
        if (headFlag == 0 || tailFlag == 0) {

            throw new RuntimeException("Invalid transaction");
        }
    }

    public void dump(final byte[] trits, final int offset) {

        System.arraycopy(messageSignature, 0, trits, offset + MESSAGE_SIGNATURE_OFFSET, MESSAGE_SIGNATURE_LENGTH);
        System.arraycopy(extraDataDigest.trits, 0, trits, offset + EXTRA_DATA_DIGEST_OFFSET, EXTRA_DATA_DIGEST_LENGTH);
        System.arraycopy(address.trits, 0, trits, offset + ADDRESS_OFFSET, ADDRESS_LENGTH);
        Converter.copy(value, trits, offset + VALUE_OFFSET, VALUE_LENGTH);
        Converter.copy(issuanceTimestamp, trits, offset + ISSUANCE_TIMESTAMP_OFFSET, ISSUANCE_TIMESTAMP_LENGTH);
        Converter.copy(timelockLowerBound, trits, offset + TIMELOCK_LOWER_BOUND_OFFSET, TIMELOCK_LOWER_BOUND_LENGTH);
        Converter.copy(timelockUpperBound, trits, offset + TIMELOCK_UPPER_BOUND_OFFSET, TIMELOCK_UPPER_BOUND_LENGTH);
        System.arraycopy(bundleNonce, 0, trits, offset + BUNDLE_NONCE_OFFSET, BUNDLE_NONCE_LENGTH);
        System.arraycopy(trunkTransactionHash.trits, 0, trits, offset + TRUNK_TRANSACTION_HASH_OFFSET, TRUNK_TRANSACTION_HASH_LENGTH);
        System.arraycopy(branchTransactionHash.trits, 0, trits, offset + BRANCH_TRANSACTION_HASH_OFFSET, BRANCH_TRANSACTION_HASH_LENGTH);
        System.arraycopy(tag.trits, 0, trits, offset + TAG_OFFSET, TAG_LENGTH);
        Converter.copy(attachmentTimestamp, trits, offset + ATTACHMENT_TIMESTAMP_OFFSET, ATTACHMENT_TIMESTAMP_LENGTH);
        Converter.copy(attachmentTimestampLowerBound, trits, offset + ATTACHMENT_TIMESTAMP_LOWER_BOUND_OFFSET, ATTACHMENT_TIMESTAMP_LOWER_BOUND_LENGTH);
        Converter.copy(attachmentTimestampUpperBound, trits, offset + ATTACHMENT_TIMESTAMP_UPPER_BOUND_OFFSET, ATTACHMENT_TIMESTAMP_UPPER_BOUND_LENGTH);
        System.arraycopy(transactionNonce, 0, trits, offset + TRANSACTION_NONCE_OFFSET, TRANSACTION_NONCE_LENGTH);
    }
}
