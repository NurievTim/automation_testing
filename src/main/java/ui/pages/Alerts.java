package ui.pages;

import lombok.Getter;

@Getter
public enum Alerts {
    ENTER_VALID_NAME("❌ Please enter a valid name."),
    NAME_MUST_CONTAINS("Name must contain two words with letters only"),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    DEPOSIT_LESS_OR_EQUAL_5000("❌ Please deposit less or equal to 5000$."),
    SUCCESS_TRANSFER("✅ Successfully transferred $%s to account %s!"),
    TRANSFER_AMOUNT_CANNOT_EXCEED_10000("❌ Error: Transfer amount cannot exceed 10000"),
    FILL_ALL_FIELDS_AND_CONFIRM("❌ Please fill all fields and confirm."),
    SUCCESS_DEPOSIT("✅ Successfully deposited $%s to account %s!");

    private final String message;

    Alerts(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
