insert into MI_RESULTADO_ERRORES
(KEY, ID_USUARIO, TIPO, CAUSA, KEY_GRUPO, KEY_RESULTADO)
values (hibernate_sequence.nextval, :#ID_USUARIO, :#TIPO, :#CAUSA, :#KEY_GRUPO, :#KEY_RESULTADO)