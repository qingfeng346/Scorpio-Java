package Scorpio.Userdata;

import Scorpio.*;

public abstract class UserdataVariable {
    protected Script m_Script;
    public String Name;
    public java.lang.Class<?> FieldType;
    public abstract Object GetValue(Object obj);
    public abstract void SetValue(Object obj, Object val);
}