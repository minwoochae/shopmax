package com.shopmax.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Table(name = "cart_item")
@Getter
@ToString
public class CartItem  extends BaseEntity{
	@Id
	@Column(name = "cart_item_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch =FetchType.LAZY)
	@JoinColumn(name="cart_id")
	private Cart cart;
	
	@ManyToOne(fetch =FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;
	
	private int cartPrice; //상품가격
	
	private int count; //수량
	
	
	//주문할 상품하고 주문 수량을 통해 orderItem객체를 만든다.
		public static CartItem createCartItem(Item item, int conut) {
		CartItem cartItem = new CartItem();
		cartItem.setItem(item);
		cartItem.setCount(conut);
		cartItem.setCartPrice(item.getPrice());
		
		item.removeStock(conut); //재고
		
		return cartItem;
		
		}

		
}
