package Scorpio.CodeDom;

import Scorpio.*;

//返回一个继承ScriptObject的变量
public class CodeScriptObject extends CodeObject {
    public ScriptObject Object;
    public CodeScriptObject(Script script, Object obj) {
        Object = script.CreateObject(obj);
        Object.setName("" + obj);
    }
}