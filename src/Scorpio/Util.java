package Scorpio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import Scorpio.Exception.ExecutionException;
import Scorpio.Exception.ScriptException;
import Scorpio.Variable.*;
import Scorpio.*;

public final class Util {
    private static final java.lang.Class TYPE_VOID = void.class;
    private static final java.lang.Class TYPE_OBJECT = Object.class;
    private static final java.lang.Class TYPE_TYPE = java.lang.Class.class;
    private static final java.lang.Class TYPE_BOOL = Boolean.class;
    private static final java.lang.Class TYPE_BOOL_PRI = Boolean.TYPE;
    private static final java.lang.Class TYPE_STRING = String.class;
    private static final java.lang.Class TYPE_SBYTE = Byte.class;
    private static final java.lang.Class TYPE_SBYTE_PRI = Byte.TYPE;
    private static final java.lang.Class TYPE_BYTE = Byte.class;
    private static final java.lang.Class TYPE_BYTE_PRI = Byte.TYPE;
    private static final java.lang.Class TYPE_SHORT = Short.class;
    private static final java.lang.Class TYPE_SHORT_PRI = Short.TYPE;
    private static final java.lang.Class TYPE_USHORT = Short.class;
    private static final java.lang.Class TYPE_USHORT_PRI = Short.TYPE;
    private static final java.lang.Class TYPE_INT = Integer.class;
    private static final java.lang.Class TYPE_INT_PRI = Integer.TYPE;
    private static final java.lang.Class TYPE_UINT = Integer.class;
    private static final java.lang.Class TYPE_UINT_PRI = Integer.TYPE;
    private static final java.lang.Class TYPE_LONG = Long.class;
    private static final java.lang.Class TYPE_LONG_PRI = Long.TYPE;
    private static final java.lang.Class TYPE_FLOAT = Float.class;
    private static final java.lang.Class TYPE_FLOAT_PRI = Float.TYPE;
    private static final java.lang.Class TYPE_DOUBLE = Double.class;
    private static final java.lang.Class TYPE_DOUBLE_PRI = Double.TYPE;

    public static void SetObject(java.util.HashMap<Object, ScriptObject> variables, Object key, ScriptObject obj) {
        variables.put(key, obj.Assign());
    }
    public static void SetObject(java.util.HashMap<String, ScriptObject> variables, String key, ScriptObject obj) {
        variables.put(key, obj.Assign());
    }
    public static boolean IsBool(java.lang.Class type) {
        return type == TYPE_BOOL || type == TYPE_BOOL_PRI;
    }
    public static boolean IsString(java.lang.Class type) {
        return type == TYPE_STRING;
    }
    public static boolean IsDouble(java.lang.Class type) {
        return type == TYPE_DOUBLE || type == TYPE_DOUBLE_PRI;
    }
    public static boolean IsLong(java.lang.Class type) {
        return type == TYPE_LONG || type == TYPE_LONG_PRI;
    }
    public static boolean IsNumber(java.lang.Class type) {
        return (type == TYPE_SBYTE || type == TYPE_BYTE || type == TYPE_SHORT || type == TYPE_USHORT || type == TYPE_INT || 
        		type == TYPE_UINT || type == TYPE_FLOAT || type == TYPE_DOUBLE || type == TYPE_LONG ||
        		type == TYPE_SBYTE_PRI || type == TYPE_BYTE_PRI || type == TYPE_SHORT_PRI || type == TYPE_USHORT_PRI || type == TYPE_INT_PRI || 
        		type == TYPE_UINT_PRI || type == TYPE_FLOAT_PRI || type == TYPE_DOUBLE_PRI || type == TYPE_LONG_PRI);
    }
    public static boolean IsEnum(java.lang.Class type) {
        return type.isEnum();
    }
    public static boolean IsVoid(java.lang.Class type) {
        return type == TYPE_VOID;
    }
    public static boolean IsType(java.lang.Class type) {
        return type == TYPE_TYPE;
    }
    public static boolean IsBoolObject(Object obj) {
        return obj instanceof Boolean;
    }
    public static boolean IsStringObject(Object obj) {
        return obj instanceof String;
    }
    public static boolean IsDoubleObject(Object obj) {
        return obj instanceof Double;
    }
    public static boolean IsLongObject(Object obj) {
        return obj instanceof Long;
    }
    public static boolean IsNumberObject(Object obj) {
        return (obj instanceof Byte || obj instanceof Byte || obj instanceof Short || obj instanceof Short || obj instanceof Integer || obj instanceof Integer || obj instanceof Float || obj instanceof Double || obj instanceof java.math.BigDecimal || obj instanceof Long);
    }
    public static boolean IsEnumObject(Object obj) {
        return IsEnum(obj.getClass());
    }
    public static Object ChangeType(Script script, ScriptObject par, java.lang.Class<?> type) {
        if (type == TYPE_OBJECT) {
            return par.getObjectValue();
        }
        else {
            if (par instanceof ScriptUserdata && Util.IsType(type)) {
                return ((ScriptUserdata)par).getValueType();
            }
            else if (par instanceof ScriptNumber) {
                return ChangeType_impl(par.getObjectValue(), type);
            }
            else {
                return par.getObjectValue();
            }
        }
    }
    public static boolean CanChangeType(ScriptObject[] pars, java.lang.Class[] types) {
        if (pars.length != types.length) {
            return false;
        }
        for (int i = 0; i < pars.length;++i) {
            if (!CanChangeType(pars[i], types[i])) {
                return false;
            }
        }
        return true;
    }
    public static boolean CanChangeType(ScriptObject par, java.lang.Class<?> type) {
    	if (type == TYPE_OBJECT)
            return true;
        else if (IsNumber(type))
            return par instanceof ScriptNumber;
        else if (IsBool(type))
            return par instanceof ScriptBoolean;
        else if (IsEnum(type))
            return par instanceof ScriptEnum && ((ScriptEnum)par).getEnumType() == type;
        else if (par instanceof ScriptNull)
            return true;
        else if (IsString(type))
            return par instanceof ScriptString;
        else if (IsType(type))
            return par instanceof ScriptUserdata;
        else if (par instanceof ScriptUserdata)
            return type.isAssignableFrom(((ScriptUserdata)par).getValueType());
        else
            return type.isAssignableFrom(par.getClass());
    }
	public static byte[] GetFileBuffer(String fileName) throws Exception {
    	ByteArrayOutputStream output = new ByteArrayOutputStream();
		FileInputStream stream = new FileInputStream(new File(fileName));
        int n = 0;
        byte[] buffer = new byte[4096];
        while (-1 != (n = stream.read(buffer))) {
            output.write(buffer, 0, n);
        }
        stream.close();
        return output.toByteArray();
    }
    public static String GetFileString(String fileName, Charset encoding) throws Exception {
        return new String(GetFileBuffer(fileName), encoding);
    }
    public static boolean IsNullOrEmpty(String str) {
    	return str == null || str.isEmpty();
    }
    public static Object ChangeType_impl(Object value, java.lang.Class conversionType) {
    	Number num = (Number)value;
    	if (conversionType == TYPE_BYTE || conversionType == TYPE_BYTE_PRI)
	    {
    		return num.byteValue();
	    }
    	else if (conversionType == TYPE_SHORT || conversionType == TYPE_SHORT_PRI)
	    {
	        return num.shortValue();
	    }
    	else if (conversionType == TYPE_INT || conversionType == TYPE_INT_PRI)
	    {
	        return num.intValue();
	    }
    	else if (conversionType == TYPE_LONG || conversionType == TYPE_LONG_PRI)
	    {
	        return num.longValue();
	    }
    	else if (conversionType == TYPE_FLOAT || conversionType == TYPE_FLOAT_PRI)
	    {
	        return num.floatValue();
	    }
    	else if (conversionType == TYPE_DOUBLE || conversionType == TYPE_LONG_PRI)
	    {
	        return num.doubleValue();
	    }
	    return null;
    }
    public static void Assert(boolean b, Script script, String message) {
        if (!b) {
            throw new ExecutionException(script, message);
        }
    }
    public static int ToInt32(Object value)
	{
    	if (IsStringObject(value))
    		return Integer.parseInt((String)value);
    	return ((Number)value).intValue();
	}
	public static double ToDouble(Object value)
	{
    	if (IsStringObject(value))
    		return Double.parseDouble((String)value);
    	return ((Number)value).doubleValue();
	}
	public static long ToInt64(Object value)
	{
    	if (IsStringObject(value))
    		return Long.parseLong((String)value);
    	return ((Number)value).longValue();
	}
}