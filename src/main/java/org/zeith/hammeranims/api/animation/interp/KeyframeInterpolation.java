package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.LzExpression;
import dev.zeith.lzvm.molang.compiler.libs.MoMathLibrary;
import dev.zeith.lzvm.op.LzVarOp;
import it.unimi.dsi.fastutil.doubles.*;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.animation.interp.keyframes.*;
import org.zeith.hammerlib.util.java.tuples.*;
import org.zeith.hammerlib.util.shaded.json.*;

import javax.annotation.Nullable;
import java.util.*;

public class KeyframeInterpolation
		extends BaseInterpolation
{
	public final int doubleCount;
	public final DoubleList keyframeTimes;
	public final List<IKeyFrame> keyframes;
	
	public KeyframeInterpolation(int doubleCount, DoubleList keyframeTimes, List<IKeyFrame> keyframes)
	{
		this.doubleCount = doubleCount;
		this.keyframeTimes = keyframeTimes;
		this.keyframes = keyframes;
	}
	
	public boolean validate(IAnimationData data)
	{
		double seconds = data.getLength().toMillis() / 1000D;
		
		double prev = Double.NEGATIVE_INFINITY;
		for(double ds : keyframeTimes)
		{
			if(ds > seconds || ds < 0)
				return false;
			if(prev > ds) // incremental-only!
				return false;
			prev = ds;
		}
		
		return true;
	}
	
	@Override
	public String toString()
	{
		return "KeyframeInterpolation" + keyframes;
	}
	
	@Override
	public int getDoubleCount()
	{
		return doubleCount;
	}
	
	@Override
	public LzExpression[] instantiate(LzVariableStore query)
	{
		LzExpression[] exprs = new LzExpression[doubleCount];
		
		List<IKeyFrameInstance> kfInst = keyframes.stream().map(f -> f.newInstance(query)).toList();
		
		for(int i = 0; i < exprs.length; i++)
			exprs[i] = new KeyframeInterpolationInstance(
					this,
					query,
					kfInst,
					i
			);
		
		return exprs;
	}
	
	public static class KeyframeInterpolationInstance
			implements LzExpression
	{
		protected final KeyframeInterpolation owner;
		protected final List<IKeyFrameInstance> keyframes;
		protected final LzVarOp anim_time, anim_duration;
		protected final int component;
		
		public KeyframeInterpolationInstance(KeyframeInterpolation owner, LzVariableStore store, List<IKeyFrameInstance> keyframes, int component)
		{
			this.owner = owner;
			this.keyframes = keyframes;
			this.anim_time = store.findVar("query.anim_time");
			this.anim_duration = store.findVar("query.anim_duration");
			this.component = component;
		}
		
		@Override
		public double get(double... args)
		{
			double anim_time = this.anim_time.get();
			
			DoubleList keyframeTimes = owner.keyframeTimes;
			int index = findInsertionIndex(keyframeTimes, anim_time);
			
			int fromIdx = index - 1;
			int toIdx = index % keyframes.size();
			if(fromIdx < 0) fromIdx += keyframes.size();
			
			IKeyFrameInstance prev = keyframes.get(fromIdx),
					next = toIdx > fromIdx ? keyframes.get(toIdx) : null;
			
			if(next == null)
				return prev.getVec(IKeyFrame.KeyFrameState.PREV).get(component);
			else if(prev == null)
				return next.getVec(IKeyFrame.KeyFrameState.NEXT).get(component);
			
			if(prev instanceof CatmullRomKeyFrame.CatmullRomKeyFrameInstance || next instanceof CatmullRomKeyFrame.CatmullRomKeyFrameInstance)
				return interpolateSmoothly(prev, next, fromIdx, toIdx, anim_time, this.anim_duration.get());
			else
				return interpolateLinear(prev, next, anim_time, component);
		}
		
		@Override
		public LzExpression instantiate(LzVariableStore store)
		{
			return owner.instantiate(store)[component];
		}
		
		private double interpolateSmoothly(IKeyFrameInstance prev, IKeyFrameInstance next, int prevIndex, int nextIndex, double anim_time, double anim_duration)
		{
			IKeyFrameInstance beforeMinus = null;
			if(prevIndex > 0) beforeMinus = keyframes.get(prevIndex - 1);
			
			IKeyFrameInstance afterPlus = null;
			if(nextIndex < keyframes.size() - 1) afterPlus = keyframes.get(nextIndex + 1);
			
			return catmullRom(beforeMinus, prev, next, afterPlus, anim_time, anim_duration, component);
		}
	}
	
	public static double interpolateLinear(IKeyFrameInstance prev, IKeyFrameInstance next, double anim_time, int component)
	{
		if(next == null) return prev.getVec(IKeyFrame.KeyFrameState.PREV).get(component);
		double duration = next.getTime() - prev.getTime();
		double iv = (anim_time - prev.getTime()) / duration;
		double a = prev.getVec(IKeyFrame.KeyFrameState.PREV).get(component);
		double b = next.getVec(IKeyFrame.KeyFrameState.NEXT).get(component);
		return MoMathLibrary.lerp(a, b, iv);
	}
	
	private static double catmullRom(@Nullable IKeyFrameInstance beforeMinus, @Nullable IKeyFrameInstance before, @Nullable IKeyFrameInstance after, @Nullable IKeyFrameInstance afterPlus, double anim_time, double anim_duration, int component)
	{
		double factor = percentage(anim_time,
				before != null
				? before.getTime()
				: 0,
				after != null
				? after.getTime()
				: anim_duration
		);
		
		return catmullRom(beforeMinus, before, after, afterPlus, factor, component);
	}
	
	private static double catmullRom(@Nullable IKeyFrameInstance beforeMinus, @Nullable IKeyFrameInstance before, @Nullable IKeyFrameInstance after, @Nullable IKeyFrameInstance afterPlus, double factor, int component)
	{
		int allocatedSize = countNonNls(beforeMinus, before, after, afterPlus);
		Vec3Animation[] points = new Vec3Animation[allocatedSize];
		
		int index = 0;
		if(beforeMinus != null) points[index++] = beforeMinus.getVec(IKeyFrame.KeyFrameState.PREV);
		if(before != null) points[index++] = before.getVec(IKeyFrame.KeyFrameState.PREV);
		if(after != null) points[index++] = after.getVec(IKeyFrame.KeyFrameState.NEXT);
		if(afterPlus != null) points[index] = afterPlus.getVec(IKeyFrame.KeyFrameState.NEXT);
		
		double time = (factor + (beforeMinus != null ? 1 : 0)) / (allocatedSize - 1);
		
		return catmullRom(time, points, component);
	}
	
	private static int countNonNls(@Nullable Object o1, @Nullable Object o2, @Nullable Object o3, @Nullable Object o4)
	{
		int count = 0;
		if(o1 != null) count++;
		if(o2 != null) count++;
		if(o3 != null) count++;
		if(o4 != null) count++;
		return count;
	}
	
	/**
	 * <a href="https://github.com/mrdoob/three.js/blob/e48fc94dfeaecfcbfa977ba67549e6108b370cbf/src/extras/curves/SplineCurve.js#L17">...</a>
	 */
	private static double catmullRom(double weightIn, Vec3Animation[] points, int component)
	{
		double p = (points.length - 1) * weightIn;
		int intPoint = (int) Math.floor(p);
		double weight = p - intPoint;
		double p0 = points[intPoint == 0 ? intPoint : intPoint - 1].get(component);
		double p1 = points[intPoint].get(component);
		double p2 = points[intPoint > points.length - 2 ? points.length - 1 : intPoint + 1].get(component);
		double p3 = points[intPoint > points.length - 3 ? points.length - 1 : intPoint + 2].get(component);
		return catmullRom(weight, p0, p1, p2, p3);
	}
	
	/**
	 * <a href="https://github.com/mrdoob/three.js/blob/e48fc94dfeaecfcbfa977ba67549e6108b370cbf/src/extras/core/Interpolations.js#L6">...</a>
	 */
	private static double catmullRom(double t, double p0, double p1, double p2, double p3)
	{
		double v0 = (p2 - p0) * 0.5F;
		double v1 = (p3 - p1) * 0.5F;
		double t2 = t * t;
		double t3 = t * t2;
		return (2 * p1 - 2 * p2 + v0 + v1) * t3 + (-3 * p1 + 3 * p2 - 2 * v0 - v1) * t2 + v0 * t + p1;
	}
	
	public static double percentage(double current, double start, double end)
	{
		return end - start != 0 ? (current - start) / (end - start) : 1;
	}
	
	public static KeyframeInterpolation parse(int doubleCount, JSONObject json)
	{
		DoubleList keyframeTimes = new DoubleArrayList(json.length());
		List<IKeyFrame> keyframes = new ArrayList<>(json.length());
		
		Iterator<Tuple2<String, Double>> itr = json
				.keySet()
				.stream()
				.map(str -> Tuples.immutable(str, Double.parseDouble(str)))
				.sorted(Comparator.comparingDouble(Tuple2::b))
				.iterator();
		
		while(itr.hasNext())
		{
			Tuple2<String, Double> s = itr.next();
			double time = s.b();
			
			Object o = json.get(s.a());
			IKeyFrame kf;
			
			if(o instanceof JSONObject)
			{
				JSONObject frame = (JSONObject) o;
				
				if(frame.has("pre"))
				{
					BaseInterpolation pre = BaseInterpolation.parse(frame.get("pre"));
					BaseInterpolation post = BaseInterpolation.parse(frame.get("post"));
					kf = new StepKeyFrame(time, pre, post);
				} else
				{
					String lerpMode = frame.optString("lerp_mode");
					
					BaseInterpolation vec = BaseInterpolation.parse(frame.opt("post"));
					
					if(lerpMode.equalsIgnoreCase("catmullrom"))
					{
						kf = new CatmullRomKeyFrame(time, vec);
					} else if(lerpMode.equalsIgnoreCase("linear"))
					{
						kf = new KeyFrame(time, vec);
					} else
					{
						throw new JSONException("Invalid lerp_mode found: " + lerpMode);
					}
				}
			} else if(o instanceof JSONArray)
			{
				BaseInterpolation a = BaseInterpolation.parse(o);
				if(a == null) return null;
				if(a.getDoubleCount() < doubleCount) return null;
				kf = (new KeyFrame(time, a));
			} else
				throw new JSONException("Invalid keyframe at " + s.a() + " found: " + o);
			
			// Add first frame if missing
			if(keyframes.isEmpty() && time > 0)
			{
				keyframes.add(kf.withNewTime(0));
				keyframeTimes.add(0);
			}
			
			keyframes.add(kf);
			keyframeTimes.add(time);
		}
		
		return new KeyframeInterpolation(doubleCount, keyframeTimes, keyframes);
	}
	
	private static int findInsertionIndex(DoubleList list, double x)
	{
		int low = 0;
		int high = list.size();
		
		while(low < high)
		{
			int mid = low + (high - low) / 2;
			if(list.getDouble(mid) < x) low = mid + 1;
			else high = mid;
		}
		
		return low;
	}
}