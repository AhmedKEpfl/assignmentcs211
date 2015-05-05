package cs211;

import processing.core.*;

public class MyProcessingSketch extends PApplet {
	
	public void setup(){
		size(200, 200);
		background(0);
	}
	
	public void draw() {
		stroke(255);
		if(mousePressed) {
			line(mouseX, mouseY, pmouseX, pmouseY);
		}
	}
}
