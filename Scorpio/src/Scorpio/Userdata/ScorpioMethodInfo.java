package Scorpio.Userdata;

public class ScorpioMethodInfo {
    public String Name;
    public boolean IsStatic;
    public java.lang.Class<?>[] ParameterType;
    public boolean Params;
    public java.lang.Class<?> ParamType;
    public String ParameterTypes;
    public ScorpioMethodInfo(String name, boolean isStatic, java.lang.Class<?>[] parameterType, boolean param, java.lang.Class<?> paramType, String parameterTypes) {
        this.Name = name;
        this.IsStatic = isStatic;
        this.ParameterType = parameterType;
        this.Params = param;
        this.ParamType = paramType;
        this.ParameterTypes = parameterTypes;
    }
}
