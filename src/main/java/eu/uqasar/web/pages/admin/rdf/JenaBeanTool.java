package eu.uqasar.web.pages.admin.rdf;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thewebsemantic.Bean2RDF;
import thewebsemantic.binding.Jenabean;

/**
 * This class provides transformation from instance of objects to RDF standard.
 * The class uses JenaBean tool.
 *
 * @author Dominik Šmíd
 */
public class JenaBeanTool implements JenaBean {

    private static final Logger loggerJenaBean = Logger.getLogger(JenaBeanTool.class);
    /* InputStream is used as input for OwlApi. */
    private InputStream outputJenaBean;
    /* List of instances of objects. */
    private List listOfObjects = new ArrayList();
    private final Model model;
    private final Bean2RDF writer;

    /**
     * Constructor JenaBean. Input is a List of instances of objects. Instances are
     * transformed into the RDF. The RDF is saved to the InputStream.
     *
     * @param listOfObjects The List contains instance of objects designated for transformation.
     */
    private JenaBeanTool(List listOfObjects) throws IOException {
        loggerJenaBean.info("JenaBean - start");
        this.listOfObjects = listOfObjects;
        model = ModelFactory.createDefaultModel();
        Jenabean.instance().bind(model);
        writer = new Bean2RDF(model);

        //isEmpty();
        convertToRdf();
        rdfToOutput();
    }

    public InputStream getOutputFromJenaBean() {
        return outputJenaBean;
    }

    /**
     * This method control if List contains some instances.
     *
     */
    public void isEmpty() {
        if (listOfObjects.isEmpty()) {
            loggerJenaBean.info("ArrayList doesn't have any objects!");
        } else {
            if (listOfObjects.size() == 1) {
                loggerJenaBean.info("ArrayList includes " + listOfObjects.size() + " object.");
            } else {
                loggerJenaBean.info("ArrayList includes " + listOfObjects.size() + " objects.");
            }
        }
    }

    /**
     * Transformation from instances to the RDF.
     *
     */
    private void convertToRdf() {
        loggerJenaBean.info("Transform objects to beans.");
        for (Object listOfObject : listOfObjects) {
            writer.save(listOfObject);
        }
    }

    /**
     * Save to the InputStream.
     *
     */
    private void rdfToOutput() throws IOException {
        ByteArrayOutputStream byos = new ByteArrayOutputStream();
        loggerJenaBean.info("Saving beans to InputStream.");
        model.write(byos);
        byte[] buf = byos.toByteArray();
        outputJenaBean = new ByteArrayInputStream(buf);
        byos.flush();
        byos.close();
        outputJenaBean.close();
    }
}
