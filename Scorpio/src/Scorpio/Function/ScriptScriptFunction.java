package Scorpio.Function;

import Scorpio.*;
import Scorpio.Runtime.*;
import Scorpio.Variable.*;
import Scorpio.Exception.*;

public class ScriptScriptFunction extends ScriptFunction {
    private ScorpioScriptFunction m_ScriptFunction; //脚本函数
    private ScriptContext m_ParentContext; //父级堆栈
    private boolean m_IsStaticFunction; //是否是静态函数(不是table内部函数)
    private java.util.HashMap<String, ScriptObject> m_stackObject = new java.util.HashMap<String, ScriptObject>(); //函数变量
    public final boolean getIsStaticFunction() {
        return m_IsStaticFunction;
    }
    public ScriptScriptFunction(Script script, String name, ScorpioScriptFunction function) {
        super(script, name);
        this.m_IsStaticFunction = true;
        this.m_ScriptFunction = function;
    }
    @Override
    public int GetParamCount() {
        return m_ScriptFunction.GetParameterCount();
    }
    @Override
    public boolean IsParams() {
        return m_ScriptFunction.IsParams();
    }
    @Override
    public boolean IsStatic() {
        return m_IsStaticFunction;
    }
    @Override
    public ScriptArray GetParams() {
        return m_ScriptFunction.GetParameters();
    }
    @Override
    public void SetValue(Object key, ScriptObject value) {
        if (!(key instanceof String)) {
            throw new ExecutionException(this.m_Script, this, "Function SetValue只支持String类型 key值为:" + key);
        }
        m_stackObject.put((String)key, value);
    }
    @Override
    public ScriptObject GetValue(Object key) {
        if (!(key instanceof String)) {
            throw new ExecutionException(this.m_Script, this, "Function GetValue只支持String类型 key值为:" + key);
        }
        String skey = (String)key;
        return m_stackObject.containsKey(skey) ? m_stackObject.get(skey) : m_Script.getNull();
    }
    public final void SetTable(ScriptTable table) {
        m_IsStaticFunction = false;
        m_stackObject.put("this", table);
        m_stackObject.put("self", table);
    }
    public final ScriptScriptFunction SetParentContext(ScriptContext context) {
        m_ParentContext = context;
        return this;
    }
    public final ScriptScriptFunction Create() {
        ScriptScriptFunction ret = new ScriptScriptFunction(m_Script, getName(), m_ScriptFunction);
        ret.m_IsStaticFunction = getIsStaticFunction();
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