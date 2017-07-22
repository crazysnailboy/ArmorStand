package net.crazysnailboy.mods.armorstand.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class GuiComponents
{

	public static class GuiToggleButton extends net.minecraft.client.gui.GuiButton
	{

		private boolean value;

		public GuiToggleButton(int buttonId, int x, int y, int width, int height, boolean defaultValue)
		{
			super(buttonId, x, y, width, height, I18n.format(defaultValue ? "gui.yes" : "gui.no"));
			this.value = defaultValue;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			super.drawButton(mc, mouseX, mouseY);
		}

		public void buttonPressed()
		{
			this.value = !this.value;
			this.displayString = I18n.format(this.value ? "gui.yes" : "gui.no");
		}

		public boolean getValue()
		{
			return this.value;
		}

		public void setValue(boolean value)
		{
			this.value = value;
			this.displayString = I18n.format(this.value ? "gui.yes" : "gui.no");
		}

	}


	public static class GuiNumberField extends net.minecraft.client.gui.GuiTextField
	{

		public GuiNumberField(int componentId, FontRenderer fontRenderer, int x, int y, int width, int height, String defaultValue)
		{
			super(componentId, fontRenderer, x, y, width, height);
			this.setText(defaultValue);
		}

		@Override
		public void writeText(String textToWrite)
		{
			if (this.isNumeric(textToWrite)) super.writeText(textToWrite);
		}

		@Override
		public String getText()
		{
			return (this.isNumeric(super.getText()) ? super.getText() : "0");
		}

		public float getFloat()
		{
			try
			{
				return Float.valueOf(super.getText());
			}
			catch (NumberFormatException ex)
			{
				return 0.0F;
			}
		}

		@Override
		public void setFocused(boolean isFocused)
		{
			super.setFocused(isFocused);
			if (!isFocused)
			{
				this.setSelectionPos(this.getText().length());
				this.setCursorPositionEnd();
			}
		}

		private boolean isNumeric(String value)
		{
			if (value.equals("-"))
			{
				return true;
			}
			else
			{
				try
				{
					Integer.parseInt(value);
					return true;
				}
				catch (NumberFormatException ex)
				{
					return false;
				}
			}
		}

	}


}
