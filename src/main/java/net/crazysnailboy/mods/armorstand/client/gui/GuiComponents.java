package net.crazysnailboy.mods.armorstand.client.gui;

import org.apache.commons.lang3.StringUtils;
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

		public GuiNumberField(int componentId, FontRenderer fontRendererObj, int x, int y, int width, int height, String defaultValue)
		{
			super(componentId, fontRendererObj, x, y, width, height);
			this.setText(defaultValue);
		}

		@Override
		public void writeText(String textToWrite)
		{
			if (StringUtils.isNumeric(textToWrite)) super.writeText(textToWrite);
		}

		@Override
		public String getText()
		{
			return (StringUtils.isNumeric(super.getText()) ? super.getText() : "0");
		}

		@Override
		public void setFocused(boolean isFocusedIn)
		{
			super.setFocused(isFocusedIn);
			if (!isFocusedIn)
			{
				this.setSelectionPos(this.getText().length());
				this.setCursorPositionEnd();
			}
		}

	}


}
