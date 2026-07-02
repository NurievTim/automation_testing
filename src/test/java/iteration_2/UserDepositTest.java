package iteration_2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserDepositTest {
    final String AUTH = "Basic dGVzdFVzZXIxOnRlc3RVc2VyMSQ=";

    @BeforeAll
    public static void setupRestAssured(){
        RestAssured.filters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
    }

    @ParameterizedTest
    @ValueSource(doubles = {5000, 200, 0.01})
    public void userCanDepositToSelfAccount(double amount) {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject().put("id", 1).put("balance", amount);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", equalTo(requestBody.get("id")));
    }

    @ParameterizedTest
    @ValueSource(doubles = {5001, 0})
    public void userCannotDepositInadmissibleAmountToSelfAccount(double amount) {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject().put("id", 1).put("balance", amount);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void userCannotDepositToNonExistentAccount() {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject().put("id", 10).put("balance", 100);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body(equalTo("Unauthorized access to account"));

    }
}
