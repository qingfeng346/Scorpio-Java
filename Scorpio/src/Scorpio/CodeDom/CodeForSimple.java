package Scorpio.CodeDom;

import Scorpio.*;
import Scorpio.Runtime.*;

// for (i=begin,finished,step)
public class CodeForSimple extends CodeObject {
	private Script m_Script; //脚本引擎
    public String Identifier;
    public CodeObject Begin;
    public CodeObject Finished;
    public CodeObject Step;
    public ScriptExecutable BlockExecutable; //for内容
    public CodeForSimple(Script script) {
        m_Script = script;
    }
    public final ScriptContext GetBlockContext() {
        return new ScriptContext(m_Script, BlockExecutable, null, Executable_Block.For);
    }
}