package br.unioeste.mu.mu_backend.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(value = {"id", "createdAt"}, allowGetters = true)
public class RegistrationRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    private String email;

    @Size(max = 50, message = "Whatsapp deve ter no máximo 50 caracteres")
    private String whatsapp;

    @Size(max = 150, message = "Instituição deve ter no máximo 150 caracteres")
    private String institution;

    @NotBlank(message = "Campus é obrigatório")
    @Size(max = 150, message = "Campus deve ter no máximo 150 caracteres")
    private String campus;

    @NotBlank(message = "Curso é obrigatório")
    @Size(max = 150, message = "Curso deve ter no máximo 150 caracteres")
    private String course;

    @NotBlank(message = "Semestre é obrigatório")
    @Size(max = 50, message = "Semestre deve ter no máximo 50 caracteres")
    private String semester;

    @NotBlank(message = "Como conheceu é obrigatório")
    @Size(max = 255, message = "Como conheceu deve ter no máximo 255 caracteres")
    private String howDidYouHear;

    private String previousExperience;

    private String message;

    public Registration toRegistration() {
        Registration registration = new Registration();
        applyTo(registration);
        return registration;
    }

    public void applyTo(Registration registration) {
        registration.setName(normalize(name));
        registration.setEmail(normalize(email));
        registration.setWhatsapp(normalizeNullable(whatsapp));
        registration.setInstitution(normalizeNullable(institution));
        registration.setCampus(normalize(campus));
        registration.setCourse(normalize(course));
        registration.setSemester(normalize(semester));
        registration.setHowDidYouHear(normalize(howDidYouHear));
        registration.setPreviousExperience(normalizeNullable(previousExperience));
        registration.setMessage(normalizeNullable(message));
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
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
}
