package br.unioeste.mu.mu_backend.contest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContestTeamRepository extends JpaRepository<ContestTeam, Long> {

    boolean existsByContestIdAndTeamNameIgnoreCase(Long contestId, String teamName);

    @Query("""
            SELECT DISTINCT t
            FROM ContestTeam t
            LEFT JOIN FETCH t.members
            WHERE t.contest.id = :contestId
            ORDER BY t.createdAt ASC
            """)
    List<ContestTeam> findAllByContestIdWithMembersOrderByCreatedAtAsc(@Param("contestId") Long contestId);
}
