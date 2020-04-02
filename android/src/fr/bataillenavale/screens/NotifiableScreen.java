package fr.bataillenavale.screens;

import com.badlogic.gdx.Screen;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.bataillenavale.models.NotifierModel;

/**
 * Classe de notification d'ecran
 */
public abstract class NotifiableScreen implements Screen {
    private HashMap<NotifierModel, Set<PropertyChangeListener>> notifiersListeners;

    /**
     * constructeur de NotifiableScreen
     */
    public NotifiableScreen() {
        this.notifiersListeners = new HashMap<>();
    }

    /**
     * Ajoute un Listener
     */
    protected void addPropertyChangeListener(NotifierModel model, PropertyChangeListener listener) {
        boolean added = true;
        if (this.notifiersListeners.containsKey(model)) {
            Set<PropertyChangeListener> notifierListeners = this.notifiersListeners.get(model);
            if (!notifierListeners.contains(listener)) {
                this.notifiersListeners.get(model).add(listener);
            } else {
                added = false;
            }
        } else {
            Set<PropertyChangeListener> notifierListeners = new HashSet<>();
            notifierListeners.add(listener);
            this.notifiersListeners.put(model, notifierListeners);
        }
        if (added) {
            model.addPropertyChangeListener(listener);
        }
    }

    @Override
    public void dispose() {
        for (NotifierModel model : this.notifiersListeners.keySet()) {
            for (PropertyChangeListener listener : this.notifiersListeners.get(model)) {
                model.deletePropertyChangeListener(listener);
            }
        }
    }
}
