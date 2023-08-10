package org.zeith.hammeranims.core.client.model;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.*;
import net.minecraftforge.fml.relauncher.*;
import org.zeith.hammeranims.api.geometry.model.RenderData;

import java.util.List;

public class ModelBoneF
{
	/** The size of the texture file's width in pixels. */
	public float textureWidth;
	/** The size of the texture file's height in pixels. */
	public float textureHeight;
	
	public float rotationPointX, rotationPointY, rotationPointZ;
	public float rotateAngleX, rotateAngleY, rotateAngleZ;
	public double scaleX = 1.0F, scaleY = 1.0F, scaleZ = 1.0F;
	public double offsetX, offsetY, offsetZ;
	
	private boolean compiled;
	
	/** The GL display list rendered by the Tessellator for this model */
	private int displayList;
	
	public boolean mirror;
	public boolean showModel;
	
	/** Makes this and all child bones hidden. */
	public boolean isHidden;
	
	/** Makes this bone invisible, but all child bones will still render. */
	public boolean isThisBoneInvisible;
	
	public ModelBoneF parent;
	public List<ModelCubeF> cubeList;
	public List<ModelBoneF> childModels;
	public final String boneName;
	
	public ModelBoneF(String boneName)
	{
		showModel = true;
		cubeList = Lists.newArrayList();
		this.boneName = boneName;
	}
	
	public void register(GeometricModelImpl model)
	{
		// IF there is no parent, this is the root.
		if(parent == null) model.rootBones.add(this);
		
		model.basePose.register(this);
	}
	
	public void resolveOffsets()
	{
		if(parent != null)
		{
			offsetX -= parent.offsetX;
			offsetY -= parent.offsetY;
			offsetZ -= parent.offsetZ;

			rotationPointX -= parent.rotationPointX;
			rotationPointY -= parent.rotationPointY;
			rotationPointZ -= parent.rotationPointZ;
		}
		
		if(childModels != null)
			for(ModelBoneF c : childModels)
				c.resolveOffsets();
	}
	
	/**
	 * Sets the current box's rotation points and rotation angles to another box.
	 */
	public void addChild(ModelBoneF child)
	{
		if(this.childModels == null) this.childModels = Lists.newArrayList();
		this.childModels.add(child);
		child.parent = this;
	}
	
	public ModelBoneF addBox(float offX, float offY, float offZ, float width, float height, float depth, CubeUVs uv, float inflate, boolean flipFaces)
	{
		this.cubeList.add(new ModelCubeF(this, uv, offX, offY, offZ, width, height, depth, inflate, flipFaces));
		return this;
	}
	
	public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn)
	{
		this.rotationPointX = rotationPointXIn;
		this.rotationPointY = rotationPointYIn;
		this.rotationPointZ = rotationPointZIn;
	}
	
	@SideOnly(Side.CLIENT)
	public void render(RenderData data)
	{
		if(!this.isHidden)
		{
			if(this.showModel)
			{
				if(!this.compiled) this.bake();
				
				GlStateManager.pushMatrix();
				GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);
				
				if(this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F)
				{// No rotation is found
					if(this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F)
					{// Rotation point not found
						if(this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F)
						{// Scale found
							GlStateManager.translate(this.scaleX * 0.0625f, this.scaleY * 0.0625f, this.scaleZ * 0.0625f);
							GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
							GlStateManager.translate(-this.scaleX * 0.0625f, -this.scaleY * 0.0625f, -this.scaleZ * 0.0625f);
						}
						
						if(!isThisBoneInvisible) GlStateManager.callList(this.displayList);
						
						if(this.childModels != null) for(ModelBoneF child : this.childModels) child.render(data);
					} else
					{// Rotation point is present
						GlStateManager.translate(this.rotationPointX, this.rotationPointY, this.rotationPointZ);
						
						if(this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F)
						{// Scale found
							GlStateManager.translate(this.scaleX * 0.0625f, this.scaleY * 0.0625f, this.scaleZ * 0.0625f);
							GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
							GlStateManager.translate(-this.scaleX * 0.0625f, -this.scaleY * 0.0625f, -this.scaleZ * 0.0625f);
						}
						
						if(!isThisBoneInvisible) GlStateManager.callList(this.displayList);
						
						if(this.childModels != null) for(ModelBoneF child : this.childModels) child.render(data);
					}
				} else
				{// Rotation is found
					GlStateManager.translate(this.rotationPointX, this.rotationPointY, this.rotationPointZ);
					
					if(this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F)
					{// Scale found
						GlStateManager.translate(this.scaleX * 0.0625f, this.scaleY * 0.0625f, this.scaleZ * 0.0625f);
						GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
						GlStateManager.translate(-this.scaleX * 0.0625f, -this.scaleY * 0.0625f, -this.scaleZ * 0.0625f);
					}
					
					// Apply rotation
					if(this.rotateAngleX != 0.0F || this.rotateAngleY != 0.0F || this.rotateAngleZ != 0.0F)
						GlStateManager.rotate(QuaternionHelper.create(rotateAngleX, rotateAngleY, rotateAngleZ, false));
					
					if(!isThisBoneInvisible) GlStateManager.callList(this.displayList);
					
					if(this.childModels != null) for(ModelBoneF child : this.childModels) child.render(data);
				}
				
				GlStateManager.popMatrix();
			}
		}
	}
	
	/**
	 * Compiles a GL display list for this model
	 */
	@SideOnly(Side.CLIENT)
	private void bake()
	{
		this.displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.glNewList(this.displayList, 4864);
		GlStateManager.pushMatrix();
//		GlStateManager.scale(-1, 1, 1);
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		for(ModelCubeF box : this.cubeList) box.bake(bufferbuilder);
		GlStateManager.popMatrix();
		GlStateManager.glEndList();
		this.compiled = true;
	}
	
	/**
	 * Returns the model renderer with the new texture parameters.
	 */
	public ModelBoneF setTextureSize(int textureWidthIn, int textureHeightIn)
	{
		this.textureWidth = (float) textureWidthIn;
		this.textureHeight = (float) textureHeightIn;
		return this;
	}
	
	public void dispose()
	{
		if(compiled)
		{
			GlStateManager.glDeleteLists(this.displayList, 1);
			compiled = false;
		}
	}
}