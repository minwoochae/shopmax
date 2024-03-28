package com.shopmax.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shopmax.Dto.MemberFormDto;
import com.shopmax.Dto.QaDto;
import com.shopmax.constant.CartStatus;
import com.shopmax.constant.QandA;
import com.shopmax.constant.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Table(name ="qa")
@Getter
@ToString
public class Qa extends BaseEntity {
	@Id
	@Column(name="qa_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private QandA QaA;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime qaDate;
	
	@ManyToOne(fetch =FetchType.LAZY)
	@JoinColumn(name ="member_id")
	private Member member;
	
	
	//제목
	private String title;

	//문의 유형
	private String inquirytype;
	//질문
	private String question;


	public static Qa createQa( QaDto qaDto, Member member) {
		Qa qa =new Qa();
		qa.setTitle(qaDto.getTitle());
		qa.setQuestion(qaDto.getQuestion());
		qa.setInquirytype(qaDto.getInquirytype());
		qa.setQaDate(LocalDateTime.now());
		qa.setQaA(QandA.PROCESSING);
		qa.setMember(member);
		
		return qa;
	}
	
	
}
