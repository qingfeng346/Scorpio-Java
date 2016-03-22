package Scorpio.CodeDom;

import Scorpio.*;
import Scorpio.Runtime.*;

//try catch 语句
public class CodeTry extends CodeObject {
    private Script m_Script;
    public ScriptExecutable TryExecutable; //try指令执行
    public ScriptExecutable CatchExecutable; //catch指令执行
    public String Identifier; //异常对象
    public CodeTry(Script script) {
        m_Script = script;
    }
    public final ScriptContext GetTryContext() {
        return new ScriptContext(m_Script, TryExecutable);
    }
    public final ScriptContext GetCatchContext() {
        return new ScriptContext(m_Script, CatchExecutable);
    }
}