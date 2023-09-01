package org.zeith.hammeranims.net;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;
import org.zeith.hammerlib.net.*;

@MainThreaded
public class PacketSyncAnimationSystem
		implements INBTPacket
{
	protected CompoundTag tag;
	protected IObjectSource<?> source;
	
	public PacketSyncAnimationSystem()
	{
	}
	
	public PacketSyncAnimationSystem(AnimationSystem system)
	{
		this.tag = system.serializeNBT();
		this.source = system.owner.getAnimationSource();
	}
	
	@Override
	public void write(CompoundTag nbt)
	{
		nbt.put("Sys", tag);
		nbt.put("Src", IObjectSource.writeSource(source));
	}
	
	@Override
	public void read(CompoundTag nbt)
	{
		tag = nbt.getCompound("Sys");
		source = IObjectSource.readSource(nbt.getCompound("Src")).orElse(null);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		var world = HammerAnimations.PROXY.getClientWorld();
		if(world != null && source != null)
		{
			var object = source.get(IAnimatedObject.class, world).orElse(null);
			if(object != null) object.getAnimationSystem().deserializeNBT(tag);
		}
	}
}