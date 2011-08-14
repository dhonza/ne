package gpat;

import common.RND;

import java.util.ArrayList;
import java.util.List;

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

    //creates an array of all terminals including inputs. At first it takes user supplied terminals (typically constants),
    //then appends one terminal for each input.
    protected ATNodeImpl[] addInputs(ATNodeImpl[] terminals, int numOfInputs) {
        ATNodeImpl[] allTerminals = new ATNodeImpl[terminals.length + numOfInputs];

        System.arraycopy(terminals, 0, allTerminals, 0, terminals.length);
        for (int i = 0; i < numOfInputs; i++) {
            allTerminals[terminals.length + i] = new ATTerminals.Input(i);
        }
        return allTerminals;
    }

    //true for input, false for constants
    protected boolean isInputById(int id) {
        return id >= getIndexOfFirstInput();
    }

    protected ATNodeImpl terminalWithId(int id) {
        return terminals[id];
    }

    protected ATNodeImpl randomTerminal() {
        return RND.randomChoice(terminals);
    }

    protected ATNodeImpl randomFunction() {
        return RND.randomChoice(functions);
    }

    protected ATNodeImpl randomFunction(int maxArity) {
        List<ATNodeImpl> functionChoice = new ArrayList<ATNodeImpl>();
        for (ATNodeImpl node : functions) {
            if (node.maxArity() == maxArity) {
                functionChoice.add(node);
            }
        }
        return RND.randomChoice(functionChoice);
    }

    protected ATNodeImpl newFunctionByName(String name) {
        for (ATNodeImpl function : functions) {
            if (function.getName().equals(name)) {
                return ATNodeFactory.createByName(function.getClass().getName());
            }
        }
        throw new IllegalArgumentException("Function: " + name + " does not exist.");
    }

    //index of a first input in "terminals"
    protected int getIndexOfFirstInput() {
        return indexOfFirstInput;
    }
}
