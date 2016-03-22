package Scorpio.CodeDom;

import Scorpio.*;

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
    public java.util.ArrayList<TableVariable> Variables = new java.util.ArrayList<TableVariable>();
    public java.util.ArrayList<ScriptFunction> Functions = new java.util.ArrayList<ScriptFunction>();
}