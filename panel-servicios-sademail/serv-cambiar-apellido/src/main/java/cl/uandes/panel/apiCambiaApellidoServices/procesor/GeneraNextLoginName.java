package cl.uandes.panel.apiCambiaApellidoServices.procesor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * @author fernando
 *
 * Recupera del body el resultado de la qry:
 * sql:select login_name from (select login_name from mi_cuentas_gmail where login_name like ':#${body}%' order by login_name desc) where rownum &lt; 2?dataSource=#bannerDataSource
 * 
 * Si no viene nada --> el nombre sugerido no esta repetido, se deja el que viene en new_login_name
 * caso contrario ya existe uno similar y si al final viene un numero, corresponde al numero de secuencia ultimo que esta ocupado, 
 * debe intentar con el siguiente numero de secuencia y dejarlo en header.new_login_name
 * debe colocar header.foundName = 1
 * 
 * Deja el login_name sugerido en header.new_login_name
 * Si esta compuesto por una base y una secuencia deja la base en header.new_login_name_base
 *  y la secuencia en header.new_login_name_seq
 */
public class GeneraNextLoginName implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>resultados = (List<Map<String, Object>>)exchange.getIn().getBody();
		if (resultados == null || resultados.isEmpty()) {
			return;
		}
		
		// hay que buscar un new_login_name
		Map<String, Object> mapDatos = resultados.get(0);
		exchange.getIn().setHeaders(getNewLoginName((String)mapDatos.get("login_name")));
	}

	private Map<String,Object> getNewLoginName(String loginName) {
		// ver si termina con un numero
		StringBuilder sb = new StringBuilder();
		char chars[] = new char[loginName.length()];
		loginName.getChars(0,loginName.length(),chars,0);
		String base = "";
		int i=chars.length - 1;
		for (; i > 0; i--) {
			char cc = chars[i];
			if (cc >= '0' && cc <= '9')
				sb.append(cc);
			else {
				base = loginName.substring(0, i+1);
				break;
			}
		}
		
		Integer valor = null;
		if (sb.length() > 0) {
			String seq = sb.reverse().toString();
			valor = Integer.valueOf(seq);
			loginName = String.format("%s%d", base, ++valor);
		}
		Map<String,Object> resultado = new HashMap<String,Object>();
		resultado.put("new_login_name", loginName);
		if (valor != null) {
			resultado.put("new_login_name_base", base);
			resultado.put("new_login_name_seq", valor);
		}
		return resultado;
	}
}
