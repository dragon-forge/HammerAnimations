package org.zeith.hammeranims.api.geometry.model;

import org.joml.Matrix4f;

import javax.annotation.*;
import java.util.*;

public interface IPositionalModel
		extends IGenericModel
{
	IPositionalModel EMPTY = new IPositionalModel()
	{
		final GeometryPose emptyPose = new GeometryPose(this::hasBone);
		
		@Override
		public void resetPose()
		{
		}
		
		@Override
		public GeometryPose emptyPose()
		{
			emptyPose.reset();
			return emptyPose;
		}
		
		@Override
		public void applyPose(GeometryPose pose)
		{
		}
		
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
		public boolean applyBoneTransforms(@Nonnull Matrix4f base, String bone)
		{
			return false;
		}
	};
	
	/**
	 * Applies bone transforms to the given matrix.
	 * If there is no bone, no transforms will happen.
	 *
	 * @param base
	 * 		The matrix to be transformed.
	 * @param bone
	 * 		The bone to calculate transforms of.
	 *
	 * @return true when the bone is found.
	 */
	boolean applyBoneTransforms(@Nonnull Matrix4f base, String bone);
}