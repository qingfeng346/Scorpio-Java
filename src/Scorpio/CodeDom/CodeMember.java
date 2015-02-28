package Scorpio.CodeDom;
//成员类型  a.b["c"].d[1]
public class CodeMember extends CodeObject {
	public CodeObject Parent;
    public CodeObject MemberObject;
    public Object MemberValue;
    public MEMBER_TYPE Type = MEMBER_TYPE.forValue(0);
    public CALC Calc = CALC.forValue(0);
    public CodeMember(String name) {
        this(name, null);
    }
    public CodeMember(Object value, CodeObject parent) {
        this.Parent = parent;
        this.MemberValue = value;
        this.Type = MEMBER_TYPE.VALUE;
    }
    public CodeMember(CodeObject member, CodeObject parent) {
        this.MemberObject = member;
        this.Parent = parent;
        this.Type = MEMBER_TYPE.OBJECT;
    }
}