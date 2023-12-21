insert nap_grupo_miembro (key_grupo, id_miembro, activo, creado_gmail, fecha_validacion)
values ((select key from mi_grupos where group_name=:#groupName), 
        (select moodle_id from mi_cuentas_gmail where login_name=:#email),
        1, 1, sysdate)