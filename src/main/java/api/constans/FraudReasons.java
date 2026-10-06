package api.constans;

public final class FraudReasons {
    private FraudReasons() {}

    public static final String LOW_RISK_REASON = "Low risk transaction";
    public static final double LOW_RISK_SCORE = 0.2;

    public static final double FALLBACK_RISK_SCORE = 0.5;
    public static final String FRAUD_CHECK_ERROR_REASON_PREFIX = "Unexpected error during fraud check: ";
}
