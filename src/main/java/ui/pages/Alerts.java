package ui.pages;

import java.util.List;

public enum Alerts {
    ENTER_VALID_NAME("❌ Please enter a valid name."),
    INVALID_NAME(
            "Name must contain two words with letters only",
            "❌ Please enter a valid name."
    ),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    DEPOSIT_LESS_OR_EQUAL_5000("❌ Please deposit less or equal to 5000$."),
    SUCCESS_TRANSFER("✅ Successfully transferred $%s to account %s!"),
    TRANSFER_AMOUNT_CANNOT_EXCEED_10000("❌ Error: Transfer amount cannot exceed 10000"),
    FILL_ALL_FIELDS_AND_CONFIRM("❌ Please fill all fields and confirm."),
    SUCCESS_DEPOSIT("✅ Successfully deposited $%s to account %s!");

    private final List<String> messages;

    Alerts(String... messages) {
        this.messages = List.of(messages);
    }

    public String getMessage(Object... args) {
        return String.format(messages.getFirst(), args);
    }

    public List<String> getMessages(Object... args) {
        return messages.stream()
                .map(message -> String.format(message, args))
                .toList();
    }
}
