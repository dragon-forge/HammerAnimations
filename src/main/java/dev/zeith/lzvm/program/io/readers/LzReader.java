package dev.zeith.lzvm.program.io.readers;

import dev.zeith.lzvm.program.LzProgram;
import dev.zeith.lzvm.program.io.LzDataBlock;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import static dev.zeith.lzvm.program.io.LzDataBlock.EOF;

public abstract class LzReader
{
	private final IBlockDecoder[] decoders = new IBlockDecoder[LzDataBlock.B_COUNT];
	protected final DataInputStream stream;
	
	public LzReader(InputStream stream)
	{
		this.stream = stream instanceof DataInputStream ? (DataInputStream) stream : new DataInputStream(stream);
		setup();
	}
	
	protected abstract void setup();
	
	protected final void regDecoder(int idx, IBlockDecoder decoder)
	{
		this.decoders[idx] = decoder;
	}
	
	public final LzProgram read()
			throws IOException
	{
		while(true)
		{
			LzDataBlock blk = readBlockHeader();
			if(blk.is(EOF))
				return buildProgram();
			IBlockDecoder dec = this.decoders[blk.getName()];
			if(dec == null)
				throw new IOException("Attempted to read unsupported data block: " + LzDataBlock.NAME_OF.getOrDefault(blk.getName(), "UNKNOWN[" + blk.getName() + "]"));
			dec.readBlock(blk);
		}
	}
	
	protected abstract LzProgram buildProgram();
	
	protected LzDataBlock readBlockHeader()
			throws IOException
	{
		int name = stream.readUnsignedShort();
		int length = stream.readInt();
		return new LzDataBlock(name, length);
	}
	
	protected IBlockDecoder readingByteArray(Consumer<byte[]> handler)
	{
		return block ->
		{
			byte[] data = new byte[block.getLength()];
			this.stream.readFully(data);
			handler.accept(data);
		};
	}
	
	protected IBlockDecoder readingByteBuf(Consumer<ByteBuffer> handler)
	{
		return readingByteArray(arr -> handler.accept(ByteBuffer.wrap(arr)));
	}
	
	protected IBlockDecoder readingStream(IStreamingBlockDecoder handler)
	{
		return block ->
		{
			byte[] data = new byte[block.getLength()];
			this.stream.readFully(data);
			try(DataInputStream in = new DataInputStream(new ByteArrayInputStream(data)))
			{
				handler.read(in);
			}
		};
	}
	
	protected IBlockDecoder skipping()
	{
		return block ->
		{
			int toSkip = block.getLength();
			int skipped = this.stream.skipBytes(toSkip);
			if(skipped < toSkip)
				throw new EOFException("Unable to skip " + toSkip + " bytes, skipped only " + skipped + " bytes.");
		};
	}
	
	protected interface IStreamingBlockDecoder
	{
		void read(DataInputStream stream)
				throws IOException;
	}
	
	@FunctionalInterface
	protected interface IBlockDecoder
	{
		void readBlock(LzDataBlock block)
				throws IOException;
	}
}