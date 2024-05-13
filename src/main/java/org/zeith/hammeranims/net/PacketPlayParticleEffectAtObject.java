package org.zeith.hammeranims.net;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammeranims.api.HammerAnimationsApi;
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
	IParticleContainer container;
	
	public PacketPlayParticleEffectAtObject(IAnimatedObject object, IParticleContainer particle)
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
		buf.writeResourceLocation(container.getRegistryKey());
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		this.source = IObjectSource.readSource(buf.readNbt()).orElse(null);
		this.container = HammerAnimationsApi.particleContainers().getValue(buf.readResourceLocation());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientExecute(PacketContext ctx)
	{
		if(source == null) return;
		if(container == null) return;
		var player = ColoredLightManager.getClientPlayer();
		if(player == null || !(player.level instanceof ClientLevel cl)) return;
		IAnimatedObject obj = source.get(IAnimatedObject.class, player.level).orElse(null);
		if(obj == null) return;
		ParticleWithEmitter pwe = new ParticleWithEmitter(
				cl,
				obj,
				container
		);
		Minecraft.getInstance().particleEngine.add(pwe);
	}
}