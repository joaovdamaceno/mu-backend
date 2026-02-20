package br.unioeste.mu.mu_backend.contest;

import java.time.LocalDateTime;

public class ContestResponse {

    private final Long id;
    private final String name;
    private final Integer durationMinutes;
    private final LocalDateTime startDateTime;
    private final boolean teamBased;
    private final String codeforcesMirrorUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private ContestResponse(Contest contest) {
        this.id = contest.getId();
        this.name = contest.getName();
        this.durationMinutes = contest.getDurationMinutes();
        this.startDateTime = contest.getStartDateTime();
        this.teamBased = contest.isTeamBased();
        this.codeforcesMirrorUrl = contest.getCodeforcesMirrorUrl();
        this.createdAt = contest.getCreatedAt();
        this.updatedAt = contest.getUpdatedAt();
    }

    public static ContestResponse from(Contest contest) {
        return new ContestResponse(contest);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public boolean isTeamBased() {
        return teamBased;
    }

    public String getCodeforcesMirrorUrl() {
        return codeforcesMirrorUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
