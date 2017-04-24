package net.crazysnailboy.mods.armorstand.common.network;

import net.crazysnailboy.mods.armorstand.client.gui.GuiArmorStand;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;


public class GuiHandler implements IGuiHandler
{

	public static final int GUI_ARMOR_STAND = 0;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int entityId, int unused1, int unused2)
	{
//		if (id == GUI_ARMOR_STAND)
//		{
//			EntityArmorStand armorstand = (EntityArmorStand)world.getEntityByID(entityId);
//			if (armorstand != null)
//			{
//				return new ContainerArmorStand(player.inventory, armorstand);
//			}
//		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int entityId, int unused1, int unused2)
	{
		if (id == GUI_ARMOR_STAND)
		{
			EntityArmorStand armorstand = (EntityArmorStand)world.getEntityByID(entityId);
			if (armorstand != null)
			{
				return new GuiArmorStand(armorstand);
//				return new GuiArmorStand_(player.inventory, armorstand);
			}
		}
		return null;
	}

}
