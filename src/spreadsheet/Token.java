package spreadsheet;

/**
 * Abstract Token class which represents a literal, cell reference, and
 * an operator.
 * 
 * @author Dmitriy Onishchenko
 * @version 26 February 2016
 *
 */
public abstract class Token {

    @Override
    /**
     * {@inheritDoc}.
     */
     public abstract String toString();
    
}
