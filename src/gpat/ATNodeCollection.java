package gpat;

import common.RND;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Nov 3, 2010
 * Time: 3:39:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATNodeCollection {
    final protected ATNodeImpl[] functions;
    //terminals, input terminals in this order here
    final protected ATNodeImpl[] terminals;
    //functions, terminals, input terminals in this order here
    final protected ATNodeImpl[] all;
    final protected int indexOfFirstInput;

    public ATNodeCollection(ATNodeImpl[] functions, ATNodeImpl[] terminals, int numOfInputs) {
        this.indexOfFirstInput = terminals.length;
        this.functions = functions.clone();
        this.terminals = addInputs(terminals, numOfInputs);
        this.all = new ATNodeImpl[this.functions.length + this.terminals.length];
        System.arraycopy(this.functions, 0, all, 0, this.functions.length);
        System.arraycopy(this.terminals, 0, all, this.functions.length, this.terminals.length);
    }

    protected ATNodeImpl[] addInputs(ATNodeImpl[] terminals, int numOfInputs) {
        ATNodeImpl[] allTerminals = new ATNodeImpl[terminals.length + numOfInputs];

        System.arraycopy(terminals, 0, allTerminals, 0, terminals.length);
        for (int i = 0; i < numOfInputs; i++) {
            allTerminals[terminals.length + i] = new ATTerminals.Input(i);
        }
        return allTerminals;
    }

    protected ATNodeImpl randomTerminal() {
        return RND.randomChoice(terminals);
    }

    protected ATNodeImpl randomFunction() {
        return RND.randomChoice(functions);
    }

    //index of a first input in "terminals"
    protected int getIndexOfFirstInput() {
        return indexOfFirstInput;
    }
}
