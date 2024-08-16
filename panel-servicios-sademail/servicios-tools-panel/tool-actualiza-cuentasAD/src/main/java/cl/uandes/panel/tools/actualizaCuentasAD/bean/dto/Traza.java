package cl.uandes.panel.tools.actualizaCuentasAD.bean.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.json.tools.Servicios;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Traza implements Serializable {
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -7703134561535991493L;
	private String inputBD;
	private String inputAD;
	private Servicios servicios[];

	@JsonCreator
	public Traza(String inputBD) {
		super();
		this.inputBD = inputBD;
	}

	@Override
	@JsonIgnore
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return String.format("No pudo serializar %s", this.getClass().getSimpleName());
		}
	}

	public String getInputBD() {
		return inputBD;
	}

	public void setInputBD(String inputBD) {
		this.inputBD = inputBD;
	}

	public String getInputAD() {
		return inputAD;
	}

	public void setInputAD(String inputAD) {
		this.inputAD = inputAD;
	}

	public Servicios[] getServicios() {
		return servicios;
	}

	public void setServicios(Servicios[] servicios) {
		this.servicios = servicios;
	}

	public Servicios add(String metodo, String uri, String body) {
		List<Servicios> lista = null;
		if (servicios == null)  {
			lista = new ArrayList<Servicios>();
		} else {
			lista = new LinkedList<Servicios>(Arrays.asList(servicios));
		}
		Servicios s = new Servicios(metodo, uri, body);
		lista.add(s);
		servicios = lista.toArray(new Servicios[0]);
		return s;
	}

	public Object dump() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("{ \"inputBD\": %s, \"inputAD\": %s,", inputBD,inputAD));
		if (servicios != null) {
			sb.append(" \"servicios\": [\n");
			for (Servicios ser : servicios) {
				sb.append(String.format("\t%s\n", ser.toString()));
			}
			sb.append("]\n");
		} else
			sb.append("\n");
		return sb.toString();
	}

}
