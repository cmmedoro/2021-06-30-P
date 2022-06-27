package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.genes.db.GenesDao;

public class Model {
	
	private GenesDao dao;
	private Graph<String, DefaultWeightedEdge> graph;
	private List<String> localizations;
	//Strutture per la ricorsione
	private List<String> best;
	
	public Model() {
		this.dao = new GenesDao();
		this.localizations = new ArrayList<>();
		this.localizations = this.dao.getLocalizations();
	}
	
	public void creaGrafo() {
		this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		//Aggiungo vertici
		Graphs.addAllVertices(this.graph, this.localizations);
		//Aggiungi archi
		for(Adiacenza a : this.dao.getArchi()) {
			if(this.graph.vertexSet().contains(a.getL1()) && this.graph.vertexSet().contains(a.getL2())) {
				int peso = this.dao.getPeso(a.getL1(), a.getL2());
				Graphs.addEdgeWithVertices(this.graph, a.getL1(), a.getL2(), (double)peso);
			}
		}
	}
	public int nVertici() {
		return this.graph.vertexSet().size();
	}
	public int nArchi() {
		return this.graph.edgeSet().size();
	}
	public boolean grafoEsiste() {
		if(this.graph != null) {
			return true;
		}else {
			return false;
		}
	}
	public List<String> getLocal(){
		return this.localizations;
	}
	
	public List<Adiacenza> getLocalConnesse(String local){
		List<Adiacenza> vicine = new ArrayList<>();
		List<String> vicini = Graphs.neighborListOf(this.graph, local);
		for(String s : vicini) {
			if(this.graph.getEdge(local,s) != null) {
				vicine.add(new Adiacenza(local, s, (int)this.graph.getEdgeWeight(this.graph.getEdge(local, s))));
			}
		}
		return vicine;
	}
	
	public void cercaCamminoSemplice(String local) {
		List<String> parziale = new ArrayList<>();
		parziale.add(local);
		this.best = new ArrayList<>();
		int peso = 0;
		ricercaCammSemplice(parziale, peso);
	}

	private void ricercaCammSemplice(List<String> parziale, int peso) {
		
		if(this.getPesoCammino(parziale) > this.getPesoCammino(best)) {
			this.best = new ArrayList<>(parziale);
		}
		
		String ultimo = parziale.get(parziale.size()-1);
		List<String> vicini = Graphs.neighborListOf(this.graph, ultimo);
		for(String s : vicini) {
			if(!parziale.contains(s)) {
				parziale.add(s);
				int weight = (int)this.graph.getEdgeWeight(this.graph.getEdge(ultimo, s));
				this.ricercaCammSemplice(parziale, peso+weight);
				parziale.remove(parziale.size()-1);
			}
		}
		
		
	}
	
	private int getPesoCammino(List<String> cammino) {
		int peso = 0;
		if(cammino.size() <= 1) {
			peso = 0;
			return peso;
		}
		for(int i = 1; i < cammino.size(); i++) {
			String l1 = cammino.get(i-1);
			String l2 = cammino.get(i);
			DefaultWeightedEdge e = this.graph.getEdge(l1, l2);
			if(e != null) {
				peso += this.graph.getEdgeWeight(e);
			}
		}
		return peso;
	}
	
	public List<String> getBest(){
		return this.best;
	}

}