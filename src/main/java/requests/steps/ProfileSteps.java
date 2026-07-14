package requests.steps;

import models.Accounts;
import models.CustomerResponse;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.ValidatedCrudRequest;
import specs.RequestSpecs;
import specs.ResponseSpec;

public class ProfileSteps {
    public static double userGetBalance(int accountId) {
        CustomerResponse customerResponse = new ValidatedCrudRequest<CustomerResponse>(
                RequestSpecs.userSpec(),
                Endpoint.GET_PROFILE,
                ResponseSpec.requestReturnsOK())
                .get();

        return customerResponse.getAccounts().stream()
                .filter(accounts -> accounts.getId() == accountId)
                .findFirst()
                .map(Accounts::getBalance)
                .orElseThrow(() -> new AssertionError("Аккаунт с ID " + accountId + " не найден в профиле!"));
    }
}
