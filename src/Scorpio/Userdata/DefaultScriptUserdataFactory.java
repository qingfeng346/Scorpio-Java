package Scorpio.Userdata;

import Scorpio.*;

/**  默认的Userdata工厂类 
*/
public class DefaultScriptUserdataFactory implements IScriptUserdataFactory {
    private Script m_Script;
    private java.util.HashMap<java.lang.Class, DefaultScriptUserdataEnum> m_Enums = new java.util.HashMap<java.lang.Class, DefaultScriptUserdataEnum>(); //所有枚举集合
    private java.util.HashMap<java.lang.Class, UserdataType> m_Types = new java.util.HashMap<java.lang.Class, UserdataType>(); //所有的类集合
    public final DefaultScriptUserdataEnum GetEnum(java.lang.Class type) {
        if (m_Enums.containsKey(type)) {
            return m_Enums.get(type);
        }
        DefaultScriptUserdataEnum ret = new DefaultScriptUserdataEnum(m_Script, type);
        m_Enums.put(type, ret);
        return ret;
    }
    public final UserdataType GetScorpioType(java.lang.Class type) {
        if (m_Types.containsKey(type)) {
            return m_Types.get(type);
        }
        UserdataType scorpioType = new UserdataType(m_Script, type);
        m_Types.put(type, scorpioType);
        return scorpioType;
    }
    public DefaultScriptUserdataFactory(Script script) {
        m_Script = script;
    }
    public final ScriptUserdata create(Script script, Object obj) {
        java.lang.Class type = (java.lang.Class)((obj instanceof java.lang.Class) ? obj : null);
        if (type != null) {
            if (Util.IsEnum(type)) {
                return GetEnum(type);
            }
            else {
                return new DefaultScriptUserdataObjectType(script, type, GetScorpioType(type));
            }
        }
        return new DefaultScriptUserdataObject(script, obj, GetScorpioType(obj.getClass()));
    }
}