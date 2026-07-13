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
import static org.hamcrest.Matchers.not;

public class UserChangeNameTest {
    final String AUTH = "Basic dGVzdFVzZXIxOnRlc3RVc2VyMSQ=";

    @BeforeAll
    public static void setupRestAssured(){
        RestAssured.filters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
    }

    @Test
    public void userCanChangeName() {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject().put("name", "Paul Newman");

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .put("/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("customer.name", equalTo("Paul Newman"))
                .body("message", equalTo("Profile updated successfully"));

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .when()
                .get("/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo("Paul Newman"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ne1w 3Name", "NewName", "Name"})
    public void userCannotPutInvalidName(String name) {
        RestAssured.baseURI = "http://localhost:4111/api/v1";
        JSONObject requestBody = new JSONObject().put("name", name);

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .body(requestBody.toString())
                .when()
                .put("/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(equalTo("Name must contain two words with letters only"));

        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH)
                .when()
                .get("/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("name", not(equalTo(name)));
    }
}
