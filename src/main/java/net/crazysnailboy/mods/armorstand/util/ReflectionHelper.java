package net.crazysnailboy.mods.armorstand.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

public class ReflectionHelper
{

	public static Field getDeclaredField(Class classToAccess, String... fieldNames)
	{
		Exception ex = null;
		for ( String fieldName : fieldNames )
		{
			try
			{
				Field declaredField = classToAccess.getDeclaredField(fieldName);
				declaredField.setAccessible(true);
				return declaredField;
			}
			catch (Exception e)
			{
				ex = e;
			}
		}
        throw new UnableToFindFieldException(fieldNames, ex);
	}


	public static Method getDeclaredMethod(Class classToAccess, String[] methodNames, Class<?>... methodTypes)
	{
		Exception ex = null;
        for ( String methodName : methodNames )
        {
            try
            {
                Method declaredMethod = classToAccess.getDeclaredMethod(methodName, methodTypes);
                declaredMethod.setAccessible(true);
                return declaredMethod;
            }
            catch (Exception e)
            {
				ex = e;
            }
        }
        throw new UnableToFindMethodException(methodNames, ex);
	}


	@SuppressWarnings("unchecked")
	public static <T,E> T getFieldValue(Field fieldToAccess, E instance)
	{
		T result = null;
		try
		{
			result = (T)fieldToAccess.get(instance);
		}
		catch (Exception ex)
		{
//			EnchantingTable.LOGGER.catching(ex);
		}
		return result;
	}

	public static <T,E> void setFieldValue(Field fieldToAccess, E instance, T value)
	{
		try
		{
			fieldToAccess.set(instance, value);
		}
		catch (Exception ex)
		{
//			EnchantingTable.LOGGER.catching(ex);
		}
	}


	public static <T> T invokeMethod(Method methodToAccess, Object instance, Object[] methodArguments)
	{
		try
		{
			return (T)methodToAccess.invoke(instance, methodArguments);
		}
		catch (Exception ex)
		{
		}
		return (T)null;
	}

	public static <T> T invokeMethod(Method methodToAccess, Object instance)
	{
		return invokeMethod(methodToAccess, instance, (Object[])null);
	}

}
