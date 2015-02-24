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


public class Apriori {
	
	private String transactionFilePath;
	private int level;
	private double minSupport;
		
	/*************************************************************************CONSTRUCTOR GETTER SETTER*************************************************************************/
	public Apriori() {
		// TODO Auto-generated constructor stub
	}
		
	public void setTransactionFilePath(String transactionFilePath) {
		this.transactionFilePath = transactionFilePath;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setMinSupport(double minSupport) {
		this.minSupport = minSupport;
	}
	
	
	/*************************************************************************LOGIC*************************************************************************/
	
	/**
	 *  
	 * @param filePath, the path of the transaction file
	 * @return the amount of rows in the transaction file, and the amount of items
	 */
	public List<Integer> getConfigInfo(String filePath) {    	
    	List <Integer> configs = new ArrayList<Integer>();
    	int itemNum = 0, transNum = 0;
    	try {
    		 transNum = countLines(filePath);
    		 String firstLine = getFirstLine(filePath);
    		 String[] items = firstLine.split(" ");
    		 itemNum = items.length;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	configs.add(itemNum);
    	configs.add(transNum);
    	return configs;
    }   
	
	
	public void run() {
		List<Integer> configInfos = getConfigInfo(this.transactionFilePath);
		
		int itemNum = configInfos.get(0);
		int transNum = configInfos.get(1);
		
		List<String> freqItems = new ArrayList<String>();
		List<String> candidates = null;
		
		for(int i = 1;i<=this.level;i++) {
			candidates = genCandidates(i, freqItems, itemNum);
		  	System.out.println("candidates of level " + i + ": " + candidates);
	
		  	freqItems = getFrequentItems(this.transactionFilePath, candidates, this.minSupport, transNum);
		  	System.out.println("freqItems of level " + i + ": " + freqItems);
		}
	}
	
	
	
	/*************************************************************************PRIVATE*************************************************************************/
	
	/**
	 * get the candidates, which are the basis to calculate frequent items. frequent items are those items or item sets which meets the minimum Support
	 * @param level
	 * @param freqItems, the frequent Items of a level
	 * @param itemNum, the amount of items
	 * @return the ids of the candidates, an id starts at 0
	 */
	private List<String> genCandidates(int level, List<String> freqItems, int itemNum) {
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
    		for(int i=0; i<freqItems.size(); i++)
    		{
    			for(int j=i+1; j<freqItems.size();j++)
    			{
    				items_i = freqItems.get(i).split(" ");
    				items_j = freqItems.get(j).split(" ");
    				itemAmount_j = items_j.length;
    				for(int c=0;c<itemAmount_j;c++)
    				{
    					sortedItemSet = sortItemSet("" + freqItems.get(i) + " " + items_j[c]);
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
	private List<String> getFrequentItems(String filePath, List<String> candidates, double minSup, int transNum) {
    	List<String> freqItems = new ArrayList<String>();
    	String line = null;
    	String[] bools = null; //each bool is either 0 or 1. 0 means that the item is not included in the transaction    	   	
    	double sup;
    	
    	int candidatesAmount = candidates.size();    	
    	int[] counts = new int[candidatesAmount]; // how many times that an item exists in all the actions
    	
    	BufferedReader br = null;
    	String[] candidateItems = null; //the items of a candidate, f.e. a candidate is "1 3 4", then the candidate items are "1", "3", "4"
    	int candidateItemsCounter; // a counter to loop the candidateItems[]    	
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
									
			/* get support of each item */
    		for(int j = 0; j < candidatesAmount; j++)
    		{
    			sup = (double)counts[j] / transNum;
    			if(sup > minSup) {
    				freqItems.add(candidates.get(j));
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
    	
    	return freqItems;
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
	 * The fastest way to get the amount of lines in a flat file
	 * @param filePath
	 * @return the amount of lines in a flat file
	 * @throws IOException
	 */
	private int countLines(String filePath) throws IOException {
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
	 * sort the itemSet numerically, f.e. "1 4 2" to "1 2 4"
	 * @param itemSet, such as "1 2 4"
	 * @return the sorted itemSet
	 */
	private String sortItemSet(String itemSet)
    {
		String sortedItemSet ="";
		String[] items = itemSet.split(" ");    	
    	int[] numbers = new int[items.length];
    	for(int i = 0;i < items.length;i++)
    	{
    	   numbers[i] = Integer.parseInt(items[i]);
    	}
    	//System.out.println("before Sort: " + Arrays.toString(numbers));
    	Arrays.sort(numbers);
    	//System.out.println("after Sort: " + Arrays.toString(numbers));
    	
    	for(int i = 0;i < items.length;i++)
    	{
    		sortedItemSet += "" + numbers[i] + " ";
    	}
    	sortedItemSet = sortedItemSet.substring(0, sortedItemSet.length()-1);
    	//System.out.println(result);
    	return sortedItemSet;
    }
}
