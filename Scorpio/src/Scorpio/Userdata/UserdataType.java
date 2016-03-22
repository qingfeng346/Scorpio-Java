package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Compiler.*;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if SCORPIO_UWP && !UNITY_EDITOR
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#define UWP
//#endif
/**  保存一个类的所有元素 
*/
public abstract class UserdataType {
    protected Script m_Script; //脚本系统
    protected java.lang.Class<?> m_Type; //类型
    public UserdataType(Script script, java.lang.Class<?> type) {
        m_Script = script;
        m_Type = type;
    }
    /**  创建一个实例 
    */
    public abstract Object CreateInstance(ScriptObject[] parameters);
    /**  获得运算符重载的函数 
    */
    public abstract UserdataMethod GetComputeMethod(TokenType type);
    /**  获得一个类变量 
    */
    public abstract Object GetValue(Object obj, String name);
    /**  设置一个类变量 
    */
    public abstract void SetValue(Object obj, String name, ScriptObject value);

}