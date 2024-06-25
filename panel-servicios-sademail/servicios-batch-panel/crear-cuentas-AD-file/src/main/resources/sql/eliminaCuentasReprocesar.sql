delete from ad_cuentas_creadas where key in (
with DI AS (
        select key, samaccount_name, rut, ou, FECHA_CREACION from ad_cuentas_creadas where rut<>samaccount_name),
     KOUT AS (
        select key from DI where  LENGTH(TRIM(TRANSLATE(samaccount_name, '0123456789K@', ' '))) is null and rownum <= 500 and fecha_creacion <= to_date('202404252300','YYYYMMDDHH24MI'))
select key from KOUT
)