package Scorpio.CodeDom.Temp;

import Scorpio.*;
import Scorpio.CodeDom.*;
import Scorpio.Runtime.*;

/**  if语句中一个 if语句 
*/
public class TempCondition {
    public CodeObject Allow; //判断条件
    public ScriptExecutable Executable; //指令列表
    public ScriptContext Context; //指令执行
    public TempCondition(Script script, CodeObject allow, ScriptExecutable executable, Executable_Block block) {
        this.Allow = allow;
        this.Executable = executable;
        this.Context = new ScriptContext(script, executable, null, block);
    }
}