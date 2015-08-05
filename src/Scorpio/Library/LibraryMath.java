package Scorpio.Library;

import Scorpio.Script;
import Scorpio.ScriptTable;

public class LibraryMath {
    public static void Load(Script script) {
        ScriptTable Table = script.CreateTable();
        script.SetObjectInternal("math", Table);
    }
}
