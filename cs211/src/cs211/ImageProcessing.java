package cs211;
import java.awt.Color;

import processing.core.PApplet;
import processing.core.PImage;
public class ImageProcessing extends PApplet {
	PImage img;
	PImage result;
	HScrollbar thresholdBar;

	//TODO part I iv faire les deux scrollbars pour les hsb

	public void setup() { 
		size(800, 600);
		thresholdBar = new HScrollbar ( this , (float)0 ,(float) 580 , (float)800 , (float)20 ) ;
		img = loadImage("board1.jpg");
		result = createImage(width, height, RGB);
		// create a new, initially transparent, 'result' image 

		//noLoop(); // no interactive behaviour: draw() will be called only once. 
	}
	public void draw() {

		background(color(0,0,0));
		//changeImage2();
		result = sobel(img);
		image(result, 0, 0);
		thresholdBar.display();
		thresholdBar.update();

	}	
	public void changeImage () {
		result.loadPixels();
		for(int i = 0; i < img.width * img.height; i++) {
			if ( brightness(img.pixels[i]) > thresholdBar.getPos()*255) {
				result.pixels[i]= Color.black.getRGB() ;
			} else {
				result.pixels[i]= Color.white.getRGB() ;
			}

		}
		result.updatePixels();
	}

	public void changeImage2 () {
		result.loadPixels();
		for(int i = 0; i < img.width * img.height; i++) {
			if ( hue(img.pixels[i]) > 120 && hue(img.pixels[i]) < 150){
				result.pixels[i]= img.pixels[i];
			} else {
				result.pixels[i]= Color.black.getRGB() ;
			}

		}
		result.updatePixels();
	}

	public PImage convolute(PImage img) {
		float[][] kernel = {{ 0, 0, 0 },
				{ 1, 0, -1 },
				{ 0, 0, 0 }};
		float weight = 1.f;

		//grayscale image
		PImage result = createImage(img.width, img.height, ALPHA);
		int kernelSize = kernel.length;
		for (int y = 0; y < img.height; y ++){
			for (int x = 0; x < img.width; x++){
				int intensities = 0;
				for (int j = 0; j < kernelSize; j++){
					for(int i = 0; i < kernel[0].length; i++){
						int iAdjusted = i-kernel[0].length/2;
						int jAdjusted = j-kernelSize/2;
						if (x + iAdjusted >= 0 && x + iAdjusted < img.width && y + jAdjusted >= 0 && y + jAdjusted < img.height){
							//on prend seulement l'alpha?
							float bright = brightness((img.pixels[(y + jAdjusted)*img.width + x + iAdjusted]));
							intensities += bright*kernel[j][i];

						}
					}
				}
				intensities /= weight;
				if(intensities > 255){
					intensities = 255;
				}
				if(intensities < 0){
					intensities = 0;
				}
				Color c = new Color((int)intensities, (int)intensities, (int)intensities);
				result.pixels[y * img.width + x] = c.getRGB();
			}
		}
		return result;
	}

	public PImage sobel(PImage img) {
		float[][] hKernel = { {0, 1, 0 }, 
				{0, 0, 0 },
				{0, -1, 0 }};
		float[][] vKernel = { {0, 0, 0 }, 
				{1, 0, -1 },
				{0, 0, 0 }};
		PImage result = createImage(img.width, img.height, ALPHA);

		//clear the image
		for (int i = 0; i < img.width * img.height; i++){
			result.pixels[i] = color(0);
		}

		float max = 0;
		float[] buffer = new float[img.width* img.height];
		int N = 3;
		for (int y = 0; y < img.height; y ++){
			for (int x = 0; x < img.width; x++){
				int hSum = 0;
				int vSum = 0;

				for (int j = 0; j < N; j++){
					for(int i = 0; i < N; i++){
						int iAdjusted = i-N/2;
						int jAdjusted = j-N/2;
						if (x + iAdjusted >= 0 && x + iAdjusted < img.width && y + jAdjusted >= 0 && y + jAdjusted < img.height){
							//on prend seulement l'alpha?
							float bright = brightness((img.pixels[(y + jAdjusted)*img.width + x + iAdjusted]));
							hSum += bright*hKernel[j][i];
							vSum += bright*vKernel[j][i];
						}
					}
				}
				float sum = sqrt(pow(hSum, 2) + pow(vSum, 2));
				buffer[y*img.width + x] = sum;
				if (sum > max){
					max = sum;
				}
			}
		}
		for(int y = 2; y < img.height -2; y++){
			for (int x = 2; x <img.width -2; x++){
				if (buffer[y *img.width + x] > (int)(max *0.3f)) {
					result.pixels[y*img.width + x] = color(255);
				}else {
					result.pixels[y * img.width + x] = color(0);
				}
			}
		}
		return result;
	}

}
