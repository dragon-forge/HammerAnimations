package org.zeith.hammeranims.api.geometry.model;

import net.minecraftforge.api.distmarker.*;
import org.zeith.hammeranims.api.animsys.AnimationSystem;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Interface for defining a geometric model with pose manipulation and rendering capabilities.
 */
public interface IGeometricModel
		extends IGenericModel
{
	IGeometricModel EMPTY = new IGeometricModel()
	{
		private final GeometryPose pose = new GeometryPose(s -> false);
		
		@Override
		public Set<String> getBoneNames()
		{
			return Collections.emptySet();
		}
		
		@Override
		public Collection<IBone> getBones()
		{
			return Collections.emptySet();
		}
		
		@Nullable
		@Override
		public IBone getBone(String bone)
		{
			return null;
		}
		
		@Override
		public boolean hasBone(String bone)
		{
			return false;
		}
		
		@Override
		public void resetPose()
		{
		}
		
		@Override
		public GeometryPose emptyPose()
		{
			pose.reset();
			return pose;
		}
		
		@Override
		public void applyPose(GeometryPose pose)
		{
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public void renderModel(RenderData data)
		{
		}
		
		@Override
		public void dispose()
		{
		}
	};
	
	/**
	 * Renders the model using the provided render data.
	 *
	 * @param data
	 * 		The render data to be passed along.
	 */
	@OnlyIn(Dist.CLIENT)
	void renderModel(RenderData data);
	
	/**
	 * Disposes this model and it's used GPU/memory resources.
	 * This will be only called on render thread.
	 */
	void dispose();
}