package Scorpio.CodeDom;

import Scorpio.*;

//返回一个table类型 t = { a = "1", b = "2", function hello() { } }
public class CodeTable extends CodeObject {
    public java.util.ArrayList<TableVariable> Variables = new java.util.ArrayList<TableVariable>();
    public java.util.ArrayList<ScriptFunction> Functions = new java.util.ArrayList<ScriptFunction>();
}