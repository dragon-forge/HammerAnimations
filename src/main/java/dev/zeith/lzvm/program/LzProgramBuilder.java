package dev.zeith.lzvm.program;

import dev.zeith.lzvm.op.LzOpcodes;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.*;

@RequiredArgsConstructor
public class LzProgramBuilder
{
	protected final List<Integer> insnList = new ArrayList<>();
	
	protected final BiMap<Double, Integer> dConstants = new BiMap<>();
	protected final BiMap<String, Integer> sConstants = new BiMap<>();
	protected final BiMap<LzCallInsn, Integer> callTable = new BiMap<>();
	
	protected final BiMap<LzLabel, Integer> labelTable = new BiMap<>(IdentityHashMap::new);
	protected final Map<Integer, LzLabel> jumps = new HashMap<>();
	
	protected final Set<Integer> usedLocals = new HashSet<>();
	
	protected final int argCount;
	
	public static LzProgramBuilder of(int argCount)
	{
		return new LzProgramBuilder(argCount);
	}
	
	public LzProgramBuilder addInsn(int insn)
	{
		insnList.add(insn);
		return this;
	}
	
	public LzProgramBuilder addConstD(double constant)
	{
		return addInsn(LzOpcodes.CONST)
				.putInsnDConstIdx(constant);
	}
	
	public LzProgramBuilder addConstS(String constant)
	{
		return addInsn(LzOpcodes.SCONST)
				.putInsnSConstIdx(constant);
	}
	
	public LzProgramBuilder addLoad(int local)
	{
		return addInsn(LzOpcodes.LOAD)
				.addInsn(local);
	}
	
	public LzProgramBuilder addStore(int local)
	{
		return addInsn(LzOpcodes.STORE)
				.addInsn(local);
	}
	
	public LzProgramBuilder addJCall(String owner, LzCallInsn call)
	{
		return addInsn(LzOpcodes.JCALL)
				.putInsnSConstIdx(owner)
				.putInsnCallIdx(call);
	}
	
	public LzProgramBuilder addCall(LzCallInsn call)
	{
		return addInsn(LzOpcodes.CALL)
				.putInsnCallIdx(call);
	}
	
	public LzProgramBuilder addRead(String varName)
	{
		return addInsn(LzOpcodes.READ)
				.putInsnSConstIdx(varName);
	}
	
	public LzProgramBuilder addWrite(String varName)
	{
		return addInsn(LzOpcodes.WRITE)
				.putInsnSConstIdx(varName);
	}
	
	public LzProgramBuilder addArrayRead(String varName)
	{
		return addInsn(LzOpcodes.READ_INDEXED)
				.putInsnSConstIdx(varName);
	}
	
	public LzProgramBuilder addArrayWrite(String varName)
	{
		return addInsn(LzOpcodes.WRITE_INDEXED)
				.putInsnSConstIdx(varName);
	}
	
	public LzProgramBuilder addLabel(LzLabel label)
	{
		if(label.insnPos != null) throw new IllegalArgumentException("Tried registering " + label + " that was already added.");
		label.insnPos = insnList.size(); // Mark as registered
		int idx = labelTable.size();
		labelTable.put(label, idx);
		label.labelPos = idx;
		return addInsn(LzOpcodes.LABEL);
	}
	
	public LzProgramBuilder addJump(int insn, LzLabel label)
	{
		addInsn(insn);
		jumps.put(insnList.size(), label);
		return addInsn(-100); // filled later
	}
	
	public LzProgramBuilder addJumpIfTrue(LzLabel label)
	{
		return addJump(LzOpcodes.JUMP_IF_TRUE, label);
	}
	
	public LzProgramBuilder addJumpIfFalse(LzLabel label)
	{
		return addJump(LzOpcodes.JUMP_IF_FALSE, label);
	}
	
	public int allocLocal()
	{
		int local = argCount + 1 + usedLocals.size() * 2;
		usedLocals.add(local);
		return local;
	}
	
	// Internal methods
	
	public LzProgramBuilder putInsnDConstIdx(double value)
	{
		int pos = dConstants.computeIfAbsent(value, s -> dConstants.size());
		insnList.add(pos);
		return this;
	}
	
	public LzProgramBuilder putInsnSConstIdx(String value)
	{
		int pos = sConstants.computeIfAbsent(value, s -> sConstants.size());
		insnList.add(pos);
		return this;
	}
	
	public LzProgramBuilder putInsnCallIdx(LzCallInsn value)
	{
		int pos = callTable.computeIfAbsent(value, s -> callTable.size());
		insnList.add(pos);
		return this;
	}
	
	public LzProgramBody build()
	{
		int[] insnList = new int[this.insnList.size()];
		double[] dConstTable = new double[this.dConstants.size()];
		String[] sConstTable = new String[this.sConstants.size()];
		LzCallInsn[] callTable = new LzCallInsn[this.callTable.size()];
		for(int i = 0; i < insnList.length; i++) insnList[i] = this.insnList.get(i);
		for(int i = 0; i < dConstTable.length; i++) dConstTable[i] = this.dConstants.getKey(i);
		for(int i = 0; i < sConstTable.length; i++) sConstTable[i] = this.sConstants.getKey(i);
		for(int i = 0; i < callTable.length; i++) callTable[i] = this.callTable.getKey(i);
		for(Map.Entry<Integer, LzLabel> e : jumps.entrySet()) insnList[e.getKey()] = e.getValue().labelPos;
		return new LzProgramBody(insnList, dConstTable, sConstTable, callTable);
	}
	
	protected static class BiMap<K, V>
	{
		private final Map<K, V> map;
		private final Map<V, K> inverseMap;
		
		public BiMap()
		{
			this(HashMap::new);
		}
		
		@SuppressWarnings({"unchecked", "rawtypes"})
		public BiMap(Supplier<Map<?, ?>> factory)
		{
			this.map = (Map) factory.get();
			this.inverseMap = (Map) factory.get();
		}
		
		void put(K key, V value)
		{
			// Remove previous potential mappings
			if(inverseMap.containsKey(value)) map.remove(inverseMap.remove(value));
			if(map.containsKey(key)) inverseMap.remove(map.remove(key));
			
			map.put(key, value);
			inverseMap.put(value, key);
		}
		
		int size()
		{
			return map.size();
		}
		
		public Set<K> keySet()
		{
			return map.keySet();
		}
		
		public Set<V> valueSet()
		{
			return inverseMap.keySet();
		}
		
		public V getValue(K key)
		{
			return map.get(key);
		}
		
		public K getKey(V value)
		{
			return inverseMap.get(value);
		}
		
		public V computeIfAbsent(K key, Function<K, V> factory)
		{
			if(!map.containsKey(key))
			{
				V val = factory.apply(key);
				put(key, val);
				return val;
			}
			return map.get(key);
		}
		
		public void clear()
		{
			map.clear();
			inverseMap.clear();
		}
	}
}