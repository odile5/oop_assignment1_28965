package com.auca.main;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.*;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;
import com.auca.util.Check;
import com.auca.util.Console;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {

    // =====================================================================
    //  MENU
    // =====================================================================
    public static void main(String[] args) {
        while (true) {
            printMenu();
            int choice;
            try {
                choice = Integer.parseInt(Console.read("Select an option: ").trim());
            } catch (NumberFormatException e) {
                System.out.println("! Please type a number from the menu.");
                continue;
            }
            switch (choice) {
                case 1 -> run(Main::registerDepartment);
                case 2 -> run(Main::registerStudent);
                case 3 -> run(Main::registerTeacher);
                case 4 -> run(Main::registerAdministrator);
                case 5 -> run(Main::registerCourse);
                case 6 -> run(Main::registerClassroom);
                case 7 -> run(Main::registerEnrollment);
                case 8 -> run(Main::registerAssessment);
                case 9 -> run(Main::registerPayment);
                case 10 -> run(Main::registerBook);
                case 11 -> run(Main::borrowBook);
                case 12 -> run(Main::returnBook);
                case 13 -> run(Main::viewAll);
                case 14 -> run(Main::searchById);
                case 15 -> run(Main::updateRecord);
                case 16 -> run(Main::academicSummary);
                case 17 -> run(Main::paymentHistory);
                case 18 -> run(Main::availableBooksReport);
                case 19 -> run(Main::demonstratePolymorphism);
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("! Option " + choice + " does not exist. Choose 0-19.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n============== AUCA_ID SCHOOL MANAGEMENT ==============");
        System.out.println(" 1. Register Department          11. Borrow Library Book");
        System.out.println(" 2. Register Student             12. Return Library Book");
        System.out.println(" 3. Register Teacher             13. View All Records");
        System.out.println(" 4. Register Administrator       14. Search Record by ID");
        System.out.println(" 5. Register Course              15. Update Existing Record");
        System.out.println(" 6. Register Classroom           16. Student Academic Summary");
        System.out.println(" 7. Enroll Student               17. Student Payment History");
        System.out.println(" 8. Record Assessment            18. View Available Books");
        System.out.println(" 9. Record Fee Payment           19. Demonstrate Polymorphism");
        System.out.println("10. Register Library Book         0. Exit");
    }

    /** The workflow boundary: every exception is caught here, explained, and we go back to the menu. */
    private static void run(Runnable workflow) {
        try {
            workflow.run();
        } catch (CancelException e) {
            System.out.println("Cancelled. Nothing was saved.");
        } catch (PrerequisiteMissingException | BookUnavailableException | RecordNotFoundException
                 | ValidationException | DuplicateRecordException e) {
            System.out.println("! " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("! Something unexpected happened (" + e.getMessage() + "). Returning to the menu.");
        }
    }

    // =====================================================================
    //  SMALL HELPERS
    // =====================================================================
    private static String uniqueId(String s) {
        String v = Check.id("ID", s);
        Store.checkId(v);
        return v;
    }

    private static boolean confirm(String question) {
        while (true) {
            String c = Console.read(question + " (1 Yes / 0 No): ").trim();
            if (c.equals("1")) {
                return true;
            }
            if (c.equals("0")) {
                return false;
            }
            System.out.println("! Enter 1 or 0.");
        }
    }

    private static void pause() {
        Console.read("Press ENTER to return to the menu...");
    }

    /**
     * Numbered selection of an EXISTING record. If the list is empty, offers to register the prerequisite
     * now and then shows the list again. register == null means "no nested registration allowed".
     */
    private static <T extends Identifiable> T pick(String what, Supplier<List<T>> source, Runnable register,
                                                   boolean optional) {
        while (true) {
            List<T> items = source.get();
            System.out.println("+------------- CHOOSE " + what.toUpperCase() + " -------------+");
            if (items.isEmpty()) {
                if (optional) {
                    System.out.println("No " + what + " is registered yet. You can assign one later.");
                    return null;
                }
                if (register == null) {
                    throw new RecordNotFoundException("There are no " + what + " records yet.");
                }
                System.out.println("No " + what + " exists. Register a " + what.toLowerCase() + " first.");
                System.out.println(" 1 Register prerequisite now");
                System.out.println(" 2 Return to menu");
                String c = Console.read("Select: ").trim();
                if (c.equals("1")) {
                    register.run();
                } else if (c.equals("2") || c.equals("0")) {
                    throw new PrerequisiteMissingException("No " + what + " exists. Task stopped and nothing was saved.");
                } else {
                    System.out.println("! Choose 1 or 2.");
                }
                continue; // show the list again (it may now have the new record)
            }
            for (int i = 0; i < items.size(); i++) {
                System.out.println(" " + (i + 1) + " " + Form.show(items.get(i)));
            }
            int registerNumber = items.size() + 1;
            if (register != null) {
                System.out.println(" " + registerNumber + " [+] Register a new " + what);
            }
            System.out.println(" 0 " + (optional ? "No " + what + " for now" : "Cancel"));
            try {
                int n = Integer.parseInt(Console.read("Select option: ").trim());
                if (n == 0) {
                    if (optional) {
                        return null;
                    }
                    throw new CancelException();
                }
                if (n >= 1 && n <= items.size()) {
                    return items.get(n - 1);
                }
                if (n == registerNumber && register != null) {
                    register.run();
                    continue;
                }
                System.out.println("! Choose a number from the list.");
            } catch (NumberFormatException e) {
                System.out.println("! Please type a number.");
            }
        }
    }

    // Pickers used by several forms (each can open a nested registration)
    private static Department pickDepartment() {
        return pick("Department", Store::departments, () -> run(Main::registerDepartment), false);
    }

    private static Teacher pickTeacher(boolean optional) {
        return pick("Teacher", Store::teachers, () -> run(Main::registerTeacher), optional);
    }

    private static Student pickStudent() {
        return pick("Student", Store::students, () -> run(Main::registerStudent), false);
    }

    private static List<LibraryBook> availableBooks() {
        return Store.books().stream().filter(b -> b.getAvailableCopies() > 0).toList();
    }

    private static List<Enrollment> activeEnrollments() {
        return Store.enrollments().stream().filter(Enrollment::isActive).toList();
    }

    private static List<BookLoan> openLoans() {
        return Store.loans().stream().filter(BookLoan::isActive).toList();
    }

    // =====================================================================
    //  REGISTRATION FORMS
    // =====================================================================
    private static void registerDepartment() {
        Form f = new Form("DEPARTMENT REGISTRATION", "Identity", "Contact", "Leadership");
        f.add(1, "Department ID", "String | unique | 4-30", Main::uniqueId);
        f.add(1, "Department name", "String | 2-60 chars", s -> Check.name("Department name", s));
        f.add(1, "Department code", "String | unique | 2-10", s -> {
            String v = Check.text("Department code", s, 2, 10);
            Store.checkDepartmentCode(v);
            return v;
        });
        f.add(2, "Office location", "String | 2-60 chars", s -> Check.text("Office location", s, 2, 60));
        f.add(2, "Contact email", "String | email", s -> Check.email("Contact email", s));
        f.addPick(3, "Head teacher", "Teacher | optional", () -> pickTeacher(true));
        f.run(() -> {
            Department d = new Department(f.str(0), f.str(1), f.str(2), f.str(3), f.str(4), f.get(5));
            d.validate();
            Store.add(d);
            System.out.println("+ Department " + d.getId() + " registered successfully.");
        });
    }

    /** Fields 01-06 shared by Student, Teacher and Administrator. */
    private static void addPersonFields(Form f) {
        f.add(1, "Person ID", "String | unique | 4-30", Main::uniqueId);
        f.add(1, "First name", "String | 2-60 chars", s -> Check.name("First name", s));
        f.add(1, "Last name", "String | 2-60 chars", s -> Check.name("Last name", s));
        f.add(1, "Email", "String | unique | email", s -> {
            String v = Check.email("Email", s);
            Store.checkEmail(v);
            return v;
        });
        f.add(1, "Phone", "String | 10-15 digits", s -> Check.phone("Phone", s));
        f.add(1, "Date of birth", "LocalDate | YYYY-MM-DD",
                s -> Check.past("Date of birth", Check.date("Date of birth", s)));
    }

    /** Fields 07-12 shared by Student and Teacher. */
    private static void addMemberFields(Form f) {
        f.add(2, "Member number", "String | unique | 4-30", s -> {
            String v = Check.id("Member number", s);
            Store.checkMemberNumber(v);
            return v;
        });
        f.addPick(2, "Department", "Department | select", Main::pickDepartment);
        f.add(2, "Join date", "LocalDate | YYYY-MM-DD", s -> Check.notFuture("Join date", Check.date("Join date", s)));
        f.add(2, "Academic status", Check.hint(Check.ACADEMIC_STATUSES),
                s -> Check.option("Academic status", s, Check.ACADEMIC_STATUSES));
        f.add(2, "Institutional email", "String | unique | email", s -> {
            String v = Check.email("Institutional email", s);
            Store.checkInstitutionalEmail(v);
            return v;
        });
        f.add(2, "Advisor name", "String | 2-120 chars", s -> Check.text("Advisor name", s, 2, 120));
    }

    private static void registerStudent() {
        Form f = new Form("STUDENT REGISTRATION", "Personal details", "Academic details", "Student details");
        addPersonFields(f);
        addMemberFields(f);
        f.add(3, "Student number", "String | unique | 4-30", s -> {
            String v = Check.id("Student number", s);
            Store.checkStudentNumber(v);
            return v;
        });
        f.add(3, "Program name", "String | 2-120 chars", s -> Check.text("Program name", s, 2, 120));
        f.add(3, "Admission date", "LocalDate | YYYY-MM-DD",
                s -> Check.notFuture("Admission date", Check.date("Admission date", s)));
        f.add(3, "Study year", "int | 1-6", s -> Check.integer("Study year", s, 1, 6));
        f.add(3, "Guardian phone", "String | 10-15 digits", s -> Check.phone("Guardian phone", s));
        f.add(3, "Scholarship", Check.hint(Check.YES_NO), s -> Check.option("Scholarship", s, Check.YES_NO));
        f.run(() -> {
            Student st = new Student(f.str(0), f.str(1), f.str(2), f.str(3), f.str(4), f.date(5),
                    f.str(6), f.get(7), f.date(8), f.str(9), f.str(10), f.str(11),
                    f.str(12), f.str(13), f.date(14), f.num(15), f.str(16), f.flag(17));
            st.validate();
            Store.add(st);
            System.out.println("+ Student " + st.getStudentNumber() + " registered successfully.");
        });
    }

    private static void registerTeacher() {
        Form f = new Form("TEACHER REGISTRATION", "Personal details", "Academic details", "Teacher details");
        addPersonFields(f);
        addMemberFields(f);
        f.add(3, "Employee number", "String | unique | 4-30", s -> {
            String v = Check.id("Employee number", s);
            Store.checkEmployeeNumber(v);
            return v;
        });
        f.add(3, "Specialization", "String | 2-120 chars", s -> Check.text("Specialization", s, 2, 120));
        f.add(3, "Hire date", "LocalDate | YYYY-MM-DD", s -> Check.notFuture("Hire date", Check.date("Hire date", s)));
        f.add(3, "Salary", "BigDecimal | > 0", s -> Check.money("Salary", s));
        f.add(3, "Office number", "String | 1-20 chars", s -> Check.text("Office number", s, 1, 20));
        f.add(3, "Employment type", Check.hint(Check.EMPLOYMENT_TYPES),
                s -> Check.option("Employment type", s, Check.EMPLOYMENT_TYPES));
        f.run(() -> {
            Teacher t = new Teacher(f.str(0), f.str(1), f.str(2), f.str(3), f.str(4), f.date(5),
                    f.str(6), f.get(7), f.date(8), f.str(9), f.str(10), f.str(11),
                    f.str(12), f.str(13), f.date(14), f.money(15), f.str(16), f.str(17));
            t.validate();
            Store.add(t);
            System.out.println("+ Teacher " + t.getEmployeeNumber() + " registered successfully.");
        });
    }

    private static void registerAdministrator() {
        Form f = new Form("ADMINISTRATOR REGISTRATION", "Personal details", "Staff details", "Access details");
        addPersonFields(f);
        f.add(2, "Staff number", "String | unique | 4-30", s -> {
            String v = Check.id("Staff number", s);
            Store.checkStaffNumber(v);
            return v;
        });
        f.add(2, "Job title", "String | 2-60 chars", s -> Check.text("Job title", s, 2, 60));
        f.add(2, "Assigned office", "String | 1-60 chars", s -> Check.text("Assigned office", s, 1, 60));
        f.add(3, "Employment date", "LocalDate | YYYY-MM-DD",
                s -> Check.notFuture("Employment date", Check.date("Employment date", s)));
        f.add(3, "Access level", "int | 1-5", s -> Check.integer("Access level", s, 1, 5));
        f.add(3, "Active", Check.hint(Check.YES_NO), s -> Check.option("Active", s, Check.YES_NO));
        f.run(() -> {
            Administrator a = new Administrator(f.str(0), f.str(1), f.str(2), f.str(3), f.str(4), f.date(5),
                    f.str(6), f.str(7), f.str(8), f.date(9), f.num(10), f.flag(11));
            a.validate();
            Store.add(a);
            System.out.println("+ Administrator " + a.getStaffNumber() + " registered successfully.");
        });
    }

    private static void registerCourse() {
        Form f = new Form("COURSE REGISTRATION", "Identity", "Academic details", "Department and teacher");
        f.add(1, "Course ID", "String | unique | 4-30", Main::uniqueId);
        f.add(1, "Course code", "String | unique | 2-20", s -> {
            String v = Check.text("Course code", s, 2, 20);
            Store.checkCourseCode(v);
            return v;
        });
        f.add(1, "Course title", "String | 2-120 chars", s -> Check.text("Course title", s, 2, 120));
        f.add(2, "Credit hours", "int | 1-6", s -> Check.integer("Credit hours", s, 1, 6));
        f.addPick(3, "Department", "Department | select", Main::pickDepartment);
        f.addPick(3, "Assigned teacher", "Teacher | select", () -> pickTeacher(false));
        f.run(() -> {
            Course c = new Course(f.str(0), f.str(1), f.str(2), f.num(3), f.get(4), f.get(5));
            c.validate();
            Store.add(c);
            System.out.println("+ Course " + c.getCourseCode() + " saved with ID " + c.getId() + ".");
        });
    }

    private static void registerClassroom() {
        Form f = new Form("CLASSROOM REGISTRATION", "Identity", "Size and place", "Availability");
        f.add(1, "Classroom ID", "String | unique | 4-30", Main::uniqueId);
        f.add(1, "Building name", "String | 2-60 chars", s -> Check.text("Building name", s, 2, 60));
        f.add(1, "Room number", "String | 1-20 chars", s -> Check.text("Room number", s, 1, 20));
        f.add(2, "Capacity", "int | > 0", s -> Check.integer("Capacity", s, 1, Integer.MAX_VALUE));
        f.add(2, "Floor number", "int | 0 or more", s -> Check.integer("Floor number", s, 0, Integer.MAX_VALUE));
        f.add(3, "Available", Check.hint(Check.YES_NO), s -> Check.option("Available", s, Check.YES_NO));
        f.run(() -> {
            Classroom c = new Classroom(f.str(0), f.str(1), f.str(2), f.num(3), f.num(4), f.flag(5));
            c.validate();
            Store.add(c);
            System.out.println("+ Classroom " + c.getId() + " registered successfully.");
        });
    }

    private static void registerEnrollment() {
        Form f = new Form("ENROLL STUDENT", "Identity", "Student and course", "Semester details");
        f.add(1, "Enrollment ID", "String | unique | 4-30", Main::uniqueId);
        f.addPick(2, "Student", "Student | select", Main::pickStudent);
        f.addPick(2, "Course", "Course | select",
                () -> pick("Course", Store::courses, () -> run(Main::registerCourse), false));
        f.add(3, "Enrollment date", "LocalDate | YYYY-MM-DD",
                s -> Check.notFuture("Enrollment date", Check.date("Enrollment date", s)));
        f.add(3, "Semester", Check.hint(Check.SEMESTERS), s -> {
            String v = Check.option("Semester", s, Check.SEMESTERS);
            Store.checkEnrollment(f.get(1), f.get(2), v); // no duplicate active enrollment
            return v;
        });
        f.add(3, "Enrollment status", Check.hint(Check.ENROLLMENT_STATUSES),
                s -> Check.option("Enrollment status", s, Check.ENROLLMENT_STATUSES));
        f.run(() -> {
            Enrollment e = new Enrollment(f.str(0), f.get(1), f.get(2), f.date(3), f.str(4), f.str(5));
            e.validate();
            Store.add(e);
            System.out.println("+ Enrollment " + e.getId() + " saved: " + e.getDisplayName());
        });
    }

    private static void registerAssessment() {
        Form f = new Form("RECORD ASSESSMENT", "Identity", "Scores", "Date");
        f.add(1, "Assessment ID", "String | unique | 4-30", Main::uniqueId);
        f.addPick(1, "Enrollment", "Enrollment | active only",
                () -> pick("Enrollment", Main::activeEnrollments, () -> run(Main::registerEnrollment), false));
        f.add(2, "Assessment type", Check.hint(Check.ASSESSMENT_TYPES),
                s -> Check.option("Assessment type", s, Check.ASSESSMENT_TYPES));
        f.add(2, "Maximum score", "double | > 0",
                s -> Check.greaterThanZero("Maximum score", Check.number("Maximum score", s)));
        f.add(2, "Obtained score", "double | 0 to maximum",
                s -> Check.between("Obtained score", Check.number("Obtained score", s), 0, f.dbl(3)));
        f.add(3, "Assessment date", "LocalDate | YYYY-MM-DD",
                s -> Check.notFuture("Assessment date", Check.date("Assessment date", s)));
        f.run(() -> {
            Enrollment en = f.get(1);
            if (!en.isActive()) {
                throw new ValidationException("Only an active enrollment can be assessed.");
            }
            Assessment a = new Assessment(f.str(0), en, f.str(2), f.dbl(3), f.dbl(4), f.date(5));
            a.validate();
            Store.add(a);
            System.out.println("+ Assessment " + a.getId() + " recorded: " + a.getDisplayName());
        });
    }

    private static void registerPayment() {
        Form f = new Form("FEE PAYMENT", "Identity", "Payment details", "Receipt");
        f.add(1, "Payment ID", "String | unique | 4-30", Main::uniqueId);
        f.addPick(1, "Student", "Student | select", Main::pickStudent);
        f.add(2, "Amount", "BigDecimal | > 0", s -> Check.money("Amount", s));
        f.add(2, "Payment date", "LocalDate | YYYY-MM-DD",
                s -> Check.notFuture("Payment date", Check.date("Payment date", s)));
        f.add(2, "Payment method", Check.hint(Check.PAYMENT_METHODS),
                s -> Check.option("Payment method", s, Check.PAYMENT_METHODS));
        f.add(3, "Receipt number", "String | unique | not empty", s -> {
            String v = Check.text("Receipt number", s, 1, 30);
            Store.checkReceipt(v);
            return v;
        });
        f.run(() -> {
            FeePayment p = new FeePayment(f.str(0), f.get(1), f.money(2), f.date(3), f.str(4), f.str(5));
            p.validate();
            Store.add(p);
            System.out.println("+ Payment " + p.getId() + " saved, receipt " + p.getReceiptNumber() + ".");
        });
    }

    private static void registerBook() {
        Form f = new Form("LIBRARY BOOK REGISTRATION", "Identity", "Book details", "Stock");
        f.add(1, "Book ID", "String | unique | 4-30", Main::uniqueId);
        f.add(1, "ISBN", "String | unique | 10-17 chars", s -> {
            String v = Check.text("ISBN", s, 10, 17);
            Store.checkIsbn(v);
            return v;
        });
        f.add(2, "Title", "String | 1-120 chars", s -> Check.text("Title", s, 1, 120));
        f.add(2, "Author", "String | 2-120 chars", s -> Check.text("Author", s, 2, 120));
        f.add(3, "Total copies", "int | 0 or more", s -> Check.integer("Total copies", s, 0, Integer.MAX_VALUE));
        f.add(3, "Available copies", "int | 0 to total",
                s -> Check.integer("Available copies", s, 0, f.num(4)));
        f.run(() -> {
            LibraryBook b = new LibraryBook(f.str(0), f.str(1), f.str(2), f.str(3), f.num(4), f.num(5));
            b.validate();
            Store.add(b);
            System.out.println("+ Book " + b.getId() + " registered: " + b.getTitle());
        });
    }

    // =====================================================================
    //  BORROW / RETURN
    // =====================================================================
    private static void borrowBook() {
        Form f = new Form("BORROW LIBRARY BOOK", "Identity", "Student and book", "Dates");
        f.add(1, "Loan ID", "String | unique | 4-30", Main::uniqueId);
        f.addPick(2, "Student", "Student | select", Main::pickStudent);
        f.addPick(2, "Library book", "LibraryBook | available only", () -> {
            if (!Store.books().isEmpty() && availableBooks().isEmpty()) {
                throw new BookUnavailableException("All books are on loan: no copy is available right now.");
            }
            return pick("Library book", Main::availableBooks, () -> run(Main::registerBook), false);
        });
        f.add(3, "Borrowed date", "LocalDate | YYYY-MM-DD",
                s -> Check.notFuture("Borrowed date", Check.date("Borrowed date", s)));
        f.add(3, "Due date", "LocalDate | after borrowed date", s -> {
            LocalDate due = Check.date("Due date", s);
            if (!due.isAfter(f.date(3))) {
                throw new ValidationException("Due date must be after the borrowed date.");
            }
            return due;
        });
        f.addFixed(3, "Returned date", "(not returned yet)", null);
        f.run(() -> {
            LibraryBook book = f.get(2);
            if (book.getAvailableCopies() <= 0) { // re-check just before saving
                throw new BookUnavailableException("No copy of \"" + book.getTitle() + "\" is available.");
            }
            BookLoan loan = new BookLoan(f.str(0), f.get(1), book, f.date(3), f.date(4), null);
            loan.validate();
            Store.add(loan);
            book.setAvailableCopies(book.getAvailableCopies() - 1); // exactly once
            System.out.println("+ Loan " + loan.getId() + " saved. Copies left: " + book.getAvailableCopies());
        });
    }

    private static void returnBook() {
        BookLoan loan = pick("Open loan", Main::openLoans, null, false);
        System.out.println("Returning: " + loan);
        LocalDate returned = (LocalDate) Form.ask("Return date", "LocalDate | YYYY-MM-DD", s -> {
            LocalDate d = Check.notFuture("Return date", Check.date("Return date", s));
            if (d.isBefore(loan.getBorrowedDate())) {
                throw new ValidationException("Return date cannot be before the borrowed date.");
            }
            return d;
        });
        if (!confirm("Confirm the return of \"" + loan.getBook().getTitle() + "\"?")) {
            throw new CancelException();
        }
        loan.setReturnedDate(returned);
        LibraryBook book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1); // exactly once
        System.out.println("+ Book returned. Copies available now: " + book.getAvailableCopies());
    }

    // =====================================================================
    //  VIEW / SEARCH / UPDATE / REPORTS
    // =====================================================================
    private static void printGroup(String name, List<? extends Identifiable> list) {
        System.out.println("--- " + name + " (" + list.size() + ") ---");
        for (Identifiable item : list) {
            System.out.println("  " + item);
        }
    }

    private static void viewAll() {
        if (Store.all().isEmpty()) {
            System.out.println("No records have been saved yet.");
        } else {
            printGroup("Departments", Store.departments());
            printGroup("Students", Store.students());
            printGroup("Teachers", Store.teachers());
            printGroup("Administrators", Store.administrators());
            printGroup("Courses", Store.courses());
            printGroup("Classrooms", Store.classrooms());
            printGroup("Enrollments", Store.enrollments());
            printGroup("Assessments", Store.assessments());
            printGroup("Fee payments", Store.payments());
            printGroup("Library books", Store.books());
            printGroup("Book loans", Store.loans());
        }
        pause();
    }

    private static void searchById() {
        String id = Console.read("Enter the record ID to search (CANCEL to go back): ").trim();
        if (id.equalsIgnoreCase("CANCEL") || id.isEmpty()) {
            throw new CancelException();
        }
        for (Identifiable item : Store.all()) {
            if (item.getId().equalsIgnoreCase(id)) {
                Registrable found = (Registrable) item; // every record is both Identifiable and Registrable
                System.out.println("Found: " + found.getRegistrationSummary());
                found.printDetails();
                return;
            }
        }
        throw new RecordNotFoundException("No record with ID " + id + " exists.");
    }

    /** Asks for a new value, shows it, asks for confirmation, then applies it with the setter. */
    private static void change(String what, String hint, Function<String, Object> parser, Consumer<Object> setter) {
        Object value = Form.ask("New " + what, hint, parser);
        if (confirm("Change " + what + " to " + Form.show(value) + "?")) {
            setter.accept(value);
            System.out.println("+ " + what + " updated.");
        } else {
            System.out.println("Change discarded.");
        }
    }

    private static int choose(String... options) {
        System.out.println("Which field do you want to change?");
        for (int i = 0; i < options.length; i++) {
            System.out.println(" " + (i + 1) + " " + options[i]);
        }
        System.out.println(" 0 Cancel");
        while (true) {
            try {
                int n = Integer.parseInt(Console.read("Select: ").trim());
                if (n == 0) {
                    throw new CancelException();
                }
                if (n >= 1 && n <= options.length) {
                    return n;
                }
                System.out.println("! Choose 0-" + options.length + ".");
            } catch (NumberFormatException e) {
                System.out.println("! Please type a number.");
            }
        }
    }

    private static void updateRecord() {
        Identifiable record = pick("Record", Store::all, null, false);
        System.out.println("Selected: " + record);
        System.out.println("(IDs can never be changed, so only other fields are offered.)");
        Function<String, Object> phone = x -> Check.phone("Phone", x);
        if (record instanceof Student st) {
            if (choose("Phone", "Study year") == 1) {
                change("phone", "String | 10-15 digits", phone, v -> st.setPhoneNumber((String) v));
            } else {
                change("study year", "int | 1-6", x -> Check.integer("Study year", x, 1, 6), v -> st.setStudyYear((Integer) v));
            }
        } else if (record instanceof Teacher te) {
            if (choose("Phone", "Salary") == 1) {
                change("phone", "String | 10-15 digits", phone, v -> te.setPhoneNumber((String) v));
            } else {
                change("salary", "BigDecimal | > 0", x -> Check.money("Salary", x), v -> te.setSalary((BigDecimal) v));
            }
        } else if (record instanceof Administrator ad) {
            if (choose("Phone", "Active") == 1) {
                change("phone", "String | 10-15 digits", phone, v -> ad.setPhoneNumber((String) v));
            } else {
                change("active", Check.hint(Check.YES_NO), x -> Check.option("Active", x, Check.YES_NO), v -> ad.setActive("YES".equals(v)));
            }
        } else if (record instanceof Department de) {
            if (choose("Office location", "Head teacher") == 1) {
                change("office location", "String | 2-60 chars", x -> Check.text("Office location", x, 2, 60), v -> de.setOfficeLocation((String) v));
            } else {
                Teacher head = pickTeacher(false);
                if (confirm("Make " + head.getDisplayName() + " head of " + de.getDepartmentName() + "?")) {
                    de.setHeadTeacher(head);
                    System.out.println("+ Head teacher assigned.");
                }
            }
        } else if (record instanceof Course co) {
            if (choose("Course title", "Credit hours") == 1) {
                change("course title", "String | 2-120 chars", x -> Check.text("Course title", x, 2, 120), v -> co.setCourseTitle((String) v));
            } else {
                change("credit hours", "int | 1-6", x -> Check.integer("Credit hours", x, 1, 6), v -> co.setCreditHours((Integer) v));
            }
        } else if (record instanceof Classroom cl) {
            if (choose("Capacity", "Available") == 1) {
                change("capacity", "int | > 0", x -> Check.integer("Capacity", x, 1, Integer.MAX_VALUE), v -> cl.setCapacity((Integer) v));
            } else {
                change("available", Check.hint(Check.YES_NO), x -> Check.option("Available", x, Check.YES_NO), v -> cl.setAvailable("YES".equals(v)));
            }
        } else if (record instanceof Enrollment en) {
            choose("Enrollment status");
            change("status", Check.hint(Check.ENROLLMENT_STATUSES), x -> Check.option("Status", x, Check.ENROLLMENT_STATUSES), v -> en.setEnrollmentStatus((String) v));
        } else if (record instanceof Assessment as) {
            choose("Obtained score");
            change("obtained score", "double | 0 to " + as.getMaximumScore(),
                    x -> Check.between("Obtained score", Check.number("Obtained score", x), 0, as.getMaximumScore()),
                    v -> as.setObtainedScore((Double) v));
        } else if (record instanceof FeePayment fp) {
            choose("Payment method");
            change("payment method", Check.hint(Check.PAYMENT_METHODS), x -> Check.option("Payment method", x, Check.PAYMENT_METHODS), v -> fp.setPaymentMethod((String) v));
        } else if (record instanceof LibraryBook bk) {
            if (choose("Title", "Author") == 1) {
                change("title", "String | 1-120 chars", x -> Check.text("Title", x, 1, 120), v -> bk.setTitle((String) v));
            } else {
                change("author", "String | 2-120 chars", x -> Check.text("Author", x, 2, 120), v -> bk.setAuthor((String) v));
            }
        } else if (record instanceof BookLoan bl) {
            choose("Due date");
            change("due date", "LocalDate | after borrowed date", x -> {
                LocalDate due = Check.date("Due date", x);
                if (!due.isAfter(bl.getBorrowedDate())) {
                    throw new ValidationException("Due date must be after the borrowed date.");
                }
                return due;
            }, v -> bl.setDueDate((LocalDate) v));
        }
    }

    private static void academicSummary() {
        Student s = pick("Student", Store::students, null, false);
        System.out.println("\n" + s);
        boolean any = false;
        double got = 0;
        double max = 0;
        for (Enrollment e : Store.enrollments()) {
            if (e.getStudent() != s) {
                continue;
            }
            any = true;
            System.out.println("  Course " + e.getCourse().getDisplayName() + " (" + e.getSemester() + ", " + e.getEnrollmentStatus() + ")");
            for (Assessment a : Store.assessments()) {
                if (a.getEnrollment() == e) {
                    System.out.println("     " + a.getAssessmentType() + ": " + a.getObtainedScore() + " / " + a.getMaximumScore());
                    got += a.getObtainedScore();
                    max += a.getMaximumScore();
                }
            }
        }
        if (!any) {
            System.out.println("  This student has no enrollments yet.");
        } else if (max > 0) {
            System.out.printf("  Overall: %.1f / %.1f = %.1f%%%n", got, max, got * 100 / max);
        } else {
            System.out.println("  No assessments recorded yet.");
        }
        pause();
    }

    private static void paymentHistory() {
        Student s = pick("Student", Store::students, null, false);
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (FeePayment p : Store.payments()) {
            if (p.getStudent() == s) {
                System.out.println("  " + p);
                total = total.add(p.getAmount());
                count++;
            }
        }
        if (count == 0) {
            System.out.println("No payments recorded for " + s.getDisplayName() + ".");
        } else {
            System.out.println("Payments: " + count + " | Total paid: " + total);
        }
        pause();
    }

    private static void availableBooksReport() {
        List<LibraryBook> list = availableBooks();
        if (list.isEmpty()) {
            System.out.println("No books are available right now.");
        } else {
            for (LibraryBook b : list) {
                System.out.println("  " + b);
            }
        }
        pause();
    }

    // =====================================================================
    //  POLYMORPHISM DEMONSTRATION
    // =====================================================================
    private static void demonstratePolymorphism() {
        System.out.println("=== 1. Superclass polymorphism: List<Person> ===");
        List<Person> people = new ArrayList<>(Store.persons());
        if (people.isEmpty()) {
            System.out.println("(register a Student, Teacher or Administrator first to see this part)");
        }
        for (Person p : people) {
            // The same call runs different code depending on the real object type (runtime polymorphism).
            System.out.println(p.getRoleDescription() + " | " + p.getDisplayName() + " | active=" + p.isActive()
                    + " | " + p.getRegistrationSummary());
            String chain = "";
            for (Class<?> c = p.getClass(); c != Object.class; c = c.getSuperclass()) {
                chain += c.getSimpleName() + (c.getSuperclass() == Object.class ? "" : " -> ");
            }
            System.out.println("     inheritance chain: " + chain);
            if (p instanceof AcademicMember m) {
                System.out.println("     academic category: " + m.getAcademicCategory());
            }
        }
        System.out.println("\n=== 2. Interface polymorphism: List<Identifiable> ===");
        List<Identifiable> things = new ArrayList<>(Store.all());
        if (things.isEmpty()) {
            System.out.println("(no records yet)");
        }
        for (Identifiable item : things) {
            System.out.println(item.getClass().getSimpleName() + " | " + item.getId() + " | "
                    + item.getDisplayName() + " | active=" + item.isActive());
        }
        System.out.println("\n=== Notes ===");
        System.out.println("* Person and AcademicMember are abstract: they declare abstract methods (getRoleDescription,");
        System.out.println("  getAcademicCategory) with no body, so Java refuses 'new Person(...)'. Only concrete subclasses can be created.");
        System.out.println("* Multilevel: Person -> AcademicMember -> Student/Teacher. Hierarchical: Student, Teacher and Administrator all extend Person.");
        System.out.println("* Constructor chaining: Student -> super(...) -> AcademicMember -> super(...) -> Person.");
        pause();
    }
}
