package com.shopmax.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.apache.avro.ipc.Server;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.shopmax.Dto.*;
import com.shopmax.entity.Item;
import com.shopmax.entity.Member;
import com.shopmax.service.CartService;
import com.shopmax.service.ItemService;
import com.shopmax.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor       //의존성 주입 위해 사용
public class ItemController {
	private final ItemService itemService;
	private final MemberService memberService;
	private final CartService cartService;
	
	// 상품전체 리스트
	@GetMapping(value = "/item/shop")
	public String itemShopList(Model model, ItemSearchDto itemSearchDto, Optional<Integer> page ,Principal principal)  {
			
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 , 6);
		Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
			
		model.addAttribute("items",items);
		model.addAttribute("itemSearchDto",itemSearchDto);
		model.addAttribute("maxPage",5);
		
		try {		
			Item sellconuts = new Item();
		Item soldOutconuts = new Item();
		Item itemSellStatusconuts = new Item();
		
		Long sellCount = itemService.ItemSellCount(sellconuts);
		Long soldOutCount = itemService.ItemSoldOutCount(soldOutconuts);
		Long itemSellStatusCount = itemService.ItemSellStatusCount(itemSellStatusconuts);
		
	    model.addAttribute("sellCount", sellCount);
	    model.addAttribute("soldOutCount", soldOutCount);
	    model.addAttribute("itemSellStatusCount", itemSellStatusCount);
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage",  "상품전체 리스트에서 에러가 발생했습니다.");
			return "/";
		}

		try {
			if(principal != null){
				Member members = memberService.getMember(principal.getName());
				Long Count = cartService.cartCount(members);
			    // 모델에 상품 수를 추가합니다
			    model.addAttribute("Count", Count);
				}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage",  "상품 SoldOut 리스트에서 에러가 발생했습니다.(count 에러)");
		}
		return "item/itemShopList";
	}
	
	// 상품sell 리스트
	@GetMapping(value = "/item/shop/sell")
	public String itemShopListSell(Model model, ItemSearchDto itemSearchDto, Optional<Integer> page ,Principal principal)  {
			
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 , 6);
		Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
		model.addAttribute("items",items);
		model.addAttribute("itemSearchDto",itemSearchDto);
		model.addAttribute("maxPage",5);
		
		try {		
			Item sellconuts = new Item();
		Item soldOutconuts = new Item();
		Item itemSellStatusconuts = new Item();
		
		Long sellCount = itemService.ItemSellCount(sellconuts);
		Long soldOutCount = itemService.ItemSoldOutCount(soldOutconuts);
		Long itemSellStatusCount = itemService.ItemSellStatusCount(itemSellStatusconuts);
		
	    model.addAttribute("sellCount", sellCount);
	    model.addAttribute("soldOutCount", soldOutCount);
	    model.addAttribute("itemSellStatusCount", itemSellStatusCount);
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage",  "상품 sell 리스트에서 에러가 발생했습니다.");
			return "/";
		}
		try {
			if(principal != null){
				Member members = memberService.getMember(principal.getName());
				Long Count = cartService.cartCount(members);
			    // 모델에 상품 수를 추가합니다
			    model.addAttribute("Count", Count);
				}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage",  "상품 SoldOut 리스트에서 에러가 발생했습니다.(count 에러)");
		}
		return "item/sell";
	}
	
	// 상품sold out 리스트
	@GetMapping(value = "/item/shop/soldout")
	public String itemShopListSoldOut(Model model, ItemSearchDto itemSearchDto, Optional<Integer> page ,Principal principal)  {
			
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 , 6);
		Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
		model.addAttribute("items",items);
		model.addAttribute("itemSearchDto",itemSearchDto);
		model.addAttribute("maxPage",5);
		
		
		try {		
			Item sellconuts = new Item();
		Item soldOutconuts = new Item();
		Item itemSellStatusconuts = new Item();
		
		Long sellCount = itemService.ItemSellCount(sellconuts);
		Long soldOutCount = itemService.ItemSoldOutCount(soldOutconuts);
		Long itemSellStatusCount = itemService.ItemSellStatusCount(itemSellStatusconuts);
		
	    model.addAttribute("sellCount", sellCount);
	    model.addAttribute("soldOutCount", soldOutCount);
	    model.addAttribute("itemSellStatusCount", itemSellStatusCount);
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage",  "상품 SoldOut 리스트에서 에러가 발생했습니다.");
			return "/";
		}
		try {
			if(principal != null){
				Member members = memberService.getMember(principal.getName());
				Long Count = cartService.cartCount(members);
			    // 모델에 상품 수를 추가합니다
			    model.addAttribute("Count", Count);
				}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage",  "상품 SoldOut 리스트에서 에러가 발생했습니다.(count 에러)");
		}
	
		return "item/soldOut";
	}

	// 상품등록 페이지
	@GetMapping(value = "/admin/item/new")
	public String itemForm(Model model) {
		model.addAttribute("itemFormDto", new ItemFormDto());
		return "item/itemForm";
	}
	
	//상품 상세 페이지
	@GetMapping(value = "/item/{itemId}")
	public String itemDtl(Model model, @PathVariable("itemId") Long itemId , Principal principal) {
		ItemFormDto itemFormDto =itemService.getItemDtl(itemId);
		model.addAttribute("item", itemFormDto);

		if(principal != null){
		Member members = memberService.getMember(principal.getName());
		Long Count = cartService.cartCount(members);
	    // 모델에 상품 수를 추가합니다
	    model.addAttribute("Count", Count);
		}
		return "item/itemDtl";
	}
	
	
	//상품, 상품이미지 등록(insert)
	@PostMapping(value = "/admin/item/new")
	public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){
		if(bindingResult.hasErrors()) {
			return "item/itemForm";
		}
		//상품등록전에 첫번째 이미지가 있는지 없느지 검사(첫번째 이미지는 필수 입력값)
		if(itemImgFileList.get(0).isEmpty()) {
			model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입니다.");
			return "item/itemForm";
		}
		
		try {
			itemService.saveItem(itemFormDto, itemImgFileList);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage",  "상품 등록 중 에러가 발생했습니다.");
			return "item/itemForm";
		}
		
		return "redirect:/";
	}

	// 상품 관리 페이지
	@GetMapping(value = {"/admin/items", "/admin/items/{page}"})
	public String itemManage(ItemSearchDto itemSearchDto , 
			@PathVariable("page") Optional<Integer> page, Model model ) {
		//of(조회할 페이지의 번호: *0부터 시작, 한페이지당 조회할 데이터 갯수)
		//url경로에 페이지가 있으면 해당 페이지 번호를 조회하도록 하고 페이지 번호가 없으면 0페이지를 조회
		Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0 , 3); 
		
		Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
		
		model.addAttribute("items", items);
		model.addAttribute("itemSearchDto", itemSearchDto);
		model.addAttribute("maxPage", 5); //상품관리 페이지 하단에 보여줄 최대 페이지 번호
		
		return "item/itemMng";
	}
	
	// 상품 수정 페이지 화면 보기
	@GetMapping(value = "/admin/item/{itemId}")
	public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {
		
		try {
			ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
			model.addAttribute("itemFormDto", itemFormDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage",  "상품 수정 페이지를 불러오는 중 에러가 발생했습니다.");
			model.addAttribute("itemFormDto", new ItemFormDto());
			return "item/itemForm";
		}

		return "item/itemModifyForm";
	}
	
		// 상품 수정
		@PostMapping(value = "/admin/item/{itemId}")
		public String itemUpdate(@Valid ItemFormDto itemFormDto, Model model,
							     BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
			if(bindingResult.hasErrors()) {
				return "item/itemForm";
			}
			// 첫번째 이미지가 있는지 검사
			if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
				model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입니다.");
				return "item/itemForm";
			}
			try {
				itemService.updateItem(itemFormDto, itemImgFileList);
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errorMessage", "상품 수정 중 에러가 발생했습니다.");
				return "item/itemForm";
			}
			
			return "redirect:/";
		}
		

	

}
