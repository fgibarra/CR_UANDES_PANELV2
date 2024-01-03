package cl.uandes.panel.gui.view;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "helloWorld")
@SessionScoped
public class HelloWorldController implements Serializable {
	   /**
	 * 
	 */
	private static final long serialVersionUID = 7300748982160040405L;
	// properties
	private String name;

	/**
	 * default empty constructor
	 */
	public HelloWorldController() {
	}

	// -------------------getter & setter
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method that is backed to a submit button of a form.
	 */
	public String send() {
		return "success?faces-redirect=true";
	}
}
