package Scorpio.Library;

import Scorpio.*;

public class LibraryTable {
    public static void Load(Script script) {
        ScriptTable Table = script.CreateTable();
        Table.SetValue("count", script.CreateFunction(new count()));
        script.SetObjectInternal("table", Table);
    }
    private static class count implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptTable)args[0]).Count();
        }
    }
}