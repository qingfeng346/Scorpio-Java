package Scorpio;

/**  函数类型 
//脚本函数类型
*/
public class ScriptFunction extends ScriptObject {
    @Override
    public ObjectType getType() {
        return ObjectType.Function;
    }
    public ScriptFunction(Script script, String name) {
        super(script);
        setName(name);
    }
    public int GetParamCount() {
        return 0;
    }
    public boolean IsParams() {
        return false;
    }
    public boolean IsStatic() {
        return false;
    }
    public ScriptArray GetParams() {
        return m_Script.CreateArray();
    }
    @Override
    public String toString() {
        return "Function(" + getName() + ")";
    }
    @Override
    public String ToJson() {
        return "\"Function\"";
    }
}