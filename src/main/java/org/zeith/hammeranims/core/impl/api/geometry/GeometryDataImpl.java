package org.zeith.hammeranims.core.impl.api.geometry;

import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.data.IGeometryData;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.geometry.model.IPositionalModel;
import org.zeith.hammeranims.core.client.model.GeometricModelImpl;
import org.zeith.hammeranims.core.client.model.ModelBoneF;
import org.zeith.hammeranims.core.impl.api.geometry.decoder.*;

import java.util.*;

public class GeometryDataImpl
		implements IGeometryData
{
	protected final IGeometryContainer container;
	protected final ModelMeshInfo mesh;
	protected final ModelMaterialInfo material;
	protected final Map<String, ModelPartInfo> bones = new HashMap<>();
	
	protected final PositionalModelImpl positionalModel;
	
	private GeometryDataImpl(IGeometryContainer container, ModelMeshInfo mesh, ModelMaterialInfo material)
	{
		this.container = container;
		this.mesh = mesh;
		this.material = material;
		registerBone(mesh.getRoot());
		
		this.positionalModel = PositionalModelImpl.create(container, mesh);
	}
	
	protected void registerBone(ModelPartInfo part)
	{
		bones.put(part.getName(), part);
		part.getChildren().forEach(this::registerBone);
	}
	
	public ModelBoneF bakeRoot()
	{
		return this.mesh.getRoot().bake(null, material.getTextureWidth(), material.getTextureHeight());
	}
	
	public static GeometryDataImpl create(IGeometryContainer container, ModelMeshInfo mesh, ModelMaterialInfo material)
	{
		return new GeometryDataImpl(container, mesh, material);
	}
	
	@Override
	public int getTextureWidth()
	{
		return material.getTextureWidth();
	}
	
	@Override
	public int getTextureHeight()
	{
		return material.getTextureHeight();
	}
	
	@Override
	public Set<String> getBones()
	{
		return bones.keySet();
	}
	
	@Override
	public IGeometricModel createModel()
	{
		return new GeometricModelImpl(this);
	}
	
	@Override
	public IPositionalModel getPositionalModel()
	{
		positionalModel.resetPose();
		return positionalModel;
	}
	
	@Override
	public IGeometryContainer getContainer()
	{
		return container;
	}
	
	@Override
	public ModelMaterialInfo getMaterial()
	{
		return material;
	}
}