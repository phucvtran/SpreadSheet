package gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import spreadsheet.Cell;
import spreadsheet.CellToken;
import spreadsheet.Spreadsheet;
import spreadsheet.Stack;

/**
 * A custom TableModel designed for the spreadsheet application
 * for correctly displaying spreadsheet values.
 * 
 * @author Dmitriy Onishchenko
 * @version 1 March 2016
 */
public class MyTableModel extends AbstractTableModel implements TableModelListener, PropertyChangeListener{

    /**
     * Generated serial ID.
     */
    private static final long serialVersionUID = -1979825020261774357L;

    /**
     * The information to display. (Cells in our case and their values) 
     */    
    private final Object[][] data;
    
    /**
     * Column header.
     */
    private String[] columns;
    
    /**
     * The spreadsheet.
     */
    private Spreadsheet spreadsheet;
    
    /**
     * Keep track of previous formula of cell in case cycle and need
     * to set to previous formula.
     */
    private String previousFormula;
    
    /**
     * Boolean flag if cycle found in graph.
     */
    private boolean cycleFound;  
    
    /**
     * The row of cell being changed.
     */
    private int row;
    
    /**
     * The column of cell being changed.
     */
    private int col;
    
    /**
     * Constructor.
     * @param spreadsheet the spreadsheet for this model.
     */
    public MyTableModel(Spreadsheet spreadsheet) {
        
        previousFormula = "";
        this.spreadsheet = spreadsheet;
        data = spreadsheet.getSpreadsheet();
        columns = new String[spreadsheet.getNumColumns()];
        setUpColumnNames();       
        spreadsheet.addPropertyChangeListener(this);
        addTableModelListener(this);       
    }
      
//////////////////////////////PRIVATE HELPER METHODS /////////////////////////////////////////////
    
    /**
     * Sets up the column names of the table model.
     */
    private void setUpColumnNames() { 

        for (int i = 0; i < spreadsheet.getNumColumns(); i++) {      

            if(i >= 26) {  // If i > 26, more than 1 char will be needed.             
                char first = (char) ((i / 26) + 64);
                char second = (char) ((i % 26) + 65);
                //convert back to int = first * 26 + second
                columns[i] = Character.toString(first) + Character.toString(second);
            }            
            else { // Only 1 char is needed
                char letter = (char) (i + 65);            
                columns[i] = Character.toString(letter);  
            }
        }        
    }

    /**
     * Helper method that updates cell that is being updated.
     * @param curCellToken the cell location.
     * @param currentCell the current cell
     * @param inputFormula the new formula
     */
    private void processCell(CellToken curCellToken, Cell currentCell, String inputFormula) {

        Stack expTreeTokenStack, expTreeTokenStack2; 
         
        if (isInteger(inputFormula)) {               
            expTreeTokenStack = spreadsheet.getFormula (inputFormula.toUpperCase());  
            expTreeTokenStack2 = spreadsheet.getFormula (inputFormula.toUpperCase());
            spreadsheet.updateCellDependency(curCellToken, expTreeTokenStack2);
            spreadsheet.changeCellFormulaAndRecalculate(curCellToken, expTreeTokenStack);         
            
        } else if (cycleFound) {          
            expTreeTokenStack = spreadsheet.getFormula (inputFormula.toUpperCase());  
            expTreeTokenStack2 = spreadsheet.getFormula (inputFormula.toUpperCase());            
            spreadsheet.updateCellDependency(curCellToken, expTreeTokenStack2);
            spreadsheet.changeCellFormulaAndRecalculate(curCellToken, expTreeTokenStack);

            if (inputFormula.length() == 0) {               
                currentCell.setFormula(inputFormula);
                currentCell.setExpressionTree(null);               
            }             

        } else if (inputFormula.equals("") || inputFormula.charAt(0) != '=') {
            Stack empty = new Stack();
            empty.makeEmpty();            
            spreadsheet.updateCellDependency(curCellToken, empty);
            spreadsheet.changeCellFormulaAndRecalculate(curCellToken, empty);            
            currentCell.setExpressionTree(null);           
            currentCell.setFormula(inputFormula);

        } else if (inputFormula.charAt(0) == '=') {           
            inputFormula = inputFormula.substring(1);
            expTreeTokenStack = spreadsheet.getFormula (inputFormula.toUpperCase());  
            expTreeTokenStack2 = spreadsheet.getFormula (inputFormula.toUpperCase());
            spreadsheet.updateCellDependency(curCellToken, expTreeTokenStack2);
            spreadsheet.changeCellFormulaAndRecalculate(curCellToken, expTreeTokenStack);
        }        

        cycleFound = false;
        // let the table know that we edited the table
        fireTableDataChanged(); 
    }   
    
/////////////////////////////// PUBLIC METHODS /////////////////////////////////////////////////////
    
    @Override
    public int getColumnCount() {
        return columns.length + 1;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        
        if (col == 0) 
            return "";
        else
            return columns[col - 1].toString();
    }
    
    @Override
    public void tableChanged(TableModelEvent e) {
        // table changed      
    }
    
    @Override
    public Object getValueAt(int row, int col) {

        if (col == 0)  
            return new Integer(row);
        else           
            return ((Cell) data[row][col -1]).toString();
    }

    /**
     * Column zero is not editable.
     */
    public boolean isCellEditable(int row, int col) {
        if (col == 0) 
            return false;
        else 
            return true;
    }  
   
    /**
     * Sets the the new value of cell at the location
     * row, column.
     */
    public void setValueAt(Object value, int row, int col) { 

        String inputFormula = ((String) value);           
        final CellToken curCellToken = new CellToken(row, col - 1); 
        final Cell currentCell = spreadsheet.getCell(curCellToken); 
         
        // keep track of old formula just in case there is a cycle
        previousFormula = currentCell.getFormula();         
        this.row = curCellToken.getRow();
        this.col = curCellToken.getColumn();          
            
        if ((!currentCell.getFormula().equals(value) && 
             !value.equals(Integer.toString(currentCell.getValue())))) {
            
            processCell(curCellToken, currentCell, inputFormula);             
            
        } else if (value.equals("0") && !currentCell.hasExpTree()) {          
            processCell(curCellToken, currentCell, inputFormula);    
        } 
    }
 
    /**
     * {@inheritDoc}. 
     * Method overridden for property change listener.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {        
        
        if (evt.getPropertyName().equals(Spreadsheet.CYCLE)) {
       
           JOptionPane.showMessageDialog(null, "Cycle Found! Cannot reference same Cell", 
                   "ERROR", JOptionPane.ERROR_MESSAGE);
       
            cycleFound = true;
            setValueAt(previousFormula, row, col + 1);
        }
    }
    
    /**
     * Method that takes a string and returns whether it is an integer or not.
     * @param str the string
     * @return true if positive or negative integer.
     */
    public static boolean isInteger(String str) {
        
        int index = 0;
        
        if (str.length() == 0) //empty string
            return false;
                
        if (str.charAt(0) == '-') {
            index = 1;            
            str = str.substring(1);
            if (str.length() == 0) {
                return false; //string only has '-'
            }
        } 
        
        for (; index < str.length(); index++) { 
            
            char c = str.charAt(index);            
            if (c < '0' || c > '9') 
                return false; // non-integer found               
        }
        return true;         
    }
}
