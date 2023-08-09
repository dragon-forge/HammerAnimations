package org.zeith.hammeranims.api.animation.interp;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.doubles.*;
import net.minecraft.util.Mth;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammerlib.util.java.tuples.*;
import org.zeith.hammerlib.util.shaded.json.JSONObject;

import java.util.*;

public class KeyframeInterpolation
		extends BaseInterpolation
{
	public final int doubleCount;
	public final DoubleList keyframeTimes;
	public final List<Keyframe> keyframes;
	
	public KeyframeInterpolation(int doubleCount, DoubleList keyframeTimes, List<Keyframe> keyframes)
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
		
		Keyframe from = keyframes.get(fromIdx), to = keyframes.get(toIdx);
		
		return from.lerpMode.lerp(from, to, query);
	}
	
	public static KeyframeInterpolation parse(int doubleCount, JSONObject json)
	{
		DoubleList keyframeTimes = new DoubleArrayList(json.length());
		List<Keyframe> keyframes = new ArrayList<>(json.length());
		
		Iterator<Tuple2<String, Double>> itr = json.keySet()
				.stream()
				.map(str -> Tuples.immutable(str, Double.parseDouble(str)))
				.sorted(Comparator.comparingDouble(Tuple2::b))
				.iterator();
		
		while(itr.hasNext())
		{
			Tuple2<String, Double> s = itr.next();
			
			Object o = json.get(s.a());
			
			Keyframe frame;
			if(o instanceof JSONObject)
			{
				JSONObject j = (JSONObject) o;
				LerpMode mode = LerpMode.get(j.optString("lerp_mode"));
				
				Object preObj = j.opt("pre");
				Object postObj = j.opt("post");
				
				BaseInterpolation pre = BaseInterpolation.parse(preObj != null ? preObj : postObj);
				BaseInterpolation post = BaseInterpolation.parse(postObj != null ? postObj : preObj);
				if(pre == null || post == null) return null;
				
				if(pre.getDoubleCount() < doubleCount || post.getDoubleCount() < doubleCount) return null;
				frame = new Keyframe(pre, post, mode, s.b());
			} else
			{
				BaseInterpolation a = BaseInterpolation.parse(o);
				if(a == null) return null;
				if(a.getDoubleCount() < doubleCount) return null;
				frame = new Keyframe(a, s.b());
			}
			
			keyframeTimes.add(s.b());
			keyframes.add(frame);
		}
		
		return new KeyframeInterpolation(doubleCount, keyframeTimes, keyframes);
	}
	
	public static class Keyframe
	{
		public final BaseInterpolation pre, post;
		public final LerpMode lerpMode;
		public final double time;
		
		public Keyframe(BaseInterpolation pre, BaseInterpolation post, LerpMode lerpMode, double time)
		{
			this.pre = pre;
			this.post = post;
			this.lerpMode = lerpMode;
			this.time = time;
		}
		
		public Keyframe(BaseInterpolation anim, double time)
		{
			this.pre = this.post = anim;
			this.lerpMode = LerpMode.LINEAR;
			this.time = time;
		}
		
		@Override
		public String toString()
		{
			return "Keyframe{" +
					"pre=" + pre +
					", post=" + post +
					", lerp_mode=" + lerpMode +
					", time=" + time +
					'}';
		}
	}
	
	public enum LerpMode
	{
		LINEAR,
		CALMULLROM
				{
					double catmullRomInterpolation(double p0, double p1, double p2, double p3, double t)
					{
						double t2 = t * t;
						double t3 = t2 * t;
						
						double b1 = 0.5 * (-t3 + 2 * t2 - t);
						double b2 = 0.5 * (3 * t3 - 5 * t2 + 2);
						double b3 = 0.5 * (-3 * t3 + 4 * t2 + t);
						double b4 = 0.5 * (t3 - t2);
						
						return b1 * p0 + b2 * p1 + b3 * p2 + b4 * p3;
					}
					
					@Override
					public double[] lerp(Keyframe from, Keyframe to, Query query)
					{
						double duration = to.time - from.time;
						double iv = (query.anim_time - from.time) / duration;
						double[] a = from.pre.get(query);
						double[] b = from.post.get(query);
						double[] c = to.pre.get(query);
						double[] d = to.post.get(query);
						double[] res = new double[Math.min(b.length, c.length)];
						for(int i = 0; i < res.length; i++)
							res[i] = catmullRomInterpolation(a[i], b[i], c[i], d[i], iv);
						return res;
					}
				};
		
		private static final Map<String, LerpMode> MODES = Suppliers.memoize(() ->
		{
			Map<String, LerpMode> lms = new HashMap<>();
			lms.put("linear", LINEAR);
			lms.put("catmullrom", CALMULLROM);
			return lms;
		}).get();
		
		public static LerpMode get(String lerpMode)
		{
			return MODES.getOrDefault(lerpMode, LINEAR);
		}
		
		public double[] lerp(Keyframe from, Keyframe to, Query query)
		{
			double duration = to.time - from.time;
			double iv = (query.anim_time - from.time) / duration;
			double[] a = from.post.get(query);
			double[] b = to.pre.get(query);
			double[] res = new double[Math.min(a.length, b.length)];
			for(int i = 0; i < res.length; i++)
				res[i] = Mth.lerp(iv, a[i], b[i]);
			return res;
		}
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