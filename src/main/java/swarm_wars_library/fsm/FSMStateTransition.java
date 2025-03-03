package swarm_wars_library.fsm;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Quartet;

import swarm_wars_library.comms.CommsGlobal;
import swarm_wars_library.map.Map;
import swarm_wars_library.physics.Vector2D;
import swarm_wars_library.swarm_algorithms.SWARMALGORITHM;

public class FSMStateTransition implements IFSMStateTransition{

  ArrayList<Quartet<Integer, FSMVARIABLE, FSMCOMPARISON, 
    Double>> transitionList;
  FSMSTATE fsmState;
  Integer stateId;
  SWARMALGORITHM swarmAlgorithm;
  double weight1 = 0;
  double weight2 = 0;
  double weight3 = 0;
  int playerId = Map.getInstance().getPlayerId();
  int enemyId = Map.getInstance().getEnemyId();
  String playerMe = "PLAYER" + Integer.toString(playerId);    
  String playerEnemy = "PLAYER" + Integer.toString(enemyId);

  //=========================================================================//
  // FSM State Constructor                                                   //
  //=========================================================================//
  public FSMStateTransition(int stateId, FSMSTATE fsmState){
    this.transitionList = 
      new ArrayList<Quartet<Integer, FSMVARIABLE, FSMCOMPARISON, Double>>();
    this.stateId = stateId;
    this.fsmState = fsmState;
  }

  //=========================================================================//
  // FSM State methods                                                       //
  //=========================================================================//
  @Override
  public Integer getFSMStateId(){
    for(int i = 0; i < this.transitionList.size(); i++){
      if(this.checkTransitionCondition(this.transitionList.get(i))){
        return this.transitionList.get(i).getValue0();
      }
    }
    return this.stateId;
  }

  @Override
  public FSMSTATE getFSMState(){
    return this.fsmState;
  }

  //=========================================================================//
  // FSM State Transition methods                                            //
  //=========================================================================//
  @Override
  public void addTransition(Integer toStateId, FSMVARIABLE variable, 
    FSMCOMPARISON comparison, double value){
    this.transitionList.add(
      new Quartet<Integer, FSMVARIABLE, FSMCOMPARISON, Double>(
        toStateId, variable, comparison, value)); 
  }

  public List getMyTransitions() {
    return this.transitionList;
  }

  @Override 
  public ArrayList<Integer> getTransitions(){
    ArrayList<Integer> list = new ArrayList<Integer>();
    for(int i = 0; i < this.transitionList.size(); i++){
      list.add(this.transitionList.get(i).getValue0());
    }
    return list;
  }

  //=========================================================================//
  // FSM State Algorithm methods                                             //
  //=========================================================================//
  @Override 
  public SWARMALGORITHM getSwarmAlgorithm(){
    return this.swarmAlgorithm;
  }

  @Override 
  public void setSwarmAlgorithm(SWARMALGORITHM swarmAlgorithm){
    this.swarmAlgorithm = swarmAlgorithm;
  }

  @Override
  public double[] getSwarmAlgorithmWeights(){
    double[] weights = {this.weight1, this.weight2, this.weight3};
    return weights;
  }

  @Override 
  public void setSwarmAlgorithmWeights(double weight1, double weight2, 
    double weight3){
    this.weight1 = weight1;
    this.weight2 = weight2;
    this.weight3 = weight3;
  }
  
  //=========================================================================//
  // FSM State Helper methods                                                //
  //=========================================================================//
  private boolean checkTransitionCondition(
    Quartet<Integer, FSMVARIABLE, FSMCOMPARISON, Double> transitionCondition){
    if(transitionCondition.getValue2().equals(FSMCOMPARISON.LESSTHAN)){
      if(this.getFSMVariable(transitionCondition.getValue1()) < 
        transitionCondition.getValue3()){
        return true;
      }
      else {
        return false;
      }
    }
    else {
      if(this.getFSMVariable(transitionCondition.getValue1()) > 
        transitionCondition.getValue3()){
        return true;
      }
      else {
        return false;
      }
    }
  }

  private double getFSMVariable(FSMVARIABLE fsmVariable){
    switch(fsmVariable){
      case ENEMYDISTANCE:
        return this.getNearestEnemyDistance();
      case PLAYERHEALTH:
        return CommsGlobal.get(this.playerMe).getPacket(0).getHealth();
      case ENEMYHEALTH:
        return CommsGlobal.get(this.playerEnemy).getPacket(0).getHealth();
      default:
        return 0.0;
    }
  }

  private double getNearestEnemyDistance(){
    // TODO get it to work with multiple players
    return Vector2D.sub(CommsGlobal.get(this.playerMe)
                                   .getPacket(0)
                                   .getLocation(),
                        CommsGlobal.get(this.playerEnemy)
                                   .getPacket(0)
                                   .getLocation())
                   .mag();
  }
}