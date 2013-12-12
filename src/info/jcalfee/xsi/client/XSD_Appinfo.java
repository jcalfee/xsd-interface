package info.jcalfee.xsi.client;

import info.jcalfee.xsi.client.XmlSchema.Entity;
import info.jcalfee.xsi.client.XmlSchema.Annotatable.Annotation;
import info.jcalfee.xsi.client.XmlSchema.Annotatable.Annotation.Appinfo;
import info.jcalfee.xsi.client.XmlSchema.Annotatable.Annotation.Appinfo.AnyNode;

public class XSD_Appinfo {

    boolean isReadonly = false;

    /**
     * Loads settings (Appinfo XML data) for the attribute. The settings under
     * the attribute takes priority over settings in attribute's type or
     * settings in the type's restriction.
     * 
     * @param attribute
     */
    public XSD_Appinfo(Entity entity) {
        if (entity == null)
            return;

        setAnnotation(entity.getType().getAnnotation());
        setAnnotation(entity.getAnnotation());
    }
    
    public void setAnnotation(Annotation annotation) {
        if (annotation == null)
            return;

        setAppinfo(annotation.appinfo);

    }

    public void setAppinfo(Appinfo appinfo) {
        if (appinfo == null)
            return;

        for (AnyNode node : appinfo.anyNodes) {
            if (node.qName.equals("readonly")) {
                isReadonly = true;
                continue;
            }
            // ...
        }
    }

    public boolean isReadonly() {
        return isReadonly;
    }
}
