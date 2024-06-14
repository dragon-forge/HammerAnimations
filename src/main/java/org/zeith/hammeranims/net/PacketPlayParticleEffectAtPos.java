package org.zeith.hammeranims.net;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3d;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.core.client.particle.ParticleWithEmitter;
import org.zeith.hammerlib.api.lighting.ColoredLightManager;
import org.zeith.hammerlib.net.*;

@MainThreaded
public class PacketPlayParticleEffectAtPos
		implements IPacket
{
	Vector3d source;
	ResourceLocation container;
	
	public PacketPlayParticleEffectAtPos(Vector3d object, IParticleContainer particle)
	{
		this.source = new Vector3d(object);
		this.container = particle.getRegistryKey();
	}
	
	public PacketPlayParticleEffectAtPos(Vector3d object, ResourceLocation particle)
	{
		this.source = new Vector3d(object);
		this.container = particle;
	}
	
	public PacketPlayParticleEffectAtPos()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeDouble(source.x).writeDouble(source.y).writeDouble(source.z);
		buf.writeResourceLocation(container);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		this.source = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
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
		if(source == null) return;
		new ParticleWithEmitter(
				cl,
				source.x, source.y, source.z,
				container
		).spawn();
	}
}