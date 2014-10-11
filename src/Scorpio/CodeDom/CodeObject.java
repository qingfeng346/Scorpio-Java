package Scorpio.CodeDom;

import Scorpio.Compiler.*;
import Scorpio.Exception.*;

//一个需要解析的Object
public class CodeObject
{
	public boolean Not; // ! 标识（非xxx）
	public boolean Negative; // - 标识（负数）
	public StackInfo StackInfo; // 堆栈数据
	public CodeObject()
	{
	}
	public CodeObject(String breviary, int line)
	{
		StackInfo = new StackInfo(breviary, line);
	}
}