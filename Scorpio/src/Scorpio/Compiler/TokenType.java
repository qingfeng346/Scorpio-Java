package Scorpio.Compiler;

//脚本的表征类型
public enum TokenType
{
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
	 +
	 
	*/
	Plus(13),
	/** 
	 ++
	 
	*/
	Increment(14),
	/** 
	 +=
	 
	*/
	AssignPlus(15),
	/** 
	 -
	 
	*/
	Minus(16),
	/** 
	 --
	 
	*/
	Decrement(17),
	/** 
	 -=
	 
	*/
	AssignMinus(18),
	/** 
	 *
	 
	*/
	Multiply(19),
	/** 
	 *=
	 
	*/
	AssignMultiply(20),
	/** 
	 /
	 
	*/
	Divide(21),
	/** 
	 /=
	 
	*/
	AssignDivide(22),
	/** 
	 % 模运算
	 
	*/
	Modulo(23),
	/** 
	 %=
	 
	*/
	AssignModulo(24),
	/** 
	 | 或运算
	 
	*/
	InclusiveOr(25),
	/** 
	 |=
	 
	*/
	AssignInclusiveOr(26),
	/** 
	 ||
	 
	*/
	Or(27),
	/** 
	 & 并运算
	 
	*/
	Combine(28),
	/** 
	 &=
	 
	*/
	AssignCombine(29),
	/** 
	 &&
	 
	*/
	And(30),
	/** 
	 ^ 异或
	 
	*/
	XOR(31),
	/** 
	 ^=
	 
	*/
	AssignXOR(32),
	/** 
	 <<左移
	 
	*/
	Shi(33),
	/** 
	 <<=
	 
	*/
	AssignShi(34),
	/** 
	 >> 右移
	 
	*/
	Shr(35),
	/** 
	 >>=
	 
	*/
	AssignShr(36),
	/** 
	 !
	 
	*/
	Not(37),
	/** 
	 =
	 
	*/
	Assign(38),
	/** 
	 ==
	 
	*/
	Equal(39),
	/** 
	 !=
	 
	*/
	NotEqual(40),
	/** 
	 >
	 
	*/
	Greater(41),
	/** 
	 >=
	 
	*/
	GreaterOrEqual(42),
	/** 
	  <
	 
	*/
	Less(43),
	/** 
	 <=
	 
	*/
	LessOrEqual(44),
	/** 
	 ...
	 
	*/
	Params(45),
	/** 
	 if
	 
	*/
	If(46),
	/** 
	 else
	 
	*/
	Else(47),
	/** 
	 elif
	 
	*/
	ElseIf(48),
	/** 
	 for
	 
	*/
	For(49),
	/** 
	 foreach
	 
	*/
	Foreach(50),
	/** 
	 in
	 
	*/
	In(51),
	/** 
	 switch
	 
	*/
	Switch(52),
	/** 
	 case
	 
	*/
	Case(53),
	/** 
	 default
	 
	*/
	Default(54),
	/** 
	 break
	 
	*/
	Break(55),
	/** 
	 continue
	 
	*/
	Continue(56),
	/** 
	 return
	 
	*/
	Return(57),
	/** 
	 while
	 
	*/
	While(58),
	/** 
	 function
	 
	*/
	Function(59),
	/** 
	 try
	 
	*/
	Try(60),
	/** 
	 catch
	 
	*/
	Catch(61),
	/** 
	 throw
	 
	*/
	Throw(62),
	/** 
	 bool true false
	 
	*/
	Boolean(63),
	/** 
	 int float
	 
	*/
	Number(64),
	/** 
	 string
	 
	*/
	String(65),
	/** 
	 @"" @'' string
	 
	*/
	SimpleString(66),
	/** 
	 null
	 
	*/
	Null(67),
	/** 
	 eval
	 
	*/
	Eval(68),
	/** 
	 说明符
	 
	*/
	Identifier(69),
	/** 
	 结束
	 
	*/
	Finished(70);

	private int intValue;
	private static java.util.HashMap<Integer, TokenType> mappings;
	private synchronized static java.util.HashMap<Integer, TokenType> getMappings()
	{
		if (mappings == null)
		{
			mappings = new java.util.HashMap<Integer, TokenType>();
		}
		return mappings;
	}

	private TokenType(int value)
	{
		intValue = value;
		TokenType.getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static TokenType forValue(int value)
	{
		return getMappings().get(value);
	}
}