package br.unioeste.mu.mu_backend.contest;

import jakarta.persistence.*;

@Entity
@Table(name = "contest_team_members",
        uniqueConstraints = @UniqueConstraint(name = "uk_contest_team_member_position", columnNames = {"team_id", "member_index"}))
public class ContestTeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private ContestTeam team;

    @Column(name = "member_index", nullable = false)
    private Integer memberIndex;

    @Column(name = "member_name", nullable = false, length = 120)
    private String memberName;

    public Long getId() {
        return id;
    }

    public ContestTeam getTeam() {
        return team;
    }

    public void setTeam(ContestTeam team) {
        this.team = team;
    }

    public Integer getMemberIndex() {
        return memberIndex;
    }

    public void setMemberIndex(Integer memberIndex) {
        this.memberIndex = memberIndex;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
