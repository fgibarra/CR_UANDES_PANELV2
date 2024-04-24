select u.spriden_id,
	u.NOMBRES as SPRIDEN_FIRST_NAME,
	(u.ap_pater||' '||u.ap_mater) as SPRIDEN_LAST_NAME, 
    case da.estado_academico
    when 'PROFESOR' then 'ProfesoresUA'
    else 'AlumnosUA' 
    end as RAMA,
    da.estado_academico as ESTADO,
    lower(u.email) EMAIL, 
    u.address1a as DIRECCION, 
    c.comuna_sndx COMUNA 
from bdc_usuario_millenium u, bdc_um_datos_academicos da, bdcc_comuna c 
where da.ESTADO_ACADEMICO in ('PROFESOR','EGRESADO','INPROGRESS','SUSPENDIDO') and u.EXP_DATE >to_date(:#periodo,'YYYYMMDD')
                and u.pkkey=da.key_usuario and da.informada_millennium=1
                and (usuario_ad is null or userid_alma is null)
                and c.codigo(+)=u.address1b
union
select u.spriden_id,
	u.NOMBRES as SPRIDEN_FIRST_NAME,
	(u.ap_pater||' '||u.ap_mater) as SPRIDEN_LAST_NAME, 
    'BibliotecaUA' as RAMA, 
    null as ESTADO,
    lower(u.email) EMAIL,
    u.address1a as DIRECCION,
    c.comuna_sndx COMUNA 
from bdc_usuario_millenium u, bdcc_comuna c 
where u.id_origen in ('AMIGO/SOCIO','CLINICA','OTROS','OTROS PROFESORES','ESE','AMIGOS') 
                and u.EXP_DATE >to_date(:#periodo,'YYYYMMDD')
                and (usuario_ad is null or userid_alma is null)
                and c.codigo=u.address1b(+)             