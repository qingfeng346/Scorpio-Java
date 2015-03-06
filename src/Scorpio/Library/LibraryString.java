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
            if (args.length == 1) {
                return messagePattern;
            }
            int index = ((ScriptNumber)((args[1] instanceof ScriptNumber) ? args[1] : null)).ToInt32();
            int length = ((ScriptNumber)((args[2] instanceof ScriptNumber) ? args[2] : null)).ToInt32();
            return messagePattern.substring(index, index + length);
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
}