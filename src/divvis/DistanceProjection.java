package divvis;

import common.RND;

/**
 * User: honza
 * Date: May 30, 2007
 * Time: 8:41:49 PM
 * The main class which projects multidimensional data to 2D.
 */
public class DistanceProjection {
    protected double[][] D;
    protected double E;
    protected double[] p;// projection

    protected int n;
    protected int args;

    protected EquationsInterface equations;
    protected OptimizeInterface optimizer;

    protected boolean projected = false;

    public DistanceProjection(double[][] oD, EquationsInterface oequations, OptimizeInterface ooptimizer) {
        D = oD;
        n = D.length;
        args = oequations.getDimension() * n;
        equations = oequations;
        optimizer = ooptimizer;
    }

    public DistanceProjection(double[][] oD, EquationsInterface oequations) {
        this(oD, oequations, new OptimizeConjugateGradient(oD, oequations));
    }

    public DistanceProjection(double[][] oD) {
        this(oD, EquationsFactory.createDefault());
    }


    public int getN() {
        return n;
    }

    public EquationsInterface getEquations() {
        return equations;
    }

    public void project(double[] oinit) {
        p = oinit;
        E = optimizer.minimize(p);
        moveCenter();
        projected = true;
    }

    public void project() {
        project(randomInitialize());
    }

    public double[] randomInitialize() {
        double aa = 0.1;
        p = new double[args];

        for (int i = 0; i < args; i++) {
            p[i] = RND.getDouble(-aa, aa);
        }
        return p;
    }

    public double[] getProjection() {
        return p;
    }

    public void getProjectionXArray(double[] ox) {
        System.arraycopy(p, 0, ox, 0, n);
    }

    public void getProjectionYArray(double[] oy) {
        System.arraycopy(p, n, oy, 0, n);
    }

    public void getProjectionZArray(double[] oz) {
        System.arraycopy(p, 2 * n, oz, 0, n);
    }

    public double getEnergy() {
        if (projected) {
            return E;
        }
        return Double.NaN;
    }

    public void rescale(double ox1, double oy1, double ox2, double oy2) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        double x, y;
        //find min and max
        for (int i = 0, j = n; i < n; i++, j++) {
            x = p[i];
            y = p[j];
            if (minX > x) {
                minX = x;
            }
            if (minY > y) {
                minY = y;
            }
            if (maxX < x) {
                maxX = x;
            }
            if (maxY < y) {
                maxY = y;
            }
        }
        double kx = (ox2 - ox1) / (maxX - minX);
        double ky = (oy2 - oy1) / (maxY - minY);

        if (kx > ky) {
            kx = ky;
        }

        for (int i = 0, j = n; i < n; i++, j++) {
            p[i] = kx * (p[i] - minX) + ox1;
            p[j] = kx * (p[j] - minY) + oy1;
        }
    }

    public void rescale3d(double ox1, double oy1, double oz1, double ox2, double oy2, double oz2) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double maxZ = Double.MIN_VALUE;

        double x, y, z;
        //find min and max
        for (int i = 0, j = n, k = 2 * n; i < n; i++, j++, k++) {
            x = p[i];
            y = p[j];
            z = p[k];
            if (minX > x) {
                minX = x;
            }
            if (minY > y) {
                minY = y;
            }
            if (minZ > z) {
                minZ = z;
            }
            if (maxX < x) {
                maxX = x;
            }
            if (maxY < y) {
                maxY = y;
            }
            if (maxZ < z) {
                maxZ = z;
            }
        }
        double kx = (ox2 - ox1) / (maxX - minX);
        double ky = (oy2 - oy1) / (maxY - minY);
        double kz = (oz2 - oz1) / (maxZ - minZ);

        if (kx > ky) {
            kx = ky;
        }

        if (kx > kz) {
            kx = kz;
        }

        for (int i = 0, j = n, k = 2 * n; i < n; i++, j++, k++) {
            p[i] = kx * (p[i] - minX) + ox1;
            p[j] = kx * (p[j] - minY) + oy1;
            p[k] = kx * (p[k] - minZ) + oz1;
        }
    }

    public void moveCenter() {
        double offX = 0.0; //offset
        double offY = 0.0;

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        double x, y;
        //find min and max
        for (int i = 0, j = n; i < n; i++, j++) {
            x = p[i];
            y = p[j];
            if (minX > x) {
                minX = x;
            }
            if (minY > y) {
                minY = y;
            }
            if (maxX < x) {
                maxX = x;
            }
            if (maxY < y) {
                maxY = y;
            }
        }

        double cenX = minX + (maxX - minX) / 2.0;
        double cenY = minY + (maxY - minY) / 2.0;

        for (int i = 0, j = n; i < n; i++, j++) {
            p[i] = p[i] - cenX + offX;
            p[j] = p[j] - cenY + offY;
        }
    }

    public void moveCenter3d() {
        //TODO combine with rescale3d
        double offX = 0.0; //offset
        double offY = 0.0;
        double offZ = 0.0;

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double maxZ = Double.MIN_VALUE;

        double x, y, z;
        //find min and max
        for (int i = 0, j = n, k = 2 * n; i < n; i++, j++, k++) {
            x = p[i];
            y = p[j];
            z = p[k];
            if (minX > x) {
                minX = x;
            }
            if (minY > y) {
                minY = y;
            }
            if (minZ > z) {
                minZ = z;
            }
            if (maxX < x) {
                maxX = x;
            }
            if (maxY < y) {
                maxY = y;
            }
            if (maxZ < z) {
                maxZ = z;
            }
        }

        double cenX = minX + (maxX - minX) / 2.0;
        double cenY = minY + (maxY - minY) / 2.0;
        double cenZ = minZ + (maxZ - minZ) / 2.0;

        for (int i = 0, j = n, k = 2 * n; i < n; i++, j++, k++) {
            p[i] = p[i] - cenX + offX;
            p[j] = p[j] - cenY + offY;
            p[k] = p[k] - cenZ + offZ;
        }
    }

    public int getNumEvaluateCalls() {
        return optimizer.getNumEvaluateCalls();
    }

    public int getNumGradientCalls() {
        return optimizer.getNumGradientCalls();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < equations.getDimension() - 1; j++) {
                b.append(p[j * n + i]);
                b.append("\t");
            }
            b.append(p[(equations.getDimension() - 1) * n + i]);
            if (i != n - 1) {
                b.append("\n");
            }
        }
        return b.toString();
    }
}
