package Scorpio.Exception;

import Scorpio.Compiler.*;

//解析语法异常
public class ParserException extends ScriptException
{
	public ParserException(String strMessage)
	{
		super(strMessage);
	}
	public ParserException(String strMessage, Token token)
	{
		super(" Line:" + token.getSourceLine() + "  Column:" + token.getSourceChar() + "  value[" + token.getLexeme().toString() + "]    " + strMessage);
	}
}