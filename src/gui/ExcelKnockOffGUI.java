package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableCellRenderer;

import spreadsheet.CellToken;
import spreadsheet.Spreadsheet;


/**
 * The SpreadSheet GUI.
 * 
 * @author Dmitriy Onishchenko
 * @author Calvin Gunther (added color-coded cells)
 * @version 1 March 2016
 */
public class ExcelKnockOffGUI extends JPanel {
    
    /**
     * Generated ID.
     */
    private static final long serialVersionUID = 3401498614862807737L;

    /**
     * The spreadsheet size.
     */
    private static final int SPEADSHEET_SIZE = 200;   

    /**
     * JTable to display spreadsheet values.
     */
    private JTable table;
    
    /**
     * The spreadsheet.
     */
    private Spreadsheet spreadsheet;
    
    /**
     * Scroll bar for table.
     */
    private JScrollPane scroll;    
    
    /**
     * Custom formula bar.
     */
    private FormulaBar formulaBar;
    
    
    /**
     * Constructor 
     */
    public ExcelKnockOffGUI() {
        super(new BorderLayout());        
        
        spreadsheet = new Spreadsheet(SPEADSHEET_SIZE);
        table = new JTable(new MyTableModel(spreadsheet));           
        scroll = new JScrollPane(table);       
        formulaBar = new FormulaBar();         
        add(formulaBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }
    
    
    
    //////////////////////////////// PRIVATE HELPER METHODS /////////////////////////////////////////
    
    private String getHelpDialog() {
    	String string = "Spreadsheet size: 200x200 \n " +
    				"Columns are in letters (A, B, C) \n " +
    				"Rows are in numbers (1, 2, 3) \n " +
    				"Columns range from A-GR, rows range from 0-199 \n" +
    				"CellTokens (locations): letter(s) followed immediately by " +
    				"numbers. Examples include: 'A3', 'AE34', and 'Z124' \n \n" +
    				"'Exit' will exit the application. \n 'New' will start a new " +
    				"spreadsheet, overwriting the current one. \n \n" +
    				"For typing formulas begin with '=' then formula afterwards. \n" +
    				"Without '=', whatever is typed is left as a String " +
    				"(useful for notes) \nFormulas = Blue text, Strings = Red text.\n" +
    				"Letters are not case-sensitive for formulas and are limited to " +
    				"Literals, CellTokens, and operators (between other non-operator tokens)" +
    				"\nValid Operations are: + - * / \n\nCells cannot have cycles which " +
    				"means that cell A1 cannot reference itself or reference cells that depend" +
    				" on cell A1.";    					
    	return string;
    }

    /**
     * Helper method that builds and returns a menu bar.
     * @param window the window to add to.
     * @return JMenuBar the menu bar.
     */
    private JMenuBar buildMenuBar(JFrame window) {

        final JMenuBar menuBar = new JMenuBar(); 
        menuBar.setPreferredSize(new Dimension(this.getWidth(), 30));

        JMenu file = new JMenu("File");       
        JMenu help = new JMenu("Help...");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem newSheet = new JMenuItem("New");
        JMenuItem manual = new JMenuItem("User Manual (Summary)");

        // Set mnemonic and accelerators
        file.setMnemonic(KeyEvent.VK_F);
        help.setMnemonic(KeyEvent.VK_H);
        exit.setMnemonic(KeyEvent.VK_E);
        newSheet.setMnemonic(KeyEvent.VK_N);        
        newSheet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));


        // add action listeners.
        newSheet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                spreadsheet.clear();
                formulaBar.resetMessages();
                table.repaint();
            }
        });       

        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent Event) {
                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));

            }
        });
        
        manual.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, getHelpDialog());
            }
        });
        
        file.add(exit);
        file.addSeparator();
        file.add(newSheet);
        
        help.add(manual);

        menuBar.add(file);
        menuBar.add(help);
        
        return menuBar;

    }
    
    /**
     * Sets up the JTable.
     */
    private void setUpTable() {
        
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setCellSelectionEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);       
        
        // add listeners listeners
        table.addMouseListener(new Selected());       
        table.addKeyListener(new KeyAdapter() {
      
            @Override
            public void keyReleased(KeyEvent e) { 
                updateFormulaBar(e);              
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                updateFormulaBar(e);               
            }
            
            // the action to take when key is pressed and released 
            // helper method
            private void updateFormulaBar(KeyEvent e) {               
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_RIGHT  ||                        
                        e.getKeyCode() == KeyEvent.VK_LEFT  || e.getKeyCode() == KeyEvent.VK_UP ||                       
                        e.getKeyCode() == KeyEvent.VK_DOWN ) {                        
                        
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();

                    if (col > 0) { // If not row header...
                        // selected cell at this location
                        CellToken cellToken = new CellToken(row, col-1);            
                        String formula = spreadsheet.getCell(cellToken).getFormula();                       
                        
                        if (spreadsheet.getCell(cellToken).hasExpTree()) {
                            formula = "=" + formula;
                        }                        
                        // update formula bar
                        formulaBar.updateFormulaBar(formula, cellToken.toString());
                    }                        
                }              
            }
        });    
               
        // add custom renderer
        for (int i = 0; i < table.getColumnCount(); i++) {
            
            if (i == 0) {
                table.getColumnModel().getColumn(i).setPreferredWidth(40); 
            }           
            table.getColumnModel().getColumn(i).setCellRenderer(new CustomRenderer());
        }        
    }
    
    
    /**
     * Method that creates the JFrame and sets its content.
     */
    private void createJFrame() {
        
        final JFrame window = new JFrame("Knock-Off Brand Excel Application");
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        window.setContentPane(this);
        window.setJMenuBar(buildMenuBar(window)); // helper method        
       
        //Set dimensions
        window.setMinimumSize(new Dimension(600,380));
        window.setPreferredSize(new Dimension(960,715));
        
        window.setVisible(true);
        window.pack();        
    }
    
    //////////////////////////////// PUBLIC METHODS ////////////////////////////   
    
    /**
     * Sets up this gui and JFrame.
     */
    public void setUpComponents() {        
        setUpTable();       
        createJFrame();     
    }
    
    //////////////////////////////////// INNER CLASSES /////////////////////////////////
    
    /**
     * 
     * A mouse listener class for the table.
     * @author Dmitriy Onishcheko
     */
    private class Selected extends MouseInputAdapter {  
        
        @Override
        public void mouseReleased(MouseEvent event) {
            int row = table.rowAtPoint(event.getPoint());
            int col = table.columnAtPoint(event.getPoint());     
            
            // selected cell at this location
            CellToken cellToken = new CellToken(row, col-1);
            
            if (col > 0) { // If not row header...
                
                String formula = spreadsheet.getCell(cellToken).getFormula();           
                
                // Checks to see if the Cell has a formula.
                if (spreadsheet.getCell(cellToken).hasExpTree()) {
                    formula = "=" + formula;
                }
                // update formula bar
                formulaBar.updateFormulaBar(formula, cellToken.toString());            
            }           
        }
    }
    
    
    /**
     * Custom renderer for displaying content in the table.
     * Changes font and background color.
     * @author Dmitriy Onishchenko
     * @author Calvin Gunther
     *
     */
    private class CustomRenderer extends DefaultTableCellRenderer {

        /**
         * Generated ID.
         */
        private static final long serialVersionUID = 1L;

        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                                    boolean hasFocus, int row, int column)
        {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 0 ) {    // Special case for row header           
                setHorizontalAlignment((int)JLabel.CENTER_ALIGNMENT);               
                cell.setBackground(new Color(238, 238, 238));
                
            } else { // All other cells - Font, Alignment, Color
                cell.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));   
                setHorizontalAlignment(SwingConstants.RIGHT);   
                
                // Checks for formulas: Strings = red, Valid Formulas = blue.
                CellToken location = new CellToken(row, column - 1);
                if (!spreadsheet.getCell(location).hasExpTree()) {
                	cell.setForeground(Color.RED); // Isn't a formula.
                } else {
                	cell.setForeground(Color.BLUE); // Is a formula.
                }         
            }
            
            table.repaint(); //Repaints JTable
            return cell;
        }
    }   
}
