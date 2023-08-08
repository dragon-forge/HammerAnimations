package org.zeith.hammeranims.core.contents.sources;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

public class EntityAnimationSourceType
		extends AnimationSourceType
{
	@Override
	public EntitySourceType readSource(NBTTagCompound tag)
	{
		return new EntitySourceType(tag);
	}
	
	public AnimationSource of(Entity ent)
	{
		return new EntitySourceType(ent.getEntityId());
	}
	
	public static class EntitySourceType
			extends AnimationSource
	{
		public final int pos;
		
		public EntitySourceType(int pos)
		{
			this.pos = pos;
		}
		
		public EntitySourceType(NBTTagCompound tag)
		{
			this.pos = tag.getInteger("id");
		}
		
		@Override
		public NBTTagCompound writeSource()
		{
			NBTTagCompound tag = InstanceHelpers.newNBTCompound();
			tag.setInteger("id", pos);
			return tag;
		}
		
		@Override
		public AnimationSourceType getType()
		{
			return DefaultsHA.ENTITY_TYPE;
		}
		
		@Override
		public IAnimatedObject get(World world)
		{
			return Cast.cast(world.getEntityByID(pos), IAnimatedObject.class);
		}
	}
}