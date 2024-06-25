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

import com.shopmax.Dto.CartHistDto;
import com.shopmax.Dto.MemberFormDto;
import com.shopmax.Dto.OrderHistDto;
import com.shopmax.Dto.PasswordDto;
import com.shopmax.Dto.QaDto;
import com.shopmax.constant.Role;
import com.shopmax.entity.Member;
import com.shopmax.entity.Qa;
import com.shopmax.service.CartService;
import com.shopmax.service.MemberService;
import com.shopmax.service.OrderService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberservice;
	private final CartService cartService;
	private final PasswordEncoder passwordEncoder;
	private final OrderService orderService;

	// 로그인 화면
	@GetMapping(value = "/members/login")
	public String loginmember() {
		return "member/memberLoginForm";
	}

	// 회원가입 화면
	@GetMapping(value = "/members/new")
	public String memberForme(Model model) {
		model.addAttribute("memberFormDto", new MemberFormDto());
		return "member/memberForm";
	}

	// 회원가입
	@PostMapping(value = "/members/new")
	public String memberForme(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
		// @valid: 유효성을 검증하려는 객체 앞에 붙인다.
		// BindingResult: 유효성 검증 후의 결과가 들어있다.
		if (bindingResult.hasErrors()) {
			// 에러가 있다면 회원가입 페이지로 이동
			return "member/memberForm";
		}
		try {
			// MemberFormDto -> Member Entity, 비밀번호 암호화
			Member member = Member.createMember(memberFormDto, passwordEncoder);
			memberservice.saveMember(member);
		} catch (IllegalStateException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "member/memberForm";
		}
		return "redirect:/";
	}

	// 마이페이지
	@GetMapping(value = "/member/mypage")
	public String mainMypage(@PathVariable("page") Optional<Integer> page, Model model, Principal principal) {
		String members = principal.getName();
		Member member = memberservice.getMember(members);
		model.addAttribute("member", member);

		try {
			// 1. 한페치지당 4개의 데이터를 가지고 오도록 설정
			Pageable pageableorder = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
//			//2. 서비스 호출
			Page<OrderHistDto> orderHistDtoList = orderService.getoderList(principal.getName(), pageableorder);
//			//3.서비스에서 가져온 값을들 view단에 model을 이용해 전송
			model.addAttribute("orders", orderHistDtoList);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "마이페이지 확인에 있어 문제가 발생하였습니다(주문)");
		}

		try {
			// 1. 한페치지당 4개의 데이터를 가지고 오도록 설정
			Pageable pageablecart = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
			// 2. 서비스 호출
			Page<CartHistDto> cartHistDtoList = cartService.getCartList(principal.getName(), pageablecart);

			// 3.서비스에서 가져온 값을들 view단에 model을 이용해 전송
			model.addAttribute("carts", cartHistDtoList);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "마이페이지 확인에 있어 문제가 발생하였습니다(장바구니)");
		}

		if (principal != null) {
			// 카운트
			Member mb = memberservice.getMember(principal.getName());
			Long Count = cartService.cartCount(mb);
			// 모델에 상품 수를 추가합니다
			model.addAttribute("Count", Count);
		}
		return "member/MyPage";
	}

	// 내 정보
	@GetMapping(value = "/member/MyInfo")
	public String mainMyInpo(Model model, Principal principal) {
		String members = principal.getName();
		Member member = memberservice.getMember(members);
		model.addAttribute("member", member);

		if (principal != null) {
			// 카운트
			Member mb = memberservice.getMember(principal.getName());
			Long Count = cartService.cartCount(mb);
			// 모델에 상품 수를 추가합니다
			model.addAttribute("Count", Count);
		}
		return "member/MyInfo";
	}

	// 내 정보 수정
	@GetMapping(value = "/member/MyInformation")
	public String mainMyInformation(Model model, Principal principal) {
		String members = principal.getName();
		Member member = memberservice.getMember(members);
		model.addAttribute("member", member);

		if (principal != null) {
			// 카운트
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

	// 비밀번호 확인
	@GetMapping("/member/checkPwd")
	@NotBlank
	public String checkPwdView(Model model, Principal principal) {
		model.addAttribute("passwordDto", new PasswordDto());
		// 카운트
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

		// 카운트
		Member mb = memberservice.getMember(principal.getName());
		Long Count = cartService.cartCount(mb);
		// 모델에 상품 수를 추가합니다
		model.addAttribute("Count", Count);
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
		if (principal != null) {
			// 카운트
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

	// 문의하기
	@GetMapping(value = "/members/qa")
	public String qa(Model model, Principal principal) {
		if (principal != null) {
			// 카운트
			Member mb = memberservice.getMember(principal.getName());
			Long Count = cartService.cartCount(mb);
			// 모델에 상품 수를 추가합니다
			model.addAttribute("Count", Count);
		}
		model.addAttribute("qaDto", new QaDto());
		return "member/qa";
	}

	@PostMapping(value = "/members/qa")
	public String postqa(@Valid QaDto qadto, BindingResult bindingResult, Model model, Principal principal) {

		if (bindingResult.hasErrors()) {
			return "qa/list";
		}

		try {
			String members = principal.getName();
			Member member = memberservice.getMember(members);
			Qa qa = Qa.createQa(qadto, member);
			memberservice.saveQa(qa);
		} catch (IllegalStateException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "qa/list";
		}
		return "redirect:/";
	}

	// 어드민 멤버 리스트
	@GetMapping(value = { "/admin/memberList", "/admin/memberList/{page}" })
	public String adminMeberList(@PathVariable("page") Optional<Integer> page, Principal principal, Model model) {

		try {
			Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
			Page<Member> member = memberservice.getmemberListPage(pageable);

			model.addAttribute("member", member);
			model.addAttribute("maxPage", 5);
			return "member/adminMemberList";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "멤버 확인에 있어 문제가 발생하였습니다(adminMemberList)");
			return "member/adminMemberList";
		}
	}

	// 멤버 리스트의 상세정보
	@GetMapping(value = "/admin/memberListDtl/{memberId}")
	public String memberListDtl(@PathVariable("memberId") Long memberId, Model model, Principal principal) {

		try {
			Member member = memberservice.getmemberListDtl(memberId);

			model.addAttribute("member", member);

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "멤버 확인에 있어 문제가 발생하였습니다(memberListDtl)");
		}

		return "member/memberListDtl";
	}

	// Q&A 리스트
	@GetMapping(value = { "/qa/list", "/qa/list/{page}" })
	public String memberManage(@PathVariable("page") Optional<Integer> page, Model model, Principal principal) {
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

		Page<Qa> qa = memberservice.getqaPage(pageable);
		model.addAttribute("qa", qa);
		model.addAttribute("maxPage", 5);
		if (principal != null) {
			// 카운트
			Member mb = memberservice.getMember(principal.getName());
			Long Count = cartService.cartCount(mb);
			// 모델에 상품 수를 추가합니다
			model.addAttribute("Count", Count);
		}
		return "member/QaList";
	}

	// 문의하기 dtl
	@GetMapping(value = "/qa/lists/{qaId}")
	public String qaListNo(@PathVariable("qaId") Long qaId, Model model, Principal principal) {
		QaDto qadtl = memberservice.getqaDtl(qaId);
		model.addAttribute("qaDetail", qadtl);
		// Qa 가져오기
		Optional<Qa> qaa = memberservice.getQaById(qaId);
		qaa.ifPresent(qa -> model.addAttribute("qa", qa)); // Optional에서 Qa 객체 추출하여 모델에 추가

		if (principal != null) {
			// 카운트
			Member mb = memberservice.getMember(principal.getName());
			Long Count = cartService.cartCount(mb);
			// 모델에 상품 수를 추가합니다
			model.addAttribute("Count", Count);
		}
		return "member/qaDtl";
	}

	// 로그인 실패했을때
	@GetMapping(value = "/members/login/error")
	public String loginError(Model model) {
		model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
		return "member/memberLoginForm";
	}

}
