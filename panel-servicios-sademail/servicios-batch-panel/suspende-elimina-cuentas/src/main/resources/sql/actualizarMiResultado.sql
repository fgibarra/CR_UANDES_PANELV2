update mi_resultado
set 
grupos_solicitados=:#countProcesados, 
grupos_creados=:#countErrores,
miembros_solicitados=:#countAgregadosBD,
miembros_creados=:#countAgregadosAD,
hora_termino=sysdate,
comentario=:#jsonResultado
where
key=:#keyResultado