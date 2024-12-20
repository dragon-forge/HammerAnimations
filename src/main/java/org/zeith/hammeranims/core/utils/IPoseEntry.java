package org.zeith.hammeranims.core.utils;

import shaded.joml.Matrix3f;
import shaded.joml.Matrix4f;

public interface IPoseEntry
{
	Matrix4f getPose();
	
	Matrix3f getNormal();
}