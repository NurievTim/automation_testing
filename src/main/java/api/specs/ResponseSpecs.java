package api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;

public class ResponseSpecs {
    private ResponseSpecs() {}

    private static final String MESSAGE_JSON_PATH = "message";

    public static final String NAME_VALIDATION_ERROR = "Name must contain two words with letters only";
    public static final String SUCCESS_TRANSFER = "Transfer successful";
    public static final String ERROR_TRANSFER = "Invalid transfer: insufficient funds or invalid accounts";
    public static final String UNAUTH_MESSAGE = "Unauthorized access to account";
    public static final String DEPOSIT_MAX_LIMIT = "Deposit amount exceeds the 5000 limit";
    public static final String TRANSFER_MAX_LIMIT = "Transfer amount cannot exceed 10000";
    public static final String AMOUNT_MUST_BE_POSITIVE = "must be greater than 0";

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

    public static ResponseSpecification requestReturnsBadRequest(String expectedMessage) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(MESSAGE_JSON_PATH, equalTo(expectedMessage))
                .build();
    }

    public static ResponseSpecification requestReturnsFieldValidationError(String expectedMessage) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody("amount" + "[0]", equalTo(expectedMessage))
                .build();
    }

    public static ResponseSpecification requestReturnsForbidden() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(MESSAGE_JSON_PATH, equalTo(UNAUTH_MESSAGE))
                .build();
    }

    public static  ResponseSpecification entityWasCreated() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }
}
