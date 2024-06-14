package org.zeith.hammeranims.net;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.core.client.particle.ParticleWithEmitter;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;
import org.zeith.hammerlib.api.lighting.ColoredLightManager;
import org.zeith.hammerlib.net.*;

@MainThreaded
public class PacketPlayParticleEffectAtObject
		implements IPacket
{
	IObjectSource<?> source;
	ResourceLocation container;
	
	public PacketPlayParticleEffectAtObject(IAnimatedObject object, IParticleContainer particle)
	{
		this.source = object.getAnimationSource();
		this.container = particle.getRegistryKey();
	}
	
	public PacketPlayParticleEffectAtObject(IAnimatedObject object, ResourceLocation particle)
	{
		this.source = object.getAnimationSource();
		this.container = particle;
	}
	
	public PacketPlayParticleEffectAtObject()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeNbt(IObjectSource.writeSource(source));
		buf.writeResourceLocation(container);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		this.source = IObjectSource.readSource(buf.readNbt()).orElse(null);
		this.container = buf.readResourceLocation();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		if(source == null) return;
		if(container == null) return;
		IParticleContainer container = IParticleContainer.byRegistryKey(this.container);
		var player = ColoredLightManager.getClientPlayer();
		if(player == null || !(player.level() instanceof ClientLevel cl) || container == null) return;
		IAnimatedObject obj = source.get(IAnimatedObject.class, cl).orElse(null);
		if(obj == null) return;
		ParticleWithEmitter pwe = new ParticleWithEmitter(
				cl,
				obj,
				container
		);
		Minecraft.getInstance().particleEngine.add(pwe);
	}
}