package Scorpio.CodeDom;

import Scorpio.*;
import Scorpio.Runtime.*;

// for (i=begin,finished,step)
public class CodeForSimple extends CodeObject {
    public String Identifier;
    public CodeObject Begin;
    public CodeObject Finished;
    public CodeObject Step;
    public ScriptExecutable BlockExecutable; //for内容
}