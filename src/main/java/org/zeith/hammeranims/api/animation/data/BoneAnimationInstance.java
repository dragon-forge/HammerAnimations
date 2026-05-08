package org.zeith.hammeranims.api.animation.data;

import dev.zeith.lzvm.LzVariableStore;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.geometry.model.GeometryTransforms;
import org.zeith.hammerlib.util.java.functions.Function3;

import javax.annotation.Nonnull;

public class BoneAnimationInstance
{
	public final @Nonnull Vec3Animation rotation;
	public final @Nonnull Vec3Animation position;
	public final @Nonnull Vec3Animation scale;
	
	public BoneAnimationInstance(BoneAnimation animation, LzVariableStore vars)
	{
		this.rotation = new Vec3Animation(animation.rotation, vars);
		this.position = new Vec3Animation(animation.position, vars);
		this.scale = new Vec3Animation(animation.scale, vars);
	}
	
	public GeometryTransforms get()
	{
		return new GeometryTransforms(
				getTranslation(),
				getRotation(),
				getScale()
		);
	}
	
	public GeometryTransforms apply(BlendMode blending, float weight, GeometryTransforms transforms)
	{
		if(transforms == null) transforms = GeometryTransforms.createDefault();
		
		Function3<Vec3, Vec3, Float, Vec3> tf = blending.additiveTransform;
		transforms.translation = tf.apply(transforms.translation, getTranslation(), weight);
		transforms.rotation = tf.apply(transforms.rotation, getRotation(), weight);
		
		transforms.scale = blending.multiplicativeTransform.apply(transforms.scale, getScale(), weight);
		
		return transforms;
	}
	
	public Vec3 getTranslation()
	{
		Vector3d v = position.get();
		return new Vec3(v.x, v.y, v.z);
	}
	
	public Vec3 getScale()
	{
		Vector3d v = scale.get();
		return new Vec3(v.x, v.y, v.z);
	}
	
	public Vec3 getRotation()
	{
		Vector3d v = rotation.get();
		return new Vec3(v.x, v.y, v.z);
	}
}