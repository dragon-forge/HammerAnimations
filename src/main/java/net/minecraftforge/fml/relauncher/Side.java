package net.minecraftforge.fml.relauncher;

/**
 * This is here to allow the game to start up...
 * The class is discarded during compilation.
 */
public enum Side
{
	CLIENT,
	SERVER;
	
	public boolean isServer()
	{
		return !this.isClient();
	}
	
	public boolean isClient()
	{
		return this == CLIENT;
	}
}