select SMTP_SERVER,EMAIL_SENDER,SMTP_USER,SMTP_PASSWORD,EMAIL_WEBMASTER,EMAIL_SUPPORT,GMAIL_ADMIN,GMAIL_PASSWD,GMAIL_DOMINIO 
from KCO_SENDMAIL_PARAMS 
WHERE KEY=:#key