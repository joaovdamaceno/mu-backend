package br.unioeste.mu.mu_backend.contest;

import java.time.LocalDateTime;

public class ContestTeamResponse {

    private final Long id;
    private final Long contestId;
    private final String teamName;
    private final String coachName;
    private final String institution;
    private final String competitor1Name;
    private final String competitor2Name;
    private final String competitor3Name;
    private final String reserveName;
    private final boolean cafeComLeite;
    private final LocalDateTime createdAt;

    private ContestTeamResponse(ContestTeam team) {
        this.id = team.getId();
        this.contestId = team.getContest().getId();
        this.teamName = team.getTeamName();
        this.coachName = team.getCoachName();
        this.institution = team.getInstitution();
        this.competitor1Name = getMemberName(team, 1);
        this.competitor2Name = getMemberName(team, 2);
        this.competitor3Name = getMemberName(team, 3);
        this.reserveName = team.getReserveName();
        this.cafeComLeite = team.isCafeComLeite();
        this.createdAt = team.getCreatedAt();
    }

    public static ContestTeamResponse from(ContestTeam team) {
        return new ContestTeamResponse(team);
    }

    private static String getMemberName(ContestTeam team, int position) {
        return team.getMembers().stream()
                .filter(member -> member.getMemberIndex() == position)
                .map(ContestTeamMember::getMemberName)
                .findFirst()
                .orElse(null);
    }

    public Long getId() {
        return id;
    }

    public Long getContestId() {
        return contestId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getCoachName() {
        return coachName;
    }

    public String getInstitution() {
        return institution;
    }

    public String getCompetitor1Name() {
        return competitor1Name;
    }

    public String getCompetitor2Name() {
        return competitor2Name;
    }

    public String getCompetitor3Name() {
        return competitor3Name;
    }

    public String getReserveName() {
        return reserveName;
    }

    public boolean isCafeComLeite() {
        return cafeComLeite;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
