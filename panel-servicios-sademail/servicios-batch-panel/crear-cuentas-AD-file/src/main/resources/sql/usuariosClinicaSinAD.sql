select upper(u.nombres) as nombres, substr(u.spriden_id, 1,8) as password,
    'BibliotecaUA' as rama, u.spriden_id as rut,
    (upper(u.ap_pater)||' '||upper(u.ap_mater)) as apellidos, 
    c.comuna_sndx as comuna, u.email, u.address1a as direccion
from bdc_usuario_millenium u, bdcc_comuna c
where c.codigo=u.address1b and u.id_origen='CLINICA' 
    and (u.usuario_ad is null or u.userid_alma is null);