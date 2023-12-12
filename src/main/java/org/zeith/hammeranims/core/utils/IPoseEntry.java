package org.zeith.hammeranims.core.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.api.distmarker.*;
import org.lwjgl.system.MemoryUtil;
import org.zeith.hammeranims.joml.*;

import java.nio.FloatBuffer;

public interface IPoseEntry
{
	FloatBuffer FB9 = MemoryUtil.memAllocFloat(9);
	FloatBuffer FB16 = MemoryUtil.memAllocFloat(16);
	
	Matrix4f getPose();
	
	Matrix3f getNormal();
	
	static IPoseEntry read(MatrixStack.Entry p0)
	{
		p0.pose().store(FB16);
		
		net.minecraft.util.math.vector.Matrix3f c = p0.normal();
		FB9.put(bufferIndexM3(0, 0), c.m00);
		FB9.put(bufferIndexM3(0, 1), c.m01);
		FB9.put(bufferIndexM3(0, 2), c.m02);
		FB9.put(bufferIndexM3(1, 0), c.m10);
		FB9.put(bufferIndexM3(1, 1), c.m11);
		FB9.put(bufferIndexM3(1, 2), c.m12);
		FB9.put(bufferIndexM3(2, 0), c.m20);
		FB9.put(bufferIndexM3(2, 1), c.m21);
		FB9.put(bufferIndexM3(2, 2), c.m22);
		
		Matrix4f pose = new Matrix4f(FB16);
		Matrix3f norm = new Matrix3f(FB9);
		
		return new IPoseEntry()
		{
			@Override
			public Matrix4f getPose()
			{
				return pose;
			}
			
			@Override
			public Matrix3f getNormal()
			{
				return norm;
			}
		};
	}
	
	@OnlyIn(Dist.CLIENT)
	static int bufferIndexM3(int p_226594_0_, int p_226594_1_)
	{
		return p_226594_1_ * 3 + p_226594_0_;
	}
}