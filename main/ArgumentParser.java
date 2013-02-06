/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package main;

import java.util.*;

public class ArgumentParser {
    
    private Vector params = new Vector();
    private Hashtable options = new Hashtable();
    private int paramIndex = 0;

	public ArgumentParser(String[] args) {
		
		for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") ) {
                int loc = args[i].indexOf("=");
                String key = (loc > 0) ? args[i].substring(1, loc) : args[i].substring(1);
                String value = (loc > 0) ? args[i].substring(loc+1) : "";
                options.put(key.toLowerCase(), value);
            }
            else {
                params.addElement(args[i]);
            }
        }
    }

    public boolean hasOption(String opt) {
        return options.containsKey(opt.toLowerCase());
    }

    public String getOption(String opt) {
        return (String) options.get(opt.toLowerCase());
    }

	public boolean hasNextParam() {
		if (paramIndex < params.size()) {
			return true;
		}
		return false;
	}

    public String nextParam() {
        if (paramIndex < params.size()) {
            return (String) params.elementAt(paramIndex++);
        }
        return null;
    }
}
