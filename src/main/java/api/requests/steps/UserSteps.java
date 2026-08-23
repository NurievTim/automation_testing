package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import api.requests.skeleton.requests.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

import static api.specs.RequestSpecs.authAsUser;

public class UserSteps {
    public static CustomerResponse getUserProfile(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<CustomerResponse>(
                authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();
    }

    public static List<Accounts> getUserAccounts(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<Accounts>(
                authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK())
                .getAll();
    }

    public static List<TransactionDto> getAccountTransactions(CreateUserRequest userRequest, long accountId) {
        return new ValidatedCrudRequester<TransactionDto>(
                authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNT_TRANSACTIONS,
                ResponseSpecs.requestReturnsOK())
                .getAll(accountId);
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
                .accountId(accountId)
                .amount(amount)
                .build();

        return new ValidatedCrudRequester<DepositResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOK()
        ).post(depositRequest);
    }

    public static DepositResponse makeDepositEnoughForTransfer(CreateUserRequest userRequest, long accountId) {
        DepositRequest depositRequest = DepositRequest.builder()
                .accountId(accountId)
                .amount(5000)
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

    public static Accounts userGetAccountById(int accountId, CreateUserRequest userRequest) {
        return UserSteps.getUserAccounts(userRequest).stream()
                .filter(accounts -> accounts.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Аккаунт с ID " + accountId + " не найден у пользователя!"));
    }
}
