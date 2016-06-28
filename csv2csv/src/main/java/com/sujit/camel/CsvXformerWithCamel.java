/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sujit.camel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.apache.camel.impl.DefaultCamelContext;


public class CsvXformerWithCamel {

    public static void main(String args[]) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
        	ExecutorService threadPool = Executors.newFixedThreadPool(50);
        	public void configure() {
        		CsvXformProcessor processor = new CsvXformProcessor();
        		//CsvDataFormat csv = new CsvDataFormat();
        	    //csv.setDelimiter('\t'); // Tabs
        	    //csv.setQuoteDisabled(true); // Otherwise single quotes will be doubled.
        		errorHandler(deadLetterChannel("log:dead?level=ERROR"));
        	    from("file://data/inbox?noop=true&delay=15m")
        	    	.split(body().tokenize("\n")).streaming()
        	    	.executorService(threadPool)
        	    	.unmarshal().csv()
        	        //.convertBodyTo(List.class)
        	        .process(processor)
        	        .marshal().csv().to("file://data/outbox?fileName=out.csv&fileExist=Append")
        	        .log("done.").end();
        	}
        });

        // start the route and let it do its work
        context.start();
        Thread.sleep(10*60000);

        // stop the CamelContext
        context.stop();
    }
}
