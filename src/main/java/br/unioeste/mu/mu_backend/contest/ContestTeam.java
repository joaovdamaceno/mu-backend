package br.unioeste.mu.mu_backend.contest;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contest_teams")
public class ContestTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @Column(name = "team_name", nullable = false, length = 120)
    private String teamName;

    @Column(name = "coach_name", length = 150)
    private String coachName;

    @Column(length = 150)
    private String institution;

    @Column(name = "reserve_name", length = 120)
    private String reserveName;

    @Column(name = "is_cafe_com_leite", nullable = false)
    private boolean cafeComLeite;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("memberIndex ASC")
    private List<ContestTeamMember> members = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ContestTeamMember> getMembers() {
        return members;
    }

    public void addMember(ContestTeamMember member) {
        member.setTeam(this);
        this.members.add(member);
    }
}
