package org.zeith.hammeranims.standalone.utils;

import org.zeith.hammeranims.api.animsys.*;
import shaded.util.math.Vec3d;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.geometry.model.GeometryPose;
import org.zeith.hammeranims.standalone.IProcedure;
import org.zeith.hammeranims.standalone.wasm.itfs.anim.HAAnimation;

import java.time.Duration;
import java.util.List;

public class AnimationState
		implements IAnimatedObject, HAAnimation
{
	protected final AnimationSystem system;
	
	public final double expectedDuration;
	public final boolean loop;
	
	public AnimationState(List<ConfiguredAnimation> animations)
	{
		var sys = AnimationSystem.builder(this);
		for(int layerId = 0; layerId < animations.size(); layerId++)
			sys.addLayers(new AnimationLayer.Builder(Integer.toString(layerId)));
		this.system = sys.build();

//		this.loop = animations.getFirst().configure().getAnimation().getData().getLoopMode() == LoopMode.LOOP;
		this.loop = true;
		
		double duration = 0;
		int lId = 0;
		for(ConfiguredAnimation a : animations)
		{
			int layerId = lId++;
			a = a.transitionTime(Duration.ZERO);
			if(!system.startAnimationAt(Integer.toString(layerId), a))
				throw new IProcedure.ConfigureException("Unable to start animation " + a.getLocation());
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
	public double getExpectedDuration()
	{
		return expectedDuration;
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