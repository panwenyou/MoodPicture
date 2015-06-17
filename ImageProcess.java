package com.panwenyou.moodpicture;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

public class ImageProcess {
	/*
	 * type: 用户输入的心情 目前只有四种：1.happy 2.sad 3.memory 4.victory pixels:
	 * 输入图像的像素，单位为integer，一个int32位，包含ARGB； width: 输入图像的宽度 height: 输入 图像的高度
	 */
	public static void colorAdjustment(String type, int[] pixels, int width,
			int height) {
		int i = 0;
		int total = width * height;
		if (type.equals("happy")) {
			for (; i < total; i++) {
				int r = (int) (pixels[i] & 0xff0000) >> 16;
				int g = (int) (pixels[i] & 0x00ff00) >> 8;
				int b = (int) (pixels[i] & 0xff);

				r = (int) (r * 1.6);
				if (r > 255)
					r = 255;
				g = (int) (g * 1.4);
				if (g > 255)
					g = 255;
				b = (int) (b * 1.4);
				if (b > 255)
					b = 255;

				pixels[i] = (0xff << 24) | (r << 16) | (g << 8) | b;
			}
		} else if (type.equals("sad")) {
			for (; i < total; i++) {
				int r = (int) (pixels[i] & 0xff0000) >> 16;
				int g = (int) (pixels[i] & 0x00ff00) >> 8;
				int b = (int) (pixels[i] & 0xff);

				b = (int) (b * 1.4);
				if (b > 255)
					b = 255;

				pixels[i] = (0xff << 24) | (r << 16) | (g << 8) | b;
			}
		} else if (type.equals("memory")) {
			for (; i < total; i++) {
				int r = (int) (pixels[i] & 0xff0000) >> 16;
				int g = (int) (pixels[i] & 0x00ff00) >> 8;
				int b = (int) (pixels[i] & 0xff);

				r = (int) (r * 1.4);
				if (r > 255)
					r = 255;
				g = (int) (g * 1.3);
				if (g > 255)
					g = 255;

				pixels[i] = (0xff << 24) | (r << 16) | (g << 8) | b;
			}
		} else if (type.equals("victory")) {
			for (; i < total; i++) {
				int r = (int) (pixels[i] & 0xff0000) >> 16;
				int g = (int) (pixels[i] & 0x00ff00) >> 8;
				int b = (int) (pixels[i] & 0xff);

				r = (int) (r * 1.8);
				if (r > 255)
					r = 255;
				g = (int) (g * 1.1);
				if (g > 255)
					g = 255;

				pixels[i] = (0xff << 24) | (r << 16) | (g << 8) | b;
			}
		}
	}

	public static Meterial renderImage(Meterial renderImage,
			ArrayList<Meterial> meterial_list) {

		
	}

}
