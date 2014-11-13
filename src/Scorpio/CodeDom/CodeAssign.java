package Scorpio.CodeDom;

import Scorpio.Compiler.*;

//复制变量 = += -= /= *= %=
public class CodeAssign extends CodeObject {
    public CodeMember member;
    public CodeObject value;
    public TokenType AssignType = TokenType.forValue(0);
    public CodeAssign(CodeMember member, CodeObject value, TokenType assignType, String breviary, int line) {
        super(breviary, line);
        this.member = member;
        this.value = value;
        this.AssignType = assignType;
    }
}