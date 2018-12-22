package task;

import java.sql.Timestamp;
import java.util.Date;

public class TestDate {

	public static void main(String[] args) {
		Timestamp timestamp = new Timestamp(1545458013721L);
		System.out.println(timestamp.toLocaleString());
		
		Date date = new Date(1545458013721L);
		System.out.println(date.toLocaleString());
	}

}
