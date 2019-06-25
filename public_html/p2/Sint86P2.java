package p2;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;





@WebServlet("/Sint86")
public class Sint86P2 extends HttpServlet {

	private static final long serialVersionUID = 1L;  

	String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	static ArrayList<String> anios = new ArrayList<String>();
	static ArrayList<Document> documentos = new ArrayList<Document>();
	static ArrayList<String> langs = new ArrayList<String>();
	static ArrayList<Cancion> canciones = new ArrayList<Cancion>();
	static ArrayList<String> interpretes = new ArrayList<String>();
	static ArrayList<String> urls = new ArrayList<String>();
	static String sch = new String();
	static String css = new String();

	ArrayList<String> XMLs = new ArrayList<String>();

	public static ArrayList<String> warning = new ArrayList<String>();
	public static ArrayList<String> error = new ArrayList<String>();
	public static ArrayList<String> fatalError = new ArrayList<String>();
	public static HashMap<String, ArrayList<String>> awarning = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, ArrayList<String>> aerror = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, ArrayList<String>> aferror = new HashMap<String, ArrayList<String>>();

	public boolean start = true;
	public static String currentURL = new String();
	public static boolean fallo = false;
	DocumentBuilderFactory dbf = null;
	DocumentBuilder db = null;
	Document documento = null;



	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		String pass = request.getParameter("p");
		String contra = "asdfg12345";
		String pfase = request.getParameter("pfase");
		sch = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+ request.getContextPath() + "/iml.xsd";
		css = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+ request.getContextPath() + "/iml.css";
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();

		if (start) {
			try {
				init(request);
			} catch (XPathExpressionException | ParserConfigurationException e) {
				e.printStackTrace();
			}

			start = false;
		}

		if (pfase == null) {
			pfase = "01";
		}


		if (pass == null) {

			if (request.getParameter("auto") == null) {
				response.setContentType("text/html");
				out.println("<!DOCTYPE html>");
				out.println("<head>");
				out.println("<meta http=\"Content-type\" content=\"text/html\" accept-charset=\"UTF-8\">");
				out.println("<link rel='stylesheet' type='text/css' href='"+css+"'>");
				out.println("<title>Servicio de consulta de información musical</title>");
				out.println("</head>");
				out.println("<h> Debe introducir la contrase�a.<h><br>");
				out.println("</body>");
				out.println("</html>");
			} else {
				out.println("<?xml version=\'1.0\' encoding=\'utf-8\'?>");
				out.println("<wrongRequest>no passwd</wrongRequest>");
			}

		} else if (pass != null && !pass.equals(contra)) {

			if (request.getParameter("auto") == null) {
				response.setContentType("text/html");
				out.println("<!DOCTYPE html>");
				out.println("<head>");
				out.println("<meta http=\"Content-type\" content=\"text/html\" accept-charset=\"UTF-8\">");
				out.println("<link rel='stylesheet' type='text/css' href='"+css+"'>");
				out.println("<title>Servicio de consulta de información musical</title>");
				out.println("</head>");
				out.println("<h>Contraseña incorrecta.<h><br>");
				out.println("</body>");
				out.println("</html>");
			} else {
				out.println("<?xml version='1.0' encoding='utf-8'?>");
				out.println("<wrongRequest>bad passwd</wrongRequest>");
			}

		} else {

			if (!(request.getParameter("auto")==null) && (request.getParameter("auto").equals("si"))) {
				response.setContentType("text/xml");


				try {
					modoAuto(request, response, pfase);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				}
			}
			else {
				response.setContentType("text/html");

				try {

					modoNavegador(request, response, pfase);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				}

			}
		}

	}
	public void init(HttpServletRequest request) throws XPathExpressionException, ParserConfigurationException {


		dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(true);
		dbf.setNamespaceAware(true);
		dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		dbf.setAttribute(JAXP_SCHEMA_SOURCE, sch);

		db = dbf.newDocumentBuilder();
		db.setErrorHandler(new XML_ErrorHandler());

		anios.add("2001");
		urls.add("http://gssi.det.uvigo.es/users/agil/public_html/SINT/18-19/iml2001.xml");

		for (int i = 0; i < anios.size(); i++) {


			fallo = false;
			Document doc = null;
			String url = urls.get(i);
			currentURL = url;

			try {
				doc = db.parse(new URL(url).openStream());



				if (fallo) {
					continue;
				}

				documentos.add(doc);


				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList nodes = (NodeList) xpath.evaluate("//Cancion/Version", doc, XPathConstants.NODESET);


				for (int j = 0; j < nodes.getLength(); j++) {

					String newYear;
					String iml;
					String newUrl;



					newYear = nodes.item(j).getAttributes().getNamedItem("anio").getTextContent();

					Node node = (Node) xpath.evaluate("//Cancion/Version[@anio = '" + newYear + "']/IML", doc, XPathConstants.NODE);
					iml = node.getFirstChild().getNodeValue();

					if (!anios.contains(newYear)) {

						anios.add(newYear);

						if (iml.contains("http://")) {

							urls.add(iml);

						} else {

							newUrl = url.substring(0, url.lastIndexOf("/") + 1) + iml;
							urls.add(newUrl);

						}

					}
				}


			} catch (Exception e) {

				if (XML_ErrorHandler.hasfatalError()) {
					if(!fatalError.contains(e.toString())) fatalError.add(e.toString());
					if(!aferror.containsKey(url)) {
						aferror.put(url,fatalError);
						XML_ErrorHandler.clear();

					}
				}
				if (XML_ErrorHandler.hasError()) {
					if(!error.contains(e.toString())) error.add(e.toString());
					if(!aerror.containsKey(url)) {
						aerror.put(url,error);
						XML_ErrorHandler.clear();

					}
				}
				if (XML_ErrorHandler.hasWarning()) {
					if(!warning.contains(e.toString())) warning.add(url+"&"+e.toString());
					if(!awarning.containsKey(url)) {
						awarning.put(url,warning);
						XML_ErrorHandler.clear();

					}
				}

				fallo = true;
				continue;



			}
		}
	}
	

	public static void modoNavegador(HttpServletRequest request, HttpServletResponse response, String pfase)
			throws IOException, XPathExpressionException {

		PrintWriter out = response.getWriter();
		response.setContentType("text/xml");

		switch (pfase) {

		case "01":

			p01(request, response);
			break;

		case "02":

			p02(request, response, pfase);
			break;

		case "21":

			p21(request, response, pfase);
			break;

		case "22":

			if (request.getParameter("plang") == null) {
				out.println("<h><b>Falta el parámetro: <b>plang</b>.<h><br>");
			} else {
				p22(request, response, pfase);
			}
			break;

		case "23":

			if (request.getParameter("plang") == null) {
				out.println("<h><b>Falta el parámetro: <b>plang</b>.<h><br>");
			} else if (request.getParameter("pgen") == null) {
				out.println("<h><b>Falta el parámetro: <b>pgen</b>.<h><br>");
			} else {
				p23(request, response, pfase);
			}
			break;

		case "24":

			if (request.getParameter("plang") == null) {
				out.println("<h><b>Falta el parámetro: <b>plang</b>.<h><br>");
			} else if (request.getParameter("pgen") == null) {
				out.println("<h><b>Falta el parámetro: <b>pgen</b>.<h><br>");
			} else if (request.getParameter("pint") == null) {
				out.println("<h><b>Falta el parámetro: <b>pint</b>.<h><br>");
			} else {
				p24(request, response, pfase);
			}
			break;

		}

	}

	

	public static void p01(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();
		String password = request.getParameter("p");
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		out.println("<!DOCTYPE html>");
		out.println("<head>");
		out.println("<meta http=\"Content-type\" content=\"text/html\" accept-charset=\"UTF-8\"/>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\""+css+"\"/>");
		out.println("<title>Servicio de consulta de información musical.</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<br><h1>Servicio de consulta de información musical</h1><br>");
		out.println("<br><h2>Bienvenido a este servicio</h2><br>");
		out.println("<form method=GET accept-charset=\"UTF-8\" action='?pfase=01'>");
		out.println("<a href='?p="+password+"&pfase=02'onClick=form.pfase.value='02'>Pulsa aqu� para ver los ficheros err�neos</a>");
		out.println("<h5>Selecciona una consulta:</h5>");
		out.println("<input type='radio' checked>Consulta 2: Discos de un interprete <br>");
		out.println("<input type='hidden' name='p' id='p' value='" + password + "'>");
		out.println("<input type='hidden' name='pfase' id='pfase' value='01'>");
		out.println("<input type='submit' value='Enviar' onclick=form.pfase.value='21'><br>");
		out.println("</form>");
		out.println("<div class=\"footer\"><b>Candela Janeiro Catoira</b></div>");
		out.println("</body>");
		out.println("</html>");

	}


	public static void p02(HttpServletRequest request, HttpServletResponse response, String pfase) throws IOException {

		PrintWriter out = response.getWriter();
		String password = request.getParameter("p");

		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		out.println("<!DOCTYPE html>");
		out.println("<head>");
		out.println("<meta http=\"Content-type\" content=\"text/html\" accept-charset=\"UTF-8\"/>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\""+css+"\"/>");
		out.println("<title>Servicio de consulta de información musical.</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<br><h1>Servicio de consulta de información musical</h1><br>");
		out.println("<form method=GET accept-charset=\"UTF-8\" action='?pfase=02'>");



		out.println("<input type=\"hidden\" name=\"p\" id=\"p\" value=\"" + password + "\">");
		out.println("<input type=\"hidden\" name=\"pfase\" id=\"pfase\" value=\"" + pfase + "\">");

		out.println("<h4> Se han encontrado " + awarning.size() + " ficheros con warnings</h4><br>");

		SortedSet<String> wkeys = new TreeSet<>(awarning.keySet());
		for (String key : wkeys) {
			ArrayList<String> value = awarning.get(key);
			out.println(key +": <br>" + value + " <br><br> ");
		}

		out.println("<h4> Se han encontrado " + aerror.size() + " ficheros con errores:</h4><br>");


		SortedSet<String> ekeys = new TreeSet<>(aerror.keySet());
		for (String key : ekeys) {
			ArrayList<String> value = aerror.get(key);
			out.println(key +": <br>" + value + " <br><br> ");
		}

		out.println("<h4> Se han encontrado " + aferror.size() + " ficheros con errores fatales</h4><br>");


		SortedSet<String> fkeys = new TreeSet<>(aferror.keySet());
		for (String key : fkeys) {
			ArrayList<String> value = aferror.get(key);
			out.println(key +": <br>" + value + " <br><br> ");
		}


		out.println("<input type='submit' value='Inicio' onClick=form.pfase.value='01'>");
		out.println("</form>");
		out.println("<div class=\"footer\"><b>Candela Janeiro Catoira</b></div>");
		out.println("</body>");
		out.println("</html>");

	}


	public static void p21(HttpServletRequest request, HttpServletResponse response, String pfase)
			throws XPathExpressionException, IOException {

		langs = getC2Langs();

		PrintWriter out = response.getWriter();
		String password = request.getParameter("p");

		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		out.println("<!DOCTYPE html>");
		out.println("<head>");
		out.println("<meta http=\"Content-type\" content=\"text/html\" accept-charset=\"UTF-8\"/>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\""+css+"\"/>");
		out.println("<title>Servicio de consulta de información musical.</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<br><h1>Servicio de consulta de información musical</h1><br>");
		out.println("<form method=GET accept-charset=\"UTF-8\" action='?pfase=21'>");



		out.println("<input type=\"hidden\" name=\"p\" id=\"p\" value=\"" + password + "\">");
		out.println("<input type=\"hidden\" name=\"pfase\" id=\"pfase\" value=\"" + pfase + "\">");


		out.println("<h4>Consulta 2. Fase 21: lista de idiomas:</h4>");

		for (int i = 0; i < langs.size(); i++) {

			if (i == (langs.size() - 1)) {
				out.println("<input type=\'radio\' name=\'plang\' value=\'" + langs.get(i) + "\' checked>" + (i + 1)
						+ ".- " + langs.get(i) + "<br>");
			} else {
				out.println("<input type=\'radio\' name=\'plang\' value=\'" + langs.get(i) + "\'>" + (i + 1) + ".- "
						+ langs.get(i) + "<br>");
			}

		}

		out.println("<br><input type=\"submit\" value=\"Enviar\" onclick=\"getElementById(\'pfase\').value=\'22\'\"><br>");
		out.println("<input type=\"submit\" value=\"Atras\" onclick=\"getElementById(\'pfase\').value=\'01\'\"><br>");
		out.println("</form>");
		out.println("<div class=\"footer\"><b>Candela Janeiro Catoira</b></div>");
		out.println("</body>");
		out.println("</html>");

	}



	public static ArrayList<String> getC2Langs() throws XPathExpressionException {

		XPath xpath = XPathFactory.newInstance().newXPath();
		ArrayList<String> langs = new ArrayList<String>();
		Document docAux = null;
		NodeList lang = null;


		for (int i = 0; i < documentos.size(); i++) {

			docAux = documentos.get(i);
			lang =  (NodeList) xpath.evaluate("/Songs/Pais/Disco/@langs", docAux, XPathConstants.NODESET);

			for (int j = 0; j < lang.getLength(); j++) {

				String[] partes = lang.item(i).getTextContent().split(" ");

				for (int k = 0; k < partes.length; k++) {
					if (!langs.contains(partes[k])) {
						langs.add(partes[k]);
					}

				}

			}
		}
		Collections.sort(langs);
		return langs;
	}

	public static void p22(HttpServletRequest request, HttpServletResponse response, String pfase)
			throws IOException, XPathExpressionException {

		String password = request.getParameter("p");
		PrintWriter out = response.getWriter();

		String lang = request.getParameter("plang");

		canciones = getC2Canciones(lang);


		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		out.println("<!DOCTYPE html>");
		out.println("<head>");
		out.println("<meta http=\"Content-type\" content=\"text/html\" accept-charset=\"UTF-8\"/>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\""+css+"\"/>");
		out.println("<title>Servicio de consulta de información musical.</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<br><h1>Servicio de consulta de información musical</h1><br>");
		out.println("<form method=GET accept-charset=\"UTF-8\" action='?pfase=22'>");



		out.println("<input type=\"hidden\" name=\"p\" id=\"p\" value=\"" + password + "\">");
		out.println("<input type=\"hidden\" name=\"pfase\" id=\"pfase\" value=\"" + pfase + "\">");



		for (int i = 0; i < canciones.size(); i++) {

			if (i == 0) {

				out.println("<input type='radio' name='pgen' value='" + canciones.get(i).getGenero() + "'checked>" + (i + 1)
						+ ".- <b>Titulo</b> = '" + canciones.get(i).getTitulo() + "' --- <b>Género</b> = '"
						+ canciones.get(i).getGenero() + "' --- <b>Descripcion</b> = '" + canciones.get(i).getDescripcion()
						+  "'<br>");

			} else {
				out.println("<input type='radio' name='pgen' value='" + canciones.get(i).getGenero() + "'>" + (i + 1)
						+ ".- <b>Titulo</b> = '" + canciones.get(i).getTitulo() + "' --- <b>Genero</b> = '"
						+ canciones.get(i).getGenero() + "' --- <b>Descripcion</b> = '" + canciones.get(i).getDescripcion()
						+  "'<br>");
			}
		}

		out.println("<input type=\"hidden\" name=\"plang\" id=\"plang\" value=\"" + lang + "\">");
		out.println("<br><input type=\"submit\" value=\"Enviar\" onclick=\"getElementById(\'pfase\').value=\'23\'\"><br>");
		out.println("<input type=\"submit\" value=\"Atras\" onclick=\"getElementById(\'pfase\').value=\'21\'\"><br>");
		out.println("<input type=\"submit\" value=\"Inicio\" onclick=\"getElementById(\'pfase\').value=\'01\'\"><br>");

		out.println("</form>");
		out.println("<div class=\"footer\"><b>Candela Janeiro Catoira</b></div>");
		out.println("</body>");
		out.println("</html>");

	}

	public static ArrayList<Cancion> getC2Canciones(String lang) throws XPathExpressionException {

		XPath xpath = XPathFactory.newInstance().newXPath();
		ArrayList<Cancion> songs = new ArrayList<Cancion>();
		Document docAux = null;

		for (int i = 0; i < documentos.size(); i++) {


			docAux = documentos.get(i);
			NodeList Canciones =  (NodeList) xpath.evaluate("/Songs/Pais/Disco[contains(@langs, '"+lang+"')]/Cancion", docAux, XPathConstants.NODESET);
			NodeList Titulos = (NodeList) xpath.evaluate("/Songs/Pais/Disco[contains(@langs, '"+lang+"')]/Cancion/Titulo", docAux, XPathConstants.NODESET);
			NodeList Genero = (NodeList) xpath.evaluate("/Songs/Pais/Disco[contains(@langs, '"+lang+"')]/Cancion/Genero", docAux, XPathConstants.NODESET);
			NodeList IDC = (NodeList) xpath.evaluate("/Songs/Pais/Disco[contains(@langs, '"+lang+"')]/Cancion/@idc", docAux, XPathConstants.NODESET);

			for (int j = 0; j < Canciones.getLength(); j++) {
				String des ="";
				Cancion cancion = new Cancion();
				NodeList Descripciones = Canciones.item(j).getChildNodes();
				for (int k = 0; k < Descripciones.getLength(); k++) {
					if (Descripciones.item(k).getNodeName().equals("#text") && !Descripciones.item(k).getTextContent().trim().isEmpty()) {
						des = Descripciones.item(k).getTextContent().trim();
					}
				}
				cancion.setTitulo(Titulos.item(j).getTextContent());
				cancion.setGenero(Genero.item(j).getTextContent());
				cancion.setIdc(IDC.item(j).getTextContent().trim());
				cancion.setDescripcion(des);
				if(!songs.contains(cancion)) songs.add(cancion);
			}

			NodeList cancionPais =  (NodeList) xpath.evaluate("/Songs/Pais[contains(@lang, '"+lang+"')]/Disco[not(@langs)]/Cancion", docAux, XPathConstants.NODESET);
			NodeList titulosPais = (NodeList) xpath.evaluate("/Songs/Pais[contains(@lang, '"+lang+"')]/Disco[not(@langs)]/Cancion/Titulo", docAux, XPathConstants.NODESET);
			NodeList generoPais = (NodeList) xpath.evaluate("/Songs/Pais[contains(@lang, '"+lang+"')]/Disco[not(@langs)]/Cancion/Genero", docAux, XPathConstants.NODESET);
			NodeList IDCPais = (NodeList) xpath.evaluate("/Songs/Pais[contains(@lang, '"+lang+"')]/Disco[not(@langs)]/Cancion/@idc", docAux, XPathConstants.NODESET);

			for (int j = 0; j < cancionPais.getLength(); j++) {
				String des ="";
				Cancion cancion = new Cancion();
				NodeList Descripciones = cancionPais.item(j).getChildNodes();
				for (int k = 0; k < Descripciones.getLength(); k++) {
					if (Descripciones.item(k).getNodeName().equals("#text") && !Descripciones.item(k).getTextContent().trim().isEmpty()) {
						des = Descripciones.item(k).getTextContent().trim();
					}

				}
				cancion.setTitulo(titulosPais.item(j).getTextContent());
				cancion.setGenero(generoPais.item(j).getTextContent());
				cancion.setIdc(IDCPais.item(j).getTextContent());
				cancion.setDescripcion(des);
				if(!songs.contains(cancion)) songs.add(cancion);
			}

		}




		Collections.sort(songs);
		return songs;
	}

	public static void p23(HttpServletRequest request, HttpServletResponse response, String pfase)
			throws IOException, XPathExpressionException {

		String password = request.getParameter("p");
		PrintWriter out = response.getWriter();

		String plang = request.getParameter("plang");
		String pgen = request.getParameter("pgen");

		interpretes = getC2Interpretes(plang, pgen);


		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		out.println("<!DOCTYPE html>");
		out.println("<head>");
		out.println("<meta http=\"Content-type\" content=\"text/html\" accept-charset=\"UTF-8\"/>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\""+css+"\"/>");
		out.println("<title>Servicio de consulta de información musical.</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<br><h1>Servicio de consulta de información musical</h1><br>");
		out.println("<form method=GET accept-charset=\"UTF-8\" action='?pfase=23'>");



		out.println("<input type=\"hidden\" name=\"p\" id=\"p\" value=\"" + password + "\">");
		out.println("<input type=\"hidden\" name=\"pfase\" id=\"pfase\" value=\"" + pfase + "\">");


		out.println("<h4>Consulta 2. Fase 23: interpretes de canciones en " + plang + " y con el g�nero: " + pgen + "</h4><br>");

		for (int i=interpretes.size()-1 ; i>=0 ; i--) {

			if (i == 0)
				out.println("<input type=\'radio\' name=\'pint\' value=\'" + interpretes.get(i) + "\' checked>" + (interpretes.size() -i)+ ".- " + interpretes.get(i) + "<br>");
			else out.println("<input type=\'radio\' name=\'pint\' value=\'" + interpretes.get(i) + "\' >" + (interpretes.size()-i )+ ".- " + interpretes.get(i) + "<br>");
		}

		out.println("<input type=\"hidden\" name=\"plang\" id=\"plang\" value=\"" + plang + "\">");
		out.println("<input type=\"hidden\" name=\"pgen\" id=\"pgen\" value=\"" + pgen + "\">");
		out.println("<br><input type=\"submit\" value=\"Enviar\" onclick=\"getElementById(\'pfase\').value=\'24\'\"><br>");
		out.println("<input type=\"submit\" value=\"Atras\" onclick=\"getElementById(\'pfase\').value=\'22\'\"><br>");
		out.println("<input type=\"submit\" value=\"Inicio\" onclick=\"getElementById(\'pfase\').value=\'01\'\"><br>");

		out.println("</form>");
		out.println("<div class=\"footer\"><b>Candela Janeiro Catoira</b></div>");
		out.println("</body>");
		out.println("</html>");

	}
	public static ArrayList<String> getC2Interpretes(String lang, String gen) throws XPathExpressionException {

		XPath xpath = XPathFactory.newInstance().newXPath();
		ArrayList<String> interpretes = new ArrayList<String>();
		Document docAux = null;


		for (int i = 0; i < documentos.size(); i++) {

			docAux = documentos.get(i);
			NodeList interprete = (NodeList) xpath.evaluate("/Songs/Pais/Disco[contains(@langs, '"+lang+"') and ./Cancion/Genero ='" + gen + "']/Interprete", docAux, XPathConstants.NODESET);

			for (int j = 0; j < interprete.getLength(); j++) {
				if(!interpretes.contains(interprete.item(j).getTextContent())) interpretes.add(interprete.item(j).getTextContent());
			}
			
			NodeList interpretePais = (NodeList) xpath.evaluate("/Songs/Pais[contains(@lang, '"+lang+"')]/Disco[not(@langs) and ./Cancion/Genero ='" + gen + "']/Interprete", docAux, XPathConstants.NODESET);

			for (int j = 0; j < interpretePais.getLength(); j++) {
				if(!interpretes.contains(interpretePais.item(j).getTextContent())) interpretes.add(interpretePais.item(j).getTextContent());
			}
			
		}


		Collections.sort(interpretes);

		return interpretes;
	}

	public static void p24(HttpServletRequest request, HttpServletResponse response, String pfase)
			throws IOException, XPathExpressionException {

		String password = request.getParameter("p");
		PrintWriter out = response.getWriter();

		String plang = request.getParameter("plang");
		String pgen = request.getParameter("pgen");
		String pint = request.getParameter("pint");


		ArrayList<Disco> resultado = new ArrayList<Disco>();
		resultado = getC2Resultado(plang, pgen, pint);


		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		out.println("<!DOCTYPE html>");
		out.println("<head>");
		out.println("<meta http=\"Content-type\" content=\"text/html\" accept-charset=\"UTF-8\"/>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\""+css+"\"/>");
		out.println("<title>Servicio de consulta de información musical.</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<br><h1>Servicio de consulta de información musical</h1><br>");
		out.println("<form method=GET accept-charset=\"UTF-8\" action='?pfase=24'>");



		out.println("<input type=\"hidden\" name=\"p\" id=\"p\" value=\"" + password + "\">");
		out.println("<input type=\"hidden\" name=\"pfase\" id=\"pfase\" value=\"" + pfase + "\">");


		out.println("<h4>Consulta 2. Fase 24: discos de canciones en " + plang + ", con el g�nero: " + pgen + " y con interprete '" + pint + "' </h4><br>");

		out.println("<h5>Estos son los discos:</h5><br>");

		for (int i = 0; i < resultado.size(); i++) {

			out.println("<li>" + "<b>Titulo</b> = '" + resultado.get(i).getTitulo()
					+ "' --- <b>IDD</b> = '" + resultado.get(i).getIdd()
					+ "' --- <b>Idiomas</b> = '" + resultado.get(i).getLangs() + "'</li>");
			out.print("<br>");
		}

		out.println("<input type=\"hidden\" name=\"plang\" id=\"plang\" value=\"" + plang + "\">");
		out.println("<input type=\"hidden\" name=\"pgen\" id=\"pgen\" value=\"" + pgen + "\">");

		out.println("<input type=\"submit\" value=\"Atras\" onclick=\"getElementById(\'pfase\').value=\'23\'\"><br>");
		out.println("<input type=\"submit\" value=\"Inicio\" onclick=\"getElementById(\'pfase\').value=\'01\'\"><br>");

		out.println("</form>");
		out.println("<div class=\"footer\"><b>Candela Janeiro Catoira</b></div>");
		out.println("</body>");
		out.println("</html>");

	}
	public static ArrayList<Disco> getC2Resultado(String lang, String gen, String inter) throws XPathExpressionException {

		XPath xpath = XPathFactory.newInstance().newXPath();
		ArrayList<Disco> discos = new ArrayList<Disco>();
		Document docAux = null;



		for (int i = 0; i < documentos.size(); i++) {

			docAux = documentos.get(i);
			NodeList disco = (NodeList) xpath.evaluate("/Songs/Pais/Disco[contains(@langs, '"+lang+"') and ./Cancion/Genero ='" + gen + "' and ./Interprete ='" + inter + "']", docAux, XPathConstants.NODESET);
			NodeList titulo = (NodeList) xpath.evaluate("/Songs/Pais/Disco[contains(@langs, '"+lang+"') and ./Cancion/Genero ='" + gen + "' and ./Interprete ='" + inter + "']/Titulo", docAux, XPathConstants.NODESET);
			NodeList iDD = (NodeList) xpath.evaluate("/Songs/Pais/Disco[contains(@langs, '"+lang+"') and ./Cancion/Genero ='" + gen + "' and ./Interprete ='" + inter + "']/@idd", docAux, XPathConstants.NODESET);
			NodeList idioma = (NodeList) xpath.evaluate("/Songs/Pais/Disco[contains(@langs, '"+lang+"') and ./Cancion/Genero ='" + gen + "' and ./Interprete ='" + inter + "']/@langs", docAux, XPathConstants.NODESET);

			for (int j = 0; j < disco.getLength(); j++) {
				Disco discoAux = new Disco();
				discoAux.setTitulo(titulo.item(j).getTextContent());
				discoAux.setIdd(iDD.item(j).getTextContent());
				discoAux.setLangs(idioma.item(j).getTextContent());

				if(!discos.contains(discoAux)) {
					discos.add(discoAux);
				}
			}


			NodeList discoPais = (NodeList) xpath.evaluate("/Songs/Pais[contains(@lang, '"+lang+"')]/Disco[not(@langs) and ./Cancion/Genero ='" + gen + "' and ./Interprete ='" + inter + "']", docAux, XPathConstants.NODESET);
			NodeList tituloPais = (NodeList) xpath.evaluate("/Songs/Pais[contains(@lang, '"+lang+"')]/Disco[not(@langs) and ./Cancion/Genero ='" + gen + "' and ./Interprete ='" + inter + "']/Titulo", docAux, XPathConstants.NODESET);
			NodeList iDDPais = (NodeList) xpath.evaluate("/Songs/Pais[contains(@lang, '"+lang+"')]/Disco[not(@langs) and ./Cancion/Genero ='" + gen + "' and ./Interprete ='" + inter + "']/@idd", docAux, XPathConstants.NODESET);
			NodeList idiomaPais = (NodeList) xpath.evaluate("/Songs/Pais[contains(@lang, '"+lang+"')]/@lang", docAux, XPathConstants.NODESET);

			for (int j = 0; j < discoPais.getLength(); j++) {
				Disco discoAux = new Disco();
				discoAux.setTitulo(tituloPais.item(j).getTextContent());
				discoAux.setIdd(iDDPais.item(j).getTextContent());
				discoAux.setLangs(idiomaPais.item(j).getTextContent());
				
				if(!discos.contains(discoAux)) {
					discos.add(discoAux);
				}		
			}
			
		}


		Collections.sort(discos);

		return discos;
	}


	public static void modoAuto(HttpServletRequest request, HttpServletResponse response, String pfase)
			throws IOException, XPathExpressionException {

		PrintWriter out = response.getWriter();
		response.setContentType("text/xml");

		switch (pfase) {

		case "01":

			pauto01(request, response);
			break;

		case "02":

			pauto02(request, response);
			break;

		case "21":

			pauto21(request, response);
			break;

		case "22":

			if (request.getParameter("plang") == null) {
				out.println("<?xml version=\'1.0\' encoding=\'utf-8\'?>");
				out.println("<wrongRequest>no param:plang</wrongRequest>");
			} else {
				pauto22(request, response);
			}
			break;

		case "23":

			if (request.getParameter("plang") == null) {
				out.println("<?xml version=\'1.0\' encoding=\'utf-8\'?>");
				out.println("<wrongRequest>no param:plang</wrongRequest>");
			} else if (request.getParameter("pgen") == null) {
				out.println("<?xml version=\'1.0\' encoding=\'utf-8\'?>");
				out.println("<wrongRequest>no param:pgen</wrongRequest>");
			} else {
				pauto23(request, response);
			}
			break;

		case "24":

			if (request.getParameter("plang") == null) {
				out.println("<?xml version=\'1.0\' encoding=\'utf-8\'?>");
				out.println("<wrongRequest>no param:plang</wrongRequest>");
			} else if (request.getParameter("pgen") == null) {
				out.println("<?xml version=\'1.0\' encoding=\'utf-8\'?>");
				out.println("<wrongRequest>no param:pgen</wrongRequest>");
			} else if (request.getParameter("pint") == null) {
				out.println("<?xml version=\'1.0\' encoding=\'utf-8\'?>");
				out.println("<wrongRequest>no param:pint</wrongRequest>");

			} else {
				pauto24(request, response);
			}
			break;
		}
	}

	public static void pauto01(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		out.println("<?xml version='1.0' encoding='UTF-8' ?>\n");
		out.println("<service>");
		out.println("<status>OK</status>");
		out.println("</service>");

	}

	
	public static void pauto02(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		out.println("<?xml version='1.0' encoding='UTF-8' ?>\n");
		out.println("<errores>\n");
		out.println("<warnings>\n");
		

		SortedSet<String> wkeys = new TreeSet<>(awarning.keySet());
		for (String key : wkeys) {
			out.println("\t<warning>\n");
			ArrayList<String> value = awarning.get(key);
			out.println("<file>" + key + "</file>");
			out.println("<cause>" + value + "</cause>");
			out.println("</warning>");
		}



		out.println("</warnings>");
		out.println("<errors>");
	


		SortedSet<String> ekeys = new TreeSet<>(aerror.keySet());
		for (String key : ekeys) {
			out.println("\t<error>\n");
			ArrayList<String> value = aerror.get(key);
			out.println("<file>" + key + "</file>");
			out.println("<cause>" + value + "</cause>");
			out.println("</error>");
		}
	
		out.println("</errors>");
		out.println("<fatalErrors>");
	

		SortedSet<String> fkeys = new TreeSet<>(aferror.keySet());
		for (String key : fkeys) {
			out.println("\t<fatalerror>\n");
			ArrayList<String> value = aferror.get(key);
			out.println("<file>" + key + "</file>");
			out.println("<cause>" + value + "</cause>");
			out.println("</fatalerror>");
		}

	
		out.println("</fatalErrors>");
		out.println("</errores>");

	}



	public static void pauto21(HttpServletRequest request, HttpServletResponse response)
			throws XPathExpressionException, IOException {

		PrintWriter out = response.getWriter();

		out.println("<?xml version='1.0' encoding='UTF-8' ?>\n");
		out.println("<langs>\n");

		langs = getC2Langs();

		for (int i = 0; i < langs.size(); i++) {
			out.println("\t <lang>" + langs.get(i) + "</lang>\n");
		}

		out.println("</langs>");

	}
	

	public static void pauto22(HttpServletRequest request, HttpServletResponse response)
			throws IOException, XPathExpressionException {

		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		String  lang = request.getParameter("plang");

		canciones = getC2Canciones(lang);


		out.println("<?xml version='1.0' encoding='utf-8'?>");
		out.println("<canciones>");

		for (int i = 0; i < canciones.size(); i++) {


			out.println("<cancion descripcion='" + canciones.get(i).getDescripcion() + "' genero='" + canciones.get(i).getGenero() + "' idc='" + canciones.get(i).getIdc() + "'>" + canciones.get(i).getTitulo() + "</cancion>");

		}

		out.println("</canciones>");

	}

	

	public static void pauto23(HttpServletRequest request, HttpServletResponse response)
			throws IOException, XPathExpressionException {

		PrintWriter out = response.getWriter();

		String plang = request.getParameter("plang");
		String pgen = request.getParameter("pgen");

		interpretes = getC2Interpretes(plang, pgen);

		out.println("<?xml version='1.0' encoding='UTF-8' ?>");
		out.println("<interpretes>");

		for (int i = interpretes.size()-1; i >=0 ; i--) {

			out.println("<interprete" + ">" + interpretes.get(i)  + "</interprete>");

		}

		out.println("</interpretes>");

	}
	

	public static void pauto24(HttpServletRequest request, HttpServletResponse response)
			throws IOException, XPathExpressionException {

		PrintWriter out = response.getWriter();

		String plang = request.getParameter("plang");
		String pgen = request.getParameter("pgen");
		String pint = request.getParameter("pint");


		ArrayList<Disco> resultado = new ArrayList<Disco>();
		resultado = getC2Resultado(plang, pgen, pint);

		out.println("<?xml version='1.0' encoding='UTF-8' ?>");
		out.println("<discos>");

		for (int i = 0; i < resultado.size(); i++) {

			out.println("<disco langs='" + resultado.get(i).getLangs() + "' idd='" + resultado.get(i).getIdd()  + "'>" + resultado.get(i).getTitulo() + "</disco>");

		}

		out.println("</discos>");
	}
}

class XML_ErrorHandler extends DefaultHandler {

	public static boolean error;
	public static boolean warning;
	public static boolean fatalError;
	public static String message;

	public XML_ErrorHandler() {
		error = false;
		warning = false;
		fatalError = false;
		message = null;
	}
	public void warning(SAXParseException spe) throws SAXException {
		warning = true;
		message = spe.toString();
		throw new SAXException();
	}
	public void error(SAXParseException spe) throws SAXException {
		error = true;
		message = spe.toString();
		throw new SAXException();
	}
	public void fatalError(SAXParseException spe) throws SAXException {
		fatalError = true;
		message = spe.toString();
		throw new SAXException();
	}
	public static boolean hasWarning() {
		return warning;
	}
	public static boolean hasError() {
		return error;
	}
	public static boolean hasfatalError() {
		return fatalError;
	}
	public static String getMessage() {
		return message;
	}
	public static void clear() {
		warning = false;
		error = false;
		fatalError = false;
		message = null;
	}
}
