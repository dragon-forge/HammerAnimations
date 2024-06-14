package org.zeith.hammeranims.api;

import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.zeith.api.registry.RegistryMapping;
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
	
	private static Registry<IAnimationContainer> ANIMATION_CONTAINERS;
	private static Registry<IGeometryContainer> GEOMETRY_CONTAINERS;
	private static Registry<TimeFunction> TIME_FUNCTIONS;
	private static Registry<AnimationAction> ANIMATION_ACTIONS;
	private static Registry<IParticleContainer> PARTICLE_CONTAINERS;
	private static Registry<IParticleComponentType> PARTICLE_COMPONENT_TYPES;
	private static boolean hasInitialized = false;
	
	public static boolean LOG_RELOADS = !Boolean.parseBoolean(System.getProperty("hammeranims.silence"));
	
	@SubscribeEvent
	public static void newRegistries(NewRegistryEvent e)
	{
		RegistryMapping.report(IAnimationContainer.class, ANIMATION_CONTAINERS = e.create(new RegistryBuilder<>(Keys.ANIMATION_CONTAINERS)), false);
		RegistryMapping.report(IGeometryContainer.class, GEOMETRY_CONTAINERS = e.create(new RegistryBuilder<>(Keys.GEOMETRY_CONTAINERS)), false);
		RegistryMapping.report(TimeFunction.class, TIME_FUNCTIONS = e.create(new RegistryBuilder<>(Keys.TIME_FUNCTIONS).defaultKey(HammerAnimations.id("linear"))), false);
		RegistryMapping.report(AnimationAction.class, ANIMATION_ACTIONS = e.create(new RegistryBuilder<>(Keys.ANIMATION_ACTIONS).defaultKey(HammerAnimations.id("empty"))), false);
		RegistryMapping.report(IParticleContainer.class, PARTICLE_CONTAINERS = e.create(new RegistryBuilder<>(Keys.PARTICLE_CONTAINERS)), false);
		RegistryMapping.report(IParticleComponentType.class, PARTICLE_COMPONENT_TYPES = e.create(new RegistryBuilder<>(Keys.PARTICLE_COMPONENT_TYPES)), false);
		
		hasInitialized = true;
	}
	
	public static boolean hasInitialized()
	{
		return hasInitialized;
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
		return ANIMATION_CONTAINERS;
	}
	
	public static Registry<IGeometryContainer> geometries()
	{
		return GEOMETRY_CONTAINERS;
	}
	
	public static Registry<TimeFunction> timeFunctions()
	{
		return TIME_FUNCTIONS;
	}
	
	public static Registry<AnimationAction> animationActions()
	{
		return ANIMATION_ACTIONS;
	}
	
	public static Registry<IParticleContainer> particleContainers()
	{
		return PARTICLE_CONTAINERS;
	}
	
	public static Registry<IParticleComponentType> particleComponentTypes()
	{
		return PARTICLE_COMPONENT_TYPES;
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