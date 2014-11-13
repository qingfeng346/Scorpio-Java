package Scorpio.CodeDom;

import Scorpio.Runtime.*;

//foreach 循环  foreach ( element in pairs(table)) { }
public class CodeForeach extends CodeObject {
    public String Identifier;
    public CodeObject LoopObject;
    public ScriptContext Context;
}