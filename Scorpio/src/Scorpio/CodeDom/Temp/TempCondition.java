package Scorpio.CodeDom.Temp;

import Scorpio.CodeDom.*;
import Scorpio.Runtime.*;

/**  if语句中一个 if语句 
*/
public class TempCondition {
    public ScriptExecutable Executable; //指令列表
    public Executable_Block Block = Executable_Block.forValue(0); //指令域类型
    public CodeObject Allow; //判断条件
    public TempCondition(CodeObject allow, ScriptExecutable executable, Executable_Block block) {
        this.Allow = allow;
        this.Executable = executable;
        this.Block = block;
    }
}