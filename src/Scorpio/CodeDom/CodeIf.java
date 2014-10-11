package Scorpio.CodeDom;

import Scorpio.CodeDom.Temp.*;

//if语句  if(true) {} elseif () {} else {}
public class CodeIf extends CodeObject
{
	public TempCondition If;
	public TempCondition Else;
	public java.util.ArrayList<TempCondition> ElseIf = new java.util.ArrayList<TempCondition>();
	public final void AddElseIf(TempCondition con)
	{
		ElseIf.add(con);
	}
}