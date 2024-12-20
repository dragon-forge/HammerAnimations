package org.zeith.hammeranims.standalone.utils;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class GifEncoder
{
	private ImageWriter gifWriter;
	private ImageOutputStream outputStream;
	private boolean isStarted;
	private boolean isFirstFrame;
	private int frameDelay = 10; // Default delay (100ms, corresponds to 10 FPS)
	private String disposalMethod = "none"; // Default disposal method
	private boolean loop = true; // Default to looping
	
	/**
	 * Initializes the encoder and prepares the output file for writing GIF data.
	 *
	 * @param outputFile
	 * 		the path to the output GIF file
	 *
	 * @throws IOException
	 * 		if an I/O error occurs
	 */
	public synchronized void start(Object outputFile)
			throws IOException
	{
		if(isStarted)
		{
			throw new IllegalStateException("GIF encoder has already been started.");
		}
		
		if(outputFile instanceof File f) f.delete();
		outputStream = ImageIO.createImageOutputStream(outputFile);
		gifWriter = ImageIO.getImageWritersByFormatName("gif").next();
		
		gifWriter.setOutput(outputStream);
		gifWriter.prepareWriteSequence(null);
		
		isStarted = true;
		isFirstFrame = true;
	}
	
	/**
	 * Sets the framerate for the GIF.
	 *
	 * @param framerate
	 * 		the framerate in frames per second
	 *
	 * @throws IllegalArgumentException
	 * 		if the framerate is less than or equal to zero
	 */
	public synchronized void setFramerate(int framerate)
	{
		if(framerate <= 0)
			throw new IllegalArgumentException("Framerate must be greater than 0.");
		
		frameDelay = 100 / framerate; // Calculate delay in hundredths of a second
	}
	
	/**
	 * Sets the disposal method for the frames.
	 *
	 * @param method
	 * 		the disposal method (e.g., "none", "restoreToBackgroundColor")
	 */
	public synchronized void setDisposalMethod(String method)
	{
		if(method == null || method.isEmpty())
			throw new IllegalArgumentException("Disposal method cannot be null or empty.");
		
		disposalMethod = method;
	}
	
	/**
	 * Enables or disables looping for the output GIF.
	 *
	 * @param loop
	 * 		true to enable looping, false to disable
	 */
	public synchronized void setLoop(boolean loop)
	{
		this.loop = loop;
	}
	
	/**
	 * Adds a frame to the GIF sequence.
	 *
	 * @param frame
	 * 		the BufferedImage representing a single frame
	 *
	 * @throws IOException
	 * 		if an I/O error occurs or if the encoder hasn't been started
	 */
	public synchronized void addFrame(RenderedImage frame)
			throws IOException
	{
		if(!isStarted)
			throw new IllegalStateException("GIF encoder has not been started.");
		
		// Retrieve default metadata for GIF
		IIOMetadata metadata = gifWriter.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(frame), null);
		
		if(isFirstFrame)
			configureLooping(metadata);
		
		// Configure metadata if needed (e.g., transparency, frame delay)
		configureMetadata(metadata);
		
		// Write the frame directly to the output stream
		gifWriter.writeToSequence(new IIOImage(frame, null, metadata), null);
		
		isFirstFrame = false;
	}
	
	/**
	 * Finalizes the GIF encoding process and writes the file to disk.
	 *
	 * @throws IOException
	 * 		if an I/O error occurs
	 */
	public synchronized void finish()
			throws IOException
	{
		if(!isStarted)
		{
			throw new IllegalStateException("GIF encoder has not been started.");
		}
		
		try
		{
			gifWriter.endWriteSequence();
		} finally
		{
			if(outputStream != null)
			{
				outputStream.close();
			}
			if(gifWriter != null)
			{
				gifWriter.dispose();
			}
			isStarted = false;
		}
	}
	
	/**
	 * Configures looping for the GIF.
	 *
	 * @throws IOException
	 * 		if an I/O error occurs
	 */
	private void configureLooping(IIOMetadata metadata)
			throws IOException
	{
		String metaFormatName = metadata.getNativeMetadataFormatName();
		IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
		
		IIOMetadataNode appExtsNode = getNode(root, "ApplicationExtensions");
		IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
		child.setAttribute("applicationID", "NETSCAPE");
		child.setAttribute("authenticationCode", "2.0");
		int loop = this.loop ? 0 : 1;
		child.setUserObject(new byte[]{ 0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)});
		appExtsNode.appendChild(child);
		
		metadata.setFromTree(metaFormatName, root);
	}
	
	/**
	 * Configures GIF metadata for each frame.
	 *
	 * @param metadata
	 * 		the IIOMetadata object to configure
	 *
	 * @throws IOException
	 * 		if an I/O error occurs
	 */
	private void configureMetadata(IIOMetadata metadata)
			throws IOException
	{
		String metaFormatName = metadata.getNativeMetadataFormatName();
		IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
		
		IIOMetadataNode gce = getNode(root, "GraphicControlExtension");
		gce.setAttribute("delayTime", Integer.toString(frameDelay));
		gce.setAttribute("disposalMethod", disposalMethod);
		
		metadata.setFromTree(metaFormatName, root);
	}
	
	/**
	 * Retrieves or creates a node in the metadata tree.
	 *
	 * @param root
	 * 		the root node
	 * @param nodeName
	 * 		the name of the node to retrieve or create
	 *
	 * @return the requested node
	 */
	private IIOMetadataNode getNode(IIOMetadataNode root, String nodeName)
	{
		for(int i = 0; i < root.getLength(); i++)
		{
			if(root.item(i).getNodeName().equalsIgnoreCase(nodeName))
			{
				return (IIOMetadataNode) root.item(i);
			}
		}
		IIOMetadataNode node = new IIOMetadataNode(nodeName);
		root.appendChild(node);
		return node;
	}
}