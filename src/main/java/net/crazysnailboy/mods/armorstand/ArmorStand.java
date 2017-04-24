package net.crazysnailboy.mods.armorstand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.crazysnailboy.mods.armorstand.common.config.ModConfiguration;
import net.crazysnailboy.mods.armorstand.common.network.ArmorStandSyncMessage;
import net.crazysnailboy.mods.armorstand.common.network.ConfigSyncMessage;
import net.crazysnailboy.mods.armorstand.common.network.GuiHandler;
import net.crazysnailboy.mods.armorstand.hooks.ArmorStandHooks;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ArmorStand.MODID, name = ArmorStand.NAME, version = ArmorStand.VERSION, acceptedMinecraftVersions = "[1.10.2]", guiFactory = ArmorStand.GUIFACTORY, updateJSON = ArmorStand.UPDATEJSON)
public class ArmorStand
{
	public static final String MODID = "csb_armorstand";
	public static final String NAME = "Armor Stand Configurator";
	public static final String VERSION = "${version}";
	public static final String GUIFACTORY = "net.crazysnailboy.mods.armorstand.client.config.ModGuiFactory";
	public static final String UPDATEJSON = "https://raw.githubusercontent.com/crazysnailboy/ArmorStand/master/update.json";


	@Instance(MODID)
	public static ArmorStand INSTANCE;

	public static Logger LOGGER = LogManager.getLogger(MODID);

	private static SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);


	public SimpleNetworkWrapper getNetwork()
	{
		return NETWORK;
	}


	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		// initialize the configuration
		ModConfiguration.preInit();
		if (event.getSide() == Side.CLIENT) ModConfiguration.clientPreInit();

		// register the network messages
		NETWORK.registerMessage(ArmorStandSyncMessage.MessageHandler.class, ArmorStandSyncMessage.class, 0, Side.SERVER);
		NETWORK.registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 1, Side.CLIENT);
		NETWORK.registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 2, Side.SERVER);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ArmorStand.INSTANCE, new GuiHandler());
	}


	@EventBusSubscriber
	public static class EventHandlers
	{

		private static boolean cancelRightClick = false;


		@SubscribeEvent
		public static void onPlayerEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event)
		{
			if (event.getTarget() instanceof EntityArmorStand)
			{

				if (ModConfiguration.overrideEntityInteract)
				{
					cancelRightClick = true;
				}
				if (ModConfiguration.overrideEntityInteract || ModConfiguration.enableConfigGui)
				{
					event.setCanceled(true);
				}


				if (event.getHand() == EnumHand.MAIN_HAND)
				{
					EntityArmorStand armorstand = (EntityArmorStand)event.getTarget();
					if (!event.getEntityPlayer().isSneaking())
					{
						if (ModConfiguration.overrideEntityInteract)
						{
							ArmorStandHooks.applyPlayerInteraction(armorstand, event.getEntityPlayer(), event.getLocalPos(), event.getItemStack(), event.getHand());
						}
					}
					else if (event.getWorld().isRemote)
					{
						if (ModConfiguration.enableConfigGui)
						{
							FMLNetworkHandler.openGui(event.getEntityPlayer(), ArmorStand.INSTANCE, GuiHandler.GUI_ARMOR_STAND, event.getWorld(), armorstand.getEntityId(), 0, 0);
						}
					}
				}

			}
		}

		@SubscribeEvent
		public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event)
		{
			if (cancelRightClick)
			{
				cancelRightClick = false;
				event.setCanceled(true);
			}
		}



	}
}
