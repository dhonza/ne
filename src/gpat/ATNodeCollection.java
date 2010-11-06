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
    final protected ATNode[] functions;
    final protected ATNode[] terminals;
    final protected ATNode[] all;

    public ATNodeCollection(ATNode[] functions, ATNode[] terminals, int numOfInputs) {
        this.functions = functions.clone();
        this.terminals = addInputs(terminals, numOfInputs);
        this.all = new ATNode[this.functions.length + this.terminals.length];
        System.arraycopy(this.functions, 0, all, 0, this.functions.length);
        System.arraycopy(this.terminals, 0, all, this.functions.length, this.terminals.length);
    }

    protected ATNode[] addInputs(ATNode[] terminals, int numOfInputs) {
        ATNode[] allTerminals = new ATNode[terminals.length + numOfInputs];

        System.arraycopy(terminals, 0, allTerminals, 0, terminals.length);
        for (int i = 0; i < numOfInputs; i++) {
            allTerminals[terminals.length + i] = new ATTerminals.Input(i);
        }
        return allTerminals;
    }

    protected ATNode randomTerminal() {
        return (ATNode) RND.randomChoice(terminals);
    }
}
