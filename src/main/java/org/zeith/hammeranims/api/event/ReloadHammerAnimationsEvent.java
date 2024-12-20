package org.zeith.hammeranims.api.event;

import lombok.Getter;
import shaded.event.Event;
import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Stream;

@Getter
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
	
	/**
	 * This even is fired on {@link org.zeith.hammeranims.api.HammerAnimationsApi#EVENT_BUS} whenever the mod performs a reload.
	 * Call {@link #addReload(CompletableFuture[])} to enqueue a custom reload of any given type.
	 * Be sure to defer all the sub-tasks into {@link #getBackgroundExecutor()}
	 */
	public static class EnqueueReloads
			extends ReloadHammerAnimationsEvent
	{
		protected final Consumer<CompletableFuture<?>> extraReload;
		
		/**
		 * -- GETTER --
		 * The main game thread for a given logical side.
		 * Please avoid adding any futures through
		 * or
		 * that run on this executor to avoid deadlocks.
		 */
		@Getter
		protected final Executor gameExecutor,
		/**
		 * -- GETTER --
		 * The worker thread pool used to parallelize a load with as many threads as possible.
		 * Any and all tasks that are going to be enqueued into this event, should be using this executor.
		 */
		backgroundExecutor;
		
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