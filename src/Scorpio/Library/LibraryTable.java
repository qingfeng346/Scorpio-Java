package Scorpio.Library;

import Scorpio.*;

public class LibraryTable
{
	public static ScriptTable Table = new ScriptTable();
	public static void Load(Script script)
	{
		Table.SetValue("count", script.CreateFunction(new count()));
		script.SetObjectInternal("table", Table);
	}
	private static class count implements ScorpioHandle
	{
		public final Object Call(Object[] args)
		{
			return ((ScriptTable)args[0]).Count();
		}
	}
}