package net.crazysnailboy.mods.armorstand.hooks;

import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.crazysnailboy.mods.armorstand.util.ReflectionHelper;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

public class ArmorStandHooks
{


	public static EnumActionResult applyPlayerInteraction(EntityArmorStand armorstand, EntityPlayer player, Vec3d vec, @Nullable ItemStack stack, EnumHand hand)
	{
		if (armorstand.hasMarker())
		{
			return EnumActionResult.PASS;
		}
		else if (!armorstand.world.isRemote && !player.isSpectator())
		{
			boolean playerHasStack = (stack != null);
			if (playerHasStack)
			{
				return giveItemToArmorStand(armorstand, player, stack);
			}
			else
			{
				return takeItemFromArmorStand(armorstand, player, vec);
			}
		}
		else
		{
			return EnumActionResult.SUCCESS;
		}
	}


	private static EnumActionResult giveItemToArmorStand(EntityArmorStand armorstand, EntityPlayer player, ItemStack playerStack)
	{
		for ( EntityEquipmentSlot giveSlot : new EntityEquipmentSlot[] { getSlotFromItem(armorstand, playerStack.getItem()), EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND } )
		{
			if (giveSlot == null) continue;

			boolean entityHasStack = armorstand.getItemStackFromSlot(giveSlot) != null;
			if (!entityHasStack && !isDisabled(armorstand, giveSlot))
			{
				if (giveSlot.getSlotType() == Type.HAND && !armorstand.getShowArms()) return EnumActionResult.FAIL;

				swapItem(armorstand, player, giveSlot, playerStack, EnumHand.MAIN_HAND);
				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.FAIL;
	}


	private static EnumActionResult takeItemFromArmorStand(EntityArmorStand armorstand, EntityPlayer player, Vec3d vec)
	{
		for ( EntityEquipmentSlot takeSlot : new EntityEquipmentSlot[] { EntityEquipmentSlot.OFFHAND, EntityEquipmentSlot.MAINHAND, getSlotFromVector(armorstand, vec) })
		{
			if (takeSlot == null) continue;

			boolean entityHasStack = armorstand.getItemStackFromSlot(takeSlot) != null;
			if (entityHasStack && !isDisabled(armorstand, takeSlot))
			{
				ItemStack takeStack = armorstand.getItemStackFromSlot(takeSlot).copy();
				if (player.inventory.addItemStackToInventory(takeStack))
				{
					armorstand.setItemStackToSlot(takeSlot, null);
				}
				else
				{
					armorstand.entityDropItem(takeStack, 0.0F);
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}


	private static EntityEquipmentSlot getSlotFromItem(EntityArmorStand armorstand, Item playerItem)
	{
		EntityEquipmentSlot giveSlot = null;
		if (playerItem instanceof ItemArmor)
		{
			giveSlot = ((ItemArmor)playerItem).armorType;
		}
		if (playerItem == Items.SKULL || playerItem == Item.getItemFromBlock(Blocks.PUMPKIN))
		{
			giveSlot = EntityEquipmentSlot.HEAD;
		}
		return giveSlot;
	}

	private static EntityEquipmentSlot getSlotFromVector(EntityArmorStand armorstand, Vec3d vec)
	{
		EntityEquipmentSlot slot = null;
		double d0 = 0.1D;
		double d1 = 0.9D;
		double d2 = 0.4D;
		double d3 = 1.6D;
		boolean isSmall = armorstand.isSmall();
		double d4 = isSmall ? vec.yCoord * 2.0D : vec.yCoord;

		if (d4 >= 0.1D && d4 < 0.1D + (isSmall ? 0.8D : 0.45D) && armorstand.getItemStackFromSlot(EntityEquipmentSlot.FEET) != null)
		{
			slot = EntityEquipmentSlot.FEET;
		}
		else if (d4 >= 0.9D + (isSmall ? 0.3D : 0.0D) && d4 < 0.9D + (isSmall ? 1.0D : 0.7D) && armorstand.getItemStackFromSlot(EntityEquipmentSlot.CHEST) != null)
		{
			slot = EntityEquipmentSlot.CHEST;
		}
		else if (d4 >= 0.4D && d4 < 0.4D + (isSmall ? 1.0D : 0.8D) && armorstand.getItemStackFromSlot(EntityEquipmentSlot.LEGS) != null)
		{
			slot = EntityEquipmentSlot.LEGS;
		}
		else if (d4 >= 1.6D && armorstand.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null)
		{
			slot = EntityEquipmentSlot.HEAD;
		}
		return slot;
	}




//	private static EnumActionResult applyPlayerInteraction_defaultImplementation(EntityArmorStand armorstand, EntityPlayer player, Vec3d vec, @Nullable ItemStack stack, EnumHand hand)
//	{
//		if (armorstand.hasMarker())
//		{
//			return EnumActionResult.PASS;
//		}
//		else if (!armorstand.world.isRemote && !player.isSpectator())
//		{
//			EntityEquipmentSlot entityequipmentslot = EntityEquipmentSlot.MAINHAND;
//			boolean flag = stack != null;
//			Item item = flag ? stack.getItem() : null;
//
//			if (flag && item instanceof ItemArmor)
//			{
//				entityequipmentslot = ((ItemArmor)item).armorType;
//			}
//
//			if (flag && (item == Items.SKULL || item == Item.getItemFromBlock(Blocks.PUMPKIN)))
//			{
//				entityequipmentslot = EntityEquipmentSlot.HEAD;
//			}
//
//			double d0 = 0.1D;
//			double d1 = 0.9D;
//			double d2 = 0.4D;
//			double d3 = 1.6D;
//			EntityEquipmentSlot entityequipmentslot1 = EntityEquipmentSlot.MAINHAND;
//			boolean flag1 = armorstand.isSmall();
//			double d4 = flag1 ? vec.yCoord * 2.0D : vec.yCoord;
//
//			if (d4 >= 0.1D && d4 < 0.1D + (flag1 ? 0.8D : 0.45D) && armorstand.getItemStackFromSlot(EntityEquipmentSlot.FEET) != null)
//			{
//				entityequipmentslot1 = EntityEquipmentSlot.FEET;
//			}
//			else if (d4 >= 0.9D + (flag1 ? 0.3D : 0.0D) && d4 < 0.9D + (flag1 ? 1.0D : 0.7D) && armorstand.getItemStackFromSlot(EntityEquipmentSlot.CHEST) != null)
//			{
//				entityequipmentslot1 = EntityEquipmentSlot.CHEST;
//			}
//			else if (d4 >= 0.4D && d4 < 0.4D + (flag1 ? 1.0D : 0.8D) && armorstand.getItemStackFromSlot(EntityEquipmentSlot.LEGS) != null)
//			{
//				entityequipmentslot1 = EntityEquipmentSlot.LEGS;
//			}
//			else if (d4 >= 1.6D && armorstand.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null)
//			{
//				entityequipmentslot1 = EntityEquipmentSlot.HEAD;
//			}
//
//			boolean flag2 = armorstand.getItemStackFromSlot(entityequipmentslot1) != null;
//
//			if (isDisabled(armorstand, entityequipmentslot1) || isDisabled(armorstand, entityequipmentslot))
//			{
//				entityequipmentslot1 = entityequipmentslot;
//
//				if (isDisabled(armorstand, entityequipmentslot))
//				{
//					return EnumActionResult.FAIL;
//				}
//			}
//
//			if (flag && entityequipmentslot == EntityEquipmentSlot.MAINHAND && !armorstand.getShowArms())
//			{
//				return EnumActionResult.FAIL;
//			}
//			else
//			{
//				if (flag)
//				{
//					swapItem(armorstand, player, entityequipmentslot, stack, hand);
//				}
//				else if (flag2)
//				{
//					swapItem(armorstand, player, entityequipmentslot1, stack, hand);
//				}
//
//				return EnumActionResult.SUCCESS;
//			}
//		}
//		else
//		{
//			return EnumActionResult.SUCCESS;
//		}
//	}


	private static final Method isDisabled = ReflectionHelper.getDeclaredMethod(EntityArmorStand.class, new String[] { "isDisabled", "func_184796_b" }, EntityEquipmentSlot.class);
	private static final Method swapItem = ReflectionHelper.getDeclaredMethod(EntityArmorStand.class, new String[] { "swapItem", "func_184795_a" }, EntityPlayer.class, EntityEquipmentSlot.class, ItemStack.class, EnumHand.class);

	private static boolean isDisabled(EntityArmorStand armorstand, EntityEquipmentSlot entityequipmentslot)
	{
		return ReflectionHelper.invokeMethod(isDisabled, armorstand, new Object[] { entityequipmentslot });
	}

	private static void swapItem(EntityArmorStand armorstand, EntityPlayer player, EntityEquipmentSlot entityequipmentslot, @Nullable ItemStack itemstack, EnumHand hand)
	{
		ReflectionHelper.invokeMethod(swapItem, armorstand, new Object[] { player, entityequipmentslot, itemstack, hand });
	}

}
