package divvis;

/**
 * User: honza
 * Date: May 31, 2007
 * Time: 4:05:17 PM
 * TODO [1] Lukasova, Sarmanova: Metody shlukove analyzy
 */
public class DataPreprocessing {
    public static void standardize(double[][] odata) {
        int n = odata.length;
        double dn = n;
        int f = odata[0].length; //#features
        double z, s;
        for (int i = 0; i < f; i++) {
            z = 0;
            s = 0;
            for (int j = 0; j < n; j++) {
                z += odata[j][i];
            }

            z /= dn;

            for (int j = 0; j < n; j++) {
                s += (odata[j][i] - z) * (odata[j][i] - z);
            }

            s = Math.sqrt(s / dn);

            for (int j = 0; j < n; j++) {
                odata[j][i] = (odata[j][i] - z) / s;
                if (odata[j][i] == Double.NaN) {//preventing NaN
                    odata[j][i] = 0.0;
                }
            }
        }
    }
}
