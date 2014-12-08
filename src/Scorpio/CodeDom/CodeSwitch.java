package Scorpio.CodeDom;

import Scorpio.*;
import Scorpio.CodeDom.Temp.*;

//switch语句
public class CodeSwitch extends CodeObject {
    public CodeObject Condition;
    public TempCase Default;
    public java.util.ArrayList<TempCase> Cases = new java.util.ArrayList<TempCase>();
    public final void AddCase(TempCase con) {
        Cases.add(con);
    }
}