package Scorpio.Extensions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import Scorpio.*;

public class DefaultScriptExtensions implements IScriptExtensions {
    public final void print(String str) {
        System.out.println(str);
    }
    public final boolean IsEnum(java.lang.Class<?> type) {
        return type.isEnum();
    }
    public final boolean FileExist(String file) {
    	if (Util.IsNullOrEmpty(file))
    		return false;
    	File f = new File(file);
    	return f.exists() && f.isFile();
    }
    public final byte[] GetFileBuffer(String file) {
    	try {
        	ByteArrayOutputStream output = new ByteArrayOutputStream();
    		FileInputStream stream = new FileInputStream(new File(file));
            int n = 0;
            byte[] buffer = new byte[4096];
            while (-1 != (n = stream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            stream.close();
            return output.toByteArray();
    	} catch (Exception e) {
    		return null;
    	}
    }
}