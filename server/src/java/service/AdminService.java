package service;

import dataaccess.DataAccess;

public class AdminService {
  private DataAccess data;
  public AdminService(DataAccess data){
    this.data = data;
  }
  public void clearAll(){
    data.clearAll();
  }
}
