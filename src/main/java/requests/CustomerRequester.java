package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.CustomerRequest;

import static io.restassured.RestAssured.given;

public class CustomerRequester extends PutRequest<CustomerRequest> {
    public CustomerRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse put(CustomerRequest model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .put("api/v1/customer/profile")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
