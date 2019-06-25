package p2;

import java.util.ArrayList;

public class Disco implements Comparable<Disco>{

	public String titulo;
	public ArrayList<String> premios;
	public String interprete;
	public ArrayList<Cancion> infoCanciones;
	public String idd;
	public String langs; //lista de idiomas, arraylist?

	public Disco() {

	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public ArrayList<String> getPremios() {
		return premios;
	}

	public void setPremios(ArrayList<String> premios) {
		this.premios = premios;
	}

	public String getInterprete() {
		return interprete;
	}

	public void setInterprete(String interprete) {
		this.interprete = interprete;
	}

	public ArrayList<Cancion> getInfoCanciones() {
		return infoCanciones;
	}

	public void setInfoCanciones(ArrayList<Cancion> infoCanciones) {
		this.infoCanciones = infoCanciones;
	}

	public String getIdd() {
		return idd;
	}

	public void setIdd(String idd) {
		this.idd = idd;
	}

	public String getLangs() {
		return langs;
	}

	public void setLangs(String langs) {
		this.langs = langs;
	}


	@Override
	public int compareTo(Disco disco) {

		String titulo = disco.getTitulo();


		return this.titulo.compareTo(titulo);

	}



































}
