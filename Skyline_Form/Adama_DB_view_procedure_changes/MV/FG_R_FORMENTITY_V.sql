CREATE MATERIALIZED VIEW FG_R_FORMENTITY_V
REFRESH FORCE ON DEMAND
AS
SELECT FE."ID",FE."FORMCODE",FE."ACTIVE",FE."DESCRIPTION",FE."FORM_TYPE",FE."TITLE",FE."SUBTITLE",FE."NUMBEROFORDER",FE."ENTITYTYPE",FE."ENTITYIMPCODE",FE."ENTITYIMPCLASS",FE."ENTITYIMPINIT",FE."COMMENTS",FE."DB_NAME",FE."FS",FE."FS_GAP",FE."PREVENTSAVE",FE."CATALOGITEM",FE."DEFAULTVALUE",FE."PARENTELEMENT",FE."HIDESCRIPT",FE."HIDEALWAYS",FE."DISABLESCRIPT",FE."DISABLEALWAYS",FE."MANDATORY",FE."MANDATORYALWAYS",FE."MANDATORYSCRIPT",FE."TABLENAME",
       V.VIEW_CODE,
       V.COLUMN_LIST,
       V."VIEW SNAPSHOT DATE",
       SYSDATE "MV SNAPSHOT DATE"
FROM (
select  td."ID" ,t."FORMCODE",t.active,t.description,t.form_type,t.title,t.subtitle,td."NUMBEROFORDER",
        td."ENTITYTYPE",td."ENTITYIMPCODE",td."ENTITYIMPCLASS",replace(td."ENTITYIMPINIT",',',', ') "ENTITYIMPINIT",
        td."COMMENTS",'SKYLINE_FORM' as DB_NAME, td.fs, td.fs_gap,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'catalogItem','') AS catalogItem,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'defaultValue','') AS defaultValue,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'parentElement','') AS parentElement,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'preventSave','') AS preventSave,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'hideScript','') AS hideScript,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'hideAlways','') AS hideAlways,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'disableScript','') AS disableScript,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'disableAlways','') AS disableAlways,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'mandatory','') AS mandatory,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'mandatoryAlways','') AS mandatoryAlways,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'mandatoryScript','') AS mandatoryScript,
        fg_get_value_from_entity_int(t."FORMCODE",td."ENTITYIMPCODE",'tableName','') AS tableName
from fg_formentity td,
     fg_form t
where t.FORMCODE =  td.FORMCODE
 )FE,
FG_R_SYSTEM_VIEW_V V
WHERE LOWER(FE.tableName)  = LOWER(V.VIEW_NAME(+));
