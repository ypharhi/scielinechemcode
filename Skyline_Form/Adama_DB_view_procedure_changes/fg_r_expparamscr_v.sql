create or replace view fg_r_expparamscr_v as
select fg_get_num_display(t.TIME,0,3) || decode(t.TIME,null,'',' [' || fg_get_uom_display(t.TIME_UOM_ID) || ']') as "Time",
       fg_get_num_display(t.WEIGHT,0,3) as "Weight [gr]",
       fg_get_num_display(t.LOSS,0,3) as "Loss [gr]",
       fg_get_num_display(t.PERCENTAGELOSS,0,3) as "% Loss",
       fg_get_num_display(t.MEANCORROSIONRATE,0,3) as "Mean Corrosion Rate [mm/yr]",
       case
         when t.REPRESENTITIVERESULT = 1 then
           'yes'
         else
           'no'
       end as "Representative Result",
       t.PARENTID as EXPERIMENT_ID
from FG_S_EXPPARAMSCRREF_ALL_V t -- fg_s_ExpParamsCrRef_DT_v
where 1=1
and t.SESSIONID is null
and t.ACTIVE = 1
