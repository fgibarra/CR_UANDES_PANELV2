select c.id_gmail, g.group_id, n.id_miembro, n.key_grupo, c.login_name
from nap_grupo_miembro n, mi_cuentas c, mi_grupos g
where c.moodle_id=n.id_miembro and g.key=n.key_grupo and n.activo=:#tipoOperacion and n.key_grupo=:#keyGrupo
