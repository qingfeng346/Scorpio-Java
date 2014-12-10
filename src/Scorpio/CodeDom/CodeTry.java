package Scorpio.CodeDom;

import Scorpio.Runtime.*;

//try catch 语句
public class CodeTry extends CodeObject {
    public ScriptContext TryContext; //try指令执行
    public ScriptContext CatchContext; //catch指令执行
    public String Identifier; //异常对象
}