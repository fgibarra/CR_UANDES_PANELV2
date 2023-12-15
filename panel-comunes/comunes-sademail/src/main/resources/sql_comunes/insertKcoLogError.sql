insert into kco_log_error 
(key, log_clase, log_metodo, log_exception, log_message, log_apoyo, log_fecha, stacktrace, log_aplicacion, key_resultado_error)
values
(HIBERNATE_SEQUENCE.NEXTVAL, :#logClase, :#logMetodo, :#logException, :#logMessage, :#logApoyo, sysdate, :#stacktrace, :#logAplicacion, :#keyResultadoError)