package it.polito.tdp.meteo;

import java.util.*;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	private MeteoDAO mDAO = new MeteoDAO();
	private List<Citta> citta = mDAO.getAllCitta();
	List<SimpleCity> ottimo = new ArrayList<SimpleCity>();
	private int mese = 0;

	public Model() {}

	public String getUmiditaMedia(int mese) {

		String s = "";

		for (Citta c : citta) {

			s += c.getNome() + " " + mDAO.getAvgRilevamentiLocalitaMese(mese, c.getNome()) + '\n';

		}
		return s;
	}

	public String trovaSequenza(int mese){

		List<SimpleCity> parziale = new ArrayList<SimpleCity>();
		this.mese = mese;
		ricorsione(parziale, 0);

		return ottimo.toString();
	}

	private void ricorsione(List<SimpleCity> parziale, int step){
		
		if (step == NUMERO_GIORNI_TOTALI){
			
			if (ottimo.size() == 0) {
				ottimo.addAll(parziale);
			}
			if (punteggioSoluzione(parziale) < punteggioSoluzione(ottimo) && controllaFinale(parziale)){
				
				ottimo.clear();
				ottimo.addAll(parziale);
				return;
			}
		}

		for (int i = 0; i < citta.size(); i++){

			SimpleCity sc = new SimpleCity(citta.get(i).getNome());
			sc.setCosto(costoGiorno(citta.get(i).getNome(), mese, step+1));
			parziale.add(sc);
			citta.get(i).increaseCounter();

			if (citta.get(i).getCounter() <= NUMERO_GIORNI_CITTA_MAX && controllaParziale(parziale)) {
				
				this.ricorsione(parziale, step+1);
				System.out.println(step+1);
			}
			parziale.remove(sc);
			citta.get(i).setCounter(citta.get(i).getCounter()-1);
		}

	}

	private int costoGiorno(String nome, int mese, int step) {

		for (Rilevamento d : mDAO.getAllRilevamenti()) {
			if (d.getData().getDay()==step && d.getData().getMonth()==mese && d.getLocalita().compareTo(nome)==0){
				return d.getUmidita();
			}
		}
		return 0;
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		SimpleCity s = soluzioneCandidata.get(0);
		double score = 0;
		for (SimpleCity t: soluzioneCandidata) {

			if(!t.equals(s))
				score+=t.getCosto()+COST;
			else
				score+=t.getCosto();
			
			s=t;
		}
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {

		boolean h = true;
		int count = 0;
		
		for (int i =0; i<parziale.size()-1; i++) {
			
			if (parziale.get(i).equals(parziale.get(i+1))){
				count++;
			} else {
				if (count<NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN){
					return false;
				}else{
					count = 1;
				}
			}
		}		
		return h;
	}
	
	private boolean controllaFinale(List<SimpleCity> parziale) {
		
		boolean h = true;
		
		for (Citta c : citta) {
			 SimpleCity y =new SimpleCity(c.getNome());
			 if (!parziale.contains(y)){
				 h=false;
			 }
		}
		
		if(!parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) 
				&& !parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-3)))
			h=false;
		
		return h;
	}
}
