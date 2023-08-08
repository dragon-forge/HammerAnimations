package org.zeith.hammeranims.api.geometry.model;

import net.minecraftforge.fml.relauncher.*;
import org.zeith.hammeranims.api.animsys.AnimationSystem;

/**
 * Interface for defining a geometric model with pose manipulation and rendering capabilities.
 */
public interface IGeometricModel
{
	IGeometricModel EMPTY = new IGeometricModel()
	{
		@Override
		public void resetPose()
		{
		}
		
		@Override
		public GeometryPose emptyPose()
		{
			return new GeometryPose(s -> false);
		}
		
		@Override
		public void applyPose(GeometryPose pose)
		{
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public void renderModel(RenderData data)
		{
		}
		
		@Override
		public void dispose()
		{
		}
	};
	
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
	 * Renders the model using the provided render data.
	 *
	 * @param data
	 * 		The render data to be passed along.
	 */
	@SideOnly(Side.CLIENT)
	void renderModel(RenderData data);
	
	/**
	 * Applies a given animation system to this model. This uses {@link #emptyPose()}, then applies animation system to that and copies the pose over to bones.
	 */
	default void applySystem(float partialTime, AnimationSystem system)
	{
		GeometryPose pose = emptyPose();
		system.applyAnimation(partialTime, pose);
		applyPose(pose);
	}
	
	/**
	 * Disposes this model and it's used GPU/memory resources.
	 * This will be only called on render thread.
	 */
	void dispose();
}