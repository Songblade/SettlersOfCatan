package settlers.gui;

public interface GUIThreadManager{

    /**
     * Starts a loop which holds the GUIs in a stasis
     */
    public void startHold();

    /**
     * Stops the loop which holds the GUIs in a stasis
     */
    public void stopHold();
}