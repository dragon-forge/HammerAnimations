package org.zeith.hammeranims.core.utils;

import com.google.common.collect.Queues;
import net.minecraftforge.fml.relauncher.*;
import org.lwjgl.opengl.GL11;
import org.zeith.hammeranims.joml.Math;
import org.zeith.hammeranims.joml.*;

import java.nio.FloatBuffer;
import java.util.Deque;

public class PoseStack
{
	private static final FloatBuffer BUF_FLOAT_16 = MinecraftHelper.createFloatBuf(16);
	
	private final Deque<Entry> stack = Queues.newArrayDeque();
	
	{
		Matrix4f matrix4f = new Matrix4f();
		Matrix3f matrix3f = new Matrix3f();
		stack.add(new Entry(matrix4f, matrix3f));
	}
	
	public void reset()
	{
		Matrix4f matrix4f = new Matrix4f();
		Matrix3f matrix3f = new Matrix3f();
		stack.clear();
		stack.add(new Entry(matrix4f, matrix3f));
	}
	
	@SideOnly(Side.CLIENT)
	public PoseStack fromGL()
	{
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, BUF_FLOAT_16);
		Matrix4f poseMatrix = new Matrix4f(BUF_FLOAT_16);
		Matrix3f normalMatrix = new Matrix3f(poseMatrix).transpose().invert();
		Entry last = this.stack.getLast();
		last.normal.mul(normalMatrix);
		last.pose.mul(poseMatrix);
		return this;
	}
	
	@SideOnly(Side.CLIENT)
	public void toGL()
	{
		Entry last = this.stack.getLast();
		Matrix4f poseMatrix = last.pose;
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadMatrix(poseMatrix.get(BUF_FLOAT_16));
	}
	
	@SideOnly(Side.CLIENT)
	public void mulGL()
	{
		Entry last = this.stack.getLast();
		Matrix4f poseMatrix = last.pose;
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glMultMatrix(poseMatrix.get(BUF_FLOAT_16));
	}
	
	public void translate(double pX, double pY, double pZ)
	{
		this.translate((float) pX, (float) pY, (float) pZ);
	}
	
	public void translate(float pX, float pY, float pZ)
	{
		Entry last = this.stack.getLast();
		last.pose.translate(pX, pY, pZ);
	}
	
	public void scale(float pX, float pY, float pZ)
	{
		Entry last = this.stack.getLast();
		last.pose.scale(pX, pY, pZ);
		if(pX == pY && pY == pZ)
		{
			if(pX > 0.0F) return;
			
			last.normal.scale(-1.0F);
		}
		
		float f = 1.0F / pX;
		float f1 = 1.0F / pY;
		float f2 = 1.0F / pZ;
		float f3 = Math.invsqrt(f * f1 * f2);
		last.normal.scale(f3 * f, f3 * f1, f3 * f2);
	}
	
	public void mulPose(Quaternionf pQuaternion)
	{
		Entry last = this.stack.getLast();
		last.pose.rotate(pQuaternion);
		last.normal.rotate(pQuaternion);
	}
	
	public void rotateAround(Quaternionf pQuaternion, float pX, float pY, float pZ)
	{
		Entry last = this.stack.getLast();
		last.pose.rotateAround(pQuaternion, pX, pY, pZ);
		last.normal.rotate(pQuaternion);
	}
	
	public void pushPose()
	{
		Entry last = this.stack.getLast();
		this.stack.addLast(new Entry(new Matrix4f(last.pose), new Matrix3f(last.normal)));
	}
	
	public void popPose()
	{
		this.stack.removeLast();
	}
	
	public Entry last()
	{
		return this.stack.getLast();
	}
	
	public boolean clear()
	{
		return this.stack.size() == 1;
	}
	
	public void setIdentity()
	{
		Entry last = this.stack.getLast();
		last.pose.identity();
		last.normal.identity();
	}
	
	public void mulPoseMatrix(Matrix4f pMatrix)
	{
		(this.stack.getLast()).pose.mul(pMatrix);
	}
	
	public static final class Entry
			implements IPoseEntry
	{
		final Matrix4f pose;
		final Matrix3f normal;
		
		Entry(Matrix4f pPose, Matrix3f pNormal)
		{
			this.pose = pPose;
			this.normal = pNormal;
		}
		
		@Override
		public Matrix4f getPose()
		{
			return this.pose;
		}
		
		@Override
		public Matrix3f getNormal()
		{
			return this.normal;
		}
	}
}