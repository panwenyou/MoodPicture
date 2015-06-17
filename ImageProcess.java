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

		Meterial result = new Meterial(), m;
		int Xmin = renderImage.x, Xmax = renderImage.x + renderImage.width, Ymin = renderImage.y, Ymax = renderImage.y
				+ renderImage.height;
		for (Iterator<Meterial> it = meterial_list.iterator(); it.hasNext();) {
			m = (Meterial) it.next();
			if (m.x < Xmin) {
				Xmin = m.x;
			}
			if (m.x + m.width > Xmax) {
				Xmax = m.x + m.width;
			}
			if (m.y < Ymin) {
				Ymin = m.y;
			}
			if (m.y + m.height > Ymax) {
				Ymax = m.y + m.height;
			}
		}
		result.x = Xmin;
		result.y = Ymin;
		result.height = Ymax - Ymin;
		result.width = Xmax - Xmin;
		result.pixels = new int[result.height * result.width];
		for (int i = 0; i < result.height * result.width; i++) {
			result.pixels[i] = 255 << 24 | 255 << 16 | 255 << 8 | 255;
		}
		int y0 = renderImage.y;
		int x0 = renderImage.x;
		for (int i = 0; i < renderImage.height * renderImage.width; i++) {
			int rgb = (y0 - result.y) * result.width + x0 - result.x;
			result.pixels[rgb] = renderImage.pixels[i];
			x0++;
			if (x0 >= renderImage.width + renderImage.x) {
				x0 = x0 - renderImage.width;
				y0++;
			}
		}

		for (Iterator<Meterial> it = meterial_list.iterator(); it.hasNext();) {
			m = (Meterial) it.next();
			y0 = m.y;
			x0 = m.x;
			for (int i = 0; i < m.height * m.width; i++) {
				int rgb = (y0 - result.y) * result.width + x0-result.x;
				int alpha = (m.pixels[i] & 0xff000000) >> 24;
			    alpha = alpha & 0x000000ff;
				if (alpha == 255) {
					result.pixels[rgb] = m.pixels[i];
				} else {
					// Nothing
				}
				x0++;
				if (x0 >= m.width + m.x) {
					x0 = x0 - m.width;
					y0++;
				}
			}
		}
		return result;
	}

}
