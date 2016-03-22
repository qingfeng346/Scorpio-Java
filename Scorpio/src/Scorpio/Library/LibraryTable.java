package Scorpio.Library;

import Scorpio.*;

public class LibraryTable {
    public static void Load(Script script) {
        ScriptTable Table = script.CreateTable();
        Table.SetValue("count", script.CreateFunction(new count()));
        Table.SetValue("clear", script.CreateFunction(new clear()));
        Table.SetValue("remove", script.CreateFunction(new remove()));
        Table.SetValue("containskey", script.CreateFunction(new containskey()));
        Table.SetValue("keys", script.CreateFunction(new keys()));
        Table.SetValue("values", script.CreateFunction(new values()));
        script.SetObjectInternal("table", Table);
    }
    private static class count implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptTable)args[0]).Count();
        }
    }
    private static class clear implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            ((ScriptTable)args[0]).Clear();
            return null;
        }
    }
    private static class remove implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            ((ScriptTable)args[0]).Remove(args[1].getObjectValue());
            return null;
        }
    }
    private static class containskey implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptTable)args[0]).HasValue(args[1].getObjectValue());
        }
    }
    private static class keys implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptTable)args [0]).GetKeys();
        }
    }
    private static class values implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptTable)args [0]).GetValues();
        }
    }
}