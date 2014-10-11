package Scorpio.CodeDom;

import Scorpio.*;

//返回一个继承ScriptObject的变量
public class CodeScriptObject extends CodeObject
{
	public CodeScriptObject(Object obj)
	{
		setObject(obj);
	}
	private Object privateObject;
	public final Object getObject()
	{
		return privateObject;
	}
	private void setObject(Object value)
	{
		privateObject = value;
	}
}