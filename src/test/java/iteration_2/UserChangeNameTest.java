package iteration_2;

import generators.RandomData;
import io.restassured.response.ValidatableResponse;
import models.Customer;
import models.CustomerRequest;
import models.CustomerResponse;
import models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpec;

import java.util.stream.Stream;

public class UserChangeNameTest {

    @Test
    public void userCanChangeName() {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name(RandomData.getValidName())
                .build();

        ValidatableResponse response = new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.PROFILE,
                ResponseSpec.requestReturnsOK("message", ResponseSpec.PROFILE_UPDATED_SUCCESSFULLY))
                .put(customerRequest);

        Customer customer = response.extract().as(Customer.class);
        CustomerResponse customerResponse = customer.getCustomer();

        ModelAssertions.assertThatModels(customerRequest, customerResponse).match();
    }

    static Stream<String> invalidNameProvider() {
        return Stream.of(
                RandomData.generateNameWithNumbers(),
                RandomData.generateNameWithoutSpace(),
                RandomData.generateSingleWord(),
                RandomData.generateOnlySpaces()
        );
    }

    @ParameterizedTest
    @MethodSource("invalidNameProvider")
    public void userCannotPutInvalidName(String invalidName) {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name(invalidName)
                .build();

        new CrudRequester(
                RequestSpecs.userSpec(),
                Endpoint.PROFILE,
                ResponseSpec.requestReturnsBadRequest(ResponseSpec.NAME_VALIDATION_ERROR))
                .put(customerRequest);
    }
}
