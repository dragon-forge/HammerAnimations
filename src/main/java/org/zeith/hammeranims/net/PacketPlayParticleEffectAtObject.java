package org.zeith.hammeranims.net;

import com.zeitheron.hammercore.net.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.core.client.particle.ParticleWithEmitter;
import org.zeith.hammeranims.joml.Matrix3f;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;

import java.io.IOException;

@MainThreaded
public class PacketPlayParticleEffectAtObject
		implements IPacket
{
	IObjectSource<?> source;
	ResourceLocation container;
	Matrix3f rotation;
	
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
	
	public PacketPlayParticleEffectAtObject(IAnimatedObject object, IParticleContainer particle, Matrix3f rotation)
	{
		this(object, particle);
		this.rotation = new Matrix3f(rotation);
	}
	
	public PacketPlayParticleEffectAtObject(IAnimatedObject object, ResourceLocation particle, Matrix3f rotation)
	{
		this(object, particle);
		this.rotation = new Matrix3f(rotation);
	}
	
	public PacketPlayParticleEffectAtObject()
	{
	}
	
	@Override
	public void write(PacketBuffer buf)
	{
		IObjectSource.writeSource(source, buf);
		buf.writeResourceLocation(container);
		buf.writeBoolean(rotation != null);
		if(rotation != null)
		{
			buf.writeFloat(rotation.m00());
			buf.writeFloat(rotation.m01());
			buf.writeFloat(rotation.m02());
			buf.writeFloat(rotation.m10());
			buf.writeFloat(rotation.m11());
			buf.writeFloat(rotation.m12());
			buf.writeFloat(rotation.m20());
			buf.writeFloat(rotation.m21());
			buf.writeFloat(rotation.m22());
		}
	}
	
	@Override
	public void read(PacketBuffer buf)
			throws IOException
	{
		this.source = IObjectSource.readSource(buf).orElse(null);
		this.container = buf.readResourceLocation();
		if(buf.readBoolean())
		{
			this.rotation = new Matrix3f(
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat()
			);
		}
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
		IAnimatedObject obj = source.get(IAnimatedObject.class, player.world).orElse(null);
		if(obj == null) return;
		ParticleWithEmitter pwe = new ParticleWithEmitter(
				obj,
				container
		);
		if(rotation != null)
			pwe.getEmitter().rotation = rotation;
		pwe.spawn();
	}
}