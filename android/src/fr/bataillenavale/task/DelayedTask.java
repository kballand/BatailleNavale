package fr.bataillenavale.task;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Classe pour les taches correspondant au timer
 */
public abstract class DelayedTask extends TimerTask {
	/**
     * long correspond au delai a gerer
     */
    private long delay;
    /**
     * Timer est le compteur gere
     */
    private Timer timer;

    /**
     * constructeur de DelayedTask
     * @param delay long
     */
    public DelayedTask(long delay) {
        if (delay < 0)
            throw new IllegalArgumentException("delay must be greater or equals to  0 !");
        this.delay = delay;
        this.timer = new Timer();
    }

    /**
     * lance le compteur
     */
    public void start() {
        this.timer.schedule(this, this.delay);
    }

    /**
     * arrete le compteur
     */
    public void stop() {
        this.timer.cancel();
    }
}
