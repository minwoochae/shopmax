package com.shopmax.Dto;


import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.shopmax.constant.CartStatus;
import com.shopmax.entity.Cart;
import com.shopmax.entity.CartItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartHistDto {
	private Long cartId; //주문아이디
	
	private String cartDate; //주문날짜
	
	private CartStatus cartStatus;
	
	private List<CartItemDto> cartItemDtoList = new ArrayList<>();
	
	
	
	public CartHistDto(Cart cart) {
		this.cartId = cart.getId();
		this.cartDate = cart.getCartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		this.cartStatus = cart.getCartStatus();
	}
	
	//orderItemDto 객체를 주문 상품 리스트에 추가 하는 메소드
	public void addCartItemDto(CartItemDto cartItemDto) {
		this.cartItemDtoList.add(cartItemDto);
	}
	

	
}
