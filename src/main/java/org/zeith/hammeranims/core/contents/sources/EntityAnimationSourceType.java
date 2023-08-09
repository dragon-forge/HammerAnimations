package org.zeith.hammeranims.core.contents.sources;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammerlib.util.java.Cast;

public class EntityAnimationSourceType
		extends AnimationSourceType
{
	@Override
	public EntitySourceType readSource(CompoundTag tag)
	{
		return new EntitySourceType(tag);
	}
	
	public AnimationSource of(Entity ent)
	{
		return new EntitySourceType(ent.getId());
	}
	
	public static class EntitySourceType
			extends AnimationSource
	{
		public final int pos;
		
		public EntitySourceType(int pos)
		{
			this.pos = pos;
		}
		
		public EntitySourceType(CompoundTag tag)
		{
			this.pos = tag.getInt("id");
		}
		
		@Override
		public CompoundTag writeSource()
		{
			var tag = InstanceHelpers.newNBTCompound();
			tag.putInt("id", pos);
			return tag;
		}
		
		@Override
		public AnimationSourceType getType()
		{
			return DefaultsHA.ENTITY_TYPE;
		}
		
		@Override
		public IAnimatedObject get(Level world)
		{
			return Cast.cast(world.getEntity(pos), IAnimatedObject.class);
		}
	}
}