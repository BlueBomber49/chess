package service;

import dataaccess.DataAccess;

public class GameService {
  private DataAccess data;
  public GameService(DataAccess data){
    this.data = data;
  }
}
