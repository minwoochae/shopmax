package com.shopmax.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shopmax.Dto.MemberFormDto;
import com.shopmax.entity.Member;
import com.shopmax.service.CartService;
import com.shopmax.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Controller 
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberservice;
	private final CartService cartService;
	private final PasswordEncoder passwordEncoder;
	//문의하기
	@GetMapping(value = "/members/qa")
	public String qa() {
		return "member/qa";
	}
	//로그인 화면
	@GetMapping(value = "/members/login")
	public String loginmember() {
		return "member/memberLoginForm";
	}
	
	//회원가입 화면
	@GetMapping(value = "/members/new")
	public String memberForme(Model model) {
		model.addAttribute("memberFormDto", new MemberFormDto());
		return "member/memberForm";
	}
	
	//회원가입
	@PostMapping(value = "/members/new")
	public String memberForme(@Valid MemberFormDto memberFormDto,
			BindingResult bindingResult, Model model) {
		//@valid: 유효성을 검증하려는 객체 앞에 붙인다.
		//BindingResult: 유효성 검증 후의 결과가 들어있다.
		
		if(bindingResult.hasErrors()) {
			//에러가 있다면 회원가입 페이지로 이동
			return "member/memberForm";
		}
		
		try {
			//MemberFormDto -> Member Entity, 비밀번호 암호화
			Member member = Member.createMember(memberFormDto, passwordEncoder);
			memberservice.saveMember(member);
		} catch (IllegalStateException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "member/memberForm";
		}
		
		return "redirect:/";
	}
	
	//마이페이지
		@GetMapping(value = "/member/mypage")
		public String mainMypage( Model model, Principal principal) {

			String members = principal.getName();
			
			Member member = memberservice.getMember(members);
			System.out.println(member);
			
			model.addAttribute("member", member);
			
			
			if(principal != null) {
			//카운트
			Member mb = memberservice.getMember(principal.getName());
			Long Count = cartService.cartCount(mb);
		    // 모델에 상품 수를 추가합니다
		    model.addAttribute("Count", Count);
			} 
			return "member/MyPage";
		}
		
		
		//내 정보 
		@GetMapping(value = "/member/MyInfo")
		public String mainMyInpo( Model model, Principal principal) {

			String members = principal.getName();
			
			Member member = memberservice.getMember(members);
			System.out.println(member);
			
			model.addAttribute("member", member);
			
			
			if(principal != null) {
			//카운트
			Member mb = memberservice.getMember(principal.getName());
			Long Count = cartService.cartCount(mb);
		    // 모델에 상품 수를 추가합니다
		    model.addAttribute("Count", Count);
			} 
			return "member/MyInfo";
		}
		
		
		
		//내 정보 수정 
		@GetMapping(value = "/member/MyInformation")
		public String mainMyInformation( Model model, Principal principal) {

			String members = principal.getName();
			
			Member member = memberservice.getMember(members);
			
			model.addAttribute("member", member);
			
			
			if(principal != null) {
			//카운트
			Member mb = memberservice.getMember(principal.getName());
			Long Count = cartService.cartCount(mb);
		    // 모델에 상품 수를 추가합니다
		    model.addAttribute("Count", Count);
			} 
			return "member/MyInformation";
		}
		
		// 카카오 이용자는 내 정보 수정이 불가능하여서 넣지 않아서 Authentication를 넣지 않음
		@PostMapping("/member/MyInformation")
		public String mypageupdate(@Valid String name, @Valid String address, Model model, Principal principal) {
			String members = principal.getName();
			
			Member member = memberservice.getMember(members);
			memberservice.updateNameAddress(member.getEmail(), name, address);
			
			return "redirect:/";
		}
		
		
		// 탈퇴하기
		@DeleteMapping(value = "/member/{memberId}/delete")
		public @ResponseBody ResponseEntity deleteMember(@RequestBody @PathVariable("memberId") Long memberId,
				Principal principal) {

			memberservice.deleteMember(memberId);

			return new ResponseEntity<Long>(memberId, HttpStatus.OK);
		}
	//로그인 실패했을때
	@GetMapping(value="/members/login/error")
	public String loginError(Model model) {
		model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
		return "member/memberLoginForm";
	}
	
	
	
}
