package Scorpio.CodeDom;

import Scorpio.*;
import Scorpio.Function.*;

//返回一个table类型 t = { a = "1", b = "2", function hello() { } }
public class CodeTable extends CodeObject {
    public static class TableVariable {
        public Object key;
        public CodeObject value;
        public TableVariable(Object key, CodeObject value) {
            this.key = key;
            this.value = value;
        }
    }
    public java.util.ArrayList<TableVariable> _Variables = new java.util.ArrayList<TableVariable>();
    public java.util.ArrayList<ScriptScriptFunction> _Functions = new java.util.ArrayList<ScriptScriptFunction>();
    public TableVariable[] Variables;
    public ScriptScriptFunction[] Functions;
    public final void Init() {
        Variables = _Variables.toArray(new TableVariable[]{});
        Functions = _Functions.toArray(new ScriptScriptFunction[]{});
    }
}