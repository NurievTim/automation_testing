package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class ProfilePage extends BasePage<ProfilePage> {
    private final SelenideElement namePlaceholder = $(Selectors.byPlaceholder("Enter new name"));
    private final SelenideElement saveChangesButton = $(Selectors.byTagAndText("button", "\uD83D\uDCBE Save Changes"));

    @Override
    public String url() {
        return "/edit-profile";
    }

    public ProfilePage changeName(String newName) {
        namePlaceholder.sendKeys(newName);
        saveChangesButton.click();
        return this;
    }



}
