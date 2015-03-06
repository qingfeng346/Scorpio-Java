package Scorpio.Library;
import Scorpio.*;
public class LibraryJson {
    public static void Load(Script script) {
        ScriptTable Table = script.CreateTable();
        Table.SetValue("encode", script.CreateFunction(new encode()));
        Table.SetValue("decode", script.CreateFunction(new decode(script)));
        script.SetObjectInternal("json", Table);
    }
    private static class encode implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0].ToJson();
        }
    }
    private static class decode implements ScorpioHandle {
        private Script m_Script;
        public decode(Script script) {
            m_Script = script;
        }
        public final Object Call(ScriptObject[] args) {
            try {
				return m_Script.LoadString(null, "return " + args[0].toString(), null, false);
			} catch (Exception e) {
				return null;
			}
        }
    }
}