package com.shopmax.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.shopmax.entity.ItemImg;
import com.shopmax.repository.ItemImgRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {
	private String itemImgLocation = "C:/shop/item";
	
	private final ItemImgRepository  itemImgRepository;
	private final FileService fileService;
	
	/*이미지 지정
	 * 1.파일을 itemImgLocation에 저장
	 * 2.item_img 테이블에 저장
	*/
	
	public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
		String oriImgName = itemImgFile.getOriginalFilename(); //파일이름 - > 이미지1.jpg
		String imgName= "";
		String imgUrl="";
		
		//1.파일을 itemImgLocation에 저장
		if(!StringUtils.isEmpty(oriImgName)) {
			//oriImgName이 반문자열이 아니라면 이미지 파일 업로드
			imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
			imgUrl = "/images/item/" + imgName;
		}
		//2.item_img 테이블에 저장 -> 이미지1.jpg , ERSFH4FDG0454.jpg, "")
		itemImg.updateItemImg(oriImgName, imgName, imgUrl);
			itemImgRepository.save(itemImg); //db에 insert
	}

	//이미지 업데이트 메소드
	public void updateItemImg(Long itemImgId, MultipartFile itemImgFile)
			throws Exception{
		if(!itemImgFile.isEmpty()) { //첨부한 이미지 파일이 있으면
			ItemImg savedItemImg  = itemImgRepository.findById(itemImgId)
					.orElseThrow(EntityNotFoundException:: new);
			//기존 이미지 파일 C:/shop/item 폴더에서 삭제
			if(!StringUtils.isEmpty(savedItemImg.getImgName())) {
				fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
			}
			
			//수정된 이미지 파일 업로드 C:/shop/item 에 업로드
			String oriImgName = itemImgFile.getOriginalFilename();
			String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
			String imgUrl = "/images/item/" + imgName;
			
			//update 쿼리문 실행
			/*
			 *한번 insert를 진행하면 앤티티가 영속성 컨텍스트에 저장 되므로
			 *그 이후로 변경 감지 기능이 동작하기 떄문에 개발자는 update쿼리문을 쓰지 않고
			 *앤티티만 변경해주면 된다.
			 */
			savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
		}
	}
}
