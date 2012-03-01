package divvis;

/**
 * User: honza
 * Date: Aug 27, 2007
 * Time: 10:54:43 PM
 */
public class EquationsA3D implements EquationsInterface {
    private final double a = 1.005;
    private final double a2 = a * a; // a^2
    private final double b = 1.1;

    public int getDimension() {
        return 3;
    }

    public double evaluate(double[][] oD, double[] op) {
        int n = oD.length;
        double x0;
        double y0;
        double z0;
        double xt;
        double yt;
        double zt;
        double d;
        double l;
        double E = 0.0;
        for (int ix = 0, iy = n, iz = iy + n; ix < n - 1; ix++, iy++, iz++) {
            x0 = op[ix];
            y0 = op[iy];
            z0 = op[iz];
            for (int jx = ix + 1, jy = iy + 1, jz = iz + 1; jx < n; jx++, jy++, jz++) {
                xt = op[jx];
                yt = op[jy];
                zt = op[jz];

                d = oD[ix][jx];
                l = Math.sqrt((xt - x0) * (xt - x0) + (yt - y0) * (yt - y0) + (zt - z0) * (zt - z0));

                E += (1.0 / n) * (l / (d + b) - (1.0 / a) * (Math.atan(l / a))); // A 3D
            }
        }
        return E;
    }

    public void gradient(double[][] oD, double[] op, double[] oforce) {
        int n = oD.length;
        double x0, y0, z0;
        double xt, yt, zt;
        double d;
        double l2; // l^2
        double l;
        double db; // d+b
        double t;
        for (int ix = 0, iy = n, iz = iy + n; ix < n; ix++, iy++, iz++) {
            x0 = op[ix];
            y0 = op[iy];
            z0 = op[iz];
            oforce[ix] = 0.0; // set gradient to zero
            oforce[iy] = 0.0;
            oforce[iz] = 0.0;
            for (int jx = 0, jy = n, jz = jy + n; jx < n; jx++, jy++, jz++) {
                if (ix != jx) {
                    xt = op[jx];
                    yt = op[jy];
                    zt = op[jz];
                    d = oD[ix][jx];
                    l2 = (xt - x0) * (xt - x0) + (yt - y0) * (yt - y0) + (zt - z0) * (zt - z0);
                    l = Math.sqrt(l2);

                    db = d + b; // A 3D
                    t = (a2 + l2 - db) / (n * l * db * (a2 + l2)); // A

                    oforce[ix] += (x0 - xt) * t;
                    oforce[iy] += (y0 - yt) * t;
                    oforce[iz] += (z0 - zt) * t;
                }
            }
        }
    }
}
