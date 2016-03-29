package Scorpio.Function;

import Scorpio.*;
import Scorpio.Runtime.*;
import Scorpio.Variable.*;
import Scorpio.Exception.*;

public class ScriptScriptFunction extends ScriptFunction {
    private ScorpioScriptFunction m_ScriptFunction; //脚本函数
    private ScriptContext m_ParentContext; //父级堆栈
    private java.util.HashMap<String, ScriptObject> m_stackObject = new java.util.HashMap<String, ScriptObject>(); //函数变量
    private boolean privateIsStatic;
    public final boolean getIsStatic() {
        return privateIsStatic;
    }
    private void setIsStatic(boolean value) {
        privateIsStatic = value;
    }
    public ScriptScriptFunction(Script script, String name, ScorpioScriptFunction function) {
        super(script, name);
        this.setIsStatic(true);
        this.m_ScriptFunction = function;
    }
    @Override
    public void SetValue(Object key, ScriptObject value) {
        if (!(key instanceof String)) {
            throw new ExecutionException(this.getScript(), "Function SetValue只支持String类型 key值为:" + key);
        }
        m_stackObject.put((String)key, value);
    }
    @Override
    public ScriptObject GetValue(Object key) {
        if (!(key instanceof String)) {
            throw new ExecutionException(this.getScript(), "Function GetValue只支持String类型 key值为:" + key);
        }
        String skey = (String)key;
        return m_stackObject.containsKey(skey) ? m_stackObject.get(skey) : getScript().getNull();
    }
    public final void SetTable(ScriptTable table) {
        setIsStatic(false);
        m_stackObject.put("this", table);
        m_stackObject.put("self", table);
    }
    public final ScriptScriptFunction SetParentContext(ScriptContext context) {
        m_ParentContext = context;
        return this;
    }
    public final ScriptScriptFunction Create() {
        ScriptScriptFunction ret = new ScriptScriptFunction(getScript(), getName(), m_ScriptFunction);
        ret.setIsStatic(getIsStatic());
        return ret;
    }
    @Override
    public Object Call(ScriptObject[] parameters) {
        return m_ScriptFunction.Call(m_ParentContext, m_stackObject, parameters);
    }
    @Override
    public ScriptObject clone() {
        return Create();
    }
}