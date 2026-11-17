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
import com.auca.exceptions.DuplicateRecordException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;
import java.util.ArrayList;
import java.util.List;

/** In-memory storage (typed ArrayLists) plus all the uniqueness rules. */
public class Store {
    private static final List<Department> departments = new ArrayList<>();
    private static final List<Student> students = new ArrayList<>();
    private static final List<Teacher> teachers = new ArrayList<>();
    private static final List<Administrator> administrators = new ArrayList<>();
    private static final List<Course> courses = new ArrayList<>();
    private static final List<Classroom> classrooms = new ArrayList<>();
    private static final List<Enrollment> enrollments = new ArrayList<>();
    private static final List<Assessment> assessments = new ArrayList<>();
    private static final List<FeePayment> payments = new ArrayList<>();
    private static final List<LibraryBook> books = new ArrayList<>();
    private static final List<BookLoan> loans = new ArrayList<>();

    private Store() {
    }

    public static List<Department> departments() { return departments; }
    public static List<Student> students() { return students; }
    public static List<Teacher> teachers() { return teachers; }
    public static List<Administrator> administrators() { return administrators; }
    public static List<Course> courses() { return courses; }
    public static List<Classroom> classrooms() { return classrooms; }
    public static List<Enrollment> enrollments() { return enrollments; }
    public static List<Assessment> assessments() { return assessments; }
    public static List<FeePayment> payments() { return payments; }
    public static List<LibraryBook> books() { return books; }
    public static List<BookLoan> loans() { return loans; }

    /** Every record of every type, seen only through the Identifiable interface. */
    public static List<Identifiable> all() {
        List<Identifiable> all = new ArrayList<>();
        all.addAll(departments);
        all.addAll(students);
        all.addAll(teachers);
        all.addAll(administrators);
        all.addAll(courses);
        all.addAll(classrooms);
        all.addAll(enrollments);
        all.addAll(assessments);
        all.addAll(payments);
        all.addAll(books);
        all.addAll(loans);
        return all;
    }

    public static List<Person> persons() {
        List<Person> list = new ArrayList<>();
        list.addAll(students);
        list.addAll(teachers);
        list.addAll(administrators);
        return list;
    }

    public static List<AcademicMember> members() {
        List<AcademicMember> list = new ArrayList<>();
        list.addAll(students);
        list.addAll(teachers);
        return list;
    }

    // ---------------- uniqueness checks (used while typing AND again on save) ----------------
    private static void dup(boolean exists, String message) {
        if (exists) {
            throw new DuplicateRecordException(message);
        }
    }

    public static void checkId(String id) {
        dup(all().stream().anyMatch(x -> x.getId().equalsIgnoreCase(id)), "ID " + id + " already exists.");
    }

    public static void checkEmail(String email) {
        dup(persons().stream().anyMatch(p -> p.getEmail().equalsIgnoreCase(email)), "Email " + email + " already exists.");
    }

    public static void checkInstitutionalEmail(String email) {
        dup(members().stream().anyMatch(m -> m.getInstitutionalEmail().equalsIgnoreCase(email)),
                "Institutional email " + email + " already exists.");
    }

    public static void checkMemberNumber(String n) {
        dup(members().stream().anyMatch(m -> m.getMemberNumber().equalsIgnoreCase(n)), "Member number " + n + " already exists.");
    }

    public static void checkStudentNumber(String n) {
        dup(students.stream().anyMatch(s -> s.getStudentNumber().equalsIgnoreCase(n)), "Student number " + n + " already exists.");
    }

    public static void checkEmployeeNumber(String n) {
        dup(teachers.stream().anyMatch(t -> t.getEmployeeNumber().equalsIgnoreCase(n)), "Employee number " + n + " already exists.");
    }

    public static void checkStaffNumber(String n) {
        dup(administrators.stream().anyMatch(a -> a.getStaffNumber().equalsIgnoreCase(n)), "Staff number " + n + " already exists.");
    }

    public static void checkDepartmentCode(String c) {
        dup(departments.stream().anyMatch(d -> d.getDepartmentCode().equalsIgnoreCase(c)), "Department code " + c + " already exists.");
    }

    public static void checkCourseCode(String c) {
        dup(courses.stream().anyMatch(x -> x.getCourseCode().equalsIgnoreCase(c)), "Course code " + c + " already exists.");
    }

    public static void checkReceipt(String r) {
        dup(payments.stream().anyMatch(p -> p.getReceiptNumber().equalsIgnoreCase(r)), "Receipt number " + r + " already exists.");
    }

    public static void checkIsbn(String isbn) {
        dup(books.stream().anyMatch(b -> b.getIsbn().equalsIgnoreCase(isbn)), "ISBN " + isbn + " already exists.");
    }

    public static void checkEnrollment(Student s, Course c, String semester) {
        dup(enrollments.stream().anyMatch(e -> e.isActive() && e.getStudent() == s && e.getCourse() == c
                && e.getSemester().equalsIgnoreCase(semester)),
                s.getDisplayName() + " already has an active enrollment in " + c.getCourseCode() + " for " + semester + ".");
    }

    // ---------------- add methods (each re-checks the rules before storing) ----------------
    public static void add(Department d) {
        checkId(d.getId());
        checkDepartmentCode(d.getDepartmentCode());
        departments.add(d);
    }

    public static void add(Student s) {
        checkId(s.getId());
        checkEmail(s.getEmail());
        checkMemberNumber(s.getMemberNumber());
        checkInstitutionalEmail(s.getInstitutionalEmail());
        checkStudentNumber(s.getStudentNumber());
        students.add(s);
    }

    public static void add(Teacher t) {
        checkId(t.getId());
        checkEmail(t.getEmail());
        checkMemberNumber(t.getMemberNumber());
        checkInstitutionalEmail(t.getInstitutionalEmail());
        checkEmployeeNumber(t.getEmployeeNumber());
        teachers.add(t);
    }

    public static void add(Administrator a) {
        checkId(a.getId());
        checkEmail(a.getEmail());
        checkStaffNumber(a.getStaffNumber());
        administrators.add(a);
    }

    public static void add(Course c) {
        checkId(c.getId());
        checkCourseCode(c.getCourseCode());
        courses.add(c);
    }

    public static void add(Classroom c) {
        checkId(c.getId());
        classrooms.add(c);
    }

    public static void add(Enrollment e) {
        checkId(e.getId());
        if (e.isActive()) {
            checkEnrollment(e.getStudent(), e.getCourse(), e.getSemester());
        }
        enrollments.add(e);
    }

    public static void add(Assessment a) {
        checkId(a.getId());
        assessments.add(a);
    }

    public static void add(FeePayment p) {
        checkId(p.getId());
        checkReceipt(p.getReceiptNumber());
        payments.add(p);
    }

    public static void add(LibraryBook b) {
        checkId(b.getId());
        checkIsbn(b.getIsbn());
        books.add(b);
    }

    public static void add(BookLoan l) {
        checkId(l.getId());
        loans.add(l);
    }
}
