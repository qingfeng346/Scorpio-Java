package Scorpio.Library;

import Scorpio.*;
import Scorpio.Exception.*;

public class LibraryBasis {
    private static class ArrayPairs implements ScorpioHandle {
        private Script m_Script;
        private java.util.Iterator<ScriptObject> m_Enumerator;
        private int m_Index = 0;
        private ScriptTable m_Table;
        public ArrayPairs(Script script, ScriptArray obj) {
            m_Script = script;
            m_Index = 0;
            m_Table = m_Script.CreateTable();
            m_Enumerator = obj.GetIterator();
        }
        public final Object Call(ScriptObject[] args) {
            if (m_Enumerator.hasNext()) {
                m_Table.SetValue("key", m_Script.CreateObject(m_Index++));
                m_Table.SetValue("value", m_Enumerator.next());
                return m_Table;
            }
            return null;
        }
    }
    private static class ArrayKPairs implements ScorpioHandle {
        private java.util.Iterator<ScriptObject> m_Enumerator;
        private int m_Index = 0;
        public ArrayKPairs(ScriptArray obj) {
            m_Index = 0;
            m_Enumerator = obj.GetIterator();
        }
        public final Object Call(ScriptObject[] args) {
            if (m_Enumerator.hasNext()) {
                return m_Index++;
            }
            return null;
        }
    }
    private static class ArrayVPairs implements ScorpioHandle {
        private java.util.Iterator<ScriptObject> m_Enumerator;
        public ArrayVPairs(ScriptArray obj) {
            m_Enumerator = obj.GetIterator();
        }
        public final Object Call(ScriptObject[] args) {
            if (m_Enumerator.hasNext()) {
                return m_Enumerator.next();
            }
            return null;
        }
    }
    private static class TablePairs implements ScorpioHandle {
        private Script m_Script;
        private java.util.Iterator<java.util.Map.Entry<Object, ScriptObject>> m_Enumerator;
        private ScriptTable m_Table;
        public TablePairs(Script script, ScriptTable obj) {
            m_Script = script;
            m_Table = m_Script.CreateTable();
            m_Enumerator = obj.GetIterator();
        }
        public final Object Call(ScriptObject[] args) {
            if (m_Enumerator.hasNext()) {
                java.util.Map.Entry<Object, ScriptObject> v = m_Enumerator.next();
                m_Table.SetValue("key", m_Script.CreateObject(v.getKey()));
                m_Table.SetValue("value", v.getValue());
                return m_Table;
            }
            return null;
        }
    }
    private static class TableKPairs implements ScorpioHandle {
        private java.util.Iterator<java.util.Map.Entry<Object, ScriptObject>> m_Enumerator;
        public TableKPairs(ScriptTable obj) {
            m_Enumerator = obj.GetIterator();
        }
        public final Object Call(ScriptObject[] args) {
            if (m_Enumerator.hasNext()) {
                return m_Enumerator.next().getKey();
            }
            return null;
        }
    }
    private static class TableVPairs implements ScorpioHandle {
        private java.util.Iterator<java.util.Map.Entry<Object, ScriptObject>> m_Enumerator;
        public TableVPairs(ScriptTable obj) {
            m_Enumerator = obj.GetIterator();
        }
        public final Object Call(ScriptObject[] args) {
            if (m_Enumerator.hasNext()) {
                return m_Enumerator.next().getValue();
            }
            return null;
        }
    }
    private static class UserdataPairs implements ScorpioHandle {
        private java.util.Iterator m_Enumerator;
        public UserdataPairs(ScriptUserdata obj) {
            Object value = obj.getValue();
            Iterable ienumerable = (Iterable)((value instanceof Iterable) ? value : null);
            if (ienumerable == null) {
                throw new ExecutionException("pairs 只支持继承 IEnumerable 的类");
            }
            m_Enumerator = ienumerable.iterator();
        }
        public final Object Call(ScriptObject[] args) {
            if (m_Enumerator.hasNext()) {
                return m_Enumerator.next();
            }
            return null;
        }
    }
    public static void Load(Script script) {
        script.SetObjectInternal("print", script.CreateFunction(new print()));
        script.SetObjectInternal("pairs", script.CreateFunction(new pairs(script)));
        script.SetObjectInternal("kpairs", script.CreateFunction(new kpairs(script)));
        script.SetObjectInternal("vpairs", script.CreateFunction(new vpairs(script)));
        script.SetObjectInternal("type", script.CreateFunction(new type()));
        script.SetObjectInternal("branchtype", script.CreateFunction(new branchtype()));
        script.SetObjectInternal("typeof", script.CreateFunction(new userdatatype()));
        script.SetObjectInternal("tonumber", script.CreateFunction(new tonumber(script)));
        script.SetObjectInternal("tolong", script.CreateFunction(new tolong(script)));
        script.SetObjectInternal("tostring", script.CreateFunction(new tostring(script)));
        script.SetObjectInternal("clone", script.CreateFunction(new clone()));
        script.SetObjectInternal("import_type", script.CreateFunction(new import_type(script)));
    }
    private static class print implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            for (int i = 0; i < args.length; ++i) {
                System.out.println(args[i].toString());
            }
            return null;
        }
    }
    private static class pairs implements ScorpioHandle {
        private Script m_script;
        public pairs(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            if (obj instanceof ScriptArray) {
                return m_script.CreateFunction(new ArrayPairs(m_script, (ScriptArray)obj));
            }
            else if (obj instanceof ScriptTable) {
                return m_script.CreateFunction(new TablePairs(m_script, (ScriptTable)obj));
            }
            else if (obj instanceof ScriptUserdata) {
                return m_script.CreateFunction(new UserdataPairs((ScriptUserdata)obj));
            }
            throw new ExecutionException("pairs必须用语table或array或者继承IEnumerable的userdata 类型");
        }
    }
    private static class kpairs implements ScorpioHandle {
        private Script m_script;
        public kpairs(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            if (obj instanceof ScriptArray) {
                return m_script.CreateFunction(new ArrayKPairs((ScriptArray)obj));
            }
            else if (obj instanceof ScriptTable) {
                return m_script.CreateFunction(new TableKPairs((ScriptTable)obj));
            }
            throw new ExecutionException("kpairs必须用语table或array类型");
        }
    }
    private static class vpairs implements ScorpioHandle {
        private Script m_script;
        public vpairs(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            if (obj instanceof ScriptArray) {
                return m_script.CreateFunction(new ArrayVPairs((ScriptArray)obj));
            }
            else if (obj instanceof ScriptTable) {
                return m_script.CreateFunction(new TableVPairs((ScriptTable)obj));
            }
            throw new ExecutionException("vpairs必须用语table或array类型");
        }
    }
    private static class type implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0].getType();
        }
    }
    private static class branchtype implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0].getBranchType();
        }
    }
    private static class userdatatype implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptUserdata)args[0]).getValueType();
        }
    }
    private static class tonumber implements ScorpioHandle {
        private Script m_script;
        public tonumber(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            if (obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum) {
                return m_script.CreateNumber(Util.ToDouble(obj.getObjectValue()));
            }
            throw new ExecutionException("不能从类型 " + obj.getType() + " 转换成Number类型");
        }
    }
    private static class tolong implements ScorpioHandle {
        private Script m_script;
        public tolong(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            if (obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum) {
                return m_script.CreateNumber(Util.ToInt64(obj.getObjectValue()));
            }
            throw new ExecutionException("不能从类型 " + obj.getType() + " 转换成Long类型");
        }
    }
    private static class tostring implements ScorpioHandle {
        private Script m_script;
        public tostring(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            if (obj instanceof ScriptString) {
                return obj;
            }
            return m_script.CreateString(obj.toString());
        }
    }
    private static class clone implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0].clone();
        }
    }
    private static class import_type implements ScorpioHandle {
        private Script m_script;
        public import_type(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptString str = (ScriptString)((args[0] instanceof ScriptString) ? args[0] : null);
            if (str == null) {
                throw new ExecutionException("import_type 参数必须是 string");
            }
            return m_script.LoadType(str.getValue());
        }
    }
}