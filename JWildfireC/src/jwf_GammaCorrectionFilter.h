/*
 JWildfireC - an external C-based fractal-flame-renderer for JWildfire
 Copyright (C) 2012 Andreas Maschke

 This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 General Public License as published by the Free Software Foundation; either version 2.1 of the
 License, or (at your option) any later version.
 
 This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this software;
 if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
#ifndef __JWF_GAMMA_CORRCTION_FILTER_H__
#define __JWF_GAMMA_CORRCTION_FILTER_H__

struct GammaCorrectionFilter {
	Flame *flame;
	int vibInt;
	int inverseVibInt;
	FLOAT vibDouble;
	FLOAT inverseVibDouble;
	FLOAT gamma;
	FLOAT sclGamma;
	int bgRed, bgGreen, bgBlue;
	FLOAT bgRedDouble, bgGreenDouble, bgBlueDouble;

	void create(Flame *pFlame) {
		flame = pFlame;
		initFilter();
	}

	void free() {
	}

	void initFilter() {
		gamma = (flame->gamma == 0.0) ? flame->gamma : 1.0 / flame->gamma;

		vibInt = (int) (flame->vibrancy * 256.0 + 0.5);
		if (vibInt < 0) {
			vibInt = 0;
		}
		else if (vibInt > 256) {
			vibInt = 256;
		}
		inverseVibInt = 256 - vibInt;

		vibDouble = flame->vibrancy;
		if (vibDouble < 0.0) {
			vibDouble = 0.0;
		}
		else if (vibDouble > 1.0) {
			vibDouble = 1.0;
		}

		sclGamma = 0.0;
		if (flame->gammaThreshold != 0.0) {
			sclGamma = JWF_POW(flame->gammaThreshold, gamma - 1.0);
		}

		bgRed = flame->bgColorRed;
		bgGreen = flame->bgColorGreen;
		bgBlue = flame->bgColorBlue;

		bgRedDouble = bgRed / 255.0;
		bgGreenDouble = bgGreen / 255.0;
		bgBlueDouble = bgBlue / 255.0;
	}

	void transformPoint(LogDensityPoint *logDensityPnt, GammaCorrectedRGBPoint *pRGBPoint) {
		FLOAT logScl;
		int inverseAlphaInt;
		if (logDensityPnt->intensity > 0.0) {
			// gamma linearization
			FLOAT alpha;
			if (logDensityPnt->intensity <= flame->gammaThreshold) {
				FLOAT frac = logDensityPnt->intensity / flame->gammaThreshold;
				alpha = (1.0 - frac) * logDensityPnt->intensity * sclGamma + frac * JWF_POW(logDensityPnt->intensity, gamma);
			}
			else {
				alpha = JWF_POW(logDensityPnt->intensity, gamma);
			}
			logScl = vibInt * alpha / logDensityPnt->intensity;
			int alphaInt = (int) (alpha * 256.0 + 0.5);
			if (alphaInt < 0)
				alphaInt = 0;
			else if (alphaInt > 255)
				alphaInt = 255;
			inverseAlphaInt = 255 - alphaInt;
		}
		else {
			pRGBPoint->red = bgRed;
			pRGBPoint->green = bgGreen;
			pRGBPoint->blue = bgBlue;
			return;
		}

		int red, green, blue;
		if (inverseVibInt > 0) {
			red = (int) (logScl * logDensityPnt->red + inverseVibInt * JWF_POW(logDensityPnt->red, gamma) + 0.5);
			green = (int) (logScl * logDensityPnt->green + inverseVibInt * JWF_POW(logDensityPnt->green, gamma) + 0.5);
			blue = (int) (logScl * logDensityPnt->blue + inverseVibInt * JWF_POW(logDensityPnt->blue, gamma) + 0.5);
		}
		else {
			red = (int) (logScl * logDensityPnt->red + 0.5);
			green = (int) (logScl * logDensityPnt->green + 0.5);
			blue = (int) (logScl * logDensityPnt->blue + 0.5);
		}

		red = red + ((inverseAlphaInt * bgRed) >> 8);
		if (red < 0)
			red = 0;
		else if (red > 255)
			red = 255;

		green = green + ((inverseAlphaInt * bgGreen) >> 8);
		if (green < 0)
			green = 0;
		else if (green > 255)
			green = 255;

		blue = blue + ((inverseAlphaInt * bgBlue) >> 8);
		if (blue < 0)
			blue = 0;
		else if (blue > 255)
			blue = 255;

		pRGBPoint->red = red;
		pRGBPoint->green = green;
		pRGBPoint->blue = blue;
	}

	void transformPointHDR(LogDensityPoint *logDensityPnt, GammaCorrectedHDRPoint *pHDRPoint) {
		FLOAT logScl;
		FLOAT inverseAlpha;
		if (logDensityPnt->intensity > 0.0) {
			// gamma linearization
			FLOAT alpha;
			if (logDensityPnt->intensity <= flame->gammaThreshold) {
				FLOAT frac = logDensityPnt->intensity / flame->gammaThreshold;
				alpha = (1.0 - frac) * logDensityPnt->intensity * sclGamma + frac * JWF_POW(logDensityPnt->intensity, gamma);
			}
			else {
				alpha = JWF_POW(logDensityPnt->intensity, gamma);
			}
			logScl = vibDouble * alpha / logDensityPnt->intensity;

			if (alpha < 0)
				alpha = 0;
			else if (alpha > 1.0)
				alpha = 1.0;
			inverseAlpha = (1.0 - alpha);
		}
		else {
			pHDRPoint->red = (FLOAT) -1.0;
			pHDRPoint->green = (FLOAT) -1.0;
			pHDRPoint->blue = (FLOAT) -1.0;
			return;
		}

		FLOAT red, green, blue;
		if (inverseVibDouble > 0.0) {
			red = logScl * logDensityPnt->red + inverseVibDouble * JWF_POW(logDensityPnt->red, gamma);
			green = logScl * logDensityPnt->green + inverseVibDouble * JWF_POW(logDensityPnt->green, gamma);
			blue = logScl * logDensityPnt->blue + inverseVibDouble * JWF_POW(logDensityPnt->blue, gamma);
		}
		else {
			red = logScl * logDensityPnt->red;
			green = logScl * logDensityPnt->green;
			blue = logScl * logDensityPnt->blue;
		}
		red += inverseAlpha * bgRedDouble;
		green += inverseAlpha * bgGreenDouble;
		blue += inverseAlpha * bgBlueDouble;

		pHDRPoint->red = (FLOAT) red;
		pHDRPoint->green = (FLOAT) green;
		pHDRPoint->blue = (FLOAT) blue;
	}

	void transformPointSimple(LogDensityPoint *logDensityPnt, GammaCorrectedRGBPoint *pRGBPoint) {
		FLOAT logScl;
		int inverseAlphaInt;
		if (logDensityPnt->intensity > 0.0) {
			// gamma linearization
			FLOAT alpha = JWF_POW(logDensityPnt->intensity, gamma);
			logScl = vibInt * alpha / logDensityPnt->intensity;
			int alphaInt = (int) (alpha * 256.0 + 0.5);
			if (alphaInt < 0)
				alphaInt = 0;
			else if (alphaInt > 255)
				alphaInt = 255;
			inverseAlphaInt = 255 - alphaInt;
		}
		else {
			pRGBPoint->red = bgRed;
			pRGBPoint->green = bgGreen;
			pRGBPoint->blue = bgBlue;
			return;
		}

		int red, green, blue;
		red = (int) (logScl * logDensityPnt->red + 0.5);
		green = (int) (logScl * logDensityPnt->green + 0.5);
		blue = (int) (logScl * logDensityPnt->blue + 0.5);

		red = red + ((inverseAlphaInt * bgRed) >> 8);
		if (red < 0)
			red = 0;
		else if (red > 255)
			red = 255;

		green = green + ((inverseAlphaInt * bgGreen) >> 8);
		if (green < 0)
			green = 0;
		else if (green > 255)
			green = 255;

		blue = blue + ((inverseAlphaInt * bgBlue) >> 8);
		if (blue < 0)
			blue = 0;
		else if (blue > 255)
			blue = 255;

		pRGBPoint->red = red;
		pRGBPoint->green = green;
		pRGBPoint->blue = blue;
	}
};

#endif // __JWF_GAMMA_CORRCTION_FILTER_H__
