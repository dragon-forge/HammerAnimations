package org.zeith.hammeranims.core.client.model;

import com.zeitheron.hammercore.utils.math.MathHelper;
import lombok.val;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.constrains.IBoneConstraints;
import org.zeith.hammeranims.api.geometry.constrains.IGeometryConstraints;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.client.render.*;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.core.utils.PoseStack;

import javax.annotation.Nullable;
import java.util.*;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.*;

public class GeometricModelImpl
		implements IGeometricModel
{
	protected final ModelBoneF root;
	protected final IGeometryContainer container;
	protected final IGeometryConstraints constraints;
	protected final Map<String, ModelBoneF> bones = new HashMap<>();
	protected final Map<String, IBoneConstraints> boneConstraints = new HashMap<>();
	protected final Map<String, ModelBoneF> locatorSources = new HashMap<>();
	
	public GeometricModelImpl(GeometryDataImpl root)
	{
		this.container = root.getContainer();
		this.constraints = container.getConstraints();
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
		boneConstraints.put(part.boxName, constraints.getConstraints(part.boxName));
		part.getChildren().values().forEach(this::registerBone);
		for(val loc : part.getLocators().entrySet())
			locatorSources.put(loc.getKey(), part);
	}
	
	@Override
	public Set<String> getBoneNames()
	{
		return bones.keySet();
	}
	
	@Override
	public IRenderableBone getRoot()
	{
		return root;
	}
	
	@Override
	public Collection<? extends IRenderableBone> getBones()
	{
		return bones.values();
	}
	
	@Nullable
	@Override
	public IRenderableBone getBone(String bone)
	{
		return bones.get(bone.toLowerCase(Locale.ROOT));
	}
	
	@Override
	public boolean hasBone(String bone)
	{
		return bones.containsKey(bone.toLowerCase(Locale.ROOT));
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
			add.applyConstraints(boneConstraints.get(boneKey));
			
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
			
			if(add.forceVertexType != null) bone.forceVertexType = add.forceVertexType;
			bone.renderCubes = !add.skipGeometry;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderModel(RenderData data)
	{
		PoseStack pose = data.pose;
		
		IVertexRenderer renderer = data.renderer;
		renderer.bind(data);
		renderer.begin(GL11.GL_QUADS, IVertexEmitter.FULL);
		root.render(pose,
				renderer,
				data.combinedLightIn, data.combinedOverlayIn,
				data.red, data.green, data.blue, data.alpha
		);
		renderer.upload();
		
		if(data.resetPoseAfterDraw) pose.reset();
	}
	
	@Override
	public void dispose()
	{
	}
}