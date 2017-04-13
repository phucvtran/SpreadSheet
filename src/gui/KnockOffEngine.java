package gui;


/**
 * Class containing the main method which runs the SpreadSheet GUI. 
 * 
 * @author Dmitriy Onishchenko 
 * @version 1 March 2016
 */
public final class KnockOffEngine {
    
    /**
     * Make sure no one can instantiate.
     */
    private KnockOffEngine() {
        //Can't instantiate me!
    }

    /**
     * Main - starts the Excel application.
     * @param args Not used.
     */
    public static void main(String[] args) {
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                final ExcelKnockOffGUI excel = new ExcelKnockOffGUI();
                excel.setUpComponents();
            }
        });
    }
}
