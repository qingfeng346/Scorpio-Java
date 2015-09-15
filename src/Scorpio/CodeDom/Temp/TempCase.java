package Scorpio.CodeDom.Temp;

import Scorpio.*;
import Scorpio.Runtime.*;

/**  switch语句中一个cast条件
*/
public class TempCase {
	private Script m_Script;
    public ScriptExecutable Executable; //指令列表
    private Executable_Block Block;
    public java.util.ArrayList<Object> Allow; //判断条件
    public TempCase(Script script, java.util.ArrayList<Object> allow, ScriptExecutable executable, Executable_Block block) {
        m_Script = script;
    	this.Allow = allow;
        this.Executable = executable;
        this.Block = block;
    }
    public final ScriptContext GetContext() {
        return new ScriptContext(m_Script, this.Executable, null, this.Block);
    }
}