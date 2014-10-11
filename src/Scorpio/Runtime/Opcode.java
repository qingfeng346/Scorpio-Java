package Scorpio.Runtime;

//指令类型
public enum Opcode
{
	/**  复制操作 
	*/
	MOV,
	/**  申请一个局部变量 
	*/
	VAR,
	/**  执行普通代码块 
	*/
	CALL_BLOCK,
	/**  执行If语句 
	*/
	CALL_IF,
	/**  执行For语句 
	*/
	CALL_FOR,
	/**  执行Foreach语句 
	*/
	CALL_FOREACH,
	/**  执行While语句 
	*/
	CALL_WHILE,
	/**  调用一个函数 
	*/
	CALL_FUNCTION,
	/**  递增递减变量 ++或-- 
	*/
	CALC,
	/**  执行一段字符串 
	*/
	EVAL,
	/**  返回值 
	*/
	RET,
	/**  break跳出 for foreach while 
	*/
	BREAK,
	/**  continue跳出本次 for foreach while 
	*/
	CONTINUE;

	public int getValue()
	{
		return this.ordinal();
	}

	public static Opcode forValue(int value)
	{
		return values()[value];
	}
}