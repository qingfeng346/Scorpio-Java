package Scorpio;

import Scorpio.CodeDom.*;
import Scorpio.Exception.*;

//脚本数据类型
public abstract class ScriptObject
{
	private static final ScriptObject[] NOPARAMETER = new ScriptObject[0]; // 没有参数
	public ScriptObject Assign() // 赋值
	{
		return this;
	}
	public ScriptObject Plus(ScriptObject obj) // +=
	{
		return null;
	}
	public ScriptObject Minus(ScriptObject obj) // -=
	{
		return null;
	}
	public ScriptObject Multiply(ScriptObject obj) // *=
	{
		return null;
	}
	public ScriptObject Divide(ScriptObject obj) // /=
	{
		return null;
	}
	public ScriptObject Modulo(ScriptObject obj) // %=
	{
		return null;
	}
	//设置变量
	public void SetValue(int key, ScriptObject value)
	{
		throw new ExecutionException("类型[" + getType() + "]不支持获取变量(int)");
	}
	//获取变量
	public ScriptObject GetValue(int key)
	{
		throw new ExecutionException("类型[" + getType() + "]不支持设置变量(int)");
	}
	//设置变量
	public void SetValue(String key, ScriptObject value) throws Exception
	{
		throw new ExecutionException("类型[" + getType() + "]不支持获取变量(string)");
	}
	//获取变量
	public ScriptObject GetValue(String key) throws Exception
	{
		throw new ExecutionException("类型[" + getType() + "]不支持设置变量(string)");
	}
	//设置变量
	public void SetValue(Object key, ScriptObject value)
	{
		throw new ExecutionException("类型[" + getType() + "]不支持获取变量(object)");
	}
	//获取变量
	public ScriptObject GetValue(Object key) throws Exception
	{
		throw new ExecutionException("类型[" + getType() + "]不支持设置变量(object)");
	}
	//调用无参函数
	public final ScriptObject Call() throws Exception
	{
		return Call(NOPARAMETER);
	}
	//调用函数
	public ScriptObject Call(ScriptObject[] parameters) throws Exception
	{
		throw new ExecutionException("类型[" + getType() + "]不支持函数调用");
	}
	public ScriptObject clone() // 复制一个变量
	{
		return this;
	}
	public abstract ObjectType getType();
	public int getBranchType()
	{
		return 0;
	}
	public Object getObjectValue()
	{
		return null;
	}
	public final boolean getIsPrimitive()
	{
		return getIsBoolean() || getIsNumber() || getIsString();
	}
	public final boolean getIsNull()
	{
		return (getType() == ObjectType.Null);
	}
	public final boolean getIsBoolean()
	{
		return (getType() == ObjectType.Boolean);
	}
	public final boolean getIsNumber()
	{
		return (getType() == ObjectType.Number);
	}
	public final boolean getIsString()
	{
		return (getType() == ObjectType.String);
	}
	public final boolean getIsFunction()
	{
		return (getType() == ObjectType.Function);
	}
	public final boolean getIsArray()
	{
		return (getType() == ObjectType.Array);
	}
	public final boolean getIsTable()
	{
		return (getType() == ObjectType.Table);
	}
	public final boolean getIsEnum()
	{
		return (getType() == ObjectType.Enum);
	}
	public final boolean getIsUserData()
	{
		return (getType() == ObjectType.UserData);
	}
}