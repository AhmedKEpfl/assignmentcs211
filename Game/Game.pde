float rotationX = 0;
float rotationY = 0;
float rotationZ = 0;

Mover mover;
void setup() {
  size(600, 600, P3D);
  mover = new Mover();
}
void draw() {
  background(255);
  mover.update();
  mover.checkEdges();
  mover.display();
}

void keyPressed(){
  if(key == CODED){

    /*if(keyCode == UP){
      rotationX -= PI / 180;
      if(rotationX < -PI/ 4){
        rotationX = -PI / 4;
      } 
    } else if(keyCode == DOWN){
      rotationX += PI / 180 * speed;
      if(rotationX > PI / 4){
        rotationX = PI / 4;
      }*/
    } else if(keyCode == LEFT){
      rotationZ -= PI / 180 * speed;
      if(rotationZ < -PI/ 4){
        rotationZ = -PI / 4;       
      } 
    } else if(keyCode == RIGHT){
      rotationZ += PI / 180 * speed;
      if(rotationZ > PI/ 4){
        rotationZ = PI / 4;
       
      }
    }
  }


void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  speed += e;
  if(speed < 1){
    speed = 1;
  }
}

void mouseDragged(){
  rotationX += (PI / 180) * (mouseY - pmouseY);
  if(rotationX > PI / 3){
     rotationX = PI / 3;
  }else if(rotationX < -PI / 3){
     rotationX = - PI/3;
  }
  
  rotationZ += (PI / 180) * (mouseX - pmouseX);
  if(rotationZ > PI / 3){
     rotationZ = PI / 3;
  }else if(rotationZ < -PI / 3){
     rotationZ = - PI/3;
  }
}
