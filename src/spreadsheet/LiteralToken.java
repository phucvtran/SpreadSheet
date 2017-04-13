package spreadsheet;


/**
 * Token class which represents a literal value.
 * 
 * @author Dmitriy Onishchenko
 * @version 26 February 2016
 *
 */
public class LiteralToken extends Token {
    
    /**
     * The integer value.
     */
    private int value;
    
    /**
     * Default Constructor;
     */
    public LiteralToken() {
        this(0);        
    }
    
    /**
     * Constructor that sets the value.
     * @param value an integer value.
     */
    public LiteralToken(final int value) {
        this.value = value;
    }
    
    
    /**
     * Returns the integer value of the token.
     * @return int the integer value
     */
    public int getValue() {
        return value;
    }
    
    @Override
    /**    
     * {@inheritDoc}.
     */
    public String toString () {
        return Integer.toString(value);
    }
}
