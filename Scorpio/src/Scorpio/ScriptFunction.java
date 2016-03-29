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
    @Override
    public String toString() {
        return "Function(" + getName() + ")";
    }
    @Override
    public String ToJson() {
        return "\"Function\"";
    }
}