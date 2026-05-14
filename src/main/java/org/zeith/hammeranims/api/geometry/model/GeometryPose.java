package org.zeith.hammeranims.api.geometry.model;

import org.teavm.jso.JSObject;
import org.teavm.jso.core.*;
import org.teavm.jso.impl.JS;
import org.zeith.hammeranims.api.animation.data.BoneAnimationInstance;
import org.zeith.hammeranims.api.animation.interp.BlendMode;
import org.zeith.hammeranims.api.animsys.SerializableMask;
import org.zeith.hammeranims.api.animsys.layer.*;
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
	
	public void apply(ActiveAnimation animation, ILayerMask mask, BlendMode mode, float weight)
	{
		for(Map.Entry<String, BoneAnimationInstance> entry : animation.getBoneAnimations().entrySet())
		{
			String bone = entry.getKey();
			if(!availableBones.test(bone) || !mask.test(bone)) continue;
			boneTransforms.put(bone, entry.getValue().apply(mode, weight, boneTransforms.get(bone)));
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
			boneTransforms.put(bone, entry.getValue().apply(mode, weight * weightFun.get(bone), boneTransforms.get(bone)));
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
	
	@Override
	public JSObject getBones()
	{
		JSObject o = JSObjects.create();
		for(Map.Entry<String, GeometryTransforms> e : boneTransforms.entrySet())
			JS.set(o, JSString.valueOf(e.getKey()),  e.getValue().toJson());
		return o;
	}
}