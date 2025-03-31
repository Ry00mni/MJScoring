package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "match_logs")
@Data
public class MatchLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "log_id")
	private Long logId;		//対局ごとの自動採番ID
	
	@ManyToOne
	@JoinColumn(name = "match_id")
	private Match matchId;	//matches_4pテーブルのmatch_idと紐づけ
	
	@Column(name = "round_number")
	private Integer roundNumber;  // 何局目か(東一局=1,東二局=2,…)
	
	@Column(name = "score_change")
	private Integer	scoreChange;
	
	@Column(name = "seat_wind")
	private String seatWind;
	
	@Column(name = "prevailing_wind")
	private String prevailingWind;  // 場風(game-settingsで設定)
	
    @Column(name = "is_houju")
    private Boolean isHouju;  // 放銃（boolean）

    @Column(name = "is_winner")
    private Boolean isWinner; // 和了したかどうか（boolean）

    @Column(name = "han")
    private Integer han; // 翻数

    @Column(name = "fu")
    private Integer fu;  // 符数

    @Column(name = "tsumo")
    private Boolean tsumo;  // ツモかロンか（boolean）
	
    @Column(name = "round_score")
    private Integer roundScore;
}
