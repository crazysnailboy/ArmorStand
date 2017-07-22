package net.crazysnailboy.mods.armorstand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.crazysnailboy.mods.armorstand.client.init.ModKeyBindings;
import net.crazysnailboy.mods.armorstand.common.config.ModConfiguration;
import net.crazysnailboy.mods.armorstand.common.network.GuiHandler;
import net.crazysnailboy.mods.armorstand.common.network.message.ArmorStandSyncMessage;
import net.crazysnailboy.mods.armorstand.common.network.message.ConfigSyncMessage;
import net.crazysnailboy.mods.armorstand.common.network.message.EntityFlagMessage;
import net.crazysnailboy.mods.armorstand.hooks.ArmorStandHooks;
import net.crazysnailboy.mods.armorstand.util.EntityUtils;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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


@Mod(modid = ArmorStand.MODID, name = ArmorStand.NAME, version = ArmorStand.VERSION, acceptedMinecraftVersions = "[1.11,1.11.2]", guiFactory = ArmorStand.GUIFACTORY, updateJSON = ArmorStand.UPDATEJSON)
public class ArmorStand
{

	public static final String MODID = "csb_armorstand";
	public static final String NAME = "Armor Stand Configurator";
	public static final String VERSION = "${version}";
	public static final String GUIFACTORY = "net.crazysnailboy.mods.armorstand.client.config.ModGuiFactory";
	public static final String UPDATEJSON = "https://raw.githubusercontent.com/crazysnailboy/ArmorStand/master/update.json";


	@Instance(MODID)
	public static ArmorStand INSTANCE;

	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);


	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		// initialize the configuration
		ModConfiguration.initializeConfig();

		// register the network messages
		NETWORK.registerMessage(ArmorStandSyncMessage.MessageHandler.class, ArmorStandSyncMessage.class, 0, Side.SERVER);
		NETWORK.registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 1, Side.CLIENT);
		NETWORK.registerMessage(ConfigSyncMessage.MessageHandler.class, ConfigSyncMessage.class, 2, Side.SERVER);
		NETWORK.registerMessage(EntityFlagMessage.MessageHandler.class, EntityFlagMessage.class, 3, Side.SERVER);

		if (event.getSide() == Side.CLIENT)
		{
			// register the key bindings
			ModKeyBindings.registerKeyBindings();
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// register the gui handler
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
				EntityArmorStand armorstand = (EntityArmorStand)event.getTarget();
				boolean isKeyDown = EntityUtils.getFlag(event.getEntityPlayer(), 2);

				if (ModConfiguration.enableConfigGui && isKeyDown)
				{
					if (event.getHand() == EnumHand.MAIN_HAND && event.getWorld().isRemote)
					{
						FMLNetworkHandler.openGui(event.getEntityPlayer(), ArmorStand.INSTANCE, GuiHandler.GUI_ARMOR_STAND, event.getWorld(), armorstand.getEntityId(), 0, 0);
					}
					event.setCanceled(true);
					return;
				}

				if (ModConfiguration.enableNameTags && !isKeyDown && !event.getEntityPlayer().isSneaking())
				{
					ItemStack stack = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
					if (!stack.isEmpty() && stack.getItem() == Items.NAME_TAG && stack.hasDisplayName())
					{
						cancelRightClick = true;
						if (event.getHand() == EnumHand.MAIN_HAND && !event.getWorld().isRemote)
						{
							armorstand.setCustomNameTag(stack.getDisplayName());
							armorstand.setAlwaysRenderNameTag(true);
						}
						event.setCanceled(true);
						return;
					}
				}

				if (ModConfiguration.overrideEntityInteract && !isKeyDown && !event.getEntityPlayer().isSneaking())
				{
					cancelRightClick = true;
					if (event.getHand() == EnumHand.MAIN_HAND)
					{
						ArmorStandHooks.applyPlayerInteraction(armorstand, event.getEntityPlayer(), event.getLocalPos(), event.getItemStack(), event.getHand());
					}
					event.setCanceled(true);
					return;
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
