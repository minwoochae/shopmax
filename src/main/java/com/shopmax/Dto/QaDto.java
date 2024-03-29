package com.shopmax.Dto;

import org.modelmapper.ModelMapper;

import com.shopmax.entity.Member;
import com.shopmax.entity.Qa;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class QaDto {
	private Long id;

	private String qaDate;

	private String title;
	
	@NotBlank(message = "문의 유형을 선택해주세요.")
	private String inquirytype;

	private String question;

	private Member member;

	private static final int TITLE_MAX_LENGTH = 15; // 제목의 최대 길이를 15으로 제한합니다.

	private static final int QUESTION_MAX_LENGTH = 200; // 질문의 최대 길이를 300으로 제한합니다.

   public void setTitle(String title) {
        // 제목이 null이 아니고 최대 길이보다 길 경우, 최대 길이까지만 저장합니다.
        if (title != null && title.length() > TITLE_MAX_LENGTH) {
            this.title = title.substring(0, TITLE_MAX_LENGTH);
        } else {
            this.title = title;
        }
    }

    public void setQuestion(String question) {
        // 질문이 null이 아니고 최대 길이보다 길 경우, 최대 길이까지만 저장합니다.
        if (question != null && question.length() > QUESTION_MAX_LENGTH) {
            this.question = question.substring(0, QUESTION_MAX_LENGTH);
        } else {
            this.question = question;
        }
    }

	private static ModelMapper modelMapper = new ModelMapper();

	// entity -> dto로 바꿈
	public static QaDto of(Qa qa) {
		return modelMapper.map(qa, QaDto.class);
	}
}
