package org.zeith.hammeranims.api;

import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.zeith.api.registry.MappedRegistryBuilder;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.animsys.actions.AnimationAction;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.api.particles.components.IParticleComponentType;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class HammerAnimationsApi
{
	public static final float APPROX_ZERO = 1.0E-30F;
	public static final IEventBus EVENT_BUS = BusBuilder.builder().build();
	
	static
	{
		Keys.init();
	}
	
	private static final List<IResourceProvider> AUXILIARY_RESOURCE_PROVIDERS = Lists.newArrayList();
	
	public static final Registry<IAnimationContainer> ANIMATION_CONTAINER = new MappedRegistryBuilder<>(IAnimationContainer.class, Keys.ANIMATION_CONTAINERS).create();
	public static final Registry<IGeometryContainer> GEOMETRY_CONTAINER = new MappedRegistryBuilder<>(IGeometryContainer.class, Keys.GEOMETRY_CONTAINERS).create();
	public static final Registry<TimeFunction> TIME_FUNCTION = new MappedRegistryBuilder<>(TimeFunction.class, Keys.TIME_FUNCTIONS).defaultKey(HammerAnimations.id("linear")).create();
	public static final Registry<AnimationAction> ANIMATION_ACTION = new MappedRegistryBuilder<>(AnimationAction.class, Keys.ANIMATION_ACTIONS).defaultKey(HammerAnimations.id("empty")).create();
	public static final Registry<IParticleContainer> PARTICLE_CONTAINER = new MappedRegistryBuilder<>(IParticleContainer.class, Keys.PARTICLE_CONTAINERS).create();
	public static final Registry<IParticleComponentType> PARTICLE_COMPONENT_TYPE = new MappedRegistryBuilder<>(IParticleComponentType.class, Keys.PARTICLE_COMPONENT_TYPES).create();
	
	public static boolean LOG_RELOADS = !Boolean.parseBoolean(System.getProperty("hammeranims.silence"));
	
	@SubscribeEvent
	public static void newRegistries(NewRegistryEvent e)
	{
		e.register(ANIMATION_CONTAINER);
		e.register(GEOMETRY_CONTAINER);
		e.register(TIME_FUNCTION);
		e.register(ANIMATION_ACTION);
		e.register(PARTICLE_CONTAINER);
		e.register(PARTICLE_COMPONENT_TYPE);
	}
	
	public static void addAuxiliaryResourceProvider(IResourceProvider provider)
	{
		AUXILIARY_RESOURCE_PROVIDERS.add(provider);
	}
	
	public static List<IResourceProvider> getAuxiliaryResourceProviders()
	{
		return Collections.unmodifiableList(AUXILIARY_RESOURCE_PROVIDERS);
	}
	
	public static Registry<IAnimationContainer> animations()
	{
		return ANIMATION_CONTAINER;
	}
	
	public static Registry<IGeometryContainer> geometries()
	{
		return GEOMETRY_CONTAINER;
	}
	
	public static Registry<TimeFunction> timeFunctions()
	{
		return TIME_FUNCTION;
	}
	
	public static Registry<AnimationAction> animationActions()
	{
		return ANIMATION_ACTION;
	}
	
	public static Registry<IParticleContainer> particleContainers()
	{
		return PARTICLE_CONTAINER;
	}
	
	public static Registry<IParticleComponentType> particleComponentTypes()
	{
		return PARTICLE_COMPONENT_TYPE;
	}
	
	public static class Keys
	{
		public static final ResourceKey<Registry<IAnimationContainer>> ANIMATION_CONTAINERS = key("animations");
		public static final ResourceKey<Registry<IGeometryContainer>> GEOMETRY_CONTAINERS = key("geometry");
		public static final ResourceKey<Registry<TimeFunction>> TIME_FUNCTIONS = key("time_functions");
		public static final ResourceKey<Registry<AnimationAction>> ANIMATION_ACTIONS = key("animation_actions");
		public static final ResourceKey<Registry<IParticleContainer>> PARTICLE_CONTAINERS = key("particle_containers");
		public static final ResourceKey<Registry<IParticleComponentType>> PARTICLE_COMPONENT_TYPES = key("particle_component_types");
		
		private static <T> ResourceKey<Registry<T>> key(String name)
		{
			return ResourceKey.createRegistryKey(HammerAnimations.id(name));
		}
		
		private static void init()
		{
		}
	}
}