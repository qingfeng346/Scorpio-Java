package Scorpio.Exception;

//词法分析程序
public class LexerException extends ScriptException
{
	public LexerException(String strMessage, int iSourceLine)
	{
		super(" Line:" + (iSourceLine+1) + "    " + strMessage);
	}
}