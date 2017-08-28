package auxiliary;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

/**
 * This class  2.2.6-1b01 Generated
 * source version: 2.2
 *
 */
@WebServiceClient(name = "MutalyzerService", targetNamespace = "http://mutalyzer.nl/2.0/services", wsdlLocation = "https://mutalyzer.nl/services/?wsdl")
public class MutalyzerService extends Service {
 
    private final static QName MUTALYZERSERVICE_QNAME = new QName("http://mutalyzer.nl/2.0/services", "MutalyzerService");

    public MutalyzerService() {
        super(null, MUTALYZERSERVICE_QNAME);
    }

    /**
     *
     * @return returns Mutalyzer
     */
    @WebEndpoint(name = "Mutalyzer")
    public Mutalyzer getMutalyzer() {
        return super.getPort(new QName("http://mutalyzer.nl/2.0/services", "Mutalyzer"), Mutalyzer.class);
    }

}
