package iteration_2.api;

import api.generators.RandomData;
import api.models.Customer;
import api.models.CustomerRequest;
import api.models.CustomerResponse;
import api.models.comparison.ModelAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import api.requests.skeleton.requests.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class UserChangeNameTest {

    @Test
    public void userCanChangeName() {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name(RandomData.getValidName())
                .build();

        Customer customer = new ValidatedCrudRequester<Customer>(
                RequestSpecs.userSpec(),
                Endpoint.PUT_PROFILE,
                ResponseSpecs.requestReturnsOK(ResponseSpecs.PROFILE_UPDATED_SUCCESSFULLY))
                .put(customerRequest);

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
                Endpoint.PUT_PROFILE,
                ResponseSpecs.requestReturnsBadRequest(ResponseSpecs.NAME_VALIDATION_ERROR))
                .put(customerRequest);

        CustomerResponse response = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();
        
        assertNotEquals(response.getName(), invalidName);
    }
}
