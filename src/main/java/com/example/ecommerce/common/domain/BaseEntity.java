package com.example.ecommerce.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import java.time.LocalDateTime;
////
////@Getter
////@Setter
////@MappedSuperclass
////@EntityListeners(AuditingEntityListener.class)
////public class BaseEntity {
////// think again because i think overengineering
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private Long id;
////
////    @CreatedDate
////    @Column(nullable = false, updatable = false)
////    private LocalDateTime createdAt;
////
////    @LastModifiedDate
////    @Column(insertable = false)
////    private LocalDateTime lastModifiedDate;
////
////    @LastModifiedBy
////    @Column(length = 150)
////    private String updatedBy;
//
//}
