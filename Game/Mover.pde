final static int BLOCK_HEIGHT = 30; 
final static int SCALE_X = 10;
final static int SCALE_Z = 10;
final static float gravityConstant = 0.1;
float collisionX = BLOCK_HEIGHT * SCALE_X / 2.0;
float collisionZ = BLOCK_HEIGHT * SCALE_Z / 2.0;

float speed = 1;

float normalForce = 1;
float mu = 0.01;
float frictionMagnitude = normalForce * mu;
PVector friction;

class Mover {
  PVector location;
  PVector velocity;
  PVector acceleration;
  Mover() {
    location = new PVector(0, 0, 0);
    velocity = new PVector(0, 0, 0);
    acceleration = new PVector(0, 0, 0);
  }
  void update() {
    friction = velocity.get();
    friction.mult(-1);
    friction.normalize();
    friction.mult(frictionMagnitude);

    acceleration.x = sin(rotationZ) * gravityConstant;
    acceleration.z = -sin(rotationX) * gravityConstant;

    acceleration.add(friction);

    velocity.add(acceleration);

    location.add(velocity);
  }
  void display() {
    lights();
    stroke(50, 50, 50);
    fill(50, 50, 50);
    background(200, 200, 200);
    text("rotationX: " + degrees(rotationX) + 
      "  RotationZ: " + degrees(rotationZ) + 
      "  Speed: " + speed, 10, 10);
    translate(width / 2, height / 2 + BLOCK_HEIGHT);
    rotateX(rotationX);
    rotateY(rotationY);
    rotateZ(rotationZ);
    pushMatrix();
    scale(SCALE_X, 1, SCALE_Z);
    box(BLOCK_HEIGHT);
    popMatrix();
    translate(location.x, -BLOCK_HEIGHT, location.z);
    //stroke(100, 100, 100);
    sphere(BLOCK_HEIGHT / 2.0);
  }
  void checkEdges() {
    if (location.x >= collisionX - BLOCK_HEIGHT / 2) {
      location.x = collisionX - BLOCK_HEIGHT / 2;
      velocity.x = -velocity.x / 2.0;
    } else if (location.x <= -collisionX + BLOCK_HEIGHT / 2) {
      location.x = -collisionX + BLOCK_HEIGHT / 2;
      velocity.x = -velocity.x / 2.0;
    }
    if (location.z >= collisionZ - BLOCK_HEIGHT / 2) {
      location.z = collisionZ - BLOCK_HEIGHT / 2;
      velocity.z = -velocity.z / 2.0;
    } else if (location.z <= -collisionZ + BLOCK_HEIGHT / 2) {
      location.z = -collisionZ + BLOCK_HEIGHT / 2;
      velocity.z = -velocity.z / 2.0;
    }
  }
}

