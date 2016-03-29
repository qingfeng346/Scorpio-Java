package Scorpio.CodeDom;

import Scorpio.CodeDom.Temp.*;

//switch语句
public class CodeSwitch extends CodeObject {
    public CodeObject Condition;
    public TempCase Default;
    public TempCase[] Cases;
    public final void SetCases(java.util.ArrayList<TempCase> Cases) {
        this.Cases = Cases.toArray(new TempCase[]{});
    }
}