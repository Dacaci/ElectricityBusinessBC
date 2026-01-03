package com.eb.eb_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "email_verification_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "code_hash", nullable = false)
    private String codeHash;
    
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    
    @Column(name = "attempt_count")
    @Builder.Default
    private Integer attemptCount = 0;
    
    @Column(name = "used_at")
    private Instant usedAt;
    
    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}













