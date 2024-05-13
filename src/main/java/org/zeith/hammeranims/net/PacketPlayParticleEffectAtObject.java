package org.zeith.hammeranims.net;

import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
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
	public void write(PacketBuffer buf)
	{
		buf.writeNbt(IObjectSource.writeSource(source));
		buf.writeResourceLocation(container.getRegistryKey());
	}
	
	@Override
	public void read(PacketBuffer buf)
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
		if(player == null || !(player.level instanceof ClientWorld)) return;
		IAnimatedObject obj = source.get(IAnimatedObject.class, player.level).orElse(null);
		if(obj == null) return;
		ParticleWithEmitter pwe = new ParticleWithEmitter(
				(ClientWorld) player.level,
				obj,
				container
		);
		Minecraft.getInstance().particleEngine.add(pwe);
	}
}