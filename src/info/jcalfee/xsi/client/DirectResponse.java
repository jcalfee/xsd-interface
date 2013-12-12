package info.jcalfee.xsi.client;

import java.io.Serializable;
import java.util.HashMap;

public class DirectResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    public boolean success = true;

    public int total = 1;

    public String message;

    public HashMap<String, String> data = new HashMap<String, String>();

    public boolean isSuccess() {
        return success;
    }

    public int getTotal() {
        return total;
    }

    public String getMessage() {
        return message;
    }

    /**
     * @return <b>null</b> if the data map is empty. This is done for the
     *         benefit of the JSP expression language. The expression language
     *         is dumb (in this case) because it can't call size() because was
     *         not called getSize().
     */
    public HashMap<String, String> getData() {
        return data.isEmpty() ? null : data;
    }
}