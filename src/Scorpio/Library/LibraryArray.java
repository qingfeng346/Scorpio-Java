﻿package Scorpio.Library;

import Scorpio.*;

public class LibraryArray {
    public static void Load(Script script) {
        ScriptTable Table = script.CreateTable();
        Table.SetValue("count", script.CreateFunction(new count()));
        Table.SetValue("insert", script.CreateFunction(new insert()));
        Table.SetValue("add", script.CreateFunction(new add()));
        Table.SetValue("clear", script.CreateFunction(new clear()));
        script.SetObjectInternal("array", Table);
    }
    private static class count implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptArray)args[0]).Count();
        }
    }
    private static class insert implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            ScriptArray array = (ScriptArray)args[0];
            int index = ((ScriptNumber)args[1]).ToInt32();
            ScriptObject obj = (ScriptObject)args[2];
            array.Insert(index, obj);
            return obj;
        }
    }
    private static class add implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            ScriptArray array = (ScriptArray)args[0];
            ScriptObject obj = (ScriptObject)args[1];
            array.Add(obj);
            return obj;
        }
    }
    private static class clear implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            ((ScriptArray)args[0]).Clear();
            return null;
        }
    }
}