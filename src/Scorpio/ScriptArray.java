package Scorpio;

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
    public ScriptObject GetValue(Object index) {
    	if (!(index instanceof Double || index instanceof Integer || index instanceof Long))
            throw new ExecutionException("Array GetValue只支持Number类型");
    	int i = Util.ToInt32(index);
        if (i < 0 || i >= m_listObject.size())
        	throw new ExecutionException("Array GetValue索引小于0或者超过最大值");
        return m_listObject.get(i);
    }
    @Override
    public void SetValue(Object index, ScriptObject obj) {
    	if (!(index instanceof Double || index instanceof Integer || index instanceof Long))
            throw new ExecutionException("Array SetValue只支持Number类型");
    	int i = Util.ToInt32(index);
        if (i < 0 || i >= m_listObject.size())
        	throw new ExecutionException("Array SetValue索引小于0或者超过最大值");
        m_listObject.set(i, obj);
    }
    public final void Add(ScriptObject obj) {
        m_listObject.add(obj);
    }
    public final void Insert(int index, ScriptObject obj) {
        m_listObject.add(index, obj);
    }
    public void Remove(ScriptObject obj) {
        m_listObject.remove(obj);
    }
    public void RemoveAt(int index) {
        m_listObject.remove(index);
    }
    public boolean Contains(ScriptObject obj) {
        return m_listObject.contains(obj);
    }
    public int IndexOf(ScriptObject obj) {
        return m_listObject.indexOf(obj);
    }
    public int LastIndexOf(ScriptObject obj) {
        return m_listObject.lastIndexOf(obj);
    }
    public final void Clear() {
        m_listObject.clear();
    }
    public final int Count() {
        return m_listObject.size();
    }
    public ScriptObject First() {
        if (m_listObject.size() > 0)
            return m_listObject.get(0);
        return ScriptNull.getInstance();
    }
    public ScriptObject Last() {
        if (m_listObject.size() > 0)
            return m_listObject.get(m_listObject.size() - 1);
        return ScriptNull.getInstance();
    }
    public ScriptObject Pop() {
        if (m_listObject.size() == 0)
            throw new ExecutionException("Array Pop 数组长度为0");
        ScriptObject obj = m_listObject.get(0);
        m_listObject.remove(0);
        return obj;
    }
    public ScriptObject SafePop() {
        if (m_listObject.size() == 0)
            return ScriptNull.getInstance();
        ScriptObject obj = m_listObject.get(0);
        m_listObject.remove(0);
        return obj;
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