package com.shopmax.entity;

import lombok.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.shopmax.Dto.MemberFormDto;
import com.shopmax.constant.Role;

import jakarta.persistence.*;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {
	@Id
	@Column(name="member_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; //멤버 아이디

	@Column(unique = true, length=255)
	private String email; //이메일
	
	@Column(nullable = false, length = 255)
	private String name; //이름
	
	@Column(nullable = false, length = 255)
	private String password;//비밀번호

	private String address;
	
	@Enumerated(EnumType.STRING)
	private Role role;//역할
	
	public void updatenameAddress(String name, String address) {
		this.name = name;
		this.address = address;
	}

	public void updatepassword(String password) {
		this.password = password;
	}

	public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
		String password = passwordEncoder.encode(memberFormDto.getPassword());
		Member member = new Member();
		member.setName(memberFormDto.getName());
		member.setEmail(memberFormDto.getEmail());
		member.setAddress(memberFormDto.getAddress());
		member.setPassword(password);
		member.setRole(Role.USER);
		
		return member;
	}
}
