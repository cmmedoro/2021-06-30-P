package it.polito.tdp.genes;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.genes.model.Adiacenza;
import it.polito.tdp.genes.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model ;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnStatistiche;

    @FXML
    private Button btnRicerca;

    @FXML
    private ComboBox<String> boxLocalizzazione;

    @FXML
    private TextArea txtResult;

    @FXML
    void doRicerca(ActionEvent event) {
    	String local = this.boxLocalizzazione.getValue();
    	if(local == null) {
    		this.txtResult.setText("Devi selezionare una localizzazione dall'apposita tendina");
    		return;
    	}
    	this.model.cercaCamminoSemplice(local);
    	if(this.model.getBest().size() == 0) {
    		this.txtResult.setText("Non è stato trovato alcun cammino semplice partendo da "+local);
    	}else {
    		this.txtResult.setText("Cammino semplice da "+local+": \n");
    		for(String s : this.model.getBest()) {
    			this.txtResult.appendText(s+"\n");
    		}
    	}

    }

    @FXML
    void doStatistiche(ActionEvent event) {
    	String local = this.boxLocalizzazione.getValue();
    	if(local == null) {
    		this.txtResult.setText("Devi selezionare una localizzazione dall'apposita tendina");
    		return;
    	}
    	//se sono qui proseguo con le statistiche
    	this.txtResult.setText("Adiacenti a: "+local+"\n\n");
    	List<Adiacenza> vicine = this.model.getLocalConnesse(local);
    	for(Adiacenza a : vicine) {
    		this.txtResult.appendText(a.getL2()+" "+a.getPeso()+"\n");
    	}
    }

    @FXML
    void initialize() {
        assert btnStatistiche != null : "fx:id=\"btnStatistiche\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnRicerca != null : "fx:id=\"btnRicerca\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxLocalizzazione != null : "fx:id=\"boxLocalizzazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		//creo il grafo e stampo numero vertici, archi, peso minimo e peso massimo
		this.model.creaGrafo();
		this.txtResult.clear();
		this.txtResult.setText("Grafo creato: "+this.model.nVertici()+" vertici, "+this.model.nArchi()+" archi\n");
		//popolo la tendina con le localizzazioni
		this.boxLocalizzazione.getItems().clear();
		this.boxLocalizzazione.getItems().addAll(this.model.getLocal());
	}
}
