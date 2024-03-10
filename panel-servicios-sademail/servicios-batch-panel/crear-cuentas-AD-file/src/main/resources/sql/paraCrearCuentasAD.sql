with faltan as (
-- posgrado que estan en BDC_USUARIO_MIlLLENIUM sin cuenta AD
SELECT DISTINCT V.rut
            FROM SWVALUM v,bdc_usuario_millenium u
            WHERE v.PIDM > 0
            AND   to_number(v.term_code_eff||lpad(v.sorlcur_seqno,3,0)||lpad(v.sorlfos_seqno,3,0)) =(
                                                                            SELECT max(to_number(x.term_code_eff||lpad(x.sorlcur_seqno,3,0)||lpad(x.sorlfos_seqno,3,0)))
                                                                            FROM swvalum x
                                                                            WHERE x.pidm = v.pidm                                                        
                                                                            )
            AND v.nivel not in ('UG')
            AND v.estado in ('INPROGRESS','SUSPENDIDO')
            and u.spriden_id = v.rut  and u.userid_alma is null
union             
select DISTINCT V.rut
-- posgrado que no estan en BDC
            FROM SWVALUM v
            WHERE v.PIDM > 0
            AND   to_number(v.term_code_eff||lpad(v.sorlcur_seqno,3,0)||lpad(v.sorlfos_seqno,3,0)) =(
                                                                            SELECT max(to_number(x.term_code_eff||lpad(x.sorlcur_seqno,3,0)||lpad(x.sorlfos_seqno,3,0)))
                                                                            FROM swvalum x
                                                                            WHERE x.pidm = v.pidm                                                        
                                                                            )
            AND v.nivel not in ('UG')
            AND v.estado in ('INPROGRESS','SUSPENDIDO')
            and rut not in (select spriden_id as rut from bdc_usuario_millenium u)
)
SELECT SPRIDEN_ID, SPRIDEN_FIRST_NAME, SPRIDEN_MI, SPRIDEN_LAST_NAME
FROM SPRIDEN SP, FALTAN fa
    WHERE SP.SPRIDEN_ID  =fa.RUT and spriden_change_ind is null
order by spriden_id

