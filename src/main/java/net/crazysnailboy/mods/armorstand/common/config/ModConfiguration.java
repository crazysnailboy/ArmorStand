package net.crazysnailboy.mods.armorstand.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.crazysnailboy.mods.armorstand.ArmorStand;
import net.crazysnailboy.mods.armorstand.client.config.ModGuiConfigEntries;
import net.crazysnailboy.mods.armorstand.common.network.ConfigSyncMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ModConfiguration
{
	private static Configuration config = null;

	public static boolean enableConfigGui = true;
	public static boolean overrideEntityInteract = true;


	public static void initializeConfiguration()
	{
		File configFile = new File(Loader.instance().getConfigDir(), ArmorStand.MODID + ".cfg");
		config = new Configuration(configFile);
		config.load();
		syncFromFile();
	}


	public static Configuration getConfig()
	{
		return config;
	}


	public static void syncFromFile()
	{
		syncConfig(true, true);
	}

	public static void syncFromGUI()
	{
		syncConfig(false, true);
	}


	private static void syncConfig(boolean loadConfigFromFile, boolean readFieldsFromConfig)
	{

		if (loadConfigFromFile)
		{
			config.load();
		}

		Property propEnableConfigGUI = config.get(Configuration.CATEGORY_GENERAL, "enableConfigGui", true, "");
		propEnableConfigGUI.setLanguageKey(String.format("%s.config.enableConfigGui", ArmorStand.MODID));
		propEnableConfigGUI.setRequiresMcRestart(false);

		Property propEnableDeathDrops = config.get(Configuration.CATEGORY_GENERAL, "overrideEntityInteract", true, "");
		propEnableDeathDrops.setLanguageKey(String.format("%s.config.overrideEntityInteract", ArmorStand.MODID));
		propEnableDeathDrops.setRequiresMcRestart(false);


		try
		{
			propEnableConfigGUI.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);
			propEnableDeathDrops.setConfigEntryClass(ModGuiConfigEntries.BooleanEntry.class);

			List<String> propOrderGeneral = new ArrayList<String>();
			propOrderGeneral.add(propEnableConfigGUI.getName());
			propOrderGeneral.add(propEnableDeathDrops.getName());
			config.setCategoryPropertyOrder(Configuration.CATEGORY_GENERAL, propOrderGeneral);

		}
		catch(NoClassDefFoundError e) { }


		if (readFieldsFromConfig)
		{
			enableConfigGui = propEnableConfigGUI.getBoolean();
			overrideEntityInteract = propEnableDeathDrops.getBoolean();
		}

		propEnableConfigGUI.set(enableConfigGui);
		propEnableDeathDrops.set(overrideEntityInteract);

		if (config.hasChanged())
		{
			config.save();
		}

	}


	@EventBusSubscriber
	public static class ConfigEventHandler
	{

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerLoggedInEvent event)
		{
			if (!event.player.world.isRemote)
			{
				ArmorStand.INSTANCE.getNetwork().sendTo(new ConfigSyncMessage(), (EntityPlayerMP)event.player);
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
		{
			if (event.getModID().equals(ArmorStand.MODID))
			{
				if (!event.isWorldRunning() || Minecraft.getMinecraft().isSingleplayer())
				{
					syncFromGUI();
					if (event.isWorldRunning() && Minecraft.getMinecraft().isSingleplayer())
					{
						ArmorStand.INSTANCE.getNetwork().sendToServer(new ConfigSyncMessage());
					}
				}
			}
		}
	}

}
