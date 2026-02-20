package br.unioeste.mu.mu_backend.contest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestTeamRepository extends JpaRepository<ContestTeam, Long> {

    List<ContestTeam> findByContestIdOrderByCreatedAtAsc(Long contestId);
}
