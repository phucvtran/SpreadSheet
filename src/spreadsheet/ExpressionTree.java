package spreadsheet;


/**
 * An expression tree as represented as a binary tree.
 * @author Dmitriy Onishchenko
 * @author Phuc Tran 
 * @version 29 February 2016
 */
public class ExpressionTree {
	
    /**
     * The root of the tree.
     */
	private ExpressionTreeNode root;
	
	/**
	 * Constructor.
	 */
	public ExpressionTree(){
		root = null;
	}
	
	
	////////////////////// PUBLIC METHODS ////////////////////////////////////
	
	
	/**
     * Make the tree empty.
     */
    public void makeEmpty(){
        root = null;
    }
    
    /**
     * Check to see if the tree is empty
     * @return true if root is null otherwise false
     */
    public boolean isEmpty () {
        return root == null;
    }   
    
    /**
     * Print the tree.
     */
    public void printTree( ) {
        if( root == null )
            System.out.println( "Empty tree" );
        else
            printTree( root );
    }         
    
    /**
     * Builds an expression tree from a stack of ExpressionTreeTokens
     * @param s a stack of expression tokens
     */
    public void buildExpressionTree(Stack s){       
        root = getExpressionTree(s);
        
        if(!s.isEmpty()){
            System.out.println("Error, empty stack");
        }
    }   
    
    /**
     * Method that returns a string representation of the expression tree.
     * (in our case the formula)
     * @param tree the Expression tree
     * @return String the formula
     */
    public String getFormula(ExpressionTree tree) {
        
        if (tree.isEmpty()) {           
            return "0";
        } else 
            return getFormulaRecurse(tree.root);        
    }
    
    /**
     * Evaluates the expression tree and returns the integer value calculated.
     * @param spreadsheet The spreadsheet
     * @return int the value 
     */
    public int evaluate (Spreadsheet spreadsheet) {       
        return evaluate(root, spreadsheet);
    }
    
    
    /**
     * Return a string associated with a token
     * @param expTreeToken an ExpressionTreeToken
     * @return a String associated with expTreeToken
     */
    public String printExpressionTreeToken (Token expTreeToken) {
        String returnString = "";

        if (expTreeToken instanceof OperatorToken) {
            returnString = ((OperatorToken) expTreeToken).getOperatorToken() + " ";
        } else if (expTreeToken instanceof CellToken) {
            returnString = printCellToken((CellToken) expTreeToken) + " ";
        } else if (expTreeToken instanceof LiteralToken) {
            returnString = ((LiteralToken) expTreeToken).getValue() + " ";
        } else {
            // This case should NEVER happen
            System.out.println("Error in printExpressionTreeToken.");
            System.exit(0);
        }
        return returnString;
    }
    
    
    
    /////////////////////////// PRIVATE HELPER METHODS //////////////////////////////////////
    
        
   
    /**
     * Helper method that recursively traverses the tree and prints it out.
     * IN-Order traversal.
     * @param tree the tree to print
     */
    private void printTree(ExpressionTreeNode tree){
        if(tree != null){
            printTree(tree.left);   
            System.out.print(tree.getToken() + " ");
            printTree(tree.right);
            
        }
    }	
    
    /**
     * Helper method for getFormula, traverses tree and builds a formula.
     * @param root the tree root
     * @return String the formula
     */
    private String getFormulaRecurse(ExpressionTreeNode root) {
        
        StringBuilder formula = new StringBuilder();
        
        if (root != null) {
            formula.append(getFormulaRecurse(root.left));  
            formula.append(root.getToken().toString());
            formula.append(getFormulaRecurse(root.right));
        }
        
        return formula.toString();     
    }
	
	/**
	 * Helper method that recursively builds a tree from a stack
	 * of tokens.
	 * @param stack a stack of expression Token
	 * @return
	 */
	private ExpressionTreeNode getExpressionTree(Stack stack) {
		ExpressionTreeNode tree = null;
		Token token;
		
		if(stack.isEmpty()){
			return null;
		}
		
		token =  (Token) stack.topAndPop(); // need to handle stack underflow ( will implement later)
		
		if((token instanceof LiteralToken) || (token instanceof CellToken)){	
			
			// Literal and Cells are leaves in the expresion tree
			tree = new ExpressionTreeNode(token, null, null);
			return tree;
			
		} else if (token instanceof OperatorToken){
			//continue finding tokens that will form the
			// right subtree and left subtree.
			ExpressionTreeNode rightSubtree = getExpressionTree(stack);
			ExpressionTreeNode leftSubtree = getExpressionTree(stack);
			
						
			tree = new ExpressionTreeNode(token, leftSubtree, rightSubtree);
			return tree;
		}		
        return null;		
	}
	
	/**
	 *  Given a CellToken, print it out as it appears on the
	 *  spreadsheet (e.g., "A3")
	 *  @param cellToken  a CellToken
	 *  @return  the cellToken's coordinates
	 */
	private String printCellToken (CellToken cellToken) {
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
	        ch = (char) ((char) ((col / largest) - 1) + 'A');
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


	/**
	 * Recursive helper method that traverses the tree (post order traversal)
	 * and computes the value.
	 * @param root the root of the tree
	 * @param spreadsheet the spreadsheet
	 * @return int the calculated value of tree
	 */
	private int evaluate(ExpressionTreeNode root, Spreadsheet spreadsheet) {

	    if (root == null) {
	        return 0;
	    }
	    
	    if (root.left == null && root.right == null) {

	        if (root.getToken() instanceof LiteralToken) {
	            return ((LiteralToken) root.getToken()).getValue();
	        } else if (root.getToken() instanceof OperatorToken){
	            return 0;
	        } else{    
	            // reference to another cell
	            CellToken token = ((CellToken) root.getToken()); 
	            
	            return spreadsheet.getCellValue(token);             
	        }

	    } else {

	        int total = 0;
	        int left = evaluate(root.left, spreadsheet);
	        int right = evaluate(root.right, spreadsheet);

	        char operator = ((OperatorToken) root.getToken()).getOperatorToken();

	        switch (operator) {

	        case '+':
	            total = left + right;
	            break;
	        case '-':
	            total = left - right;
	            break;
	        case '*':
	            total = left * right;
	            break;
	        case '/':
	            total = left / right;
	            break;  
	        default:
	            //System.out.println("Did you add an operator");
	            break; 
	        }
	        return total;
	    }       
	}

}
