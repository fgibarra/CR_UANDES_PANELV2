select key, group_name, owner_email, creado_gmail, activo, fecha_validacion
from nap_grupo_owner
where group_name = :#groupName