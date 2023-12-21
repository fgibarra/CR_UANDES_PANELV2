select 
	key, 
	funcion, 
	min_thread, 
	max_thread, 
	parametro, 
	comentario, 
	ultima_ejecucion 
from kco_funciones 
where funcion=:#proceso