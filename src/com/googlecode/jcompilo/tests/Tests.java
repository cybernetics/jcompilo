package com.googlecode.jcompilo.tests;

import com.googlecode.jcompilo.Environment;
import com.googlecode.jcompilo.Inputs;
import com.googlecode.jcompilo.Outputs;
import com.googlecode.jcompilo.Processes;
import com.googlecode.jcompilo.Processor;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Streams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.jcompilo.BootStrap.jarFile;
import static com.googlecode.jcompilo.Compiler.CPUS;
import static com.googlecode.totallylazy.Sequences.cons;
import static com.googlecode.totallylazy.Sequences.empty;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.endsWith;
import static java.io.File.pathSeparator;

public class Tests implements Processor {
    public static final Sequence<String> debugJvm = sequence("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005");
    private final List<String> tests = new ArrayList<String>();
    private final Predicate<? super String> predicate;
    private final Sequence<File> dependencies;
    private final Environment environment;
    private final int numberOfThreads;
    private final boolean debug;
    private final File reportsDirectory;

    private Tests(Environment environment, Sequence<File> dependencies, int threads, File reportsDirectory, Predicate<? super String> predicate, boolean debug) {
        this.environment = environment;
        this.dependencies = dependencies;
        this.numberOfThreads = threads;
        this.predicate = predicate;
        this.debug = debug;
        this.reportsDirectory = reportsDirectory;
    }

    public static Tests tests(Environment env, final Sequence<File> dependencies, final File reportsDirectory) {
        return tests(env, dependencies, CPUS, reportsDirectory, false);
    }

    public static Tests tests(Environment env, final Sequence<File> dependencies, final int threads, final File reportsDirectory, final boolean debug) {
        return tests(env, dependencies, threads, reportsDirectory, endsWith("Test.java"), debug);
    }

    public static Tests tests(Environment env, final Sequence<File> dependencies, final int threads, final File reportsDirectory, Predicate<? super String> predicate, final boolean debug) {
        return new Tests(env, dependencies, threads, reportsDirectory, predicate, debug);
    }

    @Override
    public boolean process(Inputs inputs, Outputs outputs) throws Exception {
        return true;
    }

    @Override
    public boolean matches(String other) {
        boolean matched = predicate.matches(other);
        if (matched) tests.add(other);
        return matched;
    }

    public boolean execute(File testJar) throws Exception {
        if(!tests.isEmpty()) {
            try {
                environment.out().prefix("    [junit] ");
                environment.out().printf("Running %s tests classes on %s threads%n", tests.size(), numberOfThreads);
                List<String> arguments = cons(javaProcess(), debug().join(sequence("-cp", dependencies.cons(testJar).cons(jarFile(getClass())).toString(pathSeparator),
                        "com.googlecode.jcompilo.tests.junit.TestExecutor", String.valueOf(numberOfThreads), reportsDirectory.toString()))).toList();
                arguments.addAll(sequence(tests).toList());
                Process process = Processes.execute(arguments, environment.workingDirectory());
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    Streams.copy(process.getInputStream(), environment.out());
                    return false;
                }
            } finally {
                environment.out().clearPrefix();
            }
        }
        return true;
    }

    private String javaProcess() {
        return environment.properties().getProperty("java.home") + "/bin/java";
    }

    private Sequence<String> debug() {
        if (debug) {
            environment.out().println("Debugging tests running with " + debugJvm.toString(" "));
            return debugJvm;
        } else return empty(String.class);
    }
}