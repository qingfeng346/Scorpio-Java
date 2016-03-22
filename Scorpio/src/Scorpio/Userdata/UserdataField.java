package Scorpio.Userdata;

import Scorpio.*;

public class UserdataField extends UserdataVariable {
    private java.lang.reflect.Field m_Field;
    public UserdataField(Script script, java.lang.reflect.Field info) {
        m_Script = script;
        Name = info.getName();
        FieldType = info.getType();
        m_Field = info;
    }
    @Override
    public Object GetValue(Object obj) {
        try {
			return m_Field.get(obj);
		} catch (Exception e) {
			return null;
		}
    }
    @Override
    public void SetValue(Object obj, Object val) {
        try {
			m_Field.set(obj, val);
		} catch (Exception e) {
		}
    }
}