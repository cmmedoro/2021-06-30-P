package it.polito.tdp.genes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.genes.model.Adiacenza;
import it.polito.tdp.genes.model.Genes;
import it.polito.tdp.genes.model.Interactions;


public class GenesDao {
	
	public List<Genes> getAllGenes(){
		String sql = "SELECT DISTINCT GeneID, Essential, Chromosome FROM Genes";
		List<Genes> result = new ArrayList<Genes>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes = new Genes(res.getString("GeneID"), 
						res.getString("Essential"), 
						res.getInt("Chromosome"));
				result.add(genes);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
	public List<String> getLocalizations(){
		String sql = "SELECT DISTINCT c.Localization "
				+ "FROM classification c";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(res.getString("c.Localization"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
	public List<Adiacenza> getArchi(){
		String sql = "SELECT DISTINCT c1.Localization AS l1, c2.Localization AS l2, COUNT(DISTINCT i.`Type`) AS peso "
				+ "FROM classification c1, classification c2, interactions i "
				+ "WHERE c1.GeneID = i.GeneID1 AND c2.GeneID = i.GeneID2 AND c1.Localization <> c2.Localization "
				+ "GROUP BY c1.Localization, c2.Localization";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(new Adiacenza(res.getString("l1"), res.getString("l2"), res.getInt("peso")));
			}
			res.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
	public int getPeso(String l1, String l2) {
		String sql = "SELECT c1.*, c2.*, i.`Type`, COUNT(*) "
				+ "FROM classification c1, classification c2, interactions i "
				+ "WHERE (c1.Localization= ? AND c2.Localization = ? AND c1.GeneID = i.GeneID1 AND c2.GeneID = i.GeneID2) OR  "
				+ "(c1.Localization=? AND c2.Localization = ? AND c1.GeneID = i.GeneID1 AND c2.GeneID = i.GeneID2) "
				+ "GROUP BY i.`Type`";
		int peso = 0;
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, l1);
			st.setString(2, l2);
			st.setString(3, l2);
			st.setString(4, l1);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				peso++;
			}
			res.close();
			st.close();
			conn.close();
			return peso;
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}


	
}
