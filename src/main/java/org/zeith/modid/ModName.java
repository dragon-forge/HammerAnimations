package org.zeith.modid;

import com.zeitheron.hammercore.HammerCore;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ModName.MOD_ID, name = ModName.MOD_NAME, version = "@VERSION@", certificateFingerprint = "@FINGERPRINT@", dependencies = "required-after:hammercore", updateJSON = "http://dccg.herokuapp.com/api/fmluc/@CF_ID@")
public class ModName
{
	public static final String MOD_ID = "modid";
	public static final String MOD_NAME = "Mod Name";

	public static final Logger LOG = LogManager.getLogger();
	private static boolean invalidCertificate;

	@Mod.EventHandler
	public void certificateViolation(FMLFingerprintViolationEvent e)
	{
		LOG.warn("*****************************");
		LOG.warn("WARNING: Somebody has been tampering with " + ModName.MOD_NAME + " jar!");
		LOG.warn("It is highly recommended that you redownload mod from https://www.curseforge.com/projects/@CF_ID@ !");
		LOG.warn("*****************************");
		invalidCertificate = true;
		HammerCore.invalidCertificates.put(ModName.MOD_ID, "https://www.curseforge.com/projects/@CF_ID@");
	}
}