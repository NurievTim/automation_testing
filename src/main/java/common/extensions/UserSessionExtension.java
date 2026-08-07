package common.extensions;

import api.configs.SessionStorage;
import api.models.CreateUserRequest;
import api.requests.steps.AdminSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

public class UserSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        UserSession annotation = context.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {
            CreateUserRequest createUserRequest = AdminSteps.createUser();
            BasePage.authAsUser(createUserRequest);

            SessionStorage.clear();
            SessionStorage.addUser(createUserRequest);
        }
    }
}
