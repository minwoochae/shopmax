package com.shopmax.entity;


import com.shopmax.Dto.ItemFormDto;
import com.shopmax.constant.ItemSellStatus;
import com.shopmax.exception.OutOfStockException;

import jakarta.persistence.*;
import lombok.*;

@Entity //엔티티 클래스로 정의
@Table(name="item") //테이블 이름 지정
@Getter
@Setter
@ToString
public class Item extends BaseEntity {
	
	@Id
	@Column(name="item_id") //테이블로 생성할때 컬럼이름을 지정해준다.
	@GeneratedValue(strategy = GenerationType.AUTO) //기본키를 자동으로 생성해주는 전략 사용
	private Long id; //상품코드
	
	@Column(nullable = false, length = 50) //not null여부, 컬럼 크기지정
	private String itemNm; //상품명
	
	@Column(nullable = false) //not null여부
	private int price; //가격
	
	@Column(nullable = false) //not null여부
	private int stockNumber; //재고 수량 -> stock_number
	
	@Lob //clob과 같은 큰 타입의 문자 타입으로 컬럼을 만든다.
	@Column(nullable = false, columnDefinition ="longtext") //not null여부
	private String itemDetail; // 상품상세설명
	
	@Enumerated(EnumType.STRING) //enum의 이름을 DB에 저장
	private ItemSellStatus itemSellStatus; //판매상태(SELL 혹은 SOLD_OUT) -> item_sell_status
	
	//item 앤티티 수정
	public  void updateItem(ItemFormDto itemFormDto) {
		this.itemNm = itemFormDto.getItemNm();
		this.price = itemFormDto.getPrice();
		this.stockNumber = itemFormDto.getStockNumber();
		this.itemDetail = itemFormDto.getItemDetail();
		this.itemSellStatus = itemFormDto.getItemSellStatus();
	}
	
	//재고를  감소시킨다.
	public void removeStock(int stockNumber) {
		int restStock = this.stockNumber - stockNumber; //남은 재고 수정
		
		if(restStock < 0 ) {
			throw new OutOfStockException("상품의 재고가 부족합니다. 현재 재고수량:"+ this.stockNumber);
		}
		
		this.stockNumber = restStock; //남은 재고수량 반영
	}
	
}
