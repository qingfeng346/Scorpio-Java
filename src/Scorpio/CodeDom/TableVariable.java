package Scorpio.CodeDom;

import Scorpio.*;

public class TableVariable {
    public Object key;
    public CodeObject value;
    public TableVariable(Object key, CodeObject value) {
        this.key = key;
        this.value = value;
    }
}