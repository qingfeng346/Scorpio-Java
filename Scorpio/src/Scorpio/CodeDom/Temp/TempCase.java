package Scorpio.CodeDom.Temp;

import Scorpio.*;
import Scorpio.CodeDom.*;
import Scorpio.Runtime.*;

/**  switch语句中一个cast条件
*/
public class TempCase {
    public ScriptExecutable Executable; //指令列表
    public CodeObject[] Allow; //判断条件
    public TempCase(Script script, java.util.ArrayList<CodeObject> allow, ScriptExecutable executable) {
        this.Allow = allow.toArray(new CodeObject[]{});
        this.Executable = executable;
    }
}