package Scorpio;

import Scorpio.Function.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;

//脚本table类型
public class ScriptTable extends ScriptObject {
    @Override
    public ObjectType getType() {
        return ObjectType.Table;
    }
    private java.util.HashMap<Object, ScriptObject> m_listObject = new java.util.HashMap<Object, ScriptObject>(); //所有的数据(函数和数据都在一个数组)
    public ScriptTable(Script script) {
        super(script);
    }
    @Override
    public void SetValue(Object key, ScriptObject value) {
        m_listObject.put(key, value.Assign());
    }
    @Override
    public ScriptObject GetValue(Object key) {
        return m_listObject.containsKey(key) ? m_listObject.get(key) : m_Script.getNull();
    }
    @Override
    public ScriptObject AssignCompute(TokenType type, ScriptObject value) {
        if (type != TokenType.AssignPlus) {
            return super.AssignCompute(type, value);
        }
        ScriptTable table = (ScriptTable)((value instanceof ScriptTable) ? value : null);
        if (table == null) {
            throw new ExecutionException(m_Script, this, "table [+=] 操作只限两个[table]之间,传入数据类型:" + value.getType());
        }
        ScriptObject obj = null;
        ScriptScriptFunction func = null;
        for (java.util.Map.Entry<Object, ScriptObject> pair : table.m_listObject.entrySet()) {
            obj = pair.getValue().clone();
            if (obj instanceof ScriptScriptFunction) {
                func = (ScriptScriptFunction)obj;
                if (!func.getIsStaticFunction()) {
                    func.SetTable(this);
                }
            }
            m_listObject.put(pair.getKey(), obj);
        }
        return this;
    }
    @Override
    public ScriptObject Compute(TokenType type, ScriptObject value) {
        if (type != TokenType.Plus) {
            return super.Compute(type, value);
        }
        ScriptTable table = (ScriptTable)((value instanceof ScriptTable) ? value : null);
        if (table == null) {
            throw new ExecutionException(m_Script, this, "table [+] 操作只限两个[table]之间,传入数据类型:" + value.getType());
        }
        ScriptTable ret = m_Script.CreateTable();
        ScriptObject obj = null;
        ScriptScriptFunction func = null;
        for (java.util.Map.Entry<Object, ScriptObject> pair : m_listObject.entrySet()) {
            obj = pair.getValue().clone();
            if (obj instanceof ScriptScriptFunction) {
                func = (ScriptScriptFunction)obj;
                if (!func.getIsStaticFunction()) {
                    func.SetTable(ret);
                }
            }
            ret.m_listObject.put(pair.getKey(), obj);
        }
        for (java.util.Map.Entry<Object, ScriptObject> pair : table.m_listObject.entrySet()) {
            obj = pair.getValue().clone();
            if (obj instanceof ScriptScriptFunction) {
                func = (ScriptScriptFunction)obj;
                if (!func.getIsStaticFunction()) {
                    func.SetTable(ret);
                }
            }
            ret.m_listObject.put(pair.getKey(), obj);
        }
        return ret;
    }
    public final boolean HasValue(Object key) {
        if (key == null) {
            return false;
        }
        return m_listObject.containsKey(key);
    }
    public final int Count() {
        return m_listObject.size();
    }
    public final void Clear() {
        m_listObject.clear();
    }
    public final void Remove(Object key) {
        m_listObject.remove(key);
    }
    public final ScriptArray GetKeys() {
        ScriptArray ret = m_Script.CreateArray();
        for (java.util.Map.Entry<Object, ScriptObject> pair : m_listObject.entrySet()) {
            ret.Add(m_Script.CreateObject(pair.getKey()));
        }
        return ret;
    }
    public final ScriptArray GetValues() {
        ScriptArray ret = m_Script.CreateArray();
        for (java.util.Map.Entry<Object, ScriptObject> pair : m_listObject.entrySet()) {
            ret.Add(pair.getValue().Assign());
        }
        return ret;
    }
    public final java.util.Iterator<java.util.Map.Entry<Object, ScriptObject>> GetIterator() {
        return m_listObject.entrySet().iterator();
    }
    @Override
    public ScriptObject clone() {
        ScriptTable ret = m_Script.CreateTable();
        ScriptObject obj = null;
        ScriptScriptFunction func = null;
        for (java.util.Map.Entry<Object, ScriptObject> pair : m_listObject.entrySet()) {
            if (pair.getValue() == this) {
                ret.m_listObject.put(pair.getKey(), ret);
            }
            else {
                obj = pair.getValue().clone();
                if (obj instanceof ScriptScriptFunction) {
                    func = (ScriptScriptFunction)obj;
                    if (!func.getIsStaticFunction()) {
                        func.SetTable(ret);
                    }
                }
                ret.m_listObject.put(pair.getKey(), obj);
            }
        }
        return ret;
    }
    @Override
    public String toString() {
        return "Table";
    }
    @Override
    public String ToJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        boolean first = true;
        for (java.util.Map.Entry<Object, ScriptObject> pair : m_listObject.entrySet()) {
            if (first) {
                first = false;
            }
            else {
                builder.append(",");
            }
            builder.append("\"");
            builder.append(pair.getKey());
            builder.append("\":");
            builder.append(pair.getValue().ToJson());
        }
        builder.append("}");
        return builder.toString();
    }
}