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
    (
        SELECT DISTINCT
            g.key
        FROM
            mi_grupos          g,
            nap_grupo_miembro  n
        WHERE
                g.key = n.key_grupo
            AND g.origen is null
            AND n.activo <> n.creado_gmail
    )          x,
    mi_grupos  g
WHERE
    g.key = x.key
    and g.seleccion_especial is null
ORDER BY
    g.key_tipo,
    g.group_name