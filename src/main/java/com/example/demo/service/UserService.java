package com.example.demo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.UserInfo;
import com.example.demo.repository.UserInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserInfoRepository userRepository;
	
	public void registerUser(String userName, String password) {
		UserInfo user = new UserInfo();
		user.setUserName(userName);
		user.setPassword(password);
		userRepository.save(user);
	}
	
	// ユーザ認証機能
	public UserInfo authenticate(String userName, String pass) {
		// ユーザ名をキーとしてDBからユーザ情報を取得
		Optional<UserInfo> userOpt = userRepository.findByUserName(userName);
		
		// ユーザが存在する、かつパスワードが一致する場合のみ認証成功
//		boolean b = userOpt.isPresent() && userOpt.get().getPassword().equals(pass);
//		System.out.println(b);
		if (userOpt.isPresent() && userOpt.get().getPassword().equals(pass)) {
			return userOpt.get();	
		}
		return null;
	}

}
