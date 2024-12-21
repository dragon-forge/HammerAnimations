package org.zeith.hammeranims.standalone.wasm.itfs.anim;

import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSString;
import org.zeith.hammeranims.api.animation.LoopMode;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.geometry.model.GeometryPose;
import org.zeith.hammeranims.standalone.ConfigureException;
import org.zeith.hammeranims.standalone.utils.Cast;
import shaded.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class AnimationState
		implements IAnimatedObject, HAAnimation
{
	protected final AnimationSystem system;
	
	private final JSString loopMode;
	public final double expectedDuration;
	
	public AnimationState(List<ConfiguredAnimation> animations)
	{
		var sys = AnimationSystem.builder(this);
		for(int layerId = 0; layerId < animations.size(); layerId++)
			sys.addLayers(new AnimationLayer.Builder(Integer.toString(layerId)));
		this.system = sys.build();
		
		LoopMode lm = LoopMode.ONCE;
		if(!animations.isEmpty()) lm = animations.getFirst().loopMode;
		loopMode = JSString.valueOf(lm.name());
		
		double duration = 0;
		int lId = 0;
		for(ConfiguredAnimation a : animations)
		{
			int layerId = lId++;
			if(!system.startAnimationAt(Integer.toString(layerId), a))
				throw new ConfigureException("Unable to start animation " + a.getLocation());
			duration = Math.max(duration, a.getAnimation().getData().getLengthSeconds());
		}
		
		expectedDuration = duration;
	}
	
	@Override
	public GeometryPose poseAt(double time)
	{
		system.setTime(time);
		for(AnimationLayer l : system.getLayers())
			l.tick(time);
		GeometryPose pose = new GeometryPose();
		system.applyAnimation(0, pose);
		return pose;
	}
	
	@Override
	public JSObject mix(JSObject other)
	{
		List<ConfiguredAnimation> animations = new ArrayList<>();
		for(AnimationLayer l : system.getLayers())
			animations.add(l.currentAnimation.config.copy());
		for(AnimationLayer l : Cast.<AnimationState>cast(other).system.getLayers())
			animations.add(l.currentAnimation.config.copy());
		return new AnimationState(animations);
	}
	
	@Override
	public double getExpectedDuration()
	{
		return expectedDuration;
	}
	
	@Override
	public JSString getLoopMode()
	{
		return loopMode;
	}
	
	@Override
	public void setupSystem(AnimationSystem.Builder builder)
	{
	}
	
	@Override
	public AnimationSystem getAnimationSystem()
	{
		return system;
	}
	
	@Override
	public Vec3d getAnimatedObjectPosition()
	{
		return Vec3d.ZERO;
	}
}