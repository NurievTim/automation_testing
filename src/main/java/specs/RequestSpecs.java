package specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class RequestSpecs {
    private RequestSpecs() {}

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()))
                .setBaseUri("http://localhost:4111/api/v1/");
    }

    public static RequestSpecification userSpec() {
        return defaultRequestBuilder()
                .addHeader("Authorization", "Basic dGVzdFVzZXIxOnRlc3RVc2VyMSQ=")
                .build();
    }

    public static RequestSpecification userEmptyBalanceSpec() {
        return defaultRequestBuilder()
                .addHeader("Authorization", "Basic dGVzdFVzZXIzOnRlc3RVc2VyMyQ=")
                .build();
    }

    public static RequestSpecification secondUserSpec() {
        return defaultRequestBuilder()
                .addHeader("Authorization", "Basic dGVzdFVzZXIyOnRlc3RVc2VyMiQ=")
                .build();
    }
}
