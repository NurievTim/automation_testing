package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import static org.hamcrest.Matchers.equalTo;

public class ResponseSpec {
    private ResponseSpec() {}

    public static final String PROFILE_UPDATED_SUCCESSFULLY = "Profile updated successfully";
    public static final String NAME_VALIDATION_ERROR = "Name must contain two words with letters only";

    private static ResponseSpecBuilder defaultResponseBuilder() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification requestReturnsOK() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK).
                build();
    }

    public static ResponseSpecification requestReturnsOK(String successKey, String successMessage) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectBody(successKey, equalTo(successMessage)).
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

    public static ResponseSpecification requestReturnsForbidden(String expectedMessage) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(equalTo(expectedMessage))
                .build();
    }
}
