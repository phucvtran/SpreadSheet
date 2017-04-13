package spreadsheet;

// StackAr class
//
// CONSTRUCTION: with or without a capacity; default is 10
//
// ******************PUBLIC OPERATIONS*********************
// void push( x )         --> Insert x
// void pop( )            --> Remove most recently inserted item
// Object top( )          --> Return most recently inserted item
// Object topAndPop( )    --> Return and remove most recently inserted item
// boolean isEmpty( )     --> Return true if empty; else false
// boolean isFull( )      --> Return true if full; else false
// void makeEmpty( )      --> Remove all items
// ******************ERRORS********************************
// Overflow and Underflow thrown as needed

/**
 * Array-based implementation of the stack.
 * @author Mark Allen Weiss
 */
public class Stack implements Cloneable{    
    
    private Object [ ] theArray;
    private int        topOfStack;

    static final int DEFAULT_CAPACITY = 10;
       
    /**
     * Construct the stack.
     */
    public Stack()
    {
        this( DEFAULT_CAPACITY );
    }

    /**
     * Construct the stack.
     * @param capacity the capacity.
     */
    public Stack( int capacity )
    {
        theArray = new Object[ capacity ];
        topOfStack = -1;
    }

    /**
     * Test if the stack is logically empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty( )
    {
        return topOfStack == -1;
    }

    /**
     * Test if the stack is logically full.
     * @return true if full, false otherwise.
     */
    public boolean isFull( )
    {
        return topOfStack == theArray.length - 1;
    }

    /**
     * Make the stack logically empty.
     */
    public void makeEmpty( )
    {
        topOfStack = -1;
    }

    /**
     * Get the most recently inserted item in the stack.
     * Does not alter the stack.
     * @return the most recently inserted item in the stack, or null, if empty.
     */
    public Object top( )
    {
        if( isEmpty( ) )
            return null;
        return theArray[ topOfStack ];
    }

    /**
     * Remove the most recently inserted item from the stack.
     * @exception Underflow if stack is already empty.
     */
    public void pop( ) //throws Underflow
    {
//        if( isEmpty( ) )
//            throw new Underflow( );
        theArray[ topOfStack-- ] = null;
    }

    /**
     * Insert a new item into the stack, if not already full.
     * @param x the item to insert. 
     */
    public void push( Object x ) 
    {
        if( isFull( ) ) { // double size if stack is full
            Object[] newArray = new Object[topOfStack * 2];
            
            for (int i = 0; i <= topOfStack; i++) {
                newArray[i] = theArray[i];
            }
            theArray = newArray;
        }
            
        theArray[ ++topOfStack ] = x;
    }

    /**
     * Return and remove most recently inserted item from the stack.
     * @return most recently inserted item, or null, if stack is empty.
     */
    public Object topAndPop( )
    {
        if( isEmpty( ) )
            return null;
        Object topItem = top( );
        theArray[ topOfStack-- ] = null;
        return topItem;
    }    
   
}