package org.zeith.hammeranims.core.impl.api.geometry.decoder;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.zeith.hammeranims.core.client.model.*;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryLocator;
import org.zeith.hammeranims.core.impl.api.geometry.PositionalModelImpl.PositionalBone;
import org.zeith.hammeranims.standalone.utils.MathHelper;

import java.util.*;

public class ModelPartInfo
{
	private final List<ModelCubeInfo> cubes;
	private final @Getter List<ModelLocatorInfo> locators;
	private final @Getter List<ModelPartInfo> children = new ArrayList<>();
	private final @Getter Vector3f pivot;
	private final Vector3f rotationDegrees;
	private final boolean neverRender;
	private final @Getter String name;
	private final @Getter String parentName;
	
	public ModelPartInfo(List<ModelCubeInfo> cubes, List<ModelLocatorInfo> locators, Vector3f pivot, Vector3f rotationDegrees, boolean neverRender, String name, String parentName)
	{
		this.cubes = cubes;
		this.locators = locators;
		this.pivot = pivot;
		this.rotationDegrees = rotationDegrees;
		this.neverRender = neverRender;
		this.name = name;
		this.parentName = parentName;
	}
	
	public static ModelPartInfo makeRoot()
	{
		return new ModelPartInfo(List.of(), List.of(), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), false, "root", null);
	}
	
	public void addChild(ModelPartInfo child)
	{
		this.children.add(child);
	}
	
	public void addChildren(List<ModelPartInfo> children)
	{
		this.children.addAll(children);
	}
	
	public PositionalBone bakePositional(@Nullable ModelPartInfo parent)
	{
		Vector3f rotationRads = new Vector3f(
				MathHelper.TO_RAD_F * (rotationDegrees.x()),
				MathHelper.TO_RAD_F * (rotationDegrees.y()),
				MathHelper.TO_RAD_F * (rotationDegrees.z())
		);
		
		rotationRads.mul(-1, -1, 1);
		
		Map<String, PositionalBone> bakedChildren = new HashMap<>();
		for(ModelPartInfo child : children)
			bakedChildren.put(child.name, child.bakePositional(this));
		
		Map<String, GeometryLocator> bakedLocators = new HashMap<>();
		for(ModelLocatorInfo locator : locators)
		{
			val off = locator.getOrigin();
			val rot = locator.getRotation();
			val b = GeometryLocator.builder().name(locator.getName());
			if(parent != null) b.offset(new Vector3f(-(off.x() - parent.pivot.x()), (off.y() - parent.pivot.y()), off.z() - parent.pivot.z()));
			else b.offset(new Vector3f(-off.x(), off.y(), off.z()));
			b.rotation(new Vector3f(
					MathHelper.TO_RAD_F * (rot.x()),
					MathHelper.TO_RAD_F * (rot.y()),
					MathHelper.TO_RAD_F * (rot.z())
			).mul(-1, -1, 1));
			bakedLocators.put(locator.getName(), b.build());
		}
		
		PositionalBone part = new PositionalBone(name, rotationRads, bakedChildren, bakedLocators);
		if(parent != null)
			part.setPos(-(pivot.x() - parent.pivot.x()), (pivot.y() - parent.pivot.y()), pivot.z() - parent.pivot.z());
		else
			part.setPos(-pivot.x(), pivot.y(), pivot.z());
		
		return part;
	}
	
	public ModelBoneF bake(@Nullable ModelPartInfo parent, int textureWidth, int textureHeight)
	{
		List<ModelCubeF> bakedCubes = new ArrayList<>();
		
		for(ModelCubeInfo cube : cubes)
			bakedCubes.add(cube.bake(this, textureWidth, textureHeight));
		
		Vector3f rotationRads = new Vector3f(
				MathHelper.TO_RAD_F * (rotationDegrees.x()),
				MathHelper.TO_RAD_F * (rotationDegrees.y()),
				MathHelper.TO_RAD_F * (rotationDegrees.z())
		);
		
		rotationRads.mul(-1, -1, 1);
		
		Map<String, ModelBoneF> bakedChildren = new HashMap<>();
		for(ModelPartInfo child : children)
			bakedChildren.put(child.name, child.bake(this, textureWidth, textureHeight));
		
		Map<String, GeometryLocator> bakedLocators = new HashMap<>();
		for(ModelLocatorInfo locator : locators)
		{
			val off = locator.getOrigin();
			val rot = locator.getRotation();
			val b = GeometryLocator.builder().name(locator.getName());
			if(parent != null) b.offset(new Vector3f(-(off.x() - parent.pivot.x()), (off.y() - parent.pivot.y()), off.z() - parent.pivot.z()));
			else b.offset(new Vector3f(-off.x(), off.y(), off.z()));
			b.rotation(new Vector3f(
					MathHelper.TO_RAD_F * (rot.x()),
					MathHelper.TO_RAD_F * (rot.y()),
					MathHelper.TO_RAD_F * (rot.z())
			).mul(-1, -1, 1));
			bakedLocators.put(locator.getName(), b.build());
		}
		
		ModelBoneF part = new ModelBoneF(name, textureWidth, textureHeight, rotationRads, List.copyOf(bakedCubes), bakedChildren, bakedLocators, neverRender);
		if(parent != null)
		{
			part.setPos(-(pivot.x() - parent.pivot.x()), (pivot.y() - parent.pivot.y()), pivot.z() - parent.pivot.z());
		} else
		{
			part.setPos(-pivot.x(), pivot.y(), pivot.z());
		}
		
		return part;
	}
}