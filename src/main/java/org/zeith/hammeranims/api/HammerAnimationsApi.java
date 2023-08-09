package org.zeith.hammeranims.api;

import com.google.common.collect.Lists;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;
import org.zeith.api.registry.RegistryMapping;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.util.*;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class HammerAnimationsApi
{
	public static final IEventBus EVENT_BUS = BusBuilder.builder().build();
	
	private static final List<IResourceProvider> AUXILIARY_RESOURCE_PROVIDERS = Lists.newArrayList();
	
	private static Supplier<IForgeRegistry<IAnimationContainer>> ANIMATION_CONTAINERS;
	private static Supplier<IForgeRegistry<IGeometryContainer>> GEOMETRY_CONTAINERS;
	private static Supplier<IForgeRegistry<AnimationSourceType>> ANIMATION_SOURCES;
	private static Supplier<IForgeRegistry<TimeFunction>> TIME_FUNCTIONS;
	private static Supplier<IForgeRegistry<AnimationAction>> ANIMATION_ACTIONS;
	private static boolean hasInitialized = false;
	
	@SubscribeEvent
	public static void newRegistries(NewRegistryEvent e)
	{
		ANIMATION_CONTAINERS = e.create(new RegistryBuilder<IAnimationContainer>()
						.setName(HammerAnimations.id("animations")),
				reg -> RegistryMapping.report(IAnimationContainer.class, reg, false)
		);
		
		GEOMETRY_CONTAINERS = e.create(new RegistryBuilder<IGeometryContainer>()
						.setName(HammerAnimations.id("geometry")),
				reg -> RegistryMapping.report(IGeometryContainer.class, reg, false)
		);
		
		ANIMATION_SOURCES = e.create(new RegistryBuilder<AnimationSourceType>()
						.setName(HammerAnimations.id("animation_sources"))
						.disableSaving(),
				reg -> RegistryMapping.report(AnimationSourceType.class, reg, false)
		);
		
		TIME_FUNCTIONS = e.create(new RegistryBuilder<TimeFunction>()
						.setName(HammerAnimations.id("time_functions"))
						.setDefaultKey(HammerAnimations.id("linear")),
				reg -> RegistryMapping.report(TimeFunction.class, reg, false)
		);
		
		ANIMATION_ACTIONS = e.create(new RegistryBuilder<AnimationAction>()
						.setName(HammerAnimations.id("animation_actions")),
				reg -> RegistryMapping.report(AnimationAction.class, reg, false)
		);
		
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
		return ANIMATION_CONTAINERS.get();
	}
	
	public static IForgeRegistry<IGeometryContainer> geometries()
	{
		return GEOMETRY_CONTAINERS.get();
	}
	
	public static IForgeRegistry<AnimationSourceType> animationSources()
	{
		return ANIMATION_SOURCES.get();
	}
	
	public static IForgeRegistry<TimeFunction> timeFunctions()
	{
		return TIME_FUNCTIONS.get();
	}
	
	public static IForgeRegistry<AnimationAction> animationActions()
	{
		return ANIMATION_ACTIONS.get();
	}
}