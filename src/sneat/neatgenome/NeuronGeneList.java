package sneat.neatgenome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NeuronGeneList {
    List<NeuronGene> l;
    final static NeuronGeneComparer neuronGeneComparer = new NeuronGeneComparer();
    public boolean orderInvalidated = false;


    /// <summary>
    /// Default constructor.
    /// </summary>
    public NeuronGeneList() {
        l = new ArrayList<NeuronGene>();
    }

    public NeuronGeneList(int count) {
        l = new ArrayList<NeuronGene>((int) (count * 1.5));
    }

    /// <summary>
    /// Copy constructor.
    /// </summary>
    /// <param name="copyFrom"></param>
    public NeuronGeneList(NeuronGeneList copyFrom) {
        int count = copyFrom.size();
        l = new ArrayList<NeuronGene>(count);

        for (int i = 0; i < count; i++)
            this.add(new NeuronGene(copyFrom.get(i)));

//			foreach(NeuronGene neuronGene in copyFrom)
//				InnerList.Add(new NeuronGene(neuronGene));
    }

    public void add(NeuronGene neuronGene) {
        if (l == null) {
            System.out.println("burn");
        }
        l.add(neuronGene);
    }


    public NeuronGene get(int i) {
        return l.get(i);
    }

    public int size() {
        return l.size();
    }

    public List<NeuronGene> getList() {
        return Collections.unmodifiableList(l);
    }

    public void remove(NeuronGene neuronGene) {
        System.out.println("WARNING: originally RemoveAt? CHECK");
        remove(neuronGene.getInnovationId());

        // This invokes a linear search. Invoke our binary search instead.
        //InnerList.Remove(neuronGene);
    }

    public void remove(int neuronId) {
        int idx = binarySearch(neuronId);
        if (idx < 0)
            throw new IllegalStateException("Attempt to remove neuron with an unknown neuronId");
        else
            l.remove(idx);

//			// Inefficient scan through the neuron list.
//			// TODO: Implement a binary search method for NeuronList (Will generics resolve this problem anyway?).
//			int bound = List.Count;
//			for(int i=0; i<bound; i++)
//			{
//				if(((NeuronGene)List[i]).InnovationId == neuronId)
//				{
//					InnerList.RemoveAt(i);
//					return;
//				}
//			}
//			throw new ApplicationException("Attempt to remove neuron with an unknown neuronId");
    }

    public NeuronGene getNeuronById(int neuronId) {
        int idx = binarySearch(neuronId);
        if (idx < 0)
            return null;
        else
            return l.get(idx);

//			// Inefficient scan through the neuron list.
//			// TODO: Implement a binary search method for NeuronList (Will generics resolve this problem anyway?).
//			int bound = List.Count;
//			for(int i=0; i<bound; i++)
//			{
//				if(((NeuronGene)List[i]).InnovationId == neuronId)
//					return (NeuronGene)List[i];
//			}
//
//			// Not found;
//			return null;
    }

    public void sortByInnovationId() {
        Collections.sort(l, neuronGeneComparer);
        orderInvalidated = false;
    }

    public int binarySearch(int innovationId) {
        int lo = 0;
        int hi = l.size() - 1;

        while (lo <= hi) {
            int i = (lo + hi) >> 1;

            if (l.get(i).getInnovationId() < innovationId)
                lo = i + 1;
            else if (l.get(i).getInnovationId() > innovationId)
                hi = i - 1;
            else
                return i;


            // TODO: This is wrong. It will fail for large innovation numbers because they are of type uint.
            // Fortunately it's very unlikely anyone has reached such large numbers!
//				int c = (int)((NeuronGene)InnerList[i]).InnovationId - (int)innovationId;
//				if (c == 0) return i;
//
//				if (c < 0) 
//					lo = i + 1;
//				else 
//					hi = i - 1;
        }

        return ~lo;
    }

    // For debug purposes only.
//		public bool IsSorted()
//		{
//			uint prevId=0;
//			foreach(NeuronGene gene in InnerList)
//			{
//				if(gene.InnovationId<prevId)
//					return false;
//				prevId = gene.InnovationId;
//			}
//			return true;
//		}

}
