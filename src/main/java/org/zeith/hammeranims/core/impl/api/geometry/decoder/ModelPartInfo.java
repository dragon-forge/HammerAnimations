package org.zeith.hammeranims.core.impl.api.geometry.decoder;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.*;
import org.joml.Vector3f;
import org.zeith.hammeranims.core.client.model.*;
import org.zeith.hammeranims.core.impl.api.geometry.PositionalModelImpl.PositionalBone;

import javax.annotation.Nullable;
import java.util.*;

public class ModelPartInfo
{
	private final List<ModelCubeInfo> cubes;
	private final List<ModelPartInfo> children = new ArrayList<>();
	private final Vector3f pivot;
	private final Vector3f rotationDegrees;
	private final boolean neverRender;
	private final String name;
	private final String parentName;
	
	public ModelPartInfo(List<ModelCubeInfo> cubes, Vector3f pivot, Vector3f rotationDegrees, boolean neverRender, String name, String parentName)
	{
		this.cubes = cubes;
		this.pivot = pivot;
		this.rotationDegrees = rotationDegrees;
		this.neverRender = neverRender;
		this.name = name;
		this.parentName = parentName;
	}
	
	public static ModelPartInfo makeRoot()
	{
		return new ModelPartInfo(ImmutableList.of(), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), false, "root", null);
	}
	
	public void addChild(ModelPartInfo child)
	{
		this.children.add(child);
	}
	
	public void addChildren(List<ModelPartInfo> children)
	{
		this.children.addAll(children);
	}
	
	public List<ModelPartInfo> getChildren()
	{
		return children;
	}
	
	public PositionalBone bakePositional(@Nullable ModelPartInfo parent)
	{
		Vector3f rotationRads = new Vector3f(
				Mth.DEG_TO_RAD * (rotationDegrees.x()),
				Mth.DEG_TO_RAD * (rotationDegrees.y()),
				Mth.DEG_TO_RAD * (rotationDegrees.z())
		);
		
		rotationRads.mul(-1, -1, 1);
		
		Object2ObjectArrayMap<String, PositionalBone> bakedChildren = new Object2ObjectArrayMap<>();
		for(ModelPartInfo child : children)
			bakedChildren.put(child.name, child.bakePositional(this));
		
		PositionalBone part = new PositionalBone(name, rotationRads, bakedChildren);
		if(parent != null)
			part.setPos(-(pivot.x() - parent.pivot.x()), (pivot.y() - parent.pivot.y()), pivot.z() - parent.pivot.z());
		else
			part.setPos(-pivot.x(), pivot.y(), pivot.z());
		
		return part;
	}
	
	@OnlyIn(Dist.CLIENT)
	public ModelBoneF bake(@Nullable ModelPartInfo parent, int textureWidth, int textureHeight)
	{
		ImmutableList.Builder<ModelCubeF> bakedCubes = ImmutableList.builder();
		
		for(ModelCubeInfo cube : cubes)
			bakedCubes.add(cube.bake(this, textureWidth, textureHeight));
		
		Vector3f rotationRads = new Vector3f(
				Mth.DEG_TO_RAD * (rotationDegrees.x()),
				Mth.DEG_TO_RAD * (rotationDegrees.y()),
				Mth.DEG_TO_RAD * (rotationDegrees.z())
		);
		
		rotationRads.mul(-1, -1, 1);
		
		Map<String, ModelBoneF> bakedChildren = new Object2ObjectArrayMap<>();
		for(ModelPartInfo child : children)
			bakedChildren.put(child.name, child.bake(this, textureWidth, textureHeight));
		
		ModelBoneF part = new ModelBoneF(name, rotationRads, bakedCubes.build(), bakedChildren, neverRender);
		if(parent != null)
			part.setPos(-(pivot.x() - parent.pivot.x()), (pivot.y() - parent.pivot.y()), pivot.z() - parent.pivot.z());
		else
			part.setPos(-pivot.x(), pivot.y(), pivot.z());
		
		return part;
	}
	
	public Vector3f getPivot()
	{
		return pivot;
	}
	
	public String getParentName()
	{
		return parentName;
	}
	
	public String getName()
	{
		return name;
	}
}