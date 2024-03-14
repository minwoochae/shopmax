package com.shopmax.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shopmax.Dto.MemberFormDto;
import com.shopmax.Dto.PasswordDto;
import com.shopmax.Dto.QaDto;
import com.shopmax.entity.Member;
import com.shopmax.entity.Qa;
import com.shopmax.service.CartService;
import com.shopmax.service.MemberService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;


@Controller 
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberservice;
	private final CartService cartService;
	private final PasswordEncoder passwordEncoder;
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
		
		@GetMapping("/member/checkPwd")
		@NotBlank
		public String checkPwdView(Model model , Principal principal) {
			model.addAttribute("passwordDto", new PasswordDto());
			//카운트
			Member mb = memberservice.getMember(principal.getName());
			Long Count = cartService.cartCount(mb);
		    // 모델에 상품 수를 추가합니다
		    model.addAttribute("Count", Count);
			
			return "member/checkPwd";
		}

		// 회원 수정 전 비밀번호 확인 
		@PostMapping(value = "/member/checkPwd")
		public String checkPwd(@Valid PasswordDto passwordDto, Principal principal, Model model) {

			  if (passwordDto.getPassword() == null || passwordDto.getPassword().trim().isEmpty()) {
			        model.addAttribute("errorMessage", "비밀번호 값이 입력되어 있지 않습니다..");
			        return "member/checkPwd";
			    }
			Member member = memberservice.findByEmail(principal.getName());

			boolean result = passwordEncoder.matches(passwordDto.getPassword(), member.getPassword());

			if (!result) {
				model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
				return "member/checkPwd";
			}

			return "member/EditMember";
		}
		
		// 내 비밀번호수정 (마이페이지에서)
		@GetMapping(value = "/member/EditMember")
		public String passwordupdate(Principal principal, Model model) {
			Member member = memberservice.findByEmail(principal.getName());
			model.addAttribute("member", member);
			return "member/EditMember";
		}

		@PostMapping("/member/EditMember")
		public String passwordupdate(@RequestParam String password, Model model, Principal principal, Member member) {
			Member members = memberservice.findByEmail(principal.getName());
			if(principal != null) {
				//카운트
				Member mb = memberservice.getMember(principal.getName());
				Long Count = cartService.cartCount(mb);
			    // 모델에 상품 수를 추가합니다
			    model.addAttribute("Count", Count);
				} 
			   if (password == null || password.trim().isEmpty()) {
			        model.addAttribute("errorMessage", "비밀번호 값이 없습니다.");
			        model.addAttribute("member", member);
			        return "member/EditMember";
			    }
			    
			
			if (passwordEncoder.matches(password, members.getPassword()) == true) {
				model.addAttribute("errorMessage", "기존 비밀번호와 같습니다.");
				model.addAttribute("member", member);
				return "member/EditMember";
			}
			
			else {
				memberservice.updatepassword(principal.getName(), passwordEncoder.encode(password), passwordEncoder);
				return "redirect:/member/mypage";
			}

		}
		//Q&A 리스트
		@GetMapping(value = { "/qa/list", "/qa/list/{page}" })
		public String memberManage(@PathVariable("page") Optional<Integer> page, Model model) {
			Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

			Page<Qa> qa = memberservice.getqaPage(pageable);
			System.out.println(qa);
			model.addAttribute("qa", qa);
			model.addAttribute("maxPage", 5);

			return "member/QaList";
		}
		
		//문의하기
		@GetMapping(value = "/members/qa")
		public String qa(Model model, Principal principal) {
		/*	String members = principal.getName();
			Member member = memberservice.getMember(members);
			model.addAttribute("member", member);
		*/		
			model.addAttribute("qaDto", new QaDto());
			return "member/qa";
		}
		
		@PostMapping(value = "/members/qa")
		public String postqa(@Valid QaDto qadto,
				BindingResult bindingResult, Model model, Principal principal) {
			//@valid: 유효성을 검증하려는 객체 앞에 붙인다.
			//BindingResult: 유효성 검증 후의 결과가 들어있다.
			
			if(bindingResult.hasErrors()) {
				return "qa/list";
			}
			
			try {
				String members = principal.getName();
				Member member = memberservice.getMember(members);
				
				Qa qa = Qa.createQa(qadto, member);
				System.out.println(qa);
				memberservice.saveQa(qa);
			} catch (IllegalStateException e) {
				model.addAttribute("errorMessage", e.getMessage());
				return "qa/list";
			}
			
			return "redirect:/";
		}
		@GetMapping(value = "/qa/lists/{qaId}")
		public String qaListNo(@PathVariable("qaId") Long qaId, Model model, Principal principal) {
		    QaDto qadtl = memberservice.getqaDtl(qaId);
		    
		    model.addAttribute("qa", qadtl);
		    Optional<Qa> qaa = memberservice.getQaById(qaId);
		    System.out.println(qaa);
		    model.addAttribute("member", qaa);
		    
		    
		    if (principal != null) {
		        // 카운트
		        Member mb = memberservice.getMember(principal.getName());
		        Long Count = cartService.cartCount(mb);
		        // 모델에 상품 수를 추가합니다
		        model.addAttribute("Count", Count);
		    }
		    return "member/qaDtl";
		}
		
		


	//로그인 실패했을때
	@GetMapping(value="/members/login/error")
	public String loginError(Model model) {
		model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
		return "member/memberLoginForm";
	}
	
	
	
}
