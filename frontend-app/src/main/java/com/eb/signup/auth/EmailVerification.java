package com.eb.signup.auth;

import java.time.Instant;

public class EmailVerification {
  private Long id;
  private Long userId;
  private String codeHash;
  private Instant expiresAt;
  private Integer attemptCount;
  private Instant usedAt;

  public EmailVerification(Long id, Long userId, String codeHash, Instant expiresAt,
                           Integer attemptCount, Instant usedAt) {
    this.id = id; this.userId = userId; this.codeHash = codeHash;
    this.expiresAt = expiresAt; this.attemptCount = attemptCount; this.usedAt = usedAt;
  }
  public Long getId() { return id; }
  public Long getUserId() { return userId; }
  public String getCodeHash() { return codeHash; }
  public Instant getExpiresAt() { return expiresAt; }
  public Integer getAttemptCount() { return attemptCount; }
  public Instant getUsedAt() { return usedAt; }
}
