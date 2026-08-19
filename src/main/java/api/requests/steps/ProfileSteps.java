package api.requests.steps;

import api.models.Accounts;
import api.models.CustomerResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

public class ProfileSteps {
    public static double userGetBalance(int accountId) {
        CustomerResponse customerResponse = new ValidatedCrudRequester<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpecs.requestReturnsOK())
                .get();

        return customerResponse.getAccounts().stream()
                .filter(accounts -> accounts.getId() == accountId)
                .findFirst()
                .map(Accounts::getBalance)
                .orElseThrow(() -> new AssertionError("Аккаунт с ID " + accountId + " не найден в профиле!"));
    }
}
