package com.shopmax.Dto;

import com.shopmax.entity.CartItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {

	public String itemNm; //상품명

	public Long itemId; //상품명

	public int count; //주문수량

	public int cartPrice; //주문 금액

	public String imgUrl; //상품 이미지 경로

	//앤티티 - > Dto로 바꿔준다.
	public CartItemDto(CartItem cartItem, String imgUrl, Long itemId) {
		this.itemNm = cartItem.getItem().getItemNm();
		this.count = cartItem.getCount();
		this.cartPrice =cartItem.getCartPrice();
		this.imgUrl = imgUrl;
		this.itemId = itemId;
	}

		
}
