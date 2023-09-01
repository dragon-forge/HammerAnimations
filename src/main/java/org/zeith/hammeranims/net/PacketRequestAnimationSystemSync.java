package org.zeith.hammeranims.net;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;
import org.zeith.hammerlib.net.*;

public class PacketRequestAnimationSystemSync
		implements INBTPacket
{
	protected IObjectSource<?> source;
	
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
		nbt.merge(IObjectSource.writeSource(source));
	}
	
	@Override
	public void read(CompoundTag nbt)
	{
		this.source = IObjectSource.readSource(nbt).orElse(null);
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		var world = ctx.getSender().level();
		var object = source != null ? source.get(IAnimatedObject.class, world).orElse(null) : null;
		if(object != null)
			ctx.withReply(new PacketSyncAnimationSystem(object.getAnimationSystem()));
	}
}