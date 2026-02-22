package br.unioeste.mu.mu_backend.registration;

import java.time.LocalDateTime;

public record RegistrationResponse(
        Long id,
        String name,
        String email,
        String whatsapp,
        String institution,
        String campus,
        String course,
        String semester,
        String howDidYouHear,
        String previousExperience,
        String message,
        LocalDateTime createdAt
) {
    public static RegistrationResponse from(Registration registration) {
        return new RegistrationResponse(
                registration.getId(),
                registration.getName(),
                registration.getEmail(),
                registration.getWhatsapp(),
                registration.getInstitution(),
                registration.getCampus(),
                registration.getCourse(),
                registration.getSemester(),
                registration.getHowDidYouHear(),
                registration.getPreviousExperience(),
                registration.getMessage(),
                registration.getCreatedAt()
        );
    }
}
