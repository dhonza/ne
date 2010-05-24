package hyper.evaluate.printer;

import common.evolution.ProgressPrinter;
import hyper.cppn.BasicSNEATCPPN;
import hyper.cppn.CPPN;
import hyper.evaluate.Problem;
import hyper.substrate.Substrate;
import sneat.SNEAT;
import sneat.SNEATBasicProgressPrinter;
import sneat.neatgenome.NeatGenome;
import sneat.neatgenome.xml.XmlGenomeWriterStatic;
import sneat.neuralnetwork.INetwork;
import sneat.neuralnetwork.activationfunctions.ActivationFunctionFactory;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jun 17, 2009
 * Time: 2:57:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SNEATProgressPrinter1D extends CommonProgressPrinter1D {
    final private SNEAT sneat;

    public SNEATProgressPrinter1D(SNEAT sneat, ProgressPrinter progressPrinter, Substrate substrate, Problem problem) {
        super(progressPrinter, substrate, problem);
        this.sneat = sneat;
    }

    public SNEATProgressPrinter1D(SNEAT sneat, Substrate substrate, Problem problem) {
        this(sneat, new SNEATBasicProgressPrinter(sneat), substrate, problem);
    }

    @Override
    protected CPPN createBSFCPPN() {
        INetwork network = sneat.getEA().getBestGenome().decode(null);
        return new BasicSNEATCPPN(network, substrate.getMaxDimension());
    }

    @Override
    protected void storeBSFCPPN(String fileName) {
        XmlGenomeWriterStatic.Write(new File(fileName), (NeatGenome) sneat.getEA().getBestGenome(), ActivationFunctionFactory.getActivationFunction("NullFn"));
    }
}