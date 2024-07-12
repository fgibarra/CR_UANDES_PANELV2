with creadas as
        (
                select
                        samaccount_name,
                        rut            ,
                        ou             ,
                        fecha_creacion
                from
                        ad_cuentas_creadas
                where
                        fecha_actualizacion is null
                and     fecha_creacion between :#fechaDesde and :#fechaHasta )
SELECT DISTINCT
        V.rut            ,
        v.pidm           ,
        c.samaccount_name,
        c.fecha_creacion ,
        c.ou             ,
        v.nivel          ,
        v.estado
FROM
        SWVALUM v,
        creadas c
WHERE
        v.PIDM > 0
AND     v.nivel NOT IN('UG') --SOLO post
AND     v.estado    IN ('INPROGRESS',
                    'SUSPENDIDO')
AND     substr(sb_fieldofstudy.f_find_current_all_ind (V.pidm,V.sorlfos_lcur_seqno,V.sorlfos_seqno,V.sorlfos_lfst_code,V.sorlfos_priority_no,V.sorlfos_current_cde), 1,1) ='Y'
AND     substr(sb_learnercurricstatus.f_is_active(V.SORLFOS_CACT_CODE),1)                                                                                                 ='Y'
and     v.rut                                                                                                                                                             = c.rut
order by
        fecha_creacion desc