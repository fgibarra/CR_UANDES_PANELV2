select n.id_miembro 
from nap_grupo_miembro n, mi_cuentas_gmail c, mi_grupos g
where g.group_name=:#groupName and c.login_name=:#loginName
and n.id_miembro=c.moodle_id and n.key_grupo = g.key