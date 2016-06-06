package Scorpio;

import Scorpio.Exception.*;

//脚本数组类型
public class ScriptArray extends ScriptObject {
    public final class Comparer implements java.util.Comparator<ScriptObject> {
        private Script script;
        private ScriptFunction func;
        public Comparer(Script script, ScriptFunction func) {
            this.script = script;
            this.func = func;
        }
        @Override
        public int compare(ScriptObject x, ScriptObject y) {
            Object tempVar = func.Call(new ScriptObject[] { x, y });
            ScriptNumber ret = (ScriptNumber)((tempVar instanceof ScriptNumber) ? tempVar : null);
            if (ret == null) {
                throw new ExecutionException(script, "Sort 返回值 必须是Number类型");
            }
            return ret.ToInt32();
        }
    }
    public final static class Enumerator {
        private ScriptArray list;
        private int index;
        private ScriptObject current;
        public Enumerator(ScriptArray list) {
            this.list = list;
            this.index = 0;
            this.current = null;
        }
        public boolean MoveNext() {
            if (index < list.m_size) {
                current = ((list.m_listObject[index]) != null) ? list.m_listObject[index] : list.m_null;
                index++;
                return true;
            }
            return false;
        }
        public ScriptObject getCurrent() {
            return current;
        }
        public void Reset() {
            index = 0;
            current = null;
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.Array;
    }
    private static final ScriptObject[] _emptyArray = new ScriptObject[0];
    private ScriptObject[] m_listObject;
    private int m_size;
    private ScriptObject m_null;
    public ScriptArray(Script script) {
        super(script);
        m_listObject = _emptyArray;
        m_size = 0;
        m_null = script.getNull();
    }
    @Override
    public ScriptObject GetValue(Object index) {
        if (index instanceof Double || index instanceof Integer || index instanceof Long) {
            int i = Util.ToInt32(index);
            if (i < 0) {
                throw new ExecutionException(m_Script, "Array GetValue索引小于0 index值为:" + index);
            }
            if (i >= m_size) {
                return m_null;
            }
            return ((m_listObject[i]) != null) ? m_listObject[i] : m_null;
        }
        else if (index instanceof String && index.equals("length")) {
            return m_Script.CreateDouble(Util.ToDouble(m_size));
        }
        throw new ExecutionException(m_Script, "Array SetValue只支持Number类型 index值为:" + index);
    }
    @Override
    public void SetValue(Object index, ScriptObject obj) {
        if (index instanceof Double || index instanceof Integer || index instanceof Long) {
            int i = Util.ToInt32(index);
            if (i < 0) {
                throw new ExecutionException(m_Script, "Array SetValue索引小于0 index值为:" + index);
            }
            if (i >= m_size) {
                EnsureCapacity(i + 1);
                m_size = i + 1;
            }
            m_listObject[i] = obj;
        }
        else {
            throw new ExecutionException(m_Script, "Array SetValue只支持Number类型 index值为:" + index);
        }
    }
    private void SetCapacity(int value) {
        if (value > 0) {
            ScriptObject[] array = new ScriptObject[value];
            if (m_size > 0) {
                System.arraycopy(m_listObject, 0, array, 0, m_size);
            }
            m_listObject = array;
        }
        else {
            m_listObject = _emptyArray;
        }
    }
    private void EnsureCapacity(int min) {
        if (m_listObject.length < min) {
            int num = (m_listObject.length == 0) ? 4 : (m_listObject.length * 2);
            if (num > 2146435071) {
                num = 2146435071;
            }
            if (num < min) {
                num = min;
            }
            SetCapacity(num);
        }
    }
    public final void Add(ScriptObject obj) {
        if (m_size == m_listObject.length) {
            EnsureCapacity(m_size + 1);
        }
        m_listObject[m_size] = obj;
        m_size++;
    }
    public final void Insert(int index, ScriptObject obj) {
        if (m_size == m_listObject.length) {
            EnsureCapacity(m_size + 1);
        }
        if (index < m_size) {
            System.arraycopy(m_listObject, index, m_listObject, index + 1, m_size - index);
        }
        m_listObject[index] = obj;
        m_size++;
    }
    public final boolean Remove(ScriptObject obj) {
        int num = IndexOf(obj);
        if (num >= 0) {
            RemoveAt(num);
            return true;
        }
        return false;
    }
    public final void RemoveAt(int index) {
        m_size--;
        if (index < m_size) {
            System.arraycopy(m_listObject, index + 1, m_listObject, index, m_size - index);
        }
        m_listObject[m_size] = null;
    }
    public final boolean Contains(ScriptObject obj) {
        for (int i = 0;i < m_size; ++i) {
            if (obj.equals(m_listObject[i])) {
                return true;
            }
        }
        return false;
    }
    public final int IndexOf(ScriptObject obj) {
        for (int i = 0; i < m_size; ++i) {
            if (obj.equals(m_listObject[i])) {
                return i;
            }
        }
        return -1;
    }
    public final int LastIndexOf(ScriptObject obj) {
        for (int i = m_size - 1; i >= 0; --i) {
            if (obj.equals(m_listObject[i])) {
                return i;
            }
        }
        return -1;
    }
    public final void Resize(int length) {
        if (length < 0) {
            throw new ExecutionException(m_Script, "Resize长度小于0 length:" + length);
        }
        if (length > m_size) {
            EnsureCapacity(length);
            m_size = length;
        }
        else {
        	for (int i = length; i < m_size; i++)
                m_listObject[i] = null;
            m_size = length;
        }
    }
    public final void Clear() {
        if (m_size > 0) {
            for (int i = 0; i < m_size; i++)
                m_listObject[i] = null;
            m_size = 0;
        }
    }
    public final int Count() {
        return m_size;
    }
    public final void Sort(ScriptFunction func) {
    	java.util.Arrays.sort(m_listObject, 0, m_size, new Comparer(m_Script, func));
    }
    public final ScriptObject First() {
        if (m_size > 0) {
            return m_listObject[0];
        }
        return m_null;
    }
    public final ScriptObject Last() {
        if (m_size > 0) {
            return m_listObject[m_size - 1];
        }
        return m_null;
    }
    public final ScriptObject PopFirst() {
        if (m_size == 0) {
            throw new ExecutionException(m_Script, "Array Pop 数组长度为 0");
        }
        ScriptObject obj = m_listObject[0];
        RemoveAt(0);
        return obj;
    }
    public final ScriptObject SafePopFirst() {
        if (m_size == 0) {
            return m_null;
        }
        ScriptObject obj = m_listObject[0];
        RemoveAt(0);
        return obj;
    }
    public final ScriptObject PopLast() {
        if (m_size == 0) {
            throw new ExecutionException(m_Script, "Array Pop 数组长度为 0");
        }
        int index = m_size - 1;
        ScriptObject obj = m_listObject[index];
        RemoveAt(index);
        return obj;
    }
    public final ScriptObject SafePopLast() {
        if (m_size == 0) {
            return m_null;
        }
        int index = m_size - 1;
        ScriptObject obj = m_listObject[index];
        RemoveAt(index);
        return obj;
    }

    public final Enumerator GetIterator() {
        return new Enumerator(this);
    }
    public final ScriptObject[] toArray() {
        ScriptObject[] array = new ScriptObject[m_size];
        System.arraycopy(m_listObject, 0, array, 0, m_size);
        return array;
    }
    @Override
    public ScriptObject clone() {
        ScriptArray ret = m_Script.CreateArray();
        ret.m_listObject = new ScriptObject[m_size];
        ret.m_size = m_size;
        for (int i = 0; i < m_size; ++i) {
            if (m_listObject[i] == this) {
                ret.m_listObject[i] = ret;
            }
            else if (m_listObject[i] == null) {
                ret.m_listObject[i] = m_null;
            }
            else {
                ret.m_listObject[i] = m_listObject[i].clone();
            }
        }
        return ret;
    }
    @Override
    public String toString() {
        return "Array";
    }
    @Override
    public String ToJson() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < m_size; ++i) {
            if (i != 0) {
                builder.append(",");
            }
            if (m_listObject[i] == null) {
                builder.append(m_null.ToJson());
            }
            else {
                builder.append(m_listObject[i].ToJson());
            }
        }
        builder.append("]");
        return builder.toString();
    }
}