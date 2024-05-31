package com.shopmax.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopmax.Dto.ItemRankDto;
import com.shopmax.constant.ItemSellStatus;
import com.shopmax.entity.Item;

//<해당 repository에서 사용할 Entity, Entity클래스의 기본키 타입>
public interface ItemRepository extends JpaRepository<Item, Long>,
										ItemRepositoryCustom{
	@Query(value ="select data.num num, item.item_id id, item.item_nm itemNm, item.price price, item_img.img_url imgUrl, item_img.repimg_yn repimgYn \r\n"
			+ "            from item \r\n"
			+ "			inner join item_img on (item.item_id = item_img.item_id)\r\n"
			+ "			inner join (select @ROWNUM\\:=@ROWNUM+1 num, groupdata.* from\r\n"
			+ "			            (select order_item.item_id, count(*) cnt\r\n"
			+ "			              from order_item\r\n"
			+ "			              inner join orders on (order_item.order_id = orders.order_id)\r\n"
			+ "			              where orders.order_status = 'ORDER'\r\n"
			+ "			             group by order_item.item_id order by cnt desc) groupdata,\r\n"
			+ "                          (SELECT @ROWNUM\\:=0) R \r\n"
			+ "                          limit 6) data\r\n"
			+ "			on (item.item_id = data.item_id)\r\n"
			+ "			where item_img.repimg_yn = 'Y'\r\n"
			+ "			order by num", nativeQuery = true)
	List<ItemRankDto> getItemRankList();
	
//	@Query("select count(*) from item where itemSellStatus = :itemSellStatus")
//	Long countItem(@Param("itemSellStatus") String itemSellStatus);
    @Query("SELECT COUNT(i) FROM Item i WHERE i.itemSellStatus = 'SELL'")
    long countBySellStatus();

    @Query("SELECT COUNT(i) FROM Item i WHERE i.itemSellStatus = 'SOLD_OUT'")
    long countBySoldOutStatus();
	
    @Query("SELECT COUNT(i) FROM Item i WHERE i.itemSellStatus = 'SOLD_OUT' OR i.itemSellStatus = 'SELL'")
    long countByitemSellStatus();
	
    
    
	Optional<Item> findByitemSellStatus(ItemSellStatus itemSellStatus);
	
}
