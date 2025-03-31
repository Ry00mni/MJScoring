package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "matches_4p")
@Data
public class Match {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "match_id")  // テーブルのカラム名を明示的に指定
    private Long matchId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "game_type")
    private String gameType;

    @Column(name = "starting_points")
    private Integer startingPoints;

    @Column(name = "ranking")
    private Integer ranking;

    @Column(name = "final_score")
    private Integer finalScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
}
