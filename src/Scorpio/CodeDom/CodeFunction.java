package Scorpio.CodeDom;

import Scorpio.*;

//返回一个内部函数  内部函数会继承父函数的所有临时变量  function t1() {  return function t2() {}  }
public class CodeFunction extends CodeObject {
    public ScriptFunction Func;
    public CodeFunction(ScriptFunction func) {
        this.Func = func;
    }
}