package org.zeith.hammeranims.core.utils;

import lombok.Getter;
import org.joml.Vector3i;

import java.util.*;
import java.util.function.Predicate;

public enum EnumFacing
{
	DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new Vector3i(0, -1, 0)),
	UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new Vector3i(0, 1, 0)),
	NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new Vector3i(0, 0, -1)),
	SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new Vector3i(0, 0, 1)),
	WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new Vector3i(-1, 0, 0)),
	EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new Vector3i(1, 0, 0));
	
	public final int index, opposite, horizontalIndex;
	@Getter
	public final String name;
	public final AxisDirection axisDirection;
	public final Axis axis;
	public final Vector3i offset;
	
	EnumFacing(int index, int opposite, int horizontalIndex, String name, AxisDirection axisDirection, Axis axis, Vector3i offset)
	{
		this.index = index;
		this.opposite = opposite;
		this.horizontalIndex = horizontalIndex;
		this.name = name;
		this.axisDirection = axisDirection;
		this.axis = axis;
		this.offset = offset;
	}
	
	public enum Axis
			implements Predicate<EnumFacing>
	{
		X("x", Plane.HORIZONTAL),
		Y("y", Plane.VERTICAL),
		Z("z", Plane.HORIZONTAL);
		
		private static final Map<String, Axis> NAME_LOOKUP = new HashMap<>();
		private final String name;
		private final Plane plane;
		
		Axis(String name, Plane plane)
		{
			this.name = name;
			this.plane = plane;
		}
		
		/**
		 * Get the axis specified by the given name
		 */
		public static EnumFacing.Axis byName(String name)
		{
			return name == null ? null
								: NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
		}
		
		/**
		 * Like getName but doesn't override the method from Enum.
		 */
		public String getName2()
		{
			return this.name;
		}
		
		/**
		 * If this Axis is on the vertical plane (Only true for Y)
		 */
		public boolean isVertical()
		{
			return this.plane == Plane.VERTICAL;
		}
		
		/**
		 * If this Axis is on the horizontal plane (true for X and Z)
		 */
		public boolean isHorizontal()
		{
			return this.plane == Plane.HORIZONTAL;
		}
		
		public String toString()
		{
			return this.name;
		}
		
		@Override
		public boolean test(EnumFacing face)
		{
			return face != null && face.axis == this;
		}
		
		/**
		 * Get this Axis' Plane (VERTICAL for Y, HORIZONTAL for X and Z)
		 */
		public Plane getPlane()
		{
			return this.plane;
		}
		
		static
		{
			for(Axis ax : values())
			{
				NAME_LOOKUP.put(ax.getName2().toLowerCase(Locale.ROOT), ax);
			}
		}
	}
	
	public enum AxisDirection
	{
		POSITIVE(1, "Towards positive"),
		NEGATIVE(-1, "Towards negative");
		
		private final int offset;
		private final String description;
		
		AxisDirection(int offset, String description)
		{
			this.offset = offset;
			this.description = description;
		}
		
		/**
		 * Get the offset for this AxisDirection. 1 for POSITIVE, -1 for NEGATIVE
		 */
		public int getOffset()
		{
			return this.offset;
		}
		
		public String toString()
		{
			return this.description;
		}
	}
	
	public enum Plane
			implements Predicate<EnumFacing>, Iterable<EnumFacing>
	{
		HORIZONTAL,
		VERTICAL;
		
		/**
		 * All EnumFacing values for this Plane
		 */
		public EnumFacing[] facings()
		{
			return switch(this)
			{
				case HORIZONTAL -> new EnumFacing[] {
						EnumFacing.NORTH, EnumFacing.EAST,
						EnumFacing.SOUTH, EnumFacing.WEST
				};
				case VERTICAL -> new EnumFacing[] {
						EnumFacing.UP, EnumFacing.DOWN
				};
				default -> throw new Error("Someone's been tampering with the universe!");
			};
		}
		
		/**
		 * Choose a random Facing from this Plane using the given Random
		 */
		public EnumFacing random(Random rand)
		{
			EnumFacing[] aenumfacing = this.facings();
			return aenumfacing[rand.nextInt(aenumfacing.length)];
		}
		
		@Override
		public boolean test(EnumFacing face)
		{
			return face != null && face.axis.getPlane() == this;
		}
		
		@Override
		public Iterator<EnumFacing> iterator()
		{
			return Arrays.asList(this.facings()).iterator();
		}
	}
}
