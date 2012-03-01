package divvis;

/**
 * User: drchaj1
 * Date: 27.3.2007
 * Time: 16:03:44
 */
public class EquationsA implements EquationsInterface {
    private final double a = 1.005;
    private final double a2 = a * a; // a^2
    private final double b = 1.1;

    public int getDimension() {
        return 2;
    }

    public double evaluate(double[][] oD, double[] op) {
        int n = oD.length;
        double x0;
        double y0;
        double xt;
        double yt;
        double d;
        double l;
        double E = 0.0;
        for (int ix = 0, iy = n; ix < n - 1; ix++, iy++) {
            x0 = op[ix];
            y0 = op[iy];
            for (int jx = ix + 1, jy = iy + 1; jx < n; jx++, jy++) {
                xt = op[jx];
                yt = op[jy];

                d = oD[ix][jx];
                l = Math.sqrt((xt - x0) * (xt - x0) + (yt - y0) * (yt - y0));

                E += (1.0 / n) * (l / (d + b) - (1.0 / a) * (Math.atan(l / a))); // A
            }
        }
        return E;
    }

    public void gradient(double[][] oD, double[] op, double[] oforce) {
        int n = oD.length;
        double x0, y0;
        double xt, yt;
        double d;
        double l2; // l^2
        double l;
        double db; // d+b
        double t;
        for (int ix = 0, iy = n; ix < n; ix++, iy++) {
            x0 = op[ix];
            y0 = op[iy];
            oforce[ix] = 0.0; // set gradient to zero
            oforce[iy] = 0.0;
            for (int jx = 0, jy = n; jx < n; jx++, jy++) {
                if (ix != jx) {
                    xt = op[jx];
                    yt = op[jy];
                    d = oD[ix][jx];
                    l2 = (xt - x0) * (xt - x0) + (yt - y0) * (yt - y0);
                    l = Math.sqrt(l2);

                    db = d + b; // A
                    t = (a2 + l2 - db) / (n * l * db * (a2 + l2)); // A

                    oforce[ix] += (x0 - xt) * t;
                    oforce[iy] += (y0 - yt) * t;
                }
            }
        }
    }
}
