package com.sujit.camel;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CsvXformProcessor implements Processor {

	public void process(Exchange msg) throws Exception {
		try{
		List<List<String>> data = (List<List<String>>) msg.getIn().getBody();
        for (List<String> line : data) {
            // Checks if column two contains text STANDARD 
            // and alters its value to DELUXE.
            if ("HAND WARMER UNION JACK".equals(line.get(2))) {
                System.out.println("Original:" + line);
                line.set(2, "My CHANGED ITEM TYPE");
                System.out.println("After: " + line);
            }
        }
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}

	}

}
