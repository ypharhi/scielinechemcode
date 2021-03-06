CREATE OR REPLACE VIEW FG_S_EXPERIMENT_ALL_V AS
select t."EXPERIMENT_ID",t."FORM_TEMP_ID",t."EXPERIMENT_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."CLONEBUTTON",t."YIELDUOM_ID",t."APPROVALTIMESTAMP",t."EXPERIMENTVERSION",t."DOCUMENTS",t."EXPERIMENTOVERVIEWRESULTS",t."EXPERIMENTMAINNAME",t."SUBSEQUENRESULTS",t."LINKSAMPLE",t."SMARTSELECTLIST",t."INSTRUMENTS",t."EXPERIMENTTYPE_ID",t."WORKUPS",t."CALCULATIONBUTTON",t."COMMENTS",t."ADDSTREAMBUTTON",t."ISLOCKSPREADSHEET",t."DIAGRAMIFRAME",t."RUNSTEPSUMMARYTABLE",t."RECIPELIST",t."YIELD",t."PRODUCTNAME_ID",t."SAFETYCOMMENTS",t."COLUMNS",t."COMPLETIONTIMESTAMP",t."SAMPLES",t."EXPERIMENTRESULTS",t."PERMOLEUOM_ID",t."EXPERIMENTSERIES",t."SAMPLELASTSELECTION",t."EXPCLONEMAINNAME",t."EXPERIMENTVIEW_ID",t."ACTUALSTARTDATE",t."COMPLETIONDATE",t."APPROVALDATE",t."EXPERIMENTNAME",t."ORIGINREQUESTID",t."COMMENTSHPLC",t."MANUALRESULTSTABLE",t."PLANNED_ACTUAL",t."MASSBALANCE3DATAPERRUN",t."SEARCHCOMPOSITIONVAL",t."COMPOSITIONTYPENAME",t."SEARCH_INSRUMENT_ID",t."WEBIXMASSBALANCETABLE3",t."PARAMETERS",t."RECIPE_ID",t."WEBIXFORMULCALC",t."REACTIONSUMRUNSASYNC",t."AUTH",t."SUBSUBPROJECT_ID",t."OWNER_ID",t."STBPLANTPSELECTION",t."SUBSEQUENTREQUEST",t."TPLASTSELECTION",t."EQUIPTPREPARATIONINSTRUCTION",t."TESTEDCOMPONENTS",t."TEMPERATUREGRADIENT",t."REACTIONSUMASYNCIMGACT",t."CHROMATOGRAMS",t."SELFTESTRESULTS",t."REACTIONSUMASYNCIMGPLN",t."VSMATERIALID",t."SELFTESTS",t."BMATERIALID",t."MATERIAL_ID",t."STANDARDS",t."CALCULATEMASSBALFIELDSBUTTON",t."EXTERNALCODE",t."SHOW",t."SPREADSHEETTEMPLATE_ID",t."COPYCOMPOSITION",t."ADDSTREAMBUTTON2",t."IMPORTEDEXTERNALCODE",t."HISTORICALDATABUTTON",t."SENDER",t."PROJECT_ID",t."MANUALRESULTSMS",t."ORIGINEXP_ID",t."TEMPLATE_ID",t."PROTOCOLTYPE_ID",t."FORMNUMBERID",t."ORIGINFORMULANTPROPREF",t."COLUMNSELECT",t."MOBILEPHASECOMPOSITION",t."STEPIFRAMES",t."CORROSIONDESCRIPTION",t."ENABLESPREADSHEET",t."BATCHSIZE",t."CALCULATEMASSBALFIELDSBUTTON2",t."ADDSTREAMBUTTON3",t."ESTIMATEDSTARTDATE",t."ADDITIONALEQUIP",t."TORETURN",t."ACTUALSTARTTIMESTAMP",t."MASSBALLANCETYPE_ID",t."TOTALCHEMICALYIELD",t."APPROVER_ID",t."AUTHZ",t."DISABLEDSTEPS",t."RESULTSTABLE",t."INSTRUMENTSTABLE",t."UNITS_ID",t."MATERIALS",t."REASONFORCHANGE",t."RESULTS",t."LASTMODIFDATE",t."EXPERIMENTGROUP",t."EXPERIMENTMAIN_ID",t."AIM",t."CONDITIONCALCULATION",t."CHEMICALYIELDUOM_ID",t."RESCALCULATION",t."GROSS_W_UOM_ID",t."STANDARDINSTRUCTION",t."COMMENTSGC",t."CORROSIONSOLUTION",t."CREATECOAPRINTFORM",t."BATCHES",t."SPREADSHEETEXCEL",t."IMPORTEDCOMPOSITIONTABLE",t."COMPOSITIONPARENT_ID",t."SAMPLETABLEEDIT",t."MASSBALANCE2DATAPERRUN",t."DENSITY",t."DIAGRAM",t."ISNEWSTEPALLOWHOLDER",t."GENERATORBUTTON",t."LIMITINGREACTANTMOLES",t."GROUPSCREW",t."LABORATORY_ID",t."LITDENSITYUOMID",t."CHARACTERIZEDSAMPLE",t."CREATOR_ID",t."CONCLUSSION",t."ACTION",t."CALCULATEBUTTON",t."WEBIXANALYTTABLE",t."HDNOLDDEFAULTMASSBALANCE",t."EXPERIMENTSUMMARYBUTTON",t."WEBIXMASSBALANCETABLE2",t."MASSBALANCEDATAPERRUN",t."ISCOMPOSITION",t."STEPS",t."REACTANTMOLESUOM_ID",t."DESCRIPTION",t."REQUEST",t."SITE_ID",t."SUBSEQUENTRESULTS",t."SUBPROJECT_ID",t."PRODUCTMW",t."PRODUCTMWUOM_ID",t."EXPERIMENTSUMMARY",t."EXPERIMENTGROUP_ID",t."WEBIXMASSBALANCETABLE",t."IMPORTEDRECIPE_ID",t."CALCULATEMASSBALFIELDSBUTTON3",t."SERIALNUMBER",t."DOCLEARCONNECTION",t."CREATIONDATETIME",t."USERSCREW",t."FAMILIARITY",t."LASTSTATUS_ID",t."PROCEDURE",t."LAST_OWNER_ID",t."EQUIVALENTPERMOLE",t."SUBSEQUENTREQ",t."EXPERIMENTRESULTSTABLEDATA",t."STATUS_ID",t."RESULTSCOMMENT",t."REACTIONTABLE",t."LITDENSITYUOM",t."SEARCHBY",t."BATCHSIZE_UOM",t."SELFTESTDEFAULTDATAHOLDER",t."EXPRUNPLANNINGTABLE",t."RECIPEFORMULATION_ID",t."PLANNEDCOMPOSITIONS",t."FORMULATIONTYPE_ID",t."HDNEXPERIMDEFAULTMASSBALANCE",t."SEARCH_MATERIAL_ID",t."ISENABLESPREADSHEET"
--t.* end! edit only the code below...
,expstatus.EXPERIMENTSTATUSNAME,typ.EXPERIMENTTYPENAME,us.USERNAME,
nvl(t.SUBSUBPROJECT_ID,t.SUBPROJECT_ID) as PARENT_SUB_PROJECT_ID, -- PARENT_SUB_PROJECT_ID is sub project or sub sub project id
( select distinct count(s.step_id) over (partition by s.EXPERIMENT_ID) from fg_s_step_v s where t.experiment_id = s.EXPERIMENT_ID ) as "NUMBER_OF_STEPS",
pt.PROTOCOLTYPENAME,explaststatus.EXPERIMENTSTATUSNAME as "LASTSTATUSNAME",/*typ.EXPERIMENTVIEW_ID, - yp 11032018 is part of the screen */ se."EXPERIMENTSERIESNAME",se.EXPERIMENTSERIES_ID,
SSP.SUBSUBPROJECTNAME,
SP.SUBPROJECTNAME,
SP.PROJECTNAME,
sp.formNUMBERId as SUBPROJECTNUMBER,
sp.PROJECTNUMBER,
ssp.FORMNUMBERID as SubSubProjectNumber,
nvl(sp.ISENABLESPREADSHEET,'No') SP_ISENABLESPREADSHEET,
ft.COMPOSITIONTYPE,
ft.FormulationTypeName,
SP.FORMULATIONTYPE_ID AS PROJECT_FORMULATIONTYPE_ID,
(select decode(count(*),0,0,1) from fg_s_exprunplanning_all_v r where r.EXPERIMENTID = t.experiment_id and r.RUNSTATUSNAME <> 'Planned') as ISRUNSTARTED
 from FG_S_EXPERIMENT_V t, fg_s_experimentstatus_all_v expstatus, fg_s_experimenttype_all_v typ, fg_s_user_v us, fg_s_protocoltype_all_v pt,fg_s_experimentstatus_all_v explaststatus
 , fg_i_series_experiment_v se,
 FG_S_SUBPROJECT_all_V SP,
           FG_S_SUBSUBPROJECT_V SSP,
           fg_s_formulationtype_v ft
          -- ,FG_S_PROJECT_V P
      where t.STATUS_ID = expstatus.EXPERIMENTSTATUS_ID (+)
      and t.LASTSTATUS_ID = explaststatus.EXPERIMENTSTATUS_ID (+)
      and t.EXPERIMENTTYPE_ID = typ.EXPERIMENTTYPE_ID(+)
      and t.OWNER_ID = us.USER_ID(+)
      and t.PROTOCOLTYPE_ID = pt.PROTOCOLTYPE_ID(+)
      and to_char(t.originformulantpropref) = to_char(se.FORMULATIONPROPREF_ID(+))
      and   T.SUBSUBPROJECT_ID = SSP.SUBSUBPROJECT_ID(+)
      and   T.SUBPROJECT_ID = SP.SUBPROJECT_ID(+)
      and t.FORMULATIONTYPE_ID = ft.formulationtype_id(+)
     -- and   T.PROJECT_ID = P.PROJECT_ID(+);
