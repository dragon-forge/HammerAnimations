package org.zeith.hammeranims.net;

import com.zeitheron.hammercore.net.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.core.client.particle.ParticleWithEmitter;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;

import java.io.IOException;

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
		IObjectSource.writeSource(source, buf);
		buf.writeResourceLocation(container.getRegistryKey());
	}
	
	@Override
	public void read(PacketBuffer buf)
			throws IOException
	{
		this.source = IObjectSource.readSource(buf).orElse(null);
		this.container = HammerAnimationsApi.particleContainers().getValue(buf.readResourceLocation());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		if(source == null) return;
		if(container == null) return;
		EntityPlayer player = net.getPlayer();
		if(player == null) return;
		IAnimatedObject obj = source.get(IAnimatedObject.class, player.world).orElse(null);
		if(obj == null) return;
		ParticleWithEmitter pwe = new ParticleWithEmitter(
				obj,
				container
		);
		pwe.spawn();
	}
}