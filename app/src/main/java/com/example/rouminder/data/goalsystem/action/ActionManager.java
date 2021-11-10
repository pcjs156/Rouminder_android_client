package com.example.rouminder.data.goalsystem.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActionManager {
    private final List<ActionEventHandler> handlers;
    private final Set<Action<?>> actions;
    private final Set<ActionInstance<?>> actionInstances;

    public ActionManager() {
        handlers = new ArrayList<>();
        actions = new HashSet<>();
        actionInstances = new HashSet<>();
    }

    /**
     * Add an Action to the manager.
     *
     * @param action to be added to manager.
     * @return true if successfully added, otherwise false.
     */
    public boolean addAction(Action<?> action) {
        return false;
    }


    /**
     * Add an ActionInstance to the manager.
     *
     * @param actionInstance to be added to manager.
     * @return true if successfully added, otherwise false.
     */
    public boolean addActionInstance(ActionInstance<?> actionInstance) {
        return false;
    }

    /**
     * Get a Action with specified id.
     *
     * @param id of a Action to be found.
     * @return a Action object if found, otherwise null.
     */
    public Action<?> getAction(int id) {
        return null;
    }

    /**
     * Get a ActionInstance with specified id.
     *
     * @param id of a ActionInstance to be found.
     * @return a ActionInstance object if found, otherwise null.
     */
    public ActionInstance<?> getActionInstance(int id) {
        return null;
    }

    /**
     * Remove an Action from manager.
     *
     * @param action to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeAction(Action<?> action) {
        return false;
    }

    /**
     * Remove an Action from manager.
     *
     * @param id of the Action to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeAction(int id) {
        return false;
    }


    /**
     * Remove an ActionInstance from manager.
     *
     * @param actionInstance to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeActionInstance(ActionInstance<?> actionInstance) {
        return false;
    }

    /**
     * Remove an ActionInstance from manager.
     *
     * @param id to be removed from manager.
     * @return true if successfully removed(found), otherwise false.
     */
    public boolean removeActionInstance(int id) {
        return false;
    }


    public void addActionEventHandler(ActionEventHandler handler) {
        handlers.add(handler);
    }

    public interface ActionEventHandler {
        void onActionInstanceUpdated(int actionInstanceID);
    }
}
