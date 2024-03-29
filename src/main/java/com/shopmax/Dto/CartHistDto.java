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

	private Long itemId;

	public CartHistDto(Cart cart) {
		this.cartId = cart.getId();
		this.cartDate = cart.getCartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		this.cartStatus = cart.getCartStatus();
		this.itemId = cart.getItem().getId();
	}

	public void addCartItemDto(CartItemDto cartItemDto) {
		this.cartItemDtoList.add(cartItemDto);
	}
	

	
}
