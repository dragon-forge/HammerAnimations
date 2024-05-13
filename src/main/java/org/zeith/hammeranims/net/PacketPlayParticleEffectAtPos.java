package org.zeith.hammeranims.net;

import com.zeitheron.hammercore.net.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.core.client.particle.ParticleWithEmitter;
import org.zeith.hammeranims.joml.Vector3d;

import java.io.IOException;

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
			throws IOException
	{
		this.source = new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
		this.container = buf.readResourceLocation();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		if(source == null) return;
		if(container == null) return;
		IParticleContainer container = IParticleContainer.byRegistryKey(this.container);
		EntityPlayer player = net.getPlayer();
		if(player == null || container == null) return;
		if(source == null) return;
		ParticleWithEmitter pwe = new ParticleWithEmitter(
				player.world,
				source.x, source.y, source.z,
				container
		);
		pwe.spawn();
	}
}