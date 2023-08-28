package org.zeith.hammeranims.core.impl.api.geometry;

import com.zeitheron.hammercore.utils.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zeith.hammeranims.api.geometry.constrains.*;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.impl.api.geometry.decoder.ModelMeshInfo;
import org.zeith.hammeranims.joml.*;

import javax.annotation.*;
import java.util.*;

public class PositionalModelImpl
		implements IPositionalModel
{
	protected final Map<String, List<PositionalBone>> parentTree = new HashMap<>();
	protected final IGeometryContainer container;
	protected final IGeometryConstraints constraints;
	protected final PositionalBone root;
	protected final Map<String, PositionalBone> bones = new HashMap<>();
	protected final Map<String, IBoneConstraints> boneConstraints = new HashMap<>();
	
	private PositionalModelImpl(IGeometryContainer container, ModelMeshInfo mesh)
	{
		this.container = container;
		this.constraints = container.getConstraints();
		this.root = mesh.getRoot().bakePositional(null);
		registerBone(new Stack<>(), this.root);
	}
	
	protected void registerBone(Stack<PositionalBone> boneStack, PositionalBone part)
	{
		boneStack.push(part);
		bones.put(part.boxName, part);
		boneConstraints.put(part.boxName, constraints.getConstraints(part.boxName));
		parentTree.put(part.boxName, new ArrayList<>(boneStack)); // all parent bones will be a part of this
		for(PositionalBone child : part.getChildren().values())
			registerBone(boneStack, child);
		boneStack.pop();
	}
	
	public static PositionalModelImpl create(IGeometryContainer container, ModelMeshInfo mesh)
	{
		return new PositionalModelImpl(container, mesh);
	}
	
	@Override
	public IBone getRoot()
	{
		return root;
	}
	
	@Override
	public Set<String> getBoneNames()
	{
		return bones.keySet();
	}
	
	@Override
	public Collection<? extends IBone> getBones()
	{
		return bones.values();
	}
	
	@Nullable
	@Override
	public IBone getBone(String bone)
	{
		return bones.get(bone);
	}
	
	@Override
	public boolean hasBone(String bone)
	{
		return bones.containsKey(bone);
	}
	
	@Override
	public void resetPose()
	{
		for(PositionalBone s : bones.values())
			s.reset();
	}
	
	GeometryPose emptyPose = new GeometryPose(this::hasBone);
	
	@Override
	public GeometryPose emptyPose()
	{
		emptyPose.reset();
		return emptyPose;
	}
	
	@Override
	public void applyPose(GeometryPose pose)
	{
		Map<String, GeometryTransforms> poseBones = pose.getBoneTransforms();
		
		for(String boneKey : bones.keySet())
		{
			PositionalBone bone = bones.get(boneKey);
			if(bone == null) continue;
			bone.reset();
			
			GeometryTransforms add = poseBones.get(boneKey);
			if(add == null) continue;
			add.applyConstraints(boneConstraints.get(boneKey));
			
			Vec3d translate = add.translation,
					rotate = add.rotation.scale(MathHelper.torad),
					scale = add.scale;
			
			bone.offset.add(
					(float) translate.x,
					(float) -translate.y,
					(float) translate.z
			);
			
			bone.getRotation().sub(
					(float) rotate.x,
					(float) rotate.y,
					(float) -rotate.z
			);
			
			bone.getScale().mul(
					(float) scale.x,
					(float) scale.y,
					(float) scale.z
			);
		}
	}
	
	@Override
	public boolean applyBoneTransforms(@Nonnull Matrix4f base, String bone)
	{
		List<PositionalBone> tree = parentTree.get(bone);
		if(tree == null || tree.isEmpty()) return false;
		
		for(PositionalBone f : tree)
		{
			f.applyTransforms(base);
		}
		
		return true;
	}
	
	public static class PositionalBone
			implements IBone
	{
		public final String boxName;
		
		private final Vector3f scale = new Vector3f(1, 1, 1);
		public Vector3f offset = new Vector3f();
		private final Vector3f startRotationRadians;
		private final Vector3f rotation;
		private final Map<String, PositionalBone> children;
		
		public float offsetX;
		public float offsetY;
		public float offsetZ;
		
		public PositionalBone(String name, Vector3f startRotRadians, Map<String, PositionalBone> children)
		{
			this.boxName = name;
			this.startRotationRadians = startRotRadians;
			this.rotation = new Vector3f(startRotRadians);
			this.children = Collections.unmodifiableMap(children);
		}
		
		@Override
		public String getName()
		{
			return boxName;
		}
		
		@Override
		public Vector3f getTranslation()
		{
			return offset;
		}
		
		@Override
		public Vector3f getRotation()
		{
			return rotation;
		}
		
		@Override
		public Vector3f getScale()
		{
			return scale;
		}
		
		public void setPos(float x, float y, float z)
		{
			offsetX = x;
			offsetY = y;
			offsetZ = z;
		}
		
		@Override
		public Map<String, PositionalBone> getChildren()
		{
			return children;
		}
		
		@Override
		public void reset()
		{
			rotation.set(startRotationRadians.x, startRotationRadians.y, startRotationRadians.z);
			offset.set(0, 0, 0);
			scale.set(1, 1, 1);
		}
		
		public void applyTransforms(Matrix4f pose)
		{
			pose.translate(-offset.x() / 16F, -offset.y() / 16F, offset.z() / 16F);
			pose.translate(this.offsetX / 16.0F, this.offsetY / 16.0F, this.offsetZ / 16.0F);
			
			if(this.rotation.x() != 0.0F || this.rotation.y() != 0.0F || this.rotation.z() != 0.0F)
				pose.rotate(new Quaternionf().rotateZYX(rotation.z(), rotation.y(), rotation.x()));
			
			if(this.scale.x() != 1.0F || this.scale.y() != 1.0F || this.scale.z() != 1.0F)
				pose.scale(scale.x(), scale.y(), scale.z());
		}
	}
}