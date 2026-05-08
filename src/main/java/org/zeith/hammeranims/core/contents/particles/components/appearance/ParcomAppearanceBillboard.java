package org.zeith.hammeranims.core.contents.particles.components.appearance;

import com.google.gson.*;
import com.mojang.blaze3d.vertex.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import lombok.val;
import net.minecraft.client.CameraType;
import net.minecraft.client.renderer.LightTexture;
import org.joml.*;
import org.joml.Math;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.components.itf.IParticleRender;
import org.zeith.hammeranims.api.particles.emitter.*;

public class ParcomAppearanceBillboard
		implements IParticleComponent
{
	/* Options */
	public LzFactory sizeW = InterpolatedDouble.zero();
	public LzFactory sizeH = InterpolatedDouble.zero();
	public CameraFacing facing = CameraFacing.LOOKAT_XYZ;
	public boolean customDirection = false;
	public float directionSpeedThreshhold = 0.01F;
	public LzFactory directionX = InterpolatedDouble.zero();
	public LzFactory directionY = InterpolatedDouble.zero();
	public LzFactory directionZ = InterpolatedDouble.zero();
	public int textureWidth = 128;
	public int textureHeight = 128;
	public LzFactory uvX = InterpolatedDouble.zero();
	public LzFactory uvY = InterpolatedDouble.zero();
	public LzFactory uvW = InterpolatedDouble.zero();
	public LzFactory uvH = InterpolatedDouble.zero();
	
	public boolean flipbook = false;
	public float stepX;
	public float stepY;
	public float fps;
	public LzFactory maxFrame = InterpolatedDouble.zero();
	public boolean stretchFPS = false;
	public boolean loop = false;
	
	public ParcomAppearanceBillboard(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		
		JsonObject element = elem.getAsJsonObject();
		
		if(element.has("size") && element.get("size").isJsonArray())
		{
			JsonArray size = element.getAsJsonArray("size");
			
			if(size.size() >= 2)
			{
				this.sizeW = InterpolatedDouble.parse(size.get(0));
				this.sizeH = InterpolatedDouble.parse(size.get(1));
			}
		}
		
		if(element.has("facing_camera_mode"))
		{
			this.facing = CameraFacing.fromString(element.get("facing_camera_mode").getAsString());
		}
		
		if(this.facing.isDirection && element.has("direction"))
		{
			this.parseDirection(element.get("direction").getAsJsonObject());
		}
		
		if(element.has("uv") && element.get("uv").isJsonObject())
		{
			this.parseUv(element.get("uv").getAsJsonObject());
		}
	}
	
	@Override
	public ParcomAppearanceBillboardInstance createInstance(LzVariableStore vars)
	{
		return new ParcomAppearanceBillboardInstance(
				this,
				vars
		);
	}
	
	protected void parseDirection(JsonObject object)
	{
		this.customDirection = object.has("mode") && object.get("mode").getAsString().equals("custom");
		
		if(this.customDirection && object.has("custom_direction"))
		{
			JsonArray directionArray = object.getAsJsonArray("custom_direction");
			this.directionX = InterpolatedDouble.parse(directionArray.get(0));
			this.directionY = InterpolatedDouble.parse(directionArray.get(1));
			this.directionZ = InterpolatedDouble.parse(directionArray.get(2));
		} else if(!this.customDirection && object.has("min_speed_threshold"))
		{
			this.directionSpeedThreshhold = object.get("min_speed_threshold").getAsFloat();
		}
	}
	
	protected void parseUv(JsonObject object)
	{
		if(object.has("texture_width")) this.textureWidth = object.get("texture_width").getAsInt();
		if(object.has("texture_height")) this.textureHeight = object.get("texture_height").getAsInt();
		
		if(object.has("uv") && object.get("uv").isJsonArray())
		{
			JsonArray uv = object.getAsJsonArray("uv");
			
			if(uv.size() >= 2)
			{
				this.uvX = InterpolatedDouble.parse(uv.get(0));
				this.uvY = InterpolatedDouble.parse(uv.get(1));
			}
		}
		
		if(object.has("uv_size") && object.get("uv_size").isJsonArray())
		{
			JsonArray uv = object.getAsJsonArray("uv_size");
			
			if(uv.size() >= 2)
			{
				this.uvW = InterpolatedDouble.parse(uv.get(0));
				this.uvH = InterpolatedDouble.parse(uv.get(1));
			}
		}
		
		if(object.has("flipbook") && object.get("flipbook").isJsonObject())
		{
			this.flipbook = true;
			this.parseFlipbook(object.get("flipbook").getAsJsonObject());
		}
	}
	
	protected void parseFlipbook(JsonObject flipbook)
	{
		if(flipbook.has("base_UV") && flipbook.get("base_UV").isJsonArray())
		{
			JsonArray uv = flipbook.getAsJsonArray("base_UV");
			
			if(uv.size() >= 2)
			{
				this.uvX = InterpolatedDouble.parse(uv.get(0));
				this.uvY = InterpolatedDouble.parse(uv.get(1));
			}
		}
		
		if(flipbook.has("size_UV") && flipbook.get("size_UV").isJsonArray())
		{
			JsonArray uv = flipbook.getAsJsonArray("size_UV");
			
			if(uv.size() >= 2)
			{
				this.uvW = InterpolatedDouble.parse(uv.get(0));
				this.uvH = InterpolatedDouble.parse(uv.get(1));
			}
		}
		
		if(flipbook.has("step_UV") && flipbook.get("step_UV").isJsonArray())
		{
			JsonArray uv = flipbook.getAsJsonArray("step_UV");
			
			if(uv.size() >= 2)
			{
				this.stepX = uv.get(0).getAsFloat();
				this.stepY = uv.get(1).getAsFloat();
			}
		}
		
		if(flipbook.has("frames_per_second")) this.fps = flipbook.get("frames_per_second").getAsFloat();
		if(flipbook.has("max_frame")) this.maxFrame = InterpolatedDouble.parse(flipbook.get("max_frame"));
		if(flipbook.has("stretch_to_lifetime")) this.stretchFPS = flipbook.get("stretch_to_lifetime").getAsBoolean();
		if(flipbook.has("loop")) this.loop = flipbook.get("loop").getAsBoolean();
	}
	
	public static class ParcomAppearanceBillboardInstance
			implements IParticleRender
	{
		public final LzExpression sizeW;
		public final LzExpression sizeH;
		public final LzExpression directionX;
		public final LzExpression directionY;
		public final LzExpression directionZ;
		public final LzExpression uvX;
		public final LzExpression uvY;
		public final LzExpression uvW;
		public final LzExpression uvH;
		public final LzExpression maxFrame;
		
		public final CameraFacing facing;
		public final boolean customDirection;
		public final float directionSpeedThreshhold;
		public final boolean flipbook;
		public final float stepX;
		public final float stepY;
		public final float fps;
		public final boolean stretchFPS;
		public final boolean loop;
		public final int textureWidth;
		public final int textureHeight;
		
		/* Runtime properties */
		protected float w;
		protected float h;
		
		protected float u1;
		protected float v1;
		protected float u2;
		protected float v2;
		
		protected Matrix4f transform = new Matrix4f();
		protected Matrix4f rotation = new Matrix4f();
		protected Vector4f[] vertices = new Vector4f[] {
				new Vector4f(0, 0, 0, 1),
				new Vector4f(0, 0, 0, 1),
				new Vector4f(0, 0, 0, 1),
				new Vector4f(0, 0, 0, 1)
		};
		protected Vector3f vector = new Vector3f();
		protected Vector3f direction = new Vector3f();
		
		public ParcomAppearanceBillboardInstance(ParcomAppearanceBillboard o, LzVariableStore vars)
		{
			this.sizeW = o.sizeW.instantiate(vars);
			this.sizeH = o.sizeH.instantiate(vars);
			this.directionX = o.directionX.instantiate(vars);
			this.directionY = o.directionY.instantiate(vars);
			this.directionZ = o.directionZ.instantiate(vars);
			this.uvX = o.uvX.instantiate(vars);
			this.uvY = o.uvY.instantiate(vars);
			this.uvW = o.uvW.instantiate(vars);
			this.uvH = o.uvH.instantiate(vars);
			this.maxFrame = o.maxFrame.instantiate(vars);
			this.facing = o.facing;
			this.customDirection = o.customDirection;
			this.directionSpeedThreshhold = o.directionSpeedThreshhold;
			this.flipbook = o.flipbook;
			this.stepX = o.stepX;
			this.stepY = o.stepY;
			this.fps = o.fps;
			this.stretchFPS = o.stretchFPS;
			this.loop = o.loop;
			this.textureWidth = o.textureWidth;
			this.textureHeight = o.textureHeight;
		}
		
		@Override
		public boolean supportsCollissionRendering()
		{
			return getClass() == ParcomAppearanceBillboardInstance.class;
		}
		
		@Override
		public void render(ParticleEmitter emitter, BedrockParticle particle, VertexConsumer builder, PoseStack pose, float partialTicks)
		{
			this.calculateUVs(particle, partialTicks);
			
			/* Render the particle */
			double px = Math.lerp(particle.prevPosition.x, particle.position.x, partialTicks);
			double py = Math.lerp(particle.prevPosition.y, particle.position.y, partialTicks);
			double pz = Math.lerp(particle.prevPosition.z, particle.position.z, partialTicks);
			float angle = Math.lerp(particle.prevRotation, particle.rotation, partialTicks);
			
			Vector3d pos = this.calculatePosition(emitter, particle, px, py, pz);
			px = pos.x;
			py = pos.y;
			pz = pos.z;
			
			/* Calculate the geometry for billboards using cool matrix math */
			int light = emitter.getBrightnessForRender(partialTicks, px, py, pz);
			
			this.calculateFacing(emitter, particle, px, py, pz);
			
			this.rotation.identity().rotateZ(angle / 180 * org.zeith.hammeranims.joml.Math.PI_f);
			this.transform.mul(this.rotation);
			this.transform.setTranslation(new Vector3f((float) px, (float) py, (float) pz));
			
			for(Vector4f vt : this.vertices)
				this.transform.transform(vt);
			
			val norm = transform.transformPosition(new Vector3f(0, 1, 0)).normalize();
			
			float u1 = this.u1 / (float) this.textureWidth;
			float u2 = this.u2 / (float) this.textureWidth;
			float v1 = this.v1 / (float) this.textureHeight;
			float v2 = this.v2 / (float) this.textureHeight;
			
			var lp = pose.last().pose();
			
			builder.vertex(lp, this.vertices[0].x, this.vertices[0].y, this.vertices[0].z)
				   .uv(u1, v1)
				   .color(particle.r, particle.g, particle.b, particle.a)
				   .uv2(light)
				   .endVertex();
			
			builder.vertex(lp, this.vertices[1].x, this.vertices[1].y, this.vertices[1].z)
				   .uv(u2, v1)
				   .color(particle.r, particle.g, particle.b, particle.a)
				   .uv2(light)
				   .endVertex();
			
			builder.vertex(lp, this.vertices[2].x, this.vertices[2].y, this.vertices[2].z)
				   .uv(u2, v2)
				   .color(particle.r, particle.g, particle.b, particle.a)
				   .uv2(light)
				   .endVertex();
			
			builder.vertex(lp, this.vertices[3].x, this.vertices[3].y, this.vertices[3].z)
				   .uv(u1, v2)
				   .color(particle.r, particle.g, particle.b, particle.a)
				   .uv2(light)
				   .endVertex();
		}
		
		protected void calculateFacing(ParticleEmitter emitter, BedrockParticle particle, double px, double py, double pz)
		{
			rotation.identity();
			transform.identity();
			
			/* Calculate yaw and pitch based on the facing mode */
			float cameraYaw = emitter.cYaw;
			float cameraPitch = emitter.cPitch;
			double cameraX = emitter.cX;
			double cameraY = emitter.cY;
			double cameraZ = emitter.cZ;
			
			/* Flip width when frontal perspective mode */
			if(emitter.perspective == CameraType.THIRD_PERSON_FRONT)
			{
				this.w = -this.w;
			}
			/* In GUI renderer */
			else if(emitter.perspective == null && !this.facing.isLookAt)
			{
				cameraYaw = 180 - cameraYaw;
				
				this.w = -this.w;
				this.h = -this.h;
			}
			
			if(this.facing.isLookAt && !this.facing.isDirection)
			{
				double dX = cameraX - px;
				double dY = cameraY - py;
				double dZ = cameraZ - pz;
				
				double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);
				
				cameraYaw = 180 - (float) (Math.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;
				cameraPitch = (float) (-(Math.atan2(dY, horizontalDistance) * (180D / Math.PI))) + 180;
			}
			
			if(this.facing.isDirection)
			{
				if(this.customDirection)
				{
					/* evaluate custom direction molang */
					this.direction.x = (float) this.directionX.get();
					this.direction.y = (float) this.directionY.get();
					this.direction.z = (float) this.directionZ.get();
				} else if(particle.speed.lengthSquared() > this.directionSpeedThreshhold * this.directionSpeedThreshhold)
				{
					this.direction.set(particle.speed);
					this.direction.normalize();
				} else
				{
					this.direction.set(1, 0, 0);
				}
				
				double lengthSq = this.direction.lengthSquared();
				if(lengthSq < 0.0001)
				{
					this.direction.set(1, 0, 0);
				} else if(Math.abs(lengthSq - 1) > 0.0001)
				{
					this.direction.normalize();
				}
			}
			
			this.calculateVertices(emitter, particle);
			
			switch(this.facing)
			{
				case ROTATE_XYZ:
				case LOOKAT_XYZ:
					this.rotation.identity().rotateY(Math.toRadians(cameraYaw));
					this.transform.mul(this.rotation);
					this.rotation.identity().rotateX(Math.toRadians(cameraPitch));
					this.transform.mul(this.rotation);
					return;
				case ROTATE_Y:
				case LOOKAT_Y:
					this.rotation.identity().rotateY(Math.toRadians(cameraYaw));
					this.transform.mul(this.rotation);
					return;
				case EMITTER_YZ:
					if(!emitter.isRenderingGUI)
					{
						this.rotation.identity().rotateZ(Math.toRadians(180));
						this.transform.mul(this.rotation);
						this.rotation.identity().rotateY(Math.toRadians(90));
						this.transform.mul(this.rotation);
					} else
					{
						this.rotation.identity().rotateY(Math.toRadians(-90));
						this.transform.mul(this.rotation);
					}
					return;
				case EMITTER_XZ:
					if(!emitter.isRenderingGUI)
					{
						this.rotation.identity().rotateX(Math.toRadians(90));
						this.transform.mul(this.rotation);
					} else
					{
						this.rotation.identity().rotateZ(Math.toRadians(180));
						this.transform.mul(this.rotation);
						this.rotation.identity().rotateX(Math.toRadians(-90));
						this.transform.mul(this.rotation);
					}
					return;
				case EMITTER_XY:
					if(!emitter.isRenderingGUI)
					{
						this.rotation.identity().rotateX(Math.toRadians(180));
						this.transform.mul(this.rotation);
					} else
					{
						this.rotation.identity().rotateY(Math.toRadians(180));
						this.transform.mul(this.rotation);
					}
					return;
				case DIRECTION_X:
					this.rotation.identity().rotateY(Math.toRadians(this.getYaw()));
					this.transform.mul(this.rotation);
					this.rotation.identity().rotateX(Math.toRadians(this.getPitch()));
					this.transform.mul(this.rotation);
					this.rotation.identity().rotateY(Math.toRadians(90));
					this.transform.mul(this.rotation);
					return;
				case DIRECTION_Y:
					this.rotation.identity().rotateY(Math.toRadians(this.getYaw()));
					this.transform.mul(this.rotation);
					this.rotation.identity().rotateX(Math.toRadians(this.getPitch() + 90));
					this.transform.mul(this.rotation);
					return;
				case DIRECTION_Z:
					this.rotation.identity().rotateY(Math.toRadians(this.getYaw()));
					this.transform.mul(this.rotation);
					this.rotation.identity().rotateX(Math.toRadians(this.getPitch()));
					this.transform.mul(this.rotation);
					return;
				case LOOKAT_DIRECTION:
					this.rotation.identity();
					this.rotation.identity().rotateY(Math.toRadians(this.getYaw()));
					this.transform.mul(this.rotation);
					this.rotation.identity().rotateX(Math.toRadians(this.getPitch() + 90));
					this.transform.mul(this.rotation);
					
					Vector3f cameraDir = new Vector3f(
							(float) (cameraX - px),
							(float) (cameraY - py),
							(float) (cameraZ - pz)
					);
					
					Vector3f rotatedNormal = new Vector3f(0, 0, 1);
					
					this.transform.transformPosition(rotatedNormal);
					
					/*
					 * The direction vector is the normal of the plane used for calculating the rotation around local y Axis.
					 * Project the cameraDir onto that plane to find out the axis angle (direction vector is the y axis).
					 */
					Vector3f projectDir = new Vector3f(this.direction);
					projectDir.mul(cameraDir.dot(this.direction));
					cameraDir.sub(projectDir);
					
					if(cameraDir.lengthSquared() < 1.0e-30) return;
					
					cameraDir.normalize();
					
					/*
					 * The angle between two vectors is only between 0 and 180 degrees.
					 * RotationDirection will be parallel to direction but pointing in different directions depending
					 * on the rotation of cameraDir. Use this to find out the sign of the angle
					 * between cameraDir and the rotatedNormal.
					 */
					Vector3f rotationDirection = new Vector3f();
					rotationDirection.cross(cameraDir, rotatedNormal);
					
					this.rotation.identity().rotateY(-java.lang.Math.copySign(cameraDir.angle(rotatedNormal), rotationDirection.dot(this.direction)));
					this.transform.mul(this.rotation);
					return;
				default:
					// Unknown facing mode
					return;
			}
		}
		
		/**
		 * @return the yaw angle in degrees of this {@link #direction}
		 */
		private float getYaw()
		{
			double yaw = Math.atan2(-this.direction.x, this.direction.z);
			yaw = Math.toDegrees(yaw);
			if(yaw < -180)
			{
				yaw += 360;
			} else if(yaw > 180)
			{
				yaw -= 360;
			}
			return (float) -yaw;
		}
		
		/**
		 * @return the pitch angle in degrees of this {@link #direction}
		 */
		private float getPitch()
		{
			double pitch = Math.atan2(this.direction.y, Math.sqrt(this.direction.x * this.direction.x + this.direction.z * this.direction.z));
			return (float) -Math.toDegrees(pitch);
		}
		
		protected void calculateVertices(ParticleEmitter emitter, BedrockParticle particle)
		{
			this.transform.identity();
			
			float hw = this.w * 0.5f;
			float hh = this.h * 0.5f;
			
			if(particle.relativeScaleBillboard)
			{
				hw *= emitter.scale[0];
				hh *= emitter.scale[1];
			}
			
			this.vertices[0].set(-hw, -hh, 0, 1);
			this.vertices[1].set(hw, -hh, 0, 1);
			this.vertices[2].set(hw, hh, 0, 1);
			this.vertices[3].set(-hw, hh, 0, 1);
		}
		
		protected Vector3d calculatePosition(ParticleEmitter emitter, BedrockParticle particle, double px, double py, double pz)
		{
			if(particle.relativePosition && particle.relativeRotation)
			{
				this.vector.set((float) px, (float) py, (float) pz);
				
				if(particle.relativeScale)
				{
					Vector3d pos = new Vector3d(px, py, pz);
					
					Matrix3d scale = new Matrix3d(emitter.scale[0], 0, 0,
							0, emitter.scale[1], 0,
							0, 0, emitter.scale[2]
					);
					
					scale.transform(pos);
					
					this.vector.x = (float) pos.x;
					this.vector.y = (float) pos.y;
					this.vector.z = (float) pos.z;
				}
				
				emitter.rotation.transform(this.vector);
				
				px = this.vector.x;
				py = this.vector.y;
				pz = this.vector.z;
				
				px += emitter.lastGlobal.x;
				py += emitter.lastGlobal.y;
				pz += emitter.lastGlobal.z;
			} else if(particle.relativeScale)
			{
				Vector3d pos = new Vector3d(px, py, pz);
				
				Matrix3d scale = new Matrix3d(emitter.scale[0], 0, 0,
						0, emitter.scale[1], 0,
						0, 0, emitter.scale[2]
				);
				
				pos.sub(emitter.lastGlobal); //transform back to local
				scale.transform(pos);
				pos.add(emitter.lastGlobal); //transform back to global
				
				px = pos.x;
				py = pos.y;
				pz = pos.z;
			}
			
			return new Vector3d(px, py, pz);
		}
		
		@Override
		public void renderOnScreen(BedrockParticle particle, VertexConsumer builder, PoseStack pose, int x, int y, float scale, float partialTicks)
		{
			this.calculateUVs(particle, partialTicks);
			
			this.w = this.h = 0.5F;
			float angle = Math.lerp(particle.prevRotation, particle.rotation, partialTicks);
			
			/* Calculate the geometry for billboards using cool matrix math */
			float hw = this.w * 0.5f;
			float hh = this.h * 0.5f;
			this.vertices[0].set(-hw, -hh, 0, 1);
			this.vertices[1].set(hw, -hh, 0, 1);
			this.vertices[2].set(hw, hh, 0, 1);
			this.vertices[3].set(-hw, hh, 0, 1);
			
			this.transform.identity();
			this.transform.scale(scale * 2.75F);
			this.transform.setTranslation(new Vector3f(x, y - scale / 2, 0));
			
			this.rotation.identity().rotateZ(angle / 180 * org.zeith.hammeranims.joml.Math.PI_f);
			this.transform.mul(this.rotation);
			
			for(Vector4f vertex : this.vertices)
				this.transform.transform(vertex);
			
			float u1 = this.u1 / (float) this.textureWidth;
			float u2 = this.u2 / (float) this.textureWidth;
			float v1 = this.v1 / (float) this.textureHeight;
			float v2 = this.v2 / (float) this.textureHeight;
			
			var lp = pose.last().pose();
			builder.vertex(lp, this.vertices[0].x, this.vertices[0].y, this.vertices[0].z).uv(u1, v1).color(particle.r, particle.g, particle.b, particle.a).uv2(LightTexture.FULL_BRIGHT).endVertex();
			builder.vertex(lp, this.vertices[1].x, this.vertices[1].y, this.vertices[1].z).uv(u2, v1).color(particle.r, particle.g, particle.b, particle.a).uv2(LightTexture.FULL_BRIGHT).endVertex();
			builder.vertex(lp, this.vertices[2].x, this.vertices[2].y, this.vertices[2].z).uv(u2, v2).color(particle.r, particle.g, particle.b, particle.a).uv2(LightTexture.FULL_BRIGHT).endVertex();
			builder.vertex(lp, this.vertices[3].x, this.vertices[3].y, this.vertices[3].z).uv(u1, v2).color(particle.r, particle.g, particle.b, particle.a).uv2(LightTexture.FULL_BRIGHT).endVertex();
		}
		
		public void calculateUVs(BedrockParticle particle, float partialTicks)
		{
			/* Update particle's UVs and size */
			this.w = (float) this.sizeW.get() * 2.25F;
			this.h = (float) this.sizeH.get() * 2.25F;
			
			float u = (float) this.uvX.get();
			float v = (float) this.uvY.get();
			float w = (float) this.uvW.get();
			float h = (float) this.uvH.get();
			
			if(this.flipbook)
			{
				int index = (int) (particle.getAge(partialTicks) * this.fps);
				int max = (int) this.maxFrame.get();
				
				if(this.stretchFPS)
				{
					float lifetime = (particle.lifetime <= 0) ? 0 : (particle.age + partialTicks) / (particle.lifetime);
					
					//for particles with expiration - stretch differently since lifetime changed
					if(particle.getExpireAge() != -1)
					{
						lifetime = (particle.lifetime <= 0) ? 0 : (particle.age + partialTicks) / (particle.getExpirationDelay());
					}
					
					index = (int) (lifetime * max);
				}
				
				if(this.loop && max != 0)
				{
					index = index % max;
				}
				
				if(index > max)
				{
					index = max;
				}
				
				u += this.stepX * index;
				v += this.stepY * index;
			}
			
			this.u1 = u;
			this.v1 = v;
			this.u2 = u + w;
			this.v2 = v + h;
		}
	}
}