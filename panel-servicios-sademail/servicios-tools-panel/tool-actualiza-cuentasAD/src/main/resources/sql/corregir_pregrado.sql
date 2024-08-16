SELECT
    pidm,
    rut,
    nombres   ,
    apellidos   ,
    login_name,
    nivel,
    estado
FROM
    (
        SELECT
            a.pidm,a.rut,a.nivel,a.estado,cg.login_name,cg.nombres, cg.apellidos
        FROM
            (
                SELECT DISTINCT
                    v.pidm,
                    v.rut,
                    v.apellido,
                    v.nivel,
                    v.estado,
                    s.spriden_first_name,
                    s.spriden_mi
                FROM
                    swvalum   v,
                    spriden   s
                WHERE
                    v.pidm > 0
                    AND to_number(v.term_code_eff
                                  || lpad(v.sorlcur_seqno, 3, 0)
                                  || lpad(v.sorlfos_seqno, 3, 0)) = (
                        SELECT
                            MAX(to_number(x.term_code_eff
                                          || lpad(x.sorlcur_seqno, 3, 0)
                                          || lpad(x.sorlfos_seqno, 3, 0)))
                        FROM
                            swvalum x
                        WHERE
                            x.pidm = v.pidm
                    )
                    AND v.nivel = 'UG'
                    AND v.estado IN (
                        'INPROGRESS',
                        'SUSPENDIDO'
                    )
                    AND s.spriden_pidm = v.pidm
                    AND s.spriden_change_ind IS NULL
            ) a,
            mi_cuentas_gmail cg, ad_cuentas_creadas acc
        WHERE
            cg.moodle_id (+) = a.rut
            and acc.rut = cg.moodle_id
    )
WHERE
    rut LIKE '@%'
ORDER BY
    spriden_id