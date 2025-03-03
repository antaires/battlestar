package swarm_wars_library.graphics;

import processing.core.PApplet;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.TOP;


public class RenderPlayer1Score{

  private PApplet sketch;

  public RenderPlayer1Score(PApplet sketch){
    this.sketch = sketch;
  }

  public void update(int score){
    this.sketch.fill(0, 101, 255);
    this.sketch.textSize(30);
    this.sketch.textAlign(LEFT, TOP);
    this.sketch.text("SCORE: " + score, 5, 5);
  }
}