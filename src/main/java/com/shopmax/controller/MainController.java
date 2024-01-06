package com.shopmax.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopmax.Dto.CartDto;
import com.shopmax.Dto.ItemRankDto;
import com.shopmax.Dto.ItemSearchDto;
import com.shopmax.Dto.MainItemDto;
import com.shopmax.entity.Member;
import com.shopmax.service.CartService;
import com.shopmax.service.ItemService;
import com.shopmax.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	private final ItemService itemService;
	private final MemberService memberService;
	private final CartService cartService;
	
	@GetMapping(value = "/")
	public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model , Principal principal) {
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 , 6);
		Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
		List<ItemRankDto> itemsRank = itemService.getItemRankList();
		
		model.addAttribute("itemsRank" , itemsRank);
		model.addAttribute("items" , items);
		model.addAttribute("itemSearchDto", itemService);
		if(principal != null) {
			Member members = memberService.getMember(principal.getName());
			if(members != null){
		Long Count = cartService.cartCount(members);
	    // 모델에 상품 수를 추가합니다
	    model.addAttribute("Count", Count);
			}
			}
		
		
		
		return "main";
	}
	

}
