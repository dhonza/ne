package hyper.builder;

import hyper.cppn.ICPPN;
import hyper.substrate.ISubstrate;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 14, 2009
 * Time: 11:30:47 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ISubstrateBuilder extends Serializable {

    public ISubstrate getSubstrate();

    /**
     * @param aCPPN
     * @throws IllegalStateException when missing output layer, unconnected layers, etc.
     */
    public void build(final ICPPN aCPPN);
}
