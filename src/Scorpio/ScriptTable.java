package Scorpio;

import java.util.Iterator;
import java.util.Map;

import Scorpio.Collections.*;

//脚本table类型
public class ScriptTable extends ScriptObject
{
	@Override
	public ObjectType getType()
	{
		return ObjectType.Table;
	}
	private TableDictionary m_listObject = new TableDictionary(); //所有的数据(函数和数据都在一个数组)
	@Override
	public void SetValue(int key, ScriptObject value)
	{
		SetValue_impl(key, value);
	}
	@Override
	public ScriptObject GetValue(int key)
	{
		return GetValue_impl(key);
	}
	@Override
	public void SetValue(String key, ScriptObject value)
	{
		SetValue_impl(key, value);
	}
	@Override
	public ScriptObject GetValue(String key)
	{
		return GetValue_impl(key);
	}
	@Override
	public void SetValue(Object key, ScriptObject value)
	{
		SetValue_impl(key, value);
	}
	@Override
	public ScriptObject GetValue(Object key)
	{
		return GetValue_impl(key);
	}
	public final void SetValue_impl(Object key, ScriptObject scriptObject)
	{
		Util.SetObject(m_listObject, key, scriptObject);
	}
	public final ScriptObject GetValue_impl(Object key)
	{
		return m_listObject.containsKey(key) ? m_listObject.get(key) : ScriptNull.getInstance();
	}
	public final boolean HasValue(Object key)
	{
		return m_listObject.containsKey(key);
	}
	public final int Count()
	{
		return m_listObject.size();
	}
	public final Iterator<Map.Entry<Object, ScriptObject>> GetIterator()
	{
		return m_listObject.entrySet().iterator();
	}
	@Override
	public ScriptObject clone()
	{
		ScriptTable ret = new ScriptTable();
		ScriptObject obj = null;
		ScriptFunction func = null;
		for (Map.Entry<Object, ScriptObject> pair : m_listObject.entrySet())
		{
			obj = pair.getValue().clone();
			if (obj instanceof ScriptFunction)
			{
				func = (ScriptFunction)obj;
				if (!func.getIsStatic())
				{
					func.SetTable(ret);
				}
			}
			ret.m_listObject.put(pair.getKey(), obj);
		}
		return ret;
	}
	@Override
	public String toString()
	{
		return "Table";
	}
}