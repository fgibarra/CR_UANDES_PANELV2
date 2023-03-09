package cl.uandes.sadmemail.comunes.google.api.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

public class Members implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1100473132531737074L;
	@JsonProperty("etag")
	private String etag;
	@JsonProperty("listaMembers")
	private List<Member> listaMembers;
	@JsonProperty("kind")
	private String kind;
	@JsonProperty("nextPageToken")
	private String nextPageToken;
	
	@JsonCreator
	public Members(
			@JsonProperty("etag")String etag, 
			@JsonProperty("listaMembers")List<Member> listaMembers, 
			@JsonProperty("kind")String kind, 
			@JsonProperty("nextPageToken")String nextPageToken) {
		super();
		this.etag = etag;
		this.listaMembers = listaMembers;
		this.kind = kind;
		this.nextPageToken = nextPageToken;
	}

	public Members(com.google.api.services.admin.directory.model.Members g_members) {
		List<Member> lista = new ArrayList<Member>();
		this.etag = g_members.getEtag();
		this.listaMembers = lista;
		this.kind = g_members.getKind();
		this.nextPageToken = g_members.getNextPageToken();
		for (com.google.api.services.admin.directory.model.Member g_member : g_members.getMembers()) {
			lista.add(new Member(g_member.getEtag(), g_member.getEmail(),
					g_member.getId(), g_member.getKind(), g_member.getRole(), g_member.getType()));
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
	public List<Member> getListaMembers() {
		return listaMembers;
	}
	public String getKind() {
		return kind;
	}
	public String getNextPageToken() {
		return nextPageToken;
	}

}
