package cl.uandes.panel.aeOwnersMembers.api.resources.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

import cl.uandes.panel.aeOwnersMembers.api.resources.soap.dto.ObjectFactory;
import cl.uandes.panel.aeOwnersMembers.api.resources.soap.dto.PanelRequestTYPE;
import cl.uandes.panel.aeOwnersMembers.api.resources.soap.dto.PanelEsbResponseTYPE;

@WebService(name = "serviciosPanelProcesarPt", 
            targetNamespace = "http://esb.panel.firsa.cl/ESB/wsdl/panel/Servicios/Procesar-v1.0")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface WsServiciosPanel {

    @WebMethod(operationName = "invocaServicioOp", 
    		   action = "http://esb.panel.firsa.cl/ESB/wsdl/panel/Servicios/Procesar-v1.0/Op")
    @WebResult(name = "invocaServicioResponse", partName = "invocaServicioResponsePanel")
    public PanelEsbResponseTYPE invocaServicioOp(@WebParam(name="request", partName="invocaServicioRequestBdc2")PanelRequestTYPE request);
}
