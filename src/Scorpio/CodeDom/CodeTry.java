package Scorpio.CodeDom;

import Scorpio.Runtime.*;

public class CodeTry extends CodeObject {
    public ScriptContext TryContext; //try指令执行
    public ScriptContext CatchContext; //catch指令执行
    public String Identifier; //异常对象
}