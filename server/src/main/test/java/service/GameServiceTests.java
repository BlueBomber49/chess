package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;

public class GameServiceTests {
  DataAccess data;
  @BeforeEach
  public void setup(){
    data = new MemoryDataAccess();
    data.createGame("Bob's game");
  }
}
