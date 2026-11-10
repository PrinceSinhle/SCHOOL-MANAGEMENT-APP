package schoolmanagement.za;

import java.time.DateTimeException;
import java.time.LocalDate;

final class SouthAfricanIdValidator {
    private SouthAfricanIdValidator() { }

    static boolean isValid(String id) {
        if (id == null || !id.matches("\\d{13}")) return false;
        int yearPart = Integer.parseInt(id.substring(0, 2));
        int month = Integer.parseInt(id.substring(2, 4));
        int day = Integer.parseInt(id.substring(4, 6));
        LocalDate today = LocalDate.now();
        if (!validDate(1900 + yearPart, month, day, today)
                && !validDate(2000 + yearPart, month, day, today)) return false;

        int sum = 0;
        for (int index = 0; index < 12; index++) {
            int digit = id.charAt(index) - '0';
            if (index % 2 == 1) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
        }
        return (10 - (sum % 10)) % 10 == id.charAt(12) - '0';
    }

    private static boolean validDate(int year, int month, int day, LocalDate today) {
        try {
            return !LocalDate.of(year, month, day).isAfter(today);
        } catch (DateTimeException ex) {
            return false;
        }
    }
}
