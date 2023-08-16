package org.zeith.hammeranims.core.client.model;

import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.utils.math.MathHelper;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.*;
import org.lwjgl.opengl.GL11;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.core.utils.PoseStack;

import java.util.*;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.*;

public class GeometricModelImpl
		implements IGeometricModel
{
	protected final ModelBoneF root;
	
	protected final Map<String, ModelBoneF> bones = new HashMap<>();
	
	public GeometricModelImpl(GeometryDataImpl root)
	{
		this.root = root.bakeRoot(new ModelBase()
		{
			@Override
			public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
			{
			}
		});
		registerBone(this.root);
	}
	
	protected void registerBone(ModelBoneF part)
	{
		bones.put(part.boxName, part);
		part.getChildren().values().forEach(this::registerBone);
	}
	
	public boolean hasBone(String bone)
	{
		return bones.containsKey(bone);
	}
	
	@Override
	public void resetPose()
	{
		for(ModelBoneF s : bones.values())
			s.reset();
	}
	
	GeometryPose emptyPose = new GeometryPose(this::hasBone);
	
	@Override
	public GeometryPose emptyPose()
	{
		emptyPose.reset();
		return emptyPose;
	}
	
	@Override
	public void applyPose(GeometryPose pose)
	{
		Map<String, GeometryTransforms> poseBones = pose.getBoneTransforms();
		
		for(String boneKey : bones.keySet())
		{
			ModelBoneF bone = bones.get(boneKey);
			if(bone == null) continue;
			bone.reset();
			
			GeometryTransforms add = poseBones.get(boneKey);
			if(add == null) continue;
			
			Vec3d translate = add.translation,
					rotate = add.rotation.scale(MathHelper.torad),
					scale = add.scale;
			
			bone.offset.add(
					(float) translate.x,
					(float) -translate.y,
					(float) translate.z
			);
			
			bone.getRotation().sub(
					(float) rotate.x,
					(float) rotate.y,
					(float) -rotate.z
			);
			
			bone.getScale().mul(
					(float) scale.x,
					(float) scale.y,
					(float) scale.z
			);
		}
	}
	
	public static final VertexFormat POSITION_TEX_LMAP_COLOR_NORMAL = new VertexFormat();
	
	static
	{
		POSITION_TEX_LMAP_COLOR_NORMAL.addElement(POSITION_3F);
		POSITION_TEX_LMAP_COLOR_NORMAL.addElement(TEX_2F);
		POSITION_TEX_LMAP_COLOR_NORMAL.addElement(TEX_2S); // lightmap
		POSITION_TEX_LMAP_COLOR_NORMAL.addElement(COLOR_4UB);
		POSITION_TEX_LMAP_COLOR_NORMAL.addElement(NORMAL_3B);
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderModel(RenderData data)
	{
		PoseStack pose = new PoseStack();
		pose.fromGL();
		
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		UtilsFX.bindTexture(data.texture);
		
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();
		bb.begin(GL11.GL_QUADS, POSITION_TEX_LMAP_COLOR);
		root.renderCubes(pose, IVertexRenderer.wrap(bb), data.combinedLightIn, data.combinedOverlayIn, data.red, data.green, data.blue, data.alpha);
		tess.draw();
		
		GlStateManager.popMatrix();
	}
	
	@Override
	public void dispose()
	{
	
	}
}