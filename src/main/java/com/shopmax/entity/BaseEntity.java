package com.shopmax.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

@EntityListeners(value = (AuditingEntityListener.class))
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends BaseTimeEntity{
	
	@CreatedBy
	@Column(updatable = false)
	private String createdBy; // 등록자
	
	@LastModifiedBy
	private String modifiedBy;//수정자
}
