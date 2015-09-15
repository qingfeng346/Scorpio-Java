package Scorpio.CodeDom.Temp;

import Scorpio.*;
import Scorpio.CodeDom.*;
import Scorpio.Runtime.*;

/**  if语句中一个 if语句 
*/
public class TempCondition {
	private Script m_Script;
	private ScriptExecutable Executable; //指令列表
	private Executable_Block Block;	//指令域类型
    public CodeObject Allow; //判断条件
    public TempCondition(Script script, CodeObject allow, ScriptExecutable executable, Executable_Block block) {
    	m_Script = script;
        this.Allow = allow;
        this.Executable = executable;
        this.Block = block;
    }
    public final ScriptContext GetContext() {
        return new ScriptContext(m_Script, this.Executable, null, this.Block);
    }
}