package org.zeith.hammeranims.api.animation.interp;

import it.unimi.dsi.fastutil.doubles.*;
import net.minecraft.util.Mth;
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
	public double[] get(Query query)
	{
		int index = findInsertionIndex(keyframeTimes, query.anim_time);
		
		int fromIdx = index - 1;
		int toIdx = index % keyframes.size();
		if(fromIdx < 0) fromIdx += keyframes.size();
		
		IKeyFrame prev = keyframes.get(fromIdx),
				next = toIdx > fromIdx ? keyframes.get(toIdx) : null;
		
		if(next == null)
			return prev.getVec(IKeyFrame.KeyFrameState.PREV).get(query);
		else if(prev == null)
			return next.getVec(IKeyFrame.KeyFrameState.NEXT).get(query);
		
		if(prev instanceof CatmullRomKeyFrame || next instanceof CatmullRomKeyFrame)
			return interpolateSmoothly(prev, next, fromIdx, toIdx, query);
		else
			return interpolateLinear(prev, next, query);
	}
	
	public static double[] interpolateLinear(IKeyFrame prev, IKeyFrame next, Query query)
	{
		if(next == null) return prev.getVec(IKeyFrame.KeyFrameState.PREV).get(query);
		double duration = next.getTime() - prev.getTime();
		double iv = (query.anim_time - prev.getTime()) / duration;
		double[] a = prev.getVec(IKeyFrame.KeyFrameState.PREV).get(query);
		double[] b = next.getVec(IKeyFrame.KeyFrameState.NEXT).get(query);
		double[] res = new double[Math.min(a.length, b.length)];
		for(int i = 0; i < res.length; i++)
			res[i] = Mth.lerp(iv, a[i], b[i]);
		return res;
	}
	
	private double[] interpolateSmoothly(IKeyFrame prev, IKeyFrame next, int prevIndex, int nextIndex, Query query)
	{
		IKeyFrame beforeMinus = null;
		if(prevIndex > 0) beforeMinus = keyframes.get(prevIndex - 1);
		
		IKeyFrame afterPlus = null;
		if(nextIndex < keyframes.size() - 1) afterPlus = keyframes.get(nextIndex + 1);
		
		return catmullRom(beforeMinus, prev, next, afterPlus, query);
	}
	
	private static double[] catmullRom(@Nullable IKeyFrame beforeMinus, @Nullable IKeyFrame before, @Nullable IKeyFrame after, @Nullable IKeyFrame afterPlus, Query query)
	{
		double factor = percentage(query.anim_time,
				before != null
				? before.getTime()
				: 0,
				after != null
				? after.getTime()
				: query.anim_duration
		);
		
		return catmullRom(beforeMinus, before, after, afterPlus, factor, query);
	}
	
	private static double[] catmullRom(@Nullable IKeyFrame beforeMinus, @Nullable IKeyFrame before, @Nullable IKeyFrame after, @Nullable IKeyFrame afterPlus, double factor, Query query)
	{
		int allocatedSize = countNonNls(beforeMinus, before, after, afterPlus);
		BaseInterpolation[] points = new BaseInterpolation[allocatedSize];
		
		int index = 0;
		if(beforeMinus != null) points[index++] = beforeMinus.getVec(IKeyFrame.KeyFrameState.PREV);
		if(before != null) points[index++] = before.getVec(IKeyFrame.KeyFrameState.PREV);
		if(after != null) points[index++] = after.getVec(IKeyFrame.KeyFrameState.NEXT);
		if(afterPlus != null) points[index] = afterPlus.getVec(IKeyFrame.KeyFrameState.NEXT);
		
		double time = (factor + (beforeMinus != null ? 1 : 0)) / (allocatedSize - 1);
		
		return catmullRom(time, points, query);
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
	private static double[] catmullRom(double weightIn, BaseInterpolation[] points, Query query)
	{
		double p = (points.length - 1) * weightIn;
		int intPoint = (int) Math.floor(p);
		
		double weight = p - intPoint;
		
		double[] p0 = points[intPoint == 0 ? intPoint : intPoint - 1].get(query);
		double[] p1 = points[intPoint].get(query);
		double[] p2 = points[intPoint > points.length - 2 ? points.length - 1 : intPoint + 1].get(query);
		double[] p3 = points[intPoint > points.length - 3 ? points.length - 1 : intPoint + 2].get(query);
		double[] iv = new double[p0.length];
		for(int i = 0; i < iv.length; i++)
			iv[i] = catmullRom(weight, p0[i], p1[i], p2[i], p3[i]);
		return iv;
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
		
		Iterator<Tuple2<String, Double>> itr = json.keySet()
				.stream()
				.map(str -> Tuples.immutable(str, Double.parseDouble(str)))
				.sorted(Comparator.comparingDouble(Tuple2::b))
				.iterator();
		
		while(itr.hasNext())
		{
			Tuple2<String, Double> s = itr.next();
			double time = s.b();
			
			Object o = json.get(s.a());
			
			if(o instanceof JSONObject)
			{
				JSONObject frame = (JSONObject) o;
				
				if(frame.has("pre"))
				{
					BaseInterpolation pre = BaseInterpolation.parse(frame.get("pre"));
					BaseInterpolation post = BaseInterpolation.parse(frame.get("post"));
					keyframes.add(new StepKeyFrame(time, pre, post));
					keyframeTimes.add(time);
					continue;
				}
				
				String lerpMode = frame.optString("lerp_mode");
				
				BaseInterpolation vec = BaseInterpolation.parse(frame.opt("post"));
				
				if(lerpMode.equalsIgnoreCase("catmullrom"))
				{
					keyframes.add(new CatmullRomKeyFrame(time, vec));
					keyframeTimes.add(time);
				} else if(lerpMode.equalsIgnoreCase("linear"))
				{
					keyframes.add(new KeyFrame(time, vec));
					keyframeTimes.add(time);
				} else
				{
					throw new JSONException("Invalid lerp_mode found: " + lerpMode);
				}
			} else if(o instanceof JSONArray)
			{
				BaseInterpolation a = BaseInterpolation.parse(o);
				if(a == null) return null;
				if(a.getDoubleCount() < doubleCount) return null;
				keyframes.add(new KeyFrame(time, a));
				keyframeTimes.add(time);
			} else
				throw new JSONException("Invalid keyframe at " + s.a() + " found: " + o);
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