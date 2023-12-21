select 'true' as existe 
from mi_cuentas_gmail c, mi_grupos g
where c.login_name=:#email and g.group_name=:#groupName
      and g.activo=1 and g.creado_gmail=1