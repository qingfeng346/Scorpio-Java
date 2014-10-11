package Scorpio.Exception;

//脚本异常
public class ScriptException extends RuntimeException
{
	public ScriptException(String strMessage)
	{
		super(strMessage);
	}
}