package cl.uandes.panelv2.apiSendmail.procesor.sendmail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Para la autentificacion SMTP
 * 
 * @author fernando
 *
 */
public class LoginAuthenticator extends Authenticator {

    private String usuarioSmtp, passwordSmtp;
    private static LoginAuthenticator instance;

    public static LoginAuthenticator getInstance(String smtpUser, String smtpPassword) {
        if (instance == null)
            instance = new LoginAuthenticator();
        instance.setUsuarioSmtp(smtpUser);
        instance.setPasswordSmtp(smtpPassword);
        return instance;
    }

    public static LoginAuthenticator getInstance() {
        if (instance == null)
            instance = new LoginAuthenticator();
        return instance;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(getUsuarioSmtp(),
                                          getPasswordSmtp());
    }

    public void setUsuarioSmtp(String usuarioSmtp) {
        this.usuarioSmtp = usuarioSmtp;
    }

    public String getUsuarioSmtp() {
        return usuarioSmtp;
    }

    public void setPasswordSmtp(String passwordSmtp) {
        this.passwordSmtp = passwordSmtp;
    }

    public String getPasswordSmtp() {
        return passwordSmtp;
    }
}
