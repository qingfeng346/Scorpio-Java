package Scorpio.Compiler;

//脚本的表征类型
public enum TokenType {
    /** 
     空类型（没有实际用途）
     
    */
    None(0),
    /** 
     var
     
    */
    Var(1),
    /** 
     {
     
    */
    LeftBrace(2),
    /** 
     }
     
    */
    RightBrace(3),
    /** 
     (
     
    */
    LeftPar(4),
    /** 
     )
     
    */
    RightPar(5),
    /** 
     [
     
    */
    LeftBracket(6),
    /** 
     ]
     
    */
    RightBracket(7),
    /** 
     .
     
    */
    Period(8),
    /** 
     ,
     
    */
    Comma(9),
    /** 
     :
     
    */
    Colon(10),
    /** 
     ;
     
    */
    SemiColon(11),
    /** 
     ?
     
    */
    QuestionMark(12),
    /** 
     #
     
    */
    Sharp(13),
    /** 
     +
     
    */
    Plus(14),
    /** 
     ++
     
    */
    Increment(15),
    /** 
     +=
     
    */
    AssignPlus(16),
    /** 
     -
     
    */
    Minus(17),
    /** 
     --
     
    */
    Decrement(18),
    /** 
     -=
     
    */
    AssignMinus(19),
    /** 
     *
     
    */
    Multiply(20),
    /** 
     *=
     
    */
    AssignMultiply(21),
    /** 
     /
     
    */
    Divide(22),
    /** 
     /=
     
    */
    AssignDivide(23),
    /** 
     % 模运算
     
    */
    Modulo(24),
    /** 
     %=
     
    */
    AssignModulo(25),
    /** 
     | 或运算
     
    */
    InclusiveOr(26),
    /** 
     |=
     
    */
    AssignInclusiveOr(27),
    /** 
     ||
     
    */
    Or(28),
    /** 
     & 并运算
     
    */
    Combine(29),
    /** 
     &=
     
    */
    AssignCombine(30),
    /** 
     &&
     
    */
    And(31),
    /** 
     ^ 异或
     
    */
    XOR(32),
    /** 
     ^=
     
    */
    AssignXOR(33),
    /** 
     <<左移
     
    */
    Shi(34),
    /** 
     <<=
     
    */
    AssignShi(35),
    /** 
     >> 右移
     
    */
    Shr(36),
    /** 
     >>=
     
    */
    AssignShr(37),
    /** 
     !
     
    */
    Not(38),
    /** 
     =
     
    */
    Assign(39),
    /** 
     ==
     
    */
    Equal(40),
    /** 
     !=
     
    */
    NotEqual(41),
    /** 
     >
     
    */
    Greater(42),
    /** 
     >=
     
    */
    GreaterOrEqual(43),
    /** 
      <
     
    */
    Less(44),
    /** 
     <=
     
    */
    LessOrEqual(45),
    /** 
     ...
     
    */
    Params(46),
    /** 
     if
     
    */
    If(47),
    /** 
     else
     
    */
    Else(48),
    /** 
     elif
     
    */
    ElseIf(49),
    /** 
     ifndef
     
    */
    Ifndef(50),
    /** 
     endif
     
    */
    Endif(51),
    /** 
     for
     
    */
    For(52),
    /** 
     foreach
     
    */
    Foreach(53),
    /** 
     in
     
    */
    In(54),
    /** 
     switch
     
    */
    Switch(55),
    /** 
     case
     
    */
    Case(56),
    /** 
     default
     
    */
    Default(57),
    /** 
     break
     
    */
    Break(58),
    /** 
     continue
     
    */
    Continue(59),
    /** 
     return
     
    */
    Return(60),
    /** 
     while
     
    */
    While(61),
    /** 
     function
     
    */
    Function(62),
    /** 
     try
     
    */
    Try(63),
    /** 
     catch
     
    */
    Catch(64),
    /** 
     throw
     
    */
    Throw(65),
    /** 
     define
     
    */
    Define(66),
    /** 
     bool true false
     
    */
    Boolean(67),
    /** 
     int float
     
    */
    Number(68),
    /** 
     string
     
    */
    String(69),
    /** 
     @"" @'' string
     
    */
    SimpleString(70),
    /** 
     null
     
    */
    Null(71),
    /** 
     eval
     
    */
    Eval(72),
    /** 
     说明符
     
    */
    Identifier(73),
    /** 
     结束
     
    */
    Finished(74);

    private int intValue;
    private static java.util.HashMap<Integer, TokenType> mappings;
    private synchronized static java.util.HashMap<Integer, TokenType> getMappings() {
        if (mappings == null) {
            mappings = new java.util.HashMap<Integer, TokenType>();
        }
        return mappings;
    }

    private TokenType(int value) {
        intValue = value;
        TokenType.getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static TokenType forValue(int value) {
        return getMappings().get(value);
    }
}