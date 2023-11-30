package com.shopmax.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.shopmax.constant.CartStatus;
import com.shopmax.constant.OrderStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="cart")
@Setter
@Getter
@ToString
public class Cart  extends BaseEntity{
	
	@Id
	@Column(name="card_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private LocalDateTime cartDate; //주문일
	
	@Enumerated(EnumType.STRING)
	private CartStatus cartStatus; //주문상태
	
	@OneToOne(fetch =FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	//order에서도 orderItem을 참조할 수 있도록 양방향 관계를 만든다.
	//다만 orderItem은 자식 테이블이 되므로 List로 만든다.
	@OneToMany(mappedBy = "cart" , cascade = CascadeType.ALL, orphanRemoval = true , fetch =FetchType.LAZY) //연관관계의 주인 설정(외래키로 지정)
	private List<CartItem> cartItems =new ArrayList<>();

	public void addOrderItem(CartItem cartItem) {
		this.cartItems.add(cartItem);
		cartItem.setCart(this);
	}
	
	//order 객체를 생성해준다
	public static Cart createCart(Member member, List<CartItem> cartItemList) {
		Cart cart = new Cart();
		cart.setMember(member);
		
		for(CartItem cartItem : cartItemList){
			cart.addOrderItem(cartItem);
		}
		cart.setCartStatus(CartStatus.CART);
		cart.setCartDate(LocalDateTime.now());
		return cart;
	}
	
}
