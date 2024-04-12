with FALTAN AS
        (
                SELECT DISTINCT
                        V.rut  ,
                        v.nivel,
                        v.estado
                FROM
                        SWVALUM v,
                        bdc_usuario_millenium u
                WHERE
                        v.PIDM       > 0
                and     u.spriden_id = v.rut
                AND     v.nivel NOT IN('UG') --SOLO post
                AND     v.estado    IN ('INPROGRESS',
                                    'SUSPENDIDO')
                AND     substr(sb_fieldofstudy.f_find_current_all_ind (V.pidm,V.sorlfos_lcur_seqno,V.sorlfos_seqno,V.sorlfos_lfst_code,V.sorlfos_priority_no,V.sorlfos_current_cde), 1,1) ='Y'
                AND     substr(sb_learnercurricstatus.f_is_active(V.SORLFOS_CACT_CODE),1)                                                                                                 ='Y'
                AND     (
                                u.userid_alma is null
                        or u.usuario_ad is null)
                
                UNION
                
                SELECT DISTINCT
                        V.rut  ,
                        v.nivel,
                        v.estado
                FROM
                        SWVALUM v
                WHERE
                        v.PIDM > 0
                AND     v.nivel NOT IN('UG') --SOLO post
                AND     v.estado    IN ('INPROGRESS',
                                    'SUSPENDIDO')
                AND     substr(sb_fieldofstudy.f_find_current_all_ind (V.pidm,V.sorlfos_lcur_seqno,V.sorlfos_seqno,V.sorlfos_lfst_code,V.sorlfos_priority_no,V.sorlfos_current_cde), 1,1) ='Y'
                AND     substr(sb_learnercurricstatus.f_is_active(V.SORLFOS_CACT_CODE),1)                                                                                                 ='Y'
                AND     V.rut not in
                        (
                                SELECT
                                        spriden_id as rut
                                from
                                        bdc_usuario_millenium u) )
SELECT
        SPRIDEN_ID        ,
        SPRIDEN_FIRST_NAME,
        SPRIDEN_MI        ,
        SPRIDEN_LAST_NAME ,
        SPRIDEN_PIDM      ,
        FA.NIVEL          ,
        FA.ESTADO
FROM
        SPRIDEN SP,
        FALTAN fa
WHERE
        SP.SPRIDEN_ID =fa.RUT
and     spriden_change_ind is null
order by
        spriden_id