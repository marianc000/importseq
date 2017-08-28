package auxiliary;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the test package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _NumberConversion_QNAME = new QName("http://mutalyzer.nl/2.0/services", "numberConversion");
    private final static QName _StringArray_QNAME = new QName("http://mutalyzer.nl/2.0/services", "stringArray");
    private final static QName _NumberConversionResponse_QNAME = new QName("http://mutalyzer.nl/2.0/services", "numberConversionResponse");

    private final static QName _NumberConversionGene_QNAME = new QName("http://mutalyzer.nl/2.0/services", "gene");

    private final static QName _NumberConversionResponseNumberConversionResult_QNAME = new QName("http://mutalyzer.nl/2.0/services", "numberConversionResult");

    public NumberConversion createNumberConversion() {
        return new NumberConversion();
    }

    /**
     * Create an instance of {@link NumberConversionResponse }
     *
     */
    public NumberConversionResponse createNumberConversionResponse() {
        return new NumberConversionResponse();
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link NumberConversion }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://mutalyzer.nl/2.0/services", name = "numberConversion")
    public JAXBElement<NumberConversion> createNumberConversion(NumberConversion value) {
        return new JAXBElement<NumberConversion>(_NumberConversion_QNAME, NumberConversion.class, null, value);
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link NumberConversionResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://mutalyzer.nl/2.0/services", name = "numberConversionResponse")
    public JAXBElement<NumberConversionResponse> createNumberConversionResponse(NumberConversionResponse value) {
        return new JAXBElement<NumberConversionResponse>(_NumberConversionResponse_QNAME, NumberConversionResponse.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringArray }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://mutalyzer.nl/2.0/services", name = "stringArray")
    public JAXBElement<StringArray> createStringArray(StringArray value) {
        return new JAXBElement<StringArray>(_StringArray_QNAME, StringArray.class, null, value);
    }
    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://mutalyzer.nl/2.0/services", name = "gene", scope = NumberConversion.class)
    public JAXBElement<String> createNumberConversionGene(String value) {
        return new JAXBElement<String>(_NumberConversionGene_QNAME, String.class, NumberConversion.class, value);
    }

    /**
     * Create an instance of
     * {@link JAXBElement }{@code <}{@link StringArray }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://mutalyzer.nl/2.0/services", name = "numberConversionResult", scope = NumberConversionResponse.class)
    public JAXBElement<StringArray> createNumberConversionResponseNumberConversionResult(StringArray value) {
        return new JAXBElement<StringArray>(_NumberConversionResponseNumberConversionResult_QNAME, StringArray.class, NumberConversionResponse.class, value);
    }

}
