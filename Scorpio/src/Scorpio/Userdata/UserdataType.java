package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Variable.*;
import Scorpio.Compiler.*;

/**  保存一个类的所有元素 
*/
public abstract class UserdataType {
    protected Script m_Script; //脚本系统
    protected java.lang.Class<?> m_Type; //类型
    protected java.util.HashMap<String, String> m_Rename = new java.util.HashMap<String, String>();
    public UserdataType(Script script, java.lang.Class<?> type) {
        m_Script = script;
        m_Type = type;
    }
    public final void Rename(String name1, String name2) {
        m_Rename.put(name2, name1);
    }
    public final Object GetValue(Object obj, String name) {
        return m_Rename.containsKey(name) ? GetValue_impl(obj, m_Rename.get(name)) : GetValue_impl(obj, name);
    }
    public final void SetValue(Object obj, String name, ScriptObject value) {
        if (m_Rename.containsKey(name)) {
            SetValue_impl(obj, m_Rename.get(name), value);
        }
        else {
            SetValue_impl(obj, name, value);
        }
    }
    /**  创建一个实例 
    */
    public abstract Object CreateInstance(ScriptObject[] parameters);
    /**  获得运算符重载的函数 
    */
    public abstract ScorpioMethod GetComputeMethod(TokenType type);
    /**  获得一个类变量 
    */
    public abstract Object GetValue_impl(Object obj, String name);
    /**  设置一个类变量 
    */
    public abstract void SetValue_impl(Object obj, String name, ScriptObject value);
}