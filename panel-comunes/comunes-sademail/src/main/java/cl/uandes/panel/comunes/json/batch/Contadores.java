package cl.uandes.panel.comunes.json.batch;

import java.io.Serializable;

public interface Contadores extends Serializable {

	public Integer getCountProcesados();
	
	public Integer getCountErrores();
	
	public Integer getCount1();
	
	public Integer getCount2();
	
	public String toString();
}
