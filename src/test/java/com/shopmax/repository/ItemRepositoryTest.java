package com.shopmax.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shopmax.constant.ItemSellStatus;
import com.shopmax.entity.Item;
import com.shopmax.entity.QItem;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest // 빈 객체로 만든다 - > 스프링 컨테이너에 등록된다
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {
	@Autowired
	ItemRepository itemRepository;

	@PersistenceContext // 연속성 컨텍스트
	EntityManager em; // 앤티티 매니저(앤티티를 관리)

	@Disabled
	@Test
	@DisplayName("상품 저장 테스트")
	public void createItemTest() {
		Item item = new Item();
		item.setItemNm("테스트 상품");
		item.setPrice(10000);
		item.setItemDetail("테스트 상품 상세설명");
		item.setItemSellStatus(ItemSellStatus.SELL);
		item.setStockNumber(100);
		item.setRegTime(LocalDateTime.now());
		item.setUpdateTime(LocalDateTime.now());

		// insert
		Item savedItem = itemRepository.save(item);
		System.out.println(savedItem.toString());

	}

	public void createItemList() {
		for (int i = 1; i <= 5; i++) {
			Item item = new Item();
			item.setItemNm("테스트 상품" + i);
			item.setPrice(10000 + i);
			item.setItemDetail("테스트 상품 상세설명" + i);
			item.setItemSellStatus(ItemSellStatus.SELL);
			item.setStockNumber(100);
			item.setRegTime(LocalDateTime.now());
			item.setUpdateTime(LocalDateTime.now());

			// insert
			Item savedItem = itemRepository.save(item);
		}
		for (int i = 6; i <= 10; i++) {
			Item item = new Item();
			item.setItemNm("테스트 상품" + i);
			item.setPrice(10000 + i);
			item.setItemDetail("테스트 상품 상세설명" + i);
			item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
			item.setStockNumber(0);
			item.setRegTime(LocalDateTime.now());
			item.setUpdateTime(LocalDateTime.now());

			// insert
			Item savedItem = itemRepository.save(item);
		}
	}

	public void createItemList2() {
		for (int i = 1; i <= 10; i++) {
			Item item = new Item();
			item.setItemNm("테스트 상품" + i);
			item.setPrice(10000 + i);
			item.setItemDetail("테스트 상품 상세설명" + i);
			item.setItemSellStatus(ItemSellStatus.SELL);
			item.setStockNumber(100);
			item.setRegTime(LocalDateTime.now());
			item.setUpdateTime(LocalDateTime.now());

			// insert
			Item savedItem = itemRepository.save(item);
		}
	}

	@Disabled
	@Test
	@DisplayName("상품 조회 테스트")
	public void findByItemNmTest() {
		this.createItemList(); // 데이터 10개 insert
		List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");

		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}

	@Disabled
	@Test
	@DisplayName("퀴즈1-1")
	public void quiz1_1Tset() {
		this.createItemList();
		List<Item> itemList = itemRepository.findByItemNmAndItemSellStatus("테스트 상품1", ItemSellStatus.SELL);

		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}

	@Disabled
	@Test
	@DisplayName("퀴즈1-2")
	public void quiz1_2Tset() {
		this.createItemList();
		List<Item> itemList = itemRepository.findByPriceBetween(10004, 10008);
		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}

	@Disabled
	@Test
	@DisplayName("퀴즈1-3")
	public void quiz1_3Tset() {
		this.createItemList();
		List<Item> itemList = itemRepository.findByRegTimeAfter(LocalDateTime.of(2023, 01, 01, 12, 12, 44));

		for (Item item : itemList) {
			System.out.println(item);
		}
	}

	@Disabled
	@Test
	@DisplayName("퀴즈1-4")
	public void quiz1_4Tset() {
		this.createItemList();
		List<Item> itemList = itemRepository.findByItemSellStatusNotNull();
		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}

	@Disabled
	@Test
	@DisplayName("퀴즈1-5")
	public void quiz1_5Tset() {
		this.createItemList();
		List<Item> itemList = itemRepository.findByItemDetailLike("%설명1");
		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}

	@Disabled
	@Test
	@DisplayName("퀴즈1-6")
	public void quiz1_6Tset() {
		this.createItemList();
		List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}

	@Disabled
	@Test
	@DisplayName("퀴즈1-7")
	public void quiz1_7Tset() {
		this.createItemList();
		List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}

	@Disabled
	@Test
	@DisplayName("@Query를 이용한 상품 조회 테스트")
	public void findByItemDetailTest() {
		createItemList();
		List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세");
		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}

	@Disabled
	@Test
	@DisplayName("@Query(native 쿼리)를 이용한 상품 조회 테스트")
	public void findByItemDetailNativeTest() {
		createItemList();
		List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세");
		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}

	@Disabled
	@Test
	@DisplayName("퀴즈2-1")
	public void quiz2_1Test() {
		this.createItemList();
		List<Item> itemList = itemRepository.findByprice(10005);

		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}

	@Disabled
	@Test
	@DisplayName("퀴즈2-2")
	public void quiz2_2Test() {
		this.createItemList();
		List<Item> itemList = itemRepository.getItemNmAndItemSellStatus("테스트 상품1", ItemSellStatus.SELL);

		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}

	@Disabled
	@Test
	@DisplayName("querydsl 조회 테스트")
	public void queryDslTest() {
		this.createItemList();
		JPAQueryFactory qf = new JPAQueryFactory(em); // 쿼리를 동적으로 생성하기 위한 객체
		QItem qItem = QItem.item; // Item앤티티

		// 쿼리문을 실행했을때 결과값을 담을 타입을 제네릭에 선언
		/*
		 * select * from Item where item_sele_status = 'SELL' and item_detail like '%테스트
		 * 상품 상세%' orderby price desc;
		 */
		JPAQuery<Item> query = qf.selectFrom(qItem).where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
				.where(qItem.itemDetail.like("%테스트 상품 상세%")).orderBy(qItem.price.desc());
		List<Item> itemList = query.fetch(); // 쿼리문 실행

		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}
	@Disabled
	@Test
	@DisplayName("qquerydsl 조회 테스트2")
	public void queryDslTest2() {
		this.createItemList2();
		JPAQueryFactory qf = new JPAQueryFactory(em);
		QItem qitem = QItem.item;

		// 0부터 페이지 변호가 시작된다.
		Pageable page = PageRequest.of(0, 2);// of(조회할 페이지의 번호, 한페이지당 조회할 데이터 갯수)

		/*
		 * select * from item where item_sell_status = 'SELL' and item_detail like '%테스트
		 * 상품 상세%' and price > 10003;
		 * 
		 */
		JPAQuery<Item> query = qf.selectFrom(qitem)
				.where(qitem.itemSellStatus.eq(ItemSellStatus.SELL))
				.where(qitem.itemDetail.like("%테스트 상품 상세%"))
				.where(qitem.price.gt(10003)) // "> 10003" 10003보다 큰
				.offset(page.getOffset()).limit(page.getPageSize());
		List<Item> itemList = query.fetch();

		for (Item item : itemList) {
			System.out.println(item.toString());
		}

	}
	@Disabled
	@Test
	@DisplayName("qquerydsl 퀴즈3-1")
	public void quiz3_1(){
		this.createItemList2();
		JPAQueryFactory qf = new JPAQueryFactory(em);
		QItem qitem = QItem.item;

		JPAQuery<Item> query = qf.selectFrom(qitem)
				.where(qitem.itemSellStatus.eq(ItemSellStatus.SELL))
				.where(qitem.itemNm.eq("테스트 상품1"));
		List<Item> itemList = query.fetch();

		for (Item item : itemList) {
			System.out.println(item.toString());
		}

	}
	@Disabled
	@Test
	@DisplayName("qquerydsl 퀴즈3-2")
	public void quiz3_2(){
		this.createItemList2();
		JPAQueryFactory qf = new JPAQueryFactory(em);
		QItem qitem = QItem.item;

		JPAQuery<Item> query = qf.selectFrom(qitem)
				.where(qitem.price.between(10004,10008));
		List<Item> itemList = query.fetch();

		for (Item item : itemList) {
			System.out.println(item.toString());
		}

	}
	@Disabled
	@Test
	@DisplayName("qquerydsl 퀴즈3-3")
	public void quiz3_3(){
		this.createItemList2();
		JPAQueryFactory qf = new JPAQueryFactory(em);
		QItem qitem = QItem.item;
		
		//LocalDateTime regTime =LocalDateTime.of(2023, 01, 01, 12, 12, 44)
		JPAQuery<Item> query = qf.selectFrom(qitem)
				.where(qitem.regTime.after(LocalDateTime.of(2023, 01, 01, 12, 12, 44)));
		List<Item> itemList = query.fetch();
		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}
	@Disabled
	@Test
	@DisplayName("qquerydsl 퀴즈3-4")
	public void quiz3_4(){
		this.createItemList2();
		JPAQueryFactory qf = new JPAQueryFactory(em);
		QItem qitem = QItem.item;

		JPAQuery<Item> query = qf.selectFrom(qitem)
				.where(qitem.itemSellStatus.isNotNull());
		List<Item> itemList = query.fetch();
		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}
	@Test
	@DisplayName("qquerydsl 퀴즈3-5")
	public void quiz3_5(){
		this.createItemList2();
		JPAQueryFactory qf = new JPAQueryFactory(em);
		QItem qitem = QItem.item;

		JPAQuery<Item> query = qf.selectFrom(qitem)
				.where(qitem.itemDetail.like("%설명1"));
		List<Item> itemList = query.fetch();
		for (Item item : itemList) {
			System.out.println(item.toString());
		}
	}
}
