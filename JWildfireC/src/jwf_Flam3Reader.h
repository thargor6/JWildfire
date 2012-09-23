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
#ifndef __JWF_FLAM3_READER_H__
#define __JWF_FLAM3_READER_H__

#include "jwf_VariationFactory.h"

class Flam3Reader {
public:
	Flam3Reader() {
		variationFactory = new VariationFactory();
	}

	~Flam3Reader() {
		delete variationFactory;
	}

	void readFlames(char *pFilename, Flame ***pFlames, int *pFlameCount) {
		*pFlames = NULL;
		*pFlameCount = 0;
		FILE* f = fopen(pFilename, "r");
		if (f == 0) {
			printf("File %s not found\n", pFilename);
			return;
		}
		fseek(f, 0, SEEK_END);
		size_t size = ftell(f);
		if (size <= 0) {
			printf("File %s is empty\n", pFilename);
			return;
		}
		char* flamesXML = new char[size+1];
		rewind(f);
		fread(flamesXML, sizeof(char), size, f);
		fclose(f);
		flamesXML[size]='\0';
		readFlamesfromXML(flamesXML, pFlames, pFlameCount);
	}

private:

#define ATTR_SIZE "size"
#define ATTR_CENTER "center"
#define ATTR_SCALE "scale"
#define ATTR_ROTATE  "rotate"
#define ATTR_OVERSAMPLE "oversample"
#define ATTR_COLOR_OVERSAMPLE  "color_oversample"
#define ATTR_FILTER "filter"
#define ATTR_QUALITY  "quality"
#define ATTR_BACKGROUND  "background"
#define ATTR_BRIGHTNESS  "brightness"
#define ATTR_GAMMA  "gamma"
#define ATTR_GAMMA_THRESHOLD  "gamma_threshold"
#define ATTR_INDEX  "index"
#define ATTR_RGB  "rgb"
#define ATTR_CAM_PITCH  "cam_pitch"
#define ATTR_CAM_YAW  "cam_yaw"
#define ATTR_CAM_PERSP  "cam_persp"
#define ATTR_CAM_PERSPECTIVE  "cam_perspective"
#define ATTR_CAM_ZPOS  "cam_zpos"
#define ATTR_CAM_DOF  "cam_dof"
#define ATTR_CAM_ZOOM  "cam_zoom"
#define ATTR_SHADING_SHADING  "shading_shading"
#define ATTR_SHADING_AMBIENT  "shading_ambient"
#define ATTR_SHADING_DIFFUSE  "shading_diffuse"
#define ATTR_SHADING_PHONG  "shading_phong"
#define ATTR_SHADING_PHONGSIZE  "shading_phongSize"
#define ATTR_SHADING_LIGHTCOUNT  "shading_lightCount"
#define ATTR_SHADING_LIGHTPOSX_  "shading_lightPosX_"
#define ATTR_SHADING_LIGHTPOSY_  "shading_lightPosY_"
#define ATTR_SHADING_LIGHTPOSZ_  "shading_lightPosZ_"
#define ATTR_SHADING_LIGHTRED_  "shading_lightRed_"
#define ATTR_SHADING_LIGHTGREEN_  "shading_lightGreen_"
#define ATTR_SHADING_LIGHTBLUE_  "shading_lightBlue_"
#define ATTR_SHADING_BLUR_RADIUS  "shading_blurRadius"
#define ATTR_SHADING_BLUR_FADE  "shading_blurFade"
#define ATTR_SHADING_BLUR_FALLOFF  "shading_blurFallOff"
#define ATTR_PRESERVE_Z  "preserve_z"
#define ATTR_RESOLUTION_PROFILE  "resolution_profile"
#define ATTR_QUALITY_PROFILE  "quality_profile"
#define ATTR_NEW_LINEAR "new_linear"
// xforms
#define ATTR_WEIGHT "weight"
#define ATTR_COLOR "color"
#define ATTR_OPACITY "opacity"
#define ATTR_COEFS "coefs"
#define ATTR_POST "post"
#define ATTR_CHAOS "chaos"
#define ATTR_SYMMETRY "symmetry"

	void readFlamesfromXML(char *pXML, Flame ***pFlameArray, int *pFlameCount) {
		int pFlames = 0;
		while (true) {
			char *flameXML;
			{
				int ps = indexOf(pXML, "<flame ", pFlames);
				if (ps < 0)
					break;
				int pe = indexOf(pXML, "</flame>", ps + 1);
				if (pe < 0)
					break;
				pFlames = pe + 8;
				flameXML = substring(pXML, ps, pFlames);
			}

			Flame *flame = (Flame*) calloc(1, sizeof(Flame));
			flame->create();
			{
				if (*pFlameCount == 0) {
					*pFlameArray = (Flame**) malloc(sizeof(Flame*));
					*pFlameArray[0] = flame;
					*pFlameCount = 1;
				}
				else {
					Flame **newFlames = (Flame**) malloc((*pFlameCount + 1) * sizeof(Flame*));
					memcpy(newFlames, *pFlameArray, (*pFlameCount) * sizeof(Flame*));
					newFlames[(*pFlameCount) + 1] = flame;
					free(*pFlameArray);
					*pFlameArray = newFlames;
					*pFlameCount = *pFlameCount + 1;
				}
			}

			// Flame attributes
			{
				int ps = indexOf(flameXML, "<flame ", 0);
				int pe = -1;
				boolean qt = false;
				for (unsigned int i = ps + 1; i < strlen(flameXML); i++) {
					if (flameXML[i] == '\"') {
						qt = !qt;
					}
					else if (!qt && flameXML[i] == '>') {
						pe = i;
						break;
					}
				}
				char *hs = substring(flameXML, ps + 7, pe);
				parseFlameAttributes(flame, hs);
				free(hs);
			}
			// XForms
			{
				int p = 0;
				while (true) {
					int ps = indexOf(flameXML, "<xform ", p + 1);
					if (ps < 0)
						break;
					int pe = indexOf(flameXML, "</xform>", ps + 1);
					if (pe < 0) {
						pe = indexOf(flameXML, "/>", ps + 1);
					}
					char *hs = substring(flameXML, ps + 7, pe);
					XForm *xForm = new XForm();
					xForm->init();
					parseXFormAttributes(xForm, hs);
					flame->addXForm(xForm);
					// TODO cause endless loop (?)
					// free(hs);
					p = pe + 2;
				}
			}
			// FinalXForm
			{
				int p = 0;
				while (true) {
					int ps = indexOf(flameXML, "<finalxform ", p + 1);
					if (ps < 0)
						break;
					int pe = indexOf(flameXML, "/>", ps + 1);
					char *hs = substring(flameXML, ps + 12, pe);
					XForm *xForm = new XForm();
					xForm->init();
					parseXFormAttributes(xForm, hs);
					flame->finalXForm = xForm;
					free(hs);
					p = pe + 2;
				}
			}

			// Colors
			{
				int p = 0;
				while (true) {
					int ps = indexOf(flameXML, "<color ", p + 1);
					if (ps < 0)
						break;
					int pe = indexOf(flameXML, "/>", ps + 1);
					char *hs = substring(flameXML, ps + 7, pe);
					{
						int index = 0;
						int r = 0, g = 0, b = 0;
						int attCount;
						char **attNames, **attValues;
						parseAttributes(hs, &attNames, &attValues, &attCount);
						char *attr;
						if ((attr = findAttribute(attNames, attValues, attCount, ATTR_INDEX)) != NULL) {
							index = atoi(attr);
						}
						if ((attr = findAttribute(attNames, attValues, attCount, ATTR_RGB)) != NULL) {
							char **s;
							int sCount;
							split(attr, " ", &s, &sCount);
							if (sCount == 3) {
								r = FTOI(atof(s[0]));
								g = FTOI(atof(s[1]));
								b = FTOI(atof(s[2]));
							}
							else {
								printf("Bad color %s\n", attr);
							}
							freeStrArray(s, sCount);
						}
						freeStrArray(attNames, attCount);
						freeStrArray(attValues, attCount);
						flame->palette->setColor(index, r, g, b);
					}
					free(hs);
					p = pe + 2;
				}
			}
			// Palette
			{
				int ps = indexOf(flameXML, "<palette ", 0);
				if (ps >= 0) {
					ps = indexOf(flameXML, ">", ps + 1);
					int pe = indexOf(flameXML, "</palette>", ps + 1);
					char *hs = substring(flameXML, ps + 1, pe);
					{
						int len = strlen(hs);
						char *sb = (char*) malloc((len + 1) * sizeof(char));
						int sbLen = 0;
						for (int i = 0; i < len; i++) {
							char c = hs[i];
							if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
								sb[sbLen++] = c;
							}
						}
						sb[sbLen] = '\0';
						free(hs);
						hs = sb;
					}

					int len = strlen(hs);
					if ((len % 6) != 0) {
						printf("Invalid/unknown palette");
					}
					int index = 0;

					for (int i = 0; i < len; i += 6) {
						int r = htoi(substring(hs, i, i + 2));
						int g = htoi(substring(hs, i + 2, i + 4));
						int b = htoi(substring(hs, i + 4, i + 6));
						// printf(" flame->palette->setColor(%d, %d, %d, %d);\n", index, r, g, b);
						flame->palette->setColor(index++, r, g, b);
					}
				}
			}
			free(flameXML);
		}
	}

	int indexOf(char *pStr, char *pToken, int pOffset) {
		int len=strlen(pStr);
		char *pos = strstr(pStr + pOffset, pToken);
		if (pos != NULL && ((pos-pStr)<len)) {
			return pos - pStr;
		}
		else {
			return -1;
		}
	}

	char* substring(char *pStr, int pOffset, int pEnd) {
		int length = pEnd - pOffset;
		if (length <= 0) {
			return NULL;
		}
		char *res = (char*) malloc((length + 1) * sizeof(char));
		strncpy(res, pStr + pOffset, length);
		res[length] = '\0';
		return res;
	}

	char *findAttribute(char **pNames, char **pValues, int pCount, char *pName) {
		for (int i = 0; i < pCount; i++) {
			if (pName != NULL && strcmp(pName, pNames[i]) == 0) {
				return pValues[i];
			}
		}
		return NULL;
	}

	void parseAttributes(char *pXML, char ***pNames, char ***pValues, int *pCount) {
		*pNames = NULL;
		*pValues = NULL;
		*pCount = 0;
		int p = 0;
		while (true) {
			int ps = indexOf(pXML, "=\"", p + 1);
			if (ps < 0)
				break;
			int pe = indexOf(pXML, "\"", ps + 2);
			char *name = substring(pXML, p, ps);
			char *value = substring(pXML, ps + 2, pe);
			if (*pCount == 0) {
				*pNames = (char**) malloc(sizeof(char**));
				*pValues = (char**) malloc(sizeof(char**));
				(*pNames)[0] = name;
				(*pValues)[0] = value;
				*pCount = 1;
			}
			else {
				{
					char **newNames = (char**) malloc((*pCount + 1) * sizeof(char**));
					memcpy(newNames, *pNames, (*pCount) * sizeof(char**));
					newNames[*pCount] = name;
					free(*pNames);
					*pNames = newNames;
				}
				{
					char **newValues = (char**) malloc((*pCount + 1) * sizeof(char**));
					memcpy(newValues, *pValues, (*pCount) * sizeof(char**));
					newValues[*pCount] = value;
					free(*pValues);
					*pValues = newValues;
					*pCount = *pCount + 1;
				}
			}
			p = pe + 2;
		}
	}

	void split(char *pString, char *delim, char ***tokens, int *tokenCount) {
		*tokens = NULL;
		*tokenCount = 0;
		if (pString == NULL || strlen(pString) == 0) {
			return;
		}

		int l = strlen(pString);
		char *copy = (char*) malloc((l + 1) * sizeof(char));
		memcpy(copy, pString, l * sizeof(char));
		copy[l] = '\0';

		if (delim == NULL || strlen(delim) == 0) {
			*tokens = (char**) malloc(sizeof(char*));
			(*tokens)[0] = copy;
			*tokenCount = 1;
		}

		char *p = strtok(copy, delim);
		while (p) {
			l = strlen(p);
			char *token = (char*) malloc((l + 1) * sizeof(char));
			memcpy(token, p, l * sizeof(char));
			token[l] = '\0';
			if ((*tokenCount) == 0) {
				*tokens = (char**) malloc(sizeof(char*));
				(*tokens)[0] = token;
				*tokenCount = 1;
			}
			else {
				char **newTokens = (char**) malloc(((*tokenCount) + 1) * sizeof(char*));
				memcpy(newTokens, *tokens, (*tokenCount) * sizeof(char*));
				newTokens[*tokenCount] = token;
				free(*tokens);
				*tokens = newTokens;
				*tokenCount = (*tokenCount) + 1;
			}
			p = strtok(NULL, delim);
		}
		free(copy);
	}

	void freeStrArray(char **pArray, int pSize) {
		if (pArray != NULL && pSize > 0) {
			for (int i = 0; i < pSize; i++) {
				free(pArray[i]);
			}
			free(pArray);
		}
	}

	int htoi(char *pHex) {
		int res = 0;
		int mul = 1;
		for (int i = strlen(pHex) - 1; i >= 0; i--) {
			char c = pHex[i];
			int val;
			if ((c >= '0') && (c <= '9')) {
				val = c - '0';
			}
			else if ((c >= 'A') && (c <= 'F')) {
				val = c - 'A' + 10;
			}
			else if ((c >= 'a') && (c <= 'f')) {
				val = c - 'a' + 10;
			}
			else {
				printf("Invalid hex string %s\n", pHex);
				return 0;
			}
			res += mul * val;
			mul *= 16;
		}
		return res;
	}

	int roundColor(float pColor) {
		int res = FTOI(pColor);
		if (res < 0)
			return 0;
		else if (res > 255)
			return 255;
		else
			return res;
	}

	void parseXFormAttributes(XForm *pXForm, char *pXML) {
		int attCount;
		char **attNames, **attValues;
		parseAttributes(pXML, &attNames, &attValues, &attCount);
		char *hs;
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_WEIGHT)) != NULL) {
			pXForm->weight = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_COLOR)) != NULL) {
			pXForm->color = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_OPACITY)) != NULL) {
			float opacity = atof(hs);
			pXForm->opacity = opacity;
			if (fabs(opacity) <= EPSILON) {
				pXForm->drawMode = DRAWMODE_HIDDEN;
			}
			else if (fabs(opacity - 1.0f) > EPSILON) {
				pXForm->drawMode = DRAWMODE_OPAQUE;
			}
			else {
				pXForm->drawMode = DRAWMODE_NORMAL;
			}
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_SYMMETRY)) != NULL) {
			pXForm->colorSymmetry = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_COEFS)) != NULL) {
			char **s;
			int sCount;
			split(hs, " ", &s, &sCount);
			if (sCount != 6) {
				printf("Invalid coefs %s\n", hs);
			}
			else {
				pXForm->coeff00 = atof(s[0]);
				pXForm->coeff01 = atof(s[1]);
				pXForm->coeff10 = atof(s[2]);
				pXForm->coeff11 = atof(s[3]);
				pXForm->coeff20 = atof(s[4]);
				pXForm->coeff21 = atof(s[5]);
			}
			freeStrArray(s, sCount);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_POST)) != NULL) {
			char **s;
			int sCount;
			split(hs, " ", &s, &sCount);
			if (sCount != 6) {
				printf("Invalid post coefs %s\n", hs);
			}
			else {
				pXForm->postCoeff00 = atof(s[0]);
				pXForm->postCoeff01 = atof(s[1]);
				pXForm->postCoeff10 = atof(s[2]);
				pXForm->postCoeff11 = atof(s[3]);
				pXForm->postCoeff20 = atof(s[4]);
				pXForm->postCoeff21 = atof(s[5]);
			}
			freeStrArray(s, sCount);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_CHAOS)) != NULL) {
			char **s;
			int sCount;
			split(hs, " ", &s, &sCount);
			for (int i = 0; i < sCount; i++) {
				pXForm->modifiedWeights[i] = atof(s[i]);
			}
			freeStrArray(s, sCount);
		}
		// variations
		{
			for (int a = 0; a < attCount; a++) {
				char *name = attNames[a];
				char *aliasName = NULL;
				char *varName = aliasName != NULL ? aliasName : name;
				Variation *srcVar = variationFactory->findVariation(varName);
				if (srcVar != NULL) {
					Variation *var = srcVar->makeCopy();
					float amount = atof(attValues[a]);
					pXForm->addVariation(var, amount);

					// params
					{
						char **paramNames = var->getParameterNames();
						if (paramNames != NULL) {
							for (int i = 0; i < var->getParameterCount(); i++) {
								char *pName = paramNames[i];
								char *pHs;
								int pAttNameLen = strlen(name) + strlen(pName) + 2;
								char *pAttName = (char*) malloc(pAttNameLen * sizeof(char));
								pAttName[0] = '\0';
								sprintf(pAttName, "%s_%s", name, pName);
								pAttName[pAttNameLen - 1] = '\0';
								if ((pHs = findAttribute(attNames, attValues, attCount, pAttName)) != NULL) {
									var->setParameter(pName, atof(pHs));
								}
							}
						}
					}
					// ressources
					{
						char **ressNames = var->getRessourceNames();
						if (ressNames != NULL) {
							for (int i = 0; i < var->getRessourceCount(); i++) {
								char *pName = ressNames[i];
								char *pHs;
								int pRessNameLen = strlen(name) + strlen(pName) + 2;
								char *pRessName = (char*) malloc(pRessNameLen * sizeof(char));
								pRessName[0] = '\0';
								sprintf(pRessName, "%s_%s", name, pName);
								pRessName[pRessNameLen - 1] = '\0';
								if ((pHs = findAttribute(attNames, attValues, attCount, pRessName)) != NULL) {
									//var->setRessource(pName, hexStringToByteArray(pHs));

                  // TODO: remove this
									if(strcmp(pName, "flame")==0) {
								    Flam3Reader *reader=new Flam3Reader();
								  	Flame **flames=NULL;
								  	int flameCount=0;
								  	char *flameXML=hexStringToByteArray(pHs);
								  	if(flameXML!=NULL && strlen(flameXML)>0) {
											reader->readFlamesfromXML(flameXML, &flames, &flameCount);
											free(flameXML);
											if(flameCount<1) {
												printf("No sub flame to render");
											}
											else {
											  Flame *flame=flames[0];
												var->setRessource(pName, flame);
											}
								  	}
									}
								}
							}
						}
					}
				}
			}
		}
		freeStrArray(attNames, attCount);
		freeStrArray(attValues, attCount);
	}

	void parseFlameAttributes(Flame *pFlame, char *pXML) {
		int attCount;
		char **attNames, **attValues;
		parseAttributes(pXML, &attNames, &attValues, &attCount);
		char *hs;
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_SIZE)) != NULL) {
			char **s;
			int sCount;
			split(hs, " ", &s, &sCount);
			if (sCount != 2) {
				printf("Invalid size %s\n", hs);
			}
			else {
				pFlame->width = atoi(s[0]);
				pFlame->height = atoi(s[1]);
			}
			freeStrArray(s, sCount);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_CENTER)) != NULL) {
			char **s;
			int sCount;
			split(hs, " ", &s, &sCount);
			if (sCount != 2) {
				printf("Invalid center %s\n", hs);
			}
			else {
				pFlame->centreX = atof(s[0]);
				pFlame->centreY = atof(s[1]);
			}
			freeStrArray(s, sCount);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_SCALE)) != NULL) {
			pFlame->pixelsPerUnit = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_ROTATE)) != NULL) {
			pFlame->camRoll = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_OVERSAMPLE)) != NULL) {
			pFlame->spatialOversample = atoi(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_COLOR_OVERSAMPLE)) != NULL) {
			pFlame->colorOversample = atoi(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_FILTER)) != NULL) {
			pFlame->spatialFilterRadius = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_QUALITY)) != NULL) {
			pFlame->sampleDensity = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_BACKGROUND)) != NULL) {
			char **s;
			int sCount;
			split(hs, " ", &s, &sCount);
			if (sCount != 3) {
				printf("Invalid color %s\n", hs);
			}
			else {
				pFlame->bgColorRed = roundColor(255.0f * atof(s[0]));
				pFlame->bgColorGreen = roundColor(255.0f * atof(s[1]));
				pFlame->bgColorBlue = roundColor(255.0f * atof(s[2]));
			}
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_BRIGHTNESS)) != NULL) {
			pFlame->brightness = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_GAMMA)) != NULL) {
			pFlame->gamma = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_GAMMA_THRESHOLD)) != NULL) {
			pFlame->gammaThreshold = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_CAM_PERSP)) != NULL) {
			pFlame->camPerspective = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_CAM_PERSPECTIVE)) != NULL) {
			pFlame->camPerspective = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_CAM_ZPOS)) != NULL) {
			pFlame->camZ = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_CAM_DOF)) != NULL) {
			pFlame->camDOF = atof(hs);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_CAM_PITCH)) != NULL) {
			pFlame->camPitch = atof(hs) * 180.0f / M_PI;
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_CAM_YAW)) != NULL) {
			pFlame->camYaw = atof(hs) * 180.0f / M_PI;
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_CAM_ZOOM)) != NULL) {
			pFlame->camZoom = atof(hs);
		}
		// preserve-z
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_NEW_LINEAR)) != NULL) {
			pFlame->preserveZ = (strcmp("1", hs) == 0);
		}
		if ((hs = findAttribute(attNames, attValues, attCount, ATTR_PRESERVE_Z)) != NULL) {
			pFlame->preserveZ = (strcmp("1", hs) == 0);
		}
		freeStrArray(attNames, attCount);
		freeStrArray(attValues, attCount);
	}

	char* hexStringToByteArray(char* pHexStr) {
		if (pHexStr != NULL && strlen(pHexStr) > 0) {
			int l = strlen(pHexStr);
			if ((l % 2) != 0) {
				printf("Illegal hex str %s\n", pHexStr);
				return NULL;
			}
			char *b = (char*)malloc((l / 2 + 1) * sizeof(char));
			b[l / 2] = '\0';
			int k = 0;
			for (int i = 0; i < l; i++) {
				char c = pHexStr[i];
				int v;
				switch (c) {
				case '0':
					v = 0;
					break;
				case '1':
					v = 1;
					break;
				case '2':
					v = 2;
					break;
				case '3':
					v = 3;
					break;
				case '4':
					v = 4;
					break;
				case '5':
					v = 5;
					break;
				case '6':
					v = 6;
					break;
				case '7':
					v = 7;
					break;
				case '8':
					v = 8;
					break;
				case '9':
					v = 9;
					break;
				case 'A':
					v = 10;
					break;
				case 'B':
					v = 11;
					break;
				case 'C':
					v = 12;
					break;
				case 'D':
					v = 13;
					break;
				case 'E':
					v = 14;
					break;
				case 'F':
					v = 15;
					break;
				default:
					printf("Illegal hex char: %c\n", c);
					return NULL;
				}
				if ((i % 2) != 0) {
					b[i / 2] = (k + v);
				}
				else {
					k = v * 16;
				}
			}
			return b;
		}
		else {
			return NULL;
		}
	}

	VariationFactory *variationFactory;

};

#endif // __JWF_FLAM3_READER_H__
