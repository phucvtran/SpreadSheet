package spreadsheet;

import java.util.LinkedList;

/**
 * A cell in the Spreadsheet ADT.
 *
 * @author Dmitriy Onishchenko
 * @author Calvin (Method stubs)
 *  
 * @version 16.02.25
 */
public class Cell {

    /**
     * The Cells in-degree number (number of dependency cells)
     */
    private int myInDegree;
    
    /**
     * The Cells in-degree number (number of dependency cells)
     * For topological sorting purpose need a copy.
     */
    private int myInDegreeSort;
    
    /**
     * The Cells out-degree number (number of adjacent cells)
     */
    private int myOutDegree;
    
    /**
     * The integer value of the cell.
     */
    private int myValue;
    
    /**
     * The row of the cell.
     */
    private int myRow;
    
    /**
     * The column of the cell.
     */
    private int myColumn;

    /**
     * The formula of the Cell.
     */
    private String myFormula;   


    /**
     * The formula of the Cell in an expression tree.
     */
    private ExpressionTree myExpressionTree;

    /**
     * List of adjacent cells.
     */
    private LinkedList<Cell> myAdjacents;

    /**
     * List of dependent cells.
     */
    private LinkedList<Cell> myDependencies; 
        

    /**
     * Creates a new cell at the specified location of Array.
     *
     * @param theX the x-coordinate of the cell.
     * @param theY the y-coordinate of the cell.
     */
    public Cell (final int theRow, final int theColumn) {
        myValue = 0;
        myFormula = ""; 
        myInDegree = 0;
        myOutDegree = 0;        
        myRow = theRow;
        myColumn = theColumn;
        myAdjacents = new LinkedList<Cell>();
        myDependencies = new LinkedList<Cell>();        
    }
    
    
    ///////////////////////////////// GETTERS //////////////////////////////////////////////////
    
    /**
     * Gets the value of the cell.
     * @return Value of the cell.
     */
    public int getValue() {
        return myValue;
    }
    
    /**
     * Gets the formula of the cell.
     * @return Formula of the cell.
     */
    public String getFormula() {
        return myFormula;
    }    
    
    /**
     * Returns the the number of dependent cells (in-degree).
     * @return int in-Degree
     */
    public int getInDegree() {
        return myInDegree;
    }    

    /**
     * Returns the the number of dependent cells (in-degree).
     * Used when topological sorting
     * @return int in-Degree
     */
    public int getInDegreeSort() {
        return myInDegreeSort;
    } 
    
    /**
     * Returns the number of adjacent cells.
     * @return int the out-Degree
     */
    public int getOutDegree() {
        return myOutDegree;
    }    
    
    /**
     * Returns the adjacent cells list.
     * @return LinkedList the adjacent cells
     */
    public LinkedList<Cell> getAdjacentCells() {        
        return myAdjacents;
    }
    
    /**
     * Returns the dependent cells list.
     * @return LinkedList the dependent cells
     */
    public LinkedList<Cell> getDependentCells() {
        return myDependencies;
    }
    
    
    
    ///////////////////////////////// SETTERS //////////////////////////////////////////
    
    /**
     * Sets the formula of the cell. 
     * @param theFormula The new formula
     */
    public void setFormula(final String theFormula) {
        myFormula = theFormula;        
    }    
    
    /**
     * Sets the expression tree of the Cell.
     * @param theTree the expression tree (formula)
     */
    public void setExpressionTree(final ExpressionTree theTree) {        
        myExpressionTree = theTree;           
    }   
    
    /**
     * Sets the in-Degree of this Cell to a value.
     * Used when topological sorting
     */
    public void setInDegreeSort(final int theInDegree) {        
        myInDegreeSort = theInDegree;
    }
    
    
    /////////////////////////////////// OTHER PUBLIC METHODS ////////////////////////////////
       
    /**
     * Resets cell to default.
     */
    public void reset() {
        myValue = 0;
        myFormula = ""; 
        myInDegree = 0;
        myInDegreeSort = 0;
        myOutDegree = 0;       
        myAdjacents.clear();
        myDependencies.clear();
        myExpressionTree = null;        
    }

    /**
     * Adds adjacent cell to Adjacency list.
     * and increments out degree.
     * @param theDependent the adjacent cell
     */
    public void addAdjacent(final Cell theAdjacent) {
        
        if (myOutDegree == 0 || !myAdjacents.contains(theAdjacent)) {
            myAdjacents.add(theAdjacent);
            myOutDegree++;
        }
    }

    /**
     * Adds dependent cell to dependency list.
     * and increments in-degree.
     * @param theDependent the dependent cell
     */
    public void addDependent(final Cell theDependent) {         
        myDependencies.add(theDependent);
        myInDegree++;
        myInDegreeSort++;
    }  

    /**
     * Returns whether the cell has has an expression tree.
     * @return boolean true if expression tree is not null
     */
    public boolean hasExpTree() {
        return myExpressionTree != null;
    }
    
    /**
     * Clears all dependent cells.
     * Resets in-degree to zero.
     */
    public void clearDependencies() {
        myDependencies.clear();       
        myInDegree = 0;
        myInDegreeSort = 0;
    }   
    
    /**
     * Removes adjacent cell from its adjacency list.
     * @param theCell the cell to remove
     */
    public void removeAjacentCell(Cell theCell) {        
        myAdjacents.remove(theCell);
        myOutDegree--;       
    }
    
    /**
     * Resets the in-degree back to the correct in-degree 
     * after sorting.
     */
    public void resetInDegreeSort() {        
        myInDegreeSort = myInDegree;        
    }
  
    /**
     * Evaluates the expression tree to compute the Cells' value.
     * @param theSpreadsheet the current SpreadSheet
     */
    public void evaluate (Spreadsheet theSpreadsheet) {
        
        // evaluate tree if contains anything 
        // otherwise myValue is 0
        if (myExpressionTree != null) {
            myValue = myExpressionTree.evaluate(theSpreadsheet);       
        }       
    }   

    
    @Override
    public String toString() {
        
        if (myExpressionTree == null) {           
            return myFormula;
        } else {             
            return Integer.toString(myValue);
        }
    }
    
    
    @Override
    /**
     * {@inheritDoc}
     * 
     * Check if two cells are the same.
     * True if they are both in the same row
     * and column of the spreadsheet. 
     * This method is used when adding adjacent cells to list, 
     * to prevent duplicates.
     */
    public boolean equals(Object theOther) {          
        
        if (this == theOther) {
            return true;
        }
        
        if (!(theOther instanceof Cell)) {
            return false;
        }
        
        Cell other = (Cell) theOther;
        
        return Integer.compare(this.myRow, other.myRow) == 0 &&
                Integer.compare(this.myColumn, other.myColumn) == 0;      
        
    }
}


