package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Variable.*;
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
    public ScorpioMethod GetComputeMethod_impl(TokenType type) {
    	if (m_ComputeNames.containsKey(type)) {
            Object tempVar = m_Value.GetValue(null, m_ComputeNames.get(type));
            return (ScorpioMethod)((tempVar instanceof ScorpioMethod) ? tempVar : null);
    	}
    	return null;
    }
    @Override
    public Object GetValue_impl(Object obj, String name) {
        return m_Value.GetValue(obj, name);
    }

    @Override
    public void SetValue_impl(Object obj, String name, ScriptObject value) {
        m_Value.SetValue(obj, name, value);
    }
}