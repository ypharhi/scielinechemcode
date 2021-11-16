-----------------------------------------------
-- Export file for user SKYLINE_FORM_MIN     --
-- Created by comply on 16/11/2021, 17:52:42 --
-----------------------------------------------

spool db_min_obj.log

prompt
prompt Creating table DR$SEARCH_IDX$I
prompt ==============================
prompt
create table DR$SEARCH_IDX$I
(
  token_text  VARCHAR2(64) not null,
  token_type  NUMBER(3) not null,
  token_first NUMBER(10) not null,
  token_last  NUMBER(10) not null,
  token_count NUMBER(10) not null,
  token_info  BLOB
)
;
create index DR$SEARCH_IDX$X on DR$SEARCH_IDX$I (TOKEN_TEXT, TOKEN_TYPE, TOKEN_FIRST, TOKEN_LAST, TOKEN_COUNT)
  compress 2;

prompt
prompt Creating table DR$SEARCH_IDX$K
prompt ==============================
prompt
create table DR$SEARCH_IDX$K
(
  docid   NUMBER(38),
  textkey ROWID not null,
  primary key (TEXTKEY)
)
organization index;

prompt
prompt Creating table DR$SEARCH_IDX$N
prompt ==============================
prompt
create table DR$SEARCH_IDX$N
(
  nlt_docid NUMBER(38) not null,
  nlt_mark  CHAR(1) not null,
  primary key (NLT_DOCID)
)
organization index;

prompt
prompt Creating table DR$SEARCH_IDX$R
prompt ==============================
prompt
create table DR$SEARCH_IDX$R
(
  row_no NUMBER(3),
  data   BLOB
)
;

prompt
prompt Creating table D_PIVOT_TMP
prompt ==========================
prompt
create table D_PIVOT_TMP
(
  row_id             NUMBER,
  row_id_order       NUMBER,
  col_id             VARCHAR2(300),
  col_name           VARCHAR2(300),
  result             VARCHAR2(300),
  user_id            NUMBER,
  session_id         VARCHAR2(300),
  time_stamp_cleanup DATE,
  row_name           VARCHAR2(500)
)
;

prompt
prompt Creating table FG_ACCESS_LOG
prompt ============================
prompt
create table FG_ACCESS_LOG
(
  user_id     NUMBER,
  station_ip  VARCHAR2(50),
  time_stamp  DATE,
  success_ind VARCHAR2(3),
  ts_in_info  VARCHAR2(500)
)
;
comment on column FG_ACCESS_LOG.ts_in_info
  is '+ for test cache';

prompt
prompt Creating table FG_ACTIVITY_LOG
prompt ==============================
prompt
create table FG_ACTIVITY_LOG
(
  formid          VARCHAR2(500),
  timestamp       DATE,
  user_id         VARCHAR2(500),
  activitylogtype VARCHAR2(500),
  changevalue     VARCHAR2(1000),
  status          VARCHAR2(100),
  location        VARCHAR2(4000),
  startdate       DATE,
  enddate         DATE,
  additionalinfo  VARCHAR2(4000),
  leveltype       VARCHAR2(500),
  stacktrace      VARCHAR2(4000),
  timestamp3      TIMESTAMP(6) default systimestamp,
  comments        CLOB
)
;
comment on column FG_ACTIVITY_LOG.changevalue
  is 'not needed';
comment on column FG_ACTIVITY_LOG.status
  is 'not needed';
comment on column FG_ACTIVITY_LOG.location
  is 'not needed';
comment on column FG_ACTIVITY_LOG.startdate
  is 'not needed';
comment on column FG_ACTIVITY_LOG.enddate
  is 'not needed';
comment on column FG_ACTIVITY_LOG.additionalinfo
  is 'json';

prompt
prompt Creating table FG_ACTIVITY_LOG_HST
prompt ==================================
prompt
create table FG_ACTIVITY_LOG_HST
(
  formid          VARCHAR2(500),
  timestamp       DATE,
  user_id         VARCHAR2(500),
  activitylogtype VARCHAR2(500),
  changevalue     VARCHAR2(1000),
  status          VARCHAR2(100),
  location        VARCHAR2(4000),
  startdate       DATE,
  enddate         DATE,
  additionalinfo  VARCHAR2(4000),
  leveltype       VARCHAR2(500),
  stacktrace      VARCHAR2(4000),
  timestamp3      TIMESTAMP(6),
  comments        CLOB
)
;

prompt
prompt Creating table FG_AUTHEN_REPORT_V
prompt =================================
prompt
create table FG_AUTHEN_REPORT_V
(
  user_id NUMBER,
  product VARCHAR2(50),
  name    VARCHAR2(50),
  site    VARCHAR2(50)
)
;

prompt
prompt Creating table FG_CHEM_CAS_API_LOG
prompt ==================================
prompt
create table FG_CHEM_CAS_API_LOG
(
  material_form_id NUMBER,
  user_id          NUMBER,
  time_stamp       DATE,
  result           CLOB,
  resultid         VARCHAR2(500)
)
;

prompt
prompt Creating table FG_CHEM_DELETED_SEARCH
prompt =====================================
prompt
create table FG_CHEM_DELETED_SEARCH
(
  cd_id                NUMBER(10) not null,
  cd_structure         BLOB not null,
  cd_smiles            VARCHAR2(4000),
  cd_formula           VARCHAR2(100),
  cd_sortable_formula  VARCHAR2(500),
  cd_molweight         FLOAT,
  cd_hash              NUMBER(10) not null,
  cd_flags             VARCHAR2(20),
  cd_timestamp         DATE not null,
  cd_pre_calculated    NUMBER(1) default 0 not null,
  cd_taut_hash         NUMBER(10) not null,
  cd_taut_frag_hash    VARCHAR2(4000),
  cd_screen_descriptor VARCHAR2(4000),
  cd_fp1               NUMBER(10) not null,
  cd_fp2               NUMBER(10) not null,
  cd_fp3               NUMBER(10) not null,
  cd_fp4               NUMBER(10) not null,
  cd_fp5               NUMBER(10) not null,
  cd_fp6               NUMBER(10) not null,
  cd_fp7               NUMBER(10) not null,
  cd_fp8               NUMBER(10) not null,
  cd_fp9               NUMBER(10) not null,
  cd_fp10              NUMBER(10) not null,
  cd_fp11              NUMBER(10) not null,
  cd_fp12              NUMBER(10) not null,
  cd_fp13              NUMBER(10) not null,
  cd_fp14              NUMBER(10) not null,
  cd_fp15              NUMBER(10) not null,
  cd_fp16              NUMBER(10) not null,
  formid               VARCHAR2(100),
  fullformcode         VARCHAR2(100),
  moltype              VARCHAR2(100),
  elementid            VARCHAR2(100)
)
;
alter table FG_CHEM_DELETED_SEARCH
  add primary key (CD_ID);
create index FG_CHEM_DELETED_SEARCH_FX on FG_CHEM_DELETED_SEARCH (CD_SORTABLE_FORMULA);
create index FG_CHEM_DELETED_SEARCH_HX on FG_CHEM_DELETED_SEARCH (CD_HASH);
create index FG_CHEM_DELETED_SEARCH_PX on FG_CHEM_DELETED_SEARCH (CD_PRE_CALCULATED);
create index FG_CHEM_DELETED_SEARCH_THX on FG_CHEM_DELETED_SEARCH (CD_TAUT_HASH);

prompt
prompt Creating table FG_CHEM_DELETED_SEARCH_UL
prompt ========================================
prompt
create table FG_CHEM_DELETED_SEARCH_UL
(
  update_id   NUMBER(10) not null,
  update_info VARCHAR2(120) not null,
  cache_id    VARCHAR2(32) not null
)
;
alter table FG_CHEM_DELETED_SEARCH_UL
  add primary key (UPDATE_ID);
create index FG_CHEM_DELETED_SEARCH_CX on FG_CHEM_DELETED_SEARCH_UL (CACHE_ID);

prompt
prompt Creating table FG_CHEM_DOC_SEARCH
prompt =================================
prompt
create table FG_CHEM_DOC_SEARCH
(
  cd_id                NUMBER(10) not null,
  cd_structure         BLOB not null,
  cd_smiles            VARCHAR2(4000),
  cd_formula           VARCHAR2(100),
  cd_sortable_formula  VARCHAR2(500),
  cd_molweight         FLOAT,
  cd_hash              NUMBER(10) not null,
  cd_flags             VARCHAR2(20),
  cd_timestamp         DATE not null,
  cd_pre_calculated    NUMBER(1) default 0 not null,
  cd_taut_hash         NUMBER(10) not null,
  cd_taut_frag_hash    VARCHAR2(4000),
  cd_screen_descriptor VARCHAR2(4000),
  cd_fp1               NUMBER(10) not null,
  cd_fp2               NUMBER(10) not null,
  cd_fp3               NUMBER(10) not null,
  cd_fp4               NUMBER(10) not null,
  cd_fp5               NUMBER(10) not null,
  cd_fp6               NUMBER(10) not null,
  cd_fp7               NUMBER(10) not null,
  cd_fp8               NUMBER(10) not null,
  cd_fp9               NUMBER(10) not null,
  cd_fp10              NUMBER(10) not null,
  cd_fp11              NUMBER(10) not null,
  cd_fp12              NUMBER(10) not null,
  cd_fp13              NUMBER(10) not null,
  cd_fp14              NUMBER(10) not null,
  cd_fp15              NUMBER(10) not null,
  cd_fp16              NUMBER(10) not null
)
;
alter table FG_CHEM_DOC_SEARCH
  add primary key (CD_ID);
create index FG_CHEM_DOC_SEARCH_FX on FG_CHEM_DOC_SEARCH (CD_SORTABLE_FORMULA);
create index FG_CHEM_DOC_SEARCH_HX on FG_CHEM_DOC_SEARCH (CD_HASH);
create index FG_CHEM_DOC_SEARCH_PX on FG_CHEM_DOC_SEARCH (CD_PRE_CALCULATED);
create index FG_CHEM_DOC_SEARCH_THX on FG_CHEM_DOC_SEARCH (CD_TAUT_HASH);

prompt
prompt Creating table FG_CHEM_DOC_SEARCH_UL
prompt ====================================
prompt
create table FG_CHEM_DOC_SEARCH_UL
(
  update_id   NUMBER(10) not null,
  update_info VARCHAR2(120) not null,
  cache_id    VARCHAR2(32) not null
)
;
alter table FG_CHEM_DOC_SEARCH_UL
  add primary key (UPDATE_ID);
create index FG_CHEM_DOC_SEARCH_CX on FG_CHEM_DOC_SEARCH_UL (CACHE_ID);

prompt
prompt Creating table FG_CHEM_DOODLE_DATA
prompt ==================================
prompt
create table FG_CHEM_DOODLE_DATA
(
  parent_id              VARCHAR2(200) not null,
  mol_data               VARCHAR2(4000),
  smiles_data            VARCHAR2(4000),
  mol_attr               VARCHAR2(200),
  mol_type               VARCHAR2(1),
  mol_order              NUMBER,
  mol_img_url            VARCHAR2(500),
  inchi_data             VARCHAR2(4000),
  reaction_all_data      CLOB,
  mol_img_file_id        VARCHAR2(500),
  full_img_file_id       VARCHAR2(500),
  reaction_all_data_link VARCHAR2(100),
  mol_cml                VARCHAR2(100)
)
;

prompt
prompt Creating table FG_CHEM_SEARCH
prompt =============================
prompt
create table FG_CHEM_SEARCH
(
  cd_id                NUMBER(10) not null,
  cd_structure         BLOB not null,
  cd_smiles            VARCHAR2(4000),
  cd_formula           VARCHAR2(100),
  cd_sortable_formula  VARCHAR2(500),
  cd_molweight         FLOAT,
  cd_hash              NUMBER(10) not null,
  cd_flags             VARCHAR2(20),
  cd_timestamp         DATE not null,
  cd_pre_calculated    NUMBER(1) default 0 not null,
  cd_taut_hash         NUMBER(10) not null,
  cd_taut_frag_hash    VARCHAR2(4000),
  cd_screen_descriptor VARCHAR2(4000),
  cd_fp1               NUMBER(10) not null,
  cd_fp2               NUMBER(10) not null,
  cd_fp3               NUMBER(10) not null,
  cd_fp4               NUMBER(10) not null,
  cd_fp5               NUMBER(10) not null,
  cd_fp6               NUMBER(10) not null,
  cd_fp7               NUMBER(10) not null,
  cd_fp8               NUMBER(10) not null,
  cd_fp9               NUMBER(10) not null,
  cd_fp10              NUMBER(10) not null,
  cd_fp11              NUMBER(10) not null,
  cd_fp12              NUMBER(10) not null,
  cd_fp13              NUMBER(10) not null,
  cd_fp14              NUMBER(10) not null,
  cd_fp15              NUMBER(10) not null,
  cd_fp16              NUMBER(10) not null,
  formid               VARCHAR2(100),
  fullformcode         VARCHAR2(100),
  moltype              VARCHAR2(100),
  elementid            VARCHAR2(100)
)
;
alter table FG_CHEM_SEARCH
  add primary key (CD_ID);
create index FG_CHEM_SEARCH_FX on FG_CHEM_SEARCH (CD_SORTABLE_FORMULA);
create index FG_CHEM_SEARCH_HX on FG_CHEM_SEARCH (CD_HASH);
create index FG_CHEM_SEARCH_PX on FG_CHEM_SEARCH (CD_PRE_CALCULATED);
create index FG_CHEM_SEARCH_THX on FG_CHEM_SEARCH (CD_TAUT_HASH);

prompt
prompt Creating table FG_CHEM_SEARCH_UL
prompt ================================
prompt
create table FG_CHEM_SEARCH_UL
(
  update_id   NUMBER(10) not null,
  update_info VARCHAR2(120) not null,
  cache_id    VARCHAR2(32) not null
)
;
alter table FG_CHEM_SEARCH_UL
  add primary key (UPDATE_ID);
create index FG_CHEM_SEARCH_CX on FG_CHEM_SEARCH_UL (CACHE_ID);

prompt
prompt Creating table FG_CLOB_FILES
prompt ============================
prompt
create table FG_CLOB_FILES
(
  file_id           VARCHAR2(200) not null,
  file_name         VARCHAR2(500),
  file_content      CLOB,
  content_type      VARCHAR2(500),
  reference_element VARCHAR2(200)
)
;
create unique index IDX_CLOB_FILES on FG_CLOB_FILES (FILE_ID);

prompt
prompt Creating table FG_DEBUG
prompt =======================
prompt
create table FG_DEBUG
(
  comments     CLOB,
  commenttime  DATE default sysdate,
  comment_info VARCHAR2(1000)
)
;

prompt
prompt Creating table FG_DIAGRAM
prompt =========================
prompt
create table FG_DIAGRAM
(
  element_id   VARCHAR2(200) not null,
  element_name VARCHAR2(500),
  content      CLOB,
  content_type VARCHAR2(500),
  image_id     VARCHAR2(200)
)
;
comment on column FG_DIAGRAM.image_id
  is 'a pointer to the fg_files table';

prompt
prompt Creating table FG_DYNAMICPARAMS
prompt ===============================
prompt
create table FG_DYNAMICPARAMS
(
  id        NUMBER,
  order_    NUMBER,
  label     VARCHAR2(500),
  parent_id VARCHAR2(500),
  active    VARCHAR2(500)
)
;

prompt
prompt Creating table FG_EXCEL_USER_TEMP_DATA
prompt ======================================
prompt
create table FG_EXCEL_USER_TEMP_DATA
(
  formid    VARCHAR2(500),
  timestamp DATE,
  user_id   VARCHAR2(500),
  domid     VARCHAR2(500),
  value     CLOB
)
;

prompt
prompt Creating table FG_EXPREPORT_MATERIALREF_TMP
prompt ===========================================
prompt
create table FG_EXPREPORT_MATERIALREF_TMP
(
  materialref_id       NUMBER,
  form_temp_id         VARCHAR2(203),
  materialref_objidval VARCHAR2(671),
  formid               VARCHAR2(100) not null,
  timestamp            DATE,
  creation_date        DATE,
  cloneid              VARCHAR2(100),
  templateflag         VARCHAR2(100),
  change_by            VARCHAR2(100),
  created_by           VARCHAR2(100),
  sessionid            VARCHAR2(100),
  active               NUMBER,
  formcode_entity      VARCHAR2(100),
  formcode             VARCHAR2(100),
  yielduom_id          VARCHAR2(500),
  mass                 VARCHAR2(500),
  mole                 VARCHAR2(500),
  limitingagent        VARCHAR2(500),
  quantityuom_id       VARCHAR2(500),
  comments             VARCHAR2(4000),
  actualpurity         VARCHAR2(500),
  concinreactionmass   VARCHAR2(500),
  volumerate           VARCHAR2(500),
  massrateppuom_id     VARCHAR2(500),
  yield                VARCHAR2(500),
  volume               VARCHAR2(500),
  materialrefname      VARCHAR2(500),
  batch_id             VARCHAR2(500),
  catalyst             VARCHAR2(500),
  chemdoodle           VARCHAR2(500),
  materialnameinf      VARCHAR2(500),
  densityuom_id_inf    VARCHAR2(500),
  reactantmaterial_id  VARCHAR2(500),
  auth                 VARCHAR2(500),
  invitemmaterial_id   VARCHAR2(500),
  equivalent           VARCHAR2(500),
  quantratiototal      VARCHAR2(500),
  voluom_id            VARCHAR2(500),
  formulainf           VARCHAR2(500),
  parentid             VARCHAR2(500),
  batchinf             VARCHAR2(500),
  mwinf                VARCHAR2(500),
  watercontent         VARCHAR2(500),
  resultid_holder      VARCHAR2(500),
  quantityrate         VARCHAR2(500),
  molerate             VARCHAR2(500),
  alias_               VARCHAR2(500),
  concentrationmole    VARCHAR2(500),
  purityuom_id_inf     VARCHAR2(500),
  totalvolume          VARCHAR2(500),
  smilesinf            VARCHAR2(500),
  casnamberinf         VARCHAR2(500),
  ratio                VARCHAR2(500),
  productdensity       VARCHAR2(500),
  quantityrate_uom     VARCHAR2(500),
  ratiotype_id         VARCHAR2(500),
  iupacnameinf         VARCHAR2(500),
  actpurityuom_id      VARCHAR2(500),
  sample_id            VARCHAR2(500),
  massrateforpp        VARCHAR2(500),
  mwuom_id_inf         VARCHAR2(500),
  densityinf           VARCHAR2(500),
  watercontuom_id      VARCHAR2(500),
  moleuom_id           VARCHAR2(500),
  molerateuom_id       VARCHAR2(500),
  volrateuom_id        VARCHAR2(500),
  quantity             VARCHAR2(500),
  purityinf            VARCHAR2(500),
  tabletype            VARCHAR2(500),
  massuom_id           VARCHAR2(500),
  islimited            VARCHAR2(500),
  totalquantity        VARCHAR2(500),
  volratiototal        VARCHAR2(500),
  casnameinf           VARCHAR2(500),
  synonymsinf          VARCHAR2(4000),
  originformid         VARCHAR2(500),
  waterconuomname      VARCHAR2(4000),
  mwuomname            VARCHAR2(4000),
  densityuomname       VARCHAR2(4000),
  actpurityuomname     VARCHAR2(4000),
  moleuomname          VARCHAR2(4000),
  purityuomname        VARCHAR2(4000),
  voluomname           VARCHAR2(4000),
  quantityuomname      VARCHAR2(4000),
  massuomname          VARCHAR2(4000),
  yielduomname         VARCHAR2(4000),
  invitemmaterialname  VARCHAR2(500),
  structure            VARCHAR2(500),
  casnumber            VARCHAR2(500),
  casname              VARCHAR2(500),
  synonyms             VARCHAR2(4000),
  density              VARCHAR2(500),
  iupacname            VARCHAR2(500),
  mw                   VARCHAR2(500),
  mw_uom_id            VARCHAR2(500),
  density_uom_id       VARCHAR2(500),
  chemicalformula      VARCHAR2(500),
  smiles               VARCHAR2(4000),
  invitembatchname     VARCHAR2(500),
  purity               VARCHAR2(500),
  purityuom_id         VARCHAR2(500),
  isstandart           VARCHAR2(500),
  experiment_id        VARCHAR2(500),
  step_id              NUMBER,
  experimentstatusname VARCHAR2(500),
  stepstatusname       VARCHAR2(500),
  isplannedsnapshout   NUMBER,
  inchi                VARCHAR2(4000),
  mol                  VARCHAR2(4000),
  project_id           VARCHAR2(500),
  material_objidval    VARCHAR2(671),
  step_objidval        VARCHAR2(671),
  stepnumber           VARCHAR2(500),
  stepname             VARCHAR2(500),
  protocoltypename     VARCHAR2(500),
  preparation_run      VARCHAR2(500),
  runnumberdisplay     VARCHAR2(4000),
  stepformnumberid     VARCHAR2(500),
  runnumber            VARCHAR2(500),
  expformcode          VARCHAR2(100),
  statekey             VARCHAR2(500),
  row_timestamp        DATE default sysdate
)
;

prompt
prompt Creating table FG_FAVORITE
prompt ==========================
prompt
create table FG_FAVORITE
(
  object_id  VARCHAR2(100) not null,
  creator_id VARCHAR2(100) not null
)
;

prompt
prompt Creating table FG_FILES
prompt =======================
prompt
create table FG_FILES
(
  file_id         VARCHAR2(200) not null,
  file_name       VARCHAR2(500),
  file_content    BLOB,
  content_type    VARCHAR2(500),
  reference_form  VARCHAR2(100),
  timestamp       DATE default sysdate,
  tmp_file        NUMBER default 0,
  file_display_id VARCHAR2(200),
  file_chem_id    VARCHAR2(200)
)
;
comment on column FG_FILES.reference_form
  is 'form that used to attach current image';
comment on column FG_FILES.timestamp
  is 'creation date';
comment on column FG_FILES.file_display_id
  is 'related file_id for preview';
comment on column FG_FILES.file_chem_id
  is 'related to FG_CHEM_DOC_SEARCH id';
create unique index IDX_FILES on FG_FILES (FILE_ID);

prompt
prompt Creating table FG_FILES_SRC
prompt ===========================
prompt
create table FG_FILES_SRC
(
  file_id      VARCHAR2(200) not null,
  file_name    VARCHAR2(500),
  file_content BLOB,
  content_type VARCHAR2(500),
  timestamp    DATE,
  parentid     VARCHAR2(200),
  cd_smiles    VARCHAR2(4000)
)
;

prompt
prompt Creating table FG_FORM
prompt ======================
prompt
create table FG_FORM
(
  id              NUMBER,
  formcode        VARCHAR2(100),
  description     VARCHAR2(4000),
  active          VARCHAR2(100),
  form_type       VARCHAR2(100),
  title           VARCHAR2(100),
  subtitle        VARCHAR2(500),
  use_as_template NUMBER default 0,
  group_name      VARCHAR2(100),
  numberoforder   NUMBER default 0,
  formcode_entity VARCHAR2(100),
  ignore_nav      VARCHAR2(100) default 0,
  usecache        VARCHAR2(100) default 0,
  change_date     DATE default SYSDATE
)
;
comment on column FG_FORM.form_type
  is 'result / report';
comment on column FG_FORM.use_as_template
  is 'one template per formtype';
comment on column FG_FORM.formcode_entity
  is 'the form code we save in the DB => FORMCODE is define the display, FORMCODE_ENTITY define the real entity';
comment on column FG_FORM.ignore_nav
  is 'ignore navigation (prevent push into the session stack)';
alter table FG_FORM
  add constraint FORMCODEUNIQUE unique (FORMCODE);
create unique index FORMCODE_INSENSITIVE_UNIQUE on FG_FORM (UPPER(FORMCODE));

prompt
prompt Creating table FG_FORMADDITIONALDATA
prompt ====================================
prompt
create table FG_FORMADDITIONALDATA
(
  id            NUMBER,
  parentid      VARCHAR2(500) default -1,
  entityimpcode VARCHAR2(500),
  value         VARCHAR2(500),
  config_id     VARCHAR2(500),
  formcode      VARCHAR2(500),
  info          VARCHAR2(500)
)
;

prompt
prompt Creating table FG_FORMADDITIONALDATA_HST
prompt ========================================
prompt
create table FG_FORMADDITIONALDATA_HST
(
  id            NUMBER,
  parentid      VARCHAR2(500),
  entityimpcode VARCHAR2(500),
  value         VARCHAR2(500),
  config_id     VARCHAR2(500),
  formcode      VARCHAR2(500),
  info          VARCHAR2(500),
  change_date   DATE
)
;

prompt
prompt Creating table FG_FORMELEMENTINFOATMETA_TMP
prompt ===========================================
prompt
create table FG_FORMELEMENTINFOATMETA_TMP
(
  formcode            VARCHAR2(100),
  entityimpcode       VARCHAR2(100),
  elementclass        VARCHAR2(100),
  displaylabel        VARCHAR2(500),
  isparentpathid      NUMBER(1),
  additionaldata      NUMBER(1),
  ishidden            NUMBER(1),
  issearchidholder    NUMBER(1),
  formcodeentitylabel VARCHAR2(100),
  formcodetyplabel    VARCHAR2(100),
  islistid            NUMBER(1),
  issearchelement     NUMBER(1),
  datatype            VARCHAR2(100),
  datatype_info       VARCHAR2(100)
)
;
comment on column FG_FORMELEMENTINFOATMETA_TMP.displaylabel
  is 'the entityimpcode label - taken from the formentity label configuration';
comment on column FG_FORMELEMENTINFOATMETA_TMP.issearchidholder
  is 'form builder config for non id elements holding an ID (for example when we put in text element id and we not using ddl because of very long lists)';
comment on column FG_FORMELEMENTINFOATMETA_TMP.formcodeentitylabel
  is 'form code entity namr taken from the lables [taken from the form.properties file]';
comment on column FG_FORMELEMENTINFOATMETA_TMP.formcodetyplabel
  is 'from code type for the forms that are mapped to different forms (like the formcode ExperimentAn which mapped to Experiment and the type is Analyitical)  [taken from the form.properties label file]';
comment on column FG_FORMELEMENTINFOATMETA_TMP.issearchelement
  is '1 - should be include in search screen , 0 - not include Note: the place we config it is in the class ElementInfoAuditTrailMeta';

prompt
prompt Creating table FG_FORMENTITY
prompt ============================
prompt
create table FG_FORMENTITY
(
  id             NUMBER not null,
  formcode       VARCHAR2(100) not null,
  numberoforder  NUMBER not null,
  entitytype     VARCHAR2(100) not null,
  entityimpcode  VARCHAR2(100) not null,
  entityimpclass VARCHAR2(100) not null,
  entityimpinit  VARCHAR2(4000) not null,
  comments       VARCHAR2(4000),
  fs             VARCHAR2(4000),
  fs_gap         VARCHAR2(4000),
  change_date    DATE default sysdate
)
;
comment on column FG_FORMENTITY.comments
  is 'developer / form builder user comment';
comment on column FG_FORMENTITY.fs
  is 'FS requirement';
comment on column FG_FORMENTITY.fs_gap
  is 'empty when no gap between FS requirement and the implementation';
alter table FG_FORMENTITY
  add constraint PK_FG_FORMENTITY primary key (ID);
alter table FG_FORMENTITY
  add constraint UK_FG_FORMENTITY unique (ENTITYIMPCODE, FORMCODE);
create unique index FORMENTITY_INSENSITIVE_UNIQUE on FG_FORMENTITY (UPPER(FORMCODE), UPPER(ENTITYIMPCODE));

prompt
prompt Creating table FG_FORMENTITY_HST
prompt ================================
prompt
create table FG_FORMENTITY_HST
(
  id             NUMBER not null,
  formcode       VARCHAR2(100) not null,
  numberoforder  NUMBER not null,
  entitytype     VARCHAR2(100) not null,
  entityimpcode  VARCHAR2(100) not null,
  entityimpclass VARCHAR2(100) not null,
  entityimpinit  VARCHAR2(4000) not null,
  comments       VARCHAR2(4000),
  fs             VARCHAR2(4000),
  fs_gap         VARCHAR2(4000),
  change_date    DATE,
  change_by      NUMBER default -1,
  change_type    VARCHAR2(1)
)
;
comment on column FG_FORMENTITY_HST.change_type
  is 'I/U';

prompt
prompt Creating table FG_FORMENTITY_INVALID
prompt ====================================
prompt
create table FG_FORMENTITY_INVALID
(
  id NUMBER not null
)
;

prompt
prompt Creating table FG_FORMID_UNPIVOT_LIST_TMP
prompt =========================================
prompt
create table FG_FORMID_UNPIVOT_LIST_TMP
(
  id   NUMBER,
  flag NUMBER
)
;
comment on column FG_FORMID_UNPIVOT_LIST_TMP.flag
  is '0';

prompt
prompt Creating table FG_FORMLASTSAVEVALUE
prompt ===================================
prompt
create table FG_FORMLASTSAVEVALUE
(
  id              NUMBER,
  formid          VARCHAR2(100) not null,
  formcode_entity VARCHAR2(100) not null,
  entityimpcode   VARCHAR2(100) not null,
  userid          VARCHAR2(100) not null,
  sessionid       VARCHAR2(500),
  active          NUMBER(1),
  formidscript    VARCHAR2(100),
  formcode_name   VARCHAR2(100),
  created_by      VARCHAR2(100),
  creation_date   DATE default sysdate,
  timestamp       DATE default sysdate,
  change_by       VARCHAR2(100),
  save_name_id    NUMBER default -1,
  login_sessionid VARCHAR2(500),
  entityimpvalue  CLOB
)
;
comment on column FG_FORMLASTSAVEVALUE.timestamp
  is 'CHANGE DATE';
comment on column FG_FORMLASTSAVEVALUE.save_name_id
  is 'reference fg_formlastsavevalue_name.save_name';

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_HST
prompt =======================================
prompt
create table FG_FORMLASTSAVEVALUE_HST
(
  id                 NUMBER,
  formid             VARCHAR2(100),
  formcode_entity    VARCHAR2(100),
  entityimpcode      VARCHAR2(100),
  entityimpvalue     VARCHAR2(4000),
  userid             VARCHAR2(100),
  change_comment     VARCHAR2(500),
  change_id          NUMBER,
  change_by          NUMBER,
  change_type        VARCHAR2(1),
  change_date        DATE,
  sessionid          VARCHAR2(500),
  active             NUMBER(1),
  displayvalue       VARCHAR2(4000),
  updatejobflag      VARCHAR2(10),
  displaylabel       VARCHAR2(500),
  path_id            VARCHAR2(100),
  is_file            NUMBER(1) default 0,
  is_idlist          NUMBER(1) default 0,
  db_transaction_id  VARCHAR2(500),
  tmp_entityimpvalue VARCHAR2(4000),
  tmp_displayvalue   VARCHAR2(4000)
)
;
comment on column FG_FORMLASTSAVEVALUE_HST.active
  is '1';
comment on column FG_FORMLASTSAVEVALUE_HST.path_id
  is 'ref and doc (pop up) get the parentId';
comment on column FG_FORMLASTSAVEVALUE_HST.is_file
  is '1 the value is link to fg_files else 0';

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_INF
prompt =======================================
prompt
create table FG_FORMLASTSAVEVALUE_INF
(
  id                 NUMBER,
  formid             VARCHAR2(100),
  formcode_entity    VARCHAR2(100),
  entityimpcode      VARCHAR2(100),
  entityimpvalue     VARCHAR2(4000),
  userid             VARCHAR2(100),
  change_comment     VARCHAR2(500),
  change_id          NUMBER,
  change_by          NUMBER,
  change_type        VARCHAR2(1),
  change_date        DATE,
  sessionid          VARCHAR2(500),
  active             NUMBER(1),
  displayvalue       VARCHAR2(4000),
  updatejobflag      VARCHAR2(10),
  displaylabel       VARCHAR2(500),
  path_id            VARCHAR2(100),
  is_file            NUMBER(1) default 0,
  is_idlist          NUMBER(1) default 0,
  db_transaction_id  VARCHAR2(500),
  tmp_entityimpvalue VARCHAR2(4000),
  tmp_displayvalue   VARCHAR2(4000)
)
;
comment on column FG_FORMLASTSAVEVALUE_INF.updatejobflag
  is '0 - OK, 1 - need display correction, 2 - error';
comment on column FG_FORMLASTSAVEVALUE_INF.path_id
  is 'ref and doc (pop up) get the parentId null in struct';
comment on column FG_FORMLASTSAVEVALUE_INF.is_file
  is '1 the value is link to fg_files else 0';

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_INF_GTMP
prompt ============================================
prompt
create global temporary table FG_FORMLASTSAVEVALUE_INF_GTMP
(
  id                 NUMBER,
  formid             VARCHAR2(100),
  formcode_entity    VARCHAR2(100),
  entityimpcode      VARCHAR2(100),
  entityimpvalue     VARCHAR2(4000),
  userid             VARCHAR2(100),
  change_comment     VARCHAR2(500),
  change_id          NUMBER,
  change_by          NUMBER,
  change_type        VARCHAR2(1),
  change_date        DATE,
  sessionid          VARCHAR2(500),
  active             NUMBER(1),
  displayvalue       VARCHAR2(4000),
  updatejobflag      VARCHAR2(10),
  displaylabel       VARCHAR2(500),
  path_id            VARCHAR2(100),
  is_file            NUMBER(1),
  is_idlist          NUMBER(1),
  db_transaction_id  VARCHAR2(500),
  tmp_entityimpvalue VARCHAR2(4000),
  tmp_displayvalue   VARCHAR2(4000)
)
on commit preserve rows;

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_INF_IDTMP
prompt =============================================
prompt
create table FG_FORMLASTSAVEVALUE_INF_IDTMP
(
  id                 NUMBER,
  formid             VARCHAR2(100),
  formcode_entity    VARCHAR2(100),
  entityimpcode      VARCHAR2(100),
  entityimpvalue     VARCHAR2(4000),
  userid             VARCHAR2(100),
  change_comment     VARCHAR2(500),
  change_id          NUMBER,
  change_by          NUMBER,
  change_type        VARCHAR2(1),
  change_date        DATE,
  sessionid          VARCHAR2(500),
  active             NUMBER(1),
  displayvalue       VARCHAR2(4000),
  updatejobflag      VARCHAR2(10),
  displaylabel       VARCHAR2(500),
  path_id            VARCHAR2(100),
  is_file            NUMBER(1),
  is_idlist          NUMBER(1),
  db_transaction_id  VARCHAR2(500),
  tmp_entityimpvalue VARCHAR2(4000),
  tmp_displayvalue   VARCHAR2(4000)
)
;

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_INF_RT
prompt ==========================================
prompt
create table FG_FORMLASTSAVEVALUE_INF_RT
(
  id      NUMBER,
  rt_text VARCHAR2(4000)
)
;

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_INF_SS
prompt ==========================================
prompt
create table FG_FORMLASTSAVEVALUE_INF_SS
(
  id                 NUMBER,
  formid             VARCHAR2(100),
  formcode_entity    VARCHAR2(100),
  entityimpcode      VARCHAR2(100),
  entityimpvalue     VARCHAR2(4000),
  userid             VARCHAR2(100),
  change_comment     VARCHAR2(500),
  change_id          NUMBER,
  change_by          NUMBER,
  change_type        VARCHAR2(1),
  change_date        DATE,
  sessionid          VARCHAR2(500),
  active             NUMBER(1),
  displayvalue       VARCHAR2(4000),
  updatejobflag      VARCHAR2(10),
  displaylabel       VARCHAR2(500),
  path_id            VARCHAR2(100),
  is_file            NUMBER(1),
  is_idlist          NUMBER(1),
  db_transaction_id  VARCHAR2(500),
  tmp_entityimpvalue VARCHAR2(4000),
  tmp_displayvalue   VARCHAR2(4000),
  experiment_id      VARCHAR2(100),
  log_date           DATE,
  log_version        VARCHAR2(100)
)
;

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_INF_SSM
prompt ===========================================
prompt
create table FG_FORMLASTSAVEVALUE_INF_SSM
(
  formid        VARCHAR2(100),
  formid_ref    VARCHAR2(100),
  change_date   DATE,
  change_by     NUMBER,
  comments      VARCHAR2(500),
  snapshot_type VARCHAR2(100)
)
;

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_INF_SST
prompt ===========================================
prompt
create global temporary table FG_FORMLASTSAVEVALUE_INF_SST
(
  formid             VARCHAR2(100),
  change_date        DATE,
  change_by          NUMBER,
  formcode_entity    VARCHAR2(100),
  userid             VARCHAR2(100),
  id                 NUMBER,
  entityimpcode      VARCHAR2(100),
  entityimpvalue     VARCHAR2(4000),
  change_comment     VARCHAR2(500),
  change_id          NUMBER,
  change_type        VARCHAR2(1),
  sessionid          VARCHAR2(500),
  active             NUMBER(1),
  displayvalue       VARCHAR2(4000),
  updatejobflag      VARCHAR2(10),
  displaylabel       VARCHAR2(500),
  path_id            VARCHAR2(100),
  is_file            NUMBER(1),
  is_idlist          NUMBER(1),
  db_transaction_id  VARCHAR2(500),
  tmp_entityimpvalue VARCHAR2(4000),
  tmp_displayvalue   VARCHAR2(4000)
)
on commit preserve rows;

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_INF_SSTMP
prompt =============================================
prompt
create table FG_FORMLASTSAVEVALUE_INF_SSTMP
(
  id                 NUMBER,
  formid             VARCHAR2(100),
  formcode_entity    VARCHAR2(100),
  entityimpcode      VARCHAR2(100),
  entityimpvalue     VARCHAR2(4000),
  userid             VARCHAR2(100),
  change_comment     VARCHAR2(500),
  change_id          NUMBER,
  change_by          NUMBER,
  change_type        VARCHAR2(1),
  change_date        DATE,
  sessionid          VARCHAR2(500),
  active             NUMBER(1),
  displayvalue       VARCHAR2(4000),
  updatejobflag      VARCHAR2(10),
  displaylabel       VARCHAR2(500),
  path_id            VARCHAR2(100),
  is_file            NUMBER(1),
  is_idlist          NUMBER(1),
  db_transaction_id  VARCHAR2(500),
  tmp_entityimpvalue VARCHAR2(4000),
  tmp_displayvalue   VARCHAR2(4000),
  experiment_id      VARCHAR2(100),
  log_date           DATE,
  log_version        VARCHAR2(100)
)
;

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_INF_SS_BU
prompt =============================================
prompt
create table FG_FORMLASTSAVEVALUE_INF_SS_BU
(
  id                 NUMBER,
  formid             VARCHAR2(100),
  formcode_entity    VARCHAR2(100),
  entityimpcode      VARCHAR2(100),
  entityimpvalue     VARCHAR2(4000),
  userid             VARCHAR2(100),
  change_comment     VARCHAR2(500),
  change_id          NUMBER,
  change_by          NUMBER,
  change_type        VARCHAR2(1),
  change_date        DATE,
  sessionid          VARCHAR2(500),
  active             NUMBER(1),
  displayvalue       VARCHAR2(4000),
  updatejobflag      VARCHAR2(10),
  displaylabel       VARCHAR2(500),
  path_id            VARCHAR2(100),
  is_file            NUMBER(1),
  is_idlist          NUMBER(1),
  db_transaction_id  VARCHAR2(500),
  tmp_entityimpvalue VARCHAR2(4000),
  tmp_displayvalue   VARCHAR2(4000)
)
;

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_NAME
prompt ========================================
prompt
create table FG_FORMLASTSAVEVALUE_NAME
(
  save_name_id     NUMBER,
  formcode_name    VARCHAR2(100),
  save_name        VARCHAR2(100),
  save_description VARCHAR2(100),
  active           NUMBER(1),
  userid           VARCHAR2(100) not null,
  created_by       VARCHAR2(100),
  creation_date    DATE default sysdate,
  timestamp        DATE
)
;

prompt
prompt Creating table FG_FORMLASTSAVEVALUE_UNPIVOT
prompt ===========================================
prompt
create table FG_FORMLASTSAVEVALUE_UNPIVOT
(
  id              NUMBER,
  formid          VARCHAR2(100) not null,
  formcode_entity VARCHAR2(100) not null,
  entityimpcode   VARCHAR2(100) not null,
  userid          VARCHAR2(100) not null,
  sessionid       VARCHAR2(500),
  active          NUMBER(1),
  formidscript    VARCHAR2(100),
  formcode_name   VARCHAR2(100),
  created_by      VARCHAR2(100),
  creation_date   DATE,
  timestamp       DATE,
  change_by       VARCHAR2(100),
  save_name_id    NUMBER,
  login_sessionid VARCHAR2(500),
  entityimpvalue  VARCHAR2(4000),
  cloneid         VARCHAR2(100),
  templateflag    VARCHAR2(100)
)
;

prompt
prompt Creating table FG_FORMLASTSAVE_TRANSACT_FAIL
prompt ============================================
prompt
create table FG_FORMLASTSAVE_TRANSACT_FAIL
(
  transaction_failure_number VARCHAR2(200),
  delete_flag                NUMBER(1) default 0,
  insertdate                 DATE default SYSDATE
)
;

prompt
prompt Creating table FG_FORMMONITOPARAM_DATA
prompt ======================================
prompt
create table FG_FORMMONITOPARAM_DATA
(
  parent_id      VARCHAR2(500),
  value          VARCHAR2(500),
  uom_id         VARCHAR2(500),
  config_id      VARCHAR2(500),
  name           VARCHAR2(500),
  json_source    CLOB,
  calcidentifier VARCHAR2(500)
)
;
comment on column FG_FORMMONITOPARAM_DATA.parent_id
  is 'refer FG_S_PARAMMONITORING_PIVOT.PARAMMONITORING_ID (formid)';

prompt
prompt Creating table FG_FORMTEST_DATA
prompt ===============================
prompt
create table FG_FORMTEST_DATA
(
  parent_id NUMBER,
  value     VARCHAR2(500),
  uom_id    VARCHAR2(100),
  config_id NUMBER
)
;
comment on column FG_FORMTEST_DATA.parent_id
  is 'formid NOTE: NOT ELEMENT ID -> THE ELEMENT IS NOT SAVE ON THE DB. WE DISPLAY ACCORDING TO THE TEMPLATE AND ON UPDATE WE MTACH ACCORDING THIS TABLE VALUES';
comment on column FG_FORMTEST_DATA.config_id
  is 'test_config';

prompt
prompt Creating table FG_FORM_CHANGE_LIST
prompt ==================================
prompt
create table FG_FORM_CHANGE_LIST
(
  formcode    VARCHAR2(100),
  update_flag NUMBER(1) default 0
)
;

prompt
prompt Creating table FG_FORM_HST
prompt ==========================
prompt
create table FG_FORM_HST
(
  id              NUMBER,
  formcode        VARCHAR2(100),
  description     VARCHAR2(4000),
  active          VARCHAR2(100),
  form_type       VARCHAR2(100),
  title           VARCHAR2(100),
  subtitle        VARCHAR2(500),
  use_as_template NUMBER,
  group_name      VARCHAR2(100),
  numberoforder   NUMBER,
  formcode_entity VARCHAR2(100),
  ignore_nav      VARCHAR2(100),
  usecache        VARCHAR2(100),
  change_date     DATE,
  change_by       NUMBER default -1,
  change_type     VARCHAR2(1)
)
;
comment on column FG_FORM_HST.change_type
  is 'I/U';

prompt
prompt Creating table FG_HISTORICAL_SPREAD_DATA
prompt ========================================
prompt
create table FG_HISTORICAL_SPREAD_DATA
(
  formid         VARCHAR2(100) not null,
  timestamp      DATE,
  user_id        VARCHAR2(100),
  active         NUMBER,
  formcode       VARCHAR2(100),
  spreadsheet_id VARCHAR2(500)
)
;

prompt
prompt Creating table FG_I_CHEM_DATA_V_B
prompt =================================
prompt
create table FG_I_CHEM_DATA_V_B
(
  clobcontent CLOB
)
;

prompt
prompt Creating table FG_I_UNITTEST_GLOBAL_SAVE_V_SS
prompt =============================================
prompt
create table FG_I_UNITTEST_GLOBAL_SAVE_V_SS
(
  parent_id       VARCHAR2(100),
  formcode        VARCHAR2(100),
  form_id         VARCHAR2(100),
  userid          CHAR(2),
  parent_formcode VARCHAR2(100),
  tabletype       VARCHAR2(400)
)
;

prompt
prompt Creating table FG_I_WEBIX_OUTPUT_ALL_V_SS
prompt =========================================
prompt
create table FG_I_WEBIX_OUTPUT_ALL_V_SS
(
  material_id   VARCHAR2(4000),
  result_id     VARCHAR2(100),
  experiment_id VARCHAR2(4000),
  step_id       VARCHAR2(4000),
  batch_id      VARCHAR2(4000),
  mass          VARCHAR2(100),
  result_value  VARCHAR2(500),
  isstandart    VARCHAR2(500),
  result_uom_id VARCHAR2(500)
)
;

prompt
prompt Creating table FG_JCEHM_BU
prompt ==========================
prompt
create table FG_JCEHM_BU
(
  cd_id                NUMBER(10) not null,
  cd_structure         BLOB not null,
  cd_smiles            VARCHAR2(4000),
  cd_formula           VARCHAR2(100),
  cd_sortable_formula  VARCHAR2(500),
  cd_molweight         FLOAT,
  cd_hash              NUMBER(10) not null,
  cd_flags             VARCHAR2(20),
  cd_timestamp         DATE not null,
  cd_pre_calculated    NUMBER(1) not null,
  cd_taut_hash         NUMBER(10) not null,
  cd_taut_frag_hash    VARCHAR2(4000),
  cd_screen_descriptor VARCHAR2(4000),
  cd_fp1               NUMBER(10) not null,
  cd_fp2               NUMBER(10) not null,
  cd_fp3               NUMBER(10) not null,
  cd_fp4               NUMBER(10) not null,
  cd_fp5               NUMBER(10) not null,
  cd_fp6               NUMBER(10) not null,
  cd_fp7               NUMBER(10) not null,
  cd_fp8               NUMBER(10) not null,
  cd_fp9               NUMBER(10) not null,
  cd_fp10              NUMBER(10) not null,
  cd_fp11              NUMBER(10) not null,
  cd_fp12              NUMBER(10) not null,
  cd_fp13              NUMBER(10) not null,
  cd_fp14              NUMBER(10) not null,
  cd_fp15              NUMBER(10) not null,
  cd_fp16              NUMBER(10) not null,
  formid               VARCHAR2(100),
  fullformcode         VARCHAR2(100),
  moltype              VARCHAR2(100),
  elementid            VARCHAR2(100)
)
;

prompt
prompt Creating table FG_NOTIFICATION_COMPARE
prompt ======================================
prompt
create table FG_NOTIFICATION_COMPARE
(
  d_notification_message_id     VARCHAR2(40),
  notification_module_id        VARCHAR2(40),
  message_type_id               VARCHAR2(40),
  description                   VARCHAR2(2000 CHAR),
  trigger_type_id               VARCHAR2(40),
  on_save_formcode              VARCHAR2(50),
  email_subject                 VARCHAR2(4000 CHAR),
  email_body                    VARCHAR2(4000),
  scheduler_interval            VARCHAR2(40),
  where_statement               VARCHAR2(4000),
  resend                        VARCHAR2(40),
  p_notification_module_type_id VARCHAR2(40),
  module_name                   VARCHAR2(200 CHAR),
  select_statement              VARCHAR2(4000),
  msguniqueidname               VARCHAR2(1000 CHAR),
  order_by                      VARCHAR2(4000),
  addressee_type_id             VARCHAR2(40),
  send_type                     VARCHAR2(10),
  addressee_user_id             VARCHAR2(40),
  params_field_names            VARCHAR2(4000),
  addressee_group_select        VARCHAR2(4000),
  add_attachments               VARCHAR2(40),
  attached_report_name          VARCHAR2(1000 CHAR),
  attached_report_type          VARCHAR2(1000 CHAR),
  isactive                      VARCHAR2(40)
)
;

prompt
prompt Creating table FG_P_EXPREPORT_DATA_TMP
prompt ======================================
prompt
create table FG_P_EXPREPORT_DATA_TMP
(
  experiment_id     VARCHAR2(500),
  order_            NUMBER,
  step_id           VARCHAR2(500),
  materialref_id    VARCHAR2(500),
  tabletype         VARCHAR2(500),
  result_id         VARCHAR2(500),
  result_smartpivot VARCHAR2(4000),
  formnumberid      VARCHAR2(500),
  statekey          VARCHAR2(500),
  order2            VARCHAR2(500),
  row_timestamp     DATE default sysdate
)
;

prompt
prompt Creating table FG_P_EXPREPORT_SAMPLE_TMP
prompt ========================================
prompt
create table FG_P_EXPREPORT_SAMPLE_TMP
(
  statekey       VARCHAR2(500),
  sample_id      NUMBER,
  samplename     VARCHAR2(500),
  sampledesc     VARCHAR2(500),
  commentsforcoa VARCHAR2(500),
  experiment_id  VARCHAR2(500),
  creator_id     VARCHAR2(500),
  ammount        VARCHAR2(500),
  row_timestamp  DATE default sysdate
)
;

prompt
prompt Creating table FG_REPORTDESIGN
prompt ==============================
prompt
create table FG_REPORTDESIGN
(
  formcode           VARCHAR2(100) default 'ReportDesign' not null,
  created_by         VARCHAR2(100),
  creation_date      DATE,
  timestamp          DATE default sysdate,
  reportdesign_value CLOB,
  reportdesign_name  VARCHAR2(100)
)
;

prompt
prompt Creating table FG_REPORT_LIST
prompt =============================
prompt
create table FG_REPORT_LIST
(
  id                 VARCHAR2(100),
  report_category    VARCHAR2(100),
  report_sql         VARCHAR2(1000),
  report_description VARCHAR2(1000),
  change_by          VARCHAR2(100),
  active             NUMBER,
  timestamp          DATE,
  report_user_id     NUMBER,
  report_scope       VARCHAR2(100),
  report_style       VARCHAR2(100),
  report_name        VARCHAR2(500) not null,
  report_save_data   VARCHAR2(4000),
  meta_data          VARCHAR2(4000),
  system_row         NUMBER default 0
)
;
comment on column FG_REPORT_LIST.report_sql
  is 'fg_report_<catalog>_v';
comment on column FG_REPORT_LIST.report_scope
  is 'public / private';
comment on column FG_REPORT_LIST.report_style
  is 'simple (query builder) / custom ';
comment on column FG_REPORT_LIST.report_name
  is 'save as or save in private / save for public';
comment on column FG_REPORT_LIST.meta_data
  is 'meta data JSON using: fg_report_<catalog>_md_v, fg_report_<catalog>_col_v, fg_report_<catalog>_grp_v - NOT SAVE IN THE DB';
comment on column FG_REPORT_LIST.system_row
  is '1 - rows enterd by the system and should not be deleted';
create unique index REPORT_NAME_UNIQUE on FG_REPORT_LIST (REPORT_NAME);

prompt
prompt Creating table FG_RESOURCE
prompt ==========================
prompt
create table FG_RESOURCE
(
  id    NUMBER,
  type  VARCHAR2(100),
  code  VARCHAR2(100),
  value VARCHAR2(1200),
  info  VARCHAR2(1000)
)
;
comment on column FG_RESOURCE.info
  is 'TODO DESC';
alter table FG_RESOURCE
  add constraint KU_RESOURCE unique (CODE, TYPE);

prompt
prompt Creating table FG_RESULTS
prompt =========================
prompt
create table FG_RESULTS
(
  result_id           VARCHAR2(100) not null,
  experiment_id       VARCHAR2(4000),
  result_test_name    VARCHAR2(4000),
  result_name         VARCHAR2(4000),
  sample_id           VARCHAR2(100),
  result_value        VARCHAR2(500),
  result_uom_id       VARCHAR2(500),
  result_type         CHAR(500),
  result_material_id  VARCHAR2(500),
  result_date         DATE,
  result_time         VARCHAR2(8),
  result_comment      VARCHAR2(4000),
  selftest_id         VARCHAR2(4000),
  result_is_active    CHAR(500),
  resultref_id        VARCHAR2(4000),
  result_is_webix     CHAR(500),
  result_materialname VARCHAR2(500),
  result_request_id   VARCHAR2(4000),
  result_change_by    VARCHAR2(100)
)
;
alter table FG_RESULTS
  add constraint PK_RESULTS primary key (RESULT_ID);

prompt
prompt Creating table FG_RESULTS_HST
prompt =============================
prompt
create table FG_RESULTS_HST
(
  result_id           VARCHAR2(100) not null,
  experiment_id       VARCHAR2(4000),
  result_test_name    VARCHAR2(4000),
  result_name         VARCHAR2(4000),
  sample_id           VARCHAR2(100),
  result_value        VARCHAR2(500),
  result_uom_id       VARCHAR2(500),
  result_type         CHAR(500),
  result_material_id  VARCHAR2(500),
  result_date         DATE,
  result_time         VARCHAR2(8),
  result_comment      VARCHAR2(500),
  selftest_id         VARCHAR2(4000),
  result_is_active    CHAR(500),
  resultref_id        VARCHAR2(4000),
  result_is_webix     CHAR(500),
  result_materialname VARCHAR2(500),
  result_request_id   VARCHAR2(4000),
  change_comment      VARCHAR2(500),
  change_id           NUMBER,
  change_by           NUMBER,
  change_type         VARCHAR2(1),
  change_date         DATE,
  result_change_by    VARCHAR2(100)
)
;

prompt
prompt Creating table FG_RICHTEXT
prompt ==========================
prompt
create table FG_RICHTEXT
(
  file_id                     VARCHAR2(200) not null,
  file_name                   VARCHAR2(500),
  file_content                CLOB,
  content_type                VARCHAR2(500),
  file_content_text           CLOB,
  file_content_text_no_tables CLOB,
  job_flag                    NUMBER default 0
)
;
create unique index FG_RICHTEXT on FG_RICHTEXT (FILE_ID);

prompt
prompt Creating table FG_R_MATERIALIZED_VIEW
prompt =====================================
prompt
create table FG_R_MATERIALIZED_VIEW
(
  db_name            VARCHAR2(100),
  view_name          VARCHAR2(100),
  view_code          CLOB,
  view_snapshot_date VARCHAR2(100)
)
;

prompt
prompt Creating table FG_R_MESSAGES
prompt ============================
prompt
create table FG_R_MESSAGES
(
  message_id     NUMBER not null,
  message_body   CLOB,
  user_id        VARCHAR2(500),
  message_type   VARCHAR2(100),
  formid         VARCHAR2(500),
  time_stamp     DATE,
  changed_by     VARCHAR2(500),
  additionalinfo VARCHAR2(4000)
)
;
comment on column FG_R_MESSAGES.user_id
  is 'user from distribution list';
comment on column FG_R_MESSAGES.changed_by
  is 'user that do current change';
alter table FG_R_MESSAGES
  add primary key (MESSAGE_ID);

prompt
prompt Creating table FG_R_MESSAGES_STATE
prompt ==================================
prompt
create table FG_R_MESSAGES_STATE
(
  message_id   VARCHAR2(500),
  user_id      VARCHAR2(500),
  is_readed    NUMBER(1) default 0,
  is_deleted   NUMBER(1) default 0,
  updated_by   VARCHAR2(500),
  updated_date DATE
)
;
comment on column FG_R_MESSAGES_STATE.user_id
  is 'user that received current message';

prompt
prompt Creating table FG_R_MESSAGES_STATE_HST
prompt ======================================
prompt
create table FG_R_MESSAGES_STATE_HST
(
  message_id  VARCHAR2(500),
  user_id     VARCHAR2(500),
  is_readed   NUMBER(1),
  is_deleted  NUMBER(1),
  change_id   INTEGER,
  changed_by  VARCHAR2(500),
  change_date DATE,
  change_type VARCHAR2(1)
)
;

prompt
prompt Creating table FG_R_SYSTEM_VIEW
prompt ===============================
prompt
create table FG_R_SYSTEM_VIEW
(
  db_name            VARCHAR2(100),
  view_name          VARCHAR2(100),
  view_code          CLOB,
  view_snapshot_date VARCHAR2(100)
)
;

prompt
prompt Creating table FG_SEQUENCE
prompt ==========================
prompt
create table FG_SEQUENCE
(
  id                    NUMBER not null,
  formcode              VARCHAR2(100),
  insertdate            DATE default sysdate,
  formidname            VARCHAR2(500) default 'NA',
  formpath              VARCHAR2(4000),
  formtabletype         VARCHAR2(400),
  comments              VARCHAR2(100),
  id_holder             NUMBER,
  search_match_id1      NUMBER,
  search_match_id2      NUMBER,
  search_match_id3      NUMBER,
  search_match_id4      NUMBER,
  changedate            DATE default sysdate,
  db_seq_transaction_id VARCHAR2(500),
  tmp_formidname        VARCHAR2(500),
  generate_userid       VARCHAR2(100),
  generate_formid       VARCHAR2(100)
)
;
comment on column FG_SEQUENCE.search_match_id1
  is 'adama-project';
comment on column FG_SEQUENCE.search_match_id2
  is 'adama-subproject';
comment on column FG_SEQUENCE.search_match_id3
  is 'adama-subsubproject';
comment on column FG_SEQUENCE.search_match_id4
  is 'adama-materialId';
comment on column FG_SEQUENCE.generate_userid
  is 'created by ';
comment on column FG_SEQUENCE.generate_formid
  is 'created from (parentid)';
alter table FG_SEQUENCE
  add constraint FG_FORMID_KEY primary key (ID);

prompt
prompt Creating table FG_SEQUENCE_EXCEL
prompt ================================
prompt
create table FG_SEQUENCE_EXCEL
(
  id         NUMBER,
  formcode   VARCHAR2(100),
  insertdate DATE,
  formidname VARCHAR2(400)
)
;

prompt
prompt Creating table FG_SEQUENCE_EXPERIMENT
prompt =====================================
prompt
create table FG_SEQUENCE_EXPERIMENT
(
  id               NUMBER not null,
  formcode         VARCHAR2(100),
  insertdate       DATE,
  formidname       VARCHAR2(400),
  formpath         VARCHAR2(4000),
  formtabletype    VARCHAR2(400),
  comments         VARCHAR2(100),
  id_holder        NUMBER,
  search_match_id1 NUMBER,
  search_match_id2 NUMBER,
  search_match_id3 NUMBER,
  search_match_id4 NUMBER,
  changedate       DATE
)
;

prompt
prompt Creating table FG_SEQUENCE_FILES
prompt ================================
prompt
create table FG_SEQUENCE_FILES
(
  id            NUMBER not null,
  formcode      VARCHAR2(100),
  insertdate    DATE,
  formidname    VARCHAR2(400),
  source_formid VARCHAR2(100)
)
;

prompt
prompt Creating table FG_SEQUENCE_HST
prompt ==============================
prompt
create table FG_SEQUENCE_HST
(
  id               NUMBER not null,
  formcode         VARCHAR2(100),
  insertdate       DATE,
  formidname       VARCHAR2(500),
  formpath         VARCHAR2(4000),
  formtabletype    VARCHAR2(400),
  comments         VARCHAR2(100),
  id_holder        NUMBER,
  search_match_id1 NUMBER,
  search_match_id2 NUMBER,
  search_match_id3 NUMBER,
  search_match_id4 NUMBER,
  changedate       DATE
)
;

prompt
prompt Creating table FG_SEQ_SCRIPT_HOLDER
prompt ===================================
prompt
create table FG_SEQ_SCRIPT_HOLDER
(
  develop_id   NUMBER,
  server_id    NUMBER,
  formcode     VARCHAR2(1000),
  "Time_STAMP" DATE default sysdate
)
;

prompt
prompt Creating table FG_STB_RESULT_SAMPLE
prompt ===================================
prompt
create table FG_STB_RESULT_SAMPLE
(
  sample_id         VARCHAR2(100) not null,
  experstbresult_id VARCHAR2(100) not null,
  resultvalue       VARCHAR2(500),
  resultsign        VARCHAR2(500)
)
;

prompt
prompt Creating table FG_SYS_PARAM
prompt ===========================
prompt
create table FG_SYS_PARAM
(
  retry_count                 NUMBER(2),
  login_timeout               NUMBER,
  password_aging              NUMBER,
  grace_period                NUMBER,
  updated_by                  NUMBER,
  updated_on                  TIMESTAMP(6),
  comments                    VARCHAR2(100),
  is_develop                  NUMBER(1) default 0,
  redirect_notification_email VARCHAR2(100),
  last_build                  DATE,
  last_refresh_mv             DATE,
  last_transaction            DATE,
  transaction_waiting         NUMBER default 0,
  refresh_mv_waiting          NUMBER default 0,
  last_refresh_mv_5min        DATE
)
;
comment on column FG_SYS_PARAM.is_develop
  is '0 develop / 1 production / 2 unittest';
comment on column FG_SYS_PARAM.redirect_notification_email
  is 'send email to this address instead of the user when show "only message" is set to true (1).';
comment on column FG_SYS_PARAM.last_build
  is 'SYSDATE';

prompt
prompt Creating table FG_SYS_SCHED
prompt ===========================
prompt
create table FG_SYS_SCHED
(
  sched_name                VARCHAR2(500),
  start_date                DATE,
  end_date                  DATE,
  comments                  VARCHAR2(4000),
  status                    VARCHAR2(1),
  interval_time             VARCHAR2(500),
  last_end_date             DATE,
  suspend                   NUMBER default 0,
  start_date_success_holder DATE
)
;
comment on column FG_SYS_SCHED.status
  is 'S - SUCCESS, F- FAILURE';
comment on column FG_SYS_SCHED.last_end_date
  is 'LAST_END_DATE';

prompt
prompt Creating table FG_S_USER_PIVOT
prompt ==============================
prompt
create table FG_S_USER_PIVOT
(
  formid                VARCHAR2(100) not null,
  timestamp             DATE,
  change_by             VARCHAR2(100),
  sessionid             VARCHAR2(100),
  active                NUMBER,
  formcode              VARCHAR2(100),
  username              VARCHAR2(500),
  position              VARCHAR2(500),
  firstname             VARCHAR2(500),
  lastname              VARCHAR2(500),
  changepassword        VARCHAR2(500),
  deleted               VARCHAR2(500),
  locked                VARCHAR2(500),
  customer_id           VARCHAR2(500),
  userrole_id           VARCHAR2(500),
  userldap              VARCHAR2(500),
  passworddate          VARCHAR2(500),
  chgpassworddate       VARCHAR2(500),
  laboratory_id         VARCHAR2(500),
  unit_id               VARCHAR2(500),
  password              VARCHAR2(500),
  lastretry             VARCHAR2(500),
  retrycount            VARCHAR2(500),
  site_id               VARCHAR2(500),
  email                 VARCHAR2(500),
  lastpassworddate      VARCHAR2(500),
  formcode_entity       VARCHAR2(100),
  teamleader_id         VARCHAR2(500),
  groupscrew            VARCHAR2(500),
  cloneid               VARCHAR2(100),
  permissiontable       VARCHAR2(500),
  sensitivitylevel_id   VARCHAR2(500),
  templateflag          VARCHAR2(100),
  created_by            VARCHAR2(100),
  creation_date         DATE,
  messagecheckinterval  VARCHAR2(500),
  lastnotificationcheck VARCHAR2(500),
  last_breadcrumb_link  VARCHAR2(500)
)
;
create unique index USER_NAME_UNIQUE on FG_S_USER_PIVOT (TRIM(UPPER(USERNAME)));

prompt
prompt Creating table FG_S_USER_PIVOT_TMP
prompt ==================================
prompt
create table FG_S_USER_PIVOT_TMP
(
  formid           VARCHAR2(100) not null,
  time_stamp       DATE,
  created_by       VARCHAR2(100) not null,
  password         VARCHAR2(500),
  firstname        VARCHAR2(500),
  userldap         VARCHAR2(500),
  username         VARCHAR2(500),
  passworddate     VARCHAR2(500),
  chgpassworddate  VARCHAR2(500),
  lastname         VARCHAR2(500),
  userrole_id      VARCHAR2(500),
  lastentry        VARCHAR2(500),
  email            VARCHAR2(500),
  retrycount       VARCHAR2(500),
  changepassword   VARCHAR2(500),
  locked           VARCHAR2(500),
  lastpassworddate VARCHAR2(500)
)
;

prompt
prompt Creating table FG_TOOL_INF_ALL_DATA
prompt ===================================
prompt
create table FG_TOOL_INF_ALL_DATA
(
  name      VARCHAR2(4000),
  id        VARCHAR2(100),
  tablename VARCHAR2(29)
)
;

prompt
prompt Creating table FG_TREE_SEARCH_ID_TMP
prompt ====================================
prompt
create global temporary table FG_TREE_SEARCH_ID_TMP
(
  id NUMBER not null
)
on commit preserve rows;

prompt
prompt Creating table FG_UNITEST_LOG
prompt =============================
prompt
create table FG_UNITEST_LOG
(
  user_id              VARCHAR2(4000),
  unittestgroupname    VARCHAR2(4000),
  message              VARCHAR2(4000),
  action               VARCHAR2(4000),
  waitingtime          VARCHAR2(4000),
  test_status          VARCHAR2(100),
  time_stamp           DATE,
  fieldvalue           VARCHAR2(4000),
  unitestlogid         VARCHAR2(100) not null,
  unitestconfigform_id VARCHAR2(100),
  id                   NUMBER
)
;

prompt
prompt Creating table FG_UNITEST_LOG_HST
prompt =================================
prompt
create table FG_UNITEST_LOG_HST
(
  user_id              VARCHAR2(4000),
  unittestgroupname    VARCHAR2(4000),
  message              VARCHAR2(4000),
  action               VARCHAR2(4000),
  waitingtime          VARCHAR2(4000),
  test_status          VARCHAR2(100),
  time_stamp           DATE,
  fieldvalue           VARCHAR2(4000),
  unitestlogid         VARCHAR2(100) not null,
  unitestconfigform_id VARCHAR2(100),
  id                   NUMBER
)
;

prompt
prompt Creating table FG_UNITTEST_AT_SEQUENCE
prompt ======================================
prompt
create table FG_UNITTEST_AT_SEQUENCE
(
  unittest_at_id NUMBER not null,
  timestamp      DATE
)
;

prompt
prompt Creating table FG_UNITTEST_SEQUENCE
prompt ===================================
prompt
create table FG_UNITTEST_SEQUENCE
(
  unittest_id   NUMBER not null,
  unittest_name VARCHAR2(100),
  timestamp     DATE
)
;

prompt
prompt Creating table FG_WEBIX_OUTPUT
prompt ==============================
prompt
create table FG_WEBIX_OUTPUT
(
  result_id            VARCHAR2(100),
  step_id              VARCHAR2(4000),
  batch_id             VARCHAR2(4000),
  result_name          VARCHAR2(4000),
  result_value         VARCHAR2(500),
  result_type          CHAR(500),
  result_date          DATE default sysdate,
  result_time          VARCHAR2(8) default to_char( sysdate, 'HH24:MI:SS' ),
  result_comment       VARCHAR2(500),
  result_is_active     CHAR(500),
  result_test_name     VARCHAR2(4000),
  mass                 VARCHAR2(100),
  experiment_id        VARCHAR2(4000),
  material_id          VARCHAR2(4000),
  result_uom_id        VARCHAR2(500),
  samples              VARCHAR2(500),
  weight               VARCHAR2(100),
  moles                VARCHAR2(100),
  yield                VARCHAR2(100),
  indication_mb        VARCHAR2(4000),
  sample_mb            VARCHAR2(4000),
  component_id         VARCHAR2(4000),
  preparationref_id    VARCHAR2(4000),
  sample_id            VARCHAR2(4000),
  analytic_data        VARCHAR2(4000),
  weighting            VARCHAR2(4000),
  stream_data          VARCHAR2(4000),
  webix_change_by      VARCHAR2(100),
  table_index_mb       VARCHAR2(5),
  table_group_index_mb VARCHAR2(1)
)
;
comment on column FG_WEBIX_OUTPUT.table_index_mb
  is 'the order of the tables in a single tab';
comment on column FG_WEBIX_OUTPUT.table_group_index_mb
  is 'the number of the tab in which the stream is displayed';

prompt
prompt Creating table FG_WEBIX_OUTPUT_HST
prompt ==================================
prompt
create table FG_WEBIX_OUTPUT_HST
(
  result_id         VARCHAR2(100),
  step_id           VARCHAR2(4000),
  batch_id          VARCHAR2(4000),
  result_name       VARCHAR2(4000),
  result_value      VARCHAR2(500),
  result_type       CHAR(500),
  result_date       DATE,
  result_time       VARCHAR2(8),
  result_comment    VARCHAR2(500),
  result_is_active  CHAR(500),
  result_test_name  VARCHAR2(4000),
  mass              VARCHAR2(100),
  experiment_id     VARCHAR2(4000),
  material_id       VARCHAR2(4000),
  result_uom_id     VARCHAR2(500),
  samples           VARCHAR2(500),
  weight            VARCHAR2(100),
  moles             VARCHAR2(100),
  yield             VARCHAR2(100),
  indication_mb     VARCHAR2(4000),
  sample_mb         VARCHAR2(4000),
  component_id      VARCHAR2(4000),
  preparationref_id VARCHAR2(4000),
  sample_id         VARCHAR2(4000),
  analytic_data     VARCHAR2(4000),
  weighting         VARCHAR2(4000),
  stream_data       VARCHAR2(4000),
  change_comment    VARCHAR2(500),
  change_id         NUMBER,
  change_by         NUMBER,
  change_type       VARCHAR2(1),
  change_date       DATE,
  webix_change_by   VARCHAR2(100),
  table_index_mb    VARCHAR2(5)
)
;

prompt
prompt Creating table FORMCODE_TO_DELETE
prompt =================================
prompt
create table FORMCODE_TO_DELETE
(
  formcode VARCHAR2(100)
)
;

prompt
prompt Creating table JCHEMPROPERTIES
prompt ==============================
prompt
create table JCHEMPROPERTIES
(
  prop_name      VARCHAR2(200) not null,
  prop_value     VARCHAR2(200),
  prop_value_ext BLOB
)
;
alter table JCHEMPROPERTIES
  add primary key (PROP_NAME);

prompt
prompt Creating table JCHEMPROPERTIES_CR
prompt =================================
prompt
create table JCHEMPROPERTIES_CR
(
  cache_id          VARCHAR2(32) not null,
  registration_time VARCHAR2(30) not null,
  is_protected      NUMBER(1) default 0 not null
)
;
alter table JCHEMPROPERTIES_CR
  add constraint CACHE_666270643_PK primary key (CACHE_ID);

prompt
prompt Creating table JCHEMPROPERTIES_DELETED
prompt ======================================
prompt
create table JCHEMPROPERTIES_DELETED
(
  prop_name      VARCHAR2(200) not null,
  prop_value     VARCHAR2(200),
  prop_value_ext BLOB
)
;
alter table JCHEMPROPERTIES_DELETED
  add primary key (PROP_NAME);

prompt
prompt Creating table JCHEMPROPERTIES_DELETED_CR
prompt =========================================
prompt
create table JCHEMPROPERTIES_DELETED_CR
(
  cache_id          VARCHAR2(32) not null,
  registration_time VARCHAR2(30) not null,
  is_protected      NUMBER(1) default 0 not null
)
;
alter table JCHEMPROPERTIES_DELETED_CR
  add constraint CACHE_467779596_PK primary key (CACHE_ID);

prompt
prompt Creating table JCHEMPROPERTIES_DOC
prompt ==================================
prompt
create table JCHEMPROPERTIES_DOC
(
  prop_name      VARCHAR2(200) not null,
  prop_value     VARCHAR2(200),
  prop_value_ext BLOB
)
;
alter table JCHEMPROPERTIES_DOC
  add primary key (PROP_NAME);

prompt
prompt Creating table JCHEMPROPERTIES_DOC_CR
prompt =====================================
prompt
create table JCHEMPROPERTIES_DOC_CR
(
  cache_id          VARCHAR2(32) not null,
  registration_time VARCHAR2(30) not null,
  is_protected      NUMBER(1) default 0 not null
)
;
alter table JCHEMPROPERTIES_DOC_CR
  add constraint CACHE_757297361_PK primary key (CACHE_ID);

prompt
prompt Creating table MATERIAL_TEMP_DATA
prompt =================================
prompt
create global temporary table MATERIAL_TEMP_DATA
(
  invitemmaterial_id VARCHAR2(500)
)
on commit delete rows;

prompt
prompt Creating table PIVOTDATA
prompt ========================
prompt
create global temporary table PIVOTDATA
(
  sample_experiment_id  VARCHAR2(500),
  experiment_id         VARCHAR2(500),
  step_id               VARCHAR2(500),
  action_id             NUMBER,
  colomn_name           VARCHAR2(500),
  operation             VARCHAR2(4000),
  resualt_style         CHAR(53),
  result_name           VARCHAR2(4000),
  result_value          VARCHAR2(4000),
  resultuomid_          VARCHAR2(500),
  result_id             VARCHAR2(100),
  actionname            VARCHAR2(500),
  result_type_order     NUMBER,
  disable_result_render NUMBER,
  self_test_status      VARCHAR2(500),
  resultref_id          NUMBER,
  assay_first_order     NUMBER,
  creation_date         DATE,
  sample_action_id      VARCHAR2(500),
  sample_id             VARCHAR2(500),
  material_id           VARCHAR2(500)
)
on commit delete rows;

prompt
prompt Creating sequence D_NOTIFICATION_CRITERIA_H_SEQ
prompt ===============================================
prompt
create sequence D_NOTIFICATION_CRITERIA_H_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 1150
increment by 1
cache 20;

prompt
prompt Creating sequence D_NOTIFICATION_CRITERIA_SEQ
prompt =============================================
prompt
create sequence D_NOTIFICATION_CRITERIA_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 1161
increment by 1
cache 20;

prompt
prompt Creating sequence D_NOTIF_ADDRESSEE_HST_SEQ
prompt ===========================================
prompt
create sequence D_NOTIF_ADDRESSEE_HST_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 959
increment by 1
cache 20;

prompt
prompt Creating sequence D_NOTIF_ADDRESSEE_SEQ
prompt =======================================
prompt
create sequence D_NOTIF_ADDRESSEE_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 1061
increment by 1
cache 20;

prompt
prompt Creating sequence D_NOTIF_EMAILGROUPM_LOG_SEQ
prompt =============================================
prompt
create sequence D_NOTIF_EMAILGROUPM_LOG_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 19460
increment by 1
cache 20;

prompt
prompt Creating sequence D_NOTIF_EMAIL_LOG_SEQ
prompt =======================================
prompt
create sequence D_NOTIF_EMAIL_LOG_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 121895
increment by 1
cache 20;

prompt
prompt Creating sequence D_NOTIF_MESSAGE_HST_SEQ
prompt =========================================
prompt
create sequence D_NOTIF_MESSAGE_HST_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 9838
increment by 1
cache 20;

prompt
prompt Creating sequence D_NOTIF_MESSAGE_SEQ
prompt =====================================
prompt
create sequence D_NOTIF_MESSAGE_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 1459
increment by 1
cache 20;

prompt
prompt Creating sequence FG_CHEM_DELETED_SEARCH_SQ
prompt ===========================================
prompt
create sequence FG_CHEM_DELETED_SEARCH_SQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 484
increment by 1
cache 20;

prompt
prompt Creating sequence FG_CHEM_DELETED_SEARCH_USQ
prompt ============================================
prompt
create sequence FG_CHEM_DELETED_SEARCH_USQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 488
increment by 1
cache 20;

prompt
prompt Creating sequence FG_CHEM_DOC_SEARCH_SQ
prompt =======================================
prompt
create sequence FG_CHEM_DOC_SEARCH_SQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 303
increment by 1
cache 20;

prompt
prompt Creating sequence FG_CHEM_DOC_SEARCH_USQ
prompt ========================================
prompt
create sequence FG_CHEM_DOC_SEARCH_USQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 244
increment by 1
cache 20;

prompt
prompt Creating sequence FG_CHEM_SEARCH_SQ
prompt ===================================
prompt
create sequence FG_CHEM_SEARCH_SQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 1670
increment by 1
cache 20;

prompt
prompt Creating sequence FG_CHEM_SEARCH_USQ
prompt ====================================
prompt
create sequence FG_CHEM_SEARCH_USQ
minvalue 1
maxvalue 9999999999999999999999999999
start with 2355
increment by 1
cache 20;

prompt
prompt Creating sequence FG_DYNAMICPARAMS_SEQ
prompt ======================================
prompt
create sequence FG_DYNAMICPARAMS_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 4609
increment by 1
cache 20;

prompt
prompt Creating sequence FG_FORMADDITIONALDATA_SEQ
prompt ===========================================
prompt
create sequence FG_FORMADDITIONALDATA_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 95386
increment by 1
cache 20;

prompt
prompt Creating sequence FG_FORMENTITYTYPES_SEQ
prompt ========================================
prompt
create sequence FG_FORMENTITYTYPES_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 1504
increment by 1
cache 20;

prompt
prompt Creating sequence FG_FORMENTITY_SEQ
prompt ===================================
prompt
create sequence FG_FORMENTITY_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 2166968
increment by 1
cache 20;

prompt
prompt Creating sequence FG_FORMLASTSAVEVALUE_HST_SEQ
prompt ==============================================
prompt
create sequence FG_FORMLASTSAVEVALUE_HST_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 2824
increment by 1
cache 20;

prompt
prompt Creating sequence FG_FORMLASTSAVEVALUE_INF_SEQ
prompt ==============================================
prompt
create sequence FG_FORMLASTSAVEVALUE_INF_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 1108557538
increment by 1
cache 20;

prompt
prompt Creating sequence FG_FORMLASTSAVEVALUE_SEQ
prompt ==========================================
prompt
create sequence FG_FORMLASTSAVEVALUE_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 9307485
increment by 1
cache 20;

prompt
prompt Creating sequence FG_FORM_SEQ
prompt =============================
prompt
create sequence FG_FORM_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 73275
increment by 1
cache 20;

prompt
prompt Creating sequence FG_MAINTENANCE_SITE_SEQ
prompt =========================================
prompt
create sequence FG_MAINTENANCE_SITE_SEQ
minvalue 1
maxvalue 999999999999999999999999999
start with 1
increment by 1
cache 20;

prompt
prompt Creating sequence FG_RESOURCE_SEQ
prompt =================================
prompt
create sequence FG_RESOURCE_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 166171
increment by 1
cache 20;

prompt
prompt Creating sequence FG_RESULTS_SEQ
prompt ================================
prompt
create sequence FG_RESULTS_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 7506
increment by 1
cache 20;

prompt
prompt Creating sequence FG_R_MSG_STATE_HST_SEQ
prompt ========================================
prompt
create sequence FG_R_MSG_STATE_HST_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 10381
increment by 1
cache 20;

prompt
prompt Creating sequence FG_SEQUENCE_FILES_SEQ
prompt =======================================
prompt
create sequence FG_SEQUENCE_FILES_SEQ
minvalue 1
maxvalue 999999999999999999999999999
start with 307150
increment by 1
cache 15;

prompt
prompt Creating sequence FG_SEQUENCE_SEQ
prompt =================================
prompt
create sequence FG_SEQUENCE_SEQ
minvalue 1
maxvalue 999999999999999999999999999
start with 367461
increment by 1
cache 15;

prompt
prompt Creating sequence FG_SEQUENCE_SYSTEM_SEQ
prompt ========================================
prompt
create sequence FG_SEQUENCE_SYSTEM_SEQ
minvalue 1
maxvalue 999999999999999999999999999
start with 1236
increment by 1
cache 20;

prompt
prompt Creating sequence FG_STRUCT_PROJECT_SEQ
prompt =======================================
prompt
create sequence FG_STRUCT_PROJECT_SEQ
minvalue 1
maxvalue 999999999999999999999999999
start with 102
increment by 1
cache 20;

prompt
prompt Creating sequence FG_STRUCT_SUBPROJECT_SEQ
prompt ==========================================
prompt
create sequence FG_STRUCT_SUBPROJECT_SEQ
minvalue 1
maxvalue 999999999999999999999999999
start with 21
increment by 1
cache 20;

prompt
prompt Creating sequence FG_UNITEST_LOG_SEQ
prompt ====================================
prompt
create sequence FG_UNITEST_LOG_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 2320
increment by 1
cache 20;

prompt
prompt Creating sequence FG_UNITTEST_AT_SEQUENCE_SEQ
prompt =============================================
prompt
create sequence FG_UNITTEST_AT_SEQUENCE_SEQ
minvalue 1
maxvalue 999999999999999999999999999
start with 220
increment by 1
cache 15;

prompt
prompt Creating sequence FG_UNITTEST_SEQUENCE_SEQ
prompt ==========================================
prompt
create sequence FG_UNITTEST_SEQUENCE_SEQ
minvalue 1
maxvalue 999999999999999999999999999
start with 315
increment by 1
cache 15;

prompt
prompt Creating sequence FG_USER_REPORT_SEQ
prompt ====================================
prompt
create sequence FG_USER_REPORT_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 931
increment by 1
cache 20;

prompt
prompt Creating sequence FG_WEBIX_OUTPUT_SEQ
prompt =====================================
prompt
create sequence FG_WEBIX_OUTPUT_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 4234
increment by 1
cache 20;

prompt
prompt Creating sequence P_NOTIF_LISTADDRESGROUP_SEQ
prompt =============================================
prompt
create sequence P_NOTIF_LISTADDRESGROUP_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 1326
increment by 1
cache 20;

prompt
prompt Creating sequence P_NOTIF_MODULE_TYPE_SEQ
prompt =========================================
prompt
create sequence P_NOTIF_MODULE_TYPE_SEQ
minvalue 0
maxvalue 999999999999999999999999999
start with 309
increment by 1
cache 20;

prompt
prompt Creating view FG_FORMENTITY_COL_LEN_V
prompt =====================================
prompt
create or replace view fg_formentity_col_len_v as
select distinct t.entityimpclass, t1.formcode, t.entityimpcode, t1.Formcode_Entity,
                decode(t.entityimpclass,'ElementParamMonitoringImp',4000,'ElementDynamicParamsImp',4000,'ElementTextareaImp',4000
                ,'ElementDataTableApiImp',nvl2(REGEXP_SUBSTR(t1.formcode_entity,
                                   '^SampleSelect|ColumnSelect|BatchSelect|RequestSelect|InstrumentSelect$')
                                       ,4000,500),500) as col_length --ElementParamMonitoringImp,ElementDynamicParamsImp,ElementTextareaImp
from FG_FORMENTITY t,
     fG_FORM t1
where t.formcode = t1.formcode;

prompt
prompt Creating view FG_FORMENTITY_V
prompt =============================
prompt
CREATE OR REPLACE VIEW FG_FORMENTITY_V AS
SELECT "ID",
          "FORMCODE",
          "NUMBEROFORDER",
          "ENTITYTYPE",
          "ENTITYIMPCODE",
          "ENTITYIMPCLASS",
          SUBSTR("ENTITYIMPINIT",2,LENGTH("ENTITYIMPINIT") - 2) AS "ENTITYIMPINIT",
          --replace(json_ENTITYIMPINIT,'","','", "') AS "ENTITYIMPINIT",
          json_ENTITYIMPINIT
FROM
(
  select  "ID",
          "FORMCODE",
          "NUMBEROFORDER",
          "ENTITYTYPE",
          "ENTITYIMPCODE",
          "ENTITYIMPCLASS",
          REGEXP_REPLACE (
                          REGEXP_REPLACE(
                                          t.ENTITYIMPINIT,
                                          '","',
                                          ', '
                                          ),
                          '"',
                          ''
                          ) as  "ENTITYIMPINIT", --for display
         /* REGEXP_REPLACE(
                        t.ENTITYIMPINIT,
                        '","',
                        ', '
                        ) as  "ENTITYIMPINIT", --for display*/
         t.ENTITYIMPINIT as json_ENTITYIMPINIT
  from fg_formentity t
  --where t.entityimpclass <> 'ElementDataTableWebixImp'
);

prompt
prompt Creating view FG_I_SCREENS_V
prompt ============================
prompt
create or replace view fg_i_screens_v as
select distinct
       sub_category as name, --unique - display in maintenance
       '{"name":"' || sub_category || '","category":"' || category_ || '","category_order":"' || category_order || '","sub_category":"' || sub_category || '","sub_category_order":"' || sub_category_order || '","formCode":"' || formCode || '","css_class":"' || class_ || '","system_level":"' || system_ || '"}' as screen_info,
       category_order, sub_category_order,system_
from (
select 'Form Builder' as category_, 1 as category_order, 'Form Builder' as sub_category, 0 as sub_category_order, '' as formCode, 'form-builder' as class_, '1' as system_
from dual
union
select 'Form Builder' as category_, 1 as category_order, 'Admin - Form Entity Summary' as sub_category, 1 as sub_category_order, 'FGEntityConfReport' as formCode, '' as class_, '1' as system_
from dual
)
order by category_order, sub_category_order;

prompt
prompt Creating view FG_S_USER_V
prompt =========================
prompt
create or replace view fg_s_user_v as
select to_number(t.formid) as user_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.UserName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as User_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."FIRSTNAME",t."USERNAME",t."POSITION",t."CHANGEPASSWORD",t."LASTNAME",t."PERMISSIONTABLE",t."TEAMLEADER_ID",t."CUSTOMER_ID",t."DELETED",t."SENSITIVITYLEVEL_ID",t."LOCKED",t."USERROLE_ID",t."USERLDAP",t."PASSWORDDATE",t."MESSAGECHECKINTERVAL",t."LASTNOTIFICATIONCHECK",t."LABORATORY_ID",t."GROUPSCREW",t."UNIT_ID",t."CHGPASSWORDDATE",t."LAST_BREADCRUMB_LINK",t."PASSWORD",t."SITE_ID",t."LASTRETRY",t."RETRYCOUNT",t."EMAIL",t."LASTPASSWORDDATE"
      from FG_S_USER_PIVOT t;

prompt
prompt Creating materialized view FG_FORMELEMENTINFOATMETA_MV
prompt ======================================================
prompt
CREATE MATERIALIZED VIEW FG_FORMELEMENTINFOATMETA_MV
REFRESH FORCE ON DEMAND
AS
SELECT t."FORMCODE",t."ENTITYIMPCODE",t."ELEMENTCLASS",t."DISPLAYLABEL",t."ISPARENTPATHID",t."ADDITIONALDATA",t."ISHIDDEN",
       t."ISSEARCHIDHOLDER",t."FORMCODEENTITYLABEL",t."FORMCODETYPLABEL",max(distinct t.islistid) over (partition by formcode_entity, entityimpcode) as "ISLISTID", t."ISSEARCHELEMENT",
       f."FORMCODE_ENTITY", t.datatype, t.datatype_info
FROM FG_FORMELEMENTINFOATMETA_TMP t,
     fg_form f
where t.formcode = f.formcode;

prompt
prompt Creating materialized view FG_FORMELEMENTINFOATMETA_ID_MV
prompt =========================================================
prompt
create materialized view FG_FORMELEMENTINFOATMETA_ID_MV
refresh force on demand
as
select distinct m.islistid , m.formcode_entity, m.entityimpcode
  from   FG_FORMELEMENTINFOATMETA_MV m
  where 1=1;

prompt
prompt Creating package FORM_TOOL_COPY_ADAMA_DATA
prompt ==========================================
prompt
create or replace package FORM_TOOL_COPY_ADAMA_DATA is
 procedure updateVerionData;
end;
/

prompt
prompt Creating type FG_ACTION_OPERATION_EXC_TYPE
prompt ==========================================
prompt
CREATE OR REPLACE TYPE "FG_ACTION_OPERATION_EXC_TYPE" as object (
    actionoperation VARCHAR2(4000),
    operation       VARCHAR2(4000),
    sample_id       VARCHAR2(500),
    action_id       NUMBER,
    comments        VARCHAR2(4000),
    comment_id_     VARCHAR2(4000)
)
/

prompt
prompt Creating type FG_ACTION_OPERATION_EXC_TABLE
prompt ===========================================
prompt
CREATE OR REPLACE TYPE "FG_ACTION_OPERATION_EXC_TABLE" IS TABLE OF "FG_ACTION_OPERATION_EXC_TYPE"
/

prompt
prompt Creating type FG_ACTION_OPERATION_TYPE
prompt ======================================
prompt
CREATE OR REPLACE TYPE "FG_ACTION_OPERATION_TYPE" as object (
    actionoperation VARCHAR2(4000),
    operation       VARCHAR2(4000),
    sample_id       VARCHAR2(500),
    action_id       NUMBER,
    results         VARCHAR2(4000),
    documents       VARCHAR2(4000),
    comments        VARCHAR2(4000),
    action_step_id  VARCHAR2(500)
)
/

prompt
prompt Creating type FG_ACTION_OPERATION_TABLE
prompt =======================================
prompt
CREATE OR REPLACE TYPE "FG_ACTION_OPERATION_TABLE" IS TABLE OF "FG_ACTION_OPERATION_TYPE"
/

prompt
prompt Creating type FG_R_ACTIONSUMMARY_EXC_TYPE
prompt =========================================
prompt
CREATE OR REPLACE TYPE "FG_R_ACTIONSUMMARY_EXC_TYPE" as object (
  step_id                  VARCHAR2(500),
  action_id                NUMBER,
  experiment_id_for_report VARCHAR2(500),
  stepstatusname           VARCHAR2(500),
  actionoperation          VARCHAR2(4000),
  "#"                      NUMBER,
  "Action"                 VARCHAR2(500),
  "Instruction"            VARCHAR2(500),
  "Observation"            VARCHAR2(4000),
  "Start Date"             DATE,
  "Start Time"             VARCHAR2(500),
  "Finish Date"            DATE,
  "Finish Time"            VARCHAR2(500),
  "Parameters"             VARCHAR2(4000),
  "Sample_SMARTLINK"       VARCHAR2(626),
  "Operation_SMARTLINK"    VARCHAR2(4000),
  results_smartpivotsql    VARCHAR2(575),
  "Comments"               VARCHAR2(4000)
)
/

prompt
prompt Creating type FG_R_ACTIONSUMMARY_EXC_TABLE
prompt ==========================================
prompt
CREATE OR REPLACE TYPE "FG_R_ACTIONSUMMARY_EXC_TABLE" IS TABLE OF "FG_R_ACTIONSUMMARY_EXC_TYPE"
/

prompt
prompt Creating type FG_R_ACTIONSUMMARY_TYPE
prompt =====================================
prompt
CREATE OR REPLACE TYPE "FG_R_ACTIONSUMMARY_TYPE" as object (
  step_id                  VARCHAR2(500),
  action_id                NUMBER,
  experiment_id_for_report VARCHAR2(500),
  stepstatusname           VARCHAR2(500),
  actionoperation          VARCHAR2(4000),
  "#"                       NUMBER,
  "Action"                 VARCHAR2(500),
  "Instruction"            VARCHAR2(500),
  "Observation_SMARTHTML"  CLOB,
  "Start date"             DATE,
  "Start time"             VARCHAR2(500),
  "Finish date"            DATE,
  "Finish time"            VARCHAR2(500),
  "Parameters"             VARCHAR2(4000),
  "Sample_SMARTLINK"       VARCHAR2(4000),
  "Operation_SMARTLINK"    VARCHAR2(4000),
  results_smartpivotsql    VARCHAR2(4000),
  "Document_SMARTFILE"     VARCHAR2(4000),
  "Comments"               VARCHAR2(4000)
)
/

prompt
prompt Creating type FG_R_ACTIONSUMMARY_TABLE
prompt ======================================
prompt
CREATE OR REPLACE TYPE "FG_R_ACTIONSUMMARY_TABLE" IS TABLE OF "FG_R_ACTIONSUMMARY_TYPE"
/

prompt
prompt Creating type TYPE_TABLE_VARCHAR
prompt ================================
prompt
CREATE OR REPLACE TYPE TYPE_TABLE_VARCHAR as table of VARCHAR(32767)
/

prompt
prompt Creating function CLOBFROMBLOB
prompt ==============================
prompt
create or replace function clobfromblob(p_blob blob) return clob is
      l_clob         clob;
      l_dest_offsset integer := 1;
      l_src_offsset  integer := 1;
      l_lang_context integer := dbms_lob.default_lang_ctx;
      l_warning      integer;

   begin

      if p_blob is null then
         return null;
      end if;

      dbms_lob.createTemporary(lob_loc => l_clob
                              ,cache   => false);

      dbms_lob.converttoclob(dest_lob     => l_clob
                            ,src_blob     => p_blob
                            ,amount       => dbms_lob.lobmaxsize
                            ,dest_offset  => l_dest_offsset
                            ,src_offset   => l_src_offsset
                            ,blob_csid    => dbms_lob.default_csid
                            ,lang_context => l_lang_context
                            ,warning      => l_warning);

      return l_clob;

   end;
/

prompt
prompt Creating function CONCAT_CLOB
prompt =============================
prompt
CREATE OR REPLACE FUNCTION CONCAT_CLOB(A IN CLOB, B IN CLOB) RETURN CLOB IS
C CLOB;
BEGIN
  dbms_lob.createtemporary(C, TRUE);
  DBMS_LOB.APPEND(C, A);
  DBMS_LOB.APPEND(C, B);
  RETURN C;
END;
/

prompt
prompt Creating function FG_AUTHENTICATE_ACCESS
prompt ========================================
prompt
create or replace function fg_authenticate_access(user_name_in varchar2, ip_address_in varchar2, status_in varchar2)
return number as
ret_code_out number;

User_Id_ fg_s_user_v.User_Id%type;

begin
  select t.User_Id into User_Id_
  from   fg_s_user_v t
  where  t.username = user_name_in;

  insert into fg_access_log (user_id, station_ip, time_stamp, success_ind) values (User_Id_, ip_address_in, sysdate, status_in);
  commit;
  ret_code_out := 0;
  return ret_code_out;
end;
/

prompt
prompt Creating function FG_AUTHENTICATE_USER
prompt ======================================
prompt
create or replace function FG_AUTHENTICATE_USER(user_name_in varchar2, enc_pwd_in varchar2, is_login_in number, ip_address_in varchar2, ts_in varchar default '0') --form_ts_in varchar default 0
return number as
ret_code_out number;
--------------------------------------------------------------
--  return codes:                                           --
--               -1 = No relevant user (by name) was found  --
--                0 = renew password process                --
--                1 = success: password not expired         --
--                2 = success: password within grace period --
--                3 = fail: account is already locked       --
--                4 = fail: password expired (contact admin)--
--                9 = fail: account just got locked  (currently unused, as wrong password doesn't alert of lock)
--------------------------------------------------------------
retry_Count number := 0;
retryLimit number := 0;
expireDate date;
graceAlertDate date;
loginTimeout number;
last_Retry date;
lastRetryChar varchar2(500);
UserRoleName_ varchar2(500); --fg_s_user_v.UserRoleName%type;
Locked_ fg_s_user_v.LOCKED%type;
Password_ fg_s_user_v.Password%type;
User_Id_ fg_s_user_v.User_Id%type;
LastPasswordDate_ fg_s_user_v.LastPasswordDate%type;

begin
  dbms_output.put_line(ts_in);

select /*+ NO_RESULT_CACHE */ t.LOCKED, t.PASSWORD, t.User_Id, t.LASTPASSWORDDATE, t.UserName into Locked_, Password_, User_Id_, LASTPASSWORDDATE_,UserRoleName_
  from   fg_s_user_v t
  where  t.username = user_name_in
  and    decode(nvl(t.DELETED,0),0,nvl(t.active, 1),0) <> 0;
  
  -- verify password
  if Password_ = enc_pwd_in then
        -- correct password, check if user is locked: user acknowledged only if password is correct
        if Locked_ = 1 AND nvl(lower(UserRoleName_),'NA') <> 'admin'
           then
          ret_code_out := 3;
          insert into fg_access_log (user_id, station_ip, time_stamp, success_ind, TS_IN_INFO)
          values (User_Id_, ip_address_in, sysdate, 'NO', ts_in);
          commit;
          return ret_code_out;
        end if;

        -- differentiate between loging and e-sign
        if is_login_in = 1 AND nvl(lower(UserRoleName_),'NA') <> 'admin'
          then
              -- check whether a renewal process
              if LastPasswordDate_ is null then
                ret_code_out := 0;
                insert into fg_access_log (user_id, station_ip, time_stamp, success_ind, TS_IN_INFO)
                values (User_Id_, ip_address_in, sysdate, 'YES', ts_in);
                commit;
                return ret_code_out;
              end if;

              -- verify if password expired
              if nullif(LastPasswordDate_,'00/00/0000') is null then
                 select s.login_timeout,
                         sysdate + s.password_aging,
                         sysdate + s.password_aging - s.grace_period
                  into   loginTimeout,
                         expireDate,
                         graceAlertDate
                  from   fg_sys_param s;
              else
                  select s.login_timeout,
                         (to_date(LastPasswordDate_,'dd/MM/yyyy') + s.password_aging),
                         (to_date(LastPasswordDate_,'dd/MM/yyyy') + s.password_aging - s.grace_period)
                  into   loginTimeout,
                         expireDate,
                         graceAlertDate
                  from   fg_sys_param s, fg_s_user_v u
                  where  u.user_id = User_Id_;
              end if;

              if trunc(sysdate) > trunc(expireDate) then
                  ret_code_out := 4;
                  insert into fg_access_log (user_id, station_ip, time_stamp, success_ind, TS_IN_INFO)
                  values (User_Id_, ip_address_in, sysdate, 'NO', ts_in);
                  
                  commit;
                  return ret_code_out;
              elsif trunc(sysdate) < trunc(graceAlertDate) then
                  -- success: password wasn't expired
                  ret_code_out := 1;
              else
                  -- success: password within grace period
                  ret_code_out := 2;
              end if;

              -- reset retry_count
              update FG_S_USER_PIVOT u
                 set u.retrycount = 0
               where u.formid = User_Id_;
              commit;

              -- update access log
              insert into fg_access_log (user_id, station_ip, time_stamp, success_ind, TS_IN_INFO)
              values (User_Id_, ip_address_in, sysdate, 'YES', ts_in);
              
              commit;
              return ret_code_out;
        else
          -- user is authenticated
          if is_login_in = 1 then -- ITS ADMIN
            insert into fg_access_log (user_id, station_ip, time_stamp, success_ind, TS_IN_INFO)
            values (User_Id_, ip_address_in, sysdate, 'YES', ts_in);
            --commit;
          end if;
          ret_code_out := 1;
          commit;
          return ret_code_out;
        end if;

  elsif is_login_in = 1 then
        -- wrong password, update retry_count and last retry (not for e-sign process)
        -- fetch retry-limit and count
        select s.retry_count, u.retrycount, nullif(u.lastretry,'00/00/0000')
        into   retryLimit, retry_Count, lastRetryChar --lastRetry
        from   fg_sys_param s, fg_s_user_v u
        where  u.user_id = User_Id_;

        if lastRetryChar is null then
          last_Retry := sysdate;
        elsif (sysdate - to_date(lastRetryChar,'dd/MM/yyyy')) > loginTimeout/24/60  then
          last_Retry := sysdate;
          retry_Count := 0;
        end if;

        retry_Count := retry_Count + 1;

        update FG_S_USER_PIVOT u
           set u.retrycount = retry_Count,
               u.lastretry = last_Retry,
               u.locked = case when retry_Count >= retryLimit then
                                 1
                               else
                                 0
                          end
         where u.formid = User_Id_;
         commit;
         -- trying to login with wrong password doesn't alert when user is locked!
         ret_code_out := -1;

         -- update access log
         insert into fg_access_log (user_id, station_ip, time_stamp, success_ind, TS_IN_INFO)
         values (User_Id_, ip_address_in, sysdate, 'NO', ts_in);
         
         commit;
         return ret_code_out;
  end if;
  EXCEPTION
    when others then
      -- no relevant record was found (i.e wrong user or is deleted)
      ret_code_out := -1;

      if is_login_in = 1 then
        -- update access log
        insert into fg_access_log (user_id, station_ip, time_stamp, success_ind)
        values (-1, ip_address_in, sysdate, 'NO');
        commit;
      end if;
  
  return ret_code_out;
end;
/

prompt
prompt Creating function FG_GET_STRUCT_FILE_ID
prompt =======================================
prompt
create or replace function FG_GET_STRUCT_FILE_ID(form_code_in varchar, formid_in varchar default null, ts_in varchar default '0') return number as
 nextValNumber number;
begin
  insert into FG_SEQUENCE_FILES (FORMCODE,INSERTDATE,SOURCE_FORMID)
  --values (decode (form_code_in,'ExperimentMain','Experiment','RequestMain','Request','ExpSeriesMain','ExperimentSeries',form_code_in),sysdate); -- we make it in postsave event here its just ->
  values (form_code_in,sysdate,formid_in);

  select FG_SEQUENCE_FILES_SEQ.CURRVAL into nextValNumber from dual;

  --insert into fg_debug(comments) values('FG_GET_STRUCT_FILE_ID (with timestamp): '|| nextValNumber || ts_in || ', nextValNumber=' || nextValNumber); --commit;

  return nextValNumber;
end;
/

prompt
prompt Creating function FG_CLOB_FILES_CONFEXCEL_INSERT
prompt ================================================
prompt
create or replace function fg_clob_files_confexcel_insert (file_content_in clob) return varchar as
  to_return  varchar(1000);
begin
  to_return:=FG_GET_STRUCT_FILE_ID('SysConfExcelData.excelFile');

  insert into fg_clob_files (file_id,file_name,file_content)
  values (to_return,'',file_content_in);

  return to_return;
end;
/

prompt
prompt Creating function FG_CONCAT_CLOB
prompt ================================
prompt
CREATE OR REPLACE FUNCTION fg_CONCAT_CLOB(A IN CLOB, B IN CLOB) RETURN CLOB IS
C CLOB;
BEGIN
  dbms_lob.createtemporary(C, TRUE);
  DBMS_LOB.APPEND(C, A);
  DBMS_LOB.APPEND(C, B);
  RETURN C;
END;
/

prompt
prompt Creating function FG_FUNCPARSEJSONDATA
prompt ======================================
prompt
create or replace function fg_funcParseJSONData(str_in varchar2) return varchar2 as
  toReturn varchar2(32000);
begin
select replace(str_in,chr(10),' ') into toReturn from dual;
/*TODO (!?) AS:
function funcParseJSONData FOR backslash,new line,carriage return,tab
*/
return toReturn;
exception
  when others then
    return str_in;
end;
/

prompt
prompt Creating function FG_GETDATEDIFF
prompt ================================
prompt
create or replace function fg_getdatediff( p_what in varchar2, p_d1 in date, p_d2 in date ) return number as
      l_result    number;
    begin
      select (p_d1-p_d2) *
             decode( upper(p_what),
                     'SS', 24*60*60, 'MI', 24*60, 'HH', 24, 'D', 1 ,'MO',1/7 , 'Y' , 1/365 ,NULL )
      into l_result from dual;

     return l_result;
    end;
/

prompt
prompt Creating function FG_GET_COLMNS_BY_TABLE
prompt ========================================
prompt
create or replace function fg_get_colmns_by_table(table_name_in varchar) return clob as
   colList varchar2(30000) := '';
begin
  for r in (
    select t.COLUMN_NAME from user_tab_columns t where t.TABLE_NAME = table_name_in
  ) loop
    if length(colList) > 0 then
      colList := colList || ', ' || r.column_name;
    else
      colList := r.column_name;
    end if;
    
  end loop;

  return colList;
end;
/

prompt
prompt Creating function FG_GET_COL_SIZE
prompt =================================
prompt
create or replace function fg_get_col_size(formcode_entity_in varchar, ENTITYIMPCODE_in varchar) RETURN NUMBER AS
 toReturn varchar2(4000);
begin

  select distinct t.col_length into toReturn
  from fg_formentity_col_len_v t
  where  t.Formcode_Entity = formcode_entity_in
  AND    upper(T.entityimpcode) = upper(ENTITYIMPCODE_in);

  return toReturn;
exception
  when others then
    return 4000;
end;
/

prompt
prompt Creating function FG_GET_EXPERIMENT_OPRN_LIST
prompt =============================================
prompt
create or replace function fg_get_experiment_oprn_list (pivotdata_in in sys_refcursor) return clob as
 toReturn clob;
 operationList_ clob;
 operation varchar2(32767);
 formcode_ varchar2(200);
 formnumberid_ varchar2(200);
begin
loop
FETCH
         pivotdata_in
      INTO
         operation;
      EXIT
  when pivotdata_in%notfound  ;
      operationList_ := operationList_ || ';' ||operation;
end loop;

if operationList_ is not null then
  operationList_ := substr(operationList_,2);
end if;

toReturn := operationList_;

/*if length(toReturn) > 4000 then
  --insert into fg_debug(comments,comment_info) values (toReturn,'fg_get_sample_list');
  return null;
end if;*/
close pivotdata_in;
return toReturn;
end;
/

prompt
prompt Creating function FG_GET_LIST_OF_NAMES_BY_ID
prompt ============================================
prompt
create or replace function fg_get_list_of_names_by_id (form_code_in in varchar, list_of_id_in in varchar) return varchar2 as
 toReturn varchar2(4000);
 tmpVarChar varchar2(4000) := '';
 tmpQuery varchar2(4000) := '';

begin
  for r in (
            select regexp_substr(/*'1100,1101,1099'*/list_of_id_in,'[^,]+', 1, level) as id_ from dual
            connect by regexp_substr(/*'1100,1101,1099'*/list_of_id_in, '[^,]+', 1, level) is not null
            )
        loop
          tmpQuery := ' select distinct upper(' || form_code_in || 'Name)
                        FROM FG_S_' || form_code_in || '_V
                        WHERE upper(' || form_code_in || '_id) = ' || r.id_;
          if toReturn is not null then
            toReturn := toReturn || ',';
          end if;
          execute immediate tmpQuery into tmpVarChar;
          toReturn := toReturn || tmpVarChar;
        end loop;
  return toReturn;
  exception
    when others then
      return null;
end;
/

prompt
prompt Creating function FG_GET_NUMERIC
prompt ================================
prompt
CREATE OR REPLACE FUNCTION fg_get_numeric(p_strval in varchar2) RETURN NUMBER
IS
  l_numval NUMBER;
BEGIN
  l_numval := TO_NUMBER(p_strval);
  RETURN l_numval;
EXCEPTION
  WHEN OTHERS THEN
    RETURN null;
END;
/

prompt
prompt Creating function FG_GET_RATIO_TYPE_LIST
prompt ========================================
prompt
create or replace function fg_get_ratio_type_list (selected_id_in in varchar,
                                                   is_new_row in varchar default '',
                                                   protocol_type_in in varchar default '')
return clob as
 toReturn clob;
 p_selected varchar2(32767);
 p_ratioTypeList varchar2(32767);
begin

    for r in (
               select t.val, t."ID", t.short_name, '{"ID":"'||t."ID"||'","displayName":"'||t.val||'"}' as sel_val
               from
               (
                 select 'Solvent Volume by Reactant Quantity' as val, 'SolventVolByReactantQnty' as "ID", 'VQ' as short_name, 1 as "ORDER"
                 from dual
                 where protocol_type_in <> 'Continuous Process'
                 union all
                 select 'Solvent Quantity by Reactant Quantity' as val, 'SolventQntyByReactantQnty' as "ID", 'QQ' as short_name, 2 as "ORDER"
                 from dual
                 where protocol_type_in <> 'Continuous Process'
                 union all
                 select 'Solvent Volume by Reactant Moles' as val, 'SolventVolByReactantMoles' as "ID", 'VM' as short_name, 3 as "ORDER"
                 from dual
                 where protocol_type_in <> 'Continuous Process'
                 union all
                 select 'Solvent Moles by Reactant Moles' as val, 'SolventMolesByReactantMoles' as "ID", 'MM' as short_name, 4 as "ORDER"
                 from dual
                )t
                order by  t."ORDER"
            )
    loop

          p_ratioTypeList := p_ratioTypeList ||','||'{"ID":"'||r.id||'","VAL":"'||r.val||'"}';

          if selected_id_in is not null and selected_id_in = r.id then
            p_selected := r.sel_val;
          elsif is_new_row is not null and is_new_row = '1' and protocol_type_in is not null then
            if lower(protocol_type_in) = 'organic' and r.short_name = 'VQ' then
               p_selected := r.sel_val;
            elsif lower(protocol_type_in) = 'pilot' and r.short_name = 'QQ' then
               p_selected := r.sel_val;
            elsif lower(protocol_type_in) = 'continuous process' and r.short_name = 'MM' then
               p_selected := r.sel_val;
            end if;
          end if;
    end loop;

  if p_ratioTypeList is not null then
    p_ratioTypeList := substr(p_ratioTypeList,2);
  end if;

  toReturn := '{"displayName":[' || p_selected || '],"htmlType":"select","maxShownResults":"20","customFuncName":"",'||
              ' "colCalcId":"ratiotype_id","dbColumnName":"ratiotype_id","formCode":"'||'InvItemMaterial'||'","formId":"'||selected_id_in||'",'||
              ' "fullList":['|| p_ratioTypeList ||']}';



return toReturn;
end;
/

prompt
prompt Creating function FG_GET_REPREF_RULENAME_LIST
prompt =============================================
prompt
create or replace function fg_get_repref_rulename_list (id_in in varchar)
return clob as
 toReturn clob;
 selectedList_ varchar2(32767);
 objList_ varchar2(32767);
begin


  for r in (
            select distinct * from (
                select '1' as ID ,'Main Solvent' AS NAME
                       ,'{"ID":"'|| '1' ||'","VAL":"'|| 'Main Solvent' ||'"}' as paramobj
                from dual
                union all
                select '2' as ID ,'Ligand' AS NAME
                       ,'{"ID":"'|| '2' ||'","VAL":"'|| 'Ligand' ||'"}' as paramobj
                from dual
            )
        )
  loop
        if r.ID is not null and (r.ID = id_in OR R.NAME = id_in) then
          selectedList_ := '{"ID":"'||r.ID||'","displayName":"'|| r.NAME||'"}';
        end if;

        if r.ID is not null then
          objList_ := objList_ || ',' || r.paramobj;
        end if;
  end loop;

  if objList_ is not null then
    objList_ := substr(objList_,2);
  end if;

  toReturn :=  '{"displayName":[' || selectedList_ || '],"htmlType":"select","dbColumnName":"RULENAME", "colCalcId":"RULENAME",'||
             ' "allowSingleDeselect":"false", "autoSave":"true",'||
             ' "fullList":['|| objList_ ||']}';

return toReturn;
end;
/

prompt
prompt Creating function FG_GET_RICHTEXT_DISPLAY
prompt =========================================
prompt
create or replace function fg_get_RichText_display ( RichText_id_in in varchar, displayType_in in number default 1) return varchar2 as
 toReturn varchar2(4000);
begin
   --DBMS_OUTPUT.put_line('CALL fg_get_RichText_display'); --kd 24022020 commented this row because was error (not enough space in the buffer)

  if RichText_id_in is null then
    return null;
  end if;
--&lt;
  if displayType_in = 1 then -- 1: simple text top 4000 
    select trim(
                replace(
                    replace(
                        regexp_replace(
                                       regexp_replace(
                                                       regexp_replace(
                                                                      regexp_replace(
                                                                                     DBMS_LOB.substr(t.file_content, 4000),
                                                                                     --DBMS_LOB.substr(fg_no_html(t.file_content), 4000), 
                                                                                    -- '<.*?>',' ')
                                                                                     '<.*?>' , '', 1, 0, 'n')
                                                                      ,'\&lt;.*?\&gt;',' ')
                                                       ,'[[:space:]]',' ')
                                       ,' {2,}', ' ')
                        ,'&nbsp;','')
                    ,'"','\"')
                ) into toReturn
    from fg_richtext t
    where  t.file_id = RichText_id_in;
   elsif displayType_in = 2 then  -- 2: return text (richtext getText())
    select t.file_content_text
    into toReturn
    from fg_richtext t
    where  t.file_id = RichText_id_in;
  end if;  

  /*if displayType_in = 2 then -- 1: simple text top 1000
    select DBMS_LOB.substr(fg_no_html(t.file_content), 1000) into toReturn
    from fg_richtext t
    where  t.file_id = RichText_id_in;
  end if; --fg_get_richtext_isnull*/

   return toReturn;
exception
  when others then
    return null;
end;
/

prompt
prompt Creating function FG_GET_RICHTEXT_DISPLAY_CLOB
prompt ==============================================
prompt
create or replace function fg_get_RichText_display_clob ( RichText_id_in in varchar, displayType_in in number default 1) return clob as
 toReturn clob;
begin

  if RichText_id_in is null then
    return null;
  end if;

  if displayType_in = 1 then
    select t.file_content as file_content
    into toReturn
    from fg_richtext t
    where  t.file_id = RichText_id_in;
  end if;

   return toReturn;
exception
  when others then
    return null;
end;
/

prompt
prompt Creating function FG_GET_RICHTEXT_ISNULL
prompt ========================================
prompt
create or replace function fg_get_richtext_isnull (RichText_id_in in varchar) return number as
  isEmpty_ number;
begin
  if RichText_id_in is null then
    return 1;
  end if;
  
  select decode(length(DBMS_LOB.substr(t.file_content_text, 1000)),null,1,0,1,0) into isEmpty_
  from fg_richtext t
  where  t.file_id = RichText_id_in;
   
  return isEmpty_;
exception
  when others then
    return 1;
end;
/

prompt
prompt Creating function FG_GET_STRUCT_FORM_ID
prompt =======================================
prompt
create or replace function FG_GET_STRUCT_FORM_ID(form_code_in varchar, parentformid_in varchar default '-1', userid_in varchar default '-1', ts_in varchar default '0') return number as
 nextValNumber number;
begin
  insert into FG_SEQUENCE (FORMCODE,INSERTDATE,GENERATE_USERID,GENERATE_FORMID)
  --values (decode (form_code_in,'ExperimentMain','Experiment','RequestMain','Request','ExpSeriesMain','ExperimentSeries',form_code_in),sysdate); -- we make it in postsave event here its just ->
  values (form_code_in,sysdate,userid_in, parentformid_in);

   select FG_SEQUENCE_SEQ.CURRVAL into nextValNumber from dual;

   insert into fg_debug(comments) values('FG_GET_STRUCT_FORM_ID (with timestamp): '|| nextValNumber || ts_in || ', nextValNumber=' || nextValNumber); --commit;

  return nextValNumber;
end;
/

prompt
prompt Creating function FG_GET_UOMTYPE_BY_RATETYPE
prompt ============================================
prompt
create or replace function fg_get_uomtype_by_ratetype (rate_type_in in varchar) return varchar2 as
 toReturn varchar2(100);
begin

  if rate_type_in = 'quantity' then
    toReturn := 'rate';
  elsif rate_type_in = 'mole' then
    toReturn := 'molRate';
  else
    toReturn := 'rateVolume';
  end if;

  return toReturn;
end;
/

prompt
prompt Creating function FG_GET_VALUE_FROM_JSON
prompt ========================================
prompt
CREATE OR REPLACE FUNCTION FG_GET_VALUE_FROM_JSON (json_in varchar2, code_in varchar2, defaultVal_in varchar2 default 'NA', innerJsonAttr_in varchar2 default '"text":') return varchar2 as
   indexOfCode number;
   firstCharOfValue varchar2(1);
   toReturn varchar2(2000);
begin
     indexOfCode := INSTR(LOWER(json_in),LOWER('"' || code_in || '"'), 1, 1);
     if indexOfCode = 0 then
        return defaultVal_in;
     end if;

     DBMS_OUTPUT.put_line(indexOfCode);

     firstCharOfValue := SUBSTR (json_in, indexOfCode + LENGTH('"' || code_in || '"') + 1, 1);
     -- value as string
     if firstCharOfValue = '"' then
         toReturn := SUBSTR(
                        json_in,
                        indexOfCode + LENGTH('"' || code_in || '"') + 2,
                        instr( SUBSTR(
                                       json_in,
                                       indexOfCode + LENGTH('"' || code_in || '"') + 2
                                      )
                               ,'"'
                             ) -1
                      );

      elsif firstCharOfValue = '{' then   -- value as json
         indexOfCode :=  INSTR(LOWER(json_in), lower(innerJsonAttr_in), indexOfCode, 1);
         toReturn := SUBSTR(
                        json_in,
                        indexOfCode + LENGTH(innerJsonAttr_in) + 1,
                        instr( SUBSTR(
                                       json_in,
                                       indexOfCode + LENGTH(innerJsonAttr_in) + 1
                                      )
                               ,'"'
                             ) -1
                      );
      else --num or bollean or array
         toReturn :=  trim(SUBSTR (json_in,
                              indexOfCode + LENGTH('"' || code_in || '"') + 1,
                              instr( SUBSTR(
                                       json_in,
                                       indexOfCode + LENGTH('"' || code_in || '"') + 1
                                      )
                               ,','
                             ) -1
                      ));
      end if;
     return toReturn;
exception
  when others then
    return defaultVal_in;
end;
/

prompt
prompt Creating function FG_GET_VALUE_FROM_ENTITY_INT
prompt ==============================================
prompt
CREATE OR REPLACE FUNCTION FG_GET_VALUE_FROM_ENTITY_INT (formCode_in varchar2, entityimpcode_in varchar2, key_in varchar2, defaultVal_in varchar2 default 'NA') return varchar2 as
   
   toReturn varchar2(2000);
begin 
     select fg_get_value_from_json(t.entityimpinit,key_in,defaultVal_in) into toReturn
     from fg_formentity t
     where lower(t.formcode) = lower(formCode_in)
     and   lower(t.entityimpcode) = lower(entityimpcode_in);

     return toReturn;
exception
  when others then
    return defaultVal_in;
end;
/

prompt
prompt Creating function FG_NO_HTML
prompt ============================
prompt
CREATE OR REPLACE FUNCTION fg_no_html (p_string  IN CLOB)
  RETURN  CLOB
AS
  v_string_in   CLOB := p_string;
  v_string_out  CLOB;
BEGIN
  WHILE INSTR (v_string_in, '>') > 0 LOOP
    v_string_out := v_string_out
     || SUBSTR (v_string_in, 1, INSTR (v_string_in, '<') - 1);
    v_string_in  := SUBSTR (v_string_in, INSTR (v_string_in, '>') + 1);
  END LOOP;
  v_string_out := v_string_out || v_string_in;
  RETURN v_string_out;
END;
/

prompt
prompt Creating function FG_SET_STRUCT_ALL_V
prompt =====================================
prompt
create or replace function FG_SET_STRUCT_ALL_V (viewName_in varchar) return varchar  as
  
  --l_search varchar2(1000) := 'union';
  l_char varchar2(32767);
  toReturn varchar2(32767);

begin
  for rec in (select text from user_views where VIEW_NAME = upper(viewName_in)  )
  loop
    l_char := rec.text;
    if instr(l_char,'--t.* end! edit only the code below...') > 0 then
       toReturn :=
      'create or replace view ' || upper(viewName_in) || ' as ' || chr(10)|| 'select t.*'  || chr(10) ||
      substr(l_char, 
             instr(l_char,
                   '--t.* end! edit only the code below...')
             );
             dbms_output.put_line(toReturn);
      EXECUTE IMMEDIATE toReturn;       
    end if;
   
     
  end loop;

  return 1;

end;
/

prompt
prompt Creating function FG_SET_STRUCT_PIVOT_TABLE_ACTV
prompt ================================================
prompt
CREATE OR REPLACE FUNCTION FG_SET_STRUCT_PIVOT_TABLE_ACTV (formCode_in varchar2, dropAndCreateTable_in number default 1) return number as
  
    is_pivot_exists number;
                
    col_list_pivot_statment varchar(32767);
            
    --TYPE ref_cursor IS REF CURSOR;
    
    --cur REF_CURSOR;
    
    --cVal_ varchar(100);
    
    sql_pivot varchar(32767); 
    
    sql_create_BASIC_v varchar(32767); 
    
    sql_create_v varchar(32767); 
    
    isViewExists number; 
    
    CREATE_VIEW_FLAG number := 0;
    CREATE_VIEW_FORMCODE_CSV_LIST varchar(32767) := '';
    
    result number;
    
    formCodeEntity varchar(100);
    
begin  
    select nvl(f.formcode_entity,f.formcode) into formCodeEntity from fg_form f where f.formcode = formCode_in;
  
    select count(*) into is_pivot_exists
    from user_tables t
    where upper(t.TABLE_NAME) = upper('FG_S_' || upper(formCodeEntity) || '_PIVOT');
    
    --update CREATE_VIEW_FLAG
    select decode(instr( ',' || CREATE_VIEW_FORMCODE_CSV_LIST || ','
                        , ',' || formCode_in || ',')
                  ,0,0,1) into CREATE_VIEW_FLAG
    from dual;
    
    for cVal in (
                    select distinct t.entityimpcode as val_ 
                    from FG_FORMENTITY t
                    where t.entitytype = 'Element' 
                    --remove not needed entityimpclass
                    and t.entityimpclass not in ('ElementLabelImp','ElementIreportImp','ElementSmartSearchImp') 
                    -- incluse element also from other forms which "mapped" to this from (using the same formcode_entity)
                    and  (t.formcode in (select f1.formcode from fg_form f1 where f1.formcode_entity = formCodeEntity)
                         OR t.formcode = formCode_in)
                    -- remove element with PREVENTSAVE form builder definition
                    -- TODO WITH SCRIPT -> and not exists (select 1 from FG_FORMENTITY t2 WHERE t.id = t2.id and upper(T2.ENTITYIMPINIT) LIKE '%"PREVENTSAVE":TRUE%')
                  )
    LOOP 
      dbms_output.put_line(  cVal.val_ );
      col_list_pivot_statment := col_list_pivot_statment || ',''' || cVal.val_ || ''' as ' ||  cVal.val_;
    END LOOP;
 
    
    sql_pivot := ' 
    SELECT * FROM
    (select "FORMID" as formId,  
           sysdate as "TIMESTAMP",
           first_value(USERID) over (partition by "FORMID" order by t.id desc) AS "CHANGE_BY",
           "SESSIONID",
            NVL("ACTIVE",''1'')  AS "ACTIVE",
           ''' || formCodeEntity || ''' as FORMCODE,
           "ENTITYIMPCODE" as col_,
           "ENTITYIMPVALUE" as val_ 
    from FG_FORMLASTSAVEVALUE t 
    where t.formcode = ''' || formCodeEntity || ''') 
    PIVOT (max(val_) FOR col_ IN (' || substr(col_list_pivot_statment,2) || '))';
 
    insert into fg_debug(comments) values('sql_pivot=' || sql_pivot);               
    dbms_output.put_line('sql_pivot=' || sql_pivot);
 
      --  if col_list_pivot is not null then
      if is_pivot_exists = 1 and dropAndCreateTable_in = 1  then
          EXECUTE IMMEDIATE 'drop table ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT';
      end if;
        
      if dropAndCreateTable_in = 1 then     
        insert into fg_debug(comments) values(' CREATE table ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT' || ' AS ' || sql_pivot); commit;
        EXECUTE IMMEDIATE ' CREATE table ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT' || ' AS ' || sql_pivot;
      end if;
 
      sql_create_BASIC_v := '
      create or replace view FG_S_' || upper(formCodeEntity) || '_V as
      select to_number(t.formid) as ' || lower(formCodeEntity) || '_id,
             t.formid || decode(nvl(t.sessionId,''-1''),''-1'',null, ''-'' || t.sessionId) || decode(nvl(t.active,1),0,''-0'') as form_temp_id,
             ''{"VAL":"'' || t.' || formCodeEntity || 'Name || ''","ID":"'' || t.formid || ''", "ACTIVE":"'' || nvl(t.active,1) || ''"}'' as ' || formCodeEntity || '_objidval,
             t.*
      from ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT' || ' t ';
   
    EXECUTE IMMEDIATE sql_create_BASIC_v;
    
    --update from code loop for views >= all_v
    for r in (
            select distinct upper(t.formcode) as formCode_uppercase, 
                            t.formcode as formCode_
            from FG_FORM T
            WHERE 1=1
            -- incluse element also from other forms which "mapped" to this from (using the same formcode_entity)
            and  (t.formcode in (select f1.formcode from fg_form f1 where f1.formcode_entity = formCodeEntity)
                         OR t.formcode = formCode_in)
    )
    loop
            --CREATE 'ALL' VIEW IF NOT EXISTS (to avoid override views we develop!!!)
            select COUNT(*) into isViewExists
            from user_views t
            where t.VIEW_NAME = upper('FG_S_' || r.formCode_uppercase || '_ALL_V');
            
            if CREATE_VIEW_FLAG = 1 or isViewExists = 0 then
              sql_create_v := '
              create or replace view FG_S_' || r.formCode_uppercase || '_ALL_V as
              select t.*
                     --t.* end! edit only the code below...
              from FG_S_' || upper(formCodeEntity) || '_V t ';
              EXECUTE IMMEDIATE sql_create_v;
            else
              result := fg_set_struct_all_v('FG_S_' || r.formCode_uppercase || '_ALL_V');
              DBMS_OUTPUT.put_line(result);
            end if;
             
            --.. insert to FG_RESOURCE
            select COUNT(*) into isViewExists
            from FG_RESOURCE t
            where t.code = 'FG_S_' || r.formCode_uppercase || '_ALL_V';
            
            if isViewExists = 0 then
                 INSERT INTO FG_RESOURCE (TYPE,CODE,VALUE,INFO) VALUES ('CATALOG_ORACLE_TABLE','FG_S_' || r.formCode_uppercase || '_ALL_V','FG_S_' || r.formCode_uppercase || '_ALL_V', r.formCode_uppercase || ' view. insert to FG_RESOURCE by FG_SET_STRUCT_PIVOT_TABLE func as ' || to_char(sysdate,'dd/MM/yyyy HH24:MI')); 
            end if;

            
            --CREATE 'DT' (Data table) VIEW IF NOT EXISTS (to avoid override views we develop!!!)
            select COUNT(*) into isViewExists
            from user_views t
            where t.VIEW_NAME = upper('FG_S_' || r.formCode_uppercase || '_DT_V');
            
            if CREATE_VIEW_FLAG = 1 or isViewExists = 0 then
              sql_create_v := '
              create or replace view FG_S_' || r.formCode_uppercase || '_DT_V as
              select *
              from FG_S_' || r.formCode_uppercase || '_ALL_V t ';
              EXECUTE IMMEDIATE sql_create_v;
            end if;
            
            --CREATE 'INF' (Data table) VIEW IF NOT EXISTS (to avoid override views we develop!!!)
            select COUNT(*) into isViewExists
            from user_views t
            where t.VIEW_NAME = upper('FG_S_' || r.formCode_uppercase || '_INF_V');
            
            if CREATE_VIEW_FLAG = 1 or isViewExists = 0 then
              sql_create_v := '
              create or replace view FG_S_' || r.formCode_uppercase || '_INF_V as
              select ''' || r.formCode_ || ''' as formCode, t.formid as id, t.' || formCodeEntity || 'Name as name
              from FG_S_' || r.formCode_uppercase || '_ALL_V t ';
              EXECUTE IMMEDIATE sql_create_v;
            end if;
            
            --.. insert to FG_RESOURCE
            select COUNT(*) into isViewExists
            from FG_RESOURCE t
            where t.code = 'FG_S_' || r.formCode_uppercase || '_DT_V';
            
            if isViewExists = 0 then
                 INSERT INTO FG_RESOURCE (TYPE,CODE,VALUE,INFO) VALUES ('CATALOG_ORACLE_TABLE','FG_S_' || r.formCode_uppercase || '_DT_V','FG_S_' || r.formCode_uppercase || '_DT_V', r.formCode_uppercase || ' view. insert to FG_RESOURCE by FG_SET_STRUCT_PIVOT_TABLE func as ' || to_char(sysdate,'dd/MM/yyyy HH24:MI')); 
            end if;
            
             --CREATE AUTHEN VIEW (Data table) VIEW IF NOT EXISTS (to avoid override views we develop!!!) --FG_AUTHEN_PROJECT_V
            select COUNT(*) into isViewExists
            from user_views t
            where t.VIEW_NAME = upper('FG_AUTHEN_' || r.formCode_uppercase || '_V');
            
            if CREATE_VIEW_FLAG = 1 or isViewExists = 0 then
              sql_create_v := '
              create or replace view FG_AUTHEN_' || r.formCode_uppercase || '_V as
              select *
              from FG_S_' || r.formCode_uppercase || '_ALL_V t where rownum <= 1';
              EXECUTE IMMEDIATE sql_create_v;
            end if;
            
            --.. insert to FG_RESOURCE
            /*select COUNT(*) into isViewExists
            from FG_RESOURCE t
            where t.code = 'FG_AUTHEN_' || upper(formCode_in) || '_V';
            
            if isViewExists = 0 then
                 INSERT INTO FG_RESOURCE (TYPE,CODE,VALUE,INFO) VALUES ('CATALOG_ORACLE_TABLE','FG_AUTHEN_' || upper(formCode_in) || '_V','FG_AUTHEN_' || upper(formCode_in) || '_V', upper(formCode_in) || ' view. insert to FG_RESOURCE by FG_SET_STRUCT_PIVOT_TABLE func as ' || to_char(sysdate,'dd/MM/yyyy HH24:MI')); 
            end if;*/
    end loop;
     
    
                
    return 0;
end;
/

prompt
prompt Creating function FORMIDFINDER
prompt ==============================
prompt
create or replace function formIdFinder (startformid_in varchar) return varchar as
 rcount_ number := 1;
 rlist_ varchar(32000) := startformid_in;
 rcomp_ varchar(32000) := 'start';
 rtmp_ varchar(32000);
begin

WHILE rcomp_ <> rlist_  
LOOP
   rlist_ := rcomp_;
  
  SELECT LISTAGG(T.ENTITYIMPVALUE , ',')
         WITHIN GROUP (ORDER BY T.ENTITYIMPVALUE) into rtmp_
  FROM FG_FORMLASTSAVEVALUE T
  WHERE T.ENTITYIMPVALUE IN (SELECT to_char(ID) FROM FG_SEQUENCE) 
  AND instr( ',' || T.FORMID || ',', ',' || rlist_ || ',') > 0
  AND instr( ',' || T.ENTITYIMPVALUE || ',', ',' || rlist_ || ',') <= 0;

  rlist_:=  rlist_ || ',' || rtmp_;
 
END LOOP;

return rlist_;

end;
/

prompt
prompt Creating function GETTABLECOLUMNLIST
prompt ====================================
prompt
create or replace function getTableColumnList (tableName_in varchar) return varchar as
  colListReturn varchar2(20000);
begin
  select LISTAGG (T.COLUMN_NAME, ',') WITHIN GROUP (ORDER BY T.COLUMN_ID) AS COL_LIST into colListReturn
  from user_tab_columns t
  where t.TABLE_NAME = upper(tableName_in);
  return colListReturn;
end;
/

prompt
prompt Creating function GETTABLECOLUMNLISTNOID
prompt ========================================
prompt
create or replace function getTableColumnListNoId (tableName_in varchar, ignoreList varchar default 'NA', asColFlag number default 0) return varchar as
  colListReturn varchar2(20000);
begin
  select LISTAGG ( decode(asColFlag,1,'NVL(to_char(' || T.COLUMN_NAME || '),''na1'')', 'NVL(to_char('||T.COLUMN_NAME||'),''na1'')') , ',') WITHIN GROUP (ORDER BY T.COLUMN_ID) AS COL_LIST into colListReturn
  from user_tab_columns t
  where t.TABLE_NAME = upper(tableName_in)
  and   INSTR(',' || UPPER(ignoreList) || ',', ',' || T.COLUMN_NAME || ',') = 0
  and   T.COLUMN_NAME not like '%_ID';
  return colListReturn;
end;
/

prompt
prompt Creating function IS_DATE
prompt =========================
prompt
CREATE OR REPLACE FUNCTION is_date(p_strval in varchar2, p_format in varchar2) RETURN DATE
IS
  l_numval DATE;
BEGIN
  l_numval := to_date(p_strval,p_format);
  RETURN l_numval;
EXCEPTION
  WHEN OTHERS THEN
    RETURN NULL;
END is_date;
/

prompt
prompt Creating function IS_NUMERIC
prompt ============================
prompt
create or replace function IS_NUMERIC(P_INPUT IN VARCHAR2) return integer is
  NUM NUMBER ;
begin
  NUM:=TO_NUMBER(P_INPUT);
  RETURN 1;
EXCEPTION WHEN OTHERS THEN
  RETURN 0;
end IS_NUMERIC;
/

prompt
prompt Creating function TO_CHAR_BY_PRECISION_ROUNDED
prompt ==============================================
prompt
create or replace function to_char_by_precision_rounded(numIn number,precIn number)return varchar is
  toReturn varchar(200);
  nines varchar(200) := '9';
  zeros varchar(200) := '0';
begin

  if precIn is null then
    return to_char(numIn);
  end if;

  --init
  WHILE length(nines) < length(floor(abs(round(numIn,precIn))))
  LOOP
       nines := nines || '9';
  END LOOP;

  WHILE length(zeros) < precIn
  LOOP
       zeros := zeros || '0';
  END LOOP;

  --special case
  if  numIn = 0 and precIn = 0 then
    return '0';
  end if;

  --build it...
  select case when numIn < 0 then '-' end -- put minus
         ||
         case when floor(abs(round(numIn,precIn))) = 0 then '0' end --put zero if eventually we get zero
         ||
         substr(
                trim(to_char(abs(round(numIn,precIn)), nines || '.' || zeros)), -- format it
                1, -- start from the begin
                length(floor(abs(numIn))) --basic length (not include leading zero, dot or minus)
                + case when precIn > 0 AND numIn <> 0 then 1 else 0 end -- for the dot
                + precIn --precision
                )
         into toReturn
  FROM Dual;
return toReturn;
exception
  when others then
    return to_char(round(numIn,precIn));
end;
/

prompt
prompt Creating function TO_CHAR_BY_PREC_NO_TRAIL_ZERO
prompt ===============================================
prompt
create or replace function to_char_by_prec_no_trail_zero(numIn number,precIn number)return varchar is
  toReturn varchar(200);
  nines varchar(200) := '9';
  zeros varchar(200) := '0';
  prec  number := precIn;
begin

  if precIn is null then
    return to_char(numIn);
  end if;

  --init
  if precIn > (length((numIn) - trunc(numIn)) - 1) then
     prec := length((numIn) - trunc(numIn)) - 1;
  end if;

  WHILE length(nines) < length(floor(abs(round(numIn,prec))))
  LOOP
       nines := nines || '9';
  END LOOP;

  WHILE length(zeros) < prec
  LOOP
       zeros := zeros || '0';
  END LOOP;

  --special case
  if  numIn = 0 and prec = 0 then
    return '0';
  end if;

  --build it...
  select case when numIn < 0 then '-' end -- put minus
         ||
         case when floor(abs(round(numIn,prec))) = 0 then '0' end --put zero if eventually we get zero
         ||
         substr(
                trim(to_char(abs(round(numIn,prec)), nines || '.' || zeros)), -- format it
                1, -- start from the begin
                length(floor(abs(numIn))) --basic length (not include leading zero, dot or minus)
                + case when prec > 0 AND numIn <> 0 then 1 else 0 end -- for the dot
                + prec --precInision
                )
         into toReturn
  FROM Dual;
return toReturn;
exception
  when others then
    return to_char(round(numIn,precIn));
end;
/

prompt
prompt Creating function TO_CHAR_EXPONENT_BY_PRECISION
prompt ===============================================
prompt
create or replace function to_char_exponent_by_precision(numIn number,precIn number)return varchar is

   /* EEEE format mask '9.9EEEE' -> Returns a value using in scientific notation.*/

  toReturn varchar(200);
  nines varchar(200) := '9';
  zeros varchar(200) := '0';
  e_index number :=0 ;
begin

  if precIn is null then
    return to_char(numIn);
  end if;

  --init

  WHILE length(nines) < precIn
  LOOP
       nines := nines || '9';
  END LOOP;

  WHILE length(zeros) < precIn
  LOOP
       zeros := zeros || '0';
  END LOOP;

  --special case
  if  numIn = 0 then
    return '0';
  end if;

  --in case after round by precision, value display only zeros -> convert to Exponential
  if numIn < 1 and (numIn * to_number('1'||zeros)) < 1 then
      toReturn := trim(to_char(numIn, '9.'||nines||'EEEE'));

      e_index := instr(toReturn,'E');
      if e_index > 0 then
         -- get rid of zero in return value, for example: '9.333E-01' -> '9.333E-1'
         toReturn := substr(toReturn,1,e_index+1)||to_number(substr(toReturn,e_index+2));
      end if;
  else
      toReturn := to_char_by_precision_rounded(numIn,precIn);
  end if;

return toReturn;
exception
  when others then
    return to_char(numIn);
end;
/

prompt
prompt Creating procedure FG_GET_FILE
prompt ==============================
prompt
create or replace procedure fg_GET_FILE(file_id_in number, file_name_out OUT varchar2, stream_out OUT blob) is
begin
  select t.file_name, t.file_content into file_name_out, stream_out
  from fg_files t
  where t.file_id = file_id_in;
end;
/

prompt
prompt Creating procedure FG_INSERT_CLOB_FILE
prompt ======================================
prompt
create or replace procedure FG_INSERT_CLOB_FILE(file_id_in varchar2, file_name_in varchar2,  CONTENT_TYPE_in varchar2,
                                                empty_clob_out OUT clob) is
is_exist number;
begin
  
  select count(*)
  into is_exist
  from  FG_CLOB_FILES t
  where t.file_id = file_id_in; 

  if is_exist = 0 then  
      insert into FG_CLOB_FILES(FILE_ID, FILE_NAME, CONTENT_TYPE, FILE_CONTENT)
      values(file_id_in, file_name_in, CONTENT_TYPE_in, empty_clob())
      return file_content into empty_clob_out;
  else
     update FG_CLOB_FILES t
     set t.file_content = empty_clob()
     where t.file_id = file_id_in
     return file_content into empty_clob_out;
  end if;

end;
/

prompt
prompt Creating procedure FG_INSERT_FILE
prompt =================================
prompt
create or replace procedure FG_INSERT_FILE(file_id_in varchar2, file_name_in varchar2,  CONTENT_TYPE_in varchar2, is_temp_in number default 0, empty_blob_out OUT blob) is
begin
  insert into FG_FILES(FILE_ID,FILE_NAME,CONTENT_TYPE,TMP_FILE,FILE_CONTENT)
  values(file_id_in,file_name_in,CONTENT_TYPE_in,is_temp_in, empty_blob())
  return file_content into empty_blob_out;

  -- fetch new attachment id
 --select FG_FILES.currval into attach_id_out from dual;
--  commit;
end;
/

prompt
prompt Creating procedure FG_INSERT_RICHTEXT
prompt =====================================
prompt
create or replace procedure FG_INSERT_RICHTEXT(file_id_in varchar2, file_name_in varchar2, CONTENT_TYPE_in varchar2, empty_clob_out OUT clob,
                                               empty_clob_plain_tex_out OUT clob) is
begin

  insert into fg_richtext(FILE_ID, FILE_NAME, CONTENT_TYPE, FILE_CONTENT, FILE_CONTENT_TEXT)
  values(file_id_in, file_name_in, CONTENT_TYPE_in, empty_clob(), empty_clob());
    --  return file_content into empty_clob_out;

  select FILE_CONTENT into empty_clob_out from fg_richtext t where t.file_id = file_id_in;
  select FILE_CONTENT_TEXT into empty_clob_plain_tex_out from fg_richtext t where t.file_id = file_id_in;
end;
/

prompt
prompt Creating procedure FG_SET_COL_SIZE
prompt ==================================
prompt
create or replace procedure fg_set_col_size(formcode_entity_in varchar2, table_in varchar2) as
   is_constraint_exist integer;
   uniqe_columns varchar2(1000);
begin

   --finding out if the constraint is already defined
   for r in (SELECT t.COLUMN_NAME
      INTO is_constraint_exist
   FROM user_tab_columns t
   WHERE t.TABLE_NAME = upper(table_in)
   and   upper(t.COLUMN_NAME) in (select upper(v.entityimpcode) from fg_formentity_col_len_v v where upper(v.Formcode_Entity) = upper(formcode_entity_in) )
   )
   
   loop 
     EXECUTE IMMEDIATE  ' alter table ' || table_in || ' modify ' || r.column_name  || ' VARCHAR2(' || fg_get_col_size(formcode_entity_in, r.column_name) || ') ';
   end loop;

end;
/

prompt
prompt Creating procedure SET_SINGLE_CONSTRAINTS_INFO
prompt ==============================================
prompt
create or replace procedure SET_SINGLE_CONSTRAINTS_INFO(constraint_name_in varchar2, table_in varchar2,  column_in varchar2, uniqe_columns_in varchar2, drop_always_in number default 0, is_check_constraint_in number default 0) as
   is_constraint_exist integer;
   uniqe_columns varchar2(1000);
   command varchar2(32767);
begin

   --finding out if the constraint is already defined
   SELECT count(*)
      INTO is_constraint_exist
   FROM user_constraints
   WHERE constraint_name = constraint_name_in;

   if is_constraint_exist = 1 and drop_always_in = 1 THEN
      EXECUTE IMMEDIATE ' alter table ' || table_in || ' drop CONSTRAINT ' || UPPER(constraint_name_in);
   end if;

   if is_check_constraint_in = 0 then
     --finding out if the indx is already defined
     SELECT count(*)
        INTO is_constraint_exist
     FROM USER_indexes
     WHERE UPPER(USER_indexes.index_name) = UPPER(constraint_name_in);

     if is_constraint_exist = 1 and drop_always_in = 1 THEN
        EXECUTE IMMEDIATE ' drop index ' || UPPER(constraint_name_in);
     end if;
    end if;

   --alter table
   /*for i in (
     SELECT  regexp_substr(uniqe_columns_in, '[^,]+', 1,LEVEL ) str
     FROM  dual
       CONNECT BY regexp_substr(uniqe_columns_in , '[^,]+', 1,LEVEL) IS NOT NULL
     )
     loop
       EXECUTE IMMEDIATE (' alter table ' || table_in || ' modify ' || i.str || ' VARCHAR2(500) ');
     end loop;
  \* if instr(uniqe_columns_in,',') > 0 then
     EXECUTE IMMEDIATE  ' alter table ' || table_in || ' modify ' || substr(uniqe_columns_in,1,instr(uniqe_columns_in,',') - 1)  || ' VARCHAR2(1000) ';
     EXECUTE IMMEDIATE  ' alter table ' || table_in || ' modify ' || substr(uniqe_columns_in, instr(uniqe_columns_in,',') + 1) || ' VARCHAR2(1000) ';
   else
     EXECUTE IMMEDIATE  ' alter table ' || table_in || ' modify ' || uniqe_columns_in || ' VARCHAR2(1000) ';
   end if;*\*/

   --create only indx that allow case insensitive
   if is_constraint_exist = 0 or drop_always_in = 1 THEN
      BEGIN
         FOR i IN
         (SELECT  regexp_substr(uniqe_columns_in, '[^,]+', 1,LEVEL ) str
          FROM  dual
          CONNECT BY regexp_substr(uniqe_columns_in , '[^,]+', 1,LEVEL) IS NOT NULL
         )
          LOOP
             uniqe_columns:= uniqe_columns || 'TRIM(UPPER('||(i.str) || ')),' ;
          END LOOP;

          uniqe_columns:= SUBSTR(uniqe_columns,1,LENGTH(uniqe_columns)-1);

          if is_check_constraint_in = 0 then
            command := 'CREATE UNIQUE INDEX ' || constraint_name_in ||
                           ' ON ' || table_in || '('||uniqe_columns||')';
            EXECUTE IMMEDIATE (command);
          else
            command := 'ALTER TABLE ' || table_in ||
                           ' ADD CONSTRAINT ' || constraint_name_in || ' CHECK ' || '('||column_in||')';
            EXECUTE IMMEDIATE (command);
          end if;
          COMMIT;

       EXCEPTION
          when others then
          DBMS_OUTPUT.put_line('--FAILURE IN SETTING COMMAND:');
          DBMS_OUTPUT.put_line(command);
       END;
   END IF;

end;
/

prompt
prompt Creating procedure SET_CONSTRAINTS_INDX
prompt =======================================
prompt
create or replace procedure SET_CONSTRAINTS_INDX as

begin
  -- example SET_SINGLE_CONSTRAINTS_INFO('PROJECT_NAME_UNIQUE', 'FG_S_PROJECT_PIVOT', 'PROJECTNAME', 'PROJECTNAME', 1);
   

   for maint in (
     select upper(formcode_entity) as formcode_entity --'SET_SINGLE_CONSTRAINTS_INDX(''' || upper(formcode_entity) || '_UNIQUE'',''FG_S_' || upper(formcode )||'_PIVOT'','''|| upper(formcode_entity) || 'NAME'','''|| upper(formcode_entity) || 'NAME,ACTIVE'',1);' as CONSTRAINT_MAINT
     from FG_FORM t
     where t.form_type = 'MAINTENANCE'
     and t.group_name = 'General' and 12 in ( select count(*) from user_tab_columns where table_name= 'FG_S_' || upper(formcode )||'_PIVOT')
     )
     loop
       SET_SINGLE_CONSTRAINTS_INFO( maint.formcode_entity || '_UNIQUE','FG_S_' || maint.formcode_entity||'_PIVOT', maint.formcode_entity || 'NAME',  maint.formcode_entity || 'NAME,ACTIVE',1);
      -- EXECUTE IMMEDIATE  (maint.constraint_maint);
      -- dbms_output.put_line( 'SET_SINGLE_CONSTRAINTS_INDX('''|| maint.formcode_entity || '_UNIQUE'',''FG_S_' || maint.formcode_entity||'_PIVOT'','''||  maint.formcode_entity || 'NAME'','''||  maint.formcode_entity || 'NAME,ACTIVE'',1);'
     end loop;

      for r in (
           select distinct TABLE_NAME,
                   UPPER(REPLACE(replace(TABLE_NAME,'FG_S_',''),'_PIVOT','')) AS TABLE_FORM_CODE,
                   DECODE(F.FORM_TYPE,'MAINTENANCE','FORMID,SESSIONID,ACTIVE,CLONEID,TIMESTAMP','FORMID,SESSIONID,ACTIVE,CLONEID') AS COL_LIST
            from user_tables t,
                 FG_FORM F
            where 1=1
            AND   UPPER(F.FORMCODE_ENTITY) = UPPER(REPLACE(REPLACE(T.TABLE_NAME,'FG_S_'),'_PIVOT'))
            AND   'DUP_' || UPPER(REPLACE(replace(TABLE_NAME,'FG_S_',''),'_PIVOT','')) NOT IN (SELECT USER_CONSTRAINTS.constraint_name FROM USER_CONSTRAINTS)

      )
      loop
          /*EXECUTE  IMMEDIATE ('ALTER TABLE ' || r.TABLE_NAME ||
                         ' ADD CONSTRAINT ' || constraint_name_in || ' UNIQUE (FORMID,SESSIONID,ACTIVE,CLONEID)');*/
        --SET_DUP_CONSTRAINTS('DUP_' || R.TABLE_FORM_CODE, r.TABLE_NAME, R.COL_LIST);
       dbms_output.put_line( 'DUP_' || R.TABLE_FORM_CODE);
       END LOOP;

      -- Formnumberid Constraints
      -- example SET_SINGLE_CONSTRAINTS_INFO('FORMNUMBERID_FORMAT_BATCH','FG_S_INVITEMBATCH_PIVOT','REGEXP_LIKE(formnumberid,''^....-..-...-....-B[1-9][0-9]*'')','',1,1);
       
end/* SET_CONSTRAINTS */;
/

prompt
prompt Creating procedure SET_DUP_CONSTRAINTS
prompt ======================================
prompt
create or replace procedure SET_DUP_CONSTRAINTS(constraint_name_in varchar2, table_in varchar2,  column_LIST_IN varchar2) as
     counter_ number;
begin

       /*EXECUTE IMMEDIATE ('ALTER TABLE ' || table_in ||
                         ' ADD CONSTRAINT ' || constraint_name_in || ' UNIQUE ('||column_LIST_IN||')');*/
                         
       execute immediate '   select distinct max(count(*)) over () from (select t.*, count(rowid) over (partition by ' || column_LIST_IN || ') as cunt_ from ' || table_in || ' t) where cunt_ > 1' into counter_;
       if counter_ > 0 then
          DBMS_OUTPUT.put_line('------ delete from ' || table_in || ' ---------------');
          DBMS_OUTPUT.put_line(' delete from ' || table_in || ' where rowid in (');
          DBMS_OUTPUT.put_line('select minrowid_ from (select t.*, min(rowid) over (partition by  ' || column_LIST_IN || ') as minrowid_, count(rowid) over (partition by  ' || column_LIST_IN || ') as cunt_ from ' || table_in || ' t) where cunt_ > 1');
          DBMS_OUTPUT.put_line(');');
       end if;

/* EXCEPTION
         when others then
         --DBMS_OUTPUT.put_line('--FAILURE IN SETTING ' || constraint_name_in || ' TO TABLE: ' ||  table_in);
         --DBMS_OUTPUT.put_line('   select * from (select t.*, count(rowid) over (partition by ' || column_LIST_IN || ') as cunt_ from ' || table_in || ' t) where cunt_ > 1;');
          DBMS_OUTPUT.put_line('------ delete from ' || table_in || ' ---------------');
          DBMS_OUTPUT.put_line(' delete from ' || table_in || ' where rowid in (');
          DBMS_OUTPUT.put_line('select minrowid_ from (select t.*, min(rowid) over (partition by  ' || column_LIST_IN || ') as minrowid_, count(rowid) over (partition by  ' || column_LIST_IN || ') as cunt_ from ' || table_in || ' t) where cunt_ > 1');
          DBMS_OUTPUT.put_line(');');*/
end;
/

prompt
prompt Creating procedure SET_SINGLE_CONSTRAINTS_INDX
prompt ==============================================
prompt
create or replace procedure SET_SINGLE_CONSTRAINTS_INDX(constraint_name_in varchar2, table_in varchar2,  column_in varchar2, uniqe_columns_in varchar2, drop_always_in number default 0) as
   is_constraint_exist integer;
   uniqe_columns varchar2(1000);
begin

   --finding out if the constraint is already defined
   SELECT count(*)
      INTO is_constraint_exist
   FROM all_constraints
   WHERE constraint_name = constraint_name_in;

   if is_constraint_exist = 1 and drop_always_in = 1 THEN
      EXECUTE IMMEDIATE ' alter table ' || table_in || ' drop CONSTRAINT ' || UPPER(constraint_name_in);
   end if;

   --finding out if the indx is already defined
   SELECT count(*)
      INTO is_constraint_exist
   FROM USER_indexes
   WHERE UPPER(USER_indexes.index_name) = UPPER(constraint_name_in);

   if is_constraint_exist = 1 and drop_always_in = 1 THEN
      EXECUTE IMMEDIATE ' drop index ' || UPPER(constraint_name_in);
   end if;

   --alter table
   /*for i in (
     SELECT  regexp_substr(uniqe_columns_in, '[^,]+', 1,LEVEL ) str
     FROM  dual
       CONNECT BY regexp_substr(uniqe_columns_in , '[^,]+', 1,LEVEL) IS NOT NULL
     )
     loop
       EXECUTE IMMEDIATE (' alter table ' || table_in || ' modify ' || i.str || ' VARCHAR2(500) ');
     end loop;
  \* if instr(uniqe_columns_in,',') > 0 then
     EXECUTE IMMEDIATE  ' alter table ' || table_in || ' modify ' || substr(uniqe_columns_in,1,instr(uniqe_columns_in,',') - 1)  || ' VARCHAR2(1000) ';
     EXECUTE IMMEDIATE  ' alter table ' || table_in || ' modify ' || substr(uniqe_columns_in, instr(uniqe_columns_in,',') + 1) || ' VARCHAR2(1000) ';
   else
     EXECUTE IMMEDIATE  ' alter table ' || table_in || ' modify ' || uniqe_columns_in || ' VARCHAR2(1000) ';
   end if;*\*/
  
   --create only indx that allow case insensitive
   if is_constraint_exist = 0 or drop_always_in = 1 THEN
      BEGIN
         FOR i IN
         (SELECT  regexp_substr(uniqe_columns_in, '[^,]+', 1,LEVEL ) str
          FROM  dual
          CONNECT BY regexp_substr(uniqe_columns_in , '[^,]+', 1,LEVEL) IS NOT NULL
         )
        LOOP
           uniqe_columns:= uniqe_columns || 'TRIM(UPPER('||(i.str) || ')),' ;
           END LOOP;
      
      uniqe_columns:= SUBSTR(uniqe_columns,1,LENGTH(uniqe_columns)-1);
                      
            EXECUTE IMMEDIATE ('CREATE UNIQUE INDEX ' || constraint_name_in ||
                         ' ON ' || table_in || '('||uniqe_columns||')');
           EXCEPTION
              when others then 
                EXECUTE IMMEDIATE 'UPDATE ' ||table_in||' SET '||column_in || ' = '||column_in||'||FORMID||ROWNUM
                                where upper('||column_in||') in
                                  (select TRIM(upper('||column_in||'))
                                  from   
                                    (select '||column_in||',
                                    count(*) over (partition by TRIM(upper('||column_in||'))) ct
                                    from '||table_in||'
                                  )
                                  where ct > 1)';  
                   
              EXECUTE IMMEDIATE ('CREATE UNIQUE INDEX ' || constraint_name_in ||
                           ' ON ' || table_in || '('||uniqe_columns||')');
            COMMIT;          
        END;
   END IF; 
     
end;
/

prompt
prompt Creating procedure SHIFTELEMENTSBOOKMARKUP
prompt ==========================================
prompt
create or replace procedure shiftElementsBookmarkUp is
begin
  --EXECUTE IMMEDIATE ' CREATE table FG_FORMENTITY_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from FG_FORMENTITY ';

  update FG_FORMENTITY t
          set t.ENTITYIMPINIT = case
                               when REGEXP_INSTR(t.entityimpinit,'General' || '[0-9][0-9][0-9][0-9]') > 0 then --4 DIGIT
                                 ''
                               when REGEXP_INSTR(t.entityimpinit,'General' || '[0-9][0-9][0-9]') > 0 then -- 3 DIGIT
                                 ''
                               when REGEXP_INSTR(t.entityimpinit,'General' || '[1-8][0-9]') > 0 then --2 DIGIT
                                 REGEXP_REPLACE(t.entityimpinit,'General' || '[0-9][0-9]','General' || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,'General') + length('General'),2)) - 1))
                               end
          where 1=1--t.formcode = 'Experiment'
          and   t.entitytype = 'Element'
          and   t.entityimpinit like '%' || 'General' || '_%';
end shiftElementsBookmarkUp;
/

prompt
prompt Creating procedure SP_GET_PIVOT_V2
prompt ==================================
prompt
create or replace procedure sp_get_pivot_v2 (user_id_in number ,session_id_in varchar, result_out out sys_refcursor, col_map_out out sys_refcursor) as
  -- the procedure return basic pivot table and columne map for the data block in d_pivot_tmp that has user_id and session_id_in as in the procedure parameters
  -- the order of the the columne is by the col_id (make sure the length of col_id is under the oracle columne limitation)
  -- the col_map_out mapping the col_id to the real columne name
  cursor c is
  select distinct t.col_id,t.col_name  from d_pivot_tmp t
  where t.user_id = user_id_in
  and   t.session_id = session_id_in
  order by t.col_id;

  newline varchar(10) :=  CHR(13) || CHR(10);

  sql_result varchar(32767) := 'SELECT d_pivot_tmp.row_id, d_pivot_tmp.row_name' || newline;
  sql_map varchar(32767) := '';

  dataFlag number := 0;

begin

  for r in c
  loop
    sql_result := sql_result || ',MAX (DECODE (d_pivot_tmp.col_id, ''' || r.col_id || ''', d_pivot_tmp.result, NULL)) ' || r.col_id || newline;
    sql_map := sql_map || 'select ''' || r.col_id || ''' key_field ,''' || r.col_name || ''' as value_field from dual union' || newline;
    dataFlag := 1;
  end loop;

  if dataFlag = 1 then
    sql_result := sql_result ||' FROM d_pivot_tmp WHERE user_id = ''' || user_id_in || ''' and session_id = ''' ||  session_id_in || '''  GROUP BY d_pivot_tmp.row_id, d_pivot_tmp.row_id_order, d_pivot_tmp.row_name order by d_pivot_tmp.row_id_order ';
    sql_map := substr(sql_map, 1, (length(sql_map) - length('union' || newline)));

--    insert into D_DEBUG (id,TIME_STAMP,USER_ID,Session_Id,SQL_CODE,COMMENTS) values (null,sysdate,user_id_in,session_id_in,sql_result,'main_test_sql_pivot');
--    commit;

--    insert into D_DEBUG (id,TIME_STAMP,USER_ID,Session_Id,SQL_CODE,COMMENTS) values (null,sysdate,user_id_in,session_id_in,sql_map,'map_col_sql_pivot');
--    commit;

    open result_out for sql_result;
    open col_map_out for sql_map;
  else
    open result_out for select 1 from dual;
    open col_map_out for select 1 from dual;
  end if;

end;
/

prompt
prompt Creating procedure TRUNCATE_IT
prompt ==============================
prompt
create or replace procedure TRUNCATE_IT(in_table varchar2) is
  v_sql   varchar2(200);
begin


  v_sql := 'truncate table ' || in_table;

  execute immediate v_sql;

end TRUNCATE_IT;
/

prompt
prompt Creating package body FORM_TOOL_COPY_ADAMA_DATA
prompt ===============================================
prompt
create or replace package body form_tool_COPY_ADAMA_DATA is
procedure updateVerionData as
       check_admin_ number;
       check_system_ number;
       check_sysparam_ number;
       adminFormId varchar2(100);
       p_db_name varchar2(100);
begin 

SELECT sys_context('USERENV','SESSION_USER') into p_db_name FROM dual;

--***** users
--admin usert
select count(*) into check_admin_ from fg_s_user_pivot t where lower(t.USERNAME) = 'admin';

if check_admin_ > 0 then
  select max(t.FORMID) into adminFormId from fg_s_user_pivot t where lower(t.USERNAME) = 'admin';
else
  --insert with password comply2005
  insert into FG_S_USER_PIVOT (formid, timestamp, change_by, sessionid, active, formcode, username, position, firstname, lastname, changepassword, deleted, locked, customer_id, userrole_id, userldap, passworddate, chgpassworddate, laboratory_id, unit_id, password, lastretry, retrycount, site_id, email, lastpassworddate, formcode_entity, teamleader_id, groupscrew, cloneid, permissiontable, sensitivitylevel_id, templateflag, created_by, creation_date)
  values (FG_GET_STRUCT_FORM_ID('User'), sysdate, null, null, 1, 'User', 'admin', null, 'admin', '1', null, '0', '0',null, null, null, '26/01/2017', '26/01/2017', null, null, 'e51e64148c4104bf2fb9f7e7b07837d5', '15-FEB-18', '1', null, 'skyline@comply.co.il', '26/01/2017', 'User', null, null, null, 'NA,ALL,fg_r_permissionUser_v,1,User,0,10,,', null, null, null, null);
  select max(t.FORMID) into adminFormId from fg_s_user_pivot t where lower(t.USERNAME) = 'admin';
end if;

update fg_s_user_pivot t set t.password = 'e51e64148c4104bf2fb9f7e7b07837d5' where t.username = 'admin'; --set to comply2005

--system user
select count(*) into check_system_ from fg_s_user_pivot t where lower(t.USERNAME) = 'system';

if check_system_ = 0 then
  --insert with password sys_comply2005
  insert into FG_S_USER_PIVOT (formid, timestamp, change_by, sessionid, active, formcode, username, position, firstname, lastname, changepassword, deleted, locked, customer_id, userrole_id, userldap, passworddate, chgpassworddate, laboratory_id, unit_id, password, lastretry, retrycount, site_id, email, lastpassworddate, formcode_entity, teamleader_id, groupscrew, cloneid, permissiontable, sensitivitylevel_id, templateflag, created_by, creation_date)
  values (FG_GET_STRUCT_FORM_ID('User'), sysdate, null, null, 1, 'User', 'system', null, 'system', '1', null, '0', '0',null, null, null, '26/01/2017', '26/01/2017', null, null, 'ab5d6cb5ad2b840e5a7e7d02afc60ff6', '15-FEB-18', '1', null, 'skyline@comply.co.il', '26/01/2017', 'User', null, null, null, 'NA,ALL,fg_r_permissionUser_v,1,User,0,10,,', null, null, null, null);
end if;

update fg_s_user_pivot t set t.password = 'ab5d6cb5ad2b840e5a7e7d02afc60ff6' where t.username = 'system'; --set to sys_comply2005


--***** FG_SYS_PARAM
select count(*) into check_sysparam_ from FG_SYS_PARAM t;
if check_system_ = 0 then
  insert into FG_SYS_PARAM (retry_count, login_timeout, password_aging, grace_period, updated_by, updated_on, comments, is_develop, redirect_notification_email, last_build)
  values (3, 10, 200, 10, (select t.formid from fg_s_user_pivot t where lower(t.USERNAME) = 'system'), sysdate, null, 0, null, sysdate);
end if;

--***** JCHEM
--JCHEMPROPERTIES_CR
delete from JCHEMPROPERTIES_CR;
insert into JCHEMPROPERTIES_CR (cache_id, registration_time, is_protected)
values ('3f308122af3648f4a7d00178ec7585e2', '2019-06-10 11:34:26.181', 0);

--JCHEMPROPERTIES
delete from JCHEMPROPERTIES;
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('option.structureCompressionDisabled', 'true');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('option.commitInterval', '50');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('propertytable.identifier', 'PT_ID_6849fa7b7ac14a28a36e966c6de9d092');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('cache.registration_table', '' || p_db_name || '.JCHEMPROPERTIES_CR');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.creationTime', '2018-09-05 15:33:55.698');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.validityTimestamp', '2018-09-05 15:33:55.729');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.JChemVersion', '18.18.0');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.tableType', '0');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.absoluteStereo', 'true');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.tautomerDuplicateFiltering', 'false');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.duplicateFiltering', 'true');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.cacheUpdaterType', 'UPDATE_LOG_TABLE');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.tautomerEqualityMode', 'g');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.version', '18.18.0.0');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.ctVersion', '18.18.0.0');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.mdVersion', '18.18.0.0');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.fingerprint.numberOfBits', '512');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.fingerprint.numberOfOnes', '2');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.fingerprint.numberOfEdges', '6');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_SEARCH.fingerprint.numberOfStrucFPCols', '0');
insert into JCHEMPROPERTIES (prop_name, prop_value)
values ('cache.validity_timestamp', '2019-06-10 11:34:26.176');

--JCHEMPROPERTIES_DOC_CR
-- NOT NEEDED?

--JCHEMPROPERTIES_DOC
delete from JCHEMPROPERTIES_DOC;
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('option.structureCompressionDisabled', 'true');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('option.commitInterval', '50');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('propertytable.identifier', 'PT_ID_f849cc68001d47f7830fb1afcb3dfb62');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('cache.registration_table', p_db_name || '.JCHEMPROPERTIES_DOC_CR');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.creationTime', '2019-07-09 09:32:11.801');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.validityTimestamp', '2019-07-09 09:32:12.116');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.JChemVersion', '18.18.0');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.tableType', '2');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.absoluteStereo', 'true');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.tautomerDuplicateFiltering', 'false');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.duplicateFiltering', 'false');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.cacheUpdaterType', 'UPDATE_LOG_TABLE');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.tautomerEqualityMode', 'g');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.version', '18.18.0.0');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.ctVersion', '18.18.0.0');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.mdVersion', '18.18.0.0');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.fingerprint.numberOfBits', '512');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.fingerprint.numberOfOnes', '2');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.fingerprint.numberOfEdges', '6');
insert into JCHEMPROPERTIES_DOC (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DOC_SEARCH.fingerprint.numberOfStrucFPCols', '0');

--JCHEMPROPERTIES_DELETED
delete from JCHEMPROPERTIES_DELETED;
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('option.structureCompressionDisabled', 'true');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('option.commitInterval', '50');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('propertytable.identifier', 'PT_ID_f849cc68001d47f7830fb1afcb3dfb62');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('cache.registration_table', p_db_name || '.JCHEMPROPERTIES_DELETED_CR');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.creationTime', '2019-07-09 09:32:11.801');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.validityTimestamp', '2019-07-09 09:32:12.116');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.JChemVersion', '18.18.0');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.tableType', '2');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.absoluteStereo', 'true');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.tautomerDuplicateFiltering', 'false');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.duplicateFiltering', 'false');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.cacheUpdaterType', 'UPDATE_LOG_TABLE');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.tautomerEqualityMode', 'g');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.version', '18.18.0.0');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.ctVersion', '18.18.0.0');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.mdVersion', '18.18.0.0');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.fingerprint.numberOfBits', '512');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.fingerprint.numberOfOnes', '2');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.fingerprint.numberOfEdges', '6');
insert into JCHEMPROPERTIES_DELETED (prop_name, prop_value)
values ('table.' || p_db_name || '.FG_CHEM_DELETED_SEARCH.fingerprint.numberOfStrucFPCols', '0');

end;

end;
/

prompt
prompt Creating trigger FG_CHEM_DELETED_SEARCH_TR
prompt ==========================================
prompt
CREATE OR REPLACE TRIGGER fg_chem_deleted_search_TR
   BEFORE INSERT OR UPDATE ON fg_chem_deleted_search
   FOR EACH ROW
   
DECLARE
       ICOUNTER fg_chem_deleted_search.cd_id%TYPE;
       ICURRENT fg_chem_deleted_search.cd_id%TYPE;
       CANNOT_CHANGE_VALUE EXCEPTION;
   BEGIN
       IF INSERTING THEN
           IF :NEW.cd_id IS NULL THEN
               SELECT fg_chem_deleted_search_SQ.NEXTVAL INTO ICOUNTER FROM DUAL;
               :NEW.cd_id := ICOUNTER;
           ELSE
               BEGIN
                   SELECT fg_chem_deleted_search_SQ.CURRVAL INTO ICURRENT FROM DUAL;
               EXCEPTION
                   WHEN OTHERS THEN      /* ON FIRST RUN NO CURRVAL AVAILABLE */
                       SELECT fg_chem_deleted_search_SQ.NEXTVAL INTO ICURRENT FROM DUAL;
               END;
               IF :NEW.cd_id > ICURRENT THEN
                   LOOP
                       SELECT fg_chem_deleted_search_SQ.NEXTVAL INTO ICOUNTER FROM DUAL;
                       EXIT WHEN :NEW.cd_id <= ICOUNTER;
                   END LOOP;
               END IF;
           END IF;
       END IF;
       IF UPDATING THEN
           IF NOT (:NEW.cd_id = :OLD.cd_id) THEN
               RAISE CANNOT_CHANGE_VALUE;
           END IF;
       END IF;
   EXCEPTION
       WHEN CANNOT_CHANGE_VALUE THEN
           RAISE_APPLICATION_ERROR(-20000, 'CANNOT CHANGE fg_chem_deleted_search.cd_id VALUE');
   END;
/

prompt
prompt Creating trigger FG_CHEM_DOC_SEARCH_TR
prompt ======================================
prompt
CREATE OR REPLACE TRIGGER FG_CHEM_DOC_SEARCH_TR
   BEFORE INSERT OR UPDATE ON FG_CHEM_DOC_SEARCH
   FOR EACH ROW
   
DECLARE
       ICOUNTER FG_CHEM_DOC_SEARCH.cd_id%TYPE;
       ICURRENT FG_CHEM_DOC_SEARCH.cd_id%TYPE;
       CANNOT_CHANGE_VALUE EXCEPTION;
   BEGIN
       IF INSERTING THEN
           IF :NEW.cd_id IS NULL THEN
               SELECT FG_CHEM_DOC_SEARCH_SQ.NEXTVAL INTO ICOUNTER FROM DUAL;
               :NEW.cd_id := ICOUNTER;
           ELSE
               BEGIN
                   SELECT FG_CHEM_DOC_SEARCH_SQ.CURRVAL INTO ICURRENT FROM DUAL;
               EXCEPTION
                   WHEN OTHERS THEN      /* ON FIRST RUN NO CURRVAL AVAILABLE */
                       SELECT FG_CHEM_DOC_SEARCH_SQ.NEXTVAL INTO ICURRENT FROM DUAL;
               END;
               IF :NEW.cd_id > ICURRENT THEN
                   LOOP
                       SELECT FG_CHEM_DOC_SEARCH_SQ.NEXTVAL INTO ICOUNTER FROM DUAL;
                       EXIT WHEN :NEW.cd_id <= ICOUNTER;
                   END LOOP;
               END IF;
           END IF;
       END IF;
       IF UPDATING THEN
           IF NOT (:NEW.cd_id = :OLD.cd_id) THEN
               RAISE CANNOT_CHANGE_VALUE;
           END IF;
       END IF;
   EXCEPTION
       WHEN CANNOT_CHANGE_VALUE THEN
           RAISE_APPLICATION_ERROR(-20000, 'CANNOT CHANGE FG_CHEM_DOC_SEARCH.cd_id VALUE');
   END;
/

prompt
prompt Creating trigger FG_CHEM_SEARCH_TR
prompt ==================================
prompt
CREATE OR REPLACE TRIGGER FG_CHEM_SEARCH_TR
   BEFORE INSERT OR UPDATE ON FG_CHEM_SEARCH
   FOR EACH ROW
   
DECLARE
       ICOUNTER FG_CHEM_SEARCH.cd_id%TYPE;
       ICURRENT FG_CHEM_SEARCH.cd_id%TYPE;
       CANNOT_CHANGE_VALUE EXCEPTION;
   BEGIN
       IF INSERTING THEN
           IF :NEW.cd_id IS NULL THEN
               SELECT FG_CHEM_SEARCH_SQ.NEXTVAL INTO ICOUNTER FROM DUAL;
               :NEW.cd_id := ICOUNTER;
           ELSE
               BEGIN
                   SELECT FG_CHEM_SEARCH_SQ.CURRVAL INTO ICURRENT FROM DUAL;
               EXCEPTION
                   WHEN OTHERS THEN      /* ON FIRST RUN NO CURRVAL AVAILABLE */
                       SELECT FG_CHEM_SEARCH_SQ.NEXTVAL INTO ICURRENT FROM DUAL;
               END;
               IF :NEW.cd_id > ICURRENT THEN
                   LOOP
                       SELECT FG_CHEM_SEARCH_SQ.NEXTVAL INTO ICOUNTER FROM DUAL;
                       EXIT WHEN :NEW.cd_id <= ICOUNTER;
                   END LOOP;
               END IF;
           END IF;
       END IF;
       IF UPDATING THEN
           IF NOT (:NEW.cd_id = :OLD.cd_id) THEN
               RAISE CANNOT_CHANGE_VALUE;
           END IF;
       END IF;
   EXCEPTION
       WHEN CANNOT_CHANGE_VALUE THEN
           RAISE_APPLICATION_ERROR(-20000, 'CANNOT CHANGE FG_CHEM_SEARCH.cd_id VALUE');
   END;
/

prompt
prompt Creating trigger FG_DYNAMICPARAMS_INSERT_TRIG
prompt =============================================
prompt
create or replace trigger FG_DYNAMICPARAMS_INSERT_TRIG
  before insert on FG_DYNAMICPARAMS
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_DYNAMICPARAMS_SEQ.nextval
   into v_new_val from dual;
   :new.ID := v_new_val ;

end FG_FORM_INSERT_TRIG;
/

prompt
prompt Creating trigger FG_FORMADDITIONALDATA_INS_TRIG
prompt ===============================================
prompt
create or replace trigger FG_FORMADDITIONALDATA_INS_TRIG
  before insert on FG_FORMADDITIONALDATA
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_FORMADDITIONALDATA_SEQ.nextval
   into v_new_val from dual;
   :new.ID := v_new_val ;
    
insert into FG_FORMADDITIONALDATA_HST
 (
    ID,
    PARENTID,
    ENTITYIMPCODE,
    VALUE,
    CONFIG_ID,
    FORMCODE,
    INFO,
    change_date
 )
 values
 (
    :NEW.ID,
    :NEW.PARENTID,
    :NEW.ENTITYIMPCODE,
    :NEW.VALUE,
    :NEW.CONFIG_ID,
    :NEW.FORMCODE,
    :NEW.INFO,
    sysdate 
);
 

end;
/

prompt
prompt Creating trigger FG_FORMENTITY_INSERT_TRIG
prompt ==========================================
prompt
create or replace trigger FG_FORMENTITY_INSERT_TRIG
  before insert on FG_FORMENTITY
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_FORMENTITY_SEQ.nextval
   into v_new_val from dual;
   :new.ID := v_new_val ;
   
 insert into FG_FORMENTITY_HST
 (
    ID, 
    formcode, 
    numberoforder, 
    entitytype, 
    entityimpcode, 
    entityimpclass, 
    entityimpinit, 
    comments, 
    fs, 
    fs_gap, 
    change_date,
    CHANGE_TYPE
 )
 values
 (
    :new.ID, 
    :NEW.formcode, 
    :NEW.numberoforder, 
    :NEW.entitytype, 
    :NEW.entityimpcode, 
    :NEW.entityimpclass, 
    :NEW.entityimpinit, 
    :NEW.comments, 
    :NEW.fs, 
    :NEW.fs_gap, 
    :NEW.change_date,
    'I'
 );

end FG_FORMENTITY_INSERT_TRIG;
/

prompt
prompt Creating trigger FG_FORMENTITY_UPDATE_TRIG
prompt ==========================================
prompt
create or replace trigger FG_FORMENTITY_UPDATE_TRIG
before update on FG_FORMENTITY
referencing new as new old as old
for each row
DECLARE
  dbname_ varchar2(100);
begin

  /*if
    :old.ENTITYIMPVALUE <> :new.ENTITYIMPVALUE or (:old.ENTITYIMPVALUE is null and :new.ENTITYIMPVALUE is not null) or (:old.ENTITYIMPVALUE is not null and :new.ENTITYIMPVALUE is null)
  then*/
  :NEW.Change_Date := sysdate;
insert into FG_FORMENTITY_HST
 (
    id,
    formcode,
    numberoforder,
    entitytype,
    entityimpcode,
    entityimpclass,
    entityimpinit,
    comments,
    fs,
    fs_gap,
    change_date,
    CHANGE_TYPE
 )
 values
 (
    :NEW.id,
    :NEW.formcode,
    :NEW.numberoforder,
    :NEW.entitytype,
    :NEW.entityimpcode,
    :NEW.entityimpclass,
    :NEW.entityimpinit,
    :NEW.comments,
    :NEW.fs,
    :NEW.fs_gap,
    SYSDATE,
    'U'
 );
  /*end if;*/

  --select t.USERNAME into dbname_ from sys.USER_USERS t;

  -- FORM SERVER TO DEVELOP
  /*if dbname_ = 'SKYLINE_FORM_SERVER' then

      update skyline_form.FG_FORMENTITY t set t.entityimpinit = :NEW.entityimpinit, t.change_date = sysdate, t.comments = 'UPDATE ON SERVER CHANGE FROM: ' || t.entityimpinit
      where t.formcode = :NEW.formcode
      and t.entityimpcode = :NEW.entityimpcode
      and t.entityimpinit <> :NEW.entityimpinit;
      --and t.change_date <= :OLD.CHANGE_DATE;

      --dbms_output.put_line('dummy');

  end if;*/

  -- FORM DEVELOP TO SERVER
  /*if dbname_ = 'SKYLINE_FORM' then
      update skyline_form_server.FG_FORMENTITY t set t.entityimpinit = :NEW.entityimpinit, t.change_date = sysdate, t.comments = 'UPDATE ON SERVER CHANGE FROM: ' || t.entityimpinit
      where t.formcode = :NEW.formcode and t.entityimpcode = :NEW.entityimpcode;
  end if;*/

end;
/

prompt
prompt Creating trigger FG_FORMLASTSAVE_INF_ID_TRIG
prompt ============================================
prompt
create or replace trigger FG_FORMLASTSAVE_INF_ID_TRIG
  before insert on FG_FORMLASTSAVEVALUE_INF
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_FORMLASTSAVEVALUE_INF_SEQ.nextval
   into v_new_val from dual;
   :new.ID := v_new_val;
end;
/

prompt
prompt Creating trigger FG_FORMLASTSAVE_INF_I_TRIG
prompt ===========================================
prompt
create or replace trigger FG_FORMLASTSAVE_INF_I_TRIG
  before insert on FG_FORMLASTSAVEVALUE_INF
  for each row
declare
  -- local variables here
  v_new_val number;
begin

if nvl(:new.IS_IDLIST,0) <> 2 then
insert into FG_FORMLASTSAVEVALUE_HST
 (
    ID,
    FORMID,
    FORMCODE_ENTITY,
    ENTITYIMPCODE,
    ENTITYIMPVALUE,
    USERID,
    change_comment,
    change_by,
    change_type,
    change_date,
    sessionid,
    ACTIVE,
    DISPLAYVALUE,
    UPDATEJOBFLAG,
    DISPLAYLABEL,
    PATH_ID,
    IS_FILE,
    IS_IDLIST,
    DB_TRANSACTION_ID,
    TMP_ENTITYIMPVALUE,
    TMP_DISPLAYVALUE

 )
 values
 (
    :NEW.ID,
    :NEW.FORMID,
    :NEW.FORMCODE_ENTITY,
    :NEW.ENTITYIMPCODE,
    :NEW.ENTITYIMPVALUE,
    :NEW.USERID,
    null,
    :NEW.USERID,
    'I',
    sysdate,
    :NEW.sessionid,
    :NEW.ACTIVE,
    :NEW.DISPLAYVALUE,
    :new.UPDATEJOBFLAG,
    :new.DISPLAYLABEL,
    :new.PATH_ID,
    :new.IS_FILE,
    :new.IS_IDLIST,
    :new.DB_TRANSACTION_ID,
    :new.TMP_ENTITYIMPVALUE,
    :new.TMP_DISPLAYVALUE
 );
 end if;


end;
/

prompt
prompt Creating trigger FG_FORMLASTSAVE_INF_U_TRIG
prompt ===========================================
prompt
create or replace trigger FG_FORMLASTSAVE_INF_U_TRIG
before update on FG_FORMLASTSAVEVALUE_INF
referencing new as new old as old
for each row
begin
  
  if
    NVL(:NEW.change_by,0) = 100 OR :old.ENTITYIMPVALUE <> :new.ENTITYIMPVALUE or (:old.ENTITYIMPVALUE is null and :new.ENTITYIMPVALUE is not null) or (:old.ENTITYIMPVALUE is not null and :new.ENTITYIMPVALUE is null)
  then
if nvl(:new.IS_IDLIST,0) <> 2 then
insert into FG_FORMLASTSAVEVALUE_HST
 (
    ID,
    FORMID,
    FORMCODE_ENTITY,
    ENTITYIMPCODE,
    ENTITYIMPVALUE,
    USERID,
    change_comment,
    change_by,
    change_type,
    change_date,
    sessionid,
    ACTIVE,
    DISPLAYVALUE,
    DISPLAYLABEL,
    PATH_ID,
    IS_FILE, 
    IS_IDLIST, 
    DB_TRANSACTION_ID, 
    TMP_ENTITYIMPVALUE, 
    TMP_DISPLAYVALUE 
 )
 values
 (
    :NEW.ID,
    :NEW.FORMID,
    :NEW.FORMCODE_ENTITY,
    :NEW.ENTITYIMPCODE,
    :NEW.ENTITYIMPVALUE,
    :NEW.USERID,
    null,
    :NEW.change_by,
    'U',
    sysdate,
    :NEW.sessionid,
    :NEW.ACTIVE,
    :NEW.DISPLAYVALUE,
    :new.DISPLAYLABEL,
    :new.PATH_ID,
    :new.IS_FILE, 
    :new.IS_IDLIST, 
    :new.DB_TRANSACTION_ID, 
    :new.TMP_ENTITYIMPVALUE, 
    :new.TMP_DISPLAYVALUE 
 );
  end if;
  end if;
end;
/

prompt
prompt Creating trigger FG_FORMLASTSAVE_INSERT_TRIG
prompt ============================================
prompt
create or replace trigger FG_FORMLASTSAVE_INSERT_TRIG
  before insert on FG_FORMLASTSAVEVALUE
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_FORMLASTSAVEVALUE_SEQ.nextval
   into v_new_val from dual;
   :new.ID := v_new_val ;
    
end;
/

prompt
prompt Creating trigger FG_FORMLASTSAVE_UPDATE_TRIG
prompt ============================================
prompt
create or replace trigger FG_FORMLASTSAVE_UPDATE_TRIG
before update on FG_FORMLASTSAVEVALUE
referencing new as new old as old
for each row
begin
  DBMS_OUTPUT.put_line('NOT IN USE');
  /*if
    :old.ENTITYIMPVALUE <> :new.ENTITYIMPVALUE or (:old.ENTITYIMPVALUE is null and :new.ENTITYIMPVALUE is not null) or (:old.ENTITYIMPVALUE is not null and :new.ENTITYIMPVALUE is null)
  then*/
/*insert into FG_FORMLASTSAVEVALUE_HST
 (
    ID,
    FORMID,
    FORMCODE_ENTITY,
    ENTITYIMPCODE,
    ENTITYIMPVALUE,
    USERID,
    change_comment,
    change_by,
    change_type,
    change_date,
    sessionid,
    ACTIVE
 )
 values
 (
    :NEW.ID,
    :NEW.FORMID,
    :NEW.FORMCODE_ENTITY,
    :NEW.ENTITYIMPCODE,
    :NEW.ENTITYIMPVALUE,
    :NEW.USERID,
    null,
    :NEW.USERID,
    'U',
    sysdate,
    :NEW.sessionid,
    :NEW.ACTIVE
 );*/
  /*end if;*/
end;
/

prompt
prompt Creating trigger FG_FORM_INSERT_TRIG
prompt ====================================
prompt
create or replace trigger FG_FORM_INSERT_TRIG
  before insert on FG_FORM
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_FORM_SEQ.nextval
   into v_new_val from dual;
   :new.ID := v_new_val ;
   
 insert into FG_FORM_HST
 (
    id, 
    formcode, 
    description, 
    active, 
    form_type, 
    title, 
    subtitle, 
    use_as_template, 
    group_name, 
    numberoforder, 
    formcode_entity, 
    ignore_nav, 
    usecache, 
    change_date,
    change_type
 )
 values
 (
    :NEW.id, 
    :NEW.formcode, 
    :NEW.description, 
    :NEW.active, 
    :NEW.form_type, 
    :NEW.title, 
    :NEW.subtitle, 
    :NEW.use_as_template, 
    :NEW.group_name, 
    :NEW.numberoforder, 
    :NEW.formcode_entity, 
    :NEW.ignore_nav, 
    :NEW.usecache, 
    :NEW.change_date,
    'I'
 );
   


end FG_FORM_INSERT_TRIG;
/

prompt
prompt Creating trigger FG_FORM_UPDATE_TRIG
prompt ====================================
prompt
create or replace trigger FG_FORM_UPDATE_TRIG
before update on FG_FORM
referencing new as new old as old
for each row
begin
  
  /*if
    :old.ENTITYIMPVALUE <> :new.ENTITYIMPVALUE or (:old.ENTITYIMPVALUE is null and :new.ENTITYIMPVALUE is not null) or (:old.ENTITYIMPVALUE is not null and :new.ENTITYIMPVALUE is null)
  then*/
insert into FG_FORM_HST
 (
    id, 
    formcode, 
    description, 
    active, 
    form_type, 
    title, 
    subtitle, 
    use_as_template, 
    group_name, 
    numberoforder, 
    formcode_entity, 
    ignore_nav, 
    usecache, 
    change_date,
    change_type
 )
 values
 (
    :NEW.id, 
    :NEW.formcode, 
    :NEW.description, 
    :NEW.active, 
    :NEW.form_type, 
    :NEW.title, 
    :NEW.subtitle, 
    :NEW.use_as_template, 
    :NEW.group_name, 
    :NEW.numberoforder, 
    :NEW.formcode_entity, 
    :NEW.ignore_nav, 
    :NEW.usecache, 
    SYSDATE,
    'U'
 );
  /*end if;*/
end;
/

prompt
prompt Creating trigger FG_REPORT_LIST_INSERT_TRIG
prompt ===========================================
prompt
create or replace trigger FG_REPORT_LIST_INSERT_TRIG
  before insert on FG_REPORT_LIST
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_USER_REPORT_SEQ.nextval
   into v_new_val from dual;
   :new.ID := v_new_val ;
end;
/

prompt
prompt Creating trigger FG_RESOURCE_INSERT_TRIG
prompt ========================================
prompt
create or replace trigger FG_RESOURCE_INSERT_TRIG
  before insert on FG_RESOURCE
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_RESOURCE_SEQ.nextval
   into v_new_val from dual;
   :new.ID := v_new_val ;

end FG_RESOURCE_INSERT_TRIG;
/

prompt
prompt Creating trigger FG_RESULTS_INSERT_TRIG
prompt =======================================
prompt
create or replace trigger FG_RESULTS_INSERT_TRIG
  before insert on Fg_Results
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_RESULTS_SEQ.nextval
   into v_new_val from dual;
   :new.RESULT_ID := TO_CHAR(v_new_val) ;

   if NVL(:NEW.RESULT_IS_ACTIVE,0) = 1 THEN

      insert into FG_RESULTS_HST
       (
          result_id,
          experiment_id,
          result_test_name,
          result_name,
          sample_id ,
          result_value,
          result_uom_id,
          result_type,
          result_material_id,
          result_date,
          result_time,
          result_comment,
          selftest_id,
          result_is_active,
          resultref_id,
          result_is_webix,
          result_materialname,
          result_request_id,
          change_comment,
          change_by,
          change_type,
          change_date,
          result_change_by
       )
       values
       (
          :NEW.result_id,
          :NEW.experiment_id,
          :NEW.result_test_name,
          :NEW.result_name,
          :NEW.sample_id,
          :NEW.result_value,
          :NEW.result_uom_id,
          :NEW.result_type,
          :NEW.result_material_id,
          :NEW.result_date,
          :NEW.result_time,
          :NEW.result_comment,
          :NEW.selftest_id,
          :NEW.result_is_active,
          :NEW.resultref_id,
          :NEW.result_is_webix,
          :NEW.result_materialname,
          :NEW.result_request_id,
          null,
          :NEW.result_change_by, --TODO ADD USER ID TO RESULT
          'I',
          sysdate,
          :NEW.result_change_by
       );
  END IF;



end FG_RESULTS_INSERT_TRIG;
/

prompt
prompt Creating trigger FG_RESULTS_UPDATE_TRIG
prompt =======================================
prompt
create or replace trigger FG_RESULTS_UPDATE_TRIG
  before update on FG_RESULTS
  referencing new as new old as old
  for each row
declare
  -- local variables here
  --v_new_val number;
begin

If NVL(:NEW.RESULT_IS_ACTIVE,0) = 1 THEN

      insert into FG_RESULTS_HST
       (
          result_id,
          experiment_id,
          result_test_name,
          result_name,
          sample_id ,
          result_value,
          result_uom_id,
          result_type,
          result_material_id,
          result_date,
          result_time,
          result_comment,
          selftest_id,
          result_is_active,
          resultref_id,
          result_is_webix,
          result_materialname,
          result_request_id,
          change_comment,
          change_by,
          change_type,
          change_date,
          result_change_by
       )
       values
       (
          :NEW.result_id,
          :NEW.experiment_id,
          :NEW.result_test_name,
          :NEW.result_name,
          :NEW.sample_id,
          :NEW.result_value,
          :NEW.result_uom_id,
          :NEW.result_type,
          :NEW.result_material_id,
          :NEW.result_date,
          :NEW.result_time,
          :NEW.result_comment,
          :NEW.selftest_id,
          :NEW.result_is_active,
          :NEW.resultref_id,
          :NEW.result_is_webix,
          :NEW.result_materialname,
          :NEW.result_request_id,
          null,
          :NEW.result_change_by, --TODO ADD USER ID TO RESULT
          'U',
          sysdate,
          :NEW.result_change_by
       );
  END IF;
end;
/

prompt
prompt Creating trigger FG_R_MESSAGES_I_TRIG
prompt =====================================
prompt
create or replace trigger fg_r_messages_i_trig
  before insert on fg_r_messages
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select D_NOTIF_EMAIL_LOG_SEQ.nextval
   into v_new_val from dual;
   :new.message_id := v_new_val ;
end;
/

prompt
prompt Creating trigger FG_R_MESSAGES_STATE_HST_I_TRIG
prompt ===============================================
prompt
create or replace trigger fg_r_messages_state_hst_i_trig
  before insert on fg_r_messages_state_hst
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select fg_r_msg_state_hst_seq.nextval
   into v_new_val from dual;
   :new.change_id := v_new_val ;
end;
/

prompt
prompt Creating trigger FG_R_MESSAGES_STATE_I_TRIG
prompt ===========================================
prompt
create or replace trigger fg_r_messages_state_i_trig
  before insert on fg_r_messages_state
  for each row
declare
begin
   insert into fg_r_messages_state_hst
   (
      message_id,
      user_id,
      is_readed,
      is_deleted,
      changed_by,
      change_date,
      change_type
   )
   values
   (
      :new.message_id,
      :new.user_id,
      :new.is_readed,
      :new.is_deleted,
      :new.updated_by,
      :new.updated_date,
      'I'
   );
end;
/

prompt
prompt Creating trigger FG_R_MESSAGES_STATE_U_TRIG
prompt ===========================================
prompt
create or replace trigger fg_r_messages_state_u_trig
before update on fg_r_messages_state
referencing new as new old as old
for each row
begin

    insert into fg_r_messages_state_hst
   (
      message_id,
      user_id,
      is_readed,
      is_deleted,
      changed_by,
      change_date,
      change_type
   )
   values
   (
      :new.message_id,
      :new.user_id,
      :new.is_readed,
      :new.is_deleted,
      :new.updated_by,
      :new.updated_date,
      'U'
   );
end;
/

prompt
prompt Creating trigger FG_SEQUENCE_FILES_INSERT_TRIG
prompt ==============================================
prompt
create or replace trigger FG_SEQUENCE_FILES_INSERT_TRIG
  before insert on FG_SEQUENCE_FILES
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_SEQUENCE_FILES_SEQ.nextval
   into v_new_val from dual;
   :new.ID := v_new_val ;

end FG_SEQUENCE_FILES_INSERT_TRIG;
/

prompt
prompt Creating trigger FG_SEQUENCE_INSERT_TRIG
prompt ========================================
prompt
create or replace trigger FG_SEQUENCE_INSERT_TRIG
  before insert on FG_SEQUENCE
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_SEQUENCE_SEQ.nextval
   into v_new_val from dual;
   :new.ID := v_new_val ;

end FG_SEQUENCE_INSERT_TRIG;
/

prompt
prompt Creating trigger FG_UNITEST_LOG_I_TRIG
prompt ======================================
prompt
create or replace trigger fg_unitest_log_i_trig
  before insert on FG_UNITEST_LOG
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_UNITEST_LOG_SEQ.nextval
   into v_new_val from dual;
   :new.ID := v_new_val ;
end;
/

prompt
prompt Creating trigger FG_UNITTEST_SEQUENCE_AT_I_TRIG
prompt ===============================================
prompt
create or replace trigger FG_UNITTEST_SEQUENCE_AT_I_TRIG
  before insert on FG_UNITTEST_AT_SEQUENCE
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_UNITTEST_AT_SEQUENCE_SEQ.nextval
   into v_new_val from dual;
   :new.UNITTEST_AT_ID := v_new_val ;

end;
/

prompt
prompt Creating trigger FG_UNITTEST_SEQUENCE_INS_TRIG
prompt ==============================================
prompt
create or replace trigger FG_UNITTEST_SEQUENCE_INS_TRIG
  before insert on FG_UNITTEST_SEQUENCE
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_UNITTEST_SEQUENCE_SEQ.nextval
   into v_new_val from dual;
   :new.UNITTEST_ID := v_new_val ;

end FG_UNITTEST_SEQUENCE_INS_TRIG;
/

prompt
prompt Creating trigger FG_WEBIX_OUTPUT_INSERT_TRIG
prompt ============================================
prompt
create or replace trigger FG_WEBIX_OUTPUT_INSERT_TRIG
  before insert on Fg_Webix_Output
  for each row
declare
  -- local variables here
  v_new_val number;
begin
   select FG_WEBIX_OUTPUT_SEQ.nextval

   into v_new_val from dual;
   :new.RESULT_ID := TO_CHAR(v_new_val) ;

 IF NVL(:NEW.RESULT_IS_ACTIVE,0) = 1 THEN
     insert into Fg_Webix_Output_HST
     (
        result_id,
        step_id,
        batch_id,
        result_name,
        result_value,
        result_type,
        result_date,
        result_time,
        result_comment,
        result_is_active,
        result_test_name,
        mass,
        experiment_id,
        material_id,
        result_uom_id,
        samples,
        weight,
        moles,
        yield,
        indication_mb,
        sample_mb,
        component_id,
        preparationref_id,
        sample_id,
        analytic_data,
        weighting,
        stream_data,
        table_index_mb,
        change_comment,
        change_by,
        change_type,
        change_date,
        webix_change_by
     )
     values
     (
        :NEW.result_id,
        :NEW.step_id,
        :NEW.batch_id,
        :NEW.result_name,
        :NEW.result_value,
        :NEW.result_type,
        :NEW.result_date,
        :NEW.result_time,
        :NEW.result_comment,
        :NEW.result_is_active,
        :NEW.result_test_name,
        :NEW.mass,
        :NEW.experiment_id,
        :NEW.material_id,
        :NEW.result_uom_id,
        :NEW.samples,
        :NEW.weight,
        :NEW.moles,
        :NEW.yield,
        :NEW.indication_mb,
        :NEW.sample_mb,
        :NEW.component_id,
        :NEW.preparationref_id,
        :NEW.sample_id,
        :NEW.analytic_data,
        :NEW.weighting,
        :NEW.stream_data,
        :NEW.table_index_mb,
        null,
        :NEW.webix_change_by, --TODO ADD USER ID TO RESULT
        'I',
        sysdate,
        :NEW.webix_change_by
     );
 END IF;
end;
/

prompt
prompt Creating trigger FG_WEBIX_OUTPUT_UPDATE_TRIG
prompt ============================================
prompt
create or replace trigger FG_WEBIX_OUTPUT_UPDATE_TRIG
  before update on Fg_Webix_Output
  referencing new as new old as old
  for each row
declare
  -- local variables here
  --v_new_val number;
begin

IF NVL(:NEW.RESULT_IS_ACTIVE,0) = 1 THEN
     insert into Fg_Webix_Output_HST
     (
        result_id,
        step_id,
        batch_id,
        result_name,
        result_value,
        result_type,
        result_date,
        result_time,
        result_comment,
        result_is_active,
        result_test_name,
        mass,
        experiment_id,
        material_id,
        result_uom_id,
        samples,
        weight,
        moles,
        yield,
        indication_mb,
        sample_mb,
        component_id,
        preparationref_id,
        sample_id,
        analytic_data,
        weighting,
        stream_data,
        table_index_mb,
        change_comment,
        change_by,
        change_type,
        change_date,
        webix_change_by
     )
     values
     (
        :NEW.result_id,
        :NEW.step_id,
        :NEW.batch_id,
        :NEW.result_name,
        :NEW.result_value,
        :NEW.result_type,
        :NEW.result_date,
        :NEW.result_time,
        :NEW.result_comment,
        :NEW.result_is_active,
        :NEW.result_test_name,
        :NEW.mass,
        :NEW.experiment_id,
        :NEW.material_id,
        :NEW.result_uom_id,
        :NEW.samples,
        :NEW.weight,
        :NEW.moles,
        :NEW.yield,
        :NEW.indication_mb,
        :NEW.sample_mb,
        :NEW.component_id,
        :NEW.preparationref_id,
        :NEW.sample_id,
        :NEW.analytic_data,
        :NEW.weighting,
        :NEW.stream_data,
        :NEW.table_index_mb,
        null,
        :NEW.webix_change_by, --TODO ADD USER ID TO RESULT
        'U',
        sysdate,
        :NEW.webix_change_by
     );
 END IF;
end;
/


spool off
