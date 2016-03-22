package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Compiler.*;

public class FastReflectUserdataType extends UserdataType {
    private IScorpioFastReflectClass m_Value;
    private FastReflectUserdataMethod m_Constructor;
    public FastReflectUserdataType(Script script, java.lang.Class<?> type, IScorpioFastReflectClass value) {
        super(script, type);
        m_Value = value;
        m_Constructor = value.GetConstructor();
    }
    @Override
    public Object CreateInstance(ScriptObject[] parameters) {
        return m_Constructor.Call(null, parameters);
    }

    @Override
    public UserdataMethod GetComputeMethod(TokenType type) {
        switch (type) {
        case Plus:
            Object tempVar = m_Value.GetValue(null, "op_Addition");
            return (UserdataMethod)((tempVar instanceof UserdataMethod) ? tempVar : null);
        case Minus:
            Object tempVar2 = m_Value.GetValue(null, "op_Subtraction");
            return (UserdataMethod)((tempVar2 instanceof UserdataMethod) ? tempVar2 : null);
        case Multiply:
            Object tempVar3 = m_Value.GetValue(null, "op_Multiply");
            return (UserdataMethod)((tempVar3 instanceof UserdataMethod) ? tempVar3 : null);
        case Divide:
            Object tempVar4 = m_Value.GetValue(null, "op_Division");
            return (UserdataMethod)((tempVar4 instanceof UserdataMethod) ? tempVar4 : null);
        default:
            return null;
        }
    }

    @Override
    public Object GetValue(Object obj, String name) {
        return m_Value.GetValue(obj, name);
    }

    @Override
    public void SetValue(Object obj, String name, ScriptObject value) {
        m_Value.SetValue(obj, name, value);
    }
}