import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class TransactionGenerator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filePath = ".\\trans.txt";
		try {
			run(filePath);
			System.out.println("[filePath] is written.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void run(String filePath) throws IOException {
		File fout = new File(filePath);
		FileOutputStream fos = new FileOutputStream(fout);
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		int lineNum = 10;
		int itemNum = 5;
		
		for (int i = 0; i < lineNum; i++) {
			bw.write(getTransaction(itemNum));
			bw.newLine();
		}
	 
		bw.close();
	}
	
	private static String getTransaction(int itemNum) {
		String result = "";
		double d;
		for(int i=0;i<itemNum;i++) {
			d = Math.random();
			if(d<=0.5) {
				result += " " + 0;
			} else {
				result += " " + 1;
			}				
		}
		
		result = result.substring(1);
		return result;
	}

}
