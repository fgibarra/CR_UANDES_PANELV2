SELECT DISTINCT V.rut AS MOODLE_ID, V.prog AS CODIGO_BANNER
FROM SWVALUM v
WHERE v.PIDM > 0
AND   to_number(v.term_code_eff||lpad(v.sorlcur_seqno,3,0)||lpad(v.sorlfos_seqno,3,0)) =(
                                                                SELECT max(to_number(x.term_code_eff||lpad(x.sorlcur_seqno,3,0)||lpad(x.sorlfos_seqno,3,0)))
                                                                FROM swvalum x
                                                                WHERE x.pidm = v.pidm                                                        
                                                                )
 
AND v.estado in ('INPROGRESS','SUSPENDIDO')
