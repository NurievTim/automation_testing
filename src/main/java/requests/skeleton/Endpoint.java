package requests.skeleton;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.*;

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
            Customer.class
    );
    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
