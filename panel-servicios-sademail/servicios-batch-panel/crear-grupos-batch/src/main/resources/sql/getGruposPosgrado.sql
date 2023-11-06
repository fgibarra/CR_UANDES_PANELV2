SELECT
    g.key,
    g.group_id,
    g.group_name,
    g.group_description,
    g.emailpermission,
    g.activo,
    g.creado_azure,
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
            mi_grupos_azure          g,
            nap_grupo_miembro_azure  n
        WHERE
                g.key = n.key_grupo
            AND g.origen = 'POSGRADO'
            AND n.activo <> n.creado_azure
    )          x,
    mi_grupos_azure  g
WHERE
    g.key = x.key
ORDER BY
    g.key_tipo,
    g.group_name