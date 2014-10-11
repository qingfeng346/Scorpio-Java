package Scorpio.Library;

import java.util.Iterator;
import java.util.Map.Entry;

import Scorpio.*;
import Scorpio.Exception.*;
import Scorpio.Collections.*;

public class LibraryBasis
{
	private static class ArrayPair implements ScorpioHandle
	{
		private java.util.Iterator<ScriptObject> m_ListEnumerator;
		public ArrayPair(ScriptObject obj)
		{
			m_ListEnumerator = ((ScriptArray)obj).GetIterator();
		}
		public final Object Call(Object[] args)
		{
			if (m_ListEnumerator.hasNext())
			{
				return m_ListEnumerator.next();
			}
			return null;
		}
	}
	private static class TablePair implements ScorpioHandle
	{
		private Script m_Script;
		private Iterator<Entry<Object, ScriptObject>> m_TableEnumerator;
		public TablePair(Script script, ScriptObject obj)
		{
			m_Script = script;
			m_TableEnumerator = ((ScriptTable)obj).GetIterator();
		}
		public final Object Call(Object[] args) throws Exception
		{
			if (m_TableEnumerator.hasNext())
			{
				java.util.Map.Entry<Object, ScriptObject> v = m_TableEnumerator.next();
				ScriptTable table = new ScriptTable();
				table.SetValue("key", m_Script.CreateObject(v.getKey()));
				table.SetValue("value", v.getValue());
				return table;
			}
			return null;
		}
	}
	public static void Load(Script script)
	{
		script.SetObjectInternal("print", script.CreateFunction(new print()));
		script.SetObjectInternal("pair", script.CreateFunction(new pair(script)));
		script.SetObjectInternal("type", script.CreateFunction(new type()));
		script.SetObjectInternal("branchtype", script.CreateFunction(new branchtype()));
		script.SetObjectInternal("typeof", script.CreateFunction(new userdatatype()));
		script.SetObjectInternal("tonumber", script.CreateFunction(new tonumber(script)));
		script.SetObjectInternal("tolong", script.CreateFunction(new tolong(script)));
		script.SetObjectInternal("tostring", script.CreateFunction(new tostring(script)));
		script.SetObjectInternal("clone", script.CreateFunction(new clone()));
		script.SetObjectInternal("load_assembly", script.CreateFunction(new load_assembly(script)));
		script.SetObjectInternal("load_assembly_safe", script.CreateFunction(new load_assembly_safe(script)));
		script.SetObjectInternal("import_type", script.CreateFunction(new import_type(script)));
	}
	private static class pair implements ScorpioHandle
	{
		private Script m_script;
		public pair(Script script)
		{
			m_script = script;
		}
		public final Object Call(Object[] args)
		{
			ScriptObject obj = (ScriptObject)((args[0] instanceof ScriptObject) ? args[0] : null);
			if (obj instanceof ScriptArray)
			{
				return m_script.CreateFunction(new ArrayPair(obj));
			}
			else if (obj instanceof ScriptTable)
			{
				return m_script.CreateFunction(new TablePair(m_script, obj));
			}
			throw new ExecutionException("pair必须用语table或array类型");
		}
	}
	private static class print implements ScorpioHandle
	{
		public final Object Call(Object[] args)
		{
			for (int i = 0; i < args.length; ++i)
			{
				System.out.println(args[i].toString());
			}
			return null;
		}
	}
	private static class type implements ScorpioHandle
	{
		public final Object Call(Object[] args)
		{
			return ((ScriptObject)args[0]).getType();
		}
	}
	private static class branchtype implements ScorpioHandle
	{
		public final Object Call(Object[] args)
		{
			return ((ScriptObject)args[0]).getBranchType();
		}
	}
	private static class userdatatype implements ScorpioHandle
	{
		public final Object Call(Object[] args)
		{
			return ((ScriptUserdata)args[0]).getValueType();
		}
	}
	private static class tonumber implements ScorpioHandle
	{
		private Script m_script;
		public tonumber(Script script)
		{
			m_script = script;
		}
		public final Object Call(Object[] args)
		{
			ScriptObject obj = (ScriptObject)((args[0] instanceof ScriptObject) ? args[0] : null);
			if (obj instanceof ScriptNumber || obj instanceof ScriptString)
			{
				return m_script.CreateNumber(Util.ToDouble(obj.getObjectValue()));
			}
			throw new ExecutionException("不能从类型 " + obj.getType() + " 转换成Number类型");
		}
	}
	private static class tolong implements ScorpioHandle
	{
		private Script m_script;
		public tolong(Script script)
		{
			m_script = script;
		}
		public final Object Call(Object[] args)
		{
			ScriptObject obj = (ScriptObject)((args[0] instanceof ScriptObject) ? args[0] : null);
			if (obj instanceof ScriptNumber || obj instanceof ScriptString)
			{
				return m_script.CreateNumber(Util.ToInt64(obj.getObjectValue()));
			}
			throw new ExecutionException("不能从类型 " + obj.getType() + " 转换成Long类型");
		}
	}
	private static class tostring implements ScorpioHandle
	{
		private Script m_script;
		public tostring(Script script)
		{
			m_script = script;
		}
		public final Object Call(Object[] args)
		{
			ScriptObject obj = (ScriptObject)((args[0] instanceof ScriptObject) ? args[0] : null);
			if (obj instanceof ScriptString)
			{
				return obj;
			}
			return m_script.CreateString(obj.toString());
		}
	}
	private static class clone implements ScorpioHandle
	{
		public final Object Call(Object[] args)
		{
			return ((ScriptObject)args[0]).clone();
		}
	}
	private static class load_assembly implements ScorpioHandle
	{
		private Script m_script;
		public load_assembly(Script script)
		{
			m_script = script;
		}
		public final Object Call(Object[] args)
		{
//			ScriptString str = (ScriptString)((args[0] instanceof ScriptString) ? args[0] : null);
//			if (str == null)
//			{
//				throw new ExecutionException("load_assembly 参数必须是 string");
//			}
//			m_script.PushAssembly(Assembly.Load(str.getValue()));
			return null;
		}
	}
	private static class load_assembly_safe implements ScorpioHandle
	{
		private Script m_script;
		public load_assembly_safe(Script script)
		{
			m_script = script;
		}
		public final Object Call(Object[] args)
		{
//			try
//			{
//				ScriptString str = (ScriptString)((args[0] instanceof ScriptString) ? args[0] : null);
//				if (str == null)
//				{
//					throw new ExecutionException("load_assembly 参数必须是 string");
//				}
//				m_script.PushAssembly(Assembly.Load(str.getValue()));
//			}
//			catch (RuntimeException e)
//			{
//			}
			return null;
		}
	}
	private static class import_type implements ScorpioHandle
	{
		private Script m_script;
		public import_type(Script script)
		{
			m_script = script;
		}
		public final Object Call(Object[] args) throws Exception
		{
			ScriptString str = (ScriptString)((args[0] instanceof ScriptString) ? args[0] : null);
			if (str == null)
			{
				throw new ExecutionException("import_type 参数必须是 string");
			}
			return m_script.LoadType(str.getValue());
		}
	}
}