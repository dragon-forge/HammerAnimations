package org.zeith.hammeranims.api.event;

import net.neoforged.bus.api.Event;
import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class ReloadHammerAnimationsEvent
		extends Event
{
	private final IResourceProvider resources;
	private final boolean clientSide;
	
	protected ReloadHammerAnimationsEvent(IResourceProvider provider, boolean clientSide)
	{
		this.resources = provider;
		this.clientSide = clientSide;
	}
	
	public IResourceProvider getResources()
	{
		return resources;
	}
	
	public boolean isClientSide()
	{
		return clientSide;
	}
	
	/**
	 * This even is fired on {@link org.zeith.hammeranims.api.HammerAnimationsApi#EVENT_BUS} whenever the mod performs a reload.
	 * Call {@link #addReload(CompletableFuture[])} to enqueue a custom reload of any given type.
	 * Be sure to defer all the sub-tasks into {@link #getBackgroundExecutor()}
	 */
	public static class EnqueueReloads
			extends ReloadHammerAnimationsEvent
	{
		protected final Consumer<CompletableFuture<?>> extraReload;
		protected final Executor gameExecutor, backgroundExecutor;
		
		public EnqueueReloads(IResourceProvider provider, boolean clientSide, Consumer<CompletableFuture<?>> extraReload, Executor gameExecutor, Executor backgroundExecutor)
		{
			super(provider, clientSide);
			this.extraReload = extraReload;
			this.gameExecutor = gameExecutor;
			this.backgroundExecutor = backgroundExecutor;
		}
		
		/**
		 * Adds a single reload task from many sub-tasks, requiring the game wait for all of them to complete (on {@link #getBackgroundExecutor()}).
		 */
		public void addReload(CompletableFuture<?>... cfs)
		{
			addReloadTask(CompletableFuture.allOf(cfs));
		}
		
		/**
		 * Adds a single reload task, that's going to get fulfilled at some point.
		 */
		public void addReloadTask(CompletableFuture<?> task)
		{
			extraReload.accept(task);
		}
		
		public <T> void enqueue(Stream<T> task, BiFunction<T, Executor, CompletableFuture<?>> factory)
		{
			addReload(task.map(t -> factory.apply(t, getBackgroundExecutor())).toArray(CompletableFuture[]::new));
		}
		
		/**
		 * The main game thread for a given logical side.
		 * Please avoid adding any futures through {@link #addReload(CompletableFuture[])} or {@link #addReloadTask(CompletableFuture)} that run on this executor to avoid deadlocks.
		 */
		public Executor getGameExecutor()
		{
			return gameExecutor;
		}
		
		/**
		 * The worker thread pool used to parallelize a load with as many threads as possible.
		 * Any and all tasks that are going to be enqueued into this event, should be using this executor.
		 */
		public Executor getBackgroundExecutor()
		{
			return backgroundExecutor;
		}
	}
	
	/**
	 * Fires on {@link org.zeith.hammeranims.api.HammerAnimationsApi#EVENT_BUS} after the resource reload is complete.
	 * Fired on the {@link EnqueueReloads#getGameExecutor()} of {@link EnqueueReloads}
	 */
	public static class Post
			extends ReloadHammerAnimationsEvent
	{
		public Post(IResourceProvider provider, boolean clientSide)
		{
			super(provider, clientSide);
		}
	}
}