package Scorpio.CodeDom;

import Scorpio.Runtime.*;

//try catch 语句
public class CodeTry extends CodeObject {
    public ScriptExecutable TryExecutable; //try指令执行
    public ScriptExecutable CatchExecutable; //catch指令执行
    public ScriptExecutable FinallyExecutable; //finally指令执行
    public String Identifier; //异常对象
}