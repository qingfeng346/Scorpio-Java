package Scorpio.Library;

import Scorpio.*;
import Scorpio.Userdata.*;

public class LibraryUserdata {
    public static void Load(Script script) {
        ScriptTable Table = script.CreateTable();
        Table.SetValue("rename", script.CreateFunction(new rename(script)));
        script.SetObjectInternal("userdata", Table);
    }
    private static class rename implements ScorpioHandle {
        private Script m_Script;
        public rename(Script script) {
            m_Script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject type = args[0];
            if (type instanceof ScriptUserdataObject || type instanceof ScriptUserdataObjectType) {
                m_Script.GetScorpioType(((ScriptUserdata)type).getValueType()).Rename(args[1].toString(), args[2].toString());
            }
            return null;
        }
    }
}