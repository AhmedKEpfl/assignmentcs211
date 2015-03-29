int HEIGHT_WINDOW = 600;
int WIDTH_WINDOW = 600;
float rotationX = 0;
float rotationY = 0;
float rotationZ = 0;
final static float FACTOR_TW = 4.0;
float rectBarChart = 4;
float rectBarChartWidth = 4;
float tVWidth = BLOCK_HEIGHT * SCALE_X / FACTOR_TW;
float tVHeight = BLOCK_HEIGHT * SCALE_Z / FACTOR_TW;

//Debut

PGraphics dataVisualization;
PGraphics topView;

Mover mover;
void setup() {
  size(WIDTH_WINDOW, HEIGHT_WINDOW, P3D);
  mover = new Mover();
  dataVisualization = createGraphics(width, 100, P2D);
  topView = createGraphics((int)(BLOCK_HEIGHT * SCALE_X / FACTOR_TW), (int)(BLOCK_HEIGHT * SCALE_Z / FACTOR_TW), P2D);
  
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

void drawDataVisualization() {
  dataVisualization.beginDraw();
  dataVisualization.background(255, 204, 153);
  drawTopView();
  
  dataVisualization.endDraw();
}

void drawTopView() {
  pushMatrix();
  dataVisualization.translate(5, 5);
  dataVisualization.pushMatrix();
  dataVisualization.fill(0, 0, 204);
  dataVisualization.stroke(0, 0, 204);
  dataVisualization.rect(0, 0, tVWidth, tVHeight);
  dataVisualization.translate(tVWidth / 2.0, tVHeight / 2.0);
  dataVisualization.fill(255, 204, 153);
  dataVisualization.stroke(255, 204, 153);
  for (int i = 0; i < listeCylindres.size (); i++) {
    dataVisualization.ellipse(listeCylindres.get(i).x / FACTOR_TW, listeCylindres.get(i).z / FACTOR_TW, 
    2 * cylinderBaseSize / FACTOR_TW, 2 * cylinderBaseSize / FACTOR_TW);
  }
  dataVisualization.fill(255, 0, 0);
  dataVisualization.stroke(255, 0, 0);

  dataVisualization.ellipse(location.x / FACTOR_TW, location.z / FACTOR_TW, 
  2 * SPHERE_RADIUS / FACTOR_TW, 2 * SPHERE_RADIUS / FACTOR_TW);
  dataVisualization.popMatrix();
  dataVisualization.translate(tVWidth + 10, 0);


  dataVisualization.stroke(255, 255, 255);
  dataVisualization.fill(255, 204, 153);
  dataVisualization.rect(0, 0, tVWidth - 10, tVHeight + 10);

  dataVisualization.stroke(0);
  dataVisualization.fill(0);
  dataVisualization.textSize(8);
  dataVisualization.text("Score : \n" + score + "\n Velocity : \n " + velocity.mag() + "\n Last Score : \n" + lastScore, 5, 15);
  //dataVisualization.text("Score : \n" + score + "\nVelocity : \n " + velocity.mag() + "\nLast Score : \n" + lastScore, 5, 15);
  dataVisualization.translate(tVWidth, 0);
  dataVisualization.fill(255, 230, 170);
  float heightBarChart = tVHeight * 0.7;
  float widthBarChart = width - 2*tVWidth - 20;
  dataVisualization.rect(0, 0, widthBarChart, heightBarChart);
  
  dataVisualization.pushMatrix();
  dataVisualization.stroke(255, 255, 255);
  dataVisualization.fill(0, 0, 200);
  dataVisualization.translate(4, heightBarChart - rectBarChart);
  if(barChartColumns.size() > widthBarChart / rectBarChartWidth - 1){
    for(int i = 0; i < barChartColumns.size() - 1; i++){
      barChartColumns.set(i, barChartColumns.get(i + 1));
    }
    barChartColumns.remove(barChartColumns.size() - 1);
  }
  
  for(int i = 0; i < barChartColumns.size(); i++){
    dataVisualization.pushMatrix();
    for(int j = 0; j < barChartColumns.get(i) && j < heightBarChart / rectBarChart - 1; j++){
      dataVisualization.rect(0, 0, rectBarChartWidth, rectBarChart);
      dataVisualization.translate(0, -rectBarChart);
    }
    dataVisualization.popMatrix();
    dataVisualization.translate(rectBarChartWidth, 0);  
  }
  
  dataVisualization.popMatrix();
  
  popMatrix();
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
  speed -= e/100;
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

