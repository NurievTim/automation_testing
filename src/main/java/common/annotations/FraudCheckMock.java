package common.annotations;

import api.constans.FraudReasons;
import org.apache.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FraudCheckMock {
    enum Decision { APPROVED, BLOCKED, REVIEW_REQUIRED, VERIFICATION_REQUIRED}
    enum Status { SUCCESS, SERVICE_ERROR }

    String DEFAULT_ENDPOINT = "/fraud-check";
    int DEFAULT_PORT = 8080;

    int httpStatus() default HttpStatus.SC_OK;

    int fixedDelayMs() default 0;

    Status status() default Status.SUCCESS;

    Decision decision() default Decision.APPROVED;

    double riskScore() default FraudReasons.LOW_RISK_SCORE;

    String reason() default FraudReasons.LOW_RISK_REASON;

    boolean requiresManualReview() default false;

    boolean additionalVerificationRequired() default false;

    int port() default DEFAULT_PORT;

    String endpoint() default DEFAULT_ENDPOINT;
}
