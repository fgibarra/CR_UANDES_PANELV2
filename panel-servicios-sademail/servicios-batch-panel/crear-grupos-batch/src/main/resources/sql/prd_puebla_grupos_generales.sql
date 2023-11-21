pck_funciones.prd_puebla_grupos_generales (
	OUT java.sql.Types.REF_CURSOR out_resultado,
	'P_PERIODO' java.sql.Types.DECIMAL ${header.DatosKcoFunciones.periodo},
	'p_email_permission' java.sql.Types.VARCHAR ${header.emailPermission},
	'p_proceso' java.sql.Types.VARCHAR ${header.proceso}
)