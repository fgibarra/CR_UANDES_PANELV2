select 'true' as existe 
from mi_grupos g
where g.group_name=:#groupName and g.activo=1 and g.creado_gmail=1