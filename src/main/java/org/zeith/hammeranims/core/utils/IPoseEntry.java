package org.zeith.hammeranims.core.utils;

import org.zeith.hammeranims.joml.*;

public interface IPoseEntry
{
	Matrix4f getPose();
	
	Matrix3f getNormal();
}