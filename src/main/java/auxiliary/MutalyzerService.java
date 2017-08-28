
package auxiliary;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.6-1b01 
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "MutalyzerService", targetNamespace = "http://mutalyzer.nl/2.0/services", wsdlLocation = "file:/C:/Users/mcaikovs/Documents/NetBeansProjects/SOAPMutalizer/src/.wsdl")
public class MutalyzerService
    extends Service
{

    private final static URL MUTALYZERSERVICE_WSDL_LOCATION;
    private final static WebServiceException MUTALYZERSERVICE_EXCEPTION;
    private final static QName MUTALYZERSERVICE_QNAME = new QName("http://mutalyzer.nl/2.0/services", "MutalyzerService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/C:/Users/mcaikovs/Documents/NetBeansProjects/SOAPMutalizer/src/.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        MUTALYZERSERVICE_WSDL_LOCATION = url;
        MUTALYZERSERVICE_EXCEPTION = e;
    }

    public MutalyzerService() {
        super(__getWsdlLocation(), MUTALYZERSERVICE_QNAME);
    }

    public MutalyzerService(WebServiceFeature... features) {
        super(__getWsdlLocation(), MUTALYZERSERVICE_QNAME, features);
    }

    public MutalyzerService(URL wsdlLocation) {
        super(wsdlLocation, MUTALYZERSERVICE_QNAME);
    }

    public MutalyzerService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, MUTALYZERSERVICE_QNAME, features);
    }

    public MutalyzerService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public MutalyzerService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns Mutalyzer
     */
    @WebEndpoint(name = "Mutalyzer")
    public Mutalyzer getMutalyzer() {
        return super.getPort(new QName("http://mutalyzer.nl/2.0/services", "Mutalyzer"), Mutalyzer.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns Mutalyzer
     */
    @WebEndpoint(name = "Mutalyzer")
    public Mutalyzer getMutalyzer(WebServiceFeature... features) {
        return super.getPort(new QName("http://mutalyzer.nl/2.0/services", "Mutalyzer"), Mutalyzer.class, features);
    }

    private static URL __getWsdlLocation() {
        if (MUTALYZERSERVICE_EXCEPTION!= null) {
            throw MUTALYZERSERVICE_EXCEPTION;
        }
        return MUTALYZERSERVICE_WSDL_LOCATION;
    }

}
