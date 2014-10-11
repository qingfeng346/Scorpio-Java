package Scorpio;

import Scorpio.CodeDom.*;
import Scorpio.Exception.*;

//脚本数组类型
public class ScriptArray extends ScriptObject
{
	public java.util.ArrayList<ScriptObject> m_listObject;
	@Override
	public ObjectType getType()
	{
		return ObjectType.Array;
	}
	public ScriptArray()
	{
		m_listObject = new java.util.ArrayList<ScriptObject>();
	}
	private ScriptArray(java.util.ArrayList<ScriptObject> objs)
	{
		m_listObject = objs;
	}
	@Override
	public ScriptObject GetValue(int index)
	{
		if (index < 0 || index >= m_listObject.size())
		{
			throw new ExecutionException("index is < 0 or out of count ");
		}
		return m_listObject.get(index);
	}
	@Override
	public void SetValue(int index, ScriptObject obj)
	{
		if (index < 0 || index >= m_listObject.size())
		{
			throw new ExecutionException("index is < 0 or out of count ");
		}
		m_listObject.set(index, obj);
	}
	public final void Add(ScriptObject obj)
	{
		m_listObject.add(obj);
	}
	public final void Insert(int index, ScriptObject obj)
	{
		m_listObject.add(index, obj);
	}
	public final void Clear()
	{
		m_listObject.clear();
	}
	public final int Count()
	{
		return m_listObject.size();
	}
	public final java.util.Iterator<ScriptObject> GetIterator()
	{
		return m_listObject.iterator();
	}
	@Override
	public ScriptObject clone()
	{
		java.util.ArrayList<ScriptObject> objs = new java.util.ArrayList<ScriptObject>();
		for (int i = 0; i < m_listObject.size(); ++i)
		{
			objs.add(m_listObject.get(i).clone());
		}
		return new ScriptArray(objs);
	}
	@Override
	public String toString()
	{
		return "Array";
	}
}