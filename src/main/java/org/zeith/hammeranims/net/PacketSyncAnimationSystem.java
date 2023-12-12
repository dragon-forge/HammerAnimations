package org.zeith.hammeranims.net;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.*;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.core.client.ClientHammerHooks;
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
		ClientHammerHooks.applySystem(source, tag, 100);
	}
}