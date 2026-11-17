package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.util.Check;
import java.time.LocalDate;

/** Abstract base of every person. It cannot be instantiated because getRoleDescription() has no body here. */
public abstract class Person implements Identifiable, Registrable {
    private String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    public Person(String personId, String firstName, String lastName, String email,
                  String phoneNumber, LocalDate dateOfBirth) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public String getPersonId() { return personId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    /** Each subclass describes its own role (polymorphism). */
    public abstract String getRoleDescription();

    @Override
    public String getId() { return personId; }

    @Override
    public String getDisplayName() { return firstName + " " + lastName; }

    @Override
    public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Check.id("Person ID", personId);
        Check.name("First name", firstName);
        Check.name("Last name", lastName);
        Check.email("Email", email);
        Check.phone("Phone", phoneNumber);
        Check.past("Date of birth", dateOfBirth);
    }

    @Override
    public void printDetails() { System.out.println(this); }

    @Override
    public String getRegistrationSummary() {
        return getRoleDescription() + " " + ID_SEPARATOR + " " + personId + " " + ID_SEPARATOR + " " + getDisplayName();
    }

    @Override
    public String toString() {
        return "[" + personId + "] " + getDisplayName() + " | " + email + " | " + phoneNumber + " | born " + dateOfBirth;
    }
}
