# shopmax
    의류 홈페이지로서 여러 의류 상품들을 집에서도 간편 하게 구입하고 구경을 할수 있습니다.


## ⏰개발 기간
* 24.03.03 ~ 진행중

## 📚 목차
* [사용 기슬 스택](#사용-기술-스택)
* [shopmax 화면](#shopmax-화면)
* [작업 규칙](#작업-규칙)

<hr>

## 사용 기술 스택
### 🧰개발 스텍
- LANGUAGE : <img src="https://img.shields.io/badge/JAVA 17-blue?style=flat&logo=Java&logoColor=white"/>,<img src="https://img.shields.io/badge/HTML-orange?style=flat&logo=html5&logoColor=white"/>,<img src="https://img.shields.io/badge/CSS-1572B6?style=flat&logo=CSS3&logoColor=white"/> ,<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=Javascript&logoColor=white"/> ,<img src="https://img.shields.io/badge/Bootstrap-7952B3?style=flat&logo=Bootstrap&logoColor=white"/>
- FRAMEWORK : <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=springboot&logoColor=white"/>, <img src="https://img.shields.io/badge/Spring MVC-6DB33F?style=flat&logo=spring&logoColor=white"/>
- DB : <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white"/>
- SECURITY : <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=springsecurity&logoColor=white"/>
- BULID : <img src="https://img.shields.io/badge/Maven-light purple?style=flat&logo=maven&logoColor=white"/>
- TOOL : <img src="https://img.shields.io/badge/intellijidea-000000?style=flat&logo=intellijidea&logoColor=white"/>
- ORM : <img src="https://img.shields.io/badge/Spring JPA-6DB33F?style=flat&logo=spring&logoColor=white"/>
##
<hr>


## 📺shopmax 화면
### 관리자 자유롭게 상품 등록을 할수 있습니다. 
<img style="width: 500px; margin: auto; display: block;" src="https://private-user-images.githubusercontent.com/130428663/317942398-a162e9a0-2125-4d51-93e9-a76457674ed7.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTE2ODE5MjUsIm5iZiI6MTcxMTY4MTYyNSwicGF0aCI6Ii8xMzA0Mjg2NjMvMzE3OTQyMzk4LWExNjJlOWEwLTIxMjUtNGQ1MS05M2U5LWE3NjQ1NzY3NGVkNy5wbmc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwMzI5JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDMyOVQwMzA3MDVaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT04ZjdmOWRiNDVmNzQ1OWE5ZjBmNjBlZWQzZjMzNGU5NWMzYTgzMTRiNzc0Yzc5NzgzNDkxMTBkYjlhZWMyN2Y2JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.FQ5eDj2LAGqXS-enbRLGzgOTUfRx7F5RLSh5rKr1t7Q"/>


```java
1.상품 등록 화면을 출력
// 상품등록
@GetMapping(value = "/admin/item/new")
public String itemForm(Model model) {
    model.addAttribute("itemFormDto", new ItemFormDto());
    return "item/itemForm";
}

2. 상품 등록에 필요한 데이터 값을 받아온 후  itemService.saveItem에 값을 전달해 준다.
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
    
3. 받아온 값을 통해 item 에 저장을 한뒤 img url이 겹치지 않게 순차적으로 등록해준다.(첫번째 이미지는 대표 이미지로 지정해준다.)
public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
    //1.상품등록
    Item item = itemFormDto.createItem(); //dto ->entity
    itemRepository.save(item); //insert(저장)

    //2.이미지등록
    for(int i=0; i<itemImgFileList.size(); i++) {
        //부모테이블에 해당하는 entity를 먼저 넣어줘야 한다.
        ItemImg itemImg = new ItemImg();
        itemImg.setItem(item);


        //첫번째 이미지 일때 대표상품 이미지 지정
        if(i == 0) {
            itemImg.setRepimgYn("Y");
        } else {
            itemImg.setRepimgYn("N");
        }

        itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
    }

    return item.getId(); //등록된 상품 id를 리턴
}
```

###

### 사용자가 구매 내역을 통해서 구매한 상품들을 확인하고 주문을 취소 할수 있습니다.

<img style="width: 500px;margin: auto; display: block;" src="https://private-user-images.githubusercontent.com/130428663/317942387-81355054-8f21-4461-94a0-efa517a11d60.PNG?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTE2ODI2NTEsIm5iZiI6MTcxMTY4MjM1MSwicGF0aCI6Ii8xMzA0Mjg2NjMvMzE3OTQyMzg3LTgxMzU1MDU0LThmMjEtNDQ2MS05NGEwLWVmYTUxN2ExMWQ2MC5QTkc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwMzI5JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDMyOVQwMzE5MTFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0wOTM2YmExOWNmM2I3MWMwMTU1NjVmMThjYTYyOTFlMmE0YmQxY2M0N2EyNGYxZTFhODA1NDZjNzk2MDZiZDUwJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.k8LDP0paJ75TO0WEJDdbfbGnoVxo2EN_A6iEuQM-TnA"/>

구매내역을 확인하고 주문 취소 -> 삭제하기가 가능하게 제작 하였습니다..

```java
1. 주문하기를 통해서 데이터를 받아오게 되고 Pageable를 이용해서 페이징 처리를 하였습니다.
//주문 내역을 보여준다.
@GetMapping(value = {"/orders", "/orders/{page}"})
public String orderHist(@PathVariable("page") Optional<Integer> page,
                        Principal principal, Model model) {
    //1. 한페치지당 4개의 데이터를 가지고 오도록 설정
    Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
//		//2. 서비스 호출
    Page<OrderHistDto> orderHistDtoList = orderService.getoderList(principal.getName(), pageable);
//		//3.서비스에서 가져온 값을들 view단에 model을 이용해 전송
    model.addAttribute("orders", orderHistDtoList);
    model.addAttribute("maxPage", 5); //하단에 보여줄 최대 페이지
    Member members = memberService.getMember(principal.getName());
    Long Count = cartService.cartCount(members);
    // 모델에 상품 수를 추가합니다
    model.addAttribute("Count", Count);
    return "order/orderHist";
}
    
 2. 주문 취소가 이루어졌을 경우 Service를 이용하여 orderid를 찾은후 entity에서 
for(
OrderItem orderItem :orderItems){
        orderItem.cancel();
		}
를 이용하여 재고를 원래대로 되돌아 오게 구현했습니다.
        //주문 취소
@PostMapping("/order/{orderId}/cancel")

public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId,
                                                Principal principal) {
    //1.주문 취소 권한이 있는지 확인(본인 확인)
    if (!orderService.validateOrder(orderId, principal.getName())) {
        return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }
    //2.주문 취소
    orderService.cancelOrder(orderId);
    return new ResponseEntity<Long>(orderId, HttpStatus.OK); //성공했을떄

}

3. orderId를 받아온 후  orderService.deleteOrder에 값을 보내주게 되고 받아 값을 이용해서 
 orderRepository.findById(orderId) 맞는 아이디를 찾은 후 orderRepository.delete 사용하여 값을 제거 합니다.
//주문 삭제
@DeleteMapping("/order/{orderId}/delete")

public @ResponseBody ResponseEntity deleteOrder(@PathVariable("orderId") Long orderId,
                                                Principal principal) {
    //1.본인인증
    if (!orderService.validateOrder(orderId, principal.getName())) {
        return new ResponseEntity<String>("주문 삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }
    //2. 주문삭제
    orderService.deleteOrder(orderId);

    return new ResponseEntity<Long>(orderId, HttpStatus.OK);
}

```

###

### 사용자가 장바구니를 통해서 원하는 상품들을 넣어둔 후 필요할시에 구입이 가능하며 x버튼를 이용해서 장바구니에 저장 되어 있는 상품들을 자유롭게 제거할수 있습니다.


<img style="width: 500px;margin: auto; display: block;"  src="https://private-user-images.githubusercontent.com/130428663/317942399-53288645-510f-43bc-8196-504b44e011e8.PNG?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTE2ODI2NTEsIm5iZiI6MTcxMTY4MjM1MSwicGF0aCI6Ii8xMzA0Mjg2NjMvMzE3OTQyMzk5LTUzMjg4NjQ1LTUxMGYtNDNiYy04MTk2LTUwNGI0NGUwMTFlOC5QTkc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwMzI5JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDMyOVQwMzE5MTFaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0wZTIxODg5N2Y4NjcwNmY5ODRkYmQ4NzQ1ZmQwNWFkMTNhMTU5MDUyOTgwYTY5M2Y5YjJjNDgyMjI4YWEwOWFlJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.mrBsemCCIaYv_xF61dDH5OABLaI6ANPRCZLKBzHtKpc">

```javascript 
주문하기 눌러 성공하게 ajax를 이용하여 추가적으로 바로 장바구니에서 삭제되게 제작 하였습니다.
function order() {
			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");
			var url = "/order/item";

			//controller(서버)에 전달할 데이터
			var paramData = {
				itemId : $("#itemId").val(), //item에 id
				count : $("#count").val() ,//수량
				cartId : $("#cartId").val() //cart에 id
			}

			//*전달하기 전에 데이터를 JSON ->문자 번호 만들어야 한다.
			var param = JSON.stringify(paramData);

			$.ajax({
				url : url, //request URL
				type :"POST", //전송방식
				contentType : "application/json",
				data : param, //서버에 전송할 데이터
				beforeSend : function(xhr) {
					//데이터를 전송하기전에 헤더에 csrf값을 설정
					xhr.setRequestHeader(header,token);
				},
				dataType : "json",
				cache : false,
				success : function(result, status) {
					deleteCartitem($("#cartId").val());
					alert("주문이 완료 되었습니다.")
					//location.href = '/';

				},
				error : function(jqXHR, status, error) {
					if (jqXHR.status == '401'){
						alert('로그인 후 이용해주세요.')
						location.href = '/members/login';
					}else{
						//에러메세지 출력(ResponseEntity에서 받아온 값을 출력해준다.)
						alert(jqXHR.responseText);
					}
				}
			});
		}
		function deleteCartitem(cartId) {

			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");

			var url = "/cart/" + cartId + "/delete";

			$.ajax({
				url : url,
				type : "DELETE", //전송방식
				contentType : "application/json",
				beforeSend : function(xhr) {
					//데이터를 전송하기 전에 헤더에 xsrf값
					xhr.setRequestHeader(header, token);
				},
				dataType : "json",
				cache : false,
				success : function(result, status) {
					location.href = '/carts';
				},
				error : function(jqXHR, status, error) {
					if (jqXHR.status == '401') {
						alert('로그인 후 이용해주세요.');
						location.href = '/member/login';
					} else {
						alert(jqXHR.responseText);
					}
				}
			});
		}

```

###

### 비밀번호 변경은 내 비밀번호 확인후 맞을 경우 내가 원하는 비밀번호로 변경이 가능하다. 
    (현재 비밀번호와 내가 변경 하려는 비밀번호 같으면 변경이 불가능 하도록 설정 해였습니다.)

<img style="width: 500px;margin: auto; display: block;" src="https://private-user-images.githubusercontent.com/130428663/317942396-f8e59f34-b821-45f4-8cc6-2cf85e7fc097.PNG?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MTE2ODMzNTgsIm5iZiI6MTcxMTY4MzA1OCwicGF0aCI6Ii8xMzA0Mjg2NjMvMzE3OTQyMzk2LWY4ZTU5ZjM0LWI4MjEtNDVmNC04Y2M2LTJjZjg1ZTdmYzA5Ny5QTkc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBVkNPRFlMU0E1M1BRSzRaQSUyRjIwMjQwMzI5JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI0MDMyOVQwMzMwNThaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT00YzUzODg0NGNlNzhjZjRmYTM0YjZjYWI2NDE2NWU0ZjllYjRkY2M5N2U2YzUxMDgwOTQ1YjUzNjNlMTY4OGNjJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCZhY3Rvcl9pZD0wJmtleV9pZD0wJnJlcG9faWQ9MCJ9.RN4TkOvD3a549-2moJnXtFjVVfMK-sRClvPTOmkrqGI" />

```java
1. 비밀번호 확인 -> 기존에 있던 비밀번호와 일치한지 확인(passwordEncoder.matches를 이용하여 암호화 된 비밀번호와 내가 입력한 비밀번호를 검사) 
-> 일치하면 비밀번호 변경 페이지 이동 
	// 회원 수정 전 비밀번호 확인
	@PostMapping(value = "/member/checkPwd")
	public String checkPwd(@Valid PasswordDto passwordDto, Principal principal, Model model) {

		if (passwordDto.getPassword() == null || passwordDto.getPassword().trim().isEmpty()) {
			model.addAttribute("errorMessage", "비밀번호 값이 입력되어 있지 않습니다..");
			return "member/checkPwd";
		}
		Member member = memberservice.findByEmail(principal.getName());
		boolean result = passwordEncoder.matches(passwordDto.getPassword(), member.getPassword());

		//카운트
		Member mb = memberservice.getMember(principal.getName());
		Long Count = cartService.cartCount(mb);
		// 모델에 상품 수를 추가합니다
		model.addAttribute("Count", Count);
		if (!result) {
			model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
			return "member/checkPwd";
		}
		return "member/EditMember";
	}

2. 변경해야 하는 아이디(현재 로그인한 계정)를 가져옴.
// 내 비밀번호수정 (마이페이지에서)
@GetMapping(value = "/member/EditMember")
public String passwordupdate(Principal principal, Model model) {
    Member member = memberservice.findByEmail(principal.getName());
    model.addAttribute("member", member);
    return "member/EditMember";
}
    

3.if 문을 이용하여 입력한 값이 없거나 기존의 비밀번호와 같을 경우 변경이 되지 않게 했습니다. 
만약 비밀번호를 올바르게 입력 하였을 경우 entity에서 받아와서 passwordEncoder.encode를 이용하여 다시 암호화 
처리를 하였습니다.
@PostMapping("/member/EditMember")
public String passwordupdate(@RequestParam String password, Model model, Principal principal, Member member) {
    Member members = memberservice.findByEmail(principal.getName());
    if(principal != null) {
        //카운트
        Member mb = memberservice.getMember(principal.getName());
        Long Count = cartService.cartCount(mb);
        // 모델에 상품 수를 추가합니다
        model.addAttribute("Count", Count);
    }
    if (password == null || password.trim().isEmpty()) {
        model.addAttribute("errorMessage", "비밀번호 값이 없습니다.");
        model.addAttribute("member", member);
        return "member/EditMember";
    }
    if (passwordEncoder.matches(password, members.getPassword()) == true) {
        model.addAttribute("errorMessage", "기존 비밀번호와 같습니다.");
        model.addAttribute("member", member);
        return "member/EditMember";
    }

    else {
        memberservice.updatepassword(principal.getName(), passwordEncoder.encode(password), passwordEncoder);
        return "redirect:/member/mypage";
    }
}
```
<br>


## 로직


### 코드 네이밍 룰

#### 모든 자바 메소드명, 변수명은 카멜 케이스를 따릅니다.

#### 또한 누구나 알기 쉬운 단어를 사용합니다.

메소드명은 동사로 네이밍합니다.

:+1:
```java
private String personName; 

public void getUserId() {

}
```

:-1:
```java
private String PersonName;
private String personname; 

public void userid() {

}
```

#### 클래스 명은 파스칼 케이스를 따릅니다.

:+1:
```text
SampleCode.java
SampleCodeDto.java
```

:-1:
```text
samplecode.java
sampleCodeDto.java
```




 
