/*@XmlSchema(
    namespace = "urn:WSLDAPUANDES",
    elementFormDefault = XmlNsForm.QUALIFIED,
    xmlns = {
    		@XmlNs(prefix="con", namespaceURI="urn:consulta"),
    		@XmlNs(prefix="dat", namespaceURI="urn:datos"),
    		@XmlNs(prefix="ret", namespaceURI="urn:retorno"),
    }
)  
!!!!!!!   al colocarlo, el unmarshal devuelve javax.xml.bind.JAXBElement en vez del objeto.
*/
package cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto;
import javax.xml.bind.annotation.*;
