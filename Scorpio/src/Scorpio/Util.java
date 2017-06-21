package Scorpio;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import Scorpio.Exception.*;

public final class Util {
    private static final java.lang.Class<?> TYPE_VOID = void.class;
    private static final java.lang.Class<?> TYPE_OBJECT = Object.class;
    private static final java.lang.Class<?> TYPE_TYPE = java.lang.Class.class;
    private static final java.lang.Class<?> TYPE_BOOL = Boolean.class;
    private static final java.lang.Class<?> TYPE_BOOL_PRI = Boolean.TYPE;
    private static final java.lang.Class<?> TYPE_STRING = String.class;
    private static final java.lang.Class<?> TYPE_SBYTE = Byte.class;
    private static final java.lang.Class<?> TYPE_SBYTE_PRI = Byte.TYPE;
    private static final java.lang.Class<?> TYPE_SHORT = Short.class;
    private static final java.lang.Class<?> TYPE_SHORT_PRI = Short.TYPE;
    private static final java.lang.Class<?> TYPE_INT = Integer.class;
    private static final java.lang.Class<?> TYPE_INT_PRI = Integer.TYPE;
    private static final java.lang.Class<?> TYPE_LONG = Long.class;
    private static final java.lang.Class<?> TYPE_LONG_PRI = Long.TYPE;
    private static final java.lang.Class<?> TYPE_FLOAT = Float.class;
    private static final java.lang.Class<?> TYPE_FLOAT_PRI = Float.TYPE;
    private static final java.lang.Class<?> TYPE_DOUBLE = Double.class;
    private static final java.lang.Class<?> TYPE_DOUBLE_PRI = Double.TYPE;

    public static boolean IsVoid(java.lang.Class<?> type) {
        return type == TYPE_VOID;
    }
    public static Object ChangeType(Script script, ScriptObject par, java.lang.Class<?> type) {
        if (type == TYPE_OBJECT) {
            return par.getObjectValue();
        }
        else {
            if (par instanceof ScriptUserdata && type == TYPE_TYPE) {
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
    public static boolean CanChangeType(ScriptObject par, java.lang.Class<?> type) {
        if (type == TYPE_OBJECT) {
            return true;
        }
        else if (type == TYPE_SBYTE || type == TYPE_SBYTE_PRI || type == TYPE_SHORT || type == TYPE_SHORT_PRI || 
        		 type == TYPE_INT || type == TYPE_INT_PRI || type == TYPE_LONG || type == TYPE_LONG_PRI ||
        		 type == TYPE_FLOAT || type == TYPE_FLOAT_PRI || type == TYPE_DOUBLE || type == TYPE_DOUBLE_PRI) {
            return par instanceof ScriptNumber;
        }
        else if (type == TYPE_BOOL || type == TYPE_BOOL_PRI) {
            return par instanceof ScriptBoolean;
        }
        else if (type.isEnum()) {
            return par instanceof ScriptEnum && ((ScriptEnum)par).getEnumType() == type;
        }
        else if (par instanceof ScriptNull) {
            return true;
        }
        else if (type == TYPE_STRING) {
            return par instanceof ScriptString;
        }
        else if (type == TYPE_TYPE) {
            return par instanceof ScriptUserdata;
        }
        else if (par instanceof ScriptUserdata) {
            return type.isAssignableFrom(((ScriptUserdata)par).getValueType());
        }
        else {
            return type.isAssignableFrom(par.getClass());
        }
    }
    public static void Assert(boolean b, Script script, String message) {
        if (!b) {
            throw new ExecutionException(script, message);
        }
    }
    public static void WriteString(ByteBuffer writer, String value) {
    	try {
            if (value == null || value.length() == 0)  {
            	writer.put((byte)0);
            } else {
            	writer.put(value.getBytes("utf-8"));
            	writer.put((byte)0);
            }
    	} catch (Exception e) { }
	}
    public static String ReadString(ByteBuffer reader) {
    	try	{
    		ArrayList<Byte> sb = new ArrayList<Byte>();
            byte ch;
            while ((ch = reader.get()) != 0)
            	sb.add(ch);
            byte[] bytes = new byte[sb.size()];
            for (int i=0;i<sb.size();++i)
            	bytes[i] = sb.get(i);
            return new String(bytes, "utf-8");
    	} catch (Exception e) {}
    	return "";
    }
    public static boolean IsNullOrEmpty(String value) {
        return value == null || value.length() == 0;
    }
    public static String Join(String separator, String[] stringarray) {
        int startindex = 0;
        int count = stringarray.length;
        String result = "";
        for (int index = startindex; index < count; index++) {
            if (index > startindex) {
                result += separator;
            }
            result += stringarray[index];
        }
        return result;
    }
    public static Object ChangeType_impl(Object value, java.lang.Class<?> conversionType) {
    	Number num = (Number)value;
    	if (conversionType == TYPE_SBYTE || conversionType == TYPE_SBYTE_PRI) {
    		return num.byteValue();
	    } else if (conversionType == TYPE_SHORT || conversionType == TYPE_SHORT_PRI) {
	        return num.shortValue();
	    } else if (conversionType == TYPE_INT || conversionType == TYPE_INT_PRI) {
	        return num.intValue();
	    } else if (conversionType == TYPE_LONG || conversionType == TYPE_LONG_PRI) {
	        return num.longValue();
	    } else if (conversionType == TYPE_FLOAT || conversionType == TYPE_FLOAT_PRI) {
	        return num.floatValue();
	    } else if (conversionType == TYPE_DOUBLE || conversionType == TYPE_DOUBLE_PRI) {
	        return num.doubleValue();
	    }
	    return null;
    }
    public static Object ToEnum(java.lang.Class<?> type, int value) {
    	try {
    		java.lang.reflect.Method values = type.getMethod("values");
            Object[] rets = (Object[]) values.invoke(null);
            return rets[value];
    	} catch (Exception e) { }
        return null;
    }
    public static byte ToSByte(Object value) {
    	if (value instanceof String)
    		return Byte.parseByte((String)value);
    	else if (value instanceof Enum<?>) {
    		return (byte) ((Enum<?>)value).ordinal();
    	}
    	return ((Number)value).byteValue();
    }
    public static byte ToByte(Object value) {
    	if (value instanceof String)
    		return Byte.parseByte((String)value);
    	else if (value instanceof Enum<?>) {
    		return (byte) ((Enum<?>)value).ordinal();
    	}
    	return ((Number)value).byteValue();
    }
    public static short ToInt16(Object value) {
    	if (value instanceof String)
    		return Short.parseShort((String)value);
    	else if (value instanceof Enum<?>) {
    		return (short) ((Enum<?>)value).ordinal();
    	}
    	return ((Number)value).shortValue();
    }
    public static short ToUInt16(Object value) {
    	if (value instanceof String)
    		return Short.parseShort((String)value);
    	else if (value instanceof Enum<?>) {
    		return (short) ((Enum<?>)value).ordinal();
    	}
    	return ((Number)value).shortValue();
    }
    public static int ToInt32(Object value) {
    	if (value instanceof String)
    		return Integer.parseInt((String)value);
    	else if (value instanceof Enum<?>) {
    		return (int) ((Enum<?>)value).ordinal();
    	}
    	return ((Number)value).intValue();
    }
    public static int ToUInt32(Object value) {
    	if (value instanceof String)
    		return Integer.parseInt((String)value);
    	else if (value instanceof Enum<?>) {
    		return (int) ((Enum<?>)value).ordinal();
    	}
    	return ((Number)value).intValue();
    }
    public static long ToInt64(Object value) {
    	if (value instanceof String)
    		return Long.parseLong((String)value);
    	else if (value instanceof Enum<?>) {
    		return (int) ((Enum<?>)value).ordinal();
    	}
    	return ((Number)value).longValue();
    }
    public static long ToUInt64(Object value) {
    	if (value instanceof String)
    		return Long.parseLong((String)value);
    	else if (value instanceof Enum<?>) {
    		return (int) ((Enum<?>)value).ordinal();
    	}
    	return ((Number)value).longValue();
    }
    public static float ToSingle(Object value) {
    	if (value instanceof String)
    		return Float.parseFloat((String)value);
    	else if (value instanceof Enum<?>) {
    		return (float) ((Enum<?>)value).ordinal();
    	}
    	return ((Number)value).floatValue();
    }
    public static double ToDouble(Object value) {
    	if (value instanceof String)
    		return Double.parseDouble((String)value);
    	else if (value instanceof Enum<?>) {
    		return (double) ((Enum<?>)value).ordinal();
    	}
    	return ((Number)value).doubleValue();
    }

}