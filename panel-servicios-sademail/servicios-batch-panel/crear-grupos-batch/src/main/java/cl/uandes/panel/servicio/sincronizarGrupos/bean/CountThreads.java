package cl.uandes.panel.servicio.sincronizarGrupos.bean;

import java.io.Serializable;

public class CountThreads implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 82774601056395536L;
	private Integer counter = 0;
	
	public synchronized Integer incCounter() {
		counter++;
		return counter;
	}
	
	public synchronized Integer decCounter() {
		counter--;
		return counter;
	}
	
	public synchronized Integer getCounter() {
		return counter;
	}
}
