float heightBox = 150;
float widthBox = 100;
float depthBox = 100;
float scaleHeightBox = 1;
float rotY = 0;
float rotX = 0;//PI / 2;



class My2DPoint {
  float x;
  float y;
  My2DPoint(float x, float y) {
    this.x = x;
    this.y = y;
  }
}

class My3DPoint {
  float x;
  float y;
  float z;
  My3DPoint(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}

class My2DBox {
  My2DPoint[] s;
  My2DBox(My2DPoint[] s) {
    this.s = s;
  }
  
  void render() {
    line(s[0].x, s[0].y, s[1].x, s[1].y);
    line(s[1].x, s[1].y, s[2].x, s[2].y);
    line(s[2].x, s[2].y, s[3].x, s[3].y);
    line(s[3].x, s[3].y, s[0].x, s[0].y);
    line(s[4].x, s[4].y, s[5].x, s[5].y);
    line(s[5].x, s[5].y, s[6].x, s[6].y);
    line(s[6].x, s[6].y, s[7].x, s[7].y);
    line(s[7].x, s[7].y, s[4].x, s[4].y);
    line(s[0].x, s[0].y, s[4].x, s[4].y);
    line(s[3].x, s[3].y, s[7].x, s[7].y);
    line(s[1].x, s[1].y, s[5].x, s[5].y);
    line(s[2].x, s[2].y, s[6].x, s[6].y);
  }
}

class My3DBox {
My3DPoint[] p;
My3DBox(My3DPoint origin, float dimX, float dimY, float dimZ){
  float x = origin.x;
  float y = origin.y;
  float z = origin.z;
  this.p = new My3DPoint[]{new My3DPoint(x,y+dimY,z+dimZ),
                            new My3DPoint(x,y,z+dimZ),
                            new My3DPoint(x+dimX,y,z+dimZ),
                            new My3DPoint(x+dimX,y+dimY,z+dimZ),
                            new My3DPoint(x,y+dimY,z),
                            origin,
                            new My3DPoint(x+dimX,y,z),
                            new My3DPoint(x+dimX,y+dimY,z)
                          };
}
My3DBox(My3DPoint[] p) {
    this.p = p;
  }
}

My2DPoint projectPoint(My3DPoint eye, My3DPoint p) {
  float xp = (eye.z * (p.x - eye.x) / (eye.z - p.z));
  float yp = (eye.z * (p.y - eye.y) / (eye.z - p.z));
  return new My2DPoint(xp, yp);
}

My2DBox projectBox(My3DPoint eye, My3DBox box) {
    My2DPoint[] points2D = new My2DPoint[8]; 
    for(int i = 0; i < points2D.length; i++){
      points2D[i] = projectPoint(eye, box.p[i]);
    }
    return new My2DBox(points2D);
}

float[] homogeneous3DPoint (My3DPoint p) {
  float[] result = {p.x, p.y, p.z, 1};
  return result;
}

float[][] rotateXMatrix(float angle) {
  return (new float[][] {{1, 0, 0, 0},
                         {0, cos(angle), sin(angle), 0},
                         {0, -sin(angle), cos(angle), 0},
                         {0, 0, 0, 1}});
}

float[][] rotateYMatrix(float angle) {
  return (new float[][] {{0, 1, 0, 0},
                         {cos(angle), 0, sin(angle), 0},
                         {-sin(angle), 0, cos(angle), 0},
                         {0, 0, 0, 1}});  
}

float[][] rotateZMatrix(float angle) {
  return (new float[][] {{0, 0, 1, 0},
                         {cos(angle), sin(angle), 0, 0},
                         {-sin(angle), cos(angle), 0, 0},
                         {0, 0, 0, 1}});
}

float[][] scaleMatrix(float x, float y, float z) {
  return (new float[][] {{x, 0, 0, 0},
                         {0, y, 0, 0},
                         {0, 0, z, 0},
                         {0 ,0 ,0, 1}});
}

float[][] translationMatrix(float x, float y, float z) {
  return (new float[][] {{1, 0, 0, x},
                         {0, 1, 0, y},
                         {0, 0, 1, z},
                         {0 ,0 ,0, 1}});
}

float[] matrixProduct(float[][] a, float[] b) {
  float[] product = new float[b.length];
  for(int i = 0; i < product.length; i++){
     product[i] = 0;
     for(int j = 0; j < a[0].length; j++){
       product[i] += a[i][j] * b[j];
     }
  }
  return product;
}

My3DBox transformBox(My3DBox box, float[][] transformMatrix){
  My3DPoint[] pointsOriginaux = box.p;
  My3DPoint[] pointsFinaux = new My3DPoint[8];
  for(int i = 0; i < 8; i++){
    pointsFinaux[i] = euclidian3DPoint(matrixProduct(transformMatrix, homogeneous3DPoint(pointsOriginaux[i])));
  }
  return new My3DBox(pointsFinaux);
}

My3DPoint euclidian3DPoint (float[] a) {
  My3DPoint result = new My3DPoint(a[0]/a[3], a[1]/a[3], a[2]/a[3]);
  return result;
}

void setup () {
  size(1000,1000,P2D);
}


void draw() {
background(255, 255, 255);
  My3DPoint eye = new My3DPoint(0, 0, -5000);
  My3DPoint origin = new My3DPoint(0, 0, 0);
  My3DBox input3DBox = new My3DBox(origin, widthBox, heightBox * scaleHeightBox, depthBox);
  //rotated and translated
  float[][] translateOrigin = translationMatrix(-widthBox / 2, - heightBox * scaleHeightBox / 2, -depthBox / 2);
  float[][] transform1 = translationMatrix(width / 2, height / 2, 0);
  float[][] transform2 = scaleMatrix(1, scaleHeightBox, 1);
  float[][] rotationY = rotateYMatrix(rotY);
  float[][] rotationX = rotateXMatrix(rotX);
  input3DBox = transformBox(input3DBox, translateOrigin);
  
  input3DBox = transformBox(input3DBox, transform2);
  input3DBox = transformBox(input3DBox, rotationX);
  input3DBox = transformBox(input3DBox, rotationY);
  input3DBox = transformBox(input3DBox, transform1);
  projectBox(eye, input3DBox).render();
}

void mouseDragged(){
  float quantity = mouseY - pmouseY ;
  scaleHeightBox -= quantity / 100;
  if(scaleHeightBox < 0){
    scaleHeightBox = 0;
  }
}

void keyPressed(){
  if(key == CODED){
    if(keyCode == RIGHT){
      rotX += PI / 180;
    } else if(keyCode == LEFT){
      rotX -= PI / 180;
    } 
    
    if(keyCode == UP){
      rotY += PI / 180;
    } else if(keyCode == DOWN){
      rotY -= PI / 180;
    }
  }
}
