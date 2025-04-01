package com.example.demo;

public class MahjongScoreCalculator {
    /**
     * 麻雀の点数を計算するメソッド
     * @param han 翻数
     * @param fu 符数
     * @param isTsumo ツモ(true)/ロン(false)
     * @param isParent 親(true)/子(false)
     * @return 計算されたスコア
     */
	    public static int calculateScore(int han, int fu, boolean tsumo, boolean isParent) {
	        int basePoints;

	        if (han >= 13) { // 数え役満
	            basePoints = 8000;
	        } else if (han >= 11) { // 三倍満
	            basePoints = 6000;
	        } else if (han >= 8) { // 倍満
	            basePoints = 4000;
	        } else if (han >= 6) { // 跳満
	            basePoints = 3000;
	        } else if (han >= 5 || (han >= 4 && fu >= 40) || (han >= 3 && fu >= 70)) { // 満貫
	            basePoints = 2000;
	        } else {
	            basePoints = (int) (fu * Math.pow(2, han + 2)); // 通常計算
	        }
	        	
        	int score;
	        if (isParent) {
	            score = tsumo ? basePoints * 6 : basePoints * 6; // 親のツモ/ロン
	        } else {
	            score = tsumo ? basePoints * 4 : basePoints * 4; // 子のツモ/ロン
	        }

	        // 最終結果を10の位で切り上げ
	        score = (int) (Math.ceil(score / 100.0) * 100);

	        return score;
	    }
}
