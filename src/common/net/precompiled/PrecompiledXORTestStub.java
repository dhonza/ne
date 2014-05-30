package common.net.precompiled;

public class PrecompiledXORTestStub implements common.net.precompiled.IPrecompiledFeedForwardStub {
    double[] w;
    double[] p;

    public double[] propagate(double b, double[] in, double[] w) {
        this.w = w;
        p = in;
        double[] n;
        n = new double[2];
        n[0] = a(w[0] * b + s(2, 1));
        n[1] = a(w[3] * b + s(2, 4));
        p = n;
        n = new double[1];
        n[0] = a(w[6] * b + s(2, 7));
        p = n;
        return n;
    }

    @Override
    public double[] getActivities(int l) {
        throw new IllegalStateException("Not implemented!");
    }

    public double a(double s) {
        return 1 / (1 + Math.exp(-4.924273 * s));
    }

    public int getNumberOfInputs() {
        return 2;
    }

    @Override
    public int getNumberOfLayers() {
        throw new IllegalStateException("Not implemented!");
    }

    public double s(int n, int wc) {
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += w[(wc + i)] * p[i];
        }
        return sum;
    }

}
