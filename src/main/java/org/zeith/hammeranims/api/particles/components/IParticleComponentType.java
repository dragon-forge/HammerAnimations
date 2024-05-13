package org.zeith.hammeranims.api.particles.components;

import com.google.gson.JsonElement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface IParticleComponentType
		extends IForgeRegistryEntry<IParticleComponentType>
{
	Map<ResourceLocation, ResourceLocation> VANILLA_COMPONENTS = Stream.of( // List of all vanilla components
			"emitter_local_space", "emitter_initialization",
			"emitter_rate_instant", "emitter_rate_steady",
			"emitter_lifetime_looping", "emitter_lifetime_once", "emitter_lifetime_expression",
			"emitter_shape_disc", "emitter_shape_box", "emitter_shape_entity_aabb", "emitter_shape_point", "emitter_shape_sphere",
			"particle_lifetime_expression", "particle_expire_if_in_blocks", "particle_expire_if_not_in_blocks", "particle_kill_plane",
			"particle_appearance_billboard", "particle_appearance_lighting", "particle_appearance_tinting",
			"particle_initial_speed", "particle_initial_spin", "particle_motion_collision", "particle_motion_dynamic", "particle_motion_parametric"
	).collect(Collectors.toMap(ResourceLocation::new, HammerAnimations::id));
	
	IParticleComponent fromJson(JsonElement element);
	
	boolean canBeEmpty();
	
	/**
	 * Gets the registry key associated with this particle component.
	 * This key can be used to identify and retrieve the component from registry.
	 *
	 * @return The {@link ResourceLocation} registry key.
	 */
	default ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.particleComponentTypes().getKey(this);
	}
	
	static IParticleComponentType byId(ResourceLocation id)
	{
		return HammerAnimationsApi.particleComponentTypes().getValue(VANILLA_COMPONENTS.getOrDefault(id, id));
	}
	
	static SimpleBuilder builder()
	{
		return new SimpleBuilder();
	}
	
	static IParticleComponentType create(IComponentDeserializer deserializer)
	{
		return builder().deserializer(deserializer).build();
	}
	
	class SimpleBuilder
	{
		protected boolean canBeEmpty = false;
		protected IComponentDeserializer deserializer;
		
		public SimpleBuilder canBeEmpty(boolean canBeEmpty)
		{
			this.canBeEmpty = canBeEmpty;
			return this;
		}
		
		public SimpleBuilder deserializer(IComponentDeserializer deserializer)
		{
			this.deserializer = deserializer;
			return this;
		}
		
		public SimpleType build()
		{
			return new SimpleType(canBeEmpty, deserializer);
		}
	}
	
	class SimpleType
			extends IForgeRegistryEntry.Impl<IParticleComponentType>
			implements IParticleComponentType
	{
		protected final boolean canBeEmpty;
		protected final IComponentDeserializer deserializer;
		
		public SimpleType(boolean canBeEmpty, IComponentDeserializer deserializer)
		{
			this.canBeEmpty = canBeEmpty;
			this.deserializer = deserializer;
		}
		
		@Override
		public IParticleComponent fromJson(JsonElement element)
		{
			return deserializer.fromJson(element);
		}
		
		@Override
		public boolean canBeEmpty()
		{
			return canBeEmpty;
		}
	}
}