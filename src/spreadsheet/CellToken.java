package spreadsheet;

/**
 * Token class which represents a cell location.
 * 
 * @author Dmitriy Onishchenko
 * @version 26 February 2016
 *
 */
public class CellToken extends Token {    
    
    /**
     * The row location of the Cell.
     */
    private int row;
    
    /**
     * The column location of the Cell.
     */
    private int column;    
  
    
    /**
     * Default Constructor.
     */
    public CellToken() {
        this(0, 0);
    }    
    
    /**
     * Constructor that sets row and column.
     * @param row the row
     * @param column the column
     */
    public CellToken(final int row, final int column) {
        this.row = row;
        this.column = column;
    }
    
    
    /**
     * Returns the row location of the Cell.
     * @return int row location
     */
    public int getRow() {        
        return row;
    }

    /**
     * Returns the column location of the Cell.
     * @return int column location
     */
    public int getColumn() {        
        return column;
    }    
       
    
    /**
     * Sets the row location of the Cell.
     * @param row the row location
     */
    public void setRow(final int row) {
        this.row = row;
    }
    
    
    /**
     * Sets the column location of the Cell.
     * @param column the column location
     */
    public void setColumn(final int column) {
        this.column = column;
    }  
      
    
    /**
     *  Given a CellToken, print it out as it appears on the
     *  spreadsheet (e.g., "A3")
     *  @param cellToken  a CellToken
     *  @return  the cellToken's coordinates
     */
    public static String printCellToken (CellToken cellToken) {
        char ch;
        String returnString = "";
        int col;
        int largest = 26;  // minimum col number with number_of_digits digits
        int number_of_digits = 2;

        col = cellToken.getColumn();

        // compute the biggest power of 26 that is less than or equal to col
        // We don't check for overflow of largest here.
        while (largest <= col) {
            largest = largest * 26;
            number_of_digits++;
        }
        largest = largest / 26;
        number_of_digits--;

        // append the column label, one character at a time
        while (number_of_digits > 1) {
            ch =  (char) (((col / largest) - 1) + 'A');
            returnString += ch;
            col = col % largest;
            largest = largest  / 26;
            number_of_digits--;
        }

        // handle last digit
        ch = (char) (col + 'A');
        returnString += ch;

        // append the row as an integer
        returnString += cellToken.getRow();

        return returnString;
    }   
    
    @Override
    /**    
     * {@inheritDoc}.
     */
    public String toString () {        
        return printCellToken(this);
    }
}
