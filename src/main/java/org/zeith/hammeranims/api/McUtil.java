package org.zeith.hammeranims.api;

import com.google.common.util.concurrent.MoreExecutors;
import com.zeitheron.hammercore.utils.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.zeith.hammeranims.HammerAnimations;

import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class McUtil
{
	private static final ExecutorService BACKGROUND_EXECUTOR = makeExecutor("Main");
	private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
	
	public static ExecutorService backgroundExecutor()
	{
		return BACKGROUND_EXECUTOR;
	}
	
	private static ExecutorService makeExecutor(String serviceName)
	{
		int i = MathHelper.clip(Runtime.getRuntime().availableProcessors() - 1, 1, getMaxThreads());
		
		if(i <= 0)
			return MoreExecutors.newDirectExecutorService();
		
		return new ForkJoinPool(i, pool ->
		{
			ForkJoinWorkerThread worker = new ForkJoinWorkerThread(pool)
			{
				@Override
				protected void onTermination(Throwable err)
				{
					if(err != null)
					{
						HammerAnimations.LOG.warn("{} died", this.getName(), err);
					} else
					{
						HammerAnimations.LOG.debug("{} shutdown", this.getName());
					}
					
					super.onTermination(err);
				}
			};
			worker.setName("Worker-" + serviceName + "-" + WORKER_COUNT.getAndIncrement());
			return worker;
		}, McUtil::onThreadException, true);
	}
	
	private static int getMaxThreads()
	{
		String s = System.getProperty("max.bg.threads");
		if(s != null)
		{
			try
			{
				int i = Integer.parseInt(s);
				if(i >= 1 && i <= 255)
				{
					return i;
				}
				
				HammerAnimations.LOG.error("Wrong {} property value '{}'. Should be an integer value between 1 and {}.", new Object[] { "max.bg.threads", s, 255 });
			} catch(NumberFormatException var2)
			{
				HammerAnimations.LOG.error("Could not parse {} property value '{}'. Should be an integer value between 1 and {}.", new Object[] { "max.bg.threads", s, 255 });
			}
		}
		
		return 255;
	}
	
	private static void onThreadException(Thread source, Throwable error)
	{
		if(error instanceof CompletionException) error = error.getCause();
		FMLCommonHandler.instance().raiseException(error, error.getMessage(), true);
		HammerAnimations.LOG.error(String.format(Locale.ROOT, "Caught exception in thread %s", source), error);
	}
}