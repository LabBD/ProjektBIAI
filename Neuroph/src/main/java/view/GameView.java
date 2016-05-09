package view;

import java.util.Scanner;

public class GameView {

	Scanner scanner = new Scanner(System.in);
			
	public double[] colectUserData() {
		try {
			double[] row = new double[8];
			System.out.println("Podaj id gracza");
			row[0] = Double.parseDouble(scanner.nextLine());
			System.out.println("Podaj id gry");
			row[1] = Double.parseDouble(scanner.nextLine());
			return row;
			
		} catch (Exception e) {
			return null;
		}
		
	}

	public double takeNumber() {
		try {
			double number = 0;
			System.out.println("Podaj symbol");
			String line = scanner.nextLine();
			number =  Double.parseDouble(line);	
			return number;
			
		} catch (Exception e) {
			return 0;
		}
	}

}
