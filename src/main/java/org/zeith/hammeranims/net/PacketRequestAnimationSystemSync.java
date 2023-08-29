package org.zeith.hammeranims.net;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammerlib.net.*;

public class PacketRequestAnimationSystemSync
		implements INBTPacket
{
	protected AnimationSource source;
	
	public PacketRequestAnimationSystemSync()
	{
	}
	
	public PacketRequestAnimationSystemSync(AnimationSystem system)
	{
		this.source = system.owner.getAnimationSource();
	}
	
	@Override
	public void write(CompoundTag nbt)
	{
		nbt.put("Src", source.writeSource());
		nbt.putString("Type", source.getType().getRegistryKey().toString());
	}
	
	@Override
	public void read(CompoundTag nbt)
	{
		AnimationSourceType type = HammerAnimationsApi.animationSources()
				.getValue(new ResourceLocation(nbt.getString("Type")));
		if(type != null)
			source = type.readSource(nbt.getCompound("Src"));
		else
			HammerAnimations.LOG.warn("Unable to find animation source {}", nbt.getString("Type"));
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		Level world = ctx.getSender().level();
		IAnimatedObject object = source.get(world);
		if(object != null)
			ctx.withReply(new PacketSyncAnimationSystem(object.getAnimationSystem()));
	}
}