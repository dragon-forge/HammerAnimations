package org.zeith.hammeranims.net;

import com.google.common.collect.ImmutableSet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.contents.commands.CommandParticle;
import org.zeith.hammeranims.core.impl.api.particles.ExtraParticleEffects;
import org.zeith.hammerlib.net.*;
import org.zeith.hammerlib.net.lft.NetTransport;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@MainThreaded
public class PacketProvideCustomParticleEffectList
		implements IPacket
{
	Set<ResourceLocation> all = Collections.emptySet();
	
	@Override
	public void write(PacketBuffer buf)
	{
		buf.writeVarInt(all.size());
		for(ResourceLocation loc : all) buf.writeResourceLocation(loc);
	}
	
	@Override
	public void read(PacketBuffer buf)
	{
		int t = buf.readVarInt();
		Set<ResourceLocation> set = new HashSet<>();
		for(int i = 0; i < t; i++) set.add(buf.readResourceLocation());
		this.all = set;
	}
	
	@Override
	public void clientExecute(PacketContext ctx)
	{
		ctx.withReply(createToServer());
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		CompletableFuture<Set<ResourceLocation>> f = CommandParticle.getOrUnresolved(ctx.getSender());
		if(f == null) return;
		if(f.isDone())
		{
			CommandParticle.PLAYER_CUSTOM_MAP.put(ctx.getSender().getUUID(), CompletableFuture.completedFuture(ImmutableSet.copyOf(all)));
			return;
		}
		f.complete(ImmutableSet.copyOf(all));
	}
	
	public static void toServer()
	{
		Network.sendToServer(createToServer());
	}
	
	public static IPacket createToServer()
	{
		ExtraParticleEffects fx = HammerAnimations.PROXY.getExtraParticles();
		Set<ResourceLocation> allFX = fx != null ? fx.getKeys() : Collections.emptySet();
		PacketProvideCustomParticleEffectList p = new PacketProvideCustomParticleEffectList();
		p.all = allFX;
		return NetTransport.wrap(p).createPacket();
	}
}