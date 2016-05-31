package Scorpio.Library;

import Scorpio.*;

public class LibraryFunc {
    public static void Load(Script script) {
        ScriptTable Table = script.CreateTable();
        Table.SetValue("count", script.CreateFunction(new count(script)));
        Table.SetValue("isparams", script.CreateFunction(new isparams(script)));
        Table.SetValue("isstatic", script.CreateFunction(new isstatic(script)));
        Table.SetValue("getparams", script.CreateFunction(new getparams(script)));
        script.SetObjectInternal("func", Table);
    }
    private static class count implements ScorpioHandle {
        private Script m_Script;
        public count(Script script) {
            m_Script = script;
        }
        public final Object Call(ScriptObject[] args) {
            Util.Assert(args[0] instanceof ScriptFunction, m_Script, "func.count 参数必须为 function 类型");
            return ((ScriptFunction)((args[0] instanceof ScriptFunction) ? args[0] : null)).GetParamCount();
        }
    }
    private static class isparams implements ScorpioHandle {
        private Script m_Script;
        public isparams(Script script) {
            m_Script = script;
        }
        public final Object Call(ScriptObject[] args) {
            Util.Assert(args[0] instanceof ScriptFunction, m_Script, "func.isparams 参数必须为 function 类型");
            return ((ScriptFunction)((args[0] instanceof ScriptFunction) ? args[0] : null)).IsParams();
        }
    }
    private static class isstatic implements ScorpioHandle {
        private Script m_Script;
        public isstatic(Script script) {
            m_Script = script;
        }
        public final Object Call(ScriptObject[] args) {
            Util.Assert(args[0] instanceof ScriptFunction, m_Script, "func.isstatic 参数必须为 function 类型");
            return ((ScriptFunction)((args[0] instanceof ScriptFunction) ? args[0] : null)).IsStatic();
        }
    }
    private static class getparams implements ScorpioHandle {
        private Script m_Script;
        public getparams(Script script) {
            m_Script = script;
        }
        public final Object Call(ScriptObject[] args) {
            Util.Assert(args[0] instanceof ScriptFunction, m_Script, "func.getparams 参数必须为 function 类型");
            return ((ScriptFunction)((args[0] instanceof ScriptFunction) ? args[0] : null)).GetParams();
        }
    }
}