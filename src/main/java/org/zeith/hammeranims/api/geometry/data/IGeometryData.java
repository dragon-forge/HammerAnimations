package org.zeith.hammeranims.api.geometry.data;

import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.model.*;

import java.util.*;

public interface IGeometryData
{
	static IGeometryData EMPTY(IGeometryContainer container)
	{
		return new IGeometryData()
		{
			@Override
			public int getTextureWidth()
			{
				return 32;
			}
			
			@Override
			public int getTextureHeight()
			{
				return 32;
			}
			
			@Override
			public Set<String> getBones()
			{
				return Collections.emptySet();
			}
			
			@Override
			public IGeometricModel createModel()
			{
				return IGeometricModel.EMPTY;
			}
			
			@Override
			public IPositionalModel getPositionalModel()
			{
				return IPositionalModel.EMPTY;
			}
			
			@Override
			public IGeometryContainer getContainer()
			{
				return container;
			}
		};
	}
	
	int getTextureWidth();
	
	int getTextureHeight();
	
	Set<String> getBones();
	
	IGeometricModel createModel();
	
	IPositionalModel getPositionalModel();
	
	IGeometryContainer getContainer();
}