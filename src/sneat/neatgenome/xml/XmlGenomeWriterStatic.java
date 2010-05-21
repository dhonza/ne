package sneat.neatgenome.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sneat.neatgenome.ConnectionGene;
import sneat.neatgenome.NeatGenome;
import sneat.neatgenome.NeuronGene;
import sneat.neuralnetwork.IActivationFunction;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XmlGenomeWriterStatic {
    public static void Write(File file, NeatGenome genome) {
        Document doc = createDoc();

        //----- Start writing. Create document root node.
        Element xmlGenome = doc.createElement("genome");
        doc.appendChild(xmlGenome);

        xmlGenome.setAttribute("id", String.valueOf(genome.getGenomeId()));
        xmlGenome.setAttribute("species-id", String.valueOf(genome.getSpeciesId()));
        xmlGenome.setAttribute("age", String.valueOf(genome.getGenomeAge()));
        xmlGenome.setAttribute("fitness", String.valueOf(genome.getFitness()));

        //----- Write neurons.
        Element xmlNeurons = doc.createElement("neurons");
        xmlGenome.appendChild(xmlNeurons);
        for (NeuronGene neuronGene : genome.getNeuronGeneList().getList())
            WriteNeuron(doc, xmlNeurons, neuronGene);

        //----- Write Connections.
        Element xmlConnections = doc.createElement("connections");
        xmlGenome.appendChild(xmlConnections);
        for (ConnectionGene connectionGene : genome.getConnectionGeneList().getList())
            WriteConnectionGene(doc, xmlConnections, connectionGene);

        saveDoc(doc, file);
    }

    /// <summary>
    ///
    /// </summary>
    /// <param name="doc"></param>
    /// <param name="genome"></param>
    /// <param name="activationFn">Not strictly part of a genome. But it is useful to document which function
    /// the genome is supposed to run against when decoded into a network.</param>
    public static void Write(File file, NeatGenome genome, IActivationFunction activationFn) {
        Document doc = createDoc();

        //----- Start writing. Create document root node.
        Element xmlGenome = doc.createElement("genome");
        doc.appendChild(xmlGenome);

        xmlGenome.setAttribute("id", String.valueOf(genome.getGenomeId()));
        xmlGenome.setAttribute("species-id", String.valueOf(genome.getSpeciesId()));
        xmlGenome.setAttribute("age", String.valueOf(genome.getGenomeAge()));
        xmlGenome.setAttribute("fitness", String.valueOf(genome.getFitness()));
        xmlGenome.setAttribute("activation-fn-id", activationFn.getFunctionId());

        //----- Write neurons.
        Element xmlNeurons = doc.createElement("neurons");
        xmlGenome.appendChild(xmlNeurons);
        for (NeuronGene neuronGene : genome.getNeuronGeneList().getList())
            WriteNeuron(doc, xmlNeurons, neuronGene);

        //----- Write Connections.
        Element xmlConnections = doc.createElement("connections");
        xmlGenome.appendChild(xmlConnections);
        for (ConnectionGene connectionGene : genome.getConnectionGeneList().getList())
            WriteConnectionGene(doc, xmlConnections, connectionGene);

        saveDoc(doc, file);
    }

    private static void WriteNeuron(Document doc, Element xmlNeurons, NeuronGene neuronGene) {
        Element xmlNeuron = doc.createElement("neuron");
        xmlNeurons.appendChild(xmlNeuron);


        xmlNeuron.setAttribute("id", String.valueOf(neuronGene.getInnovationId()));
        xmlNeuron.setAttribute("type", neuronGene.getNeuronType().getShortName());
        xmlNeuron.setAttribute("activationFunction", neuronGene.getActivationFunction().getFunctionId());
    }

    private static void WriteConnectionGene(Document doc, Element xmlConnections, ConnectionGene connectionGene) {
        Element xmlConnectionGene = doc.createElement("connection");
        xmlConnections.appendChild(xmlConnectionGene);

        xmlConnectionGene.setAttribute("innov-id", String.valueOf(connectionGene.getInnovationId()));
        xmlConnectionGene.setAttribute("src-id", String.valueOf(connectionGene.getSourceNeuronId()));
        xmlConnectionGene.setAttribute("tgt-id", String.valueOf(connectionGene.getTargetNeuronId()));
        xmlConnectionGene.setAttribute("weight", String.valueOf(connectionGene.getWeight()));
    }

    private static Document createDoc() {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            return doc;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void saveDoc(Document doc, File file) {
        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = null;
        try {
            trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }

        //create string from xml tree

        try {
            BufferedOutputStream bs = new BufferedOutputStream(new FileOutputStream(file));
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(bs);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            bs.close();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
