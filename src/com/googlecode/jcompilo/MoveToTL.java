package com.googlecode.jcompilo;

import com.googlecode.totallylazy.functions.Block;
import com.googlecode.totallylazy.Bytes;
import com.googlecode.totallylazy.functions.Function1;

import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;

public class MoveToTL {
    public static Function1<InputStream, byte[]> read() {
        return Bytes::bytes;
    }

    public static String classNameForSource(String sourceFilename) {
        return sourceFilename.replace(".java", "").replace('/', '.');
    }

    public static String classNameForByteCode(String sourceFilename) {
        return sourceFilename.replace(".class", "").replace('/', '.');
    }

    public static String classFilename(String className) {
        return className.replace('.', '/') + ".class";
    }

    public static Block<Writer> write(final Properties properties) {
        return writer -> properties.store(writer, "");
    }
}
