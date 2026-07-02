package iteration_2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.json.JSONObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.RestAssured.given;

public class UserTransferTest {
    final String AUTH = "Basic dGVzdFVzZXIxOnRlc3RVc2VyMSQ=";

    @BeforeAll
    public static void setupRestAssured(){
        RestAssured.filters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 10000, 500})
    public void userCanTransferBetweenTheirAccounts(double amount) {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject()
                .put("amount", amount)
                .put("receiverAccountId", 2)
                .put("senderAccountId", 1);

        JSONObject requestBodyRevert = new JSONObject()
                .put("amount", amount)
                .put("receiverAccountId", 1)
                .put("senderAccountId", 2);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBodyRevert.toString())
                .when()
                .post("/accounts/transfer");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 10001})
    public void userCannotTransferInadmissibleAmountBetweenTheirAccounts(double amount) {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject()
                .put("amount", amount)
                .put("receiverAccountId", 2)
                .put("senderAccountId", 1);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void userHasNotEnoughAmountToTransfer() {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject()
                .put("amount", 5000)
                .put("receiverAccountId", 1)
                .put("senderAccountId", 2);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void userCanTransferToAnotherUser() {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject()
                .put("amount", 100)
                .put("receiverAccountId", 3)
                .put("senderAccountId", 1);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }
}
