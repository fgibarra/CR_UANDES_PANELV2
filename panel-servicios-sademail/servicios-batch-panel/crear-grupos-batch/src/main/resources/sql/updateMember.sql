update nap_grupo_miembro_azure set activo=1, creado_azure=1
where id_miembro=:#idMiembro and key_grupo=:#keyGrupo
