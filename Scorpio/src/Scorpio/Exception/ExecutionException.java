package Scorpio.Exception;

import Scorpio.*;

//执行代码异常
public class ExecutionException extends ScriptException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String m_Source = "";
    public ExecutionException(Script script, String strMessage) {
        super(strMessage);
        if (script != null) {
            StackInfo stackInfo = script.GetCurrentStackInfo();
            if (stackInfo != null) {
                m_Source = stackInfo.Breviary + ":" + stackInfo.Line + ": ";
            }
        }
    }
    public ExecutionException(Script script, ScriptObject obj, String strMessage) {
        super(strMessage);
        if (script != null) {
            StackInfo stackInfo = script.GetCurrentStackInfo();
            if (stackInfo != null) {
                m_Source = stackInfo.Breviary + ":" + stackInfo.Line + "[" + obj.getName() + "]:";
            }
        }
    }
    @Override
    public String getMessage() {
        return m_Source + super.getMessage();
    }
}