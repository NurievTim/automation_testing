package iteration_2;

import models.CustomerRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.CustomerRequester;
import specs.RequestSpecs;
import specs.ResponseSpec;

import static org.hamcrest.Matchers.equalTo;

public class UserChangeNameTest {

    @Test
    public void userCanChangeName() {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name("New Name")
                .build();

        new CustomerRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsOK("message", "Profile updated successfully"))
                .put(customerRequest)
                .assertThat()
                .body("customer.name", equalTo("New Name"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ne1w 3Name", "NewName", "Name", " "})
    public void userCannotPutInvalidName(String newName) {
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name(newName)
                .build();

        new CustomerRequester(
                RequestSpecs.userSpec(),
                ResponseSpec.requestReturnsBadRequest("Name must contain two words with letters only"))
                .put(customerRequest);
    }
}
