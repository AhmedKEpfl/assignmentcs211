package cs211;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class ImageProcessing extends PApplet {
	HScrollbar thresholdBarHue, thresholdBarSaturation;

	PImage img, houghImg, result;
	final static int HEIGHT = 600, WIDTH_IMG = 800, WIDTH_HOUGH = 600;
	final static float SCALE = 0.5f;

	public void setup() {
		size((int)((2*WIDTH_IMG + WIDTH_HOUGH) * SCALE), (int)(HEIGHT * SCALE));
		// thresholdBarHue = new HScrollbar(this, 0, 580, 800, 20);
		// thresholdBarSaturation = new HScrollbar(this, 0, 560, 800, 20);
		// ...


		img = loadImage("board3.jpg");
		result = new PImage(img.width, img.height);

		result = sobel(gaussianBlur(detectGreen(img), 99.0f));
		
		
		/*
		 * PImage afterSobel = sobel(result); image(afterSobel, 0, 0);
		 */
		ArrayList<PVector> lines = hough(result);
		
		

		QuadGraph quadGraph = new QuadGraph();
		quadGraph.build(lines, result.width, result.height);
		List<int[]> quads = quadGraph.findCycles();
		List<int[]> afterFilterQuads = new ArrayList<int[]>();
		for(int[] quad: quads){
			PVector l1 = lines.get(quad[0]);
			PVector l2 = lines.get(quad[1]);
			PVector l3 = lines.get(quad[2]);
			PVector l4 = lines.get(quad[3]);
			
			PVector c12 = intersection(l1, l2);
			PVector c23 = intersection(l2, l3);
			PVector c34 = intersection(l3, l4);
			PVector c41 = intersection(l4, l1);
			System.out.println("Size of quads before filter : " + quads.size());
			if (QuadGraph.isConvex(c12, c23, c34, c41) && QuadGraph.validArea(c12, c23, c34, c41, result.width*result.height, 0) &&
					QuadGraph.nonFlatQuad(c12, c23, c34, c41)) {
				afterFilterQuads.add(quad);
			}
			System.out.println("Size of quads after filter: " + afterFilterQuads.size());
		}
		
		for (int[] quad : afterFilterQuads) {
			PVector l1 = lines.get(quad[0]);
			PVector l2 = lines.get(quad[1]);
			PVector l3 = lines.get(quad[2]);
			PVector l4 = lines.get(quad[3]);

			// (intersection() is a simplified version of the
			// intersections() method you wrote last week, that simply
			// return the coordinates of the intersection between 2 lines)
			PVector c12 = intersection(l1, l2);
			PVector c23 = intersection(l2, l3);
			PVector c34 = intersection(l3, l4);
			PVector c41 = intersection(l4, l1);
			//if (QuadGraph.isConvex(l1, l2, l3, l4) && QuadGraph.validArea(l1, l2, l3, l4, 800*600, 0) &&
				//	QuadGraph.nonFlatQuad(l1, l2, l3, l4)) {

				// Choose a random, semi-transparent colour
				Random random = new Random();
				fill(color(min(255, random.nextInt(300)),
						min(255, random.nextInt(300)),
						min(255, random.nextInt(300)), 50));
				quad(c12.x, c12.y, c23.x, c23.y, c34.x, c34.y, c41.x, c41.y);
			//}
		}

		getIntersections(lines);
		img.resize((int)(WIDTH_IMG * SCALE), 0);
		image(img, 0, 0);
		houghImg.resize((int)(WIDTH_IMG * SCALE), 0);
		image(houghImg, (WIDTH_HOUGH * SCALE), 0);
		result.resize((int)(WIDTH_IMG * SCALE), 0);
		image(result, (WIDTH_IMG + WIDTH_HOUGH) * SCALE, 0);
		// result.updatePixels();
		
		noLoop(); // you must comment out noLoop()!
	}

	/*public void draw(){
		image(img, 0, 0);
		image(result, WIDTH_IMG, 0);
	}*/

	public void changeImageMaxBright(PImage src, PImage dst) {
		dst.loadPixels();
		for (int i = 0; i < src.width * src.height; i++) {
			if (brightness(src.pixels[i]) > 25) {
				dst.pixels[i] = Color.white.getRGB();
			} else {
				dst.pixels[i] = Color.black.getRGB();
			}

		}
		dst.updatePixels();
	}

	public PVector intersection(PVector l1, PVector l2) {
		float r1 = l1.x;
		float phi1 = l1.y;
		float r2 = l2.x;
		float phi2 = l2.y;
		double d = Math.cos(phi2) * Math.sin(phi1) - Math.cos(phi1)
				* Math.sin(phi2);

		float x = (float) ((r2 * Math.sin(phi1) - r1 * Math.sin(phi2)) / d);
		float y = (float) ((-r2 * Math.cos(phi1) + r1 * Math.cos(phi2)) / d);

		return new PVector(x, y);
	}

	public ArrayList<PVector> getIntersections(List<PVector> lines) {
		ArrayList<PVector> intersections = new ArrayList<PVector>();
		for (int i = 0; i < lines.size() - 1; i++) {
			PVector line1 = lines.get(i);
			float r1 = line1.x;
			float phi1 = line1.y;
			for (int j = i + 1; j < lines.size(); j++) {
				PVector line2 = lines.get(j);
				// compute the intersection and add it to 'intersections'
				float r2 = line2.x;
				float phi2 = line2.y;
				double d = Math.cos(phi2) * Math.sin(phi1) - Math.cos(phi1)
						* Math.sin(phi2);

				float x = (float) ((r2 * Math.sin(phi1) - r1 * Math.sin(phi2)) / d);
				float y = (float) ((-r2 * Math.cos(phi1) + r1 * Math.cos(phi2)) / d);
				// draw the intersection

				intersections.add(new PVector(x, y));
				fill(255, 128, 0);
				ellipse((float) x, (float) y, 10, 10);
			}
		}
		return intersections;
	}

	public ArrayList<PVector> hough(PImage edgeImg) {
		float discretizationStepsPhi = 0.06f;
		float discretizationStepsR = 2.5f;
		// dimensions of the accumulator
		int phiDim = (int) (Math.PI / discretizationStepsPhi);
		int rDim = (int) (((edgeImg.width + edgeImg.height) * 2 + 1) / discretizationStepsR);
		// our accumulator (with a 1 pix margin around)
		int[] accumulator = new int[(phiDim + 2) * (rDim + 2)];
		// Fill the accumulator: on edge points (ie, white pixels of the edge
		// image), store all possible (r, phi) pairs describing lines going
		// through the point.
		for (int y = 0; y < edgeImg.height; y++) {
			for (int x = 0; x < edgeImg.width; x++) {
				// Are we on an edge?
				if (brightness(edgeImg.pixels[y * edgeImg.width + x]) != 0) {
					// ...determine here all the lines (r, phi) passing through
					// pixel (x,y), convert (r,phi) to coordinates in the
					// accumulator, and increment accordingly the accumulator.
					float r = 0;
					for (int i = 0; i < phiDim; i++) {
						float phi = i * discretizationStepsPhi;
						r = (float) (x * Math.cos(phi) + y * Math.sin(phi));
						int numPhi = (int) (phi / discretizationStepsPhi);
						int numR = (int) (r / discretizationStepsR)
								+ (rDim - 1) / 2;
						accumulator[(numPhi + 1) * (rDim + 2) + numR + 1] += 1;
					}
				}
			}
		}
		
		houghImg = createImage(rDim + 2, phiDim + 2, ALPHA);
		for (int i = 0; i < accumulator.length; i++) {
		houghImg.pixels[i] = color(min(255, accumulator[i]));
		}
		houghImg.updatePixels();
		houghImg.resize((int)(WIDTH_HOUGH * SCALE), (int)(HEIGHT * SCALE));
		

		/*
		 * PImage houghImg = createImage(rDim + 2, phiDim + 2, ALPHA); for (int
		 * i = 0; i < accumulator.length; i++) { houghImg.pixels[i] =
		 * color(min(255, accumulator[i])); } houghImg.updatePixels();
		 * houghImg.resize(800, 600); image(houghImg, 0, 0);
		 */

		ArrayList<Integer> bestCandidates = new ArrayList<Integer>();
		ArrayList<PVector> arrayPVector = new ArrayList<PVector>();// the array
																	// list
																	// returned
																	// by the
																	// method
																	// hough

		// ...
		// size of the region we search for a local maximum
		int neighbourhood = 10;
		// only search around lines with more that this amount of votes
		// (to be adapted to your image)
		int minVotes = 100;
		int nLines = 5;
		for (int accR = 0; accR < rDim; accR++) {
			for (int accPhi = 0; accPhi < phiDim; accPhi++) {
				// compute current index in the accumulator
				int idx = (accPhi + 1) * (rDim + 2) + accR + 1;
				if (accumulator[idx] > minVotes) {
					boolean bestCandidate = true;
					// iterate over the neighbourhood
					for (int dPhi = -neighbourhood / 2; dPhi < neighbourhood / 2 + 1; dPhi++) {
						// check we are not outside the image
						if (accPhi + dPhi < 0 || accPhi + dPhi >= phiDim)
							continue;
						for (int dR = -neighbourhood / 2; dR < neighbourhood / 2 + 1; dR++) {
							// check we are not outside the image
							if (accR + dR < 0 || accR + dR >= rDim)
								continue;
							int neighbourIdx = (accPhi + dPhi + 1) * (rDim + 2)
									+ accR + dR + 1;
							if (accumulator[idx] < accumulator[neighbourIdx]) {
								// the current idx is not a local maximum!
								bestCandidate = false;
								break;
							}
						}
						if (!bestCandidate)
							break;
					}
					if (bestCandidate) {
						// the current idx *is* a local maximum
						bestCandidates.add(idx);
					}
				}
			}
		}

		Collections.sort(bestCandidates, new HoughComparator(accumulator));
		// bestCandidates is now sorted by most voted lines.
		if (bestCandidates.size() >= nLines) {
			for (int i = 0; i < nLines; i++) {
				int idx = (int) (bestCandidates.get(i));
				int accPhi = (int) (idx / (rDim + 2)) - 1;
				int accR = idx - (accPhi + 1) * (rDim + 2) - 1;
				float r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR;
				float phi = accPhi * discretizationStepsPhi;
				arrayPVector.add(new PVector(r, phi));
				// Cartesian equation of a line: y = ax + b
				// in polar, y = (-cos(phi)/sin(phi))x + (r/sin(phi))
				// => y = 0 : x = r / cos(phi)
				// => x = 0 : y = r / sin(phi)
				// compute the intersection of this line with the 4 borders of
				// the image
				int x0 = 0;
				int y0 = (int) (r / sin(phi));
				int x1 = (int) (r / cos(phi));
				int y1 = 0;
				int x2 = edgeImg.width;
				int y2 = (int) (-cos(phi) / sin(phi) * x2 + r / sin(phi));
				int y3 = edgeImg.width;
				int x3 = (int) (-(y3 - r / sin(phi)) * (sin(phi) / cos(phi)));
				// Finally, plot the lines
				stroke(204, 102, 0);
				if (y0 > 0) {
					if (x1 > 0)
						line(x0 * SCALE, y0 * SCALE, x1 * SCALE, y1 * SCALE);
					else if (y2 > 0)
						line(x0 * SCALE, y0 * SCALE, x2 * SCALE , y2 * SCALE);
					else
						line(x0 * SCALE, y0 * SCALE , x3 * SCALE, y3 * SCALE);
				} else {
					if (x1 > 0) {
						if (y2 > 0)
							line(x1 * SCALE, y1 * SCALE, x2 * SCALE, y2 * SCALE);
						else
							line(x1 * SCALE, y1 * SCALE, x3 * SCALE , y3 * SCALE);
					} else
						line(x2 * SCALE , y2 * SCALE, x3 * SCALE, y3 * SCALE);
				}
			}
		}
		return arrayPVector;

		// return new ArrayList<PVector>();
	}

	public PImage gaussianBlur(PImage img, float weight) {
		float[][] kernel = { { 9, 12, 9 }, { 12, 15, 12 }, { 9, 12, 9 } };
		return convoluteWithKernel(img, kernel, weight);
	}

	public PImage sobel(PImage img) {
		float[][] hKernel = { { 0, 1, 0 }, { 0, 0, 0 }, { 0, -1, 0 } };
		float[][] vKernel = { { 0, 0, 0 }, { 1, 0, -1 }, { 0, 0, 0 } };
		PImage result = createImage(img.width, img.height, ALPHA);
		// clear the image
		for (int i = 0; i < img.width * img.height; i++) {
			result.pixels[i] = color(0);
		}
		float max = 0;
		float[] buffer = new float[img.width * img.height];
		// *************************************
		// Implement here the double convolution
		// *************************************

		for (int y = 0; y < img.height; y++) {
			for (int x = 0; x < img.width; x++) {
				float sum_h = 0;
				float sum_v = 0;

				for (int i = clamped(0, img.height, y - 1), k = 0; i <= clamped(
						0, img.height, y + 1); i++, k++) {
					for (int j = clamped(0, img.width, x - 1), m = 0; j <= clamped(
							0, img.width, x + 1); j++, m++) {
						sum_h += brightness(img.pixels[i * img.width + j])
								* hKernel[k][m];
						sum_v += brightness(img.pixels[i * img.width + j])
								* vKernel[k][m];
					}
				}
				/*
				 * int N = 3; for (int j = 0; j < N; j++){ for(int i = 0; i < N;
				 * i++){ int iAdjusted = i-N/2; int jAdjusted = j-N/2; if (x +
				 * iAdjusted >= 0 && x + iAdjusted < img.width && y + jAdjusted
				 * >= 0 && y + jAdjusted < img.height){ //on prend la brightness
				 * avec la fonction processing float bright =
				 * brightness((img.pixels[(y + jAdjusted)*img.width + x +
				 * iAdjusted])); sum_h += bright*hKernel[j][i]; sum_v +=
				 * bright*vKernel[j][i]; } } }
				 */
				float sum = sqrt(pow(sum_h, 2) + pow(sum_v, 2));
				if (sum > max) {
					max = sum;
				}
				buffer[y * img.width + x] = sum;
			}
		}
		// System.out.println(max);
		for (int y = 2; y < img.height - 2; y++) { // Skip top and bottom edges
			for (int x = 2; x < img.width - 2; x++) { // Skip left and right
				if (buffer[y * img.width + x] > (int) (max * 0.3f)) { // 30% of
																		// the
																		// max
					result.pixels[y * img.width + x] = color(255);
				} else {
					result.pixels[y * img.width + x] = color(0);
				}
			}
		}
		return result;
	}

	/*
	 * public PImage sobel(PImage img) { float[][] hKernel = { {0, 1, 0 }, {0,
	 * 0, 0 }, {0, -1, 0 }}; float[][] vKernel = { {0, 0, 0 }, {1, 0, -1 }, {0,
	 * 0, 0 }}; PImage result = createImage(img.width, img.height, ALPHA);
	 * 
	 * //clear the image for (int i = 0; i < img.width * img.height; i++){
	 * result.pixels[i] = color(0); }
	 * 
	 * float max = 0; float[] buffer = new float[img.width* img.height]; int N =
	 * 3; for (int y = 0; y < img.height; y ++){ for (int x = 0; x < img.width;
	 * x++){ int hSum = 0; int vSum = 0;
	 * 
	 * for (int j = 0; j < N; j++){ for(int i = 0; i < N; i++){ int iAdjusted =
	 * i-N/2; int jAdjusted = j-N/2; if (x + iAdjusted >= 0 && x + iAdjusted <
	 * img.width && y + jAdjusted >= 0 && y + jAdjusted < img.height){ //on
	 * prend la brightness avec la fonction processing float bright =
	 * brightness((img.pixels[(y + jAdjusted)*img.width + x + iAdjusted])); hSum
	 * += bright*hKernel[j][i]; vSum += bright*vKernel[j][i]; } } } float sum =
	 * sqrt(pow(hSum, 2) + pow(vSum, 2)); buffer[y*img.width + x] = sum; if (sum
	 * > max){ max = sum; } } } for(int y = 2; y < img.height -2; y++){ for (int
	 * x = 2; x <img.width -2; x++){ if (buffer[y *img.width + x] > (int)(max *
	 * 0.3f)) { result.pixels[y*img.width + x] = color(255); }else {
	 * result.pixels[y * img.width + x] = color(0); } } } return result; }
	 */
	public PImage convoluteWithKernel(PImage img, float[][] kernel, float weight) {
		// create a greyscale image (type: ALPHA) for output
		PImage result = createImage(img.width, img.height, ALPHA);
		// kernel size N = 3
		//
		// for each (x,y) pixel in the image:
		// - multiply intensities for pixels in the range
		// (x - N/2, y - N/2) to (x + N/2, y + N/2) by the
		// corresponding weights in the kernel matrix
		// - sum all these intensities and divide it by the weight
		// - set result.pixels[y * img.width + x] to this value

		for (int y = 0; y < img.height; y++) {
			for (int x = 0; x < img.width; x++) {
				float sum = 0;

				for (int i = clamped(0, img.height, y - 1), k = 0; i < clamped(
						0, img.height, y + 1); i++, k++) {
					for (int j = clamped(0, img.width, x - 1), m = 0; j < clamped(
							0, img.width, x + 1); j++, m++) {
						sum += brightness(img.pixels[i * img.width + j])
								* kernel[k][m];
					}
				}
				int intens = (int) (sum / weight);
				if (intens > 255) {
					intens = 255;
				} else if (intens < 0) {
					intens = 0;
				}
				result.pixels[y * img.width + x] = new Color(intens, intens,
						intens).getRGB();
			}
		}
		return result;
	}

	public PImage convolute(PImage img) {
		/*
		 * float[][] kernel = { { 0, 0, 0 }, { 1, 0, -1 }, { 0, 0, 0 }};
		 */
		float[][] kernel = { { 0, 1, 0 }, { 0, 0, 0 }, { 0, -1, 0 } };
		float weight = 1.f;
		// create a greyscale image (type: ALPHA) for output
		PImage result = createImage(img.width, img.height, ALPHA);
		// kernel size N = 3
		//
		// for each (x,y) pixel in the image:
		// - multiply intensities for pixels in the range
		// (x - N/2, y - N/2) to (x + N/2, y + N/2) by the
		// corresponding weights in the kernel matrix
		// - sum all these intensities and divide it by the weight
		// - set result.pixels[y * img.width + x] to this value
		int n = 3;

		for (int y = 0; y < img.height; y++) {
			for (int x = 0; x < img.width; x++) {
				float sum = 0;

				for (int i = clamped(0, img.height, y - 1), k = 0; i < clamped(
						0, img.height, y + 1); i++, k++) {
					for (int j = clamped(0, img.width, x - 1), m = 0; j < clamped(
							0, img.width, x + 1); j++, m++) {
						sum += brightness(img.pixels[i * img.width + j])
								* kernel[k][m];
					}
				}
				int intens = (int) (sum / weight);
				if (intens > 255) {
					intens = 255;
				} else if (intens < 0) {
					intens = 0;
				}
				result.pixels[y * img.width + x] = new Color(intens, intens,
						intens).getRGB();
			}
		}
		return result;
	}

	int clamped(int min, int max, int number) {
		if (number >= max) {
			return max - 1;
		} else if (number < min) {
			return min;
		} else {
			return number;
		}
	}

	/*
	 * public void exThresholdBrightness(){ background(color(0, 0, 0)); // ...
	 * for(int i = 0; i < img.width * img.height; i++){
	 * if(brightness(img.pixels[i]) > thresholdBar.getPos() * 255.0){
	 * result.pixels[i] = color(255); } else { result.pixels[i] = color(0); } }
	 * result.updatePixels(); image(result, 0, 0); thresholdBar.display();
	 * thresholdBar.update(); println(thresholdBar.getPos()); }
	 * 
	 * public void displayHueMap(){ background(color(0, 0, 0)); // ... for(int i
	 * = 0; i < img.width * img.height; i++){ result.pixels[i] =
	 * color(hue(img.pixels[i])); } result.updatePixels(); image(result, 0, 0);
	 * }
	 */

	PImage detectGreen(PImage img) {
		PImage result = new PImage(img.width, img.height);
		for (int i = 0; i < img.height; i++) {
			for (int j = 0; j < img.width; j++) {
				if (hue(img.pixels[i * img.width + j]) > 100
						&& hue(img.pixels[i * img.width + j]) < 138
						&& saturation(img.pixels[i * img.width + j]) > 50
						&& brightness(img.pixels[i * img.width + j]) > 10
						&& brightness(img.pixels[i * img.width + j]) < 245) {
					result.pixels[i * img.width + j] = img.pixels[i * img.width + j];
				} else {
					result.pixels[i * img.width + j] = color(0);
				}
			}
		}
		return result;
	}
}
