package org.zeith.hammeranims.api.geometry.data;

import lombok.Value;
import org.zeith.hammeranims.core.utils.EnumFacing;

@Value
public class FaceUV
{
	float u1, u2;
	float v1, v2;
	EnumFacing normal;
}