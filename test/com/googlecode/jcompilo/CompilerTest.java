package com.googlecode.jcompilo;

import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.io.Zip;
import com.googlecode.totallylazy.predicates.Predicates;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.googlecode.jcompilo.Compiler.compiler;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Files.directory;
import static com.googlecode.totallylazy.Files.emptyTemporaryDirectory;
import static com.googlecode.totallylazy.Files.file;
import static com.googlecode.totallylazy.Files.hasSuffix;
import static com.googlecode.totallylazy.Files.recursiveFiles;
import static com.googlecode.totallylazy.predicates.WherePredicate.where;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CompilerTest {
    private Compiler compiler;
    private File jcompilo;
    private Environment env;

    @Before
    public void setUp() throws Exception {
        env = Environment.constructors.environment();
        compiler = compiler(env, jars(env.workingDirectory(), "lib"));
        jcompilo = emptyTemporaryDirectory("jcompilo");
    }

    @Test
    public void canCompilerADirectoryDirectltToAJar() throws Exception {
        File input = directory(env.workingDirectory(), "example/src");
        File output = file(jcompilo, "example.jar");
        compiler.compile(input, output);
        assertThat(jarContains(output, "com/example/HelloWorld.class"), is(true));
//        assertThat(jarContains(output, "com/example/HelloWorld.java"), is(true));
        assertThat(jarContains(output, "com/example/resource.txt"), is(true));
        assertThat(jarContains(output, "com/example/sub/another.txt"), is(true));
    }

    @Test
    public void canCompilerADirectoryToAnotherDirectory() throws Exception {
        File input = directory(env.workingDirectory(), "example/src");
        File output = directory(env.workingDirectory(), "example/build/artifacts/compiled");
        Files.deleteFiles(output);
        compiler.compile(input, output);
        assertThat(dirContains(output, "com/example/HelloWorld.class"), is(true));
//        assertThat(dirContains(output, "com/example/HelloWorld.java"), is(true));
        assertThat(dirContains(output, "com/example/resource.txt"), is(true));
        assertThat(dirContains(output, "com/example/sub/another.txt"), is(true));
        compiler.compile(input, output);
    }

    public static boolean dirContains(File directory, final String name) throws FileNotFoundException {
        return file(directory, name).exists();
    }

    public static boolean jarContains(File jar, final String name) throws FileNotFoundException {
        return using(new ZipInputStream(new FileInputStream(jar)),
                zipInputStream -> Zip.entries(zipInputStream).exists(where(name(), Predicates.is(name))));
    }

    public static Function1<ZipEntry, String> name() {
        return zipEntry -> zipEntry.getName();
    }

    private Sequence<File> jars(File libDir, final String name) {
        return recursiveFiles(directory(libDir, name)).filter(hasSuffix("jar")).realise();
    }

}
