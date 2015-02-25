import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 
 * @author hw
 * @glossary 
 * 	transaction: a line in the transaction file, which tells what items are sold. 0 = not sold, 1 = sold
 * 	item: the id of an item in the transaction, the id starts from 0
 * 	itemSet: a set of items, separated with " ", such as "0 1 2". But a set could also be just one item
 *  candidate: a group of item sets, from which the frequent item sets will be calculated    
 *  freqItemSet: a set of items, whose Support is bigger than minimum Support
 *  freqItem: an item of a freqItemSet
 *  count: the amount of transactions that a item exists
 *  
 * @namingConversion
 * 	Style: Camel Case, always complete word except those common abbreviation or stated in glossary
 * 	Class: a name, first letter capitalized
 *  Method: a verb, first letter small
 *  Class variable: a name, first letter "_", second letter small
 *  Local variable: a name, first letter small
 *  Method parameter: a name, first letter small
 *  Constant: a name, all capitalized
 */


public class Apriori {
	
	private String _transactionFilePath;
	private int _level;
	private double _minSupport;
	private double _minConfidence;
	private List<String> _freqItemSetStore; //to store all the freqItems of all the levels, to calculate confidence. it contains all the items for level 1
	private List<Integer> _freqItemSetCountStore;//to store all the counts of all the freqItems, to calculate confidence. it contains all the items for level 1
	
	/*************************************************************************CONSTRUCTOR GETTER SETTER*************************************************************************/
	public Apriori() {
		this._freqItemSetStore = new ArrayList<String>();
		this._freqItemSetCountStore = new ArrayList<Integer>();
	}
		
	public void set_transactionFilePath(String transactionFilePath) {
		this._transactionFilePath = transactionFilePath;
	}

	public void set_level(int level) {
		this._level = level;
	}
	
	public void set_minSupport(double minSupport) {
		this._minSupport = minSupport;
	}
	
	public void set_minConfidence(double minConfidence) {
		this._minConfidence = minConfidence;
	}
	
	/*************************************************************************LOGIC*************************************************************************/	
	public void run() {
		List<Integer> configInfos = getTransactionInfo(this._transactionFilePath);
		
		int itemNum = configInfos.get(0);
		int transNum = configInfos.get(1);
		
		List<String> freqItems = new ArrayList<String>();
		List<String> candidates = null;
		
		for(int i = 1;i<=this._level;i++) {
			candidates = genCandidates(i, freqItems, itemNum);
		  	//System.out.println("candidates of level " + i + ": " + candidates);
	
		  	freqItems = getFrequentItemSets(this._transactionFilePath, candidates, this._minSupport, transNum, i);
		  	System.out.println("freqItems of level " + i + ": " + freqItems);		  	
		}
		
		getConfidentRules(this._minConfidence, transNum, itemNum);
	}
	
	
	
	
	
	/*************************************************************************PRIVATE*************************************************************************/
	
	/**
	 * @Purpose get the candidates, which are the basis to calculate frequent item sets. frequent item sets are those item sets which meets the minimum Support
	 * @param level
	 * @param freqItemSets, the frequent item sets of a level
	 * @param itemNum, the amount of items
	 * @return the ids of the candidates, an id starts at 0
	 */
	private List<String> genCandidates(int level, List<String> freqItemSets, int itemNum) {
    	List<String> candidates = new ArrayList<String>(); 
    	String[] items_i = null;
    	String[] items_j = null;
    	int itemAmount_j;
    	String sortedItemSet;
    	if(level == 1) {
    		for(int i=0; i<itemNum; i++) {
    			candidates.add(String.valueOf(i));
    		}
    	} else if(level >= 2) {
    		for(int i=0; i<freqItemSets.size(); i++)
    		{
    			for(int j=i+1; j<freqItemSets.size();j++)
    			{
    				items_i = freqItemSets.get(i).split(" ");
    				items_j = freqItemSets.get(j).split(" ");
    				itemAmount_j = items_j.length;
    				for(int c=0;c<itemAmount_j;c++)
    				{
    					sortedItemSet = sortItemSet("" + freqItemSets.get(i) + " " + items_j[c]);
    					if(!Arrays.asList(items_i).contains(items_j[c]) && !candidates.contains(sortedItemSet))
    					{
    						candidates.add(sortedItemSet);		
    					}
    				}
    			}
    		}    		
    	}
    	    	
    	return candidates;
    }
	
	
	/**
	 * 
	 * @param filePath
	 * @param freqItems
	 * @param minSup
	 * @param transNum
	 * @return the frequent items 
	 */
	private List<String> getFrequentItemSets(String filePath, List<String> candidates, double minSupport, int transNum, int level) {
    	List<String> freqItemSets = new ArrayList<String>();
    	String line = null; //a line of the transaction file
    	String[] bools = null; //a bool is either 0 or 1. bools stores all the 0 and 1 of a line    	   	
    	double support; //calculated support of each item set
    	
    	int candidatesAmount = candidates.size();    	
    	int[] counts = new int[candidatesAmount]; //how many times that an item set exists in all the actions
    	
    	BufferedReader br = null;
    	String[] candidateItems = null; //the items of a candidate, f.e. a candidate is "1 3 4", then the candidate items are "1", "3", "4"
    	int candidateItemsCounter; //a counter to loop the candidateItems[]    	
    	int tempNum ;
    	
    	try {
			br = new BufferedReader(new FileReader(filePath));
			
			while ((line = br.readLine()) != null) {				
				bools = line.split(" ");   
				
    			for(int j = 0; j< candidatesAmount; j++)
    			{
    				candidateItems = candidates.get(j).split(" ");    				
    				candidateItemsCounter = 0;
    				tempNum = 0;
    				
    				while(candidateItemsCounter < candidateItems.length)
    				{
    					if(bools[Integer.valueOf(candidateItems[candidateItemsCounter])].equals("1"))
    					{
    						tempNum++;
    					}
    					else
    					{
    						break;
    					}
    					candidateItemsCounter++;
    				}
    				    				
    				if(tempNum == candidateItems.length) {
    					counts[j] ++;
    				}
    			}
			}
									
			//get support of each item
    		for(int j = 0; j < candidatesAmount; j++)
    		{
    			support = (double)counts[j] / transNum;
    			
    			//all the item sets of level 1 and their counts will be save
    			if(level == 1) {
    				saveInfo(counts[j], candidates.get(j));
    			}
    			
    			//all the frequent item sets of level 2 or higher will be saved
    			if(support >= minSupport) {    				
    				freqItemSets.add(candidates.get(j));
    				if(level >= 2) {
    					saveInfo(counts[j], candidates.get(j));	
    				}    				
    			}
    		}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}    	
    	
    	return freqItemSets;
    }
	
	
	/**
	 * 
	 * @param freqItems
	 * @param minConfidence
	 * @param transNum
	 * @return the confident rules, the level is of course larger than 2
	 */
//	private List<String> getConfidentRules(double minConf, int transNum, int itemNum) {
	private void getConfidentRules(double minConfidence, int transNum, int itemNum) {
		List<ArrayList<String>> rules = new ArrayList<ArrayList<String>>();
		ArrayList<String> ruleLefts = new ArrayList<String>();
		ArrayList<String> ruleRights = new ArrayList<String>();
		ArrayList<Double> confidences = new ArrayList<Double>();
		
		int num = this._freqItemSetStore.size();
		String freqItemSet;
		String[] freqItems;
		double confidence;
		int freqItemAmount;
				
		for(int i = itemNum; i<num; i++) { //loop starts from item set of level 2
			freqItemSet = this._freqItemSetStore.get(i);
			freqItems = freqItemSet.split(" ");
			freqItemAmount = freqItems.length;
			
			for(int j = 1; j <= freqItemAmount -1; j++) { //the left side has j items
				//rules such as x -> y, z, ...
				if(j == 1) {
					for(String item: freqItems)	{
						confidence = (double) this._freqItemSetCountStore.get(i) / this._freqItemSetCountStore.get(Integer.valueOf(item));
						if(confidence >= minConfidence) {
							ruleLefts.add(item);
							ruleRights.add(freqItemSet.replace(item, "").trim().replace("  ", " "));
							confidences.add(confidence);
							//System.out.println(item + " -> " + freqItemSet.replace(item, "").trim().replace("  ", " ") + " with Confidence = " + Math.round(confidence * 100.0)/100.0);					
						}
					}
				} 
				/* if A -> C, D; Then it must A, C -> D; and A, D -> C. so after creating rules with 1 item on the left, we create rules have multiple items on the left 
				 */
				else {
					//check if ruleRights contains multiple items
					for(int m=0; m<ruleRights.size();m++)
					{
						if(ruleRights.get(m).length()>1) {
							System.out.println(ruleLefts.get(m));
							System.out.println(ruleRights.get(m));
							
						}							
					}
				}
			}
		}	
		
		
		rules.add(ruleLefts);
		rules.add(ruleRights);
		
		//print rules
		for(int i = 0; i < ruleLefts.size(); i++) {
			System.out.println(ruleLefts.get(i) + " -> " + ruleRights.get(i) + " with Confidence = " + confidences.get(i));
		}
			
		
//		return rules;
	}
	
	
	/**
	 * @Purpose save the frequent item sets and their counts, these information will be used to calculate confidence.  
	 * 
	 */
	private void saveInfo(int count, String itemSet)
	{
		this._freqItemSetStore.add(itemSet);
		this._freqItemSetCountStore.add(count);		
	}
	
	/**
	 *  
	 * @param filePath, the path of the transaction file
	 * @return the amount of rows in the transaction file, and the amount of items
	 */
	private List<Integer> getTransactionInfo(String filePath) {    	
    	List <Integer> infos = new ArrayList<Integer>();
    	int itemNum = 0, transNum = 0;
    	try {
    		 transNum = getTotalLineAmount(filePath);
    		 String firstLine = getFirstLine(filePath);
    		 String[] items = firstLine.split(" ");
    		 itemNum = items.length;
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	infos.add(itemNum);
    	infos.add(transNum);
    	return infos;
    }  
	
	
	/**
	 * 
	 * @param filePath
	 * @return the first line of a flat file
	 * @throws IOException
	 */
	private String getFirstLine(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String firstLine = br.readLine();
		br.close();
		return firstLine;
	}
	
	
	/**
	 * @Purpose The fastest way to get the amount of lines in a flat file
	 * @param filePath
	 * @return the amount of lines in a flat file
	 * @throws IOException
	 */
	private int getTotalLineAmount(String filePath) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filePath));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}

		
	/**
	 * @Purpose sort the itemSet numerically, f.e. sort "1 4 2" to "1 2 4"
	 * @param itemSet, such as "1 2 4"
	 * @return the sorted itemSet
	 */
	private String sortItemSet(String itemSet)
    {
		String sortedItemSet ="";
		String[] items = itemSet.split(" ");  
		
		//convert items from string into numbers
    	int[] numbers = new int[items.length];
    	for(int i = 0;i < items.length;i++)
    	{
    	   numbers[i] = Integer.parseInt(items[i]);
    	}
//    	System.out.println("before Sort: " + Arrays.toString(numbers));
    	Arrays.sort(numbers);
//    	System.out.println("after Sort: " + Arrays.toString(numbers));
    	
    	for(int i = 0;i < items.length;i++)
    	{
    		sortedItemSet += "" + numbers[i] + " ";
    	}
    	
    	sortedItemSet = sortedItemSet.substring(0, sortedItemSet.length()-1);
    	
    	return sortedItemSet;
    }
}
