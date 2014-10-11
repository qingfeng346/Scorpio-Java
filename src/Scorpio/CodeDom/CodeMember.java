package Scorpio.CodeDom;

import Scorpio.*;

//成员类型  a.b["c"].d[1]
public class CodeMember extends CodeObject
{
	public CodeObject Parent;
	public CodeObject Member;
	public String MemberString;
	public int MemberNumber;
	public Object MemberNumberObject;
	public MEMBER_TYPE Type = MEMBER_TYPE.forValue(0);
	public CALC Calc = CALC.forValue(0);
	public CodeMember(String name)
	{
		this(name, null);
	}
	public CodeMember(String name, CodeObject parent)
	{
		this.Parent = parent;
		this.MemberString = name;
		this.Type = MEMBER_TYPE.STRING;
	}
	public CodeMember(ScriptNumber mem, CodeObject parent)
	{
		this.Parent = parent;
		this.MemberNumber = mem.ToInt32();
		this.MemberNumberObject = mem.getObjectValue();
		this.Type = MEMBER_TYPE.NUMBER;
	}
	public CodeMember(CodeObject member, CodeObject parent)
	{
		this.Member = member;
		this.Parent = parent;
		this.Type = MEMBER_TYPE.OBJECT;
	}
}