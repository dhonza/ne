package sneat.neatgenome.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sneat.neatgenome.*;
import sneat.neuralnetwork.activationfunctions.ActivationFunctionFactory;
import sneat.neuralnetwork.concurrentnetwork.NeuronType;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlNeatGenomeReaderStatic {
    public static NeatGenome Read(File file) {
        Document doc = createDoc(file);
        Element xmlGenome = doc.getDocumentElement();

        if (xmlGenome == null || !xmlGenome.getNodeName().equals("genome")) {
            throw new IllegalStateException("The genome XML is missing the root 'genome' element.");
        }
        return Read(xmlGenome);
    }

    public static NeatGenome Read(Element xmlGenome) {
        int inputNeuronCount = 0;
        int outputNeuronCount = 0;

        int id = Integer.valueOf(xmlGenome.getAttribute("id"));

        //--- Read neuron genes into a list.
        NeuronGeneList neuronGeneList = new NeuronGeneList();
        NodeList listNeuronGenes = xmlGenome.getElementsByTagName("neuron");

        for (int i = 0; i < listNeuronGenes.getLength(); i++) {
            Node xmlNeuronGene = listNeuronGenes.item(i);
            NeuronGene neuronGene = ReadNeuronGene((Element) xmlNeuronGene);

            // Count the input and output neurons as we go.
            switch (neuronGene.getNeuronType()) {
                case INPUT:
                    inputNeuronCount++;
                    break;
                case OUTPUT:
                    outputNeuronCount++;
                    break;
            }
            neuronGeneList.add(neuronGene);
        }

        //--- Read connection genes into a list.
        ConnectionGeneList connectionGeneList = new ConnectionGeneList();
        NodeList listConnectionGenes = xmlGenome.getElementsByTagName("connection");
        for (int i = 0; i < listConnectionGenes.getLength(); i++) {
            Node xmlConnectionGene = listConnectionGenes.item(i);
            connectionGeneList.add(ReadConnectionGene((Element) xmlConnectionGene));
        }
        return new NeatGenome(id, neuronGeneList, connectionGeneList, inputNeuronCount, outputNeuronCount);
    }

    private static NeuronGene ReadNeuronGene(Element xmlNeuronGene) {
        int id = Integer.valueOf(xmlNeuronGene.getAttribute("id"));
        NeuronType neuronType = NeuronType.createFromShort(xmlNeuronGene.getAttribute("type"));
        String activationFn = xmlNeuronGene.getAttribute("activationFunction");
        return new NeuronGene(id, neuronType, ActivationFunctionFactory.getActivationFunction(activationFn));
    }

    private static ConnectionGene ReadConnectionGene(Element xmlConnectionGene) {
        int innovationId = Integer.valueOf(xmlConnectionGene.getAttribute("innov-id"));
        int sourceNeuronId = Integer.valueOf(xmlConnectionGene.getAttribute("src-id"));
        int targetNeuronId = Integer.valueOf(xmlConnectionGene.getAttribute("tgt-id"));
        double weight = Double.valueOf(xmlConnectionGene.getAttribute("weight"));

        return new ConnectionGene(innovationId, sourceNeuronId, targetNeuronId, weight);
    }

    private static Document createDoc(File file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
