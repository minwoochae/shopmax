package com.shopmax.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopmax.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
	@Query("select o from Cart o where o.member.email = :email order by o.cartDate desc")
	List<Cart> findCarts(@Param("email") String email, Pageable pageable);
	
	//현재 로그인한 회원의 주문 개수가 몇개인지 조회
	@Query("select count(o) from Cart o where o.member.email = :email")
	Long countCart(@Param("email") String email);
	
	Cart findByItemId(Long ItemId);
	
	
}
