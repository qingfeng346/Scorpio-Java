package Scorpio.Exception;

import Scorpio.Compiler.*;

//解析语法异常
public class ParserException extends ScriptException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParserException(String strMessage, Token token) {
        super(" Line:" + token.getSourceLine() + "  Column:" + token.getSourceChar() + "  Type:" + token.getType() + "  value[" + token.getLexeme().toString() + "]    " + strMessage);
    }
}