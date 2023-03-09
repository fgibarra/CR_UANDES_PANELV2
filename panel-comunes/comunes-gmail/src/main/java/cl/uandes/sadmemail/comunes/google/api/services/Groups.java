package cl.uandes.sadmemail.comunes.google.api.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Groups implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4840198351846740392L;
	@JsonProperty("etag")
	private String etag;
	@JsonProperty("listaGrupos")
	private List<Group> listaGrupos;
	@JsonProperty("kind")
	private String kind;
	@JsonProperty("nextPageToken")
	private String nextPageToken;

	@JsonCreator
	public Groups(
			@JsonProperty("etag")String etag, 
			@JsonProperty("listaGrupos")List<Group> listaGrupos, 
			@JsonProperty("kind")String kind, 
			@JsonProperty("nextPageToken")String nextPageToken) {
		super();
		this.etag = etag;
		this.listaGrupos = listaGrupos;
		this.kind = kind;
		this.nextPageToken = nextPageToken;
	}

	public Groups(com.google.api.services.admin.directory.model.Groups g_groups) {
		List<Group> lista = new ArrayList<Group>();
		this.etag = g_groups.getEtag();
		this.listaGrupos = lista;
		this.kind = g_groups.getKind();
		this.nextPageToken = g_groups.getNextPageToken();
		for (com.google.api.services.admin.directory.model.Group g_group : g_groups.getGroups()) {
			lista.add(new Group(g_group.getDescription(), g_group.getEmail(), g_group.getEtag(),
					g_group.getId(), g_group.getKind(), g_group.getName(), g_group.getAliases(), 
					g_group.getDirectMembersCount()));
		}
	}

	@Override
	@JsonIgnore
	public String toString() {
		try {
			return JSonUtilities.getInstance().java2json(this);
		} catch (Exception e) {
			return String.format("No pudo serializar %s",this.getClass().getSimpleName());
		}
	}

	public String getEtag() {
		return etag;
	}

	public List<Group> getListaGrupos() {
		return listaGrupos;
	}

	public String getKind() {
		return kind;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

}
