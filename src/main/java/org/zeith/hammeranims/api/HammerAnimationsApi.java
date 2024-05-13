package org.zeith.hammeranims.api;

import com.google.common.collect.Lists;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
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

@Mod.EventBusSubscriber
public class HammerAnimationsApi
{
	public static final EventBus EVENT_BUS = new EventBus();
	
	private static final List<IResourceProvider> AUXILIARY_RESOURCE_PROVIDERS = Lists.newArrayList();
	
	private static IForgeRegistry<IAnimationContainer> ANIMATION_CONTAINERS;
	private static IForgeRegistry<IGeometryContainer> GEOMETRY_CONTAINERS;
	private static IForgeRegistry<TimeFunction> TIME_FUNCTIONS;
	private static IForgeRegistry<AnimationAction> ANIMATION_ACTIONS;
	private static IForgeRegistry<IParticleContainer> PARTICLE_CONTAINERS;
	private static IForgeRegistry<IParticleComponentType> PARTICLE_COMPONENT_TYPES;
	private static boolean hasInitialized = false;
	
	public static boolean LOG_RELOADS = !Boolean.parseBoolean(System.getProperty("hammeranims.silence"));
	
	@SubscribeEvent
	public static void newRegistries(RegistryEvent.NewRegistry e)
	{
		ANIMATION_CONTAINERS = new RegistryBuilder<IAnimationContainer>()
				.setType(IAnimationContainer.class)
				.setName(HammerAnimations.id("animations"))
				.disableSaving()
				.create();
		
		GEOMETRY_CONTAINERS = new RegistryBuilder<IGeometryContainer>()
				.setType(IGeometryContainer.class)
				.setName(HammerAnimations.id("geometry"))
				.disableSaving()
				.create();
		
		TIME_FUNCTIONS = new RegistryBuilder<TimeFunction>()
				.setType(TimeFunction.class)
				.setName(HammerAnimations.id("time_functions"))
				.setDefaultKey(HammerAnimations.id("linear"))
				.disableSaving()
				.create();
		
		ANIMATION_ACTIONS = new RegistryBuilder<AnimationAction>()
				.setType(AnimationAction.class)
				.setName(HammerAnimations.id("animation_actions"))
				.setDefaultKey(HammerAnimations.id("empty"))
				.disableSaving()
				.create();
		
		PARTICLE_CONTAINERS = new RegistryBuilder<IParticleContainer>()
				.setType(IParticleContainer.class)
				.setName(HammerAnimations.id("particle_containers"))
				.disableSaving()
				.create();
		
		PARTICLE_COMPONENT_TYPES = new RegistryBuilder<IParticleComponentType>()
				.setType(IParticleComponentType.class)
				.setName(HammerAnimations.id("particle_component_types"))
				.disableSaving()
				.create();
		
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
	
	public static IForgeRegistry<IAnimationContainer> animations()
	{
		return ANIMATION_CONTAINERS;
	}
	
	public static IForgeRegistry<IGeometryContainer> geometries()
	{
		return GEOMETRY_CONTAINERS;
	}
	
	public static IForgeRegistry<TimeFunction> timeFunctions()
	{
		return TIME_FUNCTIONS;
	}
	
	public static IForgeRegistry<AnimationAction> animationActions()
	{
		return ANIMATION_ACTIONS;
	}
	
	public static IForgeRegistry<IParticleContainer> particleContainers()
	{
		return PARTICLE_CONTAINERS;
	}
	
	public static IForgeRegistry<IParticleComponentType> particleComponentTypes()
	{
		return PARTICLE_COMPONENT_TYPES;
	}
}