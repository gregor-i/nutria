package nutria.benchmark;

import org.openjdk.jmh.annotations.Benchmark;

public class ForBenchmarkJava {
    public static int n = 10000;

    @Benchmark
    public double defaultJava() {
        double a = 0;
        for(int i=0; i< n; i++)
            a += i;
        return a;
    }
}
