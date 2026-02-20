package br.unioeste.mu.mu_backend.contest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ContestRequest {

    @NotBlank(message = "Nome do contest é obrigatório")
    @Size(max = 200, message = "Nome do contest deve ter no máximo 200 caracteres")
    private String name;

    @NotNull(message = "Duração é obrigatória")
    @Positive(message = "Duração deve ser maior que zero")
    private Integer durationMinutes;

    @NotNull(message = "Data e horário de início são obrigatórios")
    private LocalDateTime startDateTime;

    private boolean teamBased;

    @Size(max = 2000, message = "URL do espelho Codeforces deve ter no máximo 2000 caracteres")
    private String codeforcesMirrorUrl;

    public void applyTo(Contest contest) {
        contest.setName(name == null ? null : name.trim());
        contest.setDurationMinutes(durationMinutes);
        contest.setStartDateTime(startDateTime);
        contest.setTeamBased(teamBased);
        contest.setCodeforcesMirrorUrl(normalizeNullable(codeforcesMirrorUrl));
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

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public boolean isTeamBased() {
        return teamBased;
    }

    public void setTeamBased(boolean teamBased) {
        this.teamBased = teamBased;
    }

    public String getCodeforcesMirrorUrl() {
        return codeforcesMirrorUrl;
    }

    public void setCodeforcesMirrorUrl(String codeforcesMirrorUrl) {
        this.codeforcesMirrorUrl = codeforcesMirrorUrl;
    }
}
