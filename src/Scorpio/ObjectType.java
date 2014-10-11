package Scorpio;
public enum ObjectType
{
	Null, //Null
	Boolean, //布尔
	Number, //数字
	String, //字符串
	Function, //函数
	Array, //数组
	Table, //MAP
	Enum, //枚举
	UserData; //普通类

	public int getValue()
	{
		return this.ordinal();
	}

	public static ObjectType forValue(int value)
	{
		return values()[value];
	}
}