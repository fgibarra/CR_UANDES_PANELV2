package cl.uandes.panel.apiCambiaApellidoServices.procesor;

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
		String newLoginName = getNewLoginName((String)mapDatos.get("login_name"));
		exchange.getIn().setHeader("new_login_name", newLoginName);
	}

	private String getNewLoginName(String loginName) {
		// ver si termina con un numero
		StringBuilder sb = new StringBuilder();
		char chars[] = new char[loginName.length()];
		loginName.getChars(0,loginName.length(),chars,0);
		String base = "";
		for (int i=chars.length - 1; i < 0; i--) {
			char cc = chars[i];
			if (cc >= '0' && cc <= '9')
				sb.append(cc);
			else {
				base = loginName.substring(0, i);
				break;
			}
		}
		if (sb.length() > 0) {
			String seq = sb.reverse().toString();
			Integer valor = Integer.valueOf(seq);
			loginName = String.format("%s%d", base, ++valor);
		}
		return loginName;
	}

}
