select case when userid_alma is null then 'FALSE' else 'TRUE' end as HAY_CUENTA 
from bdc_usuario_millenium 
where spriden_id=:#rut