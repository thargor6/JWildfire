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
#ifndef JWF_APP_SETTINGS_H_
#define JWF_APP_SETTINGS_H_

#define MIN_THREAD_COUNT 1
#define MIN_WIDTH 16
#define MAX_WIDTH 16000
#define MIN_HEIGHT 16
#define MAX_HEIGHT 16000
#define MIN_FILTER_RADIUS 0.0
#define MAX_FILTER_RADIUS 100.0
#define MIN_DENSITY 1.0
#define MAX_DENSITY 1000000.0

#include "windows.h"

class AppSettings {
public:
	AppSettings() {
		SYSTEM_INFO sysinfo;
		GetSystemInfo(&sysinfo);
		maxThreadCount = sysinfo.dwNumberOfProcessors;
		if (maxThreadCount < 1)
			maxThreadCount = 1;
		threadCount = maxThreadCount;
		flameFilename = NULL;
		outputFilename = NULL;
		outputHDRFilename = NULL;
		outputWidth = 800;
		outputHeight = 600;
		sampleDensity = 100.0;
		reportStatus = false;
		withAlpha = false;
	}

	void setThreadCount(const int pThreadCount) {
		if (pThreadCount >= MIN_THREAD_COUNT && pThreadCount <= maxThreadCount)
			threadCount = pThreadCount;
		else
			printf("Invalid thread count %d (%d...%d)\n", pThreadCount, MIN_THREAD_COUNT, maxThreadCount);
	}

	void setOutputWidth(const int pOutputWidth) {
		if (pOutputWidth >= MIN_WIDTH && pOutputWidth <= MAX_WIDTH)
			outputWidth = pOutputWidth;
		else
			printf("Invalid output width %d (%d...%d)\n", pOutputWidth, MIN_WIDTH, MAX_WIDTH);
	}

	void setOutputHeight(const int pOutputHeight) {
		if (pOutputHeight >= MIN_HEIGHT && pOutputHeight <= MAX_HEIGHT)
			outputHeight = pOutputHeight;
		else
			printf("Invalid output width %d (%d...%d)\n", pOutputHeight, MIN_HEIGHT, MAX_HEIGHT);
	}

	int getThreadCount() {
		return threadCount;
	}

	int getOutputWidth() {
		return outputWidth;
	}

	int getOutputHeight() {
		return outputHeight;
	}

	char *getFlameFilename() {
		return flameFilename;
	}

	char *getOutputFilename() {
		return outputFilename;
	}

	char *getOutputHDRFilename() {
		return outputHDRFilename;
	}

	JWF_FLOAT getSampleDensity() {
		return sampleDensity;
	}

	bool isReportStatus() {
		return reportStatus;
	}

	bool isWithAlpha() {
		return withAlpha;
	}

	void setSampleDensity(const JWF_FLOAT pSampleDensity) {
		if (pSampleDensity >= MIN_DENSITY - EPSILON && pSampleDensity <= MAX_DENSITY + EPSILON)
			sampleDensity = pSampleDensity;
		else
			printf("Invalid sample density %f (%f...%f)\n", pSampleDensity, MIN_DENSITY, MAX_DENSITY);
	}

	int parseCmlLineArgs(int pArgc, char *pArgv[]) {
		int i = 1;
		while (i < pArgc) {
			if (strcmp(pArgv[i], "-flameFilename") == 0 || strcmp(pArgv[i], "-f") == 0) {
				if (i == pArgc - 1) {
					printf("No value for parameter <%s> specified\n", pArgv[i]);
					return -1;
				}
				setParamValue(&flameFilename, pArgv[++i]);
			}
			else if (strcmp(pArgv[i], "-threadCount") == 0 || strcmp(pArgv[i], "-t") == 0) {
				if (i == pArgc - 1) {
					printf("No value for parameter <%s> specified\n", pArgv[i]);
					return -1;
				}
				setThreadCount(atoi(pArgv[++i]));
			}
			else if (strcmp(pArgv[i], "-outputFilename") == 0 || strcmp(pArgv[i], "-o") == 0) {
				if (i == pArgc - 1) {
					printf("No value for parameter <%s> specified\n", pArgv[i]);
					return -1;
				}
				setParamValue(&outputFilename, pArgv[++i]);
			}
			else if (strcmp(pArgv[i], "-outputHDRFilename") == 0 || strcmp(pArgv[i], "-hdr") == 0) {
				if (i == pArgc - 1) {
					printf("No value for parameter <%s> specified\n", pArgv[i]);
					return -1;
				}
				setParamValue(&outputHDRFilename, pArgv[++i]);
			}
			else if (strcmp(pArgv[i], "-outputWidth") == 0 || strcmp(pArgv[i], "-w") == 0) {
				if (i == pArgc - 1) {
					printf("No value for parameter <%s> specified\n", pArgv[i]);
					return -1;
				}
				setOutputWidth(atoi(pArgv[++i]));
			}
			else if (strcmp(pArgv[i], "-outputHeight") == 0 || strcmp(pArgv[i], "-h") == 0) {
				if (i == pArgc - 1) {
					printf("No value for parameter <%s> specified\n", pArgv[i]);
					return -1;
				}
				setOutputHeight(atoi(pArgv[++i]));
			}
			else if (strcmp(pArgv[i], "-sampleDensity") == 0 || strcmp(pArgv[i], "-d") == 0) {
				if (i == pArgc - 1) {
					printf("No value for parameter <%s> specified\n", pArgv[i]);
					return -1;
				}
				setSampleDensity(atof(pArgv[++i]));
			}
			else if (strcmp(pArgv[i], "-help") == 0 || strcmp(pArgv[i], "-h") == 0) {
				return -1;
			}
			else if (strcmp(pArgv[i], "-reportStatus") == 0 || strcmp(pArgv[i], "-r") == 0) {
				reportStatus = true;
			}
			else if (strcmp(pArgv[i], "-withAlpha") == 0 || strcmp(pArgv[i], "-a") == 0) {
				withAlpha = true;
			}
			else {
				printf("Unkown parameter %s\n", pArgv[i]);
			}
			i++;
		}

		return validateSettings();
	}

	void showUsage(const char *exename) {
		int pos = -1;
		for (int i = strlen(exename) - 1; i >= 0; i--) {
			if (exename[i] == '/' || exename[i] == '\\') {
				pos = i;
				break;
			}
		}
		printf("Usage: %s [-<param> <value>]\n", pos > 0 ? &(exename[pos + 1]) : exename);
		printf("Params:\n");
		printf("  -threadCount (or -t): int (%d...%d)\n", MIN_THREAD_COUNT, maxThreadCount);
		printf("  -flameFilename (or -f): string\n");
		printf("  -outputFilename (or -o): string\n");
		printf("  -outputHDRFilename (or -hdr): string\n");
		printf("  -outputWidth (or -w): int (%d...%d)\n", MIN_WIDTH, MAX_WIDTH);
		printf("  -outputHeight (or -h): int (%d...%d)\n", MIN_HEIGHT, MAX_HEIGHT);
		printf("  -sampleDensity (or -d): JWF_FLOAT (%f...%f)\n", MIN_DENSITY, MAX_DENSITY);
		printf("  -reportStatus (or -r): void\n");
		printf("  -withAlpha (or -a): void\n");
		printf("  -help (or -h): void\n");
		printf("Required param: -flameFilename\n");
	}

	void dump() {
		printf("Settings {\n");
		printf("  threadCount: %d\n", threadCount);
		printf("  flameFilename: %s\n", flameFilename);
		printf("  outputFilename: %s\n", outputFilename);
		printf("  outputHDRFilename: %s\n", outputHDRFilename);
		printf("  outputWidth: %d\n", outputWidth);
		printf("  outputHeight: %d\n", outputHeight);
		printf("  sampleDensity: %f\n", sampleDensity);
		printf("  reportStatus: %d\n", reportStatus);
		printf("  withAlpha: %d\n", withAlpha);
		printf("}\n");
	}

private:
	char *flameFilename;
	char *outputFilename;
	char *outputHDRFilename;
	int maxThreadCount;
	int threadCount;
	int outputWidth;
	int outputHeight;
	JWF_FLOAT sampleDensity;
	bool reportStatus;
	bool withAlpha;

	void setParamValue(char **pDst, char *pSrc) {
		if ((*pDst) != NULL) {
			free(*pDst);
			*pDst = NULL;
		}
		*pDst = (char*) malloc((strlen(pSrc) + 1) * sizeof(char));
		strcpy(*pDst, pSrc);
	}

	int validateSettings() {
		if (flameFilename == NULL) {
			printf("No flame file specified\n");
			return -1;
		}
		return 0;
	}

};

#undef MIN_THREAD_COUNT
#undef MAX_WIDTH
#undef MIN_WIDTH
#undef MAX_HEIGHT
#undef MIN_HEIGHT
#undef MAX_FILTER_RADIUS
#undef MIN_FILTER_RADIUS
#undef MAX_DENSITY
#undef MIN_DENSITY

#endif // JWF_APP_SETTINGS_H_
