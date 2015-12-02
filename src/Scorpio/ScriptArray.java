package Scorpio;

import java.util.Collections;
import java.util.Comparator;

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
            throw new ExecutionException(getScript(), "Array GetValue只支持Number类型 name = " + getName() + " index = " + index);
    	int i = Util.ToInt32(index);
        if (i < 0 || i >= m_listObject.size())
        	throw new ExecutionException(getScript(), "Array GetValue索引小于0或者超过最大值 name = " + getName() + " index = " + index);
        return m_listObject.get(i);
    }
    @Override
    public void SetValue(Object index, ScriptObject obj) {
    	if (!(index instanceof Double || index instanceof Integer || index instanceof Long))
            throw new ExecutionException(getScript(), "Array SetValue只支持Number类型 name = " + getName() + " index = " + index);
    	int i = Util.ToInt32(index);
        if (i < 0 || i >= m_listObject.size())
        	throw new ExecutionException(getScript(), "Array SetValue索引小于0或者超过最大值 name = " + getName() + " index = " + index);
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
    public final void Sort(final ScriptFunction func) {
    	Collections.sort(m_listObject, new Comparator<ScriptObject>() {
			@Override
			public int compare(ScriptObject o1, ScriptObject o2) {
				try {
					Object ret = func.Call(new ScriptObject[] { o1, o2 });
					if (!(ret instanceof ScriptNumber)) throw new Exception("Sort 返回值 必须是Number类型");
					return ((ScriptNumber)ret).ToInt32();
				} catch (Exception e1) {
					throw new ExecutionException(getScript(), "Sort出错:" + e1.getMessage());
				}
			}
		});
    }
    public ScriptObject First() {
        if (m_listObject.size() > 0)
            return m_listObject.get(0);
        return getScript().Null;
    }
    public ScriptObject Last() {
        if (m_listObject.size() > 0)
            return m_listObject.get(m_listObject.size() - 1);
        return getScript().Null;
    }
    public ScriptObject PopFirst() {
        if (m_listObject.size() == 0)
            throw new ExecutionException(getScript(), "Array Pop 数组长度为0");
        ScriptObject obj = m_listObject.get(0);
        m_listObject.remove(0);
        return obj;
    }
    public ScriptObject SafePopFirst() {
        if (m_listObject.size() == 0)
            return getScript().Null;
        ScriptObject obj = m_listObject.get(0);
        m_listObject.remove(0);
        return obj;
    }
    public ScriptObject PopLast()
    {
        if (m_listObject.size() == 0)
            throw new ExecutionException(getScript(), "Array Pop 数组长度为0");
        int index = m_listObject.size() - 1;
        ScriptObject obj = m_listObject.get(index);
        m_listObject.remove(index);
        return obj;
    }
    public ScriptObject SafePopLast()
    {
        if (m_listObject.size() == 0)
            return getScript().Null;
        int index = m_listObject.size() - 1;
        ScriptObject obj = m_listObject.get(index);
        m_listObject.remove(index);
        return obj;
    }
    
    public final java.util.Iterator<ScriptObject> GetIterator() {
        return m_listObject.iterator();
    }
    public ScriptObject[] ToArray()
    {
        return m_listObject.toArray(new ScriptObject[0]);
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