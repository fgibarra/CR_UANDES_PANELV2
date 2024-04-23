select u.NOMBRES,(u.ap_pater||' '||u.ap_mater) APELLIDOS,u.spriden_id RUT, 
    case da.estado_academico 
    when 'PROFESOR' then 'ProfesoresUA'
    else 'AlumnosUA' 
    end as RAMA, 
    lower(u.email) EMAIL, 
    u.address1a as DIRECCION, c.comuna_sndx COMUNA 
from bdc_usuario_millenium u, bdc_um_datos_academicos da, bdcc_comuna c 
where da.ESTADO_ACADEMICO in ('PROFESOR','EGRESADO','INPROGRESS','SUSPENDIDO') and u.EXP_DATE >to_date(:#periodo,'YYYYMMDD')
                and u.pkkey=da.key_usuario and da.informada_millennium=1
                and (usuario_ad is null or userid_alma is null)
                and c.codigo(+)=u.address1b
union
select u.NOMBRES,(u.ap_pater||' '||u.ap_mater) APELLIDOS,u.spriden_id RUT, 
    'BibliotecaUA' as RAMA, 
    lower(u.email) EMAIL, 
    u.address1a as DIRECCION, c.comuna_sndx COMUNA 
from bdc_usuario_millenium u, bdcc_comuna c 
where u.id_origen in ('AMIGO/SOCIO','CLINICA','OTROS','OTROS PROFESORES','ESE','AMIGOS') 
                and u.EXP_DATE >to_date(:#periodo,'YYYYMMDD')
                and (usuario_ad is null or userid_alma is null)
                and c.codigo=u.address1b(+)