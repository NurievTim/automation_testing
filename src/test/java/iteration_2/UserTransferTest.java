package iteration_2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.json.JSONObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserTransferTest {
    final String AUTH = "Basic dGVzdFVzZXIxOnRlc3RVc2VyMSQ=";
    final String AUTH2 = "Basic dGVzdFVzZXIyOnRlc3RVc2VyMiQ=";
    final String AUTH3 = "Basic YWRtaW46YWRtaW4=";

    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01, 9999.99, 10000})
    public void userCanTransferBetweenTheirAccounts(double amount) {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject()
                .put("amount", amount)
                .put("receiverAccountId", 2)
                .put("senderAccountId", 1);

        Response beforeTransfer = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .when()
                .get("/customer/profile");

        float senderBalance = beforeTransfer.path("accounts[0].balance");
        float receiverBalance = beforeTransfer.path("accounts[1].balance");

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("message", equalTo("Transfer successful"))
                .body("amount", equalTo((float) amount));

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .when()
                .get("/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("accounts[0].balance", equalTo((float) (senderBalance - amount)))
                .body("accounts[1].balance", equalTo((float) (receiverBalance + amount)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 10000.01})
    public void userCannotTransferInadmissibleAmountBetweenTheirAccounts(double amount) {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject()
                .put("amount", amount)
                .put("receiverAccountId", 2)
                .put("senderAccountId", 1);

        Response beforeTransfer = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .when()
                .get("/customer/profile");

        float senderBalance = beforeTransfer.path("accounts[0].balance");
        float receiverBalance = beforeTransfer.path("accounts[1].balance");

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

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .when()
                .get("/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("accounts[0].balance", equalTo(senderBalance))
                .body("accounts[1].balance", equalTo(receiverBalance));
    }

    @Test
    public void userHasNotEnoughAmountToTransfer() {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject()
                .put("amount", 5000)
                .put("receiverAccountId", 1)
                .put("senderAccountId", 4);

        float senderBalance = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH3)
                .when()
                .get("/customer/profile")
                .then()
                .extract()
                .path("accounts[0].balance");

        float receiverBalance = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .when()
                .get("/customer/profile")
                .then()
                .extract()
                .path("accounts[1].balance");

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(equalTo("Invalid transfer: insufficient funds or invalid accounts"));

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH3)
                .when()
                .get("/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("accounts[0].balance", equalTo(senderBalance));

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .when()
                .get("/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("accounts[0].balance", equalTo(receiverBalance));
    }

    @Test
    public void userCanTransferToAnotherUser() {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject()
                .put("amount", 100)
                .put("receiverAccountId", 3)
                .put("senderAccountId", 1);

        float senderBalance = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .when()
                .get("/customer/profile")
                .then()
                .extract()
                .path("accounts[0].balance");

        float receiverBalance = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH2)
                .when()
                .get("/customer/profile")
                .then()
                .extract()
                .path("accounts[0].balance");

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("message", equalTo("Transfer successful"));

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .when()
                .get("/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("accounts[0].balance", equalTo(senderBalance - 100F));

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH2)
                .when()
                .get("/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("accounts[0].balance", equalTo(receiverBalance + 100F));
    }
}
