package divvis;

/**
 * User: drchaj1
 * Date: 27.3.2007
 * Time: 16:08:50
 */
public class EquationsB3D implements EquationsInterface {
    //    private final double ac = 0.1;
    private final double ac = 0.000001;

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
                E += 1.0 / (l + ac) + l / ((d + ac) * (d + ac)) - 2 / (d + ac); // B 3D
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
                    t = -1.0 / ((l + ac) * (l + ac)) + 1.0 / ((d + ac) * (d + ac)); // B 3D
                    oforce[ix] += (x0 - xt) * t / l;
                    oforce[iy] += (y0 - yt) * t / l;
                    oforce[iz] += (z0 - zt) * t / l;
//                    oforce[ix] += (x0 - xt) * t;// / l;
//                    oforce[iy] += (y0 - yt) * t;// / l;
//                    oforce[iz] += (z0 - zt) * t;// / l;                    
                }
            }
        }
    }
}
