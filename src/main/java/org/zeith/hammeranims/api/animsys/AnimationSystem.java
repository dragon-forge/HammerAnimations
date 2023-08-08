package org.zeith.hammeranims.api.animsys;

public class AnimationSystem
{
	public final IAnimatedObject owner;
	
	public AnimationSystem(IAnimatedObject owner)
	{
		this.owner = owner;
	}
	
	public static Builder builder(IAnimatedObject owner)
	{
		return new Builder(owner);
	}
	
	public static class Builder
	{
		protected final IAnimatedObject owner;
		
		public Builder(IAnimatedObject owner)
		{
			this.owner = owner;
		}
		
		public AnimationSystem build()
		{
			AnimationSystem sys = new AnimationSystem(owner);
			
			
			
			return sys;
		}
	}
}