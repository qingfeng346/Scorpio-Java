package main;

import Scorpio.Script;
import Scorpio.ScriptObject;

public class Test {
	public static int Hello(int b, String str)
	{
		System.out.println("hello world  " + b + str);
		return 200;
	}
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		Script script = new Script();
		try {
			script.LoadLibrary();
			ScriptObject obj = script.LoadFile("C:/Users/while/Desktop/a.sco");
			System.out.println("返回值为 : " + (obj == null ? "null" : obj.toString()));
			System.out.println("运行时间: " + (System.currentTimeMillis() - start));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("错误堆栈 : " + script.GetStackInfo());
		}
	}
}
