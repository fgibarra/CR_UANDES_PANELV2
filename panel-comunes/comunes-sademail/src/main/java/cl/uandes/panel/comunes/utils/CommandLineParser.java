package cl.uandes.panel.comunes.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Permite parsear parametros contenidos en un String del tipo --key valor
 * @author fernando
 *
 */
public class CommandLineParser {

    private Map<String,String> argMap;

    /**
     * Initializes the command line parser by parsing the command line
     * args using simple rules.
     * <p>
     * The arguments are parsed into keys and values and are saved into
     * a HashMap.  Any argument that begins with a '--' or '-' is assumed
     * to be a key.  If the following argument doesn't have a '--'/'-' it
     * is assumed to be a value of the preceding argument.
     */
    public CommandLineParser(String[] arg) {
      argMap = new HashMap<String,String>();
      for (int i = 0; i < arg.length ; i++) {
        String key;
        if (arg[i].startsWith("--")) {
          key = arg[i].substring(2);
        } else if(arg[i].startsWith("-")) {
          key = arg[i].substring(1);
        } else {
          argMap.put(arg[i], null);
          continue;
        }
        String value;
        int index = key.indexOf('=');
        if (index == -1) {
          if (((i+1) < arg.length) &&
              (arg[i+1].charAt(0) != '-')) {
            argMap.put(key, arg[i+1]);
            i++;
          } else {
            argMap.put(key, null);
          }
        } else {
          value = key.substring(index+1);
          key = key.substring(0, index);
          argMap.put(key, value);
        }
      }
    }

    /**
     * Returns the value of the first key found in the map.
     */
    public String getValue(String ... keys) {
      for(int key_i = 0; key_i < keys.length; key_i++) {
        if(argMap.get(keys[key_i]) != null) {
          return argMap.get(keys[key_i]);
        }
      }
      return null;
    }

    /**
     * Returns true if any of the given keys are present in the map.
     */
    public boolean containsKey(String ... keys) {
      Set<String> keySet = argMap.keySet();
      for (Iterator<String> keysIter = keySet.iterator(); keysIter.hasNext();) {
        String key = keysIter.next();
        for (int key_i = 0; key_i < keys.length; key_i++) {
          if (key.equals(keys[key_i])) {
            return true;
          }
        }
      }
      return false;
    }
}
