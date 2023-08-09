package org.zeith.hammeranims.api.animation.data;

import net.minecraft.world.phys.Vec3;
import org.zeith.hammeranims.api.animation.AnimationLocation;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.geometry.model.GeometryTransforms;
import org.zeith.hammerlib.util.java.functions.Function3;
import org.zeith.hammerlib.util.shaded.json.JSONObject;

import javax.annotation.*;

public class BoneAnimation
{
	public final @Nonnull Vec3Animation rotation;
	public final @Nonnull Vec3Animation position;
	public final @Nonnull Vec3Animation scale;
	
	public BoneAnimation(@Nullable Vec3Animation rotation, @Nullable Vec3Animation position, @Nullable Vec3Animation scale)
	{
		this.rotation = rotation != null ? rotation : Vec3Animation.ZERO;
		this.position = position != null ? position : Vec3Animation.ZERO;
		this.scale = scale != null ? scale : Vec3Animation.ONE;
	}
	
	public GeometryTransforms get(Query query)
	{
		return new GeometryTransforms(
				getTranslation(query),
				getRotation(query),
				getScale(query)
		);
	}
	
	public GeometryTransforms apply(Query query, BlendMode blending, float weight, GeometryTransforms transforms)
	{
		if(transforms == null) transforms = GeometryTransforms.createDefault();
		
		Function3<Vec3, Vec3, Float, Vec3> tf = blending.additiveTransform;
		transforms.translation = tf.apply(transforms.translation, getTranslation(query), weight);
		transforms.rotation = tf.apply(transforms.rotation, getRotation(query), weight);
		
		transforms.scale = blending.multiplicativeTransform.apply(transforms.scale, getScale(query), weight);
		
		return transforms;
	}
	
	public Vec3 getTranslation(Query query)
	{
		return position.get(query);
	}
	
	public Vec3 getScale(Query query)
	{
		return scale.get(query);
	}
	
	public Vec3 getRotation(Query query)
	{
		return rotation.get(query);
	}
	
	public static BoneAnimation parse(AnimationLocation anim, JSONObject bone)
	{
		if(bone.has("relative_to"))
			anim.warn(
					"Warning: Detected unsupported feature: relative_to (" + bone.opt("relative_to") + ")! Ignoring.");
		
		Vec3Animation rotation = Vec3Animation.parse(bone.opt("rotation"));
		Vec3Animation position = Vec3Animation.parse(bone.opt("position"));
		Vec3Animation scale = Vec3Animation.parse(bone.opt("scale"));
		if(rotation == null && position == null && scale == null) return null;
		return new BoneAnimation(rotation, position, scale);
	}
	
	@Override
	public String toString()
	{
		return "BoneAnimation{" +
				"rotation=" + rotation +
				", position=" + position +
				", scale=" + scale +
				'}';
	}
}