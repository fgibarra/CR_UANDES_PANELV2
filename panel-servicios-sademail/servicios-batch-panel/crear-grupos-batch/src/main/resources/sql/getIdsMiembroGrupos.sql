select c.id_azure, g.group_id, n.id_miembro, n.key_grupo, c.login_name
from nap_grupo_miembro_azure n, mi_cuentas_azure c, mi_grupos_azure g
where c.moodle_id=n.id_miembro and g.key=n.key_grupo and n.activo=:#tipoOperacion and n.key_grupo=:#keyGrupo
