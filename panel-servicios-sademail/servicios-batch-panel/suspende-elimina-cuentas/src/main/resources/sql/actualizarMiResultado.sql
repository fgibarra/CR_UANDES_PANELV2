update mi_resultado
set 
grupos_solicitados=:#countProcesados, 
grupos_creados=:#countErrores,
miembros_solicitados=:#countRegistrados,
miembros_creados=:#countNoRegistrados,
hora_termino=sysdate,
comentario=:#jsonResultado
where
key=:#keyResultado