package settlers.gui;

public class GUIThreadManagerImpl implements GUIThreadManager {
    private boolean holding;

    public GUIThreadManagerImpl(){
        holding = false;
    }

    /**
     * Starts a loop which holds the GUIs in a stasis
     */
    public void startHold(){
        if(holding)throw new IllegalStateException("startHold was called while GUIThreadManagerImpl was already holding");

        holding = true;

        while (holding){
            try {
                Thread.sleep(1);
            }catch (InterruptedException e){
                throw new IllegalStateException("Unexpected InterruptedException was thrown. Exception: " + e.getMessage());
            }
        }
    }

    /**
     * Stops the loop which holds the GUIs in a stasis
     */
    public void stopHold(){
        if(!holding)throw new IllegalStateException("stopHold was called while GUIThreadManagerImpl was not holding");

        holding = false;
    }
}