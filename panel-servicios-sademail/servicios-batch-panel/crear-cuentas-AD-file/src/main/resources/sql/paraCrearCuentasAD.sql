SELECT MOODLE_ID as SPRIDEN_ID, SPRIDEN_FIRST_NAME, SPRIDEN_MI, SPRIDEN_LAST_NAME
FROM (
    select BA.MOODLE_ID, AD.RUT, SP.SPRIDEN_FIRST_NAME, SP.SPRIDEN_MI, SP.SPRIDEN_LAST_NAME from (
            SELECT DISTINCT V.rut AS MOODLE_ID, V.prog AS CODIGO_BANNER
            FROM SWVALUM v
            WHERE v.PIDM > 0
            AND   to_number(v.term_code_eff||lpad(v.sorlcur_seqno,3,0)||lpad(v.sorlfos_seqno,3,0)) =(
                                                                            SELECT max(to_number(x.term_code_eff||lpad(x.sorlcur_seqno,3,0)||lpad(x.sorlfos_seqno,3,0)))
                                                                            FROM swvalum x
                                                                            WHERE x.pidm = v.pidm                                                        
                                                                            )
            AND v.nivel not in ('UG')
            AND v.estado in ('INPROGRESS','SUSPENDIDO') ) BA,
        ad_nombres_cuenta AD, SPRIDEN SP
    WHERE SP.SPRIDEN_ID = BA.MOODLE_ID AND
    BA.MOODLE_ID=AD.RUT(+)
) X WHERE RUT IS NULL    
