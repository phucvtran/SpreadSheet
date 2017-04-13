package spreadsheet;

/**
 * Basic node stored in ExpressionTree
 * @author Phuc Tran
 */
public class ExpressionTreeNode {
	
    /**
     * The element in node.
     */
	private Token token;
	
	/**
	 * Left child.
	 */
	ExpressionTreeNode left;
	
	/**
	 * Right child.
	 */
	ExpressionTreeNode right;

    /**
     * Constructor with left and right child equal to null. 
     * @param token the token
     */
	public ExpressionTreeNode (Token token){
		this(token, null, null);
	}
	
	/**
	 * Constructor with a left and right child. 
	 * @param token the token
	 * @param lt the left child
	 * @param rt the right child
	 */
	public ExpressionTreeNode(Token token, ExpressionTreeNode lt, ExpressionTreeNode rt){
		this.token = token;
		left = lt;
		right = rt;
	}
	
	
	/**
	 * Get the element (token)
	 * @return token the token
	 */
	public Token getToken(){
		return token;
	}

}
