delete from nap_grupo_miembro
where key_grupo = (select key from mi_grupos where group_name=:#groupName)
	and id_miembro = (select moodle_id from mi_cuentas_gmail where login_name=:#email)