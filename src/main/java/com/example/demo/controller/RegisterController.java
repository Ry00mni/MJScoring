package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegisterController {

	private final UserService userService;

	// ユーザ登録画面
	@GetMapping("/register")
	public String showRegisterPage() {
		return "register";
	}

	// 確認画面 (新規登録時)
	@PostMapping("/register/confirm")
	public String confirmRegisterPost(
			@RequestParam("user_name") String userName,
			@RequestParam("password") String pass,
			Model model) {
		model.addAttribute("user_name", userName);
		model.addAttribute("password", pass);

		return "confirm";
	}

	// 確認画面 (既存情報変更時)
	@GetMapping("/register/confirm")
	public String confirmRegisterGet(
			@RequestParam(value = "user_name", required = false) String userName,
			@RequestParam(value = "password", required = false) String pass,
			Model model) {
		model.addAttribute("user_name", userName);
		model.addAttribute("password", pass);
		return "confirm";
	}

	// 完了画面（登録処理の後）
	@PostMapping("/register/complete")
	public String completeRegister(
			@RequestParam("user_name") String userName,
			@RequestParam("password") String password,
			Model model) {

		userService.registerUser(userName, password);
		
		//ユーザ名をモデルに追加
		model.addAttribute("user_name", userName);
		
		return "complete"; // complete.html を表示
	}

}
