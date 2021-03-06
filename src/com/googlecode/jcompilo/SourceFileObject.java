package com.googlecode.jcompilo;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.functions.Lazy;
import com.googlecode.totallylazy.Strings;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.io.IOException;

public class SourceFileObject extends SimpleJavaFileObject {
    private final Resource resource;
    private final Lazy<CharSequence> charContent;

    private SourceFileObject(final Resource resource) {
        super(resource.uri().toURI(), Kind.SOURCE);
        this.resource = resource;
        charContent = new Lazy<CharSequence>() {
            public CharSequence get() {
                return Strings.toString(resource.bytes());
            }
        };
    }

    public static SourceFileObject sourceFileObject(Resource resource) {
        return new SourceFileObject(resource);
    }

    public static Function1<Resource, JavaFileObject> sourceFileObject() {
        return pair -> sourceFileObject(pair);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return charContent.value();
    }

    @Override
    public long getLastModified() {
        return resource.modified().getTime();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return resource.name();
    }
}
