package br.unioeste.mu.mu_backend.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegistrationRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "University is required")
    private String university;

    @NotBlank(message = "Campus is required")
    private String campus;

    @NotBlank(message = "Course is required")
    private String course;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotBlank(message = "Origin information is required")
    private String howDidYouHear;

    private String previousExperience;

    private String message;

    private String whatsapp;

    public Registration toRegistration() {
        Registration registration = new Registration();
        registration.setName(this.fullName);
        registration.setEmail(this.email);
        registration.setUniversity(this.university);
        registration.setCampus(this.campus);
        registration.setCourse(this.course);
        registration.setSemester(this.semester);
        registration.setHowDidYouHear(this.howDidYouHear);
        registration.setPreviousExperience(this.previousExperience);
        registration.setMessage(this.message);
        registration.setWhatsapp(this.whatsapp);
        return registration;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getHowDidYouHear() {
        return howDidYouHear;
    }

    public void setHowDidYouHear(String howDidYouHear) {
        this.howDidYouHear = howDidYouHear;
    }

    public String getPreviousExperience() {
        return previousExperience;
    }

    public void setPreviousExperience(String previousExperience) {
        this.previousExperience = previousExperience;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }
}
