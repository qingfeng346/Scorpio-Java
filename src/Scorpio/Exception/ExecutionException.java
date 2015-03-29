package Scorpio.Exception;

import Scorpio.Script;

//执行代码异常
public class ExecutionException extends ScriptException {
	private String m_Source = "";
	public ExecutionException(String strMessage) {
        super(strMessage);
    }
	public ExecutionException(Script script, String strMessage) {
		super(strMessage);
		StackInfo stackInfo = script.GetCurrentStackInfo();
		m_Source = stackInfo.Breviary + ":" + stackInfo.Line + " : ";
	}
	@Override
	public String getMessage() {
		return m_Source + super.getMessage();
	}
}