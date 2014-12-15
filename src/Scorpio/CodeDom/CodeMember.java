package Scorpio.CodeDom;

import Scorpio.*;

//成员类型  a.b["c"].d[1]
public class CodeMember extends CodeObject {
	public CodeObject Parent;
    public CodeObject MemberObject;
    public String MemberString;
    public int MemberIndex;
    public Object MemberNumber;
    public MEMBER_TYPE Type = MEMBER_TYPE.forValue(0);
    public CALC Calc = CALC.forValue(0);
    public CodeMember(String name) {
        this(name, null);
    }
    public CodeMember(String name, CodeObject parent) {
        this.Parent = parent;
        this.MemberString = name;
        this.Type = MEMBER_TYPE.STRING;
    }
    public CodeMember(ScriptNumber mem, CodeObject parent) {
        this.Parent = parent;
        if (mem.getObjectValue() instanceof Double) {
            this.MemberIndex = mem.ToInt32();
            this.Type = MEMBER_TYPE.INDEX;
        }
        else {
            this.MemberNumber = mem.getObjectValue();
            this.Type = MEMBER_TYPE.NUMBER;
        }
    }
    public CodeMember(CodeObject member, CodeObject parent) {
        this.MemberObject = member;
        this.Parent = parent;
        this.Type = MEMBER_TYPE.OBJECT;
    }
}