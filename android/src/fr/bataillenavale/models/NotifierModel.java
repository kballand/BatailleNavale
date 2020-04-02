package fr.bataillenavale.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * classe permattant la notification
 */
public class NotifierModel {
    private PropertyChangeSupport pcs;

    /**
     * Constructeur de notifierModel
     */
    public NotifierModel() {
        this.pcs = new PropertyChangeSupport(this);
    }

    /**
     * Ajout listener 
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * supression listener
     */
    public void deletePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    /**
     * nettoyer les listeners
     */
    public void clearPropertyChangeListeners() {
        for (PropertyChangeListener listener : this.pcs.getPropertyChangeListeners()) {
            this.pcs.removePropertyChangeListener(listener);
        }
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        this.pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        this.pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        this.pcs.firePropertyChange(propertyName, oldValue, newValue);
    }
}
