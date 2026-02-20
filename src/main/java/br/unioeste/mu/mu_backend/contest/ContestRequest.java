package br.unioeste.mu.mu_backend.contest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class ContestRequest {

    @NotBlank(message = "Nome do contest é obrigatório")
    private String name;

    @NotNull(message = "Duração é obrigatória")
    @Positive(message = "Duração deve ser maior que zero")
    private Integer durationMinutes;

    @NotNull(message = "Data e horário de início são obrigatórios")
    private LocalDateTime startDateTime;

    private boolean teamBased;

    private String codeforcesMirrorUrl;

    public void applyTo(Contest contest) {
        contest.setName(name);
        contest.setDurationMinutes(durationMinutes);
        contest.setStartDateTime(startDateTime);
        contest.setTeamBased(teamBased);
        contest.setCodeforcesMirrorUrl(codeforcesMirrorUrl);
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
