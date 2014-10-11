package Scorpio.Exception;

import Scorpio.CodeDom.*;

//执行代码异常
public class ExecutionException extends ScriptException
{
	public ExecutionException(String strMessage)
	{
		super(strMessage);
	}
}