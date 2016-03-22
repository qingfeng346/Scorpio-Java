package Scorpio.CodeDom;

import Scorpio.CodeDom.Temp.*;

//if语句  if(true) {} elseif () {} else {}
public class CodeIf extends CodeObject {
    public TempCondition If;
    public TempCondition Else;
    public TempCondition[] ElseIf;
    public int ElseIfCount;
    public final void Init(java.util.ArrayList<TempCondition> ElseIf) {
        this.ElseIf = ElseIf.toArray(new TempCondition[]{});
        this.ElseIfCount = ElseIf.size();
    }
}