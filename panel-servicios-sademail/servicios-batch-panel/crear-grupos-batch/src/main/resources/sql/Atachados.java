package cl.uandes.panelv3.comunes.azure.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Permite definir los archivos que se adjuntaran a un correo
 * 
 * @author fernando
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Atachados implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -183696460330011240L;
	@JsonProperty("file-name")
	String fileName;
	@JsonProperty("content-type")
	String contentType;

	public Atachados() {
		super();
	}

	/**
	 * @return path del archivo
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return nombre del archivo
	 */
	@JsonIgnore
	public String getName() {
		if (fileName != null) {
			java.io.File file = new java.io.File(fileName);
			return file.getName();
		} else
			return null;
	}
	/**
	 * @param fileName: path completo
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return el content-type del archivo (futuras ampliaciones)
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType (futuras ampliaciones)
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
