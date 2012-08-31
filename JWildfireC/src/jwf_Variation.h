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

#ifndef __JWF_VARIATION_H__
#define __JWF_VARIATION_H__

#include "stdio.h"
#include "stdarg.h"
#include "jwf_XYZPoint.h"
#include "jwf_FlameTransformationContext.h"

class Variation {
public:
	float amount;

	Variation() {
    parameterCount=0;
    parameterNames=NULL;
	}

	virtual ~Variation() {

	}

	virtual const char* getName() const = 0;

	virtual const int getPriority() {
		return 0;
	}

	char **getParameterNames() {
    return parameterNames;
	};

	int getParameterCount() {
		return parameterCount;
	}

	virtual void initParameterNames(int pCount, ...) {
		if(pCount==0) {
			parameterCount=0;
			parameterNames=NULL;
		}
		else {
			parameterNames=(char**)malloc(pCount*sizeof(char*));
			parameterCount=pCount;
			va_list list;
			va_start ( list, pCount );
			for (int i=0; i < pCount; i++) {
				char *srcName=va_arg(list, char*);
				int len=strlen(srcName);
				char *dstName=(char*)malloc((len+1)*sizeof(char));
				memcpy(dstName, srcName, len);
				dstName[len]='\0';
				parameterNames[i]=dstName;
			}
			va_end(list);
		}
	}

	virtual char **getRessourceNames() {
		return NULL;
	}

	virtual int getRessourceCount() {
		return 0;
	}

	virtual void init(FlameTransformationContext *pContext, float pAmount) {

	}

	virtual void transform(FlameTransformationContext *pContext, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) = 0;

	virtual void setParameter(char *pName, float pValue) {

	}

	void dump() {
		printf("    Variation {\n");
		printf("      name=%s priority=%d amount=%.5f\n", getName(), getPriority(), amount);
		printf("    }\n");
	}

	virtual Variation* makeCopy() = 0;
protected:
	char **parameterNames;
	int parameterCount;

};

#endif // __JWF_VARIATION_H__
