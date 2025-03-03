package swarm_wars_library.entities;

import java.util.ArrayList;

import processing.core.PApplet;

import swarm_wars_library.SwarmWars;
import swarm_wars_library.comms.CommsGlobal;
import swarm_wars_library.engine.Health;
import swarm_wars_library.engine.Shooter;
import swarm_wars_library.input.Input;
import swarm_wars_library.map.Map;
import swarm_wars_library.physics.Vector2D;
import swarm_wars_library.sound.SoundMixer; 

import static processing.core.PConstants.DOWN;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import static processing.core.PConstants.UP;


public class PlayerN extends AbstractEntity implements IHealth, IInputShooter,
  IScore, ISound{

  private Health health;
  private Input input;
  private int score;
  private Shooter shooter;
  private int bulletForce = Map.getInstance().getPlayerNBulletForce();
  int oldScore;

  //=========================================================================//
  // Constructor                                                             //
  //=========================================================================//
  public PlayerN(PApplet sketch, ENTITY tag){
    super(tag, Map.getInstance().getPlayerScale());
    this.setLocation(Map.getInstance().getPlayerStartingLocation(this.tag));
    this.health = new Health(this.tag);
    this.input = new Input(this.tag, sketch);
    this.score = 0;
    this.shooter = new Shooter(this.tag, bulletForce,true);
    this.updateCommsPacket();
    this.sendCommsPacket();  
  }

  //=========================================================================//
  // Update method                                                           //
  //=========================================================================//
  @Override
  public void update(){
    if(this.isState(STATE.ALIVE)){
      this.updateHealth();
      this.updateInput();
      this.updateShooter();
      this.updateScore();
      this.updateMissile();
      this.updateState();
    }
    // Comms & explode last
    this.updateCommsPacket();
    this.sendCommsPacket();
    this.updateExplode2Dead();
  }

  //=========================================================================//
  // Comms method                                                            //
  //=========================================================================//
  @Override
  public void updateCommsPacket(){
    this.commsPacket.setHealth(this.getHealth());
    this.commsPacket.setLocation(this.getLocation());
    this.commsPacket.setScore(this.getScore());
    this.commsPacket.setMissileNum(this.shooter.getMissileNum());
    this.commsPacket.setState(this.getState());
    this.commsPacket.setVelocity(this.getVelocity());
    this.commsPacket.setMotherShipHeading(this.getHeading());
    this.commsPacket.setMoveLeft(this.getInputLeft());
    this.commsPacket.setMoveRight(this.getInputRight());
    this.commsPacket.setMoveUp(this.getInputUp());
    this.commsPacket.setMoveDown(this.getInputDown());
  }

  //=========================================================================//
  // Collision method                                                        //
  //=========================================================================//
  @Override
  public void collidedWith(ENTITY tag){
    if (SwarmWars.playNetworkGame && Map.getInstance().getPlayerId() == 2) {
      return;
    }
    if (tag == ENTITY.PLAYER2_MISSILE || tag == ENTITY.PLAYER1_MISSILE) {
      this.takeDamage(30);
    }
    if(tag==ENTITY.HEALTHPACK){
      this.takeDamage(-20);
    }
    else {
      this.takeDamage(5);
    }
  }

  //=========================================================================//
  // Health methods                                                          //
  //=========================================================================//
  @Override
  public void updateHealth(){
    this.health.update();
  }

  @Override
  public int getHealth(){
    return this.health.getCurrentHealth();
  }

  @Override
  public void updateState(){
    if(health.isDead()){
      this.state = STATE.EXPLODE;
    }
  }

  @Override
  public void takeDamage(int damage){
    this.health.takeDamage(damage);
  }

  @Override 
  public void setHealth(int health){
    this.health.setHealth(health);
  }

  //=========================================================================//
  // Input methods                                                           //
  //=========================================================================//
  @Override
  public void updateInput(){
    this.input.update();
    this.setLocation(this.getInputLocation());
    this.setHeading(this.getInputHeading());
  }

  @Override
  public Vector2D getInputLocation(){
    return this.input.getLocation();
  }

  @Override
  public double getInputHeading(){
    return this.input.getHeading();
  }

  @Override
  public int getInputLeft(){
    return this.input.getMoveLeft();
  }

  @Override
  public int getInputRight(){
    return this.input.getMoveRight();
  }

  @Override 
  public int getInputUp(){
    return this.input.getMoveUp();
  }

  @Override 
  public int getInputDown(){
    return this.input.getMoveDown();
  }

  @Override
  public int getInputMouseX(){
    return this.input.getMouseX();
  }

  @Override
  public int getInputMouseY(){
    return this.input.getMouseY();
  }

  @Override
  public int getInputMouseLeft(){
    return this.input.getMouseLeft();
  }

  @Override
  public int getInputMouseRight(){
    return this.input.getMouseRight();
  }

  @Override
  public void setInputUp(int b){
    this.input.setMove(UP, b);
  }

  @Override
  public void setInputDown(int b){
    this.input.setMove(DOWN, b);
  }

  @Override
  public void setInputLeft(int b){
    this.input.setMove(LEFT, b);
  }

  @Override
  public void setInputRight(int b){
    this.input.setMove(RIGHT, b);
  }

  @Override
  public void setInputMouseX(int mouseX) {
    this.input.setMouseX(mouseX);
  }

  @Override
  public void setInputMouseY(int mouseY) {
    this.input.setMouseY(mouseY);
  }

  @Override
  public void setInputMouseLeft(int input) {
    this.input.setMouseLeft(input);
  }

  @Override
  public void setInputMouseRight(int input) {
    this.input.setMouseRight(input);
  }

  //=========================================================================//
  // Input listeners                                                         //
  //=========================================================================//

  @Override
  public void listenKeyPressed(int keyCode) {
    this.input.setMoveBuffer(keyCode, 1);
  }

  @Override
  public void listenKeyReleased(int keyCode) {
    this.input.setMoveBuffer(keyCode, 0);
  }

  @Override
  public void listenMouseReleased() {
    this.input.setMouseLeftBuffer(0);
    this.input.setMouseRightBuffer(0);
  }

  @Override
  public void listenMousePressed(boolean isleft){
    if(isleft){
      this.input.setMouseLeftBuffer(1);
    }
    else {  
      this.input.setMouseRightBuffer(1);
    }
  }

  @Override
  public void updateInputBuffer() {
    this.input.updateBuffer();
  }


  //=========================================================================//
  // Input-Shooter methods                                                   //
  //=========================================================================//
  @Override
  public boolean isInputShoot(){
    if(this.input.getMouseLeft() == 1){
      return true;
    }
    return false;
  }

  public boolean isInputShootM(){
    if(this.input.getMouseRight() == 1){
      return true;
    }
    return false;
  }

  //=========================================================================//
  // Score methods                                                           //
  //=========================================================================//
  public void updateScore(){
    this.setScore(CommsGlobal.get(this.tag.toString())
                             .getPacket(0)
                             .getScore());
  }

  public int getScore(){
    return this.score;
  }

  public void setScore(int score){
    this.score = score;
  }

  //=========================================================================//
  // Shooter methods                                                         //
  //=========================================================================//
  @Override
  public void updateShooter(){
    this.shooter.update();
    this.shoot();
  }

  @Override
  public void shoot(){
    if(isInputShoot()){
      this.shooter.shoot(this.getLocation(), this.getHeading());

      // sound
      SoundMixer.playPlayer1Shoot();
    }
    if(isInputShootM()){
      this.shooter.shootM(this.getLocation(), this.getHeading());
    }
  }

  @Override
  public ArrayList<Bullet> getBullets(){
    return this.shooter.getMagazine();
  }

  public  ArrayList<Missile> getMissiles(){
    return this.shooter.getMagazine1();
  }

  //=========================================================================//
  // Sound methods                                                           //
  //=========================================================================//
  @Override
  public void updateSounds(){
    // TODO add conditional sound
  }

  public  void updateMissile(){

    if(this.getScore()!=oldScore){
      oldScore=this.getScore();
      if(oldScore%30==0){
        this.shooter.addMissile();
      }
    }
    //System.out.println(shooter.getMissileNum());
  }

}
