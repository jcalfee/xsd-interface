package info.jcalfee.xsi.server;

import info.jcalfee.gae.ds.Validation;
import info.jcalfee.xsi.client.XmlSchema.Attribute;
import info.jcalfee.xsi.client.XmlSchema.Element;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerValidation extends Validation {

    private static Logger log = Logger.getLogger(Validation.class.getName());

    static void log(Element element, Attribute attribute, String message) {
        log.log(Level.SEVERE, message + " [element=" + element.name
            + ",attribute=" + attribute.name);
    }
}
