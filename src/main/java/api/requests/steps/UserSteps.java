package api.requests.steps;

import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.CustomerResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.ValidatedCrudRequester;
import api.specs.ResponseSpecs;

import static api.specs.RequestSpecs.authAsUser;

public class UserSteps {
    public static CustomerResponse getUserProfile(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<CustomerResponse>(
                authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();
    }

    public static CreateAccountResponse createUserAccount(CreateUserRequest userRequest) {
        return  new ValidatedCrudRequester<CreateAccountResponse>(
                authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);
    }
}
