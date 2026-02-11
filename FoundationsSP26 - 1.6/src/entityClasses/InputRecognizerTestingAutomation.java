package entityClasses;

import java.util.ArrayList;
import java.util.List;

public class InputRecognizerTestingAutomation {

    private enum ValidatorType {
        USERNAME,
        PASSWORD,
        EMAIL,
        NAME
    }

    private static class TestCase {
        final String id;
        final ValidatorType validatorType;
        final String input;
        final boolean expectedValid;
        final String whatIsTested;
        final String howItIsTested;

        TestCase(String id, ValidatorType validatorType, String input, boolean expectedValid,
                 String whatIsTested, String howItIsTested) {
            this.id = id;
            this.validatorType = validatorType;
            this.input = input;
            this.expectedValid = expectedValid;
            this.whatIsTested = whatIsTested;
            this.howItIsTested = howItIsTested;
        }
    }

    private static String runValidator(ValidatorType type, String input) {
        switch (type) {
            case USERNAME:
                return InputRecognizer.checkUsername(input);
            case PASSWORD:
                return InputRecognizer.checkPassword(input);
            case EMAIL:
                return InputRecognizer.checkEmailAddress(input);
            case NAME:
                return InputRecognizer.checkName(input);
            default:
                return "Unsupported validator type";
        }
    }

    private static void addUsernameCases(List<TestCase> tests) {
        tests.add(new TestCase("U-OLD-01", ValidatorType.USERNAME, "Alice1", true,
                "Baseline valid username with first letter + alphanumeric characters.",
                "Run checkUsername and assert empty error string."));
        tests.add(new TestCase("U-OLD-02", ValidatorType.USERNAME, "ab", false,
                "Minimum length boundary (<4) should fail.",
                "Run checkUsername and assert non-empty error string."));
        tests.add(new TestCase("U-OLD-03", ValidatorType.USERNAME, "1Alice", false,
                "First character must be a letter.",
                "Run checkUsername and assert non-empty error string."));
        tests.add(new TestCase("U-NEW-01", ValidatorType.USERNAME, "UserName@", false,
                "Reject invalid special character '@' after a valid username prefix.",
                "Run checkUsername and assert non-empty error string."));
        tests.add(new TestCase("U-NEW-02", ValidatorType.USERNAME, "A-B_C.9", true,
                "Allow '-', '_', '.' only when between alphanumeric characters.",
                "Run checkUsername and assert empty error string."));
        tests.add(new TestCase("U-NEW-03", ValidatorType.USERNAME, "Abc-", false,
                "Reject trailing special character.",
                "Run checkUsername and assert non-empty error string."));
        tests.add(new TestCase("U-NEW-04", ValidatorType.USERNAME, "-Abc", false,
                "Reject leading special character.",
                "Run checkUsername and assert non-empty error string."));
        tests.add(new TestCase("U-NEW-05", ValidatorType.USERNAME, "Ab__cd", false,
                "Reject consecutive special characters.",
                "Run checkUsername and assert non-empty error string."));
        tests.add(new TestCase("U-NEW-06", ValidatorType.USERNAME, "Abcd1234Efgh5678", true,
                "Maximum length boundary (=16) should pass.",
                "Run checkUsername and assert empty error string."));
        tests.add(new TestCase("U-NEW-07", ValidatorType.USERNAME, "Abcd1234Efgh56789", false,
                "Maximum length boundary (>16) should fail.",
                "Run checkUsername and assert non-empty error string."));
    }

    private static void addPasswordCases(List<TestCase> tests) {
        tests.add(new TestCase("P-OLD-01", ValidatorType.PASSWORD, "Aa1!aaaa", true,
                "Baseline valid password with 8 chars and allowed special character.",
                "Run checkPassword and assert empty error string."));
        tests.add(new TestCase("P-OLD-02", ValidatorType.PASSWORD, "Aa1!aaa", false,
                "Minimum length boundary (<8) should fail.",
                "Run checkPassword and assert non-empty error string."));
        tests.add(new TestCase("P-NEW-01", ValidatorType.PASSWORD, "Aa1\\aaaa", false,
                "Reject character not in the allowed special-character set.",
                "Run checkPassword and assert non-empty error string."));
        tests.add(new TestCase("P-NEW-02", ValidatorType.PASSWORD,
                "Aa1!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", true,
                "Maximum length boundary (=64) should pass.",
                "Run checkPassword and assert empty error string."));
        tests.add(new TestCase("P-NEW-03", ValidatorType.PASSWORD,
                "Aa1!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", false,
                "Maximum length boundary (>64) should fail.",
                "Run checkPassword and assert non-empty error string."));
    }

    private static void addEmailCases(List<TestCase> tests) {
        tests.add(new TestCase("E-OLD-01", ValidatorType.EMAIL, "alice1@example.com", true,
                "Baseline valid email format.",
                "Run checkEmailAddress and assert empty error string."));
        tests.add(new TestCase("E-OLD-02", ValidatorType.EMAIL, "a@b", false,
                "Domain must satisfy minimum requirements.",
                "Run checkEmailAddress and assert non-empty error string."));
        tests.add(new TestCase("E-NEW-01", ValidatorType.EMAIL, "alice..smith@example.com", false,
                "Reject consecutive periods in local part.",
                "Run checkEmailAddress and assert non-empty error string."));
        tests.add(new TestCase("E-NEW-02", ValidatorType.EMAIL, "alice@-example.com", false,
                "Reject hyphen that is not between two alphanumeric characters in domain.",
                "Run checkEmailAddress and assert non-empty error string."));
        tests.add(new TestCase("E-NEW-03", ValidatorType.EMAIL, "alice@example-domain.com", true,
                "Allow domain hyphen only between alphanumeric characters.",
                "Run checkEmailAddress and assert empty error string."));
    }

    private static void addNameCases(List<TestCase> tests) {
        tests.add(new TestCase("N-OLD-01", ValidatorType.NAME, "Alice", true,
                "Baseline valid alphabetic name.",
                "Run checkName and assert empty error string."));
        tests.add(new TestCase("N-OLD-02", ValidatorType.NAME, "Al", false,
                "Minimum length boundary (<3) should fail.",
                "Run checkName and assert non-empty error string."));
        tests.add(new TestCase("N-NEW-01", ValidatorType.NAME, "Alice1", false,
                "Reject numeric characters in name.",
                "Run checkName and assert non-empty error string."));
        tests.add(new TestCase("N-NEW-02", ValidatorType.NAME, "AliceMaryJaneJohnsonSmithAlphaBeta", false,
                "Maximum length boundary (>32) should fail.",
                "Run checkName and assert non-empty error string."));
        tests.add(new TestCase("N-NEW-03", ValidatorType.NAME, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdef", true,
                "Maximum length boundary (=32) should pass.",
                "Run checkName and assert empty error string."));
    }

    public static void main(String[] args) {
        List<TestCase> tests = new ArrayList<>();
        addUsernameCases(tests);
        addPasswordCases(tests);
        addEmailCases(tests);
        addNameCases(tests);

        int passed = 0;

        for (TestCase test : tests) {
            String result = runValidator(test.validatorType, test.input);
            boolean actualValid = result.isEmpty();
            boolean ok = (actualValid == test.expectedValid);

            if (ok) {
                passed++;
                System.out.println("PASS " + test.id + " :: " + test.whatIsTested);
            } else {
                System.out.println("FAIL " + test.id + " :: " + test.whatIsTested);
                System.out.println("  How tested: " + test.howItIsTested);
                System.out.println("  Input: [" + test.input + "]");
                System.out.println("  Expected valid: " + test.expectedValid);
                System.out.println("  Actual valid: " + actualValid);
                System.out.println("  Validator message: " + result);
            }
        }

        System.out.println("\nResult: " + passed + "/" + tests.size() + " tests passed.");

        if (passed != tests.size()) {
            throw new RuntimeException("One or more input validation tests failed.");
        }
    }
}
