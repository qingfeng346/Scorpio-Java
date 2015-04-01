package Scorpio.Exception;

import Scorpio.Script;

//执行代码异常
public class ExecutionException extends ScriptException {
	private String m_Source = "";
	public ExecutionException(Script script, String strMessage) {
		super(strMessage);
		if (script != null) {
			StackInfo stackInfo = script.GetCurrentStackInfo();
			if (stackInfo != null) m_Source = stackInfo.Breviary + ":" + stackInfo.Line + ": ";
		}
	}
	@Override
	public String getMessage() {
		return m_Source + super.getMessage();
	}
}