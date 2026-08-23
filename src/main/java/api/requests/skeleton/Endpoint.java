package api.requests.skeleton;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    DEPOSIT(
            "/accounts/deposit",
            DepositRequest.class,
            DepositResponse.class
    ),
    TRANSFER(
            "accounts/transfer",
            TransferRequest.class,
            TransferResponse.class
    ),
    GET_PROFILE(
            "/customer/profile",
            CustomerRequest.class,
            CustomerResponse.class
    ),
    PUT_PROFILE(
            "/customer/profile",
            CustomerRequest.class,
            CustomerResponse.class
    ),
    ADMIN_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),
    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),
    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            CreateAccountResponse.class
    ),
    CUSTOMER_ACCOUNTS(
            "/customer/accounts",
            BaseModel.class,
            Accounts.class
    ),
    ACCOUNT_TRANSACTIONS(
            "/accounts/%s/transactions",
            BaseModel.class,
            TransactionDto.class
    ),
    ADMIN_USER_BY_ID(
            "/admin/users/%s",
            BaseModel.class,
            BaseModel.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

    /**
     * Подставляет path-параметры в url эндпоинта: "/accounts/%s/transactions" + 5 -> "/accounts/5/transactions".
     */
    public String getUrl(Object... pathParams) {
        return pathParams.length == 0 ? url : String.format(url, pathParams);
    }
}
