package hyper.builder.precompiled;

import hyper.substrate.layer.SubstrateInterLayerConnection;

/**
 * Created by drchajan on 22/07/14.
 */
public class PreviousLayerConnectionContainer {
    // bias connection to given layer
    public SubstrateInterLayerConnection bias;
    // connection from previous layer to given layer
    public SubstrateInterLayerConnection connection;

    public PreviousLayerConnectionContainer(SubstrateInterLayerConnection bias, SubstrateInterLayerConnection connection) {
        this.bias = bias;
        this.connection = connection;
    }
}
