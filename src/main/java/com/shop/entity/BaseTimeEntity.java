package com.shop.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@EntityListeners(value= {AuditingEntityListener.class}) //Auditing 적용
@MappedSuperclass //여러 엔티티에서 공통적으로 적용되는 속성등을 정의할 때 사용한다.
@Getter
@Setter
public abstract class BaseTimeEntity { //추상클래스로 지정함으로써 인스턴스 생성 방지

    @CreatedDate
    @Column(updatable=false)
    private LocalDateTime regTime;

    @LastModifiedDate
    private LocalDateTime updateTime;

}
