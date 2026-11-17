package com.auca.main;

import com.auca.exceptions.CancelException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.util.Console;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * One reusable step-by-step form: prompts every field, supports BACK / CANCEL,
 * shows a full review, and lets the user Save, Edit a field or Cancel.
 * Each field is either typed text (with a parser) or a picked object (with a picker).
 */
public class Form {
    private static class Field {
        int step;
        String label;
        String hint;
        Function<String, Object> parser; // for typed fields
        Supplier<Object> picker;         // for fields chosen from a list
        Object value;
        boolean fixed;                   // derived value: shown but not asked
    }

    private final String title;
    private final String[] stepNames;
    private final List<Field> fields = new ArrayList<>();

    public Form(String title, String... stepNames) {
        this.title = title;
        this.stepNames = stepNames;
    }

    // ---------------- building the form ----------------
    private Field newField(int step, String label, String hint) {
        Field f = new Field();
        f.step = step;
        f.label = label;
        f.hint = hint;
        fields.add(f);
        return f;
    }

    public void add(int step, String label, String hint, Function<String, Object> parser) {
        newField(step, label, hint).parser = parser;
    }

    public void addPick(int step, String label, String hint, Supplier<Object> picker) {
        newField(step, label, hint).picker = picker;
    }

    public void addFixed(int step, String label, String note, Object value) {
        Field f = newField(step, label, note);
        f.fixed = true;
        f.value = value;
    }

    // ---------------- reading values after the form is complete ----------------
    @SuppressWarnings("unchecked")
    public <T> T get(int i) { return (T) fields.get(i).value; }
    public String str(int i) { return (String) fields.get(i).value; }
    public int num(int i) { return (Integer) fields.get(i).value; }
    public double dbl(int i) { return (Double) fields.get(i).value; }
    public LocalDate date(int i) { return (LocalDate) fields.get(i).value; }
    public BigDecimal money(int i) { return (BigDecimal) fields.get(i).value; }
    public boolean flag(int i) { return "YES".equals(fields.get(i).value); }

    // ---------------- running the form ----------------
    public void run(Runnable save) {
        int i = 0;
        while (i < fields.size()) {
            Field f = fields.get(i);
            if (f.fixed) {
                i++;
                continue;
            }
            if (i == 0 || fields.get(i - 1).step != f.step) {
                header(f.step);
            }
            if (f.picker != null) {
                f.value = f.picker.get(); // the picker handles its own Cancel / nested registration
                i++;
                continue;
            }
            String line = prompt(i, f);
            if (line.equalsIgnoreCase("BACK")) {
                if (i > 0) {
                    do {
                        i--;
                    } while (i > 0 && fields.get(i).fixed);
                } else {
                    System.out.println("! This is the first field.");
                }
                continue;
            }
            try {
                f.value = f.parser.apply(line);
                i++;
            } catch (ValidationException | DuplicateRecordException | NumberFormatException | DateTimeParseException e) {
                System.out.println("! " + e.getMessage());
                System.out.println("  Please re-enter field " + String.format("%02d", i + 1) + ".");
            }
        }
        review(save);
    }

    private String prompt(int i, Field f) {
        String line = Console.read(String.format("%02d %-20s [%s] : ", i + 1, f.label, f.hint)).trim();
        if (line.equalsIgnoreCase("CANCEL")) {
            throw new CancelException();
        }
        return line;
    }

    private void header(int step) {
        int total = stepNames.length + 1;
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.printf("| %-40s Step %d of %d%n", title, step, total);
        StringBuilder bar = new StringBuilder("| ");
        for (int s = 0; s < stepNames.length; s++) {
            bar.append("[").append(s + 1).append("] ").append(stepNames[s]).append("  ");
        }
        System.out.println(bar + "[" + total + "] Review & confirm");
        System.out.println("| Type BACK to return to the previous field, CANCEL to discard.");
        System.out.println("+------------------------------------------------------------+");
    }

    private void review(Runnable save) {
        while (true) {
            header(stepNames.length + 1);
            for (int i = 0; i < fields.size(); i++) {
                Field f = fields.get(i);
                String shown = (f.fixed && f.value == null) ? f.hint : show(f.value);
                System.out.printf("%02d %-20s : %s%n", i + 1, f.label, shown);
            }
            System.out.println("[1] Save   [2] Edit field   [0] Cancel");
            String choice = Console.read("Select option: ").trim();
            switch (choice) {
                case "1" -> {
                    try {
                        save.run(); // builds the object, validates it, stores it
                        return;
                    } catch (CancelException e) {
                        throw e;
                    } catch (RuntimeException e) {
                        System.out.println("! Could not save: " + e.getMessage());
                        System.out.println("  Edit the field that causes the problem, or cancel.");
                    }
                }
                case "2" -> edit();
                case "0" -> throw new CancelException();
                default -> System.out.println("! Choose 1, 2 or 0.");
            }
        }
    }

    private void edit() {
        try {
            int n = Integer.parseInt(Console.read("Field number (01-" + String.format("%02d", fields.size()) + "): ").trim());
            if (n < 1 || n > fields.size()) {
                System.out.println("! There is no field " + n + ".");
                return;
            }
            Field f = fields.get(n - 1);
            if (f.fixed) {
                System.out.println("! This field is derived and cannot be edited.");
                return;
            }
            System.out.println("Old value: " + show(f.value));
            if (f.picker != null) {
                f.value = f.picker.get();
            } else {
                f.value = ask(String.format("%02d %s", n, f.label), f.hint, f.parser);
            }
        } catch (NumberFormatException e) {
            System.out.println("! Please type a field number.");
        }
    }

    /** Asks one value until it is valid. Typing CANCEL abandons the whole action. */
    public static Object ask(String label, String hint, Function<String, Object> parser) {
        while (true) {
            String line = Console.read(label + " [" + hint + "] : ").trim();
            if (line.equalsIgnoreCase("CANCEL")) {
                throw new CancelException();
            }
            try {
                return parser.apply(line);
            } catch (ValidationException | DuplicateRecordException | NumberFormatException | DateTimeParseException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    /** Human-readable text for any value (related records show as "[ID] name"). */
    public static String show(Object v) {
        if (v == null) {
            return "(none)";
        }
        if (v instanceof Identifiable i) {
            return "[" + i.getId() + "] " + i.getDisplayName();
        }
        return String.valueOf(v);
    }
}
