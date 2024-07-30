package org.zeith.hammeranims.core.client.particle;

import com.zeitheron.hammercore.client.particle.api.SimpleParticle;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.World;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;

@Getter
public class ParticleWithEmitter
		extends SimpleParticle
{
	public static int MAX_EMITTER_GENERATIONS = 6;
	
	protected final ParticleEmitter emitter;
	
	public ParticleWithEmitter(World worldIn, double posXIn, double posYIn, double posZIn, IParticleContainer container)
	{
		super(worldIn, posXIn, posYIn, posZIn);
		this.emitter = createEmitter(container);
		
		emitter.lastGlobal.set(posX, posY, posZ);
		emitter.prevGlobal.set(emitter.lastGlobal);
	}
	
	public ParticleWithEmitter(IAnimatedObject object, IParticleContainer container)
	{
		super(object.getAnimatedObjectWorld(),
				object.getAnimatedObjectPosition().x,
				object.getAnimatedObjectPosition().y,
				object.getAnimatedObjectPosition().z
		);
		this.emitter = createEmitter(container);
		emitter.setTarget(object);
		
		emitter.lastGlobal.set(posX, posY, posZ);
		emitter.prevGlobal.set(emitter.lastGlobal);
	}
	
	public ParticleEmitter createEmitter(IParticleContainer container)
	{
		ParticleEmitter e = new ParticleEmitter();
		e.setEffect(container.getParticleEffect());
		e.setWorld(world);
		return e;
	}
	
	@Override
	public void onUpdate()
	{
		if(emitter.generation >= MAX_EMITTER_GENERATIONS)
		{
			isExpired = true;
			return;
		}
		
		emitter.prevGlobal.set(emitter.lastGlobal);
		emitter.lastGlobal.set(posX, posY, posZ);
		
		emitter.update();
		if(emitter.lifetime >= 0 && emitter.age >= emitter.lifetime)
		{
			emitter.running = false;
			emitter.stop();
		}
		if(emitter.isFinished())
			isExpired = true;
		if(emitter.target != null)
		{
//			Vec3d p = emitter.target.getAnimatedObjectPosition();
//			setPosition(p.x, p.y, p.z);
			if(emitter.target.getAnimationSource().get(world) != emitter.target)
			{
				isExpired = true;
			}
		}
	}
	
	@Override
	public void spawn()
	{
		if(emitter.generation >= MAX_EMITTER_GENERATIONS) return;
		super.spawn();
	}
	
	@Override
	public void doRenderParticle(double x, double y, double z, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		if(emitter.isFinished()) return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x - posX, y - posY, z - posZ);
		emitter.render(partialTicks);
		GlStateManager.popMatrix();
	}
}