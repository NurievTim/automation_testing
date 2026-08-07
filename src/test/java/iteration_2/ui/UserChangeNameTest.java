package iteration_2.ui;

import api.configs.SessionStorage;
import api.requests.steps.UserSteps;
import api.generators.RandomData;
import api.models.CustomerResponse;
import common.annotations.UserSession;
import org.junit.jupiter.api.Test;
import ui.pages.Alerts;
import ui.pages.UserDashboardPage;

import static org.assertj.core.api.Assertions.assertThat;

public class UserChangeNameTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanChangeName() {
        String newName = RandomData.getValidName();

        new UserDashboardPage()
                .open()
                .goToProfilePage()
                .changeName(newName)
                .checkAlertMessageAndAccept(
                        Alerts.NAME_UPDATED_SUCCESSFULLY.getMessage()
                );
        CustomerResponse userProfile = UserSteps.getUserProfile(SessionStorage.getUser());

        assertThat(userProfile.getName()).isEqualTo(newName);
    }

    @Test
    @UserSession
    public void userCannotChangeName() {
        String newName = RandomData.generateNameWithoutSpace();

        new UserDashboardPage()
                .open()
                .goToProfilePage()
                .changeName(newName)
                .checkAlertMessageAndAccept(
                        Alerts.NAME_MUST_CONTAINS.getMessage()
                );
        CustomerResponse userProfile = UserSteps.getUserProfile(SessionStorage.getUser());

        assertThat(userProfile.getName()).isNotEqualTo(newName);
    }
}
