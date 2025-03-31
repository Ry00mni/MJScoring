package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
			@RequestParam String gameType,
			@RequestParam int startingPoints,
			HttpSession session,
			Model model) {
		
		/* セッションにユーザIDがない(未ログイン)場合、ログイン画面に遷移 */
		Long userId = (Long) session.getAttribute("userId");
		System.out.println("セッションの userId: "  + userId);
		if (userId == null) {
			return "redirect:/login";
		}
		
		/* matches_4pテーブルに記録する情報を取得 */
		Match match = new Match();
		
		match.setUserId(userId);
		match.setGameType(gameType);
		match.setStartingPoints(startingPoints);
		
		// saveで
		matchRepository.save(match);
		
		// 対局IDを取得し、セッションに保存
		session.setAttribute("matchId", match.getMatchId());
		
		// 対局画面へ
		return "redirect:/game";
		
	}
	
	@GetMapping("/game")
	public String showGameScreen(HttpSession session, Model model) {
	    Long userId = (Long) session.getAttribute("userId");
	    
	    System.out.println("userId:" + userId);
	    if (userId == null) {
	        return "redirect:/login";
	    }

	    Long matchId = (Long) session.getAttribute("matchId");
	    System.out.println("matchId:" + matchId);
	    if (matchId == null) {
	        return "redirect:/home";
	    }
	    
	    Match match = matchRepository.findById(matchId).orElse(null);
	    if (match == null) {
	    	return "redirect:/home";
		}
	    
	    // 直近の局情報を取得
	    List<MatchLog> matchLogs = matchLogRepository.findByMatchId(match);
	    MatchLog latestMatchLog = matchLogs.isEmpty() ?  null : matchLogs.get(matchLogs.size() -1 );
	    
	    // 局数取得
	    Integer roundNumber = (latestMatchLog != null) ? latestMatchLog.getRoundNumber() : 1; //初期値1 = 東一局
	    
	    // 場風取得
	    String prevailingWind = (latestMatchLog != null) ? latestMatchLog.getPrevailingWind() : "東"; //ヌルポ回避の為適当な場風
	    
	    // **追加: 現在のスコア**
	    int displayScore = (latestMatchLog == null) ? match.getStartingPoints() : latestMatchLog.getRoundScore();
	    System.out.println("表示スコアは: " + displayScore); // デバッグ用
	    model.addAttribute("displayScore", displayScore); 
	    
	    model.addAttribute("match", match);
	    model.addAttribute("roundNumber", roundNumber);
	    model.addAttribute("prevailingWind", prevailingWind);

        return "game"; // game.html に遷移
	}	
	
	@PostMapping("/game/updateScore")
	public String updateScore(
//			@RequestParam int scoreChange,
			HttpSession session) {
		Long matchId = (Long) session.getAttribute("matchId");
		System.out.println("matchIdはこちら" + matchId);
		if (matchId == null) return "redirect:/home";
		
		Match match = matchRepository.findById(matchId).orElse(null);
		
		//デバッグ用
		System.out.println("matchIdのDB検索: " + matchId);
		System.out.println("match取得: " + match);
		
		if (match == null) return "redirect:/home";
		
		
		List<MatchLog> matchLogs = matchLogRepository.findByMatchId(match);
		
		// 最新のMatchlog取得、2回目以降は配列処理
		MatchLog latestMatchLog = null;
		if (!matchLogs.isEmpty()) {
		    latestMatchLog = matchLogs.get(matchLogs.size() - 1);
		}
		
		
		int scoreChange = 3900;	//デバッグ用
		
//		// 初回の持ち点を取得
//		int previousScore = (latestMatchLog == null) ? match.getStartingPoints() : latestMatchLog.getRoundScore();
//
//		// 新しい MatchLog の作成
//		MatchLog newMatchLog = new MatchLog();
//		newMatchLog.setRoundScore(previousScore + scoreChange);

	    // 新しい局情報を作成
	    MatchLog newMatchLog = new MatchLog();
	    newMatchLog.setMatchId(match);  // match_idをセット
	    newMatchLog.setRoundNumber((latestMatchLog != null) ? latestMatchLog.getRoundNumber() + 1 : 1); // 局数+1
	    newMatchLog.setSeatWind("東"); // 仮の自風 (後で変更可)
	    newMatchLog.setPrevailingWind("東"); //TODO 後で自風を変更するメソッドを作成
	    newMatchLog.setScoreChange(scoreChange);
	    newMatchLog.setIsHouju(false);
	    newMatchLog.setIsWinner(false);
	    newMatchLog.setHan(0);
	    newMatchLog.setFu(0);
	    newMatchLog.setTsumo(false);

	    // スコア計算（初回: starting_pointsに加算, 2回目以降: 直前のround_scoreに加算）
	    if (latestMatchLog == null) {
	        newMatchLog.setRoundScore(match.getStartingPoints() + scoreChange);
	    } else {
	        newMatchLog.setRoundScore(latestMatchLog.getRoundScore() + scoreChange);
	    }
	    

	    // 保存
	    matchLogRepository.save(newMatchLog);
	    
	    // 画面表示用のスコア
// 		int displayScore = newMatchLog.getRoundScore();
 
 		
	    // ゲーム画面へリダイレクト
	    return "redirect:/game";

	}
	

}
