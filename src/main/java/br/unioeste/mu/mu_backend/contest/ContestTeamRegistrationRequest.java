package br.unioeste.mu.mu_backend.contest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContestTeamRegistrationRequest {

    @NotBlank(message = "Nome do time é obrigatório")
    @Size(max = 120, message = "Nome do time deve ter no máximo 120 caracteres")
    private String teamName;

    @Size(max = 150, message = "Nome do coach deve ter no máximo 150 caracteres")
    private String coachName;

    @Size(max = 150, message = "Instituição deve ter no máximo 150 caracteres")
    private String institution;

    @NotBlank(message = "Competidor 1 é obrigatório")
    @Size(max = 120, message = "Nome do competidor 1 deve ter no máximo 120 caracteres")
    private String competitor1Name;

    @Size(max = 120, message = "Nome do competidor 2 deve ter no máximo 120 caracteres")
    private String competitor2Name;

    @Size(max = 120, message = "Nome do competidor 3 deve ter no máximo 120 caracteres")
    private String competitor3Name;

    @Size(max = 120, message = "Nome da reserva deve ter no máximo 120 caracteres")
    private String reserveName;

    private boolean cafeComLeite;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getCompetitor1Name() {
        return competitor1Name;
    }

    public void setCompetitor1Name(String competitor1Name) {
        this.competitor1Name = competitor1Name;
    }

    public String getCompetitor2Name() {
        return competitor2Name;
    }

    public void setCompetitor2Name(String competitor2Name) {
        this.competitor2Name = competitor2Name;
    }

    public String getCompetitor3Name() {
        return competitor3Name;
    }

    public void setCompetitor3Name(String competitor3Name) {
        this.competitor3Name = competitor3Name;
    }

    public String getReserveName() {
        return reserveName;
    }

    public void setReserveName(String reserveName) {
        this.reserveName = reserveName;
    }

    public boolean isCafeComLeite() {
        return cafeComLeite;
    }

    public void setCafeComLeite(boolean cafeComLeite) {
        this.cafeComLeite = cafeComLeite;
    }
}
