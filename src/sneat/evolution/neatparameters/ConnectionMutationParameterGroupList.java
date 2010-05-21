package sneat.evolution.neatparameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectionMutationParameterGroupList {
    final private ArrayList<ConnectionMutationParameterGroup> groupList = new ArrayList<ConnectionMutationParameterGroup>();

    public ConnectionMutationParameterGroupList() {
    }

/// <summary>

    /// Copy constructor.
/// </summary>
    public ConnectionMutationParameterGroupList(ConnectionMutationParameterGroupList copyFrom) {
        for (ConnectionMutationParameterGroup paramGroup : copyFrom.getGroupList()) {
            add(new ConnectionMutationParameterGroup(paramGroup));
        }
    }

    public void add(ConnectionMutationParameterGroup connectionMutationParameterGroup) {
        groupList.add(connectionMutationParameterGroup);
    }

    public List<ConnectionMutationParameterGroup> getGroupList() {
        return Collections.unmodifiableList(groupList);
    }
}
