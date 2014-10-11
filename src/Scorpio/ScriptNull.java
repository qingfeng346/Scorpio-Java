﻿package Scorpio;

//脚本null类型
public class ScriptNull extends ScriptObject
{
	private static ScriptNull s_ScriptNull;
	public static ScriptNull getInstance()
	{
		if (s_ScriptNull == null)
		{
			s_ScriptNull = new ScriptNull();
		}
			return s_ScriptNull;
	}
	@Override
	public ObjectType getType()
	{
		return ObjectType.Null;
	}
	@Override
	public String toString()
	{
		return "Null";
	}
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof ScriptNull))
		{
			return false;
		}
		return true;
	}
	@Override
	public int hashCode()
	{
		return 0;
	}
}