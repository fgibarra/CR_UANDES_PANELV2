pck_funciones.prd_actualiza_cuenta(
	'p_login_name' java.sql.Types.VARCHAR ${header.login_name},
	'p_nombres' java.sql.Types.VARCHAR ${header.nombres},
	'p_apellidos' java.sql.Types.VARCHAR ${header.apellidos},
	'p_fecha_suspension' java.sql.Types.TIMESTAMP ${header.fecha_suspension},
	'p_id_gmail' java.sql.Types.VARCHAR ${header.id_gmail},
	'p_last_login' java.sql.Types.TIMESTAMP ${header.last_login},
	'p_fecha_creacion' java.sql.Types.TIMESTAMP ${header.fecha_creacion},
	'p_gmail_used_quota' java.sql.Types.INTEGER ${header.gmail_used_quota},
	'p_drive_used_quota' java.sql.Types.INTEGER ${header.drive_used_quota},
	'p_photos_used_quota' java.sql.Types.INTEGER ${header.photos_used_quota},
	'p_total_quota' java.sql.Types.INTEGER ${header.total_quota},
	'p_used_quota' java.sql.Types.INTEGER ${header.used_quota},
	'p_used_quota_percentage' java.sql.Types.INTEGER ${header.used_quota_percentage},
	,OUT java.sql.Types.VARCHAR estado_academico,
	,OUT java.sql.Types.DATE fecha_aviso
)