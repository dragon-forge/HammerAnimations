package org.zeith.hammeranims.api.geometry.model;

import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ILayerMask;

import javax.annotation.Nullable;
import java.util.*;

public interface IGenericModel
{
	/**
	 * Creates a mask that will match any root bone or their children (or children of children etc)
	 */
	default ILayerMask maskAnyOfOrChildren(String... rootBones)
	{
		List<String> expanded = new ArrayList<>(Arrays.asList(rootBones));
		for(int i = 0; i < expanded.size(); i++)
		{
			IBone b = getBone(expanded.get(i));
			if(b != null)
				for(String s : b.getChildren().keySet())
					if(!expanded.contains(s))
						expanded.add(s);
		}
		return expanded::contains;
	}
	
	Set<String> getBoneNames();
	
	Collection<? extends IBone> getBones();
	
	@Nullable
	IBone getBone(String bone);
	
	boolean hasBone(String bone);
	
	/**
	 * Resets the pose of the geometric model to its default state.
	 */
	void resetPose();
	
	/**
	 * Gets an empty pose with no bones configured.
	 * This is used for applying animation system to the model.
	 * The returned object may or may not be a new instance, but is guaranteed to have no configurations.
	 */
	GeometryPose emptyPose();
	
	/**
	 * Applies the specified pose to the geometric model's default state.
	 *
	 * @param pose
	 * 		The pose to apply.
	 */
	void applyPose(GeometryPose pose);
	
	/**
	 * Applies a given animation system to this model. This uses {@link #emptyPose()}, then applies animation system to that and copies the pose over to bones.
	 */
	default void applySystem(float partialTime, AnimationSystem system)
	{
		GeometryPose pose = emptyPose();
		system.applyAnimation(partialTime, pose);
		applyPose(pose);
	}
}