SELECT 
             SPRIDEN_PIDM,
             SPRIDEN_ID, 
             SPRIDEN_LAST_NAME, 
             SPRIDEN_FIRST_NAME,
             SPRIDEN_MI
              from SGBSTDN,SPRIDEN
             where 
                 SPRIDEN_PIDM = SGBSTDN_PIDM 
             and SPRIDEN_CHANGE_IND is null
             and SGBSTDN_STST_CODE = 'AS'
            and  SGBSTDN_TERM_CODE_ADMIT  IN (:#in:periodo)
            and   (SGBSTDN_TERM_CODE_EFF = SGBSTDN_TERM_CODE_ADMIT)
            and   (SGBSTDN_ADMT_CODE <> 'CI') and (SGBSTDN_ADMT_CODE <> 'CB') 
            and   (SGBSTDN_STYP_CODE <> 'D' )
            
            order by SPRIDEN_PIDM