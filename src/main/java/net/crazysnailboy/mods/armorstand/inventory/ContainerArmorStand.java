package net.crazysnailboy.mods.armorstand.inventory;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public class ContainerArmorStand extends Container
{

	public ContainerArmorStand(IInventory playerInventory, final EntityArmorStand armorstand)
	{
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}

}
