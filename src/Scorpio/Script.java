package Scorpio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import Scorpio.Collections.*;
import Scorpio.Runtime.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;
import Scorpio.Library.*;
import Scorpio.Userdata.*;
import Scorpio.Variable.*;

//脚本类
public class Script
{
	private static final String Version = "0.0.1beta";
	private static final String GLOBAL_TABLE = "_G"; //全局table
	private static final String GLOBAL_VERSION = "_VERSION"; //版本号
	private IScriptUserdataFactory m_UserdataFactory = null; //Userdata工厂
	private ScriptTable m_GlobalTable = new ScriptTable(); //全局Table
	private java.util.ArrayList<StackInfo> m_StackInfoStack = new java.util.ArrayList<StackInfo>(); //堆栈数据
	private StackInfo m_StackInfo = new StackInfo(); //最近堆栈数据
	public Script()
	{
		m_GlobalTable.SetValue(GLOBAL_TABLE, m_GlobalTable);
		m_GlobalTable.SetValue(GLOBAL_VERSION, CreateString(Version));
	}
	public final ScriptObject LoadFile(String strFileName) throws IOException, Exception
	{
		return LoadFile(strFileName, "UTF8");
	}
	public final ScriptObject LoadFile(String strFileName, String charsetName) throws IOException, Exception
	{
		try {
			return LoadString(strFileName, Util.GetFileString(strFileName, charsetName));
		} catch (RuntimeException e) {
			throw new ScriptException("load file [" + strFileName + "] is error : " + e.toString());
		}
	}
	public final ScriptObject LoadString(String strBuffer) throws Exception
	{
		return LoadString("", strBuffer);
	}
	public final ScriptObject LoadString(String strBreviary, String strBuffer) throws Exception
	{
		return LoadString(strBreviary, strBuffer, null);
	}
	public final ScriptObject LoadString(String strBreviary, String strBuffer, ScriptContext context) throws Exception
	{
		try
		{
			m_StackInfoStack.clear();
			ScriptLexer scriptLexer = new ScriptLexer(strBuffer);
			strBreviary = (strBreviary == null || strBreviary.isEmpty()) ? scriptLexer.GetBreviary() : strBreviary;
			ScriptParser scriptParser = new ScriptParser(this, scriptLexer.GetTokens(), strBreviary);
			ScriptExecutable scriptExecutable = scriptParser.Parse();
			return new ScriptContext(this, scriptExecutable, context, Executable_Block.Context).Execute();
		}
		catch (RuntimeException e)
		{
			throw new ScriptException("load buffer [" + strBreviary + "] is error : " + e.toString());
		}
	}
	public final ScriptObject LoadType(String str) throws Exception
	{
		Class<?> clazz = Class.forName(str);
		return clazz != null ? CreateUserdata(clazz) : ScriptNull.getInstance();
	}
	public final void SetStackInfo(StackInfo info)
	{
		m_StackInfo = info;
	}
	public final void PushStackInfo()
	{
		m_StackInfoStack.add(m_StackInfo);
	}
	public final String GetStackInfo()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Source [ " + m_StackInfo.Breviary + "] Line [" + m_StackInfo.Line + "]\n");
		for (int i = m_StackInfoStack.size() - 1; i >= 0;--i)
		{
			builder.append("        Source [" + m_StackInfoStack.get(i).Breviary + "] Line [" + m_StackInfoStack.get(i).Line + "]\n");
		}
		return builder.toString();
	}
	public final boolean HasValue(String key)
	{
		return m_GlobalTable.HasValue(key);
	}
	public final ScriptObject GetValue(String key)
	{
		return m_GlobalTable.GetValue(key);
	}
	public final void SetObject(String key, Object value) throws Exception
	{
		m_GlobalTable.SetValue(key, CreateObject(value));
	}
	public final void SetObjectInternal(String key, ScriptObject value)
	{
		m_GlobalTable.SetValue(key, value);
	}
	public final ScriptObject Call(String strName, Object... args) throws Exception
	{
		ScriptObject obj = m_GlobalTable.GetValue(strName);
		if (obj instanceof ScriptNull)
		{
			throw new ScriptException("找不到变量[" + strName + "]");
		}
		int length = args.length;
		ScriptObject[] parameters = new ScriptObject[length];
		for (int i = 0; i < length;++i)
		{
			parameters[i] = CreateObject(args[i]);
		}
		m_StackInfoStack.clear();
		return obj.Call(parameters);
	}
	public final ScriptObject CreateObject(Object value) throws Exception
	{
		if (value == null)
		{
			return ScriptNull.getInstance();
		}
		else if (value instanceof ScriptObject)
		{
			return (ScriptObject)value;
		}
		else if (value instanceof ScorpioHandle)
		{
			return CreateFunction((ScorpioHandle)value);
		}
		else if (value instanceof ScorpioMethod)
		{
			return CreateFunction((ScorpioMethod)value);
		}
		else if (Util.IsBoolObject(value))
		{
			return CreateBool(((Boolean)value).booleanValue());
		}
		else if (Util.IsStringObject(value))
		{
			return CreateString((String)value);
		}
		else if (Util.IsNumberObject(value))
		{
			return CreateNumber(value);
		}
		else if (Util.IsEnumObject(value))
		{
			return CreateEnum(value);
		}
		return CreateUserdata(value);
	}
	public final ScriptBoolean CreateBool(boolean value)
	{
		return ScriptBoolean.Get(value);
	}
	public final ScriptString CreateString(String value)
	{
		return new ScriptString(this, value);
	}
	public final ScriptNumber CreateNumber(Object value)
	{
		if (Util.IsLongObject(value))
		{
			return new ScriptNumberLong(this, ((Long)value).longValue());
		}
		else if (Util.IsDoubleObject(value))
		{
			return new ScriptNumberDouble(this, ((Double)value).doubleValue());
		}
		return new ScriptNumberDouble(this, Util.ToDouble(value));
	}
	public final ScriptEnum CreateEnum(Object value)
	{
		return new ScriptEnum(this, value);
	}
	public final ScriptUserdata CreateUserdata(Object value) throws Exception
	{
		return m_UserdataFactory.create(this, value);
	}
	public final ScriptFunction CreateFunction(String name, ScorpioScriptFunction value)
	{
		return new ScriptFunction(this, name, value);
	}
	public final ScriptFunction CreateFunction(ScorpioHandle value)
	{
		return new ScriptFunction(this, value);
	}
	public final ScriptFunction CreateFunction(ScorpioMethod value)
	{
		return new ScriptFunction(this, value);
	}
	public final void LoadLibrary()
	{
		m_UserdataFactory = new DefaultScriptUserdataFactory();
		LibraryBasis.Load(this);
		LibraryArray.Load(this);
		LibraryString.Load(this);
		LibraryTable.Load(this);
	}
}