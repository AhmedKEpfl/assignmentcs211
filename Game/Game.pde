final static int BLOCK_HEIGHT = 30; 
final static int SCALE_X = 10;
final static int SCALE_Z = 10;
float rotationX = 0;
float rotationY = 0;
float rotationZ = 0;

float collisionX = BLOCK_HEIGHT * SCALE_X / 2.0;
float collisionZ = BLOCK_HEIGHT * SCALE_Z / 2.0;
float vitesseX = 0;
float vitesseY = 0;
float vitesseZ = 0;
float accelerationX = 0; 
float accelerationY = 0; 
float accelerationZ = 0; 
float positionX = 0;
float positionY = 0;
float positionZ = 0;

void setup() {
  size(500, 500, P3D);
}

void draw(){
  background(200, 200, 200);
  rotateX(-PI / 8);
  translate(width / 2, height / 2 + BLOCK_HEIGHT);
  rotateX(rotationX);
  rotateY(rotationY);
  rotateZ(rotationZ);
  pushMatrix();
  scale(SCALE_X, 1, SCALE_Z);
  box(BLOCK_HEIGHT);
  popMatrix();
  accelerationX = sin(rotationZ) / 5;
  accelerationZ = -sin(rotationX) / 5;
  vitesseX += accelerationX;
  vitesseZ += accelerationZ;
  positionX += vitesseX;
  if(positionX >= collisionX - BLOCK_HEIGHT / 2){
    positionX = collisionX - BLOCK_HEIGHT / 2;
    vitesseX = -vitesseX / 2.0;
  }else if(positionX <= -collisionX + BLOCK_HEIGHT / 2){
    positionX = -collisionX + BLOCK_HEIGHT / 2;
    vitesseX = -vitesseX / 2.0;
  }
  positionY += vitesseY;
  
  positionZ += vitesseZ;
  if(positionZ >= collisionZ - BLOCK_HEIGHT / 2){
    positionZ = collisionZ - BLOCK_HEIGHT / 2;
    vitesseZ = -vitesseZ / 2.0;
  }else if(positionZ <= -collisionZ + BLOCK_HEIGHT / 2){
    positionZ = -collisionZ + BLOCK_HEIGHT / 2;
    vitesseZ = -vitesseZ / 2.0;
  }
  
  translate(positionX, -BLOCK_HEIGHT, positionZ);
  sphere(BLOCK_HEIGHT / 2.0);
  
}

void keyPressed(){
  if(key == CODED){
    /*if(keyCode == UP){
      rotationX -= PI / 180;
      if(rotationX < -PI/ 4){
        rotationX = -PI / 4;
      } 
    } else if(keyCode == DOWN){
      rotationX += PI / 180;
      if(rotationX > PI / 4){
        rotationX = PI / 4;
      }*/
    if(keyCode == LEFT){
      rotationY -= PI / 180;
    } else if(keyCode == RIGHT){
      rotationY += PI / 180;
      
    }
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
