package org.zeith.hammeranims.api.animation.data;

import org.zeith.hammeranims.api.animation.Animation;

import javax.annotation.Nullable;
import java.util.*;

public interface IReadAnimationHolder
		extends IAnimationHolder
{
	IReadAnimationHolder EMPTY = new IReadAnimationHolder()
	{
		@Override
		public Set<String> getKeySet()
		{
			return Collections.emptySet();
		}
		
		@Nullable
		@Override
		public Animation get(String key)
		{
			return null;
		}
		
		@Override
		public Set<Map.Entry<String, Animation>> entrySet()
		{
			return Collections.emptySet();
		}
		
		@Override
		public Collection<Animation> values()
		{
			return Collections.emptySet();
		}
	};
}
