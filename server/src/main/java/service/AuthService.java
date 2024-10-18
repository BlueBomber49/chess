package service;

import dataaccess.DataAccess;

public class AuthService {
  private DataAccess data;
  public AuthService(DataAccess data){
    this.data = data;
  }
}
