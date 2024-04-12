SELECT
        *
FROM
        (
                SELECT DISTINCT
                        V.pidm              ,
                        V.rut               ,
                        V.apellido          ,
                        s.spriden_first_name,
                        s.spriden_mi        ,
                        v.nivel             ,
                        v.estado
                FROM
                        SWVALUM v,
                        spriden s
                WHERE
                        v.PIDM         > 0
                and     s.spriden_pidm = v.pidm
                and     s.SPRIDEN_CHANGE_IND is null
                AND     v.nivel  IN('UG') --SOLO PREGRADO
                AND     v.estado IN ('INPROGRESS',
                                    'SUSPENDIDO')
                AND     substr(sb_fieldofstudy.f_find_current_all_ind (V.pidm,V.sorlfos_lcur_seqno,V.sorlfos_seqno,V.sorlfos_lfst_code,V.sorlfos_priority_no,V.sorlfos_current_cde), 1,1) ='Y'
                AND     substr(sb_learnercurricstatus.f_is_active(V.SORLFOS_CACT_CODE),1)                                                                                                 ='Y' ) A,
        mi_cuentas_gmail cg
WHERE
        cg.moodle_id(+) = a.rut
AND     cg.moodle_id is null
ORDER BY
        a.rut