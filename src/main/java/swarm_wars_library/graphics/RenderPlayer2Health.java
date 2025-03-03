package swarm_wars_library.graphics;

import processing.core.PApplet;

public class RenderPlayer2Health{

  private PApplet sketch;
  private float healthBarLength = 100;
  private float healthBarOffset = 15;
  private float healthBarLower = 65;

  public RenderPlayer2Health(PApplet sketch){
    this.sketch = sketch;
  }

  public void update(int health){
    //draw border
    this.sketch.rectMode(0);
    this.sketch.stroke(20, 100, 0);
    this.sketch.fill(130, 6, 29);
    this.sketch.rect(
      sketch.width - this.healthBarLength - this.healthBarOffset, 
      this.healthBarLower, 
      this.healthBarLength, 
      30);
    //draw health
    this.sketch.stroke(130, 6, 29);
    this.sketch.fill(255, 22, 65);
    this.sketch.rect(
      sketch.width - this.healthBarLength - this.healthBarOffset, 
      this.healthBarLower,
      health, 
      30
    );
  }
}