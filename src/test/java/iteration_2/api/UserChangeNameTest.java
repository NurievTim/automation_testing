package iteration_2.api;

import api.generators.RandomData;
import api.models.CreateUserRequest;
import api.models.Customer;
import api.models.CustomerRequest;
import api.models.CustomerResponse;
import api.models.comparison.ModelAssertions;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import api.requests.skeleton.requests.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserChangeNameTest extends BaseTest {

    @Test
    public void userCanChangeName() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name(RandomData.getValidName())
                .build();

        Customer customer = new ValidatedCrudRequester<Customer>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
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
        CreateUserRequest userRequest = AdminSteps.createUser();
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name(invalidName)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.PUT_PROFILE,
                ResponseSpecs.requestReturnsBadRequest(ResponseSpecs.NAME_VALIDATION_ERROR))
                .put(customerRequest);

        CustomerResponse response = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();
        
        assertNotEquals(response.getName(), invalidName);
    }
}
