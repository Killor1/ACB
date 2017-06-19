import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ACBMenu {
	private int op;

	public ACBMenu() {
		super();
	}

	public int mPpal() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println(" \nMENU PRINCIPAL \n");
		System.out.println("1. Mostra equips ");
		System.out.println("2. Mostra Jugadors d'un equip ");
		System.out.println("3. Crea equip ");
		System.out.println("4. Crea jugador ");
		System.out.println("5. Crea partit ");
		System.out.println("6. Mostra jugadors sense equip  ");
		System.out.println("7. Assigna un jugador a un equip");
		System.out.println("8. Desvincula jugador d'un equip");
		System.out.println("9. Carrega estadístiques");
		System.out.println("10. Sortir. ");

		System.out.println("Esculli opció: ");
		try {
			op = Integer.parseInt(br.readLine());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return op;
	}
}
