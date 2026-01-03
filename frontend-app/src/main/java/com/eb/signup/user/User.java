package com.eb.signup.user;

import java.time.LocalDate;

public class User {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private LocalDate dateOfBirth;
  private String address;
  private String postalCode;
  private String city;
  private String passwordHash;
  private String status;

  public User(Long id, String firstName, String lastName, String email, String phone, 
              LocalDate dateOfBirth, String address, String postalCode, String city, 
              String passwordHash, String status) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.dateOfBirth = dateOfBirth;
    this.address = address;
    this.postalCode = postalCode;
    this.city = city;
    this.passwordHash = passwordHash;
    this.status = status;
  }
  
  // Constructeur pour compatibilité avec l'ancien code
  public User(Long id, String email, String passwordHash, String status) {
    this(id, null, null, email, null, null, null, null, null, passwordHash, status);
  }
  
  public Long getId() { return id; }
  public String getFirstName() { return firstName; }
  public String getLastName() { return lastName; }
  public String getEmail() { return email; }
  public String getPhone() { return phone; }
  public LocalDate getDateOfBirth() { return dateOfBirth; }
  public String getAddress() { return address; }
  public String getPostalCode() { return postalCode; }
  public String getCity() { return city; }
  public String getPasswordHash() { return passwordHash; }
  public String getStatus() { return status; }
  
  // Méthodes utilitaires
  public String getFullName() {
    return (firstName != null && lastName != null) ? firstName + " " + lastName : email;
  }
}
