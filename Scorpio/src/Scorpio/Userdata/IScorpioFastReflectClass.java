package Scorpio.Userdata;

import Scorpio.*;

public interface IScorpioFastReflectClass {
    FastReflectUserdataMethod GetConstructor();
    Object GetValue(Object obj, String name);
    void SetValue(Object obj, String name, ScriptObject value);
}