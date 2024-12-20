package org.zeith.hammeranims.api.geometry.model;

import shaded.json.JSONObject;
import org.teavm.jso.JSObject;
import org.teavm.jso.json.JSON;
import org.zeith.hammeranims.api.animation.data.*;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animsys.SerializableMask;
import org.zeith.hammeranims.api.animsys.layer.ILayerMask;
import org.zeith.hammeranims.standalone.wasm.itfs.anim.HAAnimationPose;

import java.util.*;
import java.util.function.Predicate;

public class GeometryPose
		implements HAAnimationPose
{
	protected final Map<String, GeometryTransforms> boneTransforms = new HashMap<>();
	protected final Map<String, GeometryTransforms> boneTransformsView = Collections.unmodifiableMap(boneTransforms);
	protected final Predicate<String> availableBones;
	
	public GeometryPose()
	{
		this(b -> true);
	}
	
	public GeometryPose(Predicate<String> availableBones)
	{
		this.availableBones = availableBones;
	}
	
	public void reset()
	{
		boneTransforms.clear();
	}
	
	public void apply(IAnimationData animation, ILayerMask mask, BlendMode mode, float weight, Query query)
	{
		for(Map.Entry<String, BoneAnimation> entry : animation.getBoneAnimations().entrySet())
		{
			String bone = entry.getKey();
			if(!availableBones.test(bone) || !mask.test(bone)) continue;
			boneTransforms.put(bone, entry.getValue().apply(query, mode, weight, boneTransforms.get(bone)));
		}
	}
	
	public void apply(SerializableMask animationMask, IAnimationData animation, ILayerMask mask, BlendMode mode, float weight, Query query)
	{
		Set<String> excludes = animationMask.getExcludes();
		SerializableMask.WeightFunction weightFun = animationMask.getBoneWeight();
		for(Map.Entry<String, BoneAnimation> entry : animation.getBoneAnimations().entrySet())
		{
			String bone = entry.getKey();
			if(!availableBones.test(bone) || !mask.test(bone) || excludes.contains(bone)) continue;
			boneTransforms.put(bone, entry.getValue().apply(query, mode, weight * weightFun.get(bone), boneTransforms.get(bone)));
		}
	}
	
	public GeometryPose copy()
	{
		GeometryPose c = new GeometryPose(availableBones);
		for(Map.Entry<String, GeometryTransforms> bone : boneTransforms.entrySet())
			c.boneTransforms.put(bone.getKey(), bone.getValue().copy());
		return c;
	}
	
	public GeometryTransforms getTransform(String bone)
	{
		if(!availableBones.test(bone)) return null;
		return boneTransforms.computeIfAbsent(bone, key -> GeometryTransforms.createDefault());
	}
	
	public Map<String, GeometryTransforms> getBoneTransforms()
	{
		return boneTransformsView;
	}
	
	public JSONObject toJson()
	{
		JSONObject o = new JSONObject();
		for(Map.Entry<String, GeometryTransforms> e : boneTransforms.entrySet())
			o.put(e.getKey(), e.getValue().toJson());
		return o;
	}
	
	@Override
	public JSObject getBones()
	{
		return JSON.parse(toJson().toString());
	}
}