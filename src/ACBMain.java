import java.sql.Connection;

public class ACBMain {

	public static void main(String[] args) throws Exception {
		ACBMenu menu = new ACBMenu();
		
		@SuppressWarnings("unused")
		Connection conn = null;

		int option;
		DBAccessor dbaccessor = new DBAccessor();
		dbaccessor.init();

		conn = dbaccessor.getConnection();
		do {
			option = menu.mPpal();
			switch (option) {
			case 1:
				dbaccessor.mostraEquips();
				break;
			case 2:
				dbaccessor.mostraJugadorsEquip();
				break;
			case 3:
				dbaccessor.crearEquip();
				break;
			case 4:
				dbaccessor.crearJugador();
				break;
			case 5:
				dbaccessor.creaPartit();
				break;
			case 6:
				dbaccessor.mostraNoEquip();
				break;
			case 7:
				dbaccessor.assignarJug();
				break;
			case 8:
				dbaccessor.desvincularJug();
				break;
			case 9:
				dbaccessor.carregarEstad(conn);
				break;
			case 10:
				dbaccessor.sortir();
				break;
			default:
				break;
			}
		} while (option != 10);
	}
}
