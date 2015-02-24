
public class AprioriTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Apriori apr = new Apriori();
		apr.setLevel(3);
		apr.setMinSupport(0.2);
		apr.setTransactionFilePath(".\\trans.txt");
		
		apr.run();
	}

}
