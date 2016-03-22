package Scorpio;

//脚本null类型
public class ScriptNull extends ScriptObject {
    public ScriptNull(Script script) {
        super(script);
    }
    @Override
    public ObjectType getType() {
        return ObjectType.Null;
    }
    @Override
    public Object getObjectValue() {
        return null;
    }
    @Override
    public String toString() {
        return "null";
    }
    @Override
    public String ToJson() {
        return "null";
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ScriptNull)) {
            return false;
        }
        return true;
    }
    @Override
    public int hashCode() {
        return 0;
    }
    @Override
    public boolean LogicOperation() {
        return false;
    }
}