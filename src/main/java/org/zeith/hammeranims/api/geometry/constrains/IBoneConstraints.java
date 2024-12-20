package org.zeith.hammeranims.api.geometry.constrains;

public interface IBoneConstraints
{
	boolean hasTranslation();
	
	boolean hasRotation();
	
	boolean hasScale();
	
	///// TRANSLATION CONSTRAINTS /////
	
	double getMinTranslationX();
	
	double getMaxTranslationX();
	
	double getMinTranslationY();
	
	double getMaxTranslationY();
	
	double getMinTranslationZ();
	
	double getMaxTranslationZ();
	
	///// ROTATION CONSTRAINTS /////
	
	double getMinRotationX();
	
	double getMaxRotationX();
	
	double getMinRotationY();
	
	double getMaxRotationY();
	
	double getMinRotationZ();
	
	double getMaxRotationZ();
	
	///// SCALE CONSTRAINTS /////
	
	double getMinScaleX();
	
	double getMaxScaleX();
	
	double getMinScaleY();
	
	double getMaxScaleY();
	
	double getMinScaleZ();
	
	double getMaxScaleZ();
	
	IBoneConstraints NONE = new IBoneConstraints()
	{
		@Override
		public boolean hasTranslation()
		{
			return false;
		}
		
		@Override
		public boolean hasRotation()
		{
			return false;
		}
		
		@Override
		public boolean hasScale()
		{
			return false;
		}
		
		@Override
		public double getMinTranslationX()
		{
			return 0;
		}
		
		@Override
		public double getMaxTranslationX()
		{
			return 0;
		}
		
		@Override
		public double getMinTranslationY()
		{
			return 0;
		}
		
		@Override
		public double getMaxTranslationY()
		{
			return 0;
		}
		
		@Override
		public double getMinTranslationZ()
		{
			return 0;
		}
		
		@Override
		public double getMaxTranslationZ()
		{
			return 0;
		}
		
		@Override
		public double getMinRotationX()
		{
			return 0;
		}
		
		@Override
		public double getMaxRotationX()
		{
			return 0;
		}
		
		@Override
		public double getMinRotationY()
		{
			return 0;
		}
		
		@Override
		public double getMaxRotationY()
		{
			return 0;
		}
		
		@Override
		public double getMinRotationZ()
		{
			return 0;
		}
		
		@Override
		public double getMaxRotationZ()
		{
			return 0;
		}
		
		@Override
		public double getMinScaleX()
		{
			return 0;
		}
		
		@Override
		public double getMaxScaleX()
		{
			return 0;
		}
		
		@Override
		public double getMinScaleY()
		{
			return 0;
		}
		
		@Override
		public double getMaxScaleY()
		{
			return 0;
		}
		
		@Override
		public double getMinScaleZ()
		{
			return 0;
		}
		
		@Override
		public double getMaxScaleZ()
		{
			return 0;
		}
	};
}