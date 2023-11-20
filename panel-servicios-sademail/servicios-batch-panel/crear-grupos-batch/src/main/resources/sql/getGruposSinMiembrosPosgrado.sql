SELECT
    g.key,
    g.group_id,
    g.group_name,
    g.group_description,
    g.emailpermission,
    g.activo,
    g.creado_gmail,
    g.periodo,
    g.fecha_validacion,
    g.seleccion_especial,
    g.key_tipo,
    g.origen,
    g.fecha_creacion
FROM
    mi_grupos  g
WHERE
    g.activo=0 AND g.creado_gmail=1 
    AND (select count(*) from nap_grupo_owner n where n.group_name = g.group_name) = 0
    AND g.origen = 'POSGRADO'
ORDER BY
    g.key_tipo,
    g.group_name