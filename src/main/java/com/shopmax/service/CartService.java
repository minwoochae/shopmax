package com.shopmax.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import com.shopmax.Dto.CartDto;
import com.shopmax.Dto.CartHistDto;
import com.shopmax.Dto.CartItemDto;
import com.shopmax.Dto.OrderDto;
import com.shopmax.entity.Cart;
import com.shopmax.entity.CartItem;
import com.shopmax.entity.Item;
import com.shopmax.entity.ItemImg;
import com.shopmax.entity.Member;
import com.shopmax.entity.Order;
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
	private final OrderRepository orderRepository;
	

	//장바구니 담기
	public Long cart(CartDto cartDto,String email) {
		
		//1.주문할 상품을 조회
		Item item = itemRepository.findById(cartDto.getItemId())
								  .orElseThrow(EntityNotFoundException::new);
		
		//2.현재 로그인한 회원의 이메일을 이용해 회원정보를 조회
		Member member = memberRepository.findByEmail(email);
		
		//3.주문할 상품 엔티티의 주문 수량을 이용하여 주문상품 엔티티를 생성
		List<CartItem> cartItemList = new ArrayList<>();
		CartItem cartItem = CartItem.createCartItem(item, cartDto.getCount());
		cartItemList.add(cartItem);
		
		System.out.println(item.getId());
		System.out.println(cartItem.getItem().getId());
		//4. 회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성
		System.out.println(cartItemList);
		Cart cart = Cart.createCart(member , cartItemList,item);
		cartRepository.save(cart); //insert
	
		return cart.getId();
	}
	
	
/*	public Long cart(CartDto cartDto, String email) {
	    // 1. 주문할 상품을 조회
	    Item item = itemRepository.findById(cartDto.getItemId())
	                              .orElseThrow(EntityNotFoundException::new);

	    // 2. 현재 로그인한 회원의 이메일을 이용해 회원정보를 조회
	    Member member = memberRepository.findByEmail(email);

	    // 3. 회원의 장바구니에서 이미 주문한 상품인지 확인
	    Cart cart = cartRepository.findByMember(member);

	    List<CartItem> cartItemList;
	    if (cart == null) {
	        // 3-1. 장바구니가 없으면 새로운 장바구니 생성
	        cartItemList = new ArrayList<>();
	        cart = Cart.createCart(member, cartItemList);
	    } else {
	        // 3-2. 장바구니가 이미 있으면 해당 상품의 수량을 업데이트
	        cartItemList = cart.getCartItems();
	        Optional<CartItem> existingCartItem = cartItemList.stream()
	                .filter(ci -> ci.getItem().getId().equals(item.getId()))
	                .findFirst();

	        if (existingCartItem.isPresent()) {
	            // 이미 장바구니에 있는 상품이면 알림 및 처리
	            // 여기서는 간단히 예외를 던지도록 했지만, 실제로는 어떻게 처리할지를 결정해야 함
	            throw new IllegalArgumentException("이미 장바구니에 있는 상품입니다.");
	        } else {
	            // 3-3. 새로운 상품이면 장바구니에 추가
	            CartItem cartItem = CartItem.createCartItem(item, cartDto.getCount());
	            cartItemList.add(cartItem);
	        }
	    }

	    // 4. 회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 저장
	    cartRepository.save(cart);

	    return cart.getId();
	}*/
	
	//주문 목록을 가져오는 서비스
	@Transactional(readOnly = true)
	public Page<CartHistDto> getCartList(String email, Pageable pageable){
		//1. 유저 아이디와 페이징 조건을 이용하여 주문 목록을 조회
		System.out.println(email + "ㅇㅇㅇㅇㅇㅇㅇ");
		List<Cart> carts =cartRepository.findCarts(email, pageable);
		//2. 유저의 주문 총개수를 구한다.
		Long totalcount = cartRepository.countCart(email);
	
		
		//3. 주문 리스트를 순회하면서 구매 이력 페이지 전달형 DTO(orderHistDto)를 생성
		List<CartHistDto> cartHistDtos = new ArrayList<>();
		for(Cart cart : carts) {
			CartHistDto cartHistDto = new CartHistDto(cart);
			List<CartItem> cartItems = cart.getCartItems();
			
			for(CartItem cartItem : cartItems) {
				//상품의 대표 이미지 가져오기
				ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(cartItem.getItem().getId(), "Y");
					CartItemDto cartItemDto = new CartItemDto(cartItem, itemImg.getImgUrl());
					cartHistDto.addCartItemDto(cartItemDto);
					
			}
			cartHistDtos.add(cartHistDto);
		}
	
		
		return new PageImpl<>(cartHistDtos,pageable, totalcount); //4.페이지 구현 객체를 생성하여 return
	}
	
	//본인확인 (현재 로그인한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사)
	public boolean validateCart(Long cartId, String email) {
		Member curMember = memberRepository.findByEmail(email); //로그인한 사용자 찾기
		Cart cart = cartRepository.findById(cartId)
									 .orElseThrow(EntityNotFoundException::new); //주문 내역
		
		Member saveMember = cart.getMember(); //주문한 사용자 찾기
		//로그인한 사용자의 이메일과 주문한 사용자의 이메일의 값이 같은지 최종 비교
		if(!StringUtils.equals(curMember.getEmail(), saveMember.getEmail())) {
			//같지 않으면
			return false;
		}
		return true;
	}
	
	//주문삭제
	public void deleteCart(Long cartId) {
		//+delete하기 전에 select 를 한번 해준다
		//-> 영속성 컨텍스트에 앤티티를 저장한 후 변경 감지를 하도록 하기 위해
		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(EntityNotFoundException::new);
		
//		delete
		cartRepository.delete(cart);
	}
	
}
 