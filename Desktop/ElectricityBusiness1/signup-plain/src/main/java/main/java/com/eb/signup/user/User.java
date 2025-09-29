package main.java.com.eb.signup.user;

public class User {
  private Long id;
  private String email;
  private String passwordHash;
  private String status;

  public User(Long id, String email, String passwordHash, String status) {
    this.id = id; 
    this.email = email; 
    this.passwordHash = passwordHash; 
    this.status = status;
  }
  
  public Long getId() { 
    return id; 
  }
  
  public String getEmail() { 
    return email; 
  }
  
  public String getPasswordHash() { 
    return passwordHash; 
  }
  
  public String getStatus() { 
    return status; 
  }
}
