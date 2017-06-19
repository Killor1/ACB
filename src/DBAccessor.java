import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.StringTokenizer;

public class DBAccessor {
	private String dbname;
	private String host;
	private String port;
	private String user;
	private String passwd;
	private String schema;
	Connection conn = null;
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public void init() {
		Properties p = new Properties();
		InputStream pStream = this.getClass().getClassLoader().getResourceAsStream("db.properties");
		try {
			p.load(pStream);
			this.dbname = p.getProperty("dbname");
			this.host = p.getProperty("host");
			this.port = p.getProperty("port");
			this.user = p.getProperty("user");
			this.passwd = p.getProperty("passwd");
			this.schema = p.getProperty("schema");

		} catch (IOException e) {
			String message = "ERROR: db.properties file could not be found";
			System.err.println(message);
			throw new RuntimeException(message, e);
		}
	}

	public Connection getConnection() throws SQLException {
		String url = null;
		try {
			// Loads the driver
			Class.forName("org.postgresql.Driver");
			// Prepara connexió a la base de dades
			StringBuffer sbUrl = new StringBuffer();
			sbUrl.append("jdbc:postgresql:");
			if (host != null && !host.equals("")) {
				sbUrl.append("//").append(host);
				if (port != null && !port.equals("")) {
					sbUrl.append(":").append(port);
				}
			}
			sbUrl.append("/").append(dbname);
			url = sbUrl.toString();

			conn = DriverManager.getConnection(url, this.user, this.passwd);
			conn.setAutoCommit(false);

		} catch (ClassNotFoundException e1) {
			System.err.println("ERROR: Al Carregar el driver JDBC");
			System.err.println(e1.getMessage());
		} catch (SQLException e2) {
			System.err.println("ERROR: No connectat  a la BD " + url);
			System.err.println(e2.getMessage());
		}

		if (conn != null) {
			Statement statement = null;
			try {
				statement = conn.createStatement();
				statement.executeUpdate("SET search_path TO " + this.schema);
				// missatge de prova: verificaciÃ³
				System.out.println("OK: connectat a l'esquema " + this.schema + " de la base de dades " + url
						+ " usuari: " + user + " password:" + passwd);
				System.out.println();
				//
			} catch (SQLException e) {
				System.err.println("ERROR: Unable to set search_path");
				System.err.println(e.getMessage());
			} finally {
				try {
					statement.close();
				} catch (SQLException e) {
					System.err.println("ERROR: Closing statement");
					System.err.println(e.getMessage());
				}
			}
		}
		return conn;
	}

	public void mostraEquips() throws Exception {
		Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = st.executeQuery("SELECT * FROM team");
		if (rs == null) {
			System.out.println("No hi han equips.");
		} else {
			while (rs.next()) {
				System.out.format("%s\n", rs.getString(1));
			}
		}
		st.close();
		rs.close();
	}

	public void mostraJugadorsEquip() throws Exception {
		BufferedReader bb = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Introdueix l'equip del que vols veure els jugadors: ");
		String eq = bb.readLine();

		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(
				"SELECT federation_license_code, first_name, last_name, height FROM player WHERE team_name LIKE '" + eq
						+ "'");

		if (rs == null) {
			System.out.println("No s'ha trobat l'equip.");
		} else {
			while (rs.next()) {
				System.out.println(
						rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4));
			}
		}
		rs.close();
		st.close();
	}

	public void crearEquip() throws Exception {
		String city = null;
		String courtName = null;
		BufferedReader bb = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Introdueix el nom del nou equip: ");
		String newTeam = bb.readLine();
		System.out.println("Introdueix el tipus d'equip (club/national team): ");
		String teamType = bb.readLine();
		System.out.println("Introdueix el país: ");
		String country = bb.readLine();

		System.out.println("Introdueix la ciutat: ");
		city = bb.readLine();

		System.out.println("Introdueix el nom de l'estadi: ");
		courtName = bb.readLine();

		Statement st = conn.createStatement();
		st.executeUpdate("INSERT INTO team(name, type, country, city, court_name) VALUES" + "('" + newTeam + "'," + "'"
				+ teamType + "'," + "'" + country + "'," + "'" + city + "'," + "'" + courtName + "')");

		st.close();
	}

	public void crearJugador() throws Exception {
		String sql = "INSERT INTO player(federation_license_code, first_name, last_name, birth_date, gender, height, team_name, mvp_total) VALUES(";

		System.out.println("Introdueix el codi de la llicència: ");
		String codi = br.readLine();

		System.out.println("Introdueix el nom:");
		String firstname = br.readLine();

		System.out.println("Introdueix el cognom:");
		String lastname = br.readLine();

		System.out.println("Introdueix la data de naixement (aaaa-mm-dd):");
		String naix = br.readLine();

		System.out.println("Introdueix el gènere (M/F):");
		String gen = br.readLine();

		System.out.println("Introdueix l'altura (en cm):");
		int altura = Integer.parseInt(br.readLine());

		System.out.println("Introdueix l'equip: ");
		String equip = br.readLine();

		System.out.println("Introdueix el nombre d'mvps:");
		int mvp = Integer.parseInt(br.readLine());

		Statement st;
		st = conn.createStatement();
		try {
			st.executeUpdate(sql + "'" + codi + "','" + firstname + "','" + lastname + "','" + naix + "','" + gen
					+ "','" + altura + "','" + equip + "','" + mvp + "')");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("\nHas introduït alguna dada erròniament o has posat un equip inexistent.");
		} catch (Exception e2) {
			System.out.println(" ");
		}

		st.close();
	}

	public void creaPartit() throws Exception {
		String home, visitor, mDate, mvp;
		int atten;

		System.out.println("Introdueix l'equip local:");
		home = br.readLine();
		System.out.println("Introdueix l'equip visitant:");
		visitor = br.readLine();
		System.out.println("Introdueix la data del partit (aaaa-mm-dd):");
		mDate = br.readLine();
		System.out.println("Introdueix el nombre d'assistents:");
		atten = Integer.parseInt(br.readLine());
		System.out.println("Introdueix l'MVP:");
		mvp = br.readLine();

		Statement st;
		st = conn.createStatement();

		String sql = "INSERT INTO match(home_team,visitor_team,match_date,attendance,mvp_player) VALUES(";

		st.executeUpdate(sql + "'" + home + "','" + visitor + "','" + mDate + "','" + atten + "','" + mvp + "')");
		st.close();
	}

	public void mostraNoEquip() throws Exception {
		String sql = "SELECT * FROM player WHERE team_name IS NULL";
		Statement st;
		st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		if (rs == null) {
			System.out.println("No s'han trobat coincidències.");
		} else {
			while (rs.next()) {
				System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t"
						+ rs.getString(4) + "\t" + rs.getString(5) + "\t" + rs.getString(6) + "\t" + rs.getString(8));
			}
		}
		rs.close();
		st.close();
	}

	public void mostraNomsEquips() throws Exception {
		Statement teams = conn.createStatement();
		ResultSet rt = null;
		rt = teams.executeQuery("SELECT name FROM team");
		while (rt.next()) {
			System.out.println(rt.getString(1));
		}
	}

	public void assignarJug() throws Exception {
		mostraNoEquip();

		System.out.println("\nIntrodueix el número de federació del jugador que vols assignar a un equip:");
		String fn = br.readLine();

		mostraNomsEquips();
		System.out.println("\nIntrodueix el nom de l'equip al que vols assignar el jugador:");
		String nomTeam = br.readLine();

		Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		st.executeUpdate(
				"UPDATE player SET team_name = '" + nomTeam + "' WHERE federation_license_code LIKE '" + fn + "'");
		st.close();

	}

	public void desvincularJug() throws Exception {
		Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
		String fede = null;

		System.out.println("Introdueix el número de la federació de l'MVP:");
		fede = br.readLine();
		// rs = st.executeQuery("SELECT first_name,last_name,team_name FROM
		// player WHERE federation_license_code LIKE '"+fede+"'");
		// rs.updateNull("team_name");
		st.executeUpdate("UPDATE player SET team_name = NULL WHERE federation_license_code LIKE '" + fede + "'");
		st.close();
	}

	public void carregarEstad(Connection conn) throws Exception {
		String s = "INSERT INTO match_statistics VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = conn.prepareStatement(s);
		BufferedReader fr = new BufferedReader(new FileReader("src/estadistiques.csv"));
		String line = null;
		fr.readLine();

		boolean eof = false;
		StringTokenizer token;
		String output[] = new String[22];
		int i = 0;

		do {
			line = fr.readLine();
			if (line == null) {
				eof = true;
			} else {
				token = new StringTokenizer(line, ",");
				while (token.hasMoreElements()) {
					output[i] = token.nextToken();
					i = i + 1;
				}
				
				i = 0;

				ps.clearParameters();

				ps.setString(1, output[0]);
				ps.setString(2, output[1]);
				ps.setDate(3, java.sql.Date.valueOf(output[2]));
				ps.setString(4, output[3]);
				ps.setInt(5, Integer.parseInt(output[4]));
				ps.setInt(6, Integer.parseInt(output[5]));
				ps.setInt(7, Integer.parseInt(output[6]));
				ps.setInt(8, Integer.parseInt(output[7]));
				ps.setInt(9, Integer.parseInt(output[8]));
				ps.setInt(10, Integer.parseInt(output[9]));
				ps.setInt(11, Integer.parseInt(output[10]));
				ps.setInt(12, Integer.parseInt(output[11]));
				ps.setInt(13, Integer.parseInt(output[12]));
				ps.setInt(14, Integer.parseInt(output[13]));
				ps.setInt(15, Integer.parseInt(output[14]));
				ps.setInt(16, Integer.parseInt(output[15]));
				ps.setInt(17, Integer.parseInt(output[16]));
				ps.setInt(18, Integer.parseInt(output[17]));
				ps.setInt(19, Integer.parseInt(output[18]));
				ps.setInt(20, Integer.parseInt(output[19]));
				ps.setInt(21, Integer.parseInt(output[20]));
				ps.setInt(22, Integer.parseInt(output[21]));

				ps.executeUpdate();
			}
		} while (eof != true);

		System.out.println("Dades carregades.");

		Statement aaa = conn.createStatement();
		ResultSet test = aaa.executeQuery("SELECT * FROM match_statistics");
		while (test.next()) {
			System.out.println(test.getString(1) + " " + test.getString(2) + " " + test.getString(3) + " "
					+ test.getString(4) + " " + test.getString(5) + " " + test.getString(6) + " " + test.getString(7)
					+ " " + test.getString(8) + " " + test.getString(9));
		}
		aaa.close();
		ps.close();
		
	}

	public void sortir() throws Exception {
		System.out.println("Adéu!");
		conn.close();
	}
}
