select pidm SPRIDEN_PIDM, rut SPRIDEN_ID, apellido SPRIDEN_LAST_NAME, spriden_first_name, spriden_mi  from 
    (select * from 
        (
        SELECT
        DISTINCT
        V.pidm,V.rut,V.apellido,s.spriden_first_name, s.spriden_mi
        FROM SWVALUM v,spriden s
        WHERE v.PIDM > 0
        AND   to_number(v.term_code_eff||lpad(v.sorlcur_seqno,3,0)||lpad(v.sorlfos_seqno,3,0)) =(
                                                                    select max(to_number(x.term_code_eff||lpad(x.sorlcur_seqno,3,0)||lpad(x.sorlfos_seqno,3,0)))
                                                                    from swvalum x
                                                                    where x.pidm = v.pidm
                                                                    )
        and v.nivel ='UG'
        AND v.estado in ('INPROGRESS','SUSPENDIDO')
        and s.spriden_pidm = v.pidm and s.SPRIDEN_CHANGE_IND is null) a,
    mi_cuentas_gmail cg
    where cg.moodle_id(+) = a.rut 
    ) where moodle_id is null
    order by rut
