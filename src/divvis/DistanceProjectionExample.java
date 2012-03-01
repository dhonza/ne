package divvis;

import common.RND;

/**
 * User: honza
 * Date: May 31, 2007
 * Time: 10:27:07 AM
 */
public class DistanceProjectionExample {
    public static void main(String[] args) {
        RND.initializeTime();
        int[] clusters = {3, 5};
        double[][] D = ArtificialClusterGenerator.generateClusters(clusters, 0.1, 0.1, 0.9, 0.9);
        DistanceProjection prj = new DistanceProjection(D);
        prj.project();
        System.out.println("prj.getEnergy() = " + prj.getEnergy());
    }
}
