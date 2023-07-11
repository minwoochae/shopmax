package com.shopmax.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import com.shopmax.Dto.OrderDto;
import com.shopmax.Dto.OrderHistDto;
import com.shopmax.Dto.OrderItemDto;
import com.shopmax.entity.Item;
import com.shopmax.entity.ItemImg;
import com.shopmax.entity.Member;
import com.shopmax.entity.Order;
import com.shopmax.entity.OrderItem;
import com.shopmax.repository.ItemImgRepository;
import com.shopmax.repository.ItemRepository;
import com.shopmax.repository.MemberRepository;
import com.shopmax.repository.OrderRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
	private final ItemRepository itemRepository;
	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	
	private final ItemImgRepository  itemImgRepository;
	
	//주문하기
	public Long order(OrderDto orderDto, String email) {
		
		//1.주문할 상품을 조회
		Item item = itemRepository.findById(orderDto.getItemId())
								  .orElseThrow(EntityNotFoundException::new);
		
		//2.현재 로그인한 회원의 이메일을 이용해 회원정보를 조회
		Member member = memberRepository.findByEmail(email);
		
		//3.주문할 상품 엔티티의 주문 수량을 이용하여 주문상품 엔티티를 생성
		List<OrderItem> orderItemList = new ArrayList<>();
		OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
		orderItemList.add(orderItem);
		
		//4. 회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성
		Order order = Order.createOrder(member, orderItemList);
		orderRepository.save(order); //insert
		
		return order.getId();
	}
	//주문 목록을 가져오는 서비스
	@Transactional(readOnly = true)
	public Page<OrderHistDto> getoderList(String email, Pageable pageable){
		//1. 유저 아이디와 페이징 조건을 이용하여 주문 목록을 조회
		System.out.println(email + "ㅇㅇㅇㅇㅇㅇㅇ");
		List<Order> orders =orderRepository.findOrders(email, pageable);
		//2. 유저의 주문 총개수를 구한다.
		Long totalcount = orderRepository.countOrder(email);
	
		
		//3. 주문 리스트를 순회하면서 구매 이력 페이지 전달형 DTO(orderHistDto)를 생성
		List<OrderHistDto> orderHistDtos = new ArrayList<>();
		for(Order order : orders) {
			OrderHistDto orderHistDto = new OrderHistDto(order);
			List<OrderItem> orderItems = order.getOrderItems();
			
			for(OrderItem orderItem : orderItems) {
				//상품의 대표 이미지 가져오기
				ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
					OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
					orderHistDto.addOrderItemDto(orderItemDto);
					
			}
			orderHistDtos.add(orderHistDto);
		}
	
		
		return new PageImpl<>(orderHistDtos,pageable, totalcount); //4.페이지 구현 객체를 생성하여 return
	}
	
	//본인확인 (현재 로그인한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사)
	public boolean validateOrder(Long orderId, String email) {
		Member curMember = memberRepository.findByEmail(email); //로그인한 사용자 찾기
		Order order = orderRepository.findById(orderId)
									 .orElseThrow(EntityNotFoundException::new); //주문 내역
		
		Member saveMember = order.getMember(); //주문한 사용자 찾기
		//로그인한 사용자의 이메일과 주문한 사용자의 이메일의 값이 같은지 최종 비교
		if(!StringUtils.equals(curMember.getEmail(), saveMember.getEmail())) {
			//같지 않으면
			return false;
		}
		return true;
	}
	
	//주문취소
	public void cancelOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
									 .orElseThrow(EntityNotFoundException::new);
		//OrderStatus를 update -> entity의 필드 값을 바꿔주면 된다.
		order.cancelOrder();
		
	}
	
	//주문삭제
	public void deleteOrder(Long orderId) {
		//+delete하기 전에 select 를 한번 해준다
		//-> 영속성 컨텍스트에 앤티티를 저장한 후 변경 감지를 하도록 하기 위해
		Order order = orderRepository.findById(orderId)
				.orElseThrow(EntityNotFoundException::new);
		
//		delete
		orderRepository.delete(order);
	}
} 
