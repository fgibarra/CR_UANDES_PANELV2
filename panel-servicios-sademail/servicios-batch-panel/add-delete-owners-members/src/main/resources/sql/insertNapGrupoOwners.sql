insert into nap_grupo_owner (key, group_name, owner_email, creado_gmail, activo, fecha_validacion)
values(HIBERNATE_SEQUENCE.NEXTVAL, :#groupName, :#email, 1, 1, sysdate)