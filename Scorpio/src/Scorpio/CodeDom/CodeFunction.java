package Scorpio.CodeDom;

import Scorpio.Function.*;

//返回一个内部函数  内部函数会继承父函数的所有临时变量  function t1() {  return function t2() {}  }
public class CodeFunction extends CodeObject {
    public ScriptScriptFunction Func;
    public CodeFunction(ScriptScriptFunction func) {
        this.Func = func;
    }
    public CodeFunction(ScriptScriptFunction func, String breviary, int line) {
        super(breviary, line);
        this.Func = func;
    }
}