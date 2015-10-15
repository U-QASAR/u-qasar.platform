package eu.uqasar.web.pages.admin.rdf;

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

    static Logger loggerJenaBean = Logger.getLogger(JenaBeanTool.class);
    /* InputStream is used as input for OwlApi. */
    private InputStream outputJenaBean;
    /* List of instances of objects. */
    private List listOfObjects = new ArrayList();
    public Model model;
    public Bean2RDF writer;

    /**
     * Constructor JenaBean. Input is a List of instances of objects. Instances are
     * transformed into the RDF. The RDF is saved to the InputStream.
     *
     * @param listOfObjects The List contains instance of objects designated for transformation.
     */
    public JenaBeanTool(List listOfObjects) throws IOException {
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
        if (listOfObjects.isEmpty() == true) {
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
     * @param list List of instances.
     */
    public void convertToRdf() {
        loggerJenaBean.info("Transform objects to beans.");
        for (int i = 0; i < listOfObjects.size(); i++) {
            writer.save(listOfObjects.get(i));
        }
    }

    /**
     * Save to the InputStream.
     *
     */
    public void rdfToOutput() throws IOException {
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
