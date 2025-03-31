package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user_info")
@Data  //Lombokでgetter/setterの記載を省略
public class UserInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	private Long userId;      //自動採番のUID
	
	/*
	 * @Columnアノテーション
	 * 	=> フィールドと実際のDBのカラム名を対応付ける
	 * 	   制約についても指定する
	 */
	@Column(name = "username", nullable = false, length = 10)
	private String userName;
	
	@Column(nullable = false)
	private String password;
	
}
