package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.UserInfo;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class LoginController {
	
	private final UserService userService;
	
	// ログイン画面
	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}
	
	@PostMapping("/login")
	public String login(
			@RequestParam("username") String userName,
			@RequestParam("password") String pass,
			HttpSession session,
			Model model) {
		
		UserInfo user = userService.authenticate(userName, pass);
		if (user != null) {
			// ユーザ情報をセッションに保存
			session.setAttribute("loginUser", user);
			session.setAttribute("userId", user.getUserId());
			return "redirect:/home";
		}
		model.addAttribute("error","ユーザ名、またはパスワードに誤りがあります。");
		return "login";
		
	}
	

}
