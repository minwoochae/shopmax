package com.shopmax.entity;


import com.shopmax.constant.OrderStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Table(name ="order_item")
@ToString
public class OrderItem extends BaseEntity{
	@Id
	@Column(name = "order_item_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch =FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	@ManyToOne(fetch =FetchType.LAZY)
	@JoinColumn(name="order_id")
	private Order order;
	
	
	private int orderPrice; //주문가격
	
	private int count; //수량

	//주문할 상품하고 주문 수량을 통해 orderItem객체를 만든다.
	public static OrderItem createOrderItem(Item item, int conut) {
	OrderItem orderItem = new OrderItem();
	orderItem.setItem(item);
	orderItem.setCount(conut);
	orderItem.setOrderPrice(item.getPrice());
	
	item.removeStock(conut); //재고
	
	return orderItem;
	
	}
	
	public int getTotalPrice() {
		return orderPrice + count;
	}
	

	//재고를 원래대로
	public void cancel() {
		this.getItem().addStock(count);
		
	}
	}
