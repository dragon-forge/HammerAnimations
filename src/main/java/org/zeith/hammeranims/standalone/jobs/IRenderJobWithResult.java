package org.zeith.hammeranims.standalone.jobs;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.*;

public interface IRenderJobWithResult<T>
{
	CompletableFuture<T> render(GlWindow window);
	
	default <R> IRenderJobWithResult<R> map(Function<T, R> map)
	{
		return window -> render(window).thenApply(map);
	}
	
	default <R> IRenderJobWithResult<R> mapAsync(Function<T, R> map, Executor executor)
	{
		return window -> render(window).thenApplyAsync(map, executor);
	}
	
	default IRenderJobSource toJob(Consumer<CompletableFuture<T>> onFinish)
	{
		return () -> (window) -> onFinish.accept(render(window));
	}
	
	static <T> IRenderJobSource wrap(IRenderJobWithResult<T> job, Consumer<? super T> onFinish)
	{
		return job.toJob((t) -> t.thenAccept(onFinish));
	}
	
	static <T> IRenderJobSource wrap(Supplier<IRenderJobWithResult<T>> job, Consumer<? super T> onFinish)
	{
		return () -> job.get().toJob((t) -> t.thenAccept(onFinish)).asJob();
	}
	
	static <T> IRenderJobSource wrapAsync(Supplier<IRenderJobWithResult<T>> job, Consumer<? super T> onFinish, Executor executor)
	{
		return () -> job.get().toJob((t) -> t.thenAcceptAsync(onFinish, executor)).asJob();
	}
}