select TOTAL_CUENTAS, CUENTAS_EXTERNAS, TOTAL_PROFESORES, CUENTAS_ALUMNOS, USADO_GMAIL, 
	USADO_DRIVE, USADO_PHOTOS, QUOTA
from (
 select a.gmail+b.gmail usado_gmail, a.drive+b.drive usado_drive, a.photos+b.photos usado_photos, a.quota+b.quota quota from
    (select sum(gmail_used_quota) gmail,sum(drive_used_quota) drive,sum(photos_used_quota) photos,sum(used_quota) quota from mi_cuentas_externas) a,
    (select sum(gmail_used_quota) gmail,sum(drive_used_quota) drive,sum(photos_used_quota) photos,sum(used_quota) quota from mi_cuentas_gmail) b
 ),( 
 select total1 + total2 as total_cuentas, total2 as cuentas_externas, total_profesores, total1-total_profesores as cuentas_alumnos from 
    (select count(*) total1 from mi_cuentas_gmail) a,
    (select count(*) total2 from mi_cuentas_externas) b,
    (select count(c.moodle_id) total_profesores from mi_cuentas_gmail c, bdc_usuario_millenium u, bdc_um_datos_academicos da
            where da.informada_millennium=1 and u.pkkey=da.key_usuario and da.estado_academico='PROFESOR' and u.spriden_id=c.moodle_id) c
 
 )