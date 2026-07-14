package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;

public class ResponseSpec {
    private ResponseSpec() {}

    private static final String MESSAGE_JSON_PATH = "message";

    public static final String PROFILE_UPDATED_SUCCESSFULLY = "Profile updated successfully";
    public static final String NAME_VALIDATION_ERROR = "Name must contain two words with letters only";
    public static final String SUCCESS_TRANSFER = "Transfer successful";
    public static final String ERROR_TRANSFER = "Invalid transfer: insufficient funds or invalid accounts";
    public static final String UNAUTH_MESSAGE = "Unauthorized access to account";
    public static final String DEPOSIT_MAX_LIMIT = "Deposit amount cannot exceed 5000";
    public static final String DEPOSIT_MIN_LIMIT = "Deposit amount must be at least 0.01";
    public static final String TRANSFER_MIN_LIMIT = "Transfer amount must be at least 0.01";
    public static final String TRANSFER_MAX_LIMIT = "Transfer amount cannot exceed 10000";

    private static ResponseSpecBuilder defaultResponseBuilder() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification requestReturnsOK() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK).
                build();
    }

    public static ResponseSpecification requestReturnsOK(String successMessage) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectBody(MESSAGE_JSON_PATH, equalTo(successMessage)).
                build();
    }

    public static ResponseSpecification requestReturnsBadRequest(String errorKey, String errorValue) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(errorKey, equalTo(errorValue))
                .build();
    }

    public static ResponseSpecification requestReturnsBadRequest(String expectedMessage) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(equalTo(expectedMessage))
                .build();
    }

    public static ResponseSpecification requestReturnsForbidden() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(equalTo(UNAUTH_MESSAGE))
                .build();
    }
}
