CREATE MATERIALIZED VIEW FG_R_FORMENTITY_CAT_REF_V
REFRESH FORCE ON DEMAND
AS
SELECT T1.*, 'select ' ||  SUBSTR(catalogitem, INSTR(catalogitem,'.') + 1) || ' from ' || TABLENAME || ' where ''@ID@'' = ''FORMID'' ' as SQL_EXPRESSION
FROM (
SELECT T."ID",T."FORMCODE",T."ACTIVE",T."DESCRIPTION",
T."FORM_TYPE",T."TITLE",T."SUBTITLE",T."NUMBEROFORDER",
T."ENTITYTYPE",T."ENTITYIMPCODE",T."ENTITYIMPCLASS",T."ENTITYIMPINIT",
T."COMMENTS",T."DB_NAME",T."FS",T."FS_GAP",T."PREVENTSAVE",T."CATALOGITEM",
T."DEFAULTVALUE",T."PARENTELEMENT",T."HIDESCRIPT",T."HIDEALWAYS",T."DISABLESCRIPT",
T."DISABLEALWAYS",T."MANDATORY",T."MANDATORYALWAYS",T."MANDATORYSCRIPT",NVL(T."TABLENAME",C."TABLENAME") AS "TABLENAME",
NVL(T."VIEW_CODE",C."VIEW_CODE") AS "VIEW_CODE" ,NVL(T."COLUMN_LIST",C."COLUMN_LIST") "COLUMN_LIST", T."VIEW SNAPSHOT DATE",T."MV SNAPSHOT DATE"


FROM FG_R_FORMENTITY_V T, --FG_FORMENTITY
     (SELECT * FROM FG_R_FORMENTITY_V V2 WHERE V2.ENTITYIMPCLASS = 'CatalogDBTableImp') C
WHERE UPPER(T.CATALOGITEM) LIKE UPPER(C.ENTITYIMPCODE(+) || '.%')
AND   T.FORMCODE = C.FORMCODE(+)
) t1
WHERE ENTITYTYPE <> 'Catalog'
AND TABLENAME IS NOT NULL;
