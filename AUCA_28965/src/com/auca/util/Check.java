package com.auca.util;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Small reusable validation methods. Each one returns the clean value or throws ValidationException. */
public class Check {
    public static final String[] ACADEMIC_STATUSES = {"ACTIVE", "INACTIVE"};
    public static final String[] YES_NO = {"YES", "NO"};
    public static final String[] SEMESTERS = {"FALL", "SPRING", "SUMMER"};
    public static final String[] ENROLLMENT_STATUSES = {"ACTIVE", "COMPLETED", "DROPPED"};
    public static final String[] ASSESSMENT_TYPES = {"QUIZ", "ASSIGNMENT", "MIDTERM", "EXAM"};
    public static final String[] PAYMENT_METHODS = {"CASH", "MOBILE_MONEY", "CARD", "BANK_TRANSFER"};
    public static final String[] EMPLOYMENT_TYPES = {"FULL_TIME", "PART_TIME", "CONTRACT"};

    private Check() {
    }

    public static String text(String label, String value, int min, int max) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(label + " cannot be blank.");
        }
        String t = value.trim();
        if (t.length() < min || t.length() > max) {
            throw new ValidationException(label + " must be " + min + "-" + max + " characters.");
        }
        return t;
    }

    public static String id(String label, String value) {
        return text(label, value, Identifiable.MIN_ID_LENGTH, Identifiable.MAX_ID_LENGTH);
    }

    public static String name(String label, String value) {
        return text(label, value, Registrable.MIN_NAME_LENGTH, Registrable.MAX_NAME_LENGTH);
    }

    public static String email(String label, String value) {
        String t = text(label, value, 5, Registrable.MAX_EMAIL_LENGTH);
        if (!t.matches("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+")) {
            throw new ValidationException(label + " is not a valid email (example: name@example.com).");
        }
        return t;
    }

    public static String phone(String label, String value) {
        String t = text(label, value, 1, 30);
        if (!t.matches("\\+?\\d{" + Registrable.MIN_PHONE_LENGTH + "," + Registrable.MAX_PHONE_LENGTH + "}")) {
            throw new ValidationException(label + " must have 10-15 digits with an optional leading +.");
        }
        return t;
    }

    public static LocalDate date(String label, String value) {
        try {
            return LocalDate.parse(text(label, value, 1, 30)); // ISO format rejects invalid calendar dates
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid " + label + ": use YYYY-MM-DD (example: 2004-05-16).");
        }
    }

    public static LocalDate past(String label, LocalDate d) {
        if (!required(label, d).isBefore(LocalDate.now())) {
            throw new ValidationException(label + " must be in the past.");
        }
        return d;
    }

    public static LocalDate notFuture(String label, LocalDate d) {
        if (required(label, d).isAfter(LocalDate.now())) {
            throw new ValidationException(label + " cannot be in the future.");
        }
        return d;
    }

    public static int range(String label, int n, int min, int max) {
        if (n < min || n > max) {
            if (max == Integer.MAX_VALUE) {
                throw new ValidationException(label + " must be at least " + min + ".");
            }
            throw new ValidationException(label + " must be between " + min + " and " + max + ".");
        }
        return n;
    }

    public static int integer(String label, String value, int min, int max) {
        try {
            return range(label, Integer.parseInt(text(label, value, 1, 20)), min, max);
        } catch (NumberFormatException e) {
            throw new ValidationException(label + " must be a whole number.");
        }
    }

    public static double number(String label, String value) {
        try {
            return Double.parseDouble(text(label, value, 1, 30));
        } catch (NumberFormatException e) {
            throw new ValidationException(label + " must be a number.");
        }
    }

    public static double greaterThanZero(String label, double n) {
        if (n <= 0) {
            throw new ValidationException(label + " must be greater than zero.");
        }
        return n;
    }

    public static double between(String label, double n, double min, double max) {
        if (n < min || n > max) {
            throw new ValidationException(label + " must be between " + min + " and " + max + ".");
        }
        return n;
    }

    public static BigDecimal money(String label, String value) {
        try {
            return positiveMoney(label, new BigDecimal(text(label, value, 1, 30)));
        } catch (NumberFormatException e) {
            throw new ValidationException(label + " must be a valid amount (example: 150000.50).");
        }
    }

    public static BigDecimal positiveMoney(String label, BigDecimal b) {
        if (required(label, b).signum() <= 0) {
            throw new ValidationException(label + " must be greater than zero.");
        }
        return b;
    }

    /** Accepts the option number (1, 2, ...) or the option text. Returns the allowed text. */
    public static String option(String label, String value, String... allowed) {
        String t = text(label, value, 1, 30);
        if (t.matches("\\d+")) {
            int i = Integer.parseInt(t);
            if (i >= 1 && i <= allowed.length) {
                return allowed[i - 1];
            }
        }
        for (String a : allowed) {
            if (a.equalsIgnoreCase(t)) {
                return a;
            }
        }
        throw new ValidationException(label + " must be one of: " + hint(allowed));
    }

    /** Checks a value that is already stored (exact text, ignoring case). */
    public static String oneOf(String label, String value, String... allowed) {
        for (String a : allowed) {
            if (a.equalsIgnoreCase(value)) {
                return a;
            }
        }
        throw new ValidationException(label + " must be one of: " + hint(allowed));
    }

    public static String hint(String... allowed) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allowed.length; i++) {
            sb.append(i == 0 ? "" : " / ").append(i + 1).append(" ").append(allowed[i]);
        }
        return sb.toString();
    }

    public static <T> T required(String label, T value) {
        if (value == null) {
            throw new ValidationException(label + " is required.");
        }
        return value;
    }
}
