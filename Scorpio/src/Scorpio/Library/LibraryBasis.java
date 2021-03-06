package Scorpio.Library;

import Scorpio.*;
import Scorpio.Exception.*;
import Scorpio.Variable.*;

public class LibraryBasis {
    private static class ArrayPairs implements ScorpioHandle {
        private Script m_Script;
        private ScriptArray.Enumerator m_Enumerator;
        private int m_Index = 0;
        public ArrayPairs(Script script, ScriptArray obj) {
            m_Script = script;
            m_Index = 0;
            m_Enumerator = obj.GetIterator();
        }
        public final Object Call(ScriptObject[] args) {
            if (m_Enumerator.MoveNext()) {
                ScriptTable table = m_Script.CreateTable();
                table.SetValue("key", m_Script.CreateObject(m_Index++));
                table.SetValue("value", m_Enumerator.getCurrent());
                return table;
            }
            return null;
        }
    }
    private static class ArrayKPairs implements ScorpioHandle {
        private ScriptArray.Enumerator m_Enumerator;
        private int m_Index = 0;
        public ArrayKPairs(ScriptArray obj) {
            m_Index = 0;
            m_Enumerator = obj.GetIterator();
        }
        public final Object Call(ScriptObject[] args) {
            if (m_Enumerator.MoveNext()) {
                return m_Index++;
            }
            return null;
        }
    }
    private static class ArrayVPairs implements ScorpioHandle {
        private ScriptArray.Enumerator m_Enumerator;
        public ArrayVPairs(ScriptArray obj) {
            m_Enumerator = obj.GetIterator();
        }
        public final Object Call(ScriptObject[] args) {
            if (m_Enumerator.MoveNext()) {
                return m_Enumerator.getCurrent();
            }
            return null;
        }
    }
    private static class TablePairs implements ScorpioHandle {
        private Script m_Script;
        private java.util.Iterator<java.util.Map.Entry<Object, ScriptObject>> m_Enumerator;
        public TablePairs(Script script, ScriptTable obj) {
            m_Script = script;
            m_Enumerator = obj.GetIterator();
        }
        public final Object Call(ScriptObject[] args) {
            if (m_Enumerator.hasNext()) {
                ScriptTable table = m_Script.CreateTable();
                java.util.Map.Entry<Object, ScriptObject> v = m_Enumerator.next();
                table.SetValue("key", m_Script.CreateObject(v.getKey()));
                table.SetValue("value", v.getValue());
                return table;
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
        private Script m_Script;
        private java.util.Iterator<?> m_Enumerator;
        public UserdataPairs(Script script, ScriptUserdata obj) {
            m_Script = script;
            Object value = obj.getValue();
            Iterable<?> ienumerable = (Iterable<?>)((value instanceof Iterable) ? value : null);
            if (ienumerable == null) {
                throw new ExecutionException(m_Script, "pairs 只支持继承 IEnumerable 的类");
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
        script.SetObjectInternal("is_null", script.CreateFunction(new is_null()));
        script.SetObjectInternal("is_bool", script.CreateFunction(new is_bool()));
        script.SetObjectInternal("is_number", script.CreateFunction(new is_number()));
        script.SetObjectInternal("is_double", script.CreateFunction(new is_double()));
        script.SetObjectInternal("is_long", script.CreateFunction(new is_long()));
        script.SetObjectInternal("is_int", script.CreateFunction(new is_int()));
        script.SetObjectInternal("is_string", script.CreateFunction(new is_string()));
        script.SetObjectInternal("is_function", script.CreateFunction(new is_function()));
        script.SetObjectInternal("is_array", script.CreateFunction(new is_array()));
        script.SetObjectInternal("is_table", script.CreateFunction(new is_table()));
        script.SetObjectInternal("is_enum", script.CreateFunction(new is_enum()));
        script.SetObjectInternal("is_userdata", script.CreateFunction(new is_userdata()));
        script.SetObjectInternal("branchtype", script.CreateFunction(new branchtype()));
        script.SetObjectInternal("typeof", script.CreateFunction(new userdatatype()));
        script.SetObjectInternal("tonumber", script.CreateFunction(new tonumber(script)));
        script.SetObjectInternal("tosbyte", script.CreateFunction(new tosbyte(script)));
        script.SetObjectInternal("tobyte", script.CreateFunction(new tobyte(script)));
        script.SetObjectInternal("toshort", script.CreateFunction(new toshort(script)));
        script.SetObjectInternal("toushort", script.CreateFunction(new toushort(script)));
        script.SetObjectInternal("toint", script.CreateFunction(new toint(script)));
        script.SetObjectInternal("touint", script.CreateFunction(new touint(script)));
        script.SetObjectInternal("tolong", script.CreateFunction(new tolong(script)));
        script.SetObjectInternal("toulong", script.CreateFunction(new toulong(script)));
        script.SetObjectInternal("tofloat", script.CreateFunction(new tofloat(script)));
        script.SetObjectInternal("toenum", script.CreateFunction(new toenum(script)));
        script.SetObjectInternal("tostring", script.CreateFunction(new tostring(script)));
        script.SetObjectInternal("clone", script.CreateFunction(new clone()));
        script.SetObjectInternal("require", script.CreateFunction(new require(script)));
        script.SetObjectInternal("import", script.CreateFunction(new require(script)));
        script.SetObjectInternal("using", script.CreateFunction(new require(script)));
        script.SetObjectInternal("push_search", script.CreateFunction(new push_search(script)));
        script.SetObjectInternal("push_define", script.CreateFunction(new push_define(script)));

        script.SetObjectInternal("import_type", script.CreateFunction(new import_type(script)));
    }
    private static class print implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            for (int i = 0; i < args.length; ++i) {
                if (args[i] instanceof ScriptArray || args[i] instanceof ScriptTable) {
                    System.out.println(args[i].ToJson());
                }
                else {
                    System.out.println(args[i].toString());
                }
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
                return m_script.CreateFunction(new UserdataPairs(m_script, (ScriptUserdata)obj));
            }
            throw new ExecutionException(m_script, "pairs必须用语table或array或者继承IEnumerable的userdata 类型");
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
            throw new ExecutionException(m_script, "kpairs必须用语table或array类型");
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
            throw new ExecutionException(m_script, "vpairs必须用语table或array类型");
        }
    }
    private static class type implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0].getType().ordinal();
        }
    }
    private static class is_null implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptNull;
        }
    }
    private static class is_bool implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptBoolean;
        }
    }
    private static class is_number implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptNumber;
        }
    }
    private static class is_double implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptNumberDouble;
        }
    }
    private static class is_long implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptNumberLong;
        }
    }
    private static class is_int implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptNumberInt;
        }
    }
    private static class is_string implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptString;
        }
    }
    private static class is_function implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptFunction;
        }
    }
    private static class is_array implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptArray;
        }
    }
    private static class is_table implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptTable;
        }
    }
    private static class is_enum implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptEnum;
        }
    }
    private static class is_userdata implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return args[0] instanceof ScriptUserdata;
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
            Util.Assert(obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum, m_script, "tonumber 不能从类型 " + obj.getType() + " 转换成Number类型");
            return new ScriptNumberDouble(m_script, Util.ToDouble(obj.getObjectValue()));
        }
    }
    private static class tosbyte implements ScorpioHandle {
        private Script m_script;
        public tosbyte(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            Util.Assert(obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum, m_script, "传入类型错误 " + obj.getType());
            return new ScriptNumberSByte(m_script, Util.ToSByte(obj.getObjectValue()));
        }
    }
    private static class tobyte implements ScorpioHandle {
        private Script m_script;
        public tobyte(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            Util.Assert(obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum, m_script, "传入类型错误 " + obj.getType());
            return new ScriptNumberByte(m_script, Util.ToByte(obj.getObjectValue()));
        }
    }
    private static class toshort implements ScorpioHandle {
        private Script m_script;
        public toshort(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            Util.Assert(obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum, m_script, "传入类型错误 " + obj.getType());
            return new ScriptNumberShort(m_script, Util.ToInt16(obj.getObjectValue()));
        }
    }
    private static class toushort implements ScorpioHandle {
        private Script m_script;
        public toushort(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            Util.Assert(obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum, m_script, "传入类型错误 " + obj.getType());
            return new ScriptNumberUShort(m_script, Util.ToUInt16(obj.getObjectValue()));
        }
    }
    private static class toint implements ScorpioHandle {
        private Script m_script;
        public toint(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            Util.Assert(obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum, m_script, "传入类型错误 " + obj.getType());
            return new ScriptNumberInt(m_script, Util.ToInt32(obj.getObjectValue()));
        }
    }
    private static class touint implements ScorpioHandle {
        private Script m_script;
        public touint(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            Util.Assert(obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum, m_script, "传入类型错误 " + obj.getType());
            return new ScriptNumberUInt(m_script, Util.ToUInt32(obj.getObjectValue()));
        }
    }
    private static class tolong implements ScorpioHandle {
        private Script m_script;
        public tolong(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            Util.Assert(obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum, m_script, "传入类型错误 " + obj.getType());
            return new ScriptNumberLong(m_script, Util.ToInt64(obj.getObjectValue()));
        }
    }
    private static class toulong implements ScorpioHandle {
        private Script m_script;
        public toulong(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            Util.Assert(obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum, m_script, "传入类型错误 " + obj.getType());
            return new ScriptNumberULong(m_script, Util.ToUInt64(obj.getObjectValue()));
        }
    }
    private static class tofloat implements ScorpioHandle {
        private Script m_script;
        public tofloat(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptObject obj = args[0];
            Util.Assert(obj instanceof ScriptNumber || obj instanceof ScriptString || obj instanceof ScriptEnum, m_script, "传入类型错误 " + obj.getType());
            return new ScriptNumberFloat(m_script, Util.ToSingle(obj.getObjectValue()));
        }
    }
    private static class toenum implements ScorpioHandle {
        private Script m_script;
        public toenum(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            Util.Assert(args.length == 2, m_script, "toenum 第一个参数是枚举类 第二个参数必须是number类型");
            ScriptUserdata obj = (ScriptUserdata)((args[0] instanceof ScriptUserdata) ? args[0] : null);
            ScriptNumber number = (ScriptNumber)((args[1] instanceof ScriptNumber) ? args[1] : null);
            Util.Assert(obj != null && number != null, m_script, "toenum 第一个参数是枚举类 第二个参数必须是number类型");
            return new ScriptEnum(m_script, Util.ToEnum(obj.getValueType(), number.ToInt32()));
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
    private static class require implements ScorpioHandle {
        private Script m_script;
        public require(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptString str = (ScriptString)((args[0] instanceof ScriptString) ? args[0] : null);
            Util.Assert(str != null, m_script, "require 参数必须是 string");
            return m_script.LoadSearchPathFile(str.getValue());
        }
    }
    private static class push_search implements ScorpioHandle {
        private Script m_script;
        public push_search(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptString str = (ScriptString)((args[0] instanceof ScriptString) ? args[0] : null);
            Util.Assert(str != null, m_script, "push_search 参数必须是 string");
            m_script.PushSearchPath(str.getValue());
            return null;
        }
    }
    private static class push_define implements ScorpioHandle {
        private Script m_script;
        public push_define(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptString str = (ScriptString)((args[0] instanceof ScriptString) ? args[0] : null);
            Util.Assert(str != null, m_script, "push_define 参数必须是 string");
            m_script.PushDefine(str.getValue());
            return null;
        }
    }
    private static class import_type implements ScorpioHandle {
        private Script m_script;
        public import_type(Script script) {
            m_script = script;
        }
        public final Object Call(ScriptObject[] args) {
            ScriptString str = (ScriptString)((args[0] instanceof ScriptString) ? args[0] : null);
            Util.Assert(str != null, m_script, "import_type 参数必须是 string");
            return m_script.LoadType(str.getValue());
        }
    }
}