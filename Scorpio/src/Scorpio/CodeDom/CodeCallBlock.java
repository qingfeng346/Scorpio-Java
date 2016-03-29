package Scorpio.CodeDom;

import Scorpio.Runtime.*;

public class CodeCallBlock extends CodeObject {
    public ScriptExecutable Executable;
    public CodeCallBlock(ScriptExecutable executable) {
        this.Executable = executable;
    }
}