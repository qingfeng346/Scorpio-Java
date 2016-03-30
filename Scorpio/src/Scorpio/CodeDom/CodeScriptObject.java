package Scorpio.CodeDom;

import Scorpio.*;

//返回一个继承ScriptObject的变量
public class CodeScriptObject extends CodeObject {
    public CodeScriptObject(Script script, Object obj) {
        setObject(script.CreateObject(obj));
        getObject().setName("" + obj);
    }
    public CodeScriptObject(Script script, Object obj, String breviary, int line) {
        super(breviary, line);
        setObject(script.CreateObject(obj));
        getObject().setName("" + obj);
    }
    private ScriptObject privateObject;
    public final ScriptObject getObject() {
        return privateObject;
    }
    private void setObject(ScriptObject value) {
        privateObject = value;
    }
}