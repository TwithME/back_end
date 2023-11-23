package com.example.twithme.common.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @LastModifiedBy
    @Column(name = "mod_user_id")
    private Long modUserId;

    @LastModifiedDate
    @Column(name = "mod_dt")
    private LocalDateTime modDateTime;

    @CreatedBy
    @Column(name = "reg_user_id")
    private Long regUserId;

    @CreatedDate
    @Column(name = "reg_dt")
    private LocalDateTime regDateTime;

}

