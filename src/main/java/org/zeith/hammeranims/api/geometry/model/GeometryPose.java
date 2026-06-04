package org.zeith.hammeranims.api.geometry.model;

import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.api.animation.data.BoneAnimationInstance;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animsys.SerializableMask;
import org.zeith.hammeranims.api.animsys.layer.*;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;

import java.util.*;
import java.util.function.*;

public class GeometryPose
{
	protected static final Predicate<String> ALLOW_ALL_BONES = b -> true;
	protected final Function<String, GeometryTransforms> TRANSFORM_FACTORY = this::createTransforms;
	protected final Map<String, GeometryTransforms> boneTransforms;
	protected final Map<String, GeometryTransforms> boneTransformsView;
	protected final Predicate<String> availableBones;
	
	protected @Setter Function<String, @Nullable VertexType> boneRenderTypes;
	
	public GeometryPose()
	{
		this(ALLOW_ALL_BONES);
	}
	
	public GeometryPose(Predicate<String> availableBones)
	{
		this.availableBones = availableBones;
		this.boneTransforms = new HashMap<>();
		this.boneTransformsView = Collections.unmodifiableMap(boneTransforms);
	}
	
	public GeometryPose(Predicate<String> availableBones, int prealloc)
	{
		this.availableBones = availableBones;
		this.boneTransforms = new HashMap<>(prealloc);
		this.boneTransformsView = Collections.unmodifiableMap(boneTransforms);
	}
	
	public GeometryTransforms createTransforms(String bone)
	{
		GeometryTransforms gtf = GeometryTransforms.createDefault();
		if(boneRenderTypes != null)
		{
			VertexType type = boneRenderTypes.apply(bone);
			if(type != null) gtf.forceVertexType = type;
		}
		return gtf;
	}
	
	public void reset()
	{
		boneTransforms.clear();
	}
	
	public void apply(ActiveAnimation animation, ILayerMask mask, BlendMode mode, float weight)
	{
		for(Map.Entry<String, BoneAnimationInstance> entry : animation.getBoneAnimations().entrySet())
		{
			String bone = entry.getKey();
			if(!availableBones.test(bone) || !mask.test(bone)) continue;
			boneTransforms.put(bone, entry.getValue().apply(mode, weight, getTransform(bone)));
		}
	}
	
	public void apply(SerializableMask animationMask, ActiveAnimation animation, ILayerMask mask, BlendMode mode, float weight)
	{
		Set<String> excludes = animationMask.getExcludes();
		SerializableMask.WeightFunction weightFun = animationMask.getBoneWeight();
		for(Map.Entry<String, BoneAnimationInstance> entry : animation.getBoneAnimations().entrySet())
		{
			String bone = entry.getKey();
			if(!availableBones.test(bone) || !mask.test(bone) || excludes.contains(bone)) continue;
			boneTransforms.put(bone, entry.getValue().apply(mode, weight * weightFun.get(bone), getTransform(bone)));
		}
	}
	
	protected GeometryPose newInstance()
	{
		GeometryPose origin = this;
		return new GeometryPose(availableBones)
		{
			@Override
			public GeometryTransforms createTransforms(String bone)
			{
				return origin.createTransforms(bone);
			}
		};
	}
	
	public GeometryPose copy()
	{
		GeometryPose copy = newInstance();
		for(Map.Entry<String, GeometryTransforms> bone : boneTransforms.entrySet())
			copy.boneTransforms.put(bone.getKey(), bone.getValue().copy());
		return copy;
	}
	
	public GeometryTransforms getTransform(String bone)
	{
		bone = bone.toLowerCase(Locale.ROOT);
		if(!availableBones.test(bone)) return null;
		return boneTransforms.computeIfAbsent(bone, TRANSFORM_FACTORY);
	}
	
	public Map<String, GeometryTransforms> getBoneTransforms()
	{
		return boneTransformsView;
	}
}