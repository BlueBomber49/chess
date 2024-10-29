package service;

import dataaccess.DataAccess;
import dataaccess.exception.ResponseException;

public class AdminService {
  private DataAccess data;
  public AdminService(DataAccess data){
    this.data = data;
  }
  public void clearAll() throws ResponseException {
    data.clearAll();
  }
}
