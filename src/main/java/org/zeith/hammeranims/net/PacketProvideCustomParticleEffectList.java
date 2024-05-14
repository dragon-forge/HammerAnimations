package org.zeith.hammeranims.net;

import com.google.common.collect.ImmutableSet;
import com.zeitheron.hammercore.net.*;
import com.zeitheron.hammercore.net.transport.NetTransport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.contents.commands.CommandParticle;
import org.zeith.hammeranims.core.impl.api.particles.ExtraParticleEffects;

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
	public void executeOnClient2(PacketContext net)
	{
		net.withReply(createToServer());
	}
	
	@Override
	public void executeOnServer2(PacketContext ctx)
	{
		CompletableFuture<Set<ResourceLocation>> f = CommandParticle.getOrUnresolved(ctx.getSender());
		if(f == null) return;
		if(f.isDone())
		{
			CommandParticle.PLAYER_CUSTOM_MAP.put(ctx.getSender().getUniqueID(), CompletableFuture.completedFuture(ImmutableSet.copyOf(all)));
			return;
		}
		f.complete(ImmutableSet.copyOf(all));
	}
	
	public static void toServer()
	{
		HCNet.INSTANCE.sendToServer(createToServer());
	}
	
	public static IPacket createToServer()
	{
		ExtraParticleEffects fx = HammerAnimations.PROXY.getExtraParticles();
		Set<ResourceLocation> allFX = fx != null ? fx.getKeys() : Collections.emptySet();
		PacketProvideCustomParticleEffectList p = new PacketProvideCustomParticleEffectList();
		p.all = allFX;
		return NetTransport.wrap(p).createPacket();
	}
	
	@MainThreaded
	public static class PacketResetList
			implements IPacket
	{
		@Override
		public void executeOnServer2(PacketContext ctx)
		{
			EntityPlayerMP s = ctx.getSender();
			if(s == null) return;
			CompletableFuture<Set<ResourceLocation>> f = CommandParticle.PLAYER_CUSTOM_MAP.remove(s.getUniqueID());
			if(f != null && !f.isDone()) f.complete(ImmutableSet.of());
		}
	}
}