package org.zeith.hammeranims.core.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import org.lwjgl.system.MemoryUtil;
import org.zeith.hammeranims.joml.*;

import java.nio.FloatBuffer;

public interface IPoseEntry
{
	FloatBuffer FB9 = MemoryUtil.memAllocFloat(9);
	FloatBuffer FB16 = MemoryUtil.memAllocFloat(16);
	
	Matrix4f getPose();
	
	Matrix3f getNormal();
	
	default void write(PoseStack.Pose pose)
	{
		pose.pose().load(getPose().get(FB16));
		pose.normal().load(getNormal().get(FB9));
	}
	
	static IPoseEntry read(PoseStack.Pose p0)
	{
		p0.pose().store(FB16);
		p0.normal().store(FB9);
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
}