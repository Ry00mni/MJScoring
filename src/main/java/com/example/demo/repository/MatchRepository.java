package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Match;


@Repository
public interface MatchRepository extends JpaRepository<Match, Long> { 
    // findByMatchId ではなく findById を使う
    Optional<Match> findById(Long matchId);
}
