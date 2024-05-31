package com.shopmax.Dto;

import java.time.format.DateTimeFormatter;

import com.shopmax.constant.ItemSellStatus;
import com.shopmax.entity.Item;
import com.shopmax.entity.Order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {
	private String searchDateType;

	private ItemSellStatus searchSellStatus;

	private String searchBy;

	private String searchQuery= "";
	
	
	public void itemsearchSellStatus(Item item) {
		this.searchSellStatus = item.getItemSellStatus();
}
}
