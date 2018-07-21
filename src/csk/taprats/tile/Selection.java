/*
 * taprats -- an interactive design tool for computer-generated 
 *            Islamic patterns.
 *
 * Copyright (C) 2000 Craig S. Kaplan, all rights reserved
 *
 * email: csk@cs.washington.edu
 * www:   http://www.cs.washington.edu/homes/csk/taprats/
 *
 * You may not copy, redistribute or reuse this source code at
 * this time.  It is likely that in the future I will make the
 * source more freely available.  In the meantime, please be
 * patient, and contact me if you have questions about the use
 * of this source code or the compiled applet.
 *
 */

/* 
 * Selection.java
 *
 * A helper struct that holds information about a selection (see FeatureView).
 * Probably not used in the applet at all.
 */

package csk.taprats.tile;

class Selection
{
	int feature;
	int detail;

	Selection( int feature, int detail )
	{
		this.feature = feature;
		this.detail = detail;
	}
}
