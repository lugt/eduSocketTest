package edu.szu;

import edu.szu.socket.KnockServer;

public class TestHelper {
  public static void main(String[] args) {
    Harness harness = HarnessFactory.createHarness(new KnockServer(), "8099");
  }
}
