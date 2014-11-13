package Scorpio.Exception;

import Scorpio.*;

public class InteriorException extends RuntimeException {
    public ScriptObject obj;
    public InteriorException(ScriptObject obj) {
        this.obj = obj;
    }
}