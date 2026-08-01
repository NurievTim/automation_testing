package iteration_2.ui;

import api.requests.steps.UserSteps;
import api.generators.RandomData;
import api.models.CreateUserRequest;
import api.models.CustomerResponse;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import ui.pages.Alerts;
import ui.pages.UserDashboardPage;

import static org.assertj.core.api.Assertions.assertThat;

public class UserChangeNameTest extends BaseUiTest {

    @Test
    public void userCanChangeName() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        authAsUser(userRequest);
        String newName = RandomData.getValidName();
        new UserDashboardPage()
                .open()
                .goToProfilePage()
                .changeName(newName)
                .checkAlertMessageAndAccept(
                        Alerts.NAME_UPDATED_SUCCESSFULLY.getMessage()
                );
        CustomerResponse userProfile = UserSteps.getUserProfile(userRequest);

        assertThat(userProfile.getName()).isEqualTo(newName);
    }

    @Test
    public void userCannotChangeName() {
        CreateUserRequest userRequest = AdminSteps.createUser();
        authAsUser(userRequest);
        String newName = RandomData.generateNameWithoutSpace();
        new UserDashboardPage()
                .open()
                .goToProfilePage()
                .changeName(newName)
                .checkAlertMessageAndAccept(
                        Alerts.NAME_MUST_CONTAINS.getMessage()
                );
        CustomerResponse userProfile = UserSteps.getUserProfile(userRequest);

        assertThat(userProfile.getName()).isNotEqualTo(newName);
    }
}
