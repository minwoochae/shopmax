package com.shopmax.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="cart")
@Setter
@Getter
@ToString
public class Cart {
	
	@Id
	@Column(name="card_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@OneToOne(fetch =FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
}
