package Scorpio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import Scorpio.Collections.*;
import Scorpio.Variable.*;
import Scorpio.*;

public final class Util
{
	private static final java.lang.Class TYPE_OBJECT = Object.class;
	private static final java.lang.Class TYPE_TYPE = java.lang.Class.class;
	private static final java.lang.Class TYPE_BOOL = Boolean.class;
	private static final java.lang.Class TYPE_STRING = String.class;
	private static final java.lang.Class TYPE_SBYTE = Byte.class;
	private static final java.lang.Class TYPE_BYTE = Byte.class;
	private static final java.lang.Class TYPE_SHORT = Short.class;
	private static final java.lang.Class TYPE_USHORT = Short.class;
	private static final java.lang.Class TYPE_INT = Integer.class;
	private static final java.lang.Class TYPE_UINT = Integer.class;
	private static final java.lang.Class TYPE_LONG = Long.class;
	private static final java.lang.Class TYPE_FLOAT = Float.class;
	private static final java.lang.Class TYPE_DOUBLE = Double.class;
	private static final java.lang.Class TYPE_DECIMAL = java.math.BigDecimal.class;
	private static FileInputStream stream;

	public static void SetObject(TableDictionary variables, Object key, ScriptObject obj)
	{
		variables.put(key, obj.Assign());
	}
	public static void SetObject(VariableDictionary variables, String key, ScriptObject obj)
	{
		variables.put(key, obj.Assign());
	}
	public static boolean IsBool(java.lang.Class type)
	{
		return type == TYPE_BOOL;
	}
	public static boolean IsString(java.lang.Class type)
	{
		return type == TYPE_STRING;
	}
	public static boolean IsDouble(java.lang.Class type)
	{
		return type == TYPE_DOUBLE;
	}
	public static boolean IsLong(java.lang.Class type)
	{
		return type == TYPE_LONG;
	}
	public static boolean IsNumber(java.lang.Class type)
	{
		return (type == TYPE_SBYTE || type == TYPE_BYTE || type == TYPE_SHORT || type == TYPE_USHORT || type == TYPE_INT || type == TYPE_UINT || type == TYPE_FLOAT || type == TYPE_DOUBLE || type == TYPE_DECIMAL || type == TYPE_LONG);
	}
	public static boolean IsEnum(java.lang.Class type)
	{
		return type.isEnum();
	}
	public static boolean IsType(java.lang.Class type)
	{
		return type == TYPE_TYPE;
	}
	public static boolean IsBoolObject(Object obj)
	{
		return obj instanceof Boolean;
	}
	public static boolean IsStringObject(Object obj)
	{
		return obj instanceof String;
	}
	public static boolean IsDoubleObject(Object obj)
	{
		return obj instanceof Double;
	}
	public static boolean IsLongObject(Object obj)
	{
		return obj instanceof Long;
	}
	public static boolean IsNumberObject(Object obj)
	{
		return (obj instanceof Byte || obj instanceof Byte || obj instanceof Short || obj instanceof Short || obj instanceof Integer || obj instanceof Integer || obj instanceof Float || obj instanceof Double || obj instanceof java.math.BigDecimal || obj instanceof Long);
	}
	public static boolean IsEnumObject(Object obj)
	{
		return obj.getClass().isEnum();
	}
	public static Object ChangeType(ScriptObject par, java.lang.Class type) throws Exception
	{
		if (type == TYPE_OBJECT)
		{
			if (par instanceof ScriptNumber)
			{
				return type.isEnum() ? ToEnum(type, ((ScriptNumber)par).ToInt32()) : ChangeType(par.getObjectValue(), type);
			}
			else
			{
				return par.getObjectValue();
			}
		}
		else
		{
			if (type.isAssignableFrom(par.getClass()))
			{
				return par;
			}
			else if (par instanceof ScriptNumber)
			{
				return type.isEnum() ? ToEnum(type, ((ScriptNumber)par).ToInt32()) : ChangeType(par.getObjectValue(), type);
			}
			else if (par instanceof ScriptUserdata)
			{
				if (Util.IsType(type))
				{
					return ((ScriptUserdata)par).getValueType();
				}
				else
				{
					return par.getObjectValue();
				}
			}
			else
			{
				return par.getObjectValue();
			}
		}
	}
	public static boolean CanChangeType(ScriptObject[] pars, java.lang.Class[] types)
	{
		if (pars.length != types.length)
		{
			return false;
		}
		for (int i = 0; i < pars.length;++i)
		{
			if (!CanChangeType(pars[i], types[i]))
			{
				return false;
			}
		}
		return true;
	}
	public static boolean CanChangeType(ScriptObject par, java.lang.Class type)
	{
		if (type == TYPE_OBJECT || par.getIsNull())
		{
			return true;
		}
		else
		{
			if (par instanceof ScriptString && Util.IsString(type))
			{
				return true;
			}
			else if (par instanceof ScriptNumber && (IsNumber(type) || IsEnum(type)))
			{
				return true;
			}
			else if (par instanceof ScriptBoolean && IsBool(type))
			{
				return true;
			}
			else if (par instanceof ScriptEnum && ((ScriptEnum)((par instanceof ScriptEnum) ? par : null)).getEnumType() == type)
			{
				return true;
			}
			else if (par instanceof ScriptUserdata)
			{
				if (Util.IsType(type))
				{
					return true;
				}
				else if (type.isAssignableFrom(((ScriptUserdata)par).getValueType()))
				{
					return true;
				}
			}
			else if (type.isAssignableFrom(par.getClass()))
			{
				return true;
			}
		}
		return false;
	}
	public static String GetFileString(String fileName, String charsetName) throws IOException
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		stream = new FileInputStream(new File(fileName));
        int n = 0;
        byte[] buffer = new byte[4096];
        while (-1 != (n = stream.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return new String(output.toByteArray(), charsetName);
	}
	public static boolean IsNullOrEmpty(String str)
	{
		return str == null || str.isEmpty();
	}
	public static Object ChangeType(Object value, java.lang.Class conversionType)
	{
		if (conversionType == TYPE_BYTE)
	    {
	        return (Byte)value;
	    }
	    if (conversionType == TYPE_SHORT)
	    {
	        return (Short)value;
	    }
	    if (conversionType == TYPE_INT)
	    {
	        return (Integer)value;
	    }
	    if (conversionType == TYPE_LONG)
	    {
	        return (Long)value;
	    }
	    if (conversionType == TYPE_FLOAT)
	    {
	        return (Float)value;
	    }
	    if (conversionType == TYPE_DOUBLE)
	    {
	        return (Double)value;
	    }
	    return null;
	}
	public static int ToInt32(Object value)
	{
		return (Integer)value;
		//return Integer.parseInt(value);
	}
	public static double ToDouble(Object value)
	{
		return (Double)value;
		//return Double.parseDouble(value);
	}
	public static long ToInt64(Object value)
	{
		return (Long)value;
		//return Long.parseLong(value);
	}
	public static Object ToEnum(Class<?> type, int number) throws Exception
	{
		Method values = type.getMethod("values");
		Object[] rets = (Object[]) values.invoke(null);
		return rets[number];
	}
}