package org.zeith.hammeranims.api.geometry.model;

import org.zeith.hammeranims.api.animation.data.*;
import org.zeith.hammeranims.api.animation.interp.*;

import java.util.*;
import java.util.function.Predicate;

public class GeometryPose
{
	protected final Map<String, GeometryTransforms> boneTransforms = new HashMap<>();
	protected final Map<String, GeometryTransforms> boneTransformsView = Collections.unmodifiableMap(boneTransforms);
	protected final Predicate<String> availableBones;
	
	public GeometryPose(Predicate<String> availableBones)
	{
		this.availableBones = availableBones;
	}
	
	public void reset()
	{
		boneTransforms.clear();
	}
	
	public void apply(IAnimationData animation, BlendMode mode, float weight, Query query)
	{
		for(Map.Entry<String, BoneAnimation> entry : animation.getBoneAnimations().entrySet())
		{
			String bone = entry.getKey();
			if(!availableBones.test(bone)) continue;
			boneTransforms.put(bone, entry.getValue().apply(query, mode, weight, boneTransforms.get(bone)));
		}
	}
	
	public Map<String, GeometryTransforms> getBoneTransforms()
	{
		return boneTransformsView;
	}
}