package cs211;
import java.awt.Color;

import processing.core.PApplet;
import processing.core.PImage;
public class ImageProcessing extends PApplet {
	PImage img;
	PImage intermediate1;
	PImage intermediate2;
	PImage result;
	HScrollbar thresholdBar1;
	HScrollbar thresholdBar2;

	//TODO part I iv faire les deux scrollbars pour les hsb
	//TODO faire un fix pour le sobel algorithm

	public void setup() { 
		size(800, 600);
		thresholdBar1 = new HScrollbar ( this , (float)0 ,(float) 580 , (float)800 , (float)20 ) ;
		thresholdBar2 = new HScrollbar ( this , (float)0 ,(float) 560 , (float)800 , (float)20 ) ;
		img = loadImage("board1.jpg");
		intermediate1 = createImage(width, height, RGB);
		intermediate2 = createImage(width, height, RGB);
		result = createImage(width, height, RGB);
		
		// create a new, initially transparent, 'result' image 

		//noLoop(); // no interactive behaviour: draw() will be called only once. 
	}
	public void draw() {

		background(color(0,0,0));
		intermediate1 = convolute(img);
		changeImageMinBright(intermediate1, result);
		changeImageHueFixed(img, intermediate1);
		result = sobel(intermediate1);
		image(result, 0, 0);
		thresholdBar1.display();
		thresholdBar2.display();
		thresholdBar1.update();
		thresholdBar2.update();
	}	
	public void changeImageMaxBright (PImage src, PImage dst) {
		dst.loadPixels();
		for(int i = 0; i < src.width * src.height; i++) {
			if ( brightness(src.pixels[i]) > thresholdBar1.getPos()*255) {
				dst.pixels[i]= Color.black.getRGB() ;
			} else {
				dst.pixels[i]= Color.white.getRGB() ;
			}

		}
		dst.updatePixels();
	}
	
	public void changeImageMinBright (PImage src, PImage dst) {
		dst.loadPixels();
		for(int i = 0; i < src.width * src.height; i++) {
			if ( brightness(src.pixels[i]) < thresholdBar2.getPos()*255) {
				dst.pixels[i]= Color.black.getRGB() ;
			} else {
				dst.pixels[i]= Color.white.getRGB() ;
			}

		}
		dst.updatePixels();
	}

	public void changeImageHue (PImage src, PImage dst) {
		dst.loadPixels();
		for(int i = 0; i < src.width * src.height; i++) {
			if ( hue(src.pixels[i]) > thresholdBar1.getPos() * 255 && hue(src.pixels[i]) < thresholdBar2.getPos() * 255){
				dst.pixels[i]= Color.white.getRGB();
			} else {
				dst.pixels[i]= Color.black.getRGB() ;
			}

		}
		dst.updatePixels();
	}
	public void changeImageHueFixed (PImage src, PImage dst) {
		dst.loadPixels();
		for(int i = 0; i < src.width * src.height; i++) {
			if ( hue(src.pixels[i]) > 116 && hue(src.pixels[i]) < 136){//values determined experimentally for the best
				dst.pixels[i]= Color.white.getRGB();
			} else {
				dst.pixels[i]= Color.black.getRGB() ;
			}

		}
		dst.updatePixels();
	}

	public PImage convolute(PImage img) {
		float[][] kernel = {{ 9, 12, 9 },
				{ 12, 15, 12 },
				{ 9, 12, 9 }};
		float weight = 99.f;

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
							//on prend la brightness avec la fonction processing
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
				if (buffer[y *img.width + x] > (int)(max * 0.3f)) {
					result.pixels[y*img.width + x] = color(255);
				}else {
					result.pixels[y * img.width + x] = color(0);
				}
			}
		}
		return result;
	}

}
