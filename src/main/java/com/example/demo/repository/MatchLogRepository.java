package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Match;
import com.example.demo.entity.MatchLog;

@Repository
public interface MatchLogRepository extends JpaRepository<MatchLog, Long>{
	List<MatchLog> findByMatchId(Match match);
}


