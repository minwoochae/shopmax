package com.shopmax.Dto;

import com.shopmax.entity.OrderItem;

import lombok.*;

@Getter
@Setter
public class OrderItemDto {
	//앤티티 - > Dto로 바꿔준다.
	public OrderItemDto(OrderItem orderItem, String imgUrl) {
		this.itemNm = orderItem.getItem().getItemNm();
		this.count = orderItem.getCount();
		this.orderPrice =orderItem.getOrderPrice();
		this.imgUrl = imgUrl;
	}
	
	public String itemNm; //상품명

	public int count; //주문수량
	
	public int orderPrice; //주문 금액
	
	public String imgUrl; //상품 이미지 경로
	
	
}
