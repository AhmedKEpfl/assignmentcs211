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
  if (shift) {
    mover.displayShift();
  } else {
    mover.update();
    mover.checkEdges();
    mover.checkCylinderCollision();
    mover.display();
  }
}

void keyPressed() {
  if (key == CODED) {
    if (keyCode == SHIFT) {
      shift = true;
    }
  }
}

void keyReleased() {
  if (key == CODED) {
    if (keyCode == SHIFT) {
      shift = false;
    }
  }
}

void mousePressed() {
  if (shift) {
    PVector p = new PVector(mouseX - width / 2, 0, mouseY - height / 2);
    boolean collisionCyl = cylinderBaseSize + BLOCK_HEIGHT / 2.0 >= p.dist(location);
    if (mouseX <= width / 2 + BLOCK_HEIGHT * SCALE_X / 2 && mouseX >= width / 2 - BLOCK_HEIGHT * SCALE_X / 2 &&
      mouseY <= height / 2 + BLOCK_HEIGHT * SCALE_Z / 2 && mouseY >= height / 2 - BLOCK_HEIGHT * SCALE_Z / 2 && !collisionCyl) {
      listeCylindres.add(p);
    }
  }
}


void mouseWheel(MouseEvent event) {
  float e = event.getCount();
  speed += e/100;
  if (speed < 0.25) {
    speed = 0.25;
  }
  if (speed > 2) {
    speed = 2;
  }
}

void mouseDragged() {
  rotationX += (PI / 180) * (mouseY - pmouseY) * speed;
  if (rotationX > PI / 3) {
    rotationX = PI / 3;
  } else if (rotationX < -PI / 3) {
    rotationX = - PI/3;
  }

  rotationZ += (PI / 180) * (mouseX - pmouseX) * speed;
  if (rotationZ > PI / 3) {
    rotationZ = PI / 3;
  } else if (rotationZ < -PI / 3) {
    rotationZ = - PI/3;
  }
}

