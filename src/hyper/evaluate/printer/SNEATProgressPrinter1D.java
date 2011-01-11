package hyper.evaluate.printer;

import common.evolution.IProgressPrinter;
import common.pmatrix.ParameterCombination;
import hyper.cppn.BasicSNEATCPPN;
import hyper.cppn.ICPPN;
import hyper.evaluate.IProblem;
import hyper.evaluate.storage.GenomeStorage;
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

    public SNEATProgressPrinter1D(SNEAT sneat, IProgressPrinter progressPrinter, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        super(progressPrinter, problem, reportStorage, parameters);
        this.sneat = sneat;
    }

    public SNEATProgressPrinter1D(SNEAT sneat, IProblem problem, ReportStorage reportStorage, ParameterCombination parameters) {
        this(sneat, new SNEATBasicProgressPrinter(sneat), problem, reportStorage, parameters);
    }

    @Override
    protected ICPPN createBSFCPPN() {
        INetwork network = sneat.getEA().getBestGenome().decode(null);
        return new BasicSNEATCPPN(network, problem.getSubstrate().getMaxDimension());
    }

    @Override
    protected void storeBSFCPPN(String fileName) {
        GenomeStorage.saveGenome(parameters,
                sneat.getEA().getBestGenome(),
                reportStorage.getCompleteFilename(fileName, ".xml")
        );
    }
}