package p2;


public class Cancion  implements Comparable<Cancion>{

	public String titulo;
	public int duracion;
	public String genero;
	public String version;
	public String idc;
	public String descripcion;
	public String premios;
	public String comentario;


	public Cancion() {

	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIdc() {
		return idc;
	}

	public void setIdc(String idc) {
		this.idc = idc;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}


	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getPremios() {
		return premios;
	}

	public void setPremios(String premios) {
		this.premios = premios;
	}

	@Override
	public int compareTo(Cancion song) {

		String titulo = song.getTitulo();
		String genero = song.getGenero();

		if(this.genero.equals(genero)) {
			return this.titulo.compareTo(titulo);
		}

		return this.genero.compareTo(genero);

	}

















}
