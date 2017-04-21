package it.polito.tdp.meteo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;

public class MeteoDAO {

	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita "+
						   "FROM situazione "+
						   "ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {

		final String sql = "SELECT Localita, Data, Umidita "+
						   "FROM situazione "+
						   "WHERE data>=? "+
						   "AND data<=? "+
						   "AND localita=? "+
						   "ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			String d1 = "2013-"+mese+"-01";
			String d2 = "2013-"+mese+"-31";
			st.setString(1, d1);
			st.setString(2, d2);
			st.setString(3, localita);
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {

		final String sql = "SELECT AVG(umidita) "+
				           "FROM situazione "+
				           "WHERE data>=? "+
				           "AND data<=? "+
				           "AND localita=? "+
				           "ORDER BY data ASC";

        double media= 0.0;

        try {
        	Connection conn = DBConnect.getInstance().getConnection();
        	PreparedStatement st = conn.prepareStatement(sql);
        	
        	String d1 = "2013-"+mese+"-01";
        	String d2 = "2013-"+mese+"-31";
        	st.setString(1, d1);
        	st.setString(2, d2);
        	st.setString(3, localita);
	
        	ResultSet rs = st.executeQuery();

        	while (rs.next()) {
        		media=rs.getDouble(1);
        	}

        	conn.close();
        	return media;

        } catch (SQLException e) {

        	e.printStackTrace();
        	throw new RuntimeException(e);
        }
	}

	public List<Citta> getAllCitta() {

		final String sql = "SELECT DISTINCT Localita "+
						   "FROM situazione";

		List<Citta> citta = new ArrayList<Citta>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Citta c = new Citta(rs.getString("Localita"));
				citta.add(c);
			}

			conn.close();
			return citta;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
