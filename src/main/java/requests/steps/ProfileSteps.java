package requests.steps;

import models.Accounts;
import models.CustomerResponse;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

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
