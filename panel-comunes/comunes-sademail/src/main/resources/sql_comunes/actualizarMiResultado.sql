update mi_resultado
set 
grupos_solicitados=:#countProcesados, 
grupos_creados=:#countErrores,
miembros_solicitados=:#count1,
miembros_creados=:#count2,
hora_termino=sysdate,
threads_engendradas=1,
comentario=:#jsonResultado
where
key=:#keyResultado