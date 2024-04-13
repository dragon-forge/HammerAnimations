package org.zeith.hammeranims.net;

import com.zeitheron.hammercore.net.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;

import java.io.IOException;

public class PacketRequestAnimationSystemSync
		implements IPacket
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
	public void write(PacketBuffer buf)
	{
		IObjectSource.writeSource(source, buf);
	}
	
	@Override
	public void read(PacketBuffer buf)
			throws IOException
	{
		source = IObjectSource.readSource(buf).orElse(null);
	}
	
	@Override
	public void executeOnServer2(PacketContext net)
	{
		World world = net.getSender().getServerWorld();
		source.get(IAnimatedObject.class, world)
				.ifPresent(object -> net.withReply(new PacketSyncAnimationSystem(object.getAnimationSystem())));
	}
}