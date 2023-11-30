package com.shopmax.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopmax.Dto.CartDto;
import com.shopmax.Dto.OrderDto;
import com.shopmax.entity.Cart;
import com.shopmax.entity.CartItem;
import com.shopmax.entity.Item;
import com.shopmax.entity.Member;
import com.shopmax.entity.Order;
import com.shopmax.entity.OrderItem;
import com.shopmax.repository.CartRepository;
import com.shopmax.repository.ItemImgRepository;
import com.shopmax.repository.ItemRepository;
import com.shopmax.repository.MemberRepository;
import com.shopmax.repository.OrderRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
	private final ItemRepository itemRepository;
	private final MemberRepository memberRepository;
	private final CartRepository cartRepository;
	private final ItemImgRepository itemImgRepository;
	

	//장바구니 담기
	public Long cart(CartDto cartDto, String email) {
		
		//1.주문할 상품을 조회
		Item item = itemRepository.findById(cartDto.getItemId())
								  .orElseThrow(EntityNotFoundException::new);
		
		//2.현재 로그인한 회원의 이메일을 이용해 회원정보를 조회
		Member member = memberRepository.findByEmail(email);
		
		//3.주문할 상품 엔티티의 주문 수량을 이용하여 주문상품 엔티티를 생성
		List<CartItem> cartItemList = new ArrayList<>();
		CartItem cartItem = CartItem.createCartItem(item, cartDto.getCount());
		cartItemList.add(cartItem);
		
		//4. 회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성
		Cart cart = Cart.createCart(member, cartItemList);
		cartRepository.save(cart); //insert
		
		return cart.getId();
	}
}
