package com.shopmax.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderController {
	
	@GetMapping(value = "/orders")
	public String orderHist() {
		return "order/orderHist";
	}
}
