float score = 0;
float lastScore = 0;
final static float COUNTER_BAR_CHART = 5;
int compteur = 0;
final static float AMORTISSEMENT_COLLISION = 0.5;
final static float SPHERE_RADIUS = 15;
final static int BLOCK_HEIGHT = 30; 
final static int SCALE_X = 10;
final static int SCALE_Z = 10;
final static float gravityConstant = 0.1;
float collisionX = BLOCK_HEIGHT * SCALE_X / 2.0;
float collisionZ = BLOCK_HEIGHT * SCALE_Z / 2.0;
float positionScrollX = 2 * tVWidth + 50;
float positionScrollY = HEIGHT_WINDOW - 20;


float cylinderBaseSize = 20;

float cylinderHeight = 50;
int cylinderResolution = 40;

float speed = 1;

float normalForce = 1;
float mu = 0.01;
float frictionMagnitude = normalForce * mu;
PVector friction;
PVector location;
PVector velocity;
PVector acceleration;

ArrayList<PVector> listeCylindres;
ArrayList<Integer> barChartColumns;
PShape openCylinder, roofCylinder;

boolean shift = false;

class Mover {
  //PVector location;
  Mover() {
    location = new PVector(0, 0, 0);
    velocity = new PVector(0, 0, 0);
    acceleration = new PVector(0, 0, 0);
    listeCylindres = new ArrayList<PVector>();
    barChartColumns = new ArrayList<Integer>();
    hs = new HScrollbar(positionScrollX, positionScrollY, 200, 15);
    
    float angle;
    float[] x = new float[cylinderResolution + 1];
    float[] y = new float[cylinderResolution + 1];
    //get the x and y position on a circle for all the sides
    for (int i = 0; i < x.length; i++) {
      angle = (TWO_PI / cylinderResolution) * i;
      x[i] = sin(angle) * cylinderBaseSize;
      y[i] = cos(angle) * cylinderBaseSize;
    }
    openCylinder = createShape();
    openCylinder.beginShape(QUAD_STRIP);
    //draw the border of the cylinder
    for (int i = 0; i < x.length; i++) {
      openCylinder.vertex(x[i], 0, y[i]);
      openCylinder.vertex(x[i], cylinderHeight, y[i]);
    }


    openCylinder.endShape();
    roofCylinder = createShape();
    roofCylinder.beginShape(TRIANGLES);
    for (int i = 0; i < x.length - 1; i++) {
      roofCylinder.vertex(x[i], 0, y[i]);
      roofCylinder.vertex(0, 0, 0);
      roofCylinder.vertex(x[i + 1], 0, y[i + 1]);
    }
    roofCylinder.endShape();
  }
  void update() {
    friction = velocity.get();
    friction.mult(-1);
    friction.normalize();
    friction.mult(frictionMagnitude);
    if(compteur > COUNTER_BAR_CHART){
      barChartColumns.add((int)score);
      compteur = -1;
    }
    compteur++;
    acceleration.x = sin(rotationZ) * gravityConstant;
    acceleration.z = -sin(rotationX) * gravityConstant;

    acceleration.add(friction);

    velocity.add(acceleration);

    location.add(velocity);
    rectBarChartWidth = RECT_BAR_CHART_WIDTH_INITIAL * hs.getPos();
  }
  void display() {
    lights();
    pushMatrix();
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
    for (int i = 0; i < listeCylindres.size (); i++) {
      addCylinder(listeCylindres.get(i));
    }

    translate(location.x, -BLOCK_HEIGHT, location.z);
    stroke(100, 100, 100);
    fill(200, 200, 200);
    sphere(BLOCK_HEIGHT / 2.0);
    popMatrix();
    drawDataVisualization();
    image(dataVisualization, 0, height - dataVisualization.height);
    hs.update();
    hs.display();
  }
  void checkEdges() {
    if (location.x >= collisionX - BLOCK_HEIGHT / 2) {
      if (velocity.x > 0.1) {
        score -= velocity.mag();
        lastScore = -velocity.mag();
      }
      location.x = collisionX - BLOCK_HEIGHT / 2;

      velocity.x = -velocity.x * AMORTISSEMENT_COLLISION;
    } else if (location.x <= -collisionX + BLOCK_HEIGHT / 2) {
      if (velocity.x < -0.1) {
        score -= velocity.mag();
        lastScore = -velocity.mag();
      }
      location.x = -collisionX + BLOCK_HEIGHT / 2;
      velocity.x = -velocity.x * AMORTISSEMENT_COLLISION;
    }
    if (location.z >= collisionZ - BLOCK_HEIGHT / 2) {
      if (velocity.z > 0.1) {
        score -= velocity.mag();
        lastScore = -velocity.mag();
      }
      location.z = collisionZ - BLOCK_HEIGHT / 2;
      velocity.z = -velocity.z * AMORTISSEMENT_COLLISION;
    } else if (location.z <= -collisionZ + BLOCK_HEIGHT / 2) {
      if (velocity.z < -0.1) {
        score -= velocity.mag();
        lastScore = -velocity.mag();
      }
      location.z = -collisionZ + BLOCK_HEIGHT / 2;
      velocity.z = -velocity.z * AMORTISSEMENT_COLLISION;
    }
  }

  void displayShift() {
    lights();

    translate(width / 2, height / 2, 0);
    rotateX(-PI / 2);
    pushMatrix();
    scale(SCALE_X, 1, SCALE_Z);

    stroke(50, 50, 50);
    fill(50, 50, 50);
    box(BLOCK_HEIGHT);
    popMatrix();
    for (int i = 0; i < listeCylindres.size (); i++) {
      addCylinder(listeCylindres.get(i));
    }
    translate(location.x, -BLOCK_HEIGHT, location.z);
    stroke(100, 100, 100);
    fill(200, 200, 200);
    sphere(BLOCK_HEIGHT / 2.0);


    /*float rotX = rotationX;
     float rotY = rotationY;
     float rotZ = rotationZ;
     rotationX = - PI / 2.0;
     rotationY = 0;
     rotationZ = 0;
     display();
     rotationX = rotX;
     rotationY = rotY;
     rotationZ = rotZ;*/
  }

  void checkCylinderCollision() {
    int cylinderCollision = -1;
    PVector n = new PVector(0, 0, 0);
    PVector nCopy = new PVector(0, 0, 0);

    for (int i = 0; i < listeCylindres.size (); i++) {
      if (cylinderBaseSize + BLOCK_HEIGHT / 2.0 >= listeCylindres.get(i).dist(location)) {
        score += velocity.mag();
        lastScore = velocity.mag();
        cylinderCollision = i;
        n.x = (location.x - (listeCylindres.get(i)).x);
        n.y = (location.y - (listeCylindres.get(i)).y);
        n.z = (location.z - (listeCylindres.get(i)).z);
        n.normalize();
        nCopy = n.get();

        n.mult(2* (velocity.dot(n)));
        velocity.sub(n);
        nCopy.mult(cylinderBaseSize+ BLOCK_HEIGHT / 2.0);
        location.set(nCopy);
        location.add(listeCylindres.get(i));

        //n * r +  + listeCylindre.get(i)
      }
    }
  }
}

void addCylinder(PVector point) {
  pushMatrix();
  translate(0, - 2 * BLOCK_HEIGHT, 0);
  translate(point.x, 0, point.z);
  shape(openCylinder);
  shape(roofCylinder);
  translate(0, cylinderHeight, 0);
  shape(roofCylinder);
  popMatrix();
}

