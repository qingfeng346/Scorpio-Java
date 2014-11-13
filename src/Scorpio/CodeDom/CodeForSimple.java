package Scorpio.CodeDom;

import Scorpio.*;
import Scorpio.Runtime.*;

// for (i=begin,finished,step)
public class CodeForSimple extends CodeObject {
    public String Identifier;
    public CodeObject Begin;
    public CodeObject Finished;
    public CodeObject Step;
    public ScriptExecutable BlockExecutable; //for内容
    public ScriptContext BlockContext; //内容执行
    private Script m_Script; //脚本引擎
    public java.util.HashMap<String, ScriptObject> variables; //变量
    public CodeForSimple(Script script) {
        m_Script = script;
        variables = new java.util.HashMap<String, ScriptObject>();
    }
    public final void SetContextExecutable(ScriptExecutable blockExecutable) {
        BlockExecutable = blockExecutable;
        BlockContext = new ScriptContext(m_Script, blockExecutable, null, Executable_Block.For);
    }
}