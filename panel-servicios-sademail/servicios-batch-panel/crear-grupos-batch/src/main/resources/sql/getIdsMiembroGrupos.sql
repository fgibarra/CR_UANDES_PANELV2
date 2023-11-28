select c.moodle_id, c.login_name, c.id_gmail, g.group_name, 
	n.id_miembro, n.key_grupo, n.activo, n.creado_gmail, n.fecha_validacion
from nap_grupo_miembro n, mi_cuentas_gmail c, mi_grupos g
where c.moodle_id=n.id_miembro and g.key=n.key_grupo and n.activo=:#tipoOperacion and n.key_grupo=:#keyGrupo
