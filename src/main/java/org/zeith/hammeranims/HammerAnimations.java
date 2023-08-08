package org.zeith.hammeranims;

import com.zeitheron.hammercore.HammerCore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.*;
import org.zeith.hammeranims.api.annotations.*;
import org.zeith.hammeranims.core.impl.api.animation.AnimationDecoder;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDecoder;
import org.zeith.hammeranims.core.proxy.CommonProxy;
import org.zeith.hammeranims.core.utils.reg.*;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;

@Mod(modid = HammerAnimations.MOD_ID, name = HammerAnimations.MOD_NAME, version = "@VERSION@", certificateFingerprint = "@FINGERPRINT@", dependencies = "required-after:hammercore", updateJSON = "https://api.modrinth.com/updates/C7cTlgwS/forge_updates.json")
public class HammerAnimations
{
	public static final String ROOT_PACKAGE = "org.zeith.hammeranims";
	public static final String MOD_ID = "hammeranims";
	public static final String MOD_NAME = "HammerAnimations";
	
	public static final Logger LOG = LogManager.getLogger(MOD_NAME);
	
	@SidedProxy(serverSide = ROOT_PACKAGE + ".core.proxy.ServerProxy",
			clientSide = ROOT_PACKAGE + ".core.proxy.ClientProxy")
	public static CommonProxy PROXY;
	
	public HammerAnimations()
	{
		AnimationDecoder.init();
		GeometryDecoder.init();
	}
	
	@Mod.EventHandler
	public void certificateViolation(FMLFingerprintViolationEvent e)
	{
		LOG.warn("*****************************");
		LOG.warn("WARNING: Somebody has been tampering with " + HammerAnimations.MOD_NAME + " jar!");
		LOG.warn("It is highly recommended that you redownload mod from https://www.curseforge.com/projects/@CF_ID@ !");
		LOG.warn("*****************************");
		HammerCore.invalidCertificates.put(HammerAnimations.MOD_ID, "https://www.curseforge.com/projects/@CF_ID@");
	}
	
	@Mod.EventHandler
	public void construct(FMLConstructionEvent e)
	{
		LOG.info("{} is constructing.", MOD_NAME);
		MinecraftForge.EVENT_BUS.register(PROXY);
		PROXY.construct();
		
		ASMDataTable asm = e.getASMHarvestedData();
		
		scan(asm, RegisterAnimations.class, (n, mod) -> MinecraftForge.EVENT_BUS.register(new AnimationRegistrar(n, mod)));
		scan(asm, RegisterGeometries.class, (n, mod) -> MinecraftForge.EVENT_BUS.register(new GeometryRegistrar(n, mod)));
		scan(asm, RegisterTimeFunctions.class, (n, mod) -> MinecraftForge.EVENT_BUS.register(new TimeFunctionRegistrar(n, mod)));
		scan(asm, RegisterAnimationSourceTypes.class, (n, mod) -> MinecraftForge.EVENT_BUS.register(new AnimationSourceTypesRegistrar(n, mod)));
		scan(asm, RegisterAnimations.class, (n, mod) -> MinecraftForge.EVENT_BUS.register(new AnimationActionsRegistrar(n, mod)));
	}
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
//		SimpleRegistration.registerFieldBlocksFrom(ContainersHA.class, MOD_ID, CreativeTabs.MISC);
	}
	
	@Mod.EventHandler
	public void commonSetup(FMLInitializationEvent e)
	{
		PROXY.init();
	}
	
	@Mod.EventHandler
	public void startServer(FMLServerAboutToStartEvent e)
	{
		PROXY.serverAboutToStart(e.getServer());
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
	
	private void scan(ASMDataTable table, Class<? extends Annotation> type, BiConsumer<String, ModContainer> handler)
	{
		for(ASMDataTable.ASMData data : table.getAll(type.getCanonicalName()))
		{
			ModContainer mod = data.getCandidate().getContainedMods().stream().findFirst().orElse(null);
			if(mod == null)
			{
				LOG.warn("Skipping @{}-annotated class {} since it does not belong to any mod.", type.getSimpleName(), data.getClassName());
				continue;
			}
			
			handler.accept(data.getClassName(), mod);
			LOG.info("Applied @{} to {}, which belongs to {} ({})", type.getSimpleName(), data.getClassName(), mod.getModId(), mod.getName());
		}
	}
}