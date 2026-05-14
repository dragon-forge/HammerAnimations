package org.zeith.hammeranims.api.animation.data;

import dev.zeith.lzvm.LzVariableStore;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.geometry.model.GeometryTransforms;
import shaded.joml.Vector3d;
import shaded.tuples.functions.Function3;
import shaded.util.math.Vec3d;

public class BoneAnimationInstance
{
	public final @NotNull Vec3Animation rotation;
	public final @NotNull Vec3Animation position;
	public final @NotNull Vec3Animation scale;
	
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
		
		Function3<Vec3d, Vec3d, Float, Vec3d> tf = blending.additiveTransform;
		transforms.translation = tf.apply(transforms.translation, getTranslation(), weight);
		transforms.rotation = tf.apply(transforms.rotation, getRotation(), weight);
		
		transforms.scale = blending.multiplicativeTransform.apply(transforms.scale, getScale(), weight);
		
		return transforms;
	}
	
	public Vec3d getTranslation()
	{
		Vector3d v = position.get();
		return new Vec3d(v.x, v.y, v.z);
	}
	
	public Vec3d getScale()
	{
		Vector3d v = scale.get();
		return new Vec3d(v.x, v.y, v.z);
	}
	
	public Vec3d getRotation()
	{
		Vector3d v = rotation.get();
		return new Vec3d(v.x, v.y, v.z);
	}
}