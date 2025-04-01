package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.MahjongScoreCalculator;
import com.example.demo.entity.Match;
import com.example.demo.entity.MatchLog;
import com.example.demo.repository.MatchLogRepository;
import com.example.demo.repository.MatchRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GameController {
	
	private final MatchRepository matchRepository;
	private final MatchLogRepository matchLogRepository;
	
	/**
	 * ユーザhome画面から対局設定画面へ遷移
	 */
	@GetMapping("/game-settings")
	public String showGameSettings() {
		return "game-settings";
	}
	
	/*
	 * game-settingsからのフォーム情報取得
	 * game画面へのリダイレクト処理
	 */
	@PostMapping("/game")
	public String startGame(
			@RequestParam String gameType, 		// 東風 or 半荘
			@RequestParam int startingPoints, 	// 初期持ち点
			@RequestParam String ownWind,		// ゲーム開始時の自風
			HttpSession session,
			Model model) {
		
		/* セッションにユーザIDがない(未ログイン)場合、ログイン画面に遷移 */
		Long userId = (Long) session.getAttribute("userId");
			// DEBUG
			System.out.println("セッションの userId: "  + userId);
			
		if (userId == null) {
			return "redirect:/login";
		}
		
		/* matches_4pテーブルに記録する情報を取得 */
		Match match = new Match();
		match.setUserId(userId);					// ユーザID
		match.setGameType(gameType);				// 東風 or 半荘
		match.setStartingPoints(startingPoints);	// 初期持ち点
		// 上記の情報をmatches_4pテーブルに新規レコードとして格納
		matchRepository.save(match);
			// DEBUG	
			System.out.println("[DEBUG] ownWind: " + ownWind);
		
		/** 
		 * セッションにmatch_idと自風情報を格納
		 */
		session.setAttribute("ownWind", ownWind);
		session.setAttribute("matchId", match.getMatchId());
			// DEBUG
			System.out.println("[DEBUG] セッションにセットされた自風: " + session.getAttribute("ownWind"));
		
		// 対局画面へ
		return "redirect:/game";
		
	}
	
	/*
	 * 【機能】読み込まれる度に以下の値を取得、modelへの追加し画面に表示する。
	 * ・displayScore 	: 現在の持ち点を対局画面に表示する
	 * ・displayRound 	: 東1局,東2局表示用変数
	 * 	 └・roundNumber	: 第何局か
	 *   └・gameType		: 東風/半荘の設定
	 * ・match		  	: 設定画面で採番されるmatch_idをreference keyとしてJOINするため
	 * ・ownWind			: 自風
	 */
	@GetMapping("/game")
	public String showGameScreen(
			HttpSession session,
			Model model) {
		
		/* セッションにユーザIDがない(未ログイン)場合、ログイン画面に遷移 */
	    Long userId = (Long) session.getAttribute("userId");
	    	// DEBUG
	    	System.out.println("userId:" + userId);
	    if (userId == null) return "redirect:/login";
	    
	    /* セッションにmatch_idがない(設定画面を経由していない)場合、ホーム画面に遷移 */
	    Long matchId = (Long) session.getAttribute("matchId");
	    	// DEBUG
	    	System.out.println("matchId:" + matchId);
	    if (matchId == null) return "redirect:/home";
	    
	    // match_id情報がなければホーム画面に遷移
	    Match match = matchRepository.findById(matchId).orElse(null);
	    if (match == null) return "redirect:/home";
	    
	    /*
	     * matchLogs内に、findByMatchId(match)でmatchIdに紐づく対局があるか調査する。
	     * ・ヒット件数が0 => 直近の対局情報を格納するlatestMatchLogオブジェクトをnullで生成
	     * ・それ以外     => 一つ前の対局情報レコードを取得する 
	     */
	    List<MatchLog> matchLogs = matchLogRepository.findByMatchId(match);
	    MatchLog latestMatchLog = matchLogs.isEmpty() ?  null : matchLogs.get(matchLogs.size() - 1 );
	    
	    /*
	     * 初回アクセス時はmatch_logsテーブルに、matches_4pテーブルで採番されたmatch_idが含まれていない
	     * = findByMatchIdのヒット数が0件となり、配列matchLogsは空となる
	     * 空の場合の処理は以下の通り
	     * 【roundNumber】: 何局目か(東1局) → デフォルト値1を採用
	     * 【ownWind】    : 自風			 → game-settings画面で設定した情報をセッションから取得
	     */
	    Integer roundNumber;
	    String ownWind;
	    
	    if (latestMatchLog != null) {
	    	roundNumber = latestMatchLog.getRoundNumber() + 1;
	    	ownWind = latestMatchLog.getOwnWind();
		} else {
			roundNumber = 1;
			ownWind = (String) session.getAttribute("ownWind");
		}
	    	//DEBUG
	    	System.out.println("[DEBUG]セッションから取得したownWind: " + ownWind);		
	    
	    // game_typeを取得
	    String gameType = match.getGameType();
	    	//DEBUG
	    	System.out.println("[DEBUG]gameType: " + gameType);
	    	
	    /* 対局画面で表示する局数を生成 */
	    String displayRound = getDisplayRound(roundNumber, gameType);
	    	//DEBUG
	    	System.out.println("[DEBUG]displayRound: " + displayRound);
	    
	    // 途中経過スコア取得
	    int displayScore = (latestMatchLog == null) ? match.getStartingPoints() : latestMatchLog.getRoundScore();
	    	//DEBUG
	    	System.out.println("[DEBUG]displayScore: " + displayScore);
	    
	    // calculateScoreメソッドで、最新のmatch_logの値を使うためセッションに格納
        if (latestMatchLog != null) {
            session.setAttribute("latestMatchLog", latestMatchLog); // セッションに保存
        }
    	// modelに追加、thymeleafで取り出し可能にする
	    model.addAttribute("displayScore", displayScore); 
	    model.addAttribute("displayRound", displayRound);
	    model.addAttribute("match", match);
	    model.addAttribute("roundNumber", roundNumber);
	    model.addAttribute("ownWind", ownWind);
	    model.addAttribute("gameType", gameType);
	   
        return "game"; // game.html に遷移
	}	
	
	@PostMapping("/game/updateScore")
	public String updateScore(
			@RequestParam (required = false) int scoreChange,
			@RequestParam int han,
			@RequestParam int fu,
			@RequestParam Boolean tsumo,
			HttpSession session) {
    		// DEBUG
			System.out.println("### updateScore DEBUG  STARTED ###");
		//TODO GETと同じ処理しているので、メソッドに切り出して良さそう
		Long matchId = (Long) session.getAttribute("matchId");
		if (matchId == null) return "redirect:/home";
		
		Match match = matchRepository.findById(matchId).orElse(null);
		if (match == null) return "redirect:/home";
		
	    /*
	     * matchLogs内に、findByMatchId(match)でmatchIdに紐づく対局があるか調査する。
	     * ・ヒット件数が0 => 直近の対局情報を格納するlatestMatchLogオブジェクトをnullで生成
	     * ・それ以外     => 一つ前の対局情報レコードを取得する 
	     */
	    List<MatchLog> matchLogs = matchLogRepository.findByMatchId(match);
	    MatchLog latestMatchLog = matchLogs.isEmpty() ?  null : matchLogs.get(matchLogs.size() - 1 );
	    
	    /*
	     * 初回アクセス時はmatch_logsテーブルに、matches_4pテーブルで採番されたmatch_idが含まれていない
	     * = findByMatchIdのヒット数が0件となり、配列matchLogsは空となる
	     * 空の場合の処理は以下の通り
	     * 【roundNumber】: 何局目か(東1局) → デフォルト値1を採用
	     * 【ownWind】    : 自風			 → game-settings画面で設定した情報をセッションから取得
	     */
	    Integer roundNumber;
	    String ownWind;
	    
	    if (latestMatchLog != null) {
	    	roundNumber = latestMatchLog.getRoundNumber() + 1;
	    	ownWind = latestMatchLog.getOwnWind();
		} else {
			roundNumber = 1;
			ownWind = (String) session.getAttribute("ownWind");
		}
	    //TODO メソッドとしてまとめるならここまで？
	    
	    
	    /* 次の自風取得 */	
	    String newOwnWind = getNextWind(ownWind);  
	    	// DEBUG
	    	System.out.println("[DEBUG] 次の局の自風: " + newOwnWind);

	    // 1局終了時の持ち点計算
	    int newRoundScore = (latestMatchLog == null) 
	            ? match.getStartingPoints() + scoreChange 
	            : latestMatchLog.getRoundScore() + scoreChange;
	    	// DEBUG
	    	System.out.println("[DEBUG]roundScore: " + newRoundScore);
	    	System.out.println("### updateScore DEBUG  ENDED ###");	    
	    /**
	     * 新規match_logsのレコード作成
	     * 今回の対局データをINSERT
	     */
	    MatchLog newMatchLog = new MatchLog();
	    newMatchLog.setMatchId(match);  
	    newMatchLog.setRoundNumber(roundNumber);
	    newMatchLog.setOwnWind(newOwnWind);
	    newMatchLog.setRoundScore(newRoundScore);
	    newMatchLog.setScoreChange(scoreChange);
	    //TODO 放銃/和了判定の真偽値をgame.htmlのフォームから受け取る処理作成
	    newMatchLog.setIsHouju(false); 
	    newMatchLog.setIsWinner(false);
	    newMatchLog.setHan(han);
	    newMatchLog.setFu(fu);
	    newMatchLog.setTsumo(tsumo);

	    // match_logsテーブルに保存
	    matchLogRepository.save(newMatchLog);
 		
	    // ゲーム画面へリダイレクト
	    return "redirect:/game";

	}
	
	/*
	 * game.html画面で【計算】ボタン押下時、入力された翻数、符数、ツモ/ロンの真偽値を受け取り
	 * 計算結果を画面にリアルタイムに出力
	 */
	@PostMapping("/calculate-score")
	@ResponseBody
	public int calculateScore(
	    @RequestParam int han,
	    @RequestParam int fu,
	    @RequestParam boolean tsumo,
	    @RequestParam(required = false) boolean isHouju,
	    HttpSession session) {
		
		MatchLog latestMatchLog = (MatchLog) session.getAttribute("latestMatchLog");
	    String ownWind = (latestMatchLog != null) ? latestMatchLog.getOwnWind() : (String) session.getAttribute("ownWind");
	    boolean isParent = ownWind.equals("東"); // 東なら親
	    	// DEBUG
	    	System.out.println("### calculateScore DEBUG  STARTED ###");
	    	System.out.println("[DEBUG]ownWind: "+ ownWind);
	    	System.out.println("[DEBUG]han: " + han );
	    	System.out.println("[DEBUG]fu: " + fu);
	    	System.out.println("[DEBUG]isParent: " + isParent);
	    	System.out.println("[DEBUG] isHouju: " + isHouju);
	    
	    int score = MahjongScoreCalculator.calculateScore(han, fu, tsumo, isParent);
	    
	    // 放銃であれば負の数に変換
	    if (isHouju) {
	        score = -score;
	    }
	    	// DEBUG
	    	System.out.println("[DEBUG] 計算結果: " + score);
	    	System.out.println("### calculateScore DEBUG  ENDED ###");
	    return score;
	}
	
	/* 対局画面で表示する局数を生成するメソッド */
	private String getDisplayRound(int roundNumber, String gameType) {
		String windOfRound = "東";
		
		if (gameType.equals("半荘") && roundNumber > 4) {
			windOfRound = "南";
			roundNumber = roundNumber - 4;
		}
		
		return windOfRound + roundNumber + "局";
	}
	
	/* 東 → 南 → 西　→　北の風移行メソッド */
	private String getNextWind(String currentWind) {
		switch (currentWind) {
		case "東": return "南";
		case "南": return "西";
		case "西": return "北";
		default : return "東"; // 北の次は東
		}
	}
}
