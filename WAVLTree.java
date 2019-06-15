/**
 * WAVLTree
 *
 * An implementation of a WAVL Tree with
 * distinct integer keys and info
 *
 */


public class WAVLTree {
	private IWAVLNode root;
	private IWAVLNode virtual=new WAVLNode(0,null,true); //virtual node with rank -1.
	private IWAVLNode search_place; //in use for insert and deletion (saved through search)
	private IWAVLNode min; 
	private IWAVLNode max;
	
	
	
  /**
   * public boolean empty()
   *
   * @return true if and only if the tree is empty
   *
   */
  public boolean empty() {
	  if (root==null) {
		  return true;
	  }
	  return false; // to be replaced by student code
  }

 /**
   * public String search(int k)
   *
   * @param k (key)
   * 
   * @return the info of an item with key k if it exists in the tree
   * otherwise, returns null
   * 
   * In addition, search is used for finding the place of insertion
   * or the node to be deleted, which marked as search_place - a private field of 
   * WAVLVTree T.
   */
  public String search(int k)
  {
	  if (this.empty()) {
		  return null;
	  }
	  IWAVLNode search = this.getRoot();
	  while (search!=virtual) {
		  //System.out.println("searching");
		  search_place=search;
		  if (k==search.getKey()) {
			  return search.getValue();
		  }
		  else if (k>search.getKey()) {
			  search=search.getRightin();
		  }
		  else {
			  search=search.getLeftin();
		  }
	  }
	  return null;
}
 
 
  /** 
   * size_update as it's name, is used for updating every node that the node which node.key==k
   * is a descendant of it.
   * size_update travels throughout the tree in the direction of the node which node.key==k.
   *  
   * 
   * 
   * @param k the key of the node which node.key==k
   * @param A - the root of the tree that contains the node which node.key==k
   * @param sizeUpdate - +1 in case of insertion, -1 in case of deletion
   */
  public void size_update (int k, IWAVLNode A, int sizeUpdate){
	  while (A.getKey()!=k && A!=virtual) {
		  A.setSubtreeSize(A.getSubtreeSize()+sizeUpdate);
		  if (k>A.getKey()) {
			  A=A.getRightin();
		  }
		  else {
			  A=A.getLeftin();
		  }
	  }
  }
  
  
  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the WAVL tree.
   * the tree must remain valid (keep its invariants).
   * @param k - the key of the node that is about to be inserted
   * @param i - the value of the node that is about to be inserted
   * 
   * @return the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   *  -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) {
	  IWAVLNode new_node = new WAVLNode(k,i,false);
	  new_node.setLeft(virtual);
	  new_node.setRight(virtual);
	  new_node.setRank(0);
	  new_node.setSubtreeSize(1);
	  
	  if (empty()) { //inserting the root
		  this.min=new_node;
		  this.max=new_node;
		  root=new_node;
		  return 0;
	  }
	  
	  String answer_search = this.search(k); //checking if the key exists in tree
	  if (answer_search!=null) {
		  return -1;
	  }
	  
	  //updating max and min: //works even if the tree is just a root.
	  if (this.min.getKey()>k) {
		  this.min=new_node;
	  }
	  
	  if (this.max.getKey()<k) {
		  this.max=new_node;
	  }
	  
	  new_node.setFather(search_place); //setting the place where the new node is about to be inserted
	  size_update(k, this.root, 1); //updating the subTreeSizes of every node that new_node is
	  //a descendant of it
	 
	  if (search_place.getRank()==1) { //in case that parent is *not* a leaf
		  if (k>search_place.getKey()) {
			  search_place.setRight(new_node);
			  return 0;
		  }
		  else {
			  search_place.setLeft(new_node);
			  return 0;
		  }
	  }
	  
	  else { //in case that parent is a leaf		  
		  if (search_place.getKey()<k) {
			  search_place.setRight(new_node);  
		  }
		  else {
			  search_place.setLeft(new_node);
		  }
  
		  int count_rebalance=0; 
		  IWAVLNode parent=search_place;
		  IWAVLNode son=new_node;
		  boolean isRight;
		  
		  while (true) { //rebalancing!!!
			  if (parent.getRightin()==son) { //checking the side of son
				  isRight=true;
			  }
			  else {
				  isRight=false;
			  }
			  
			  	//checking the rank difference between father and other son
			  	if (!isRight) {
			  		if (parent.getRank()-son.getRank()>0) { //checking if we're done
			  			return count_rebalance;
			  		}
			  		
			  		int rank_difference=parent.getRank()-parent.getRightin().getRank();
			  		if (rank_difference==1) { 
			  			parent.promote();
			  			count_rebalance++;
			  			if (parent==this.root) {
			  				return count_rebalance;
			  			}
			  			son=parent;
			  			parent=parent.getFather();
			  			continue;
			  		}
			  		//difference with other son is 2
			  		if (son.getRank()-son.getLeftin().getRank()==1) { //only one rotate needed
			  			if (this.getRoot()==parent) {
			  				root=son;
			  			}
			  			parent.rotateWithLeftChild();
			  			parent.demote();
			  			count_rebalance++;
			  			return count_rebalance;
			  		}
			  		else { //double rotate is needed
			  			if (this.getRoot()==parent) {
			  				root=son.getRightin();
			  			}
			  			son.rotateWithRightChild();
			  			parent.rotateWithLeftChild();
			  			son.demote();
			  			parent.demote();
			  			parent.getFather().promote();
			  			count_rebalance+=2;
			  			return count_rebalance;
			  		}
			  	}
			  	else { //son is a right child
			  		if (parent.getRank()-son.getRank()>0) {  //checking if we're done
			  			return count_rebalance;
			  		}
			  		int rank_difference=parent.getRank()-parent.getLeftin().getRank();
			  		if (rank_difference==1) {
			  			parent.promote();
			  			count_rebalance++;
			  			if (parent==this.root) {
			  				return count_rebalance;
			  			}
			  			son=parent;
			  			parent=parent.getFather();
			  			continue;
			  		}
			  		//difference with other son is 2
			  		if (son.getRank()-son.getRightin().getRank()==1) { //only one rotate needed
			  			if (this.getRoot()==parent) {
			  				root=son;
			  			}
			  			parent.rotateWithRightChild();
			  			parent.demote();
			  			count_rebalance++;
			  			return count_rebalance;
			  		}
			  		else { //double rotate is needed
			  			if (this.getRoot()==parent) {
			  				root=son.getLeftin();
			  			}
			  			son.rotateWithLeftChild();
			  			parent.rotateWithRightChild();
			  			son.demote();
			  			parent.demote();
			  			parent.getFather().promote();
			  			count_rebalance+=2;
			  			return count_rebalance;
			  		}
			  	}
		  }
	  }
  }


  /**
   * public int delete(int k)
   *
   *@param k - the key of the node which is desired to be deleted.
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * @returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k)
   {
	   int counter_rebalancing=0;
	   
	   if (search(k)==null) { //item not found in tree
		   return -1;
	   }	   
	   
	   IWAVLNode delete_node=search_place; //after the search, we need a pointer to the be-deleted node
	   
	   //updating min:
	   if (this.min==delete_node) {
		   if (delete_node.getRightin()!=virtual) {
			   this.min=(this.min.getRightin());
		   }
		   else if (delete_node.getFather()!=null) {
			   this.min=this.min.getFather();
		   }
		   else { //delete_node is the root
				   this.min=null;
		   }
	   }
	   
	   //Updating max:
	   if (this.max==delete_node) {
		   if (this.max.getLeftin()!=virtual) {
				   this.max=this.max.getLeftin();
		   }
		   else if (delete_node.getFather()!=null) {
			   this.min=delete_node.getFather();
		   }
		   else { //delete_node is the root
				   this.max=null;
		   }
	   }
	   

	 //if k is the key of the root:
	   if (delete_node==this.root) {		   
		   if (delete_node.getRightin()==virtual) {
			   if (delete_node.getLeftin()==virtual) {
				   this.root=null;
				   return 0;
			   }
			   else {
				   this.root=delete_node.getLeftin();
				   this.root.setFather(null);
				   return 0;
			   }
		   }
		   else if (delete_node.getLeftin()==virtual) { //getRight isn't virtual
			   this.root=delete_node.getRightin();
			   this.root.setFather(null);
			   return 0;
		   }
	   }	
	   
	   if (delete_node.getRightin()!=virtual && delete_node.getLeftin()!=virtual) { //delete node is an internal node. switching place with successor!
		   IWAVLNode delete_node_seccessor=successor_for_deletion(delete_node);
		   size_update(delete_node_seccessor.getKey(),this.root, -1); //updating tree's nodes sizes - key is after switching

		   //switching information of deleted node and it's descendant (because deleted node isn't 
		   //leaf or unary node
		   int delete_node_seccessor_key=delete_node_seccessor.getKey();
		   String delete_node_seccessor_info=delete_node_seccessor.getValue();
		   delete_node_seccessor.setKey(delete_node.getKey());
		   delete_node_seccessor.setValue(delete_node.getValue());
		   delete_node.setKey(delete_node_seccessor_key);
		   delete_node.setValue(delete_node_seccessor_info);
		   
		   delete_node=delete_node_seccessor;
		      
	   }
	   else {
		   size_update(k,this.root, -1); //updating tree's nodes sizes if delete_node is leaf or
		   //unary node
	   }
	   
	   //the Deletion itself
	   IWAVLNode delete_node_father=delete_node.getFather();
	   boolean isRight=false;
	   if (delete_node.getFather().getRightin()==delete_node) {
		   isRight=true;
	   }
	   
	   if (!isRight) {
		  if (delete_node.getRank()==0) { //if delete_node is a leaf
			  if (delete_node_father.getRank()==1) {
				  if (delete_node_father.getRightin().getRank()==0) { //terminal case
					  delete_node_father.setLeft(virtual);
					  return 0;
				  }
				  else if (delete_node_father.getRightin().getRank()==-1) { //not terminal case
					  delete_node_father.demote();
					  counter_rebalancing+=1;
					  delete_node_father.setLeft(virtual);
					  if (delete_node_father==this.root) {
						  return counter_rebalancing;
					  }
					  delete_node_father=delete_node_father.getFather(); //for rebalancing
				  }
			  }
			  else { //father rank is 2 - not terminal case
				  delete_node_father.setLeft(virtual);
			  }	  
		  }
		  if (delete_node.getRank()==1) { //if delete_node is a unary node
			  IWAVLNode delete_node_son=delete_node.getRightin(); //checking which side is the son of delete_node
			  if (delete_node.getRightin()==virtual) { 
				  delete_node_son=delete_node.getLeftin();
			  }
			  
			  delete_node_father.setLeft(delete_node_son); //delete_node is deleted, rebalancing check will come later
			  delete_node_son.setFather(delete_node_father);
		  }
	   }
		  
	   if (isRight) {
			  if (delete_node.getRank()==0) { //if delete_node is a leaf
				  if (delete_node_father.getRank()==1) {
					  if (delete_node_father.getLeftin().getRank()==0) { //terminal case
						  delete_node_father.setRight(virtual);
						  return 0;
					  }
					  else if (delete_node_father.getLeftin().getRank()==-1) { //not terminal case
						  delete_node_father.demote();
						  counter_rebalancing+=1;
						  delete_node_father.setRight(virtual);
						  if (delete_node_father==this.root) {
							  return counter_rebalancing;
						  }
						  delete_node_father=delete_node_father.getFather();
					  }
				  }
				  else { //father rank is 2 - not terminal case
					  delete_node_father.setRight(virtual);
				  }	  
			  }
			  if (delete_node.getRank()==1) { //if delete_node is a unary node
				  IWAVLNode delete_node_son=delete_node.getRightin(); //checking which side is the son of delete_node
				  if (delete_node.getRightin()==virtual) { 
					  delete_node_son=delete_node.getLeftin();
				  }
				  
				  delete_node_father.setRight(delete_node_son); //delete_node is deleted, rebalancing check will come later
				  delete_node_son.setFather(delete_node_father);
			  }
	   }
	   
	   //Rebalancing!!!
	   while (true) {
		   if (delete_node_father.getRank()-delete_node_father.getLeftin().getRank()==3) { //rank difference is 3 with left son!
			   if (delete_node_father.getRank()-delete_node_father.getRightin().getRank()==2) { //case1: demote
				   delete_node_father.demote();
				   counter_rebalancing++;
				   if (delete_node_father==this.root) { //the problem cannot rise further
					   return counter_rebalancing;
				   }
				   delete_node_father=delete_node_father.getFather();
				   continue;
			   }
			   //rank difference with right child is 1
			   if (delete_node_father.getRank()-delete_node_father.getRightin().getRightin().getRank()==2) { //terminal case3: rotate
				   if (delete_node_father==this.root) {
					this.root=delete_node_father.getRightin();   
				   }
				   delete_node_father.rotateWithRightChild();
				   delete_node_father.demote();
				   delete_node_father.getFather().promote();
				   if (delete_node_father.getLeftin()==virtual && delete_node_father.getRightin()==virtual) { //delete_node_father is 2,2-leaf
					   delete_node_father.demote(); 
				   }
				   counter_rebalancing++;
				   return counter_rebalancing;
			   }
			   
			   //rank difference with right son of right son of father is 3 = (1+2)! now checking left son of right son
			   if (delete_node_father.getRank()-delete_node_father.getRightin().getLeftin().getRank()==2) { //terminal case4: double rotate
				   if (delete_node_father==this.root) {
					this.root=delete_node_father.getRightin().getLeftin();   
				   }
				   delete_node_father.demote();
				   delete_node_father.demote(); //double demote is needed
				   delete_node_father.getRightin().demote();
				   delete_node_father.getRightin().getLeftin().promote();
				   delete_node_father.getRightin().getLeftin().promote(); //double promote is needed 
				   
				   delete_node_father.getRightin().rotateWithLeftChild(); //double rotation...
				   delete_node_father.rotateWithRightChild();
				   
				   counter_rebalancing+=2;
				   return counter_rebalancing;  
			   }
			   
			   //rank difference of right son with both it's children is 2,2 - double demote is needed! (case2)
			   delete_node_father.demote();
			   delete_node_father.getRightin().demote();
			   counter_rebalancing+=2;
			   if (delete_node_father==this.root) { //the problem cannot rise further
				   return counter_rebalancing;
			   }
			   delete_node_father=delete_node_father.getFather();
			   continue;
		   }
		   
		   else if (delete_node_father.getRank()-delete_node_father.getRightin().getRank()==3) { //rank difference with right son is 3!
			   if (delete_node_father.getRank()-delete_node_father.getLeftin().getRank()==2) { //case1: demote
				   delete_node_father.demote();
				   counter_rebalancing++;
				   if (delete_node_father==this.root) { //the problem cannot rise further
					   return counter_rebalancing;
				   }
				   delete_node_father=delete_node_father.getFather();
				   continue;
			   }
			   //rank difference with left child is 1
			   if (delete_node_father.getRank()-delete_node_father.getLeftin().getLeftin().getRank()==2) { //terminal case3: rotate
				   if (delete_node_father==this.root) {
					this.root=delete_node_father.getLeftin();   
				   }
				   
				   delete_node_father.rotateWithLeftChild();
				   delete_node_father.demote();
				   delete_node_father.getFather().promote();
				   if (delete_node_father.getLeftin()==virtual && delete_node_father.getRightin()==virtual) { //delete_node_father is 2,2-leaf
					   delete_node_father.demote(); 
				   }
				   counter_rebalancing++;
				   return counter_rebalancing;
			   }
			   
			   //rank difference with left son of left  son of father is 3 = (1+2)! now checking right son of left son
			   if (delete_node_father.getRank()-delete_node_father.getLeftin().getRightin().getRank()==2) { //terminal case4: double rotate
				   if (delete_node_father==this.root) {
					this.root=delete_node_father.getLeftin().getRightin();   
				   }
				   
				   delete_node_father.demote();
				   delete_node_father.demote(); //double demote is needed
				   delete_node_father.getLeftin().demote();
				   delete_node_father.getLeftin().getRightin().promote();
				   delete_node_father.getLeftin().getRightin().promote(); //double promote is needed 
				   
				   delete_node_father.getLeftin().rotateWithRightChild(); //double rotation...
				   delete_node_father.rotateWithLeftChild();
				   
				   counter_rebalancing+=2;
				   return counter_rebalancing;  
			   }
			   
			   //rank difference of right son with both it's children is 2,2 - double demote is needed! (case2)
			   delete_node_father.demote();
			   delete_node_father.getLeftin().demote();
			   counter_rebalancing+=2;
			   if (delete_node_father==this.root) { //the problem cannot rise further
				   return counter_rebalancing;
			   }
			   delete_node_father=delete_node_father.getFather();
			   continue;
		   }
		   
		   else { //no more rebalancing needed!
			   return counter_rebalancing;
		   }
	   }
   }

   /** 
    * @pre - A has a right son (is not a virtual node)
    * 
    * @param A - the node which it's successor is about to be found
    * @return - the successor of A
    * 
    */
   public IWAVLNode successor_for_deletion (IWAVLNode A) { 
	   IWAVLNode answer=A.getRightin();	 
	   while (answer.getLeftin()!=virtual) {
		   answer=answer.getLeftin();
	   }
	   return answer;
   }
   

   /**
    * public String min()
    *
    * @Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    */
   public String min()
   {
	   if (this.min==null) {
		   return null;
	   }
	   return this.min.getValue();
   }
 

   /**
    * public String max()
    *
    * @Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
	   if (this.max==null) {
		   return null;
	   }
	   return this.max.getValue();
   }

  /**
   * public int[] keysToArray()
   *
   * @Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */   
 
   public int[] keysToArray()
  {
      if (this.empty()) {
    	  return new int[0];
      }
	  int[] keys_array=new int[root.getSubtreeSize()];
	  int i=0;
	  keysToArray_rec(i,keys_array,this.root);
      return keys_array;
  }
  
  public int keysToArray_rec(int i, int[] keys_array, IWAVLNode place) {
	  if (place.getLeftin().getRank()!=-1) {
		  i=keysToArray_rec(i, keys_array, place.getLeftin());
	  }
	  keys_array[i]=place.getKey();
	  i++;
	  if (place.getRightin().getRank()!=-1) {
		  i=keysToArray_rec(i, keys_array, place.getRightin());
	  }
	  return i;
  } 
  
  
  /**
   * public String[] infoToArray()
   *
   * @Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
  public String[] infoToArray()
  {
      if (this.empty()) {
    	  return new String[0];
      }
	  String[] info_array=new String[root.getSubtreeSize()];
	  int i=0;
	  infoToArray_rec(i,info_array,this.root);
      return info_array;
  }
  
  public int infoToArray_rec(int i, String[] info_array, IWAVLNode place) {
	  if (place.getLeftin().getRank()!=-1) {
		  i=infoToArray_rec(i, info_array, place.getLeftin());
	  }
	  info_array[i]=place.getValue();
	  i++;
	  if (place.getRightin().getRank()!=-1) {
		  i=infoToArray_rec(i, info_array, place.getRightin());
	  }
	  return i;
  }

  
   /**
    * public int size()
    *
    * @Returns the number of nodes in the tree.
    *
    * @precondition: none
    * @postcondition: none
    */
   public int size()
   {
	   if (empty()) {
		   return 0;
	   }
	   else {
		   return this.root.getSubtreeSize();   
	   }
   }
   
     /**
    * public int getRoot()
    *
    * @Returns the root WAVL node, or null if the tree is empty
    *
    * @precondition: none
    * @postcondition: none
    */
   public IWAVLNode getRoot()
   {
	   return this.root;
   }
   
     /**	
    * public int select(int i)
    *
    * 
    * Example 1: select(1) returns the value of the node with minimal key 
	* Example 2: select(size()) returns the value of the node with maximal key 
	* Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor 	
    *
    * @Returns the value of the i'th smallest key (return -1 if tree is empty)
	* 
	* @precondition: size() >= i > 0
    * @postcondition: none
    */   
   
   public String select(int i) {
	   if (empty() || i>this.root.getSubtreeSize()) {
		   return null;
	   }
	   
	   if (this.root.getLeftin().getSubtreeSize()>=i) { //finger search from minimum
		   IWAVLNode root_of_subTree=this.min;
		   while (root_of_subTree.getSubtreeSize()<i) {
			   root_of_subTree=root_of_subTree.getFather();
		   }
		   return select_rec(root_of_subTree, i).getValue();
	   }
	   else { //finger search from maximum
		   IWAVLNode root_of_subTree=this.max;
		   while (root_of_subTree.getSubtreeSize()<this.root.getSubtreeSize()-i+1) {
			   root_of_subTree=root_of_subTree.getFather();
		   }
		   i=root_of_subTree.getSubtreeSize()+i-this.root.getSubtreeSize();
		   return select_rec(root_of_subTree, i).getValue();   
	   }
   }
  
   /** 
    * helps "select", doing a recursive search of the i-th item in a tree which's root is place.
    * 
    * @param place - root of the sub tree
    * @param i, i<place.getSubtreesize()
    * @return the node which is the i-th node in the tree
    */
   public IWAVLNode select_rec(IWAVLNode place, int i) {
	   int r=place.getLeftin().getSubtreeSize()+1;
	   if (i==r) {
		   return place;
	   }
	   else if (i<r) {
		   return select_rec(place.getLeftin(), i);
	   }
	   else {
		   return select_rec(place.getRightin(), i-r);
	   }
   }

   
	/**
	   * public interface IWAVLNode
	   * ! Do not delete or modify this - otherwise all tests will fail !
	   */
	public interface IWAVLNode{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public void setKey(int k);
		
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setValue(String value);
		
		public IWAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setLeft(IWAVLNode L);
		
		public IWAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setRight(IWAVLNode R);
		
		public IWAVLNode getLeftin(); //for internal use
		public IWAVLNode getRightin(); //for internal use 
		
		public IWAVLNode getFather();
		public void setFather(IWAVLNode F);
		
		public boolean isRealNode(); // Returns True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)
		
		public void setSubtreeSize(int s); 
		public int getSubtreeSize(); // Returns the number of real nodes in this node's subtree (Should be implemented in O(1))
		
		public void setRank(int r);
		public int getRank();
		public void promote();
		public void demote();
		
		public void rotateWithRightChild();
		public void rotateWithLeftChild();
	}

   /**
   * public class WAVLNode
   *
   * If you wish to implement classes other than WAVLTree
   * (for example WAVLNode), do it in this file, not in 
   * another file.
   * This class can and must be modified.
   * (It must implement IWAVLNode)
   */
  public class WAVLNode implements IWAVLNode{
		private String value;
		private int key;
		private IWAVLNode left;
		private IWAVLNode right;
		private IWAVLNode father;
		private int rank;
		private int subtreesize;
		
		public WAVLNode(int key, String value, boolean isVirtual) {
			this.value=value;
			this.key=key;
			if (isVirtual) {
				this.rank=-1;
			}
		}
	  
		
		public void setKey(int k) {
			this.key=k;
		}
		public int getKey()
		{
			if (rank==-1) {
				return -1;
			}
			return key; 
		}
		
		
		public void setValue(String value) {
			this.value=value;
		}
		public String getValue()
		{
			if (value==null) {
				return null;
			}
			return value; 
		}
		
		
		public void setLeft(IWAVLNode L) {
			this.left=L;
		}
		public IWAVLNode getLeftin()
		{
			return left; 
		}
		public IWAVLNode getLeft() {
			if (this.left.getRank()!=-1) {
				return this.left;
			}
			else {
				return null;
			}
		}
		
		
		public void setRight(IWAVLNode R) {
			this.right=R;
		}
		public IWAVLNode getRightin()
		{
			return right; // 
		}
		public IWAVLNode getRight() {
			if (this.right.getRank()!=-1) {
				return this.right;
			}
			else {
				return null;
			}
		}
		
		
		public void setFather(IWAVLNode F) {
			this.father=F;
		}
		public IWAVLNode getFather() {
			return father;
		}
		
		
		public void setRank(int r) {
			this.rank=r;
		}
		public int getRank() {
			return this.rank;
		}
		public void promote() {
			this.setRank(this.getRank()+1);
		}
		public void demote() {
			this.setRank(this.getRank()-1);
		}
		
		
		public void setSubtreeSize(int s) {
			this.subtreesize=s;
		}
		public int getSubtreeSize()
		{
			return subtreesize; 
		}
		
		
		/**
		 * @Returns True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)
		 */
		public boolean isRealNode()
		{
			if (rank>-1) {
				return true;
			}
			return false; 
		}
		
		
		/**
		 * used for rotation of a node with it's right child - used in insertion and deletion,
		 * as part of rebalancing.
		 */
		public void rotateWithRightChild() {
			IWAVLNode right_child=this.right;
			
			//updating subtreesizes
			this.setSubtreeSize(this.getLeftin().getSubtreeSize()+this.getRightin().getLeftin().getSubtreeSize()+1);
			right_child.setSubtreeSize(this.getSubtreeSize()+right_child.getRightin().getSubtreeSize()+1);
			
			//we need to update the grandparent son - now checking which side is the father (if father!=root).
			if (this.getFather()!=null) { //*this* is not a root
				right_child.setFather(this.getFather());
				if (this.getFather().getRightin()==this) {
					this.getFather().setRight(right_child);
				}
				else {
					this.getFather().setLeft(right_child);
				}
			}
			else {
				right_child.setFather(null);
			}
			this.setFather(right_child);
			this.setRight(right_child.getLeftin());
			right_child.setLeft(this);
			this.getRightin().setFather(this);	
		}
		
		
		/**
		 * used for rotation of a node with it's left child - used in insertion and deletion,
		 * as part of rebalancing.
		 */
		public void rotateWithLeftChild() {
			IWAVLNode left_child=this.left;
			
			//updating subtreesizes
			this.setSubtreeSize(this.getRightin().getSubtreeSize()+this.getLeftin().getRightin().getSubtreeSize()+1);
			left_child.setSubtreeSize(this.getSubtreeSize()+left_child.getLeftin().getSubtreeSize()+1);
			
			//we need to update the grandparent son - now checking which side is the father (if father!=root).
			
			if (this.getFather()!=null) { //*this* is not a root
				left_child.setFather(this.getFather());
				if (this.getFather().getRightin()==this) {
					this.getFather().setRight(left_child);
				}
				else {
					this.getFather().setLeft(left_child);
				}
			}
			else{
				left_child.setFather(null);
			}
			this.setFather(left_child);
			this.setLeft(left_child.getRightin());
			left_child.setRight(this);
			this.getLeftin().setFather(this);
		}
  }
}
  

