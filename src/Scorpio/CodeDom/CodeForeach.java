package Scorpio.CodeDom;

import Scorpio.*;
import Scorpio.Runtime.*;

//foreach 循环  foreach ( element in pairs(table)) { }
public class CodeForeach extends CodeObject {
	private Script m_Script;                            //脚本引擎
    public String Identifier;
    public CodeObject LoopObject;
    public ScriptExecutable BlockExecutable;
    public CodeForeach(Script script) {
        m_Script = script;
    }
    public final ScriptContext GetBlockContext() {
        return new ScriptContext(m_Script, BlockExecutable, null, Executable_Block.Foreach);
    }
}