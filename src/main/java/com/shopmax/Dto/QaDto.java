package com.shopmax.Dto;

import org.modelmapper.ModelMapper;

import com.shopmax.entity.Qa;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QaDto {
	private Long id;
	
	private String qaDate;
	
	private String title;
	
	private String question;
	
	
	private static ModelMapper modelMapper = new ModelMapper();
	
	//entity -> dto로 바꿈
		public static QaDto of(Qa qa) {
			return modelMapper.map(qa, QaDto.class);
		}
}
