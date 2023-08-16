package org.zeith.hammeranims.core.impl.api.geometry.decoder;

import org.zeith.hammeranims.core.client.model.ModelCubeF;
import org.joml.Vector3f;

public class ModelCubeInfo
{
	private final Vector3f origin;
	private final Vector3f size;
	private final UVDefinition uv;
	private final float inflate;
	private final boolean mirrored;
	
	public ModelCubeInfo(Vector3f origin, Vector3f size, UVDefinition uv, float inflate, boolean mirrored)
	{
		this.origin = origin;
		this.size = size;
		this.uv = uv;
		this.inflate = inflate;
		this.mirrored = mirrored;
	}
	
	public ModelCubeF bake(ModelPartInfo ownerPart, int textureWidth, int textureHeight)
	{
		//The position of the cube, relative to the entity origin - located at the bottom front left point of the cube.
		Vector3f origin = new Vector3f(-(this.origin.x() + this.size.x() - ownerPart.getPivot().x()), (this.origin.y() -
				ownerPart.getPivot().y()), this.origin.z() - ownerPart.getPivot().z());
		
		float inflate = this.inflate;
		if(size.x() == 0 || size.y() == 0 || size.z() == 0)
		{
			inflate = Math.max(0.008F, inflate);
		}
		
		return ModelCubeF.make(origin, size, uv.bake(size), inflate, mirrored, textureWidth, textureHeight);
	}
}