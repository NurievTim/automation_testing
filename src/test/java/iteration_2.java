import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.json.JSONObject;

import java.util.List;

import static io.restassured.RestAssured.given;

public class iteration_2 {
    final String AUTH = "Basic dGVzdFVzZXIxOnRlc3RVc2VyMSQ=";

    @BeforeAll
    public static void setupRestAssured(){
        RestAssured.filters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
    }

    @Test
    public void userCanDepositToSelfAccount() {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject().put("id", 1).put("balance", 5000);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

    }

    @Test
    public void userCannotDepositMoreThan5000ToSelfAccount() {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject().put("id", 1).put("balance", 5001);

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
    public void userCannotDeposit0ToSelfAccount() {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject().put("id", 1).put("balance", 0);

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
    public void userCanDepositSmallAmountToSelfAccount() {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject().put("id", 1).put("balance", 0.01);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .post("/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

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
                .statusCode(HttpStatus.SC_FORBIDDEN);

    }
}
