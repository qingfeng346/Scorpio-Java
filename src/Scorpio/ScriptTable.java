package Scorpio;

//脚本table类型
public class ScriptTable extends ScriptObject {
    @Override
    public ObjectType getType() {
        return ObjectType.Table;
    }
    private java.util.HashMap<Object, ScriptObject> m_listObject = new java.util.HashMap<Object, ScriptObject>(); //所有的数据(函数和数据都在一个数组)
    public ScriptTable(Script script) {
        super(script);
    }
    @Override
    public void SetValue(Object key, ScriptObject value) {
    	Util.SetObject(m_listObject, key, value);
    }
    @Override
    public ScriptObject GetValue(Object key) {
    	return m_listObject.containsKey(key) ? m_listObject.get(key) : ScriptNull.getInstance();
    }
    public final boolean HasValue(Object key) {
        return m_listObject.containsKey(key);
    }
    public final int Count() {
        return m_listObject.size();
    }
    public void Clear() {
        m_listObject.clear();
    }
    public void Remove(Object key) {
        m_listObject.remove(key);
    }
    public final java.util.Iterator<java.util.Map.Entry<Object, ScriptObject>> GetIterator() {
        return m_listObject.entrySet().iterator();
    }
    @Override
    public ScriptObject clone() {
        ScriptTable ret = getScript().CreateTable();
        ScriptObject obj = null;
        ScriptFunction func = null;
        for (java.util.Map.Entry<Object, ScriptObject> pair : m_listObject.entrySet()) {
            obj = pair.getValue().clone();
            if (obj instanceof ScriptFunction) {
                func = (ScriptFunction)obj;
                if (!func.getIsStatic()) {
                    func.SetTable(ret);
                }
            }
            ret.m_listObject.put(pair.getKey(), obj);
        }
        return ret;
    }
    @Override
    public String toString() {
        return "Table";
    }
    @Override
    public String ToJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        boolean first = true;
        for (java.util.Map.Entry<Object, ScriptObject> pair : m_listObject.entrySet()) {
            if (first) {
                first = false;
            }
            else {
                builder.append(",");
            }
            builder.append("\"");
            builder.append(pair.getKey());
            builder.append("\":");
            builder.append(pair.getValue().ToJson());
        }
        builder.append("}");
        return builder.toString();
    }
}