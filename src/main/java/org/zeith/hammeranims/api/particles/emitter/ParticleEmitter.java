package org.zeith.hammeranims.api.particles.emitter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.animsys.IAnimatedObject;
import org.zeith.hammeranims.api.particles.ParticleEffect;
import org.zeith.hammeranims.api.particles.components.itf.*;
import org.zeith.hammeranims.api.particles.curve.ParticleCurve;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;
import org.zeith.hammeranims.core.impl.api.particles.components.appearance.ParcomCollisionAppearance;
import org.zeith.hammeranims.core.init.ParticleComponentsHA;
import org.zeith.hammeranims.joml.*;

import java.lang.Math;
import java.util.*;

public class ParticleEmitter
{
	public ParticleEffect effect;
	public List<BedrockParticle> particles = new ArrayList<>();
	public List<BedrockParticle> splitParticles = new ArrayList<>();
	
	public final Map<String, InterpolatedDouble.NumberWrapped<ParticleVariables>> variables = new HashMap<>();
	public final Object2DoubleMap<String> initialValues = new Object2DoubleOpenHashMap<>();
	
	public boolean isRenderingGUI = false;
	
	public IAnimatedObject target;
	public World world;
	public boolean lit;
	
	public boolean added;
	public int sanityTicks;
	public boolean running = true;
	private BedrockParticle guiParticle;
	
	/* Intermediate values */
	public Vector3d lastGlobal = new Vector3d();
	public Vector3d prevGlobal = new Vector3d();
	public Matrix3f rotation = new Matrix3f(1, 0, 0, 0, 1, 0, 0, 0, 1);
	public Matrix3f prevRotation = new Matrix3f(1, 0, 0, 0, 1, 0, 0, 0, 1);
	public Vector3f angularVelocity = new Vector3f();
	/**
	 * Translation of immediate bodypart
	 */
	public Vector3d translation = new Vector3d();
	
	/* Runtime properties */
	public int age;
	public int lifetime;
	public double spawnedParticles;
	public boolean playing = true;
	
	public float random1 = (float) Math.random();
	public float random2 = (float) Math.random();
	public float random3 = (float) Math.random();
	public float random4 = (float) Math.random();
	
	private Mutable blockPos = new Mutable();
	
	public double[] scale = { 1, 1, 1 };
	
	/* Camera properties */
	public PointOfView perspective;
	public float cYaw;
	public float cPitch;
	
	public double cX;
	public double cY;
	public double cZ;
	
	public final ParticleVariables vars = new ParticleVariables();
	
	public boolean isFinished()
	{
		return !this.running && this.particles.isEmpty();
	}
	
	public double getAge()
	{
		return this.getAge(0);
	}
	
	public double getAge(float partialTicks)
	{
		return (this.age + partialTicks) / 20.0;
	}
	
	public void setTarget(IAnimatedObject target)
	{
		this.target = target;
		this.world = target == null ? null : target.getAnimatedObjectWorld();
	}
	
	public void setWorld(World world)
	{
		this.world = world;
	}
	
	public void setEffect(ParticleEffect scheme)
	{
		this.setEffect(scheme, null);
	}
	
	public void setEffect(ParticleEffect effect, Map<String, String> variables)
	{
		this.effect = effect;
		
		if(this.effect == null)
		{
			return;
		}
		
		for(ParticleCurve curve : effect.curves)
		{
			registerVariable(curve.variable, curve);
		}
		
		if(variables != null)
		{
			this.parseVariables(variables);
		}
		
		this.lit = true;
		this.stop();
		this.start();
		
		this.setEmitterVariables(0);
	}
	
	public void setParticleVariables(BedrockParticle particle, float partialTicks)
	{
		vars.particle_age = particle.getAge(partialTicks);
		vars.particle_lifetime = particle.lifetime / 20.0;
		vars.particle_random_1 = particle.random1;
		vars.particle_random_2 = particle.random2;
		vars.particle_random_3 = particle.random3;
		vars.particle_random_4 = particle.random4;
		
		Vector3d relativePos = new Vector3d(particle.getGlobalPosition(this));
		relativePos.sub(this.lastGlobal);
		
		vars.particle_pos.set(relativePos);
		vars.particle_speed.set(particle.speed);
		vars.particle_bounces = particle.bounces;
		
		for(Map.Entry<String, InterpolatedDouble.NumberWrapped<ParticleVariables>> e : variables.entrySet())
		{
			vars.putUpdate(e.getKey(), e.getValue());
		}
	}
	
	public void setEmitterVariables(float partialTicks)
	{
		vars.emitter_age = this.getAge(partialTicks);
		vars.emitter_lifetime = this.lifetime / 20.0;
		vars.emitter_random_1 = this.random1;
		vars.emitter_random_2 = this.random2;
		vars.emitter_random_3 = this.random3;
		vars.emitter_random_4 = this.random4;
		
		for(Map.Entry<String, InterpolatedDouble.NumberWrapped<ParticleVariables>> e : variables.entrySet())
		{
			vars.putUpdate(e.getKey(), e.getValue());
		}
	}
	
	public void parseVariables(Map<String, String> variables)
	{
		for(Map.Entry<String, String> entry : variables.entrySet())
		{
			String name = entry.getKey(), expression = entry.getValue();
			registerVariable(name, InterpolatedDouble.parse(expression));
		}
	}
	
	public void registerVariable(String name, InterpolatedDouble<ParticleVariables> expression)
	{
		if(!name.startsWith("variable."))
		{
			HammerAnimations.LOG.warn("Tried to registerVariable, the name '{}' does not start with 'variable.'", name);
			return;
		}
		
		this.variables.put(name, new InterpolatedDouble.NumberWrapped<>(expression));
	}
	
	public void replaceVariables()
	{
	}
	
	public void start()
	{
		if(this.playing)
		{
			return;
		}
		
		this.age = 0;
		this.spawnedParticles = 0;
		this.playing = true;
		
		for(IEmitterInitialize component : this.effect.emitterInitializes)
			component.apply(this);
	}
	
	public void stop()
	{
		if(!this.playing)
			return;
		
		this.spawnedParticles = 0;
		this.playing = false;
		
		this.random1 = (float) Math.random();
		this.random2 = (float) Math.random();
		this.random3 = (float) Math.random();
		this.random4 = (float) Math.random();
	}
	
	/**
	 * Update this current emitter
	 */
	public void update()
	{
		if(this.effect == null)
		{
			return;
		}
		
		this.setEmitterVariables(0);
		
		for(IEmitterUpdate component : this.effect.emitterUpdates)
			component.update(this);
		
		this.setEmitterVariables(0);
		this.updateParticles();
		this.brightnessCache.clear(); // Reset lighting cache
		
		this.age += 1;
		this.sanityTicks += 1;
		if(target != null) this.vars.entity_scale = target.getAnimatedObjectScale();
	}
	
	/**
	 * Update all particles
	 */
	private void updateParticles()
	{
		Iterator<BedrockParticle> it = this.particles.iterator();
		
		while(it.hasNext())
		{
			BedrockParticle particle = it.next();
			
			this.updateParticle(particle);
			
			if(particle.dead)
			{
				it.remove();
			}
		}
		
		if(!this.splitParticles.isEmpty())
		{
			this.particles.addAll(this.splitParticles);
			this.splitParticles.clear();
		}
	}
	
	/**
	 * Update a single particle
	 */
	private void updateParticle(BedrockParticle particle)
	{
		particle.update(this);
		
		this.setParticleVariables(particle, 0);
		
		for(IParticleUpdate component : this.effect.particleUpdates)
			component.update(this, particle);
	}
	
	/**
	 * Spawn a particle
	 */
	public void spawnParticle()
	{
		if(!this.running)
		{
			return;
		}
		
		this.particles.add(this.createParticle(false));
	}
	
	/**
	 * Create a new particle
	 */
	public BedrockParticle createParticle(boolean forceRelative)
	{
		BedrockParticle particle = new BedrockParticle(this);
		
		this.setParticleVariables(particle, 0);
		particle.setupMatrix(this);
		
		for(IParticleInitialize component : this.effect.particleInitializes)
			component.apply(this, particle);
		
		if(particle.relativePosition && !particle.relativeRotation)
		{
			Vector3d vec = new Vector3d(particle.position);
			
			particle.matrix.transform(vec);
			
			particle.position.x = vec.x;
			particle.position.y = vec.y;
			particle.position.z = vec.z;
		}
		
		if(!(particle.relativePosition && particle.relativeRotation))
		{
			particle.position.add(this.lastGlobal);
			particle.initialPosition.add(this.lastGlobal);
		}
		
		particle.prevPosition.set(particle.position);
		particle.rotation = particle.initialRotation;
		particle.prevRotation = particle.rotation;
		
		return particle;
	}
	
	/**
	 * Render the particle on screen
	 */
	public void renderOnScreen(IRenderTypeBuffer buffers, MatrixStack pose, int x, int y, float scale)
	{
		if(this.effect == null) return;
		
		float partialTicks = Minecraft.getInstance().getDeltaFrameTime();
		
		List<IParticleRender> listParticle = this.effect.particleRender;
		
		Matrix3f rotation = this.rotation;
		
		this.rotation = new Matrix3f();
		
		if(!listParticle.isEmpty())
		{
			var buf = buffers.getBuffer(effect.material.renderType.apply(effect.texture));
			
			this.effect.material.beginGL();
			RenderSystem.disableCull();
			
			if(this.guiParticle == null || this.guiParticle.dead)
			{
				this.guiParticle = this.createParticle(true);
			}
			
			this.rotation.identity();
			this.guiParticle.update(this);
			this.setEmitterVariables(partialTicks);
			this.setParticleVariables(this.guiParticle, partialTicks);
			
			for(IParticleRender render : listParticle)
			{
				render.renderOnScreen(vars, this.guiParticle, buf, pose, x, y, scale, partialTicks);
			}
			
			this.effect.material.endGL();
			RenderSystem.enableCull();
		}
		
		this.rotation = rotation;
	}
	
	/**
	 * Render all the particles in this particle emitter
	 */
	public void render(ActiveRenderInfo info, IRenderTypeBuffer buffers, MatrixStack pose, float partialTicks)
	{
		if(this.effect == null)
		{
			return;
		}
		
		this.setupCameraProperties(info, partialTicks);
		
		List<IParticleRender> renders = this.effect.particleRender;
		
		this.setupOpenGL(partialTicks, pose);
		
		for(IParticlePreRender component : this.effect.particlePreRender)
			component.preRender(this, partialTicks);
		
		if(!this.particles.isEmpty())
		{
			this.depthSorting();
			
			var renderer = buffers.getBuffer(effect.material.renderType.apply(effect.texture));
			this.renderParticles(renderer, pose, renders, false, partialTicks);
			
			ParcomCollisionAppearance collisionAppearance = this.effect.get(ParcomCollisionAppearance.class, ParticleComponentsHA.PARTICLE_COLLISION_APPEARANCE);
			
			/* rendering the collided particles with an extra component */
			if(collisionAppearance != null && collisionAppearance.texture != null)
			{
				renderer = buffers.getBuffer(effect.material.renderType.apply(collisionAppearance.texture));
				this.renderParticles(renderer, pose, renders, true, partialTicks);
			}
		}
		
		for(IParticlePostRender component : this.effect.particlePostRender)
			component.postRender(this, partialTicks);
		
		this.endOpenGL(pose);
	}
	
	/**
	 * This method renders the particles using the default bedrock billboards
	 *
	 * @param pose
	 * @param renderComponents
	 * @param collided
	 * @param partialTicks
	 */
	private void renderParticles(IVertexBuilder builder, MatrixStack pose, List<IParticleRender> renderComponents, boolean collided, float partialTicks)
	{
		for(BedrockParticle particle : this.particles)
		{
			boolean collisionStuff = particle.isCollisionTexture(this) || particle.isCollisionTinting(this);
			
			if(collisionStuff != collided)
			{
				continue;
			}
			
			this.setEmitterVariables(partialTicks);
			this.setParticleVariables(particle, partialTicks);
			
			for(IParticleRender component : renderComponents)
			{
				/* if collisionTexture or collisionTinting is true - means that those options are enabled
				 * therefore the old Billboardappearance should not be called
				 * because collisionAppearance.class is rendering
				 */
				if(!(collisionStuff && component.supportsCollissionRendering()))
					component.render(vars, this, particle, builder, pose, partialTicks);
			}
		}
	}
	
	private void setupOpenGL(float partialTicks, MatrixStack pose)
	{
		this.effect.material.beginGL();
		
		if(!isRenderingGUI)
		{
			Entity camera = Minecraft.getInstance().getCameraEntity();
			
			var playerPos = camera.getPosition(partialTicks);
			
			double playerX = playerPos.x;
			double playerY = playerPos.y;
			double playerZ = playerPos.z;
			
			pose.pushPose();
			pose.translate(-playerX, -playerY, -playerZ);
			
			RenderSystem.disableCull();
			RenderSystem.enableTexture();
		}
	}
	
	private void endOpenGL(MatrixStack pose)
	{
		if(!isRenderingGUI)
		{
			pose.popPose();
		}
		
		this.effect.material.endGL();
	}
	
	
	private void depthSorting()
	{
		this.particles.sort((a, b) ->
		{
			double ad = a.getDistanceSq(this);
			double bd = b.getDistanceSq(this);
			
			if(ad < bd)
			{
				return 1;
			} else if(ad > bd)
			{
				return -1;
			}
			
			return 0;
		});
	}
	
	public void setupCameraProperties(ActiveRenderInfo info, float partialTicks)
	{
		if(this.world == null) return;
		
		Entity camera = Minecraft.getInstance().getCameraEntity();
		if(camera == null) return;
		
		this.perspective = Minecraft.getInstance().options.getCameraType();
		this.cYaw = 180 - info.getYRot();
		this.cPitch = 180 - info.getXRot();
		
		var cpos = info.getPosition();
		this.cX = cpos.x;
		this.cY = cpos.y + camera.getEyeHeight();
		this.cZ = cpos.z;
	}
	
	Long2IntMap brightnessCache = new Long2IntOpenHashMap();
	
	/**
	 * Get brightness for the block
	 */
	public int getBrightnessForRender(float partialTicks, double x, double y, double z)
	{
		if(this.lit || this.world == null)
			return 0xf000f0; // full-brightness
		this.blockPos.set(x, y, z);
		return getBrightnessCached(this.blockPos);
	}
	
	private int getBrightnessCached(BlockPos pos)
	{
		return brightnessCache.computeIfAbsent(pos.asLong(), l ->
		{
			BlockPos ipos = pos.immutable();
			int max = WorldRenderer.getLightColor(world, ipos);
			for(Direction dir : Direction.values())
			{
				int cur = WorldRenderer.getLightColor(world, ipos.relative(dir));
				max = LightTexture.pack(
						Math.max(LightTexture.block(max), LightTexture.block(cur)),
						Math.max(LightTexture.sky(max), LightTexture.sky(cur))
				);
			}
			return max;
		});
	}
}