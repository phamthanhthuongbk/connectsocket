package ClientTest;

public class Client {

	public static void main(String[] args) {
		Connect cn = new Connect();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cn.sendRequest();
//		cn.receve();
	}
}
