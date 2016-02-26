package Scorpio.Exception;

//脚本异常
public class ScriptException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScriptException(String strMessage) {
        super(strMessage);
    }
}