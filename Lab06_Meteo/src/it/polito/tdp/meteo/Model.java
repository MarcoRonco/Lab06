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
	private int mese = 0;

	public Model() {

	}

	public String getUmiditaMedia(int mese) {

		String s = "";

		for (Citta c : citta) {

			s += c.getNome() + mDAO.getAvgRilevamentiLocalitaMese(mese, c.getNome()) + '\n';

		}
		return s;
	}

	List<SimpleCity> ottimo = new ArrayList<SimpleCity>();

	public String trovaSequenza(int mese) {

		List<SimpleCity> parziale = new ArrayList<SimpleCity>();
		this.mese = mese;
		ricorsione(parziale, 0);

		return ottimo.toString();
	}

	private void ricorsione(List<SimpleCity> parziale, int step) {
		System.out.println(step);

		System.out.println(parziale);
		
		if (step >= NUMERO_GIORNI_TOTALI) {

			System.out.println("entrato");
			if (ottimo.size() == 0) {
				ottimo.addAll(parziale);
			}

			if (punteggioSoluzione(parziale) < punteggioSoluzione(ottimo)) {

				ottimo.clear();
				ottimo.addAll(parziale);
				System.out.println(ottimo.toString());

			}
		}

		for (int i = 0; i < citta.size(); i++) {

			SimpleCity sc = new SimpleCity(citta.get(i).getNome());

			sc.setCosto(costoGiorno(citta.get(i).getNome(), mese, step + 1));

			parziale.add(sc);
			citta.get(i).setCounter(citta.get(i).getCounter() + 1);

			if (citta.get(i).getCounter() < NUMERO_GIORNI_CITTA_MAX && controllaParziale(parziale) == true) {

				this.ricorsione(parziale, step + 1);

			}

			parziale.remove(sc);
			citta.get(i).setCounter(citta.get(i).getCounter() - 1);
		}

	}

	private int costoGiorno(String nome, int mese, int step) {

		for (Rilevamento d : mDAO.getAllRilevamenti()) {
			if (d.getData().getDay() == step && d.getData().getMonth() == mese
					&& d.getLocalita().compareTo(nome) == 0) {
				return d.getUmidita();
			}
		}
		return 0;
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		double score = 0.0;
		for (int i = 0; i < soluzioneCandidata.size(); i++) {

			if (i > 0) {

				if (soluzioneCandidata.get(i).equals(soluzioneCandidata.get(i - 1))) {
					score += soluzioneCandidata.get(i).getCosto();
				} else {
					score += soluzioneCandidata.get(i).getCosto() + COST;
				}
			} else {
				score += soluzioneCandidata.get(i).getCosto();
			}
		}
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {

		boolean h = true;
		int count = 1;
		for (int i = 0; i < parziale.size() - 1; i++) {
			if (parziale.get(i).equals(parziale.get(i + 1))) {
				count++;
			} else {
				if (count < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
					h = false;
				}
				count = 1;
			}
		}

		if (parziale.size() == 15) {
			for(int y = 0; y < citta.size(); y++) {
				SimpleCity s = new SimpleCity(citta.get(y).getNome());
				if(!parziale.contains(s)){
					h=false;
				}
			}
		}

		return h;
	}

}
