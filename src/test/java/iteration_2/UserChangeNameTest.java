package iteration_2;

import generators.RandomData;
import models.Customer;
import models.CustomerRequest;
import models.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequester;
import requests.skeleton.requests.ValidatedCrudRequest;
import specs.RequestSpecs;
import specs.ResponseSpec;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserChangeNameTest {

    @Test
    public void userCanChangeName() {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name(RandomData.getValidName())
                .build();

        CustomerResponse customerResponse = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.PROFILE,
                ResponseSpec.requestReturnsOK("message", ResponseSpec.PROFILE_UPDATED_SUCCESSFULLY))
                .put(customerRequest);

        assertEquals(customerRequest.getName(), customerResponse.getName());
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
