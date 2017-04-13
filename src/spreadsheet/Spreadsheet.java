package spreadsheet;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;



/**
 * Class that resembles the actual spreadsheet.
 * Has a 2 dimensional array of cells.
 * 
 * @author Dmitriy Onishchenko
 * @version 26 February 2016
 *
 */
public class Spreadsheet {
    
    /**
     * Flag for property change listener.
     */
    public static final String CYCLE = "Cycle found";    
    
    /**
     * The value of a cell that is out of bounds.
     */
    private static final int BADCELL = -1;
    
    /**
     * Default size of the spreadsheet rows by columns
     */
    private static final int SIZE = 4;
    
    /**
     * 2-D Array of Cells our spreadsheet
     */
    private Cell spreadsheet[][];    
    
    /**
     * Property change support.
     */
    private PropertyChangeSupport myPcs;
    
    /**
     * Default Constructor
     */
    public Spreadsheet() {
        this(SIZE);        
    }    
    
    /**
     * Constructor
     * @param size the size of the spreadsheet
     */
    public Spreadsheet(int size) { 
        
        myPcs = new PropertyChangeSupport(this);
        
        spreadsheet = new Cell[size][size];  
        
        // populate the spreadsheet with cells
        for (int row = 0; row < spreadsheet.length; row++) {
            for (int col = 0; col < spreadsheet[row].length; col++) {
                
                spreadsheet[row][col] = new Cell(row, col);           
            }            
        }        
    }
    
    
    /////////////////////////// GETTERS ///////////////////////////////////////////////
    
    
    /**
     * Returns the number of rows in the spreadsheet.
     * @return int number of rows.
     */
    public int getNumRows() {        
        return spreadsheet.length;
    }

    /**
     * Returns the number of columns in the spreasheet.
     * @return int number of columns
     */
    public int getNumColumns() {        
        return spreadsheet[0].length;
    } 
    
    /**
     * Returns the spreadsheet 2-D array used for JTable.
     * @return Cell[][] the spreadsheet
     */
    public Cell[][] getSpreadsheet() {
        return spreadsheet;
    }
    
    /**
     * a String representation of the Cells' formula associated with cellToken.
     * @param cellToken the cells location in spreadsheet 
     */
    public String getCellFormula(CellToken cellToken) {
        
        final int row = cellToken.getRow();
        final int col = cellToken.getColumn();
        
        return spreadsheet[row][col].getFormula();     
        
    }    
    
    /**
     * Returns the Cell in the location of the cellToken.
     * @param cellToken the cellToken (location)
     * @return Cell the cell at that location
     */
    public Cell getCell(CellToken cellToken) {        
        final int row = cellToken.getRow();
        final int col = cellToken.getColumn();   
             
        return spreadsheet[row][col];        
    }    
    
    /**
     * Returns the value of the cell in the location of the cellToken.
     * @param cellToken the location of cell
     * @return int value the value of that Cell
     */
    public int getCellValue(CellToken cellToken) {        
        int row = cellToken.getRow();
        int col = cellToken.getColumn();
        
        return spreadsheet[row][col].getValue();        
    }
    
  
 
    /**
     * getFormula
     * 
     * Given a string that represents a formula that is an infix
     * expression, return a stack of Tokens so that the expression,
     * when read from the bottom of the stack to the top of the stack,
     * is a postfix expression.
     * 
     * A formula is defined as a sequence of tokens that represents
     * a legal infix expression.
     * 
     * A token can consist of a numeric literal, a cell reference, or an
     * operator (+, -, *, /).
     * 
     * Multiplication (*) and division (/) have higher precedence than
     * addition (+) and subtraction (-).  Among operations within the same
     * level of precedence, grouping is from left to right.
     * 
     * This algorithm follows the algorithm described in Weiss, pages 105-108.
     */
    public Stack getFormula(String formula) {
        Stack returnStack = new Stack();  // stack of Tokens (representing a postfix expression)
        Boolean error = false;
        char ch = ' ';

        int literalValue = 0;

        CellToken cellToken;
        int column = 0;
        int row = 0;

        int index = 0;  // index into formula
        Stack operatorStack = new Stack();  // stack of operators

        while (index < formula.length() ) {
            // get rid of leading whitespace characters
            while (index < formula.length() ) {
                ch = formula.charAt(index);
                if (!Character.isWhitespace(ch)) {
                    break;
                }
                index++;
            }

            if (index == formula.length() ) {
                error = true;
                break;
            }

            // ASSERT: ch now contains the first character of the next token.
            if (OperatorToken.isOperator(ch)) {
                // We found an operator token
                switch (ch) {
                case OperatorToken.Plus:
                case OperatorToken.Minus:
                case OperatorToken.Mult:
                case OperatorToken.Div:
                case OperatorToken.LeftParen:
                    // push operatorTokens onto the output stack until
                    // we reach an operator on the operator stack that has
                    // lower priority than the current one.
                    OperatorToken stackOperator;
                    while (!operatorStack.isEmpty()) {
                        stackOperator = (OperatorToken) operatorStack.top();
                        if ( (stackOperator.priority() >= OperatorToken.operatorPriority(ch)) &&
                                (stackOperator.getOperatorToken() != OperatorToken.LeftParen) ) {

                            // output the operator to the return stack    
                            operatorStack.pop();
                            returnStack.push(stackOperator);
                        } else {
                            break;
                        }
                    }
                    break;

                default:
                    // This case should NEVER happen
                    System.out.println("Error in getFormula.");
                    System.exit(0);
                    break;
                }
                // push the operator on the operator stack
                operatorStack.push(new OperatorToken(ch));

                index++;

            } else if (ch == ')') {    // maybe define OperatorToken.RightParen ?
                OperatorToken stackOperator;
                stackOperator = (OperatorToken) operatorStack.topAndPop();
                // This code does not handle operatorStack underflow.
                while (stackOperator.getOperatorToken() != OperatorToken.LeftParen) {
                    // pop operators off the stack until a LeftParen appears and
                    // place the operators on the output stack
                    returnStack.push(stackOperator);
                    stackOperator = (OperatorToken) operatorStack.topAndPop();
                }

                index++;
            } else if (Character.isDigit(ch)) {
                // We found a literal token
                literalValue = ch - '0';
                index++;
                while (index < formula.length()) {
                    ch = formula.charAt(index);
                    if (Character.isDigit(ch)) {
                        literalValue = (literalValue * 10) + (ch - '0');
                        index++;
                    } else {
                        break;
                    }
                }
                // place the literal on the output stack
                returnStack.push(new LiteralToken(literalValue));

            } else if (Character.isUpperCase(ch)) {
                // We found a cell reference token
                cellToken = new CellToken();
                index = getCellToken(formula, index, cellToken);
                if (cellToken.getRow() == BADCELL) {                   
                    error = true;
                    break;
                } else {
                    // place the cell reference on the output stack
                    returnStack.push(cellToken);
                }

            } else {
                error = true;
                break;
            }
        }

        // pop all remaining operators off the operator stack
        while (!operatorStack.isEmpty()) {
            returnStack.push(operatorStack.topAndPop());
        }

        if (error) {
            // a parse error; return the empty stack
            returnStack.makeEmpty();
        }

        return returnStack;
    } 
      

    
    /////////////////////////////// OTHER PUBLIC METHODS //////////////////////////////////////
    
    
    
    /**
     * Prints out all of the values of each cell.
     */
    public void printValues() {
        for (int row = 0; row < spreadsheet.length; row++) {
            for (int col = 0; col < spreadsheet[row].length; col++) {

                if (col == spreadsheet[row].length - 1)
                    System.out.println(spreadsheet[row][col].getValue());    
                else 
                    System.out.print(spreadsheet[row][col].getValue() + ", "); 
            }            
        }        
    }
    
    
    /**
     * Prints formulas of entire spreadsheet.
     */
    public void printAllFormulas() {
        
        for (int row = 0; row < spreadsheet.length; row++) {
            for (int col = 0; col < spreadsheet[row].length; col++) {

                if (col == spreadsheet[row].length - 1)
                    System.out.println(spreadsheet[row][col].getFormula());    
                else 
                    System.out.print(spreadsheet[row][col].getFormula() + ", "); 
            }            
        }        
    }

    /**
     * Prints the Cells' formula associated with cellToken.
     * @param cellToken the cells location in spreadsheet 
     */
    public void printCellFormula(CellToken cellToken) {
        
        final int row = cellToken.getRow();
        final int col = cellToken.getColumn();
        
        System.out.println(spreadsheet[row][col].getFormula());     
        
    }    
   
    /**
     * Updates the Cells dependent cells list and adjacent list 
     * located at the cellToken location given an A stack of Tokens. 
     * @param cellToken the cell location
     * @param expTreeTokenStack the stack of tokens
     */
    public void updateCellDependency(CellToken cellToken, Stack expTreeTokenStack) {
        
        Cell currentCell = spreadsheet[cellToken.getRow()][cellToken.getColumn()];        
        LinkedList<Cell> dependents = currentCell.getDependentCells();
        
        // for current cells dependent cells remove it from 
        // their adjacent list.
        for (Cell c: dependents) {
            c.removeAjacentCell(currentCell);          
        }     
        
        // clear all exsisting dependencies 
        currentCell.clearDependencies();
        
        // now update depending on new formula
        while (!expTreeTokenStack.isEmpty()) {
            
            Token curToken = (Token) expTreeTokenStack.topAndPop();
            
            if (curToken instanceof CellToken) { 
                
                Cell dep = getCell((CellToken) curToken);                                            
                currentCell.addDependent(dep);                
                dep.addAdjacent(currentCell);                
            }           
        }      
    }
    
    /**
     * Changes the cell formula and recalculates value for entire
     * spreadsheet. 
     * @param cellToken the cell token
     * @param expTreeTokenStack the expression tree.
     */
    public void changeCellFormulaAndRecalculate(CellToken cellToken, Stack expTreeTokenStack) {
       
        Cell updateCell = getCell(cellToken); // cell to be worked on  
        
        final ExpressionTree expressionTree = new ExpressionTree();
        expressionTree.buildExpressionTree(expTreeTokenStack);       
        
        updateCell.setExpressionTree(expressionTree);        
        updateCell.setFormula(expressionTree.getFormula(expressionTree));
         
        // perform topological sorting on this spreadsheet
        // to figure out which cell to evaluate first
        topologicalSort(); 
        
    }
    
    /**
     * Clears entire spreadsheet.  
     */
    public void clear() {

        for (int row = 0; row < spreadsheet.length; row++) {
            for (int col = 0; col < spreadsheet[row].length; col++) {

                spreadsheet[row][col].reset();        
            }            
        }   
    }
    
    
    /**
     * Adds the PropertyChangeListener thePcl to the list of PropertyChangeListeners
     * managed by objects of this class.
     * @param thePcl the PropertyChangeListener added.
     */
    public void addPropertyChangeListener(final PropertyChangeListener thePcl) {
        myPcs.addPropertyChangeListener(thePcl);
    }
    
    /**
     * Removes the PropertyChangeListener thePcl from the list of PropertyChangeListeners
     * managed by objects of this class.
     * @param thePcl the PropertyChangeListener removed.
     */ 
    public void removePropertyChangeListener(final PropertyChangeListener thePcl) {
        myPcs.removePropertyChangeListener(thePcl);
    }
    
    //////////////////////////////// PRIVATE HELPER METHODS //////////////////////////////
    
    
    /**
     * Performs a topological sort on the spreadsheet cells to determine 
     * what order to evalute the Cells. 
     */
    private void topologicalSort() {
        
        Queue<Cell> queue = new ArrayDeque<Cell>();  
        int counter = 0;
        queue.clear();
        Cell vertex;    
        
        // add cells of indegree zero to queue
        for (int row = 0; row < spreadsheet.length; row++) {
            for (int col = 0; col < spreadsheet[row].length; col++) {
                
                Cell curCell = spreadsheet[row][col];                
                
                if (curCell.getInDegree() == 0) {
                    queue.add(curCell);
                }               
            }            
        }
        
        
        while (!queue.isEmpty()) {            
            vertex = queue.remove();            
            vertex.evaluate(this);
            vertex.resetInDegreeSort();
            counter++;
            
            // if vertex has adjacent cells
            if (vertex.getOutDegree() > 0) {
                
                LinkedList<Cell> adjacents = vertex.getAdjacentCells();               
                
                // decrement the adjacent cells indegree
                // add to queue if their indegree is zero
                for (Cell adj: adjacents) {                    
    
                    adj.setInDegreeSort(adj.getInDegreeSort() - 1);
                    
                    if (adj.getInDegreeSort() == 0) {
                        queue.add(adj);
                    }                    
                }               
            }          
        }         
        
        if (counter != getNumRows() * getNumColumns()) {            
            myPcs.firePropertyChange(CYCLE , getNumRows() * getNumColumns() , counter);
        }
    }
    
    /**
     * getCellToken
     * 
     * Assuming that the next chars in a String (at the given startIndex)
     * is a cell reference, set cellToken's column and row to the
     * cell's column and row.
     * If the cell reference is invalid, the row and column of the return CellToken
     * are both set to BADCELL (which should be a final int that equals -1).
     * Also, return the index of the position in the string after processing
     * the cell reference.
     * (Possible improvement: instead of returning a CellToken with row and
     * column equal to BADCELL, throw an exception that indicates a parsing error.)
     * 
     * A cell reference is defined to be a sequence of CAPITAL letters,
     * followed by a sequence of digits (0-9).  The letters refer to
     * columns as follows: A = 0, B = 1, C = 2, ..., Z = 25, AA = 26,
     * AB = 27, ..., AZ = 51, BA = 52, ..., ZA = 676, ..., ZZ = 701,
     * AAA = 702.  The digits represent the row number.
     *
     * @param inputString  the input string
     * @param startIndex  the index of the first char to process
     * @param cellToken  a cellToken (essentially a return value)
     * @return  index corresponding to the position in the string just after the cell reference
     */
    private int getCellToken (String inputString, int startIndex, CellToken cellToken) {
        char ch;
        int column = 0;
        int row = 0;
        int index = startIndex;

        // handle a bad startIndex
        if ((startIndex < 0) || (startIndex >= inputString.length() )) {
            cellToken.setColumn(BADCELL);
            cellToken.setRow(BADCELL);
            return index;
        }

        // get rid of leading whitespace characters
        while (index < inputString.length() ) {
            ch = inputString.charAt(index);            
            if (!Character.isWhitespace(ch)) {
                break;
            }
            index++;
        }
        if (index == inputString.length()) {
            // reached the end of the string before finding a capital letter
            cellToken.setColumn(BADCELL);
            cellToken.setRow(BADCELL);
            return index;
        }

        // ASSERT: index now points to the first non-whitespace character

        ch = inputString.charAt(index);            
        // process CAPITAL alphabetic characters to calculate the column
        if (!Character.isUpperCase(ch)) {
            cellToken.setColumn(BADCELL);
            cellToken.setRow(BADCELL);
            return index;
        } else {
            column = ch - 'A';
            index++;
        }

        while (index < inputString.length() ) {
            ch = inputString.charAt(index);            
            if (Character.isUpperCase(ch)) {
                column = ((column + 1) * 26) + (ch - 'A');
                index++;
            } else {
                break;
            }
        }
        if (index == inputString.length() ) {
            // reached the end of the string before fully parsing the cell reference
            cellToken.setColumn(BADCELL);
            cellToken.setRow(BADCELL);
            return index;
        }

        // ASSERT: We have processed leading whitespace and the
        // capital letters of the cell reference

        // read numeric characters to calculate the row
        if (Character.isDigit(ch)) {
            row = ch - '0';
            index++;
        } else {
            cellToken.setColumn(BADCELL);
            cellToken.setRow(BADCELL);
            return index;
        }

        while (index < inputString.length() ) {
            ch = inputString.charAt(index);            
            if (Character.isDigit(ch)) {
                row = (row * 10) + (ch - '0');
                index++;
            } else {
                break;
            }
        }

        // successfully parsed a cell reference
        cellToken.setColumn(column);
        cellToken.setRow(row);
        return index;
    }  
}

    
    
  

