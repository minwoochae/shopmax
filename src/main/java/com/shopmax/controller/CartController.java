package com.shopmax.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.shopmax.entity.Item;
import com.shopmax.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shopmax.Dto.CartDto;
import com.shopmax.Dto.CartHistDto;
import com.shopmax.Dto.OrderDto;
import com.shopmax.Dto.OrderHistDto;
import com.shopmax.entity.Member;
import com.shopmax.entity.Order;
import com.shopmax.service.CartService;
import com.shopmax.service.MemberService;
import com.shopmax.service.OrderService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {
	private final CartService cartService;
	private final MemberService memberService;
	private final ItemService itemService;

	//장바구니
	@PostMapping(value = "/cart")
	public @ResponseBody ResponseEntity cartList(@RequestBody @Valid CartDto cartDto,
			BindingResult bindingResult , Principal principal) {
		//Principal: 로그인한 사용자의 정보를 가져올수 있다.
		
				if(bindingResult.hasErrors()) {
					StringBuilder sb = new StringBuilder();
					List<FieldError> fieldErrors = bindingResult.getFieldErrors();
					
					for(FieldError fieldError : fieldErrors) {
						sb.append(fieldError.getDefaultMessage()); //에러메세지를 합친다.
					}
					return new ResponseEntity<String>(sb.toString(),HttpStatus.BAD_REQUEST);
				}
				
				String email = principal.getName(); //id 에 해당하는 정보를 가지고 온다,(email)
				Long cartId;
				try {
					cartId = cartService.cart(cartDto, email); //주문하기
				} catch (Exception e) {
					return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
				}
				
				return new ResponseEntity<Long>(cartId,HttpStatus.OK); //성공시
	}		
	
	//장바구니 리스트
	@GetMapping(value = {"/carts" , "/carts/{page}"})
	public String cartHist(@PathVariable("page") Optional<Integer> page,
			Principal principal, Model model) {
		//1. 한페치지당 4개의 데이터를 가지고 오도록 설정
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
		//2. 서비스 호출
		Page<CartHistDto> cartHistDtoList = cartService.getCartList(principal.getName() ,pageable);

		//3.서비스에서 가져온 값을들 view단에 model을 이용해 전송
		model.addAttribute("carts",cartHistDtoList);
		model.addAttribute("maxPage", 5); //하단에 보여줄 최대 페이지

		//카운트
		Member members = memberService.getMember(principal.getName());
		Long Count = cartService.cartCount(members);
	    // 모델에 상품 수를 추가합니다
	    model.addAttribute("Count", Count);
		
		return "cart/cartList";
	}
	
	

	
	//장바구니 삭제
	@DeleteMapping("/cart/{cartId}/delete")
	public @ResponseBody ResponseEntity deleteOrder(@PathVariable("cartId") Long cartId,
			Principal principal) {
		//1.본인인증
		if(!cartService.validateCart(cartId, principal.getName())) {
			return new ResponseEntity<String>("주문 삭제 권한이 없습니다." , HttpStatus.FORBIDDEN);
		}
		//2. 주문삭제
		cartService.deleteCart(cartId);
		
		return new ResponseEntity<Long>(cartId, HttpStatus.OK);
	}
	

}
