package edu.szu;

import java.util.ArrayList;
import java.util.List;

public interface Harness {
  void start(String ... args);
  void close();
}


class HarnessFactory {

  static List<Harness> allHarness = new ArrayList<Harness>();

  public static <T extends Harness> T createHarness(T harness, String ... args){
    allHarness.add(harness);
    new Thread(() -> {
      harness.start(args);
    });
    return harness;
  }

  public static <T extends Harness>  void removeAllHarnessClient(){
    allHarness.forEach(Harness::close);
  }
}