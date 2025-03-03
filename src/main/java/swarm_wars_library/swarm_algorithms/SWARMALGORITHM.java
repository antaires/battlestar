package swarm_wars_library.swarm_algorithms;

import java.util.Map;

import swarm_wars_library.fsm.FSMSTATE;

import java.util.HashMap;

public enum SWARMALGORITHM {
  SPECIALSUICIDE(0),
  SPECIALGHOST(1),
  SPECIALSTAR(2),
  SPECIALSACRIFICE(3), 
  DEFENDSHELL(4), 
  DEFENDFLOCK(5), 
  DEFENDINVINCIBLE(6), 
  DEFENDHIBERNATE(7), 
  SCOUTRANDOM(8), 
  SCOUTBEE(9), 
  SCOUTANT(10),
  SCOUTPSO(11);

  private int value;
  private static Map<Integer, SWARMALGORITHM> map = new HashMap<>();

  private SWARMALGORITHM(int value) {
      this.value = value;
  }

  static {
      for (SWARMALGORITHM swarmAlgorithm : SWARMALGORITHM.values()) {
          map.put(swarmAlgorithm.value, swarmAlgorithm);
      }
  }

  public static SWARMALGORITHM valueOf(int swarmAlgorithm) {
      return (SWARMALGORITHM) map.get(swarmAlgorithm);
  }

  public int getValue() {
      return value;
  }

  public FSMSTATE getFSMState(){
    if(this.value <= 3){
        return FSMSTATE.SPECIAL;
    }
    else if (this.value <= 7){
        return FSMSTATE.DEFEND;
    }
    else {
        return FSMSTATE.SCOUT;
    }
  }
}