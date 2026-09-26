package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import api.requests.skeleton.requests.ValidatedCrudRequester;
import api.specs.RequestSpecs;
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
        return new ValidatedCrudRequester<CreateAccountResponse>(
                authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated()
        ).post(null);
    }

    public static DepositResponse makeDeposit(CreateUserRequest userRequest, long accountId, double amount) {
        DepositRequest depositRequest = DepositRequest.builder()
                .id((int) accountId)
                .balance(amount)
                .build();

        return new ValidatedCrudRequester<DepositResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        ).post(depositRequest);
    }
    public static DepositResponse makeDepositEnoughForTransfer(CreateUserRequest userRequest, long accountId) {
        DepositRequest depositRequest = DepositRequest.builder()
                .id((int) accountId)
                .balance(5000)
                .build();

        new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        ).post(depositRequest);

        return new ValidatedCrudRequester<DepositResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        ).post(depositRequest);
    }
}
