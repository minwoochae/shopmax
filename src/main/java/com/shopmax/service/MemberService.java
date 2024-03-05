package com.shopmax.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopmax.Dto.QaDto;
import com.shopmax.entity.Member;
import com.shopmax.entity.Qa;
import com.shopmax.repository.MemberRepository;
import com.shopmax.repository.QaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@Transactional //쿼리문 수행시 에러가 발생하면 데이터를 이전 상대로 롤백시킨다.
@RequiredArgsConstructor  //@Autowired를 사용하지 않고 필드의 의존성 주입을 시켜준다.
public class MemberService implements UserDetailsService{
	private final MemberRepository memberRepository;
	private final QaRepository qaRepository;
	
	//회원가입 데이터를 DB에 저장한다.
	public Member saveMember(Member member) {
		validateDuplicatMember(member); //이메일 중복체크
		Member savedMember = memberRepository.save(member); //insert
		return savedMember; //회원가입된 데이터를 리턴시켜준다.
	}
	
	public Member getMember(String email) {
		Member getMembers = memberRepository.findByEmail(email); 
		
		return getMembers; //
	}
	
	//비밀번호 수정
	public void updatepassword(String email, String password, PasswordEncoder passwordEncoder) {
		Member member = memberRepository.findByEmail(email);

		if (passwordEncoder.matches(password, member.getPassword()) == true) {
			throw new IllegalStateException("기존 비밀번호와 동일합니다.");
		} else {
			member.updatepassword(password);
		}
	}
	//이메일 중복채크
	private void validateDuplicatMember(Member member) {
		Member findMember = memberRepository.findByEmail(member.getEmail());
		
		if(findMember != null) {
			throw new IllegalStateException("이미 가입된 회원입니다.");
		}
	}
	
	//이메일 찾기
	public Member findByEmail(String email) {
		Member member = memberRepository.findByEmail(email);
	    return member;
	}
	
	
	public void deleteMember(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);

		memberRepository.delete(member);
	}
	
	//업데이트(내정보수정)
	public void updateNameAddress(String email, String name, String address) {

		Member member = memberRepository.findByEmail(email);

		member.updatenameAddress(name, address);

	}


	@Transactional(readOnly = true)
	public Page<Qa> getqaPage(Pageable pageable) {
		return qaRepository.findAll(pageable);
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		//사용자가 입력한 email이 DB에 있는지 쿼리문을 사용한다.
		Member member = memberRepository.findByEmail(email);
		
		if(member ==null) {
				throw new UsernameNotFoundException(email);
		}
		
		//사용자가 있다면 DB에서 가져온 값으로 userDetails 객체를 만들어서 반환
		return User.builder()
				.username(member.getEmail())
				.password(member.getPassword())
				.roles(member.getRole().toString())
				.build();
	}
	
	
	

	
} 
