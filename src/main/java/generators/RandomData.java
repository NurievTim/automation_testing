package generators;

import net.datafaker.Faker;

import java.util.Locale;

public class RandomData {
    private RandomData() {
    }

    private static final Faker faker = new Faker(Locale.ENGLISH);

    public static String getValidName() {
        String firstName = faker.name().firstName().replaceAll("[^a-zA-Z]", "");
        String lastName = faker.name().lastName().replaceAll("[^a-zA-Z]", "");
        return firstName + " " + lastName;
    }

    public static String generateNameWithNumbers() {
        return faker.name().firstName().replace('e', '1') + " " +
                faker.regexify("[A-Z][a-z]{5}[0-9]{4}");
    }

    public static String generateNameWithoutSpace() {
        return faker.name().firstName() + faker.name().lastName();
    }

    public static String generateSingleWord() {
        return faker.name().firstName();
    }

    public static String generateOnlySpaces() {
        return " ".repeat(faker.number().numberBetween(1, 10));
    }

    public static int generateDepositAmount() {
        return faker.number().numberBetween(1, 5000);
    }

    public static int generateTransferAmount() {
        return faker.number().numberBetween(1, 10000);
    }

    public static int generateNonExistId() {
        return faker.number().numberBetween(50, 100);
    }
}
