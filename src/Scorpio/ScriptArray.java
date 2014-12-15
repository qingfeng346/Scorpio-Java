package Scorpio;

import Scorpio.CodeDom.*;
import Scorpio.Exception.*;

//脚本数组类型
public class ScriptArray extends ScriptObject {
    @Override
    public ObjectType getType() {
        return ObjectType.Array;
    }
    public java.util.ArrayList<ScriptObject> m_listObject = new java.util.ArrayList<ScriptObject>();
    public ScriptArray(Script script) {
        super(script);
    }
    @Override
    public ScriptObject GetValue(int index) {
        if (index < 0 || index >= m_listObject.size()) {
            throw new ExecutionException("index is < 0 or out of count ");
        }
        return m_listObject.get(index);
    }
    @Override
    public void SetValue(int index, ScriptObject obj) {
        if (index < 0 || index >= m_listObject.size()) {
            throw new ExecutionException("index is < 0 or out of count ");
        }
        m_listObject.set(index, obj);
    }
    public final void Add(ScriptObject obj) {
        m_listObject.add(obj);
    }
    public final void Insert(int index, ScriptObject obj) {
        m_listObject.add(index, obj);
    }
    public final void Clear() {
        m_listObject.clear();
    }
    public final int Count() {
        return m_listObject.size();
    }
    public final java.util.Iterator<ScriptObject> GetIterator() {
        return m_listObject.iterator();
    }
    @Override
    public ScriptObject clone() {
        ScriptArray ret = getScript().CreateArray();
        for (int i = 0; i < m_listObject.size(); ++i) {
            ret.m_listObject.add(m_listObject.get(i).clone());
        }
        return ret;
    }
    @Override
    public String toString() {
        return "Array";
    }
    @Override
    public String ToJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < m_listObject.size();++i) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append(m_listObject.get(i).ToJson());
        }
        builder.append("]");
        return builder.toString();
    }
}