package Scorpio.CodeDom.Temp;

import Scorpio.*;
import Scorpio.Runtime.*;

/**  switch语句中一个cast条件
*/
public class TempCase {
    public java.util.ArrayList<Object> Allow; //判断条件
    public ScriptExecutable Executable; //指令列表
    public ScriptContext Context; //指令执行
    public TempCase(Script script, java.util.ArrayList<Object> allow, ScriptExecutable executable, Executable_Block block) {
        this.Allow = allow;
        this.Executable = executable;
        this.Context = new ScriptContext(script, executable, null, block);
    }
}