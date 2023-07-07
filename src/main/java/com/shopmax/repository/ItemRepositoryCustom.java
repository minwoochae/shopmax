package com.shopmax.repository;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;

import com.shopmax.Dto.ItemSearchDto;
import com.shopmax.Dto.MainItemDto;
import com.shopmax.entity.Item;

public interface ItemRepositoryCustom {
	Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
	
	Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
