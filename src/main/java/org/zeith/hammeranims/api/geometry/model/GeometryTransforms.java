package org.zeith.hammeranims.api.geometry.model;

import lombok.*;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.*;
import org.teavm.jso.impl.JS;
import org.zeith.hammeranims.api.geometry.constrains.IBoneConstraints;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;
import org.zeith.hammeranims.standalone.mc.math.Vec3d;

import static org.zeith.hammeranims.standalone.mc.math.Vec3d.ZERO;

@With
@AllArgsConstructor
public class GeometryTransforms
	implements Cloneable
{
	public static final Vec3d ONE = new Vec3d(1, 1, 1);
	
	public Vec3d translation;
	public Vec3d rotation; // (in degrees)
	public Vec3d scale;
	public boolean skipGeometry;
	public VertexType forceVertexType;
	
	public GeometryTransforms(Vec3d translation, Vec3d rotation, Vec3d scale)
	{
		this.translation = translation;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public static GeometryTransforms createDefault()
	{
		return new GeometryTransforms(ZERO, ZERO, ONE);
	}
	
	public void applyConstraints(IBoneConstraints constraints)
	{
		if(constraints.hasTranslation())
		{
			double x = Math.max(constraints.getMinTranslationX(), Math.min(constraints.getMaxTranslationX(), translation.x));
			double y = Math.max(constraints.getMinTranslationY(), Math.min(constraints.getMaxTranslationY(), translation.y));
			double z = Math.max(constraints.getMinTranslationZ(), Math.min(constraints.getMaxTranslationZ(), translation.z));
			translation = new Vec3d(x, y, z);
		}
		
		if(constraints.hasRotation())
		{
			double x = Math.max(constraints.getMinRotationX(), Math.min(constraints.getMaxRotationX(), rotation.x));
			double y = Math.max(constraints.getMinRotationY(), Math.min(constraints.getMaxRotationY(), rotation.y));
			double z = Math.max(constraints.getMinRotationZ(), Math.min(constraints.getMaxRotationZ(), rotation.z));
			rotation = new Vec3d(x, y, z);
		}
		
		if(constraints.hasScale())
		{
			double x = Math.max(constraints.getMinScaleX(), Math.min(constraints.getMaxScaleX(), scale.x));
			double y = Math.max(constraints.getMinScaleY(), Math.min(constraints.getMaxScaleY(), scale.y));
			double z = Math.max(constraints.getMinScaleZ(), Math.min(constraints.getMaxScaleZ(), scale.z));
			scale = new Vec3d(x, y, z);
		}
		
		// If the scale is zero or so, we don't need to render geometry
		skipGeometry = scale.length() < 1.0E-10;
	}
	
	public GeometryTransforms copy()
	{
		return withSkipGeometry(skipGeometry);
	}
	
	@Override
	protected GeometryTransforms clone()
	{
		return copy();
	}
	
	public JSObject toJson()
	{
		JSObject obj = JSObjects.create();
		JS.set(obj, JSString.valueOf("translation"), translation.toJson());
		JS.set(obj, JSString.valueOf("rotation"), rotation.toJson());
		JS.set(obj, JSString.valueOf("scale"), scale.toJson());
		return obj;
	}
}