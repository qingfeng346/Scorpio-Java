package Scorpio;
/**  函数类型 
*/
public enum FunstionType
{
	//脚本函数
	Script,
	//注册的C函数
	Function,
	//注册的C函数
	Handle,
	//动态委托
	Delegate,
	//函数
	Method;

	public int getValue()
	{
		return this.ordinal();
	}

	public static FunstionType forValue(int value)
	{
		return values()[value];
	}
}