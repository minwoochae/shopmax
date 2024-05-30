package com.shopmax.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopmax.constant.Role;
import com.shopmax.entity.Member;


public interface MemberRepository extends JpaRepository<Member, Long> {
	//select * from member where email = ?
	Member findByEmail(String email);
	
	Member findByRole(Role role);
}
