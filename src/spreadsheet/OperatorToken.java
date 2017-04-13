package spreadsheet;


/**
 * Token class which represents an operator.
 *  operators: +, -, *, /, ( 
 * 
 * @author Dmitriy Onishchenko
 * @version 26 February 2016
 *
 */
public class OperatorToken extends Token {
    
    /**
     * Operator plus.
     */
    public static final char Plus = '+';
    
    /**
     * Operator minus.
     */
    public static final char Minus = '-';
    
    /**
     * Operator Multiply.
     */
    public static final char Mult = '*';
    
    /**
     * Operator Divide.
     */
    public static final char Div = '/';
    
    /**
     * Operator Left Parenthesis
     */
    public static final char LeftParen = '(';
    
    /**
     * One of the five operators.
     */
    private final char operatorToken;
    
    
    /**
     * Default Constructor;
     */
    public OperatorToken() {
        this(' ');
    }   
    
    /**
     * Constructor that assigns the char value as operator.
     * @param ch the operator
     */
    public OperatorToken(final char ch) {        
        operatorToken = ch;
    }  
    
    
    /**
     * Returns the operator.
     * @return char the operator
     */
    public char getOperatorToken() {        
        return operatorToken;
    }
    
    
    @Override
    /**    
     * {@inheritDoc}.
     */
    public String toString () {        
        return Character.toString(operatorToken);
    }    
    
    
    /**
     * Return true if the char ch is an operator of a formula.
     * Current operators are: +, -, *, /, (.
     * @param ch  a char
     * @return  whether ch is an operator
     */
    public static boolean isOperator (char ch) {
        return ((ch == Plus) ||
                (ch == Minus) ||
                (ch == Mult) ||
                (ch == Div) ||
                (ch == LeftParen) );
    }
    
    /**
     * Given an operator, return its priority.
     *
     * priorities:
     *   +, - : 0
     *   *, / : 1
     *   (    : 2
     *
     * @param ch  a char
     * @return  the priority of the operator
     */
    public static int operatorPriority (char ch) {
        if (!isOperator(ch)) {
            // This case should NEVER happen
            System.out.println("Error in operatorPriority.");
            System.exit(0);
        }
        switch (ch) {
            case Plus:
                return 0;
            case Minus:
                return 0;
            case Mult:
                return 1;
            case Div:
                return 1;
            case LeftParen:
                return 2;

            default:
                // This case should NEVER happen
                System.out.println("Error in operatorPriority.");
                System.exit(0);                
                break;
                
        }
        // this should never be reached
        System.out.println("Error in operatorPriority.");
        return -1;
    }    
    
    /**
     * Return the priority of this OperatorToken.
     *
     * priorities:
     *   +, - : 0
     *   *, / : 1
     *   (    : 2
     *
     * @return  the priority of operatorToken
     */
    public int priority () {
        switch (this.operatorToken) {
            case Plus:
                return 0;
            case Minus:
                return 0;
            case Mult:
                return 1;
            case Div:
                return 1;
            case LeftParen:
                return 2;

            default:
                // This case should NEVER happen
                System.out.println("Error in priority.");
                System.exit(0);
                break;
        }
        
        // this should never be reached
        System.out.println("Error in priority.");
        return -1;
    }
}




