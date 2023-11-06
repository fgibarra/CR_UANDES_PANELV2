update mi_resultado
set hora_termino=sysdate,
grupos_creados=:#countGruposAgregadosBD,
miembros_creados=:#countMiembrosAgregadosBD,
rec_procesar=:#countProcesados,
comentario=:#resultado
where key=:#keyResultado