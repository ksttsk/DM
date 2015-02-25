import java.util.ArrayList;
import java.util.List;


public class AprioriTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Apriori apr = new Apriori();
		apr.set_level(3);
		apr.set_minSupport(0.2);
		apr.set_minConfidence(0.2);
		apr.set_transactionFilePath(".\\trans.txt");
		
		apr.run();
		
		
//		ArrayList2d<String> list2d = new ArrayList2d<String>();
//		list2d.Add("1", 1);
//		list2d.Add("2", 2);
//		list2d.set(0, 0, "A");
//		list2d.set(0, 0, "B");
//		System.out.println(list2d.getNumRows());
		
		
//		List<ArrayList<String>> list2d = new ArrayList<ArrayList<String>>();
//		ArrayList<String> list_1 = new ArrayList<String>();
//		ArrayList<String> list_2 = new ArrayList<String>();
//		list_1.add("A");
//		list_1.add("B");
//		list_2.add("1");
//		list_2.add("2");
//		list2d.add(list_1);
//		list2d.add(list_2);
//		
//		System.out.println(list2d.get(0));
//		System.out.println(list2d.get(1));
		
	}

}
