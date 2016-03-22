package Scorpio.CodeDom;

import Scorpio.Compiler.*;

//运算符号   
public class CodeOperator extends CodeObject {
    public CodeObject Left; //左边值
    public CodeObject Right; //右边值
    public TokenType Operator = TokenType.forValue(0); //符号类型
    public CodeOperator(CodeObject Right, CodeObject Left, TokenType type, String breviary, int line) {
        super(breviary, line);
        this.Left = Left;
        this.Right = Right;
        this.Operator = type;
    }
}