package Scorpio.Library;

import Scorpio.*;

public class LibraryString {
    public static void Load(Script script) {
        ScriptTable Table = script.CreateTable();
        Table.SetValue("format", script.CreateFunction(new format()));
        Table.SetValue("substring", script.CreateFunction(new substring()));
        Table.SetValue("length", script.CreateFunction(new length()));
        Table.SetValue("tolower", script.CreateFunction(new tolower()));
        Table.SetValue("toupper", script.CreateFunction(new toupper()));
        Table.SetValue("trim", script.CreateFunction(new trim()));
        Table.SetValue("replace", script.CreateFunction(new replace()));
        Table.SetValue("isnullorempty", script.CreateFunction(new isnullorempty()));
        Table.SetValue("indexof", script.CreateFunction(new indexof()));
        Table.SetValue("lastindexof", script.CreateFunction(new lastindexof()));
        Table.SetValue("startswith", script.CreateFunction(new startswith()));
        Table.SetValue("endswith", script.CreateFunction(new endswith()));
        Table.SetValue("contains", script.CreateFunction(new contains()));
        script.SetObjectInternal("string", Table);
    }
    private static final String DELIM_STR = "{}";
    private static class format implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            if (args == null || args.length == 0) {
                return null;
            }
            String messagePattern = ((ScriptString)((args[0] instanceof ScriptString) ? args[0] : null)).getValue();
            if (args.length == 1) {
                return messagePattern;
            }
            int i = 0;
            int j;
            StringBuilder sbuf = new StringBuilder();
            int length = args.length;
            int L;
            for (L = 1; L < length; L++) {
                j = messagePattern.indexOf(DELIM_STR, i);
                if (j == -1) {
                    if (i == 0) {
                        return messagePattern;
                    }
                    else {
                        sbuf.append(messagePattern.substring(i));
                        return sbuf.toString();
                    }
                }
                else {
                    sbuf.append(messagePattern.substring(i, j));
                    sbuf.append(args[L].toString());
                    i = j + 2;
                }
            }
            sbuf.append(messagePattern.substring(i));
            return sbuf.toString();
        }
    }
    private static class substring implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            String messagePattern = ((ScriptString)((args[0] instanceof ScriptString) ? args[0] : null)).getValue();
            if (args.length == 1) return messagePattern;
            if (args.length == 3)
                return messagePattern.substring(((ScriptNumber)args[1]).ToInt32(), ((ScriptNumber)args[2]).ToInt32());
            else
                return messagePattern.substring(((ScriptNumber)args[1]).ToInt32());
        }
    }
    private static class length implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptString)((args[0] instanceof ScriptString) ? args[0] : null)).getValue().length();
        }
    }
    private static class tolower implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptString)((args[0] instanceof ScriptString) ? args[0] : null)).getValue().toLowerCase();
        }
    }
    private static class toupper implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptString)((args[0] instanceof ScriptString) ? args[0] : null)).getValue().toUpperCase();
        }
    }
    private static class trim implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptString)((args[0] instanceof ScriptString) ? args[0] : null)).getValue().trim();
        }
    }
    private static class replace implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            String str = ((ScriptString)((args[0] instanceof ScriptString) ? args[0] : null)).getValue();
            String oldValue = ((ScriptString)((args[1] instanceof ScriptString) ? args[1] : null)).getValue();
            String newValue = ((ScriptString)((args[2] instanceof ScriptString) ? args[2] : null)).getValue();
            return str.replace(oldValue, newValue);
        }
    }
    private static class isnullorempty implements ScorpioHandle
    {
        public final Object Call(ScriptObject[] args)
        {
        	Object val = args[0].getObjectValue();
        	return Util.IsNullOrEmpty(val instanceof String ? (String)val : null);
        }
    }
    private static class indexof implements ScorpioHandle
    {
        public final Object Call(ScriptObject[] args)
        {
            String str = ((ScriptString)args[0]).getValue();
            String value = ((ScriptString)args[1]).getValue();
            if (args.length == 3)
                return str.indexOf(value, ((ScriptNumber)args[2]).ToInt32());
            else
                return str.indexOf(value);
        }
    }
    private static class lastindexof implements ScorpioHandle
    {
        public final Object Call(ScriptObject[] args)
        {
            String str = ((ScriptString)args[0]).getValue();
            String value = ((ScriptString)args[1]).getValue();
            if (args.length == 3)
                return str.lastIndexOf(value, ((ScriptNumber)args[2]).ToInt32());
            else
                return str.lastIndexOf(value);
        }
    }
    private static class startswith implements ScorpioHandle
    {
        public final Object Call(ScriptObject[] args)
        {
            return ((ScriptString)args[0]).getValue().startsWith(((ScriptString)args[1]).getValue());
        }
    }
    private static class endswith implements ScorpioHandle
    {
    	public final Object Call(ScriptObject[] args)
        {
        	return ((ScriptString)args[0]).getValue().endsWith(((ScriptString)args[1]).getValue());
        }
    }
    private static class contains implements ScorpioHandle
    {
    	public final Object Call(ScriptObject[] args)
        {
        	return ((ScriptString)args[0]).getValue().contains(((ScriptString)args[1]).getValue());
        }
    }
}