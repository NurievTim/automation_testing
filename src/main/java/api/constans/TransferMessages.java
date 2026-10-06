package api.constans;

public final class TransferMessages {
    private TransferMessages() {}

    public static final String APPROVED_MSG = "Transfer approved and processed immediately";
    public static final String MANUAL_REVIEW_MSG = "Transfer requires manual review";
    public static final String BLOCKED_TRANSFER = "Transfer blocked due to fraud detection";
    public static final String VERIFICATION_REQUIRED = "Additional verification required";
}
