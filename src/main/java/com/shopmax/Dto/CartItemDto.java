package com.shopmax.Dto;

import com.shopmax.entity.CartItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
	
	//앤티티 - > Dto로 바꿔준다.
	public CartItemDto(CartItem cartItem, String imgUrl) {
		this.itemNm = cartItem.getItem().getItemNm();
		this.count = cartItem.getCount();
		this.cartPrice =cartItem.getCartPrice();
		this.imgUrl = imgUrl;
	}
	
	public String itemNm; //상품명
	
	
	public int count; //주문수량
	
	public int cartPrice; //주문 금액
	
	public String imgUrl; //상품 이미지 경로
	
		
}
