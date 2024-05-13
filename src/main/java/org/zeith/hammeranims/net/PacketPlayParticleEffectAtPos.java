package org.zeith.hammeranims.net;

import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.core.client.particle.ParticleWithEmitter;
import org.zeith.hammeranims.joml.Vector3d;
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
	public void write(PacketBuffer buf)
	{
		buf.writeDouble(source.x).writeDouble(source.y).writeDouble(source.z);
		buf.writeResourceLocation(container);
	}
	
	@Override
	public void read(PacketBuffer buf)
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
		if(player == null || !(player.level instanceof ClientWorld) || container == null) return;
		if(source == null) return;
		ParticleWithEmitter pwe = new ParticleWithEmitter(
				(ClientWorld) player.level,
				source.x, source.y, source.z,
				container
		);
		Minecraft.getInstance().particleEngine.add(pwe);
	}
}