package cl.uandes.panelv2.apiSendmail.procesor.sendmail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;

/**
 * Envia correo usando en api javax.mail<br>
 * Dado que esta version de Camel (2.12) no soporta los adjunto, se debe enviar los correos con adjunto usando el api
 * javax.mail<br>
 * Usat SendMailDTO para contener los datos usados por esta funcion.
 * 
 * @author fernando
 *
 */
public class SendMail {

    private Logger logger = Logger.getLogger(getClass());
    private static SendMail instance;

    private String smtpServer;
    private String from;
    private String smtpUser, smtpPassword;
    private boolean inicializado;
    private Properties props;
    private Authenticator authentificator;
    private Integer smtpPort;
    private final Integer SMTP_PORT_DEFAULT = Integer.valueOf(587);
    private Boolean debugSmtp;
    
    public static String SMTP_SERVER = "smtpServer";
    public static String SMTP_USER = "smtpUser";
    public static String SMTP_PASSWORD = "smtpPasswd";
    public static String SMPT_PORT = "smtpPort";
    public static String SMTP_TLS = "smtpTLS";
    public static String SMTP_DEBUG = "debugSMTP";
    
    private SendMail(Properties smtpProp) {
        logger.info("SendMail Inicializacion: \n"+dumpProperties(smtpProp));
        try {
            smtpServer =
                    (String)smtpProp.get(SMTP_SERVER);
            smtpUser = (String)smtpProp.get(SMTP_USER);
            smtpPassword =
                    (String)smtpProp.get(SMTP_PASSWORD);
            smtpPort = Integer.valueOf((String) smtpProp.get(SMPT_PORT));

            if (smtpServer == null) {
                logger.error("NO hay SMTP Server definido");
                throw new RuntimeException("NO se define SMTP server");
            }
            if (smtpPort == null)
                smtpPort = SMTP_PORT_DEFAULT;

            // Get system properties
            //  props = System.getProperties();
            props = new Properties();

            // Setup mail server
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.port", smtpPort);
            if ("true".equalsIgnoreCase((String) smtpProp.get(SMTP_TLS)))
                props.setProperty("mail.smtp.starttls.enable", "true");


            if (smtpUser != null)
                createAuthenticator();

            String valor = (String)smtpProp.get(SMTP_DEBUG);
            if (valor == null)
            	this.debugSmtp = Boolean.FALSE;
			else
				try {
					this.debugSmtp = Boolean.valueOf(valor);
				} catch (Exception e) {
					this.debugSmtp = Boolean.FALSE;
				}
            logger.info("SendMail Inicializacion: \n"+dumpProperties(smtpProp));
            inicializado = true;
        } catch (Exception e) {
            logger.error("instanciando Sendmail", e);
            inicializado = false;
        }
    }

    private String dumpProperties(Properties smtpProp) {
		StringBuffer sb = new StringBuffer();
		sb.append(SMTP_SERVER).append('=').append(smtpProp.get(SMTP_SERVER)).append('\n');
		sb.append(SMTP_USER).append('=').append(smtpProp.get(SMTP_USER)).append('\n');
		sb.append(SMTP_PASSWORD).append('=').append(smtpProp.get(SMTP_PASSWORD)).append('\n');
		sb.append(SMPT_PORT).append('=').append(smtpProp.get(SMPT_PORT)).append('\n');
		sb.append(SMTP_TLS).append('=').append(smtpProp.get(SMTP_TLS)).append('\n');
		if (props != null) {
			sb.append("mail.smtp.host").append('=').append(props.get("mail.smtp.host")).append('\n');
			sb.append("mail.smtp.port").append('=').append(props.get("mail.smtp.port")).append('\n');
			sb.append("mail.smtp.starttls.enable").append('=').append(props.get("mail.smtp.starttls.enable")).append('\n');
			sb.append("mail.smtp.auth").append('=').append(props.get("mail.smtp.auth")).append('\n');
		}
		return sb.toString();
	}

	public static SendMail getInstance(Properties smtpProp) {
        if (instance == null) {
            instance = new SendMail(smtpProp);
        }

        return instance;
    }

    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("smtpServer=").append(smtpServer).append('\n');
    	sb.append("from=").append(from).append('\n');
    	sb.append("smtpUser=").append(smtpUser).append('\n');
    	sb.append("smtpPassword=").append(smtpPassword).append('\n');
    	sb.append("smtpPort=").append(smtpPort).append('\n');
    	sb.append("inicializado=").append(inicializado).append('\n');

    	sb.append("props\n");
    	Enumeration<?> keys = props.keys();
    	while (keys.hasMoreElements()) {
    		String key = (String)keys.nextElement();
    		sb.append(key).append('=').append(props.get(key)).append('\n');
    	}

    	sb.append("LoginAuthenticator:\n");
    	sb.append("usuarioSmtp:").append(((LoginAuthenticator)authentificator).getUsuarioSmtp()).append('\n');
    	sb.append("passwordSmtp:").append(((LoginAuthenticator)authentificator).getPasswordSmtp()).append('\n');

    	return sb.toString();
    }

    public void createAuthenticator(String smtpUser, String smtpPassword) {
        this.smtpUser = smtpUser;
        this.smtpPassword = smtpPassword;
        createAuthenticator();
    }

    public void createAuthenticator() {
        this.authentificator =
                (Authenticator)LoginAuthenticator.getInstance(smtpUser,
                                                              smtpPassword);
        props.put("mail.smtp.auth", "true");
    }

    public SendMailDTO send(SendMailDTO dto) {
    	from = dto.getFrom();
    	if (from == null) {
    		dto.setExitoso(false);
    		logger.error("send: NO CONFIGURADO from es NULO");
    		return dto;
    	}

        dto.setExitoso(true);
        StringBuffer sb = new StringBuffer(dto.getTo());
        if (dto.getCc() != null && dto.getCc().length > 0) {
        	for (String cc : dto.getCc())
        		sb.append(';').append(cc);
        }
    	StringTokenizer st = new StringTokenizer(sb.toString(),";");
    	while (st.hasMoreElements()) {
    		String destinatario = st.nextToken();
	        try {
	            // Get session
	        	logger.info(toString());

/*	            Session session =
	                Session.getDefaultInstance(props, (Authenticator)authentificator);*/
	        	Session session = Session.getInstance(props,
	                    new javax.mail.Authenticator() {
	                        protected PasswordAuthentication getPasswordAuthentication() {
	                            return new PasswordAuthentication(smtpUser, smtpPassword);
	                        }
	                    });
	            session.setDebug(debugSmtp);

	            // Define message
	            MimeMessage message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(from));

	            InternetAddress[] address = { new InternetAddress(destinatario) };
	            message.setRecipients(Message.RecipientType.TO, address);

	            message.setSubject(dto.getSubject(),"UTF8");
	            logger.info("send:asunto {" + dto.getSubject() + "}");
	            message.setSentDate(new Date());


	            // Create the message part
	            BodyPart messageBodyPart = new MimeBodyPart();

	            // Fill the message
	            messageBodyPart.setContent(dto.getMensaje(),"text/html");
	            //messageBodyPart.setText(dto.getMensaje());

	            // Create a Multipart
	            Multipart multipart = new MimeMultipart();

	            // Add part one
	            multipart.addBodyPart(messageBodyPart);

	            //
	            // Part two is attachment
	            //
	            DataSource source = null;
	            if (dto.getFileName() != null && dto.getFileName().length() > 0) {
	                // Create second body part
	                messageBodyPart = new MimeBodyPart();

	                // Get the attachment
	                if (dto.getInputStream() == null) {
	                    source = new FileDataSource(dto.getFileName());
	                } else {
	                    source = new InputStreamDataSource(dto);
	                }

	                // Set the data handler to the attachment
	                messageBodyPart.setDataHandler(new DataHandler(source));

	                // Set the filename
	                messageBodyPart.setFileName(dto.getNombreArchivo());
	                logger.info("send: nombre archivo:" + dto.getFileName() +
	                             " - " + dto.getNombreArchivo());

	                // Add part two
	                multipart.addBodyPart(messageBodyPart);

	            }
	            if (dto.getAttachments() != null) {
	            	Object attachments = dto.getAttachments();
	            	if (attachments instanceof String) {
	            		messageBodyPart = createBodyPart(attachments);
	            		multipart.addBodyPart(messageBodyPart);
	            	} else if (attachments instanceof List) {
	            		@SuppressWarnings("unchecked")
						List<String> l = (List<String>)attachments;
	            		for (String archivo: l) {
	                		messageBodyPart = createBodyPart(archivo);
	                		multipart.addBodyPart(messageBodyPart);
	            		}
	            	}
	            }
	            // Put parts in message
	            message.setContent(multipart);
	            
	            logger.info("send: antes de enviar [" + (dto.getMensaje().length() < 1000 ? dto.getMensaje() : dto.getMensaje().substring(0, 1000)) + "]");
	            // Send the message
	            Transport.send(message);

	            logger.info("send: enviado");

	            if (dto.getFileName() != null && dto.getFileName().length() > 0 &&
	                dto.getInputStream() != null &&
	                source instanceof InputStreamDataSource) {
	                // Eliminar archivo de trabajo
	                ((InputStreamDataSource)source).getFile().delete();
	            }

	        } catch (Exception e) {
	            logger.error("send smtpServer=" + smtpServer + " from=" + from, e);
	            dto.setMsgError(e.getMessage());
	            dto.setExitoso(false);
	        }
    	}
        return dto;
    }

    private BodyPart createBodyPart(Object archivo) throws Exception {
        // Create second body part
    	BodyPart messageBodyPart = new MimeBodyPart();
    	DataSource source = null;
        // Get the attachment
    	if (archivo instanceof String)
    		source = new FileDataSource((String)archivo);

        // Set the data handler to the attachment
        messageBodyPart.setDataHandler(new DataHandler(source));

        // Set the filename
        String name = (String)archivo;
        logger.info("createBodyPart: archivo viene "+name);
        int li = name.lastIndexOf(File.separator);
        if (li >= 0)
        	name = name.substring(li+1);
        messageBodyPart.setFileName(name);
        logger.info("send: nombre archivo:" + name);
        return messageBodyPart;
    }

    public void setInicializado(boolean inicializado) {
        this.inicializado = inicializado;
    }

    public boolean isInicializado() {
        return inicializado;
    }

    @SuppressWarnings("unused")
	public static File upLoad2FileInJboss(InputStream input, String fileName) {
        try {

            String fullPath = getUploadHome();

            byte buffer[] = null;
            // crear archivo de salida
            File out = new File(fullPath.toLowerCase());


            if (!out.exists()) {
                if (!out.mkdirs()) {
                    throw new RuntimeException("no se puede crear directorio");
                }
            }
            File f = new File(fullPath.toLowerCase(), fileName);
            if (f.exists())
                f.delete();

            FileOutputStream fos = new FileOutputStream(f);
            int cuenta = 0;
            int bSize = 0;
            do {
                bSize = input.available();
                if (bSize > 0) {
                    buffer = new byte[bSize];
                    cuenta = input.read(buffer);
                    fos.write(buffer);
                }
            } while (bSize > 0);
            fos.close();

            return f;

        } catch (Exception e) {
            //logger.error("upLoad2FileInJboss" + e.getMessage());
            throw new RuntimeException("upLoad2FileInJboss" + e.getMessage());
        }
    }

    private static String getUploadHome() {
		return System.getProperty("jboss.server.temp.dir", "/tmp");
	}

	private class InputStreamDataSource extends FileDataSource implements DataSource {
        private SendMailDTO dto;

        public InputStreamDataSource(SendMailDTO dto) {
            super(SendMail.upLoad2FileInJboss(dto.getInputStream(),
                                              dto.getFileName()));
            this.dto = dto;
        }

        public File getFile() {
            return new File(getUploadHome(), dto.getFileName());
        }
    }
}
