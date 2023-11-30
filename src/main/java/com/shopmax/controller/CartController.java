package com.shopmax.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shopmax.Dto.CartDto;
import com.shopmax.Dto.OrderDto;
import com.shopmax.service.CartService;
import com.shopmax.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {
	private final CartService cartService;
	
	@GetMapping(value = "/cart")
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
	
}
