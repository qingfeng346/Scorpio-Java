package Scorpio;
//C#函数指针
//C# TO JAVA CONVERTER TODO TASK: Delegates are not available in Java:
//public delegate object ScorpioFunction(ScriptObject[] Parameters);
//C#类执行
public interface ScorpioHandle {
    Object Call(ScriptObject[] Parameters);
}