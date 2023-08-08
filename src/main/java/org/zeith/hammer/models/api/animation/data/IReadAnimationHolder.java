package org.zeith.hammer.models.api.animation.data;

import org.zeith.hammer.models.api.animation.*;

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
