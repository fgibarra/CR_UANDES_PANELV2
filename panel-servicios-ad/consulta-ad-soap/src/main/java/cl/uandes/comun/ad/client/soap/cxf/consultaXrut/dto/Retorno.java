package cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author fernando
 *
 */
@XmlRootElement(name="retorno"/*,namespace="urn:retorno"*/)
@XmlAccessorType(XmlAccessType.FIELD) // indica que las anotaciones van en el atributo
@XmlType(name = "retorno", propOrder = {
		"cantidad",
	    "rut",
	    "usuario",
	    "employeeid",
	    "dn",
	    "correo",
	    "activa",
	    "bloqueada",
	    "ou",
	    "estado"
})
public class Retorno implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4412552831251047543L;

    @XmlElement(required = true)
    protected String cantidad;
    @XmlElement(required = true)
    protected String rut;
    @XmlElement(required = true)
    protected String usuario;
    @XmlElement(required = true)
    protected String employeeid;
    @XmlElement(required = true)
    protected String dn;
    @XmlElement(required = true)
    protected String correo;
    @XmlElement(required = true)
    protected String activa;
    @XmlElement(required = true)
    protected String bloqueada;
    @XmlElement(required = true)
    protected String ou;
    @XmlElement(required = true)
    protected String estado;

    /**
     * Gets the value of the cantidad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCantidad() {
        return cantidad;
    }

    /**
     * Sets the value of the cantidad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCantidad(String value) {
        this.cantidad = value;
    }

    /**
     * Gets the value of the rut property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRut() {
        return rut;
    }

    /**
     * Sets the value of the rut property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRut(String value) {
        this.rut = value;
    }

    /**
     * Gets the value of the usuario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Sets the value of the usuario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuario(String value) {
        this.usuario = value;
    }

    /**
     * Gets the value of the employeeid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeeid() {
        return employeeid;
    }

    /**
     * Sets the value of the employeeid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeeid(String value) {
        this.employeeid = value;
    }

    /**
     * Gets the value of the dn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDn() {
        return dn;
    }

    /**
     * Sets the value of the dn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDn(String value) {
        this.dn = value;
    }

    /**
     * Gets the value of the correo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Sets the value of the correo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorreo(String value) {
        this.correo = value;
    }

    /**
     * Gets the value of the activa property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActiva() {
        return activa;
    }

    /**
     * Sets the value of the activa property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActiva(String value) {
        this.activa = value;
    }

    /**
     * Gets the value of the bloqueada property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBloqueada() {
        return bloqueada;
    }

    /**
     * Sets the value of the bloqueada property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBloqueada(String value) {
        this.bloqueada = value;
    }

    /**
     * Gets the value of the ou property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOu() {
        return ou;
    }

    /**
     * Sets the value of the ou property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOu(String value) {
        this.ou = value;
    }

    /**
     * Gets the value of the estado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Sets the value of the estado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }
}
