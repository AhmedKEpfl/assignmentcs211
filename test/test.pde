PShape tree;
float rotX = 0;
void setup() {
  size(500, 500, P3D);
  // The files "simpleTree.obj" and "simpleTree.mtl" must be in the data folder
  // of the current sketch to load successfully, download them from Moodle
  tree = loadShape("simpleTree.obj");
  tree.scale(40);
}
void draw() {
  background(255);
  translate(width/2, height/2);
  rotX += 0.02;
  rotateX(rotX);
  shape(tree);
}

