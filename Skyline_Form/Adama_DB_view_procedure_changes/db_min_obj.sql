-----------------------------------------------
-- Export file for user SKYLINE_FORM_MIN     --
-- Created by comply on 24/11/2021, 12:46:40 --
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
prompt Creating table FG_I_USERSGROUP_SUMMARYLIST_MV
prompt =============================================
prompt
create table FG_I_USERSGROUP_SUMMARYLIST_MV
(
  parentid VARCHAR2(500),
  user_id  VARCHAR2(4000)
)
;

prompt
prompt Creating table FG_I_USERS_GROUP_SUMMARYDIS_MV
prompt =============================================
prompt
create table FG_I_USERS_GROUP_SUMMARYDIS_MV
(
  parentid VARCHAR2(500),
  user_id  VARCHAR2(100),
  username VARCHAR2(500)
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
prompt Creating table FG_REPORT_LIST_BU2
prompt =================================
prompt
create table FG_REPORT_LIST_BU2
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
  system_row         NUMBER
)
;

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
prompt Creating table FG_S_CUSTOMER_PIVOT
prompt ==================================
prompt
create table FG_S_CUSTOMER_PIVOT
(
  formid          VARCHAR2(100) not null,
  timestamp       DATE,
  change_by       VARCHAR2(100),
  sessionid       VARCHAR2(500),
  active          NUMBER,
  formcode        VARCHAR2(100),
  customername    VARCHAR2(500),
  description     VARCHAR2(500),
  cloneid         VARCHAR2(100),
  templateflag    VARCHAR2(100),
  formcode_entity VARCHAR2(100),
  created_by      VARCHAR2(100),
  creation_date   DATE default sysdate
)
;

prompt
prompt Creating table FG_S_DYNAMICREPORTSQL_PIVOT
prompt ==========================================
prompt
create table FG_S_DYNAMICREPORTSQL_PIVOT
(
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
  sqltext              VARCHAR2(500),
  dynamicreportsqlname VARCHAR2(500),
  execute              VARCHAR2(500),
  systemreport         VARCHAR2(500),
  sqlresulttable       VARCHAR2(500)
)
;

prompt
prompt Creating table FG_S_GROUPSCREW_PIVOT
prompt ====================================
prompt
create table FG_S_GROUPSCREW_PIVOT
(
  formid          VARCHAR2(100) not null,
  timestamp       DATE,
  change_by       VARCHAR2(100),
  sessionid       VARCHAR2(100),
  active          NUMBER,
  formcode        VARCHAR2(100),
  group_id        VARCHAR2(500),
  parentid        VARCHAR2(500),
  groupscrewname  VARCHAR2(500),
  formcode_entity VARCHAR2(100),
  cloneid         VARCHAR2(100),
  templateflag    VARCHAR2(100),
  created_by      VARCHAR2(100),
  creation_date   DATE
)
;

prompt
prompt Creating table FG_S_GROUP_PIVOT
prompt ===============================
prompt
create table FG_S_GROUP_PIVOT
(
  formid          VARCHAR2(100) not null,
  timestamp       DATE,
  change_by       VARCHAR2(100),
  sessionid       VARCHAR2(500),
  active          NUMBER,
  formcode        VARCHAR2(100),
  groupname       VARCHAR2(500),
  selectuser      VARCHAR2(500),
  grouptype       VARCHAR2(500),
  description     VARCHAR2(4000),
  site_id         VARCHAR2(500),
  formcode_entity VARCHAR2(100),
  cloneid         VARCHAR2(100),
  templateflag    VARCHAR2(100),
  created_by      VARCHAR2(100),
  creation_date   DATE default sysdate
)
;

prompt
prompt Creating table FG_S_LABORATORY_PIVOT
prompt ====================================
prompt
create table FG_S_LABORATORY_PIVOT
(
  formid          VARCHAR2(100) not null,
  timestamp       DATE,
  change_by       VARCHAR2(100),
  sessionid       VARCHAR2(100),
  active          NUMBER,
  formcode        VARCHAR2(100),
  laboratoryname  VARCHAR2(500),
  formnumberid    VARCHAR2(500),
  units_id        VARCHAR2(500),
  site_id         VARCHAR2(500),
  cloneid         VARCHAR2(100),
  templateflag    VARCHAR2(100),
  formcode_entity VARCHAR2(100),
  lab_manager_id  VARCHAR2(500),
  created_by      VARCHAR2(100),
  creation_date   DATE
)
;
create unique index LABORATORY_ID_UNIQUE on FG_S_LABORATORY_PIVOT (TRIM(UPPER(FORMNUMBERID)));

prompt
prompt Creating table FG_S_PERMISSIONOBJECT_PIVOT
prompt ==========================================
prompt
create table FG_S_PERMISSIONOBJECT_PIVOT
(
  formid                 VARCHAR2(100) not null,
  timestamp              DATE,
  change_by              VARCHAR2(100),
  sessionid              VARCHAR2(500),
  active                 NUMBER,
  formcode_entity        VARCHAR2(100),
  formcode               VARCHAR2(100),
  permissionobjectname   VARCHAR2(500),
  lab                    VARCHAR2(500),
  site                   VARCHAR2(500),
  unit                   VARCHAR2(500),
  objectsinherit         VARCHAR2(500),
  cloneid                VARCHAR2(100),
  templateflag           VARCHAR2(100),
  objectsinheritoncreate VARCHAR2(500),
  created_by             VARCHAR2(100),
  creation_date          DATE default sysdate
)
;

prompt
prompt Creating table FG_S_PERMISSIONPOLICY_PIVOT
prompt ==========================================
prompt
create table FG_S_PERMISSIONPOLICY_PIVOT
(
  formid               VARCHAR2(100) not null,
  timestamp            DATE,
  change_by            VARCHAR2(100),
  sessionid            VARCHAR2(500),
  active               NUMBER,
  formcode             VARCHAR2(100),
  customer_id          VARCHAR2(500),
  userrole_id          VARCHAR2(500),
  permissionpolicyname VARCHAR2(500),
  policyexpression     VARCHAR2(500),
  policypermission     VARCHAR2(500),
  cloneid              VARCHAR2(100),
  templateflag         VARCHAR2(100),
  formcode_entity      VARCHAR2(100)
)
;

prompt
prompt Creating table FG_S_PERMISSIONSCHEME_PIVOT
prompt ==========================================
prompt
create table FG_S_PERMISSIONSCHEME_PIVOT
(
  formid               VARCHAR2(100) not null,
  timestamp            DATE,
  change_by            VARCHAR2(100),
  sessionid            VARCHAR2(100),
  active               NUMBER,
  formcode_entity      VARCHAR2(100),
  formcode             VARCHAR2(100),
  permissionschemename VARCHAR2(500),
  users                VARCHAR2(500),
  permissiontable      VARCHAR2(500),
  groupscrew           VARCHAR2(500),
  screen               VARCHAR2(500),
  cloneid              VARCHAR2(100),
  templateflag         VARCHAR2(100),
  created_by           VARCHAR2(100),
  creation_date        DATE,
  maintenanceformlist  VARCHAR2(1000)
)
;

prompt
prompt Creating table FG_S_PERMISSIONSREF_INFA_MV
prompt ==========================================
prompt
create table FG_S_PERMISSIONSREF_INFA_MV
(
  formcode                   CHAR(14),
  id                         VARCHAR2(100) not null,
  permissionschemename       VARCHAR2(500),
  permissionsrefname         VARCHAR2(500),
  name                       VARCHAR2(500),
  site_id                    VARCHAR2(500),
  unit_id                    VARCHAR2(500),
  lab_id                     VARCHAR2(500),
  permission                 VARCHAR2(500),
  user_crew_list             VARCHAR2(4000),
  active                     NUMBER,
  is_active                  VARCHAR2(40),
  permissionobjectname_group VARCHAR2(1502)
)
;

prompt
prompt Creating table FG_S_PERMISSIONSREF_INF_MV
prompt =========================================
prompt
create table FG_S_PERMISSIONSREF_INF_MV
(
  formcode                   CHAR(14),
  id                         VARCHAR2(100) not null,
  permissionschemename       VARCHAR2(500),
  permissionsrefname         VARCHAR2(500),
  name                       VARCHAR2(500),
  site_id                    VARCHAR2(500),
  unit_id                    VARCHAR2(500),
  lab_id                     VARCHAR2(500),
  permission                 VARCHAR2(500),
  user_crew_list             VARCHAR2(4000),
  active                     NUMBER,
  is_active                  VARCHAR2(40),
  permissionobjectname_group VARCHAR2(1502)
)
;

prompt
prompt Creating table FG_S_PERMISSIONSREF_PIVOT
prompt ========================================
prompt
create table FG_S_PERMISSIONSREF_PIVOT
(
  formid             VARCHAR2(100) not null,
  timestamp          DATE,
  change_by          VARCHAR2(100),
  sessionid          VARCHAR2(500),
  active             NUMBER,
  formcode_entity    VARCHAR2(100),
  formcode           VARCHAR2(100),
  parentid           VARCHAR2(500),
  permission         VARCHAR2(500),
  permissionsrefname VARCHAR2(500),
  lab_id             VARCHAR2(500),
  unit_id            VARCHAR2(500),
  site_id            VARCHAR2(500),
  tabletype          VARCHAR2(500),
  cloneid            VARCHAR2(100),
  templateflag       VARCHAR2(100),
  created_by         VARCHAR2(100),
  creation_date      DATE default sysdate
)
;

prompt
prompt Creating table FG_S_SENSITIVITYLEVEL_PIVOT
prompt ==========================================
prompt
create table FG_S_SENSITIVITYLEVEL_PIVOT
(
  formid                VARCHAR2(100) not null,
  timestamp             DATE,
  change_by             VARCHAR2(100),
  sessionid             VARCHAR2(500),
  active                NUMBER,
  formcode              VARCHAR2(100),
  customer_id           VARCHAR2(500),
  sensitivitylevelname  VARCHAR2(500),
  description           VARCHAR2(500),
  formcode_entity       VARCHAR2(100),
  sensitivitylevelorder VARCHAR2(500),
  cloneid               VARCHAR2(100),
  templateflag          VARCHAR2(100),
  created_by            VARCHAR2(100),
  creation_date         DATE default sysdate
)
;

prompt
prompt Creating table FG_S_SITE_PIVOT
prompt ==============================
prompt
create table FG_S_SITE_PIVOT
(
  formid          VARCHAR2(100) not null,
  timestamp       DATE,
  change_by       VARCHAR2(100),
  sessionid       VARCHAR2(500),
  active          NUMBER,
  formcode        VARCHAR2(100),
  customer_id     VARCHAR2(500),
  sitename        VARCHAR2(500),
  cloneid         VARCHAR2(100),
  templateflag    VARCHAR2(100),
  formcode_entity VARCHAR2(100),
  created_by      VARCHAR2(100),
  creation_date   DATE default sysdate
)
;

prompt
prompt Creating table FG_S_SYSCONFDTCRITERIA_PIVOT
prompt ===========================================
prompt
create table FG_S_SYSCONFDTCRITERIA_PIVOT
(
  formid                VARCHAR2(100) not null,
  timestamp             DATE,
  cloneid               VARCHAR2(100),
  templateflag          VARCHAR2(100),
  change_by             VARCHAR2(100),
  sessionid             VARCHAR2(500),
  active                NUMBER,
  formcode_entity       VARCHAR2(100),
  formcode              VARCHAR2(100),
  sysconfdtcriterianame VARCHAR2(4000),
  argformcode           VARCHAR2(4000),
  argstruct             VARCHAR2(4000),
  sysconfsqlpool_id     VARCHAR2(4000),
  created_by            VARCHAR2(100),
  creation_date         DATE default sysdate
)
;

prompt
prompt Creating table FG_S_SYSCONFEXCELDATA_PIVOT
prompt ==========================================
prompt
create table FG_S_SYSCONFEXCELDATA_PIVOT
(
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
  sysconfexceldataname VARCHAR2(500),
  excelfile            VARCHAR2(500),
  exceldata            VARCHAR2(500)
)
;

prompt
prompt Creating table FG_S_SYSCONFPRESAVECALC_PIVOT
prompt ============================================
prompt
create table FG_S_SYSCONFPRESAVECALC_PIVOT
(
  formid                 VARCHAR2(100) not null,
  timestamp              DATE,
  change_by              VARCHAR2(100),
  sessionid              VARCHAR2(500),
  active                 NUMBER,
  formcode               VARCHAR2(100),
  arg3                   VARCHAR2(500),
  resultelement          VARCHAR2(500),
  arg4                   VARCHAR2(500),
  formulaorfunction      VARCHAR2(500),
  sysconfpresavecalcname VARCHAR2(500),
  arg5                   VARCHAR2(500),
  argelement             VARCHAR2(500),
  arg2                   VARCHAR2(500),
  arg1                   VARCHAR2(500),
  cloneid                VARCHAR2(100),
  templateflag           VARCHAR2(100),
  formcode_entity        VARCHAR2(100)
)
;

prompt
prompt Creating table FG_S_SYSCONFSQLCRITERIA_PIVOT
prompt ============================================
prompt
create table FG_S_SYSCONFSQLCRITERIA_PIVOT
(
  formid                 VARCHAR2(100) not null,
  timestamp              DATE,
  creation_date          DATE,
  cloneid                VARCHAR2(100),
  templateflag           VARCHAR2(100),
  change_by              VARCHAR2(100),
  created_by             VARCHAR2(100),
  sessionid              VARCHAR2(100),
  active                 NUMBER,
  formcode_entity        VARCHAR2(100),
  formcode               VARCHAR2(100),
  sqldescription         VARCHAR2(4000),
  additionalmatchinfo    VARCHAR2(500),
  structlevel            VARCHAR2(500),
  sqltext                VARCHAR2(4000),
  sysconfsqlcriterianame VARCHAR2(500),
  ignore                 VARCHAR2(500),
  executationtype        VARCHAR2(500),
  screen                 VARCHAR2(500),
  isdefault              VARCHAR2(500)
)
;

prompt
prompt Creating table FG_S_SYSCONFSQLCRITERIA_PIVOT1
prompt =============================================
prompt
create table FG_S_SYSCONFSQLCRITERIA_PIVOT1
(
  formid                 VARCHAR2(100) not null,
  timestamp              DATE,
  creation_date          DATE,
  cloneid                VARCHAR2(100),
  templateflag           VARCHAR2(100),
  change_by              VARCHAR2(100),
  created_by             VARCHAR2(100),
  sessionid              VARCHAR2(100),
  active                 NUMBER,
  formcode_entity        VARCHAR2(100),
  formcode               VARCHAR2(100),
  sqltype                VARCHAR2(500),
  sqldescription         VARCHAR2(4000),
  structlevel            VARCHAR2(500),
  sqltext                VARCHAR2(4000),
  ignore                 VARCHAR2(500),
  sysconfsqlcriterianame VARCHAR2(500),
  executationtype        VARCHAR2(500),
  isdefault              VARCHAR2(500),
  screen                 VARCHAR2(500),
  additionalmatchinfo    VARCHAR2(500)
)
;

prompt
prompt Creating table FG_S_SYSCONFSQLPOOL_PIVOT
prompt ========================================
prompt
create table FG_S_SYSCONFSQLPOOL_PIVOT
(
  formid             VARCHAR2(100) not null,
  timestamp          DATE,
  cloneid            VARCHAR2(100),
  templateflag       VARCHAR2(100),
  change_by          VARCHAR2(100),
  sessionid          VARCHAR2(500),
  active             NUMBER,
  formcode_entity    VARCHAR2(100),
  formcode           VARCHAR2(100),
  sqltype            VARCHAR2(500),
  sqldescription     VARCHAR2(4000),
  sysconfsqlpoolname VARCHAR2(500),
  sqltext            VARCHAR2(4000),
  structlevel        VARCHAR2(500),
  ignore             VARCHAR2(500),
  executationtype    VARCHAR2(500),
  screen             VARCHAR2(500),
  isdefault          VARCHAR2(500)
)
;

prompt
prompt Creating table FG_S_SYSCONFWFNEW_PIVOT
prompt ======================================
prompt
create table FG_S_SYSCONFWFNEW_PIVOT
(
  formid           VARCHAR2(100) not null,
  timestamp        DATE,
  change_by        VARCHAR2(100),
  sessionid        VARCHAR2(500),
  active           NUMBER,
  formcode         CHAR(12),
  parammapname     VARCHAR2(4000),
  parammapval      VARCHAR2(4000),
  removefromlist   VARCHAR2(4000),
  sysconfwfnewname VARCHAR2(4000),
  jsonname         VARCHAR2(4000)
)
;

prompt
prompt Creating table FG_S_SYSCONFWFSTATUS_PIVOT
prompt =========================================
prompt
create table FG_S_SYSCONFWFSTATUS_PIVOT
(
  formid              VARCHAR2(100) not null,
  timestamp           DATE,
  creation_date       DATE,
  cloneid             VARCHAR2(100),
  templateflag        VARCHAR2(100),
  change_by           VARCHAR2(100),
  created_by          VARCHAR2(100),
  sessionid           VARCHAR2(100),
  active              NUMBER,
  formcode_entity     VARCHAR2(100),
  formcode            VARCHAR2(100),
  wherepartparmname   VARCHAR2(500),
  sysconfwfstatusname VARCHAR2(500),
  statusformcode      VARCHAR2(500),
  statusinfcolumn     VARCHAR2(500),
  jsonname            VARCHAR2(500)
)
;

prompt
prompt Creating table FG_S_SYSEVENTHANDLERREF_PIVOT
prompt ============================================
prompt
create table FG_S_SYSEVENTHANDLERREF_PIVOT
(
  formid                 VARCHAR2(100) not null,
  timestamp              DATE,
  change_by              VARCHAR2(100),
  sessionid              VARCHAR2(500),
  active                 NUMBER,
  formcode               VARCHAR2(100),
  handlerorderonfail     VARCHAR2(4000),
  parentid               VARCHAR2(4000),
  tabletype              VARCHAR2(4000),
  syseventhandlerrefname VARCHAR2(4000),
  handlerorder           VARCHAR2(4000),
  created_by             VARCHAR2(100),
  creation_date          DATE default sysdate
)
;

prompt
prompt Creating table FG_S_SYSEVENTHANDLERSET_PIVOT
prompt ============================================
prompt
create table FG_S_SYSEVENTHANDLERSET_PIVOT
(
  formid                 VARCHAR2(100) not null,
  timestamp              DATE,
  change_by              VARCHAR2(100),
  sessionid              VARCHAR2(500),
  active                 NUMBER,
  formcode               VARCHAR2(100),
  handlerorderonfail     VARCHAR2(4000),
  handlersetcomment      VARCHAR2(4000),
  handlerorder           VARCHAR2(4000),
  syseventpoint_id       VARCHAR2(4000),
  syseventhandlersetname VARCHAR2(4000),
  cloneid                VARCHAR2(100),
  templateflag           VARCHAR2(100),
  formcode_entity        VARCHAR2(100),
  created_by             VARCHAR2(100),
  creation_date          DATE default sysdate
)
;

prompt
prompt Creating table FG_S_SYSEVENTHANDLER_PIVOT
prompt =========================================
prompt
create table FG_S_SYSEVENTHANDLER_PIVOT
(
  formid                VARCHAR2(100) not null,
  timestamp             DATE,
  creation_date         DATE,
  cloneid               VARCHAR2(100),
  templateflag          VARCHAR2(100),
  change_by             VARCHAR2(100),
  created_by            VARCHAR2(100),
  sessionid             VARCHAR2(100),
  active                NUMBER,
  formcode_entity       VARCHAR2(100),
  formcode              VARCHAR2(100),
  handlervalidation     VARCHAR2(500),
  syseventhandlername   VARCHAR2(500),
  calcarg               VARCHAR2(500),
  handlerorder          VARCHAR2(500),
  syseventpointfullname VARCHAR2(500),
  handlerdescription    VARCHAR2(500),
  handlerunittest       VARCHAR2(500),
  calcformula           VARCHAR2(4000)
)
;

prompt
prompt Creating table FG_S_SYSEVENTHANDLETYPE_PIVOT
prompt ============================================
prompt
create table FG_S_SYSEVENTHANDLETYPE_PIVOT
(
  formid                 VARCHAR2(100) not null,
  timestamp              DATE,
  creation_date          DATE default sysdate,
  cloneid                VARCHAR2(100),
  templateflag           VARCHAR2(100),
  change_by              VARCHAR2(100),
  created_by             VARCHAR2(100),
  sessionid              VARCHAR2(500),
  active                 NUMBER,
  formcode_entity        VARCHAR2(100),
  formcode               VARCHAR2(100),
  syseventhandletypename VARCHAR2(500)
)
;

prompt
prompt Creating table FG_S_SYSEVENTMANAGER_PIVOT
prompt =========================================
prompt
create table FG_S_SYSEVENTMANAGER_PIVOT
(
  formid              VARCHAR2(100) not null,
  timestamp           DATE,
  change_by           VARCHAR2(100),
  sessionid           VARCHAR2(500),
  active              NUMBER,
  formcode            VARCHAR2(100),
  handlerset          VARCHAR2(4000),
  formcodematch       VARCHAR2(4000),
  syseventmanagername VARCHAR2(4000),
  syseventtype_id     VARCHAR2(4000)
)
;

prompt
prompt Creating table FG_S_SYSEVENTPOINT_PIVOT
prompt =======================================
prompt
create table FG_S_SYSEVENTPOINT_PIVOT
(
  formid            VARCHAR2(100) not null,
  timestamp         DATE,
  creation_date     DATE,
  cloneid           VARCHAR2(100),
  templateflag      VARCHAR2(100),
  change_by         VARCHAR2(100),
  created_by        VARCHAR2(100),
  sessionid         VARCHAR2(100),
  active            NUMBER,
  formcode_entity   VARCHAR2(100),
  formcode          VARCHAR2(100),
  additionalmatch   VARCHAR2(500),
  formcodematch     VARCHAR2(500),
  syseventypename   VARCHAR2(500),
  syseventpointname VARCHAR2(500)
)
;

prompt
prompt Creating table FG_S_SYSEVENTTYPE_PIVOT
prompt ======================================
prompt
create table FG_S_SYSEVENTTYPE_PIVOT
(
  formid           VARCHAR2(100) not null,
  timestamp        DATE,
  creation_date    DATE default sysdate,
  cloneid          VARCHAR2(100),
  templateflag     VARCHAR2(100),
  change_by        VARCHAR2(100),
  created_by       VARCHAR2(100),
  sessionid        VARCHAR2(500),
  active           NUMBER,
  formcode_entity  VARCHAR2(100),
  formcode         VARCHAR2(100),
  syseventtypename VARCHAR2(500)
)
;

prompt
prompt Creating table FG_S_UNITS_PIVOT
prompt ===============================
prompt
create table FG_S_UNITS_PIVOT
(
  formid          VARCHAR2(100) not null,
  timestamp       DATE,
  change_by       VARCHAR2(100),
  sessionid       VARCHAR2(500),
  active          NUMBER,
  formcode        VARCHAR2(100),
  unitsname       VARCHAR2(500),
  site_id         VARCHAR2(500),
  cloneid         VARCHAR2(100),
  templateflag    VARCHAR2(100),
  formcode_entity VARCHAR2(100),
  created_by      VARCHAR2(100),
  creation_date   DATE default sysdate
)
;

prompt
prompt Creating table FG_S_UNITTESTCONFIG_PIVOT
prompt ========================================
prompt
create table FG_S_UNITTESTCONFIG_PIVOT
(
  formid                 VARCHAR2(100) not null,
  timestamp              DATE,
  change_by              VARCHAR2(100),
  sessionid              VARCHAR2(100),
  active                 NUMBER,
  formcode_entity        VARCHAR2(100),
  formcode               VARCHAR2(100),
  orderofexecution       VARCHAR2(500),
  unittestconfigname     VARCHAR2(500),
  unittestaction         VARCHAR2(500),
  ignoretest             VARCHAR2(500),
  waitingtime            VARCHAR2(500),
  entityimpname          VARCHAR2(500),
  testingformcode        VARCHAR2(500),
  fieldvalue             VARCHAR2(4000),
  unittestconfigcomments VARCHAR2(500),
  cloneid                VARCHAR2(100),
  templateflag           VARCHAR2(100),
  created_by             VARCHAR2(100),
  creation_date          DATE,
  groupname              VARCHAR2(500)
)
;

prompt
prompt Creating table FG_S_UNITTESTGROUP_PIVOT
prompt =======================================
prompt
create table FG_S_UNITTESTGROUP_PIVOT
(
  formid            VARCHAR2(100) not null,
  timestamp         DATE,
  change_by         VARCHAR2(100),
  sessionid         VARCHAR2(500),
  active            NUMBER,
  formcode_entity   VARCHAR2(100),
  formcode          VARCHAR2(100),
  orderofexecution  VARCHAR2(500),
  ignore            VARCHAR2(500),
  unittestgroupname VARCHAR2(500),
  unittestlevels    VARCHAR2(500),
  comments          VARCHAR2(500),
  cloneid           VARCHAR2(100),
  templateflag      VARCHAR2(100),
  created_by        VARCHAR2(100),
  creation_date     DATE default sysdate
)
;

prompt
prompt Creating table FG_S_UOMTYPE_PIVOT
prompt =================================
prompt
create table FG_S_UOMTYPE_PIVOT
(
  formid          VARCHAR2(100) not null,
  timestamp       DATE,
  change_by       VARCHAR2(100),
  sessionid       VARCHAR2(500),
  active          NUMBER,
  formcode        VARCHAR2(100),
  uomtypename     VARCHAR2(500),
  formcode_entity VARCHAR2(100),
  cloneid         VARCHAR2(100),
  templateflag    VARCHAR2(100),
  created_by      VARCHAR2(100),
  creation_date   DATE default sysdate
)
;
create unique index UOMTYPE_UNIQUE on FG_S_UOMTYPE_PIVOT (TRIM(UPPER(UOMTYPENAME)), TRIM(UPPER(TO_CHAR(ACTIVE))));

prompt
prompt Creating table FG_S_UOM_PIVOT
prompt =============================
prompt
create table FG_S_UOM_PIVOT
(
  formid          VARCHAR2(100) not null,
  timestamp       DATE,
  change_by       VARCHAR2(100),
  sessionid       VARCHAR2(500),
  active          NUMBER,
  formcode        VARCHAR2(100),
  isnormal        VARCHAR2(500),
  factor          VARCHAR2(500),
  precision       VARCHAR2(500),
  uomname         VARCHAR2(500),
  type            VARCHAR2(500),
  formcode_entity VARCHAR2(100),
  cloneid         VARCHAR2(100),
  templateflag    VARCHAR2(100),
  created_by      VARCHAR2(100),
  creation_date   DATE default sysdate
)
;
create unique index UOM_UNIQUE on FG_S_UOM_PIVOT (TRIM(UPPER(TO_CHAR(ACTIVE))), TRIM(UPPER(UOMNAME)), TRIM(UPPER(TYPE)));

prompt
prompt Creating table FG_S_USERGUIDEPOOL_PIVOT
prompt =======================================
prompt
create table FG_S_USERGUIDEPOOL_PIVOT
(
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
  userguidedescription VARCHAR2(4000),
  userguidefile        VARCHAR2(500),
  itemorder            VARCHAR2(500),
  userguidepoolname    VARCHAR2(500)
)
;

prompt
prompt Creating table FG_S_USERGUIDEVIDEOPOOL_PIVOT
prompt ============================================
prompt
create table FG_S_USERGUIDEVIDEOPOOL_PIVOT
(
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
  userguidedescription VARCHAR2(4000),
  userguidefile        VARCHAR2(4000),
  userguidename        VARCHAR2(4000)
)
;

prompt
prompt Creating table FG_S_USERROLE_PIVOT
prompt ==================================
prompt
create table FG_S_USERROLE_PIVOT
(
  formid          VARCHAR2(100) not null,
  timestamp       DATE,
  change_by       VARCHAR2(100),
  sessionid       VARCHAR2(500),
  active          NUMBER,
  formcode        VARCHAR2(100),
  customer_id     VARCHAR2(500),
  userrolename    VARCHAR2(500),
  formcode_entity VARCHAR2(100),
  cloneid         VARCHAR2(100),
  templateflag    VARCHAR2(100),
  created_by      VARCHAR2(100),
  creation_date   DATE default sysdate
)
;

prompt
prompt Creating table FG_S_USERSCREW_PIVOT
prompt ===================================
prompt
create table FG_S_USERSCREW_PIVOT
(
  formid          VARCHAR2(100) not null,
  timestamp       DATE,
  change_by       VARCHAR2(100),
  sessionid       VARCHAR2(100),
  active          NUMBER,
  formcode        VARCHAR2(100),
  userscrewname   VARCHAR2(500),
  parentid        VARCHAR2(500),
  user_id         VARCHAR2(500),
  formcode_entity VARCHAR2(100),
  cloneid         VARCHAR2(100),
  disabled        VARCHAR2(500),
  templateflag    VARCHAR2(100),
  created_by      VARCHAR2(100),
  creation_date   DATE
)
;

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
start with 2167408
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
start with 73675
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
start with 166271
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
start with 307195
increment by 1
cache 15;

prompt
prompt Creating sequence FG_SEQUENCE_SEQ
prompt =================================
prompt
create sequence FG_SEQUENCE_SEQ
minvalue 1
maxvalue 999999999999999999999999999
start with 367506
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
prompt Creating view FG_S_DYNAMICREPORTSQL_V
prompt =====================================
prompt
create or replace view fg_s_dynamicreportsql_v as
select to_number(t.formid) as dynamicreportsql_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.DynamicReportSqlName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as DynamicReportSql_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."EXECUTE",t."SQLTEXT",t."DYNAMICREPORTSQLNAME",t."SYSTEMREPORT",t."SQLRESULTTABLE"
      from FG_S_DYNAMICREPORTSQL_PIVOT t;

prompt
prompt Creating view FG_S_DYNAMICREPORTSQL_ALL_V
prompt =========================================
prompt
create or replace view fg_s_dynamicreportsql_all_v as
select t."DYNAMICREPORTSQL_ID",t."FORM_TEMP_ID",t."DYNAMICREPORTSQL_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."EXECUTE",t."SQLTEXT",t."DYNAMICREPORTSQLNAME",t."SYSTEMREPORT",t."SQLRESULTTABLE"
--t.* end! edit only the code below...
,c.file_content as SQLTEXT_CONTENT
              from FG_S_DYNAMICREPORTSQL_V t,
                   fg_clob_files c
              where t.SQLTEXT = c.file_id;

prompt
prompt Creating view FG_AUTHEN_DYNAMICREPORTSQL_V
prompt ==========================================
prompt
create or replace view fg_authen_dynamicreportsql_v as
select "DYNAMICREPORTSQL_ID","FORM_TEMP_ID","DYNAMICREPORTSQL_OBJIDVAL","FORMID","TIMESTAMP","CREATION_DATE","CLONEID","TEMPLATEFLAG","CHANGE_BY","CREATED_BY","SESSIONID","ACTIVE","FORMCODE_ENTITY","FORMCODE","SQLTEXT","DYNAMICREPORTSQLNAME"
              from FG_S_DYNAMICREPORTSQL_ALL_V;

prompt
prompt Creating view FG_S_GROUPSCREW_V
prompt ===============================
prompt
create or replace view fg_s_groupscrew_v as
select to_number(t.formid) as groupscrew_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.GroupsCrewName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as GroupsCrew_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."GROUP_ID",t."PARENTID",t."GROUPSCREWNAME"
      from FG_S_GROUPSCREW_PIVOT t;

prompt
prompt Creating view FG_S_GROUP_V
prompt ==========================
prompt
CREATE OR REPLACE VIEW FG_S_GROUP_V AS
select to_number(t.formid) as group_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.GroupName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as Group_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."GROUPNAME",t."SELECTUSER",t."GROUPTYPE",t."DESCRIPTION",t."SITE_ID",t."FORMCODE_ENTITY",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_GROUP_PIVOT t;

prompt
prompt Creating view FG_S_GROUPSCREW_ALL_V
prompt ===================================
prompt
create or replace view fg_s_groupscrew_all_v as
select t."GROUPSCREW_ID",t."FORM_TEMP_ID",t."GROUPSCREW_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."GROUP_ID",t."PARENTID",t."GROUPSCREWNAME"
--t.* end! edit only the code below...
,pt.GroupName as "GROUP",pt.DESCRIPTION,pt.GROUP_ID as "GROUP_ID_SINGLE"
from FG_S_GROUPSCREW_V t,FG_S_GROUP_V pt
    --  where t.GROUP_ID = pt.GROUP_ID(+)
    where 1=1 and
    instr(',' || t.GROUP_ID || ',',',' || pt.GROUP_ID || ',') > 0;

prompt
prompt Creating view FG_AUTHEN_GROUPSCREW_V
prompt ====================================
prompt
create or replace view fg_authen_groupscrew_v as
select "GROUPSCREW_ID","GROUPSCREW_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","PARENTID","GROUPSCREWNAME","GROUP_ID"
      from FG_S_GROUPSCREW_ALL_V t;

prompt
prompt Creating view FG_S_GROUP_ALL_V
prompt ==============================
prompt
CREATE OR REPLACE VIEW FG_S_GROUP_ALL_V AS
select t."GROUP_ID",t."FORM_TEMP_ID",t."GROUP_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."GROUPNAME",t."SELECTUSER",t."GROUPTYPE",t."DESCRIPTION",t."SITE_ID",t."FORMCODE_ENTITY",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
from FG_S_GROUP_V t;

prompt
prompt Creating view FG_AUTHEN_GROUP_V
prompt ===============================
prompt
create or replace view fg_authen_group_v as
select "GROUP_ID","GROUP_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","DESCRIPTION","GROUPNAME"
      from FG_S_GROUP_ALL_V t;

prompt
prompt Creating view FG_S_LABORATORY_V
prompt ===============================
prompt
create or replace view fg_s_laboratory_v as
select to_number(t.formid) as laboratory_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.LaboratoryName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as Laboratory_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."LAB_MANAGER_ID",t."FORMNUMBERID",t."LABORATORYNAME",t."UNITS_ID",t."SITE_ID"
      from FG_S_LABORATORY_PIVOT t;

prompt
prompt Creating view FG_S_UNITS_V
prompt ==========================
prompt
create or replace view fg_s_units_v as
select to_number(t.formid) as units_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.UnitsName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as Units_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."UNITSNAME",t."SITE_ID",t."CLONEID",t."TEMPLATEFLAG",t."FORMCODE_ENTITY",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_UNITS_PIVOT t;

prompt
prompt Creating view FG_S_SITE_V
prompt =========================
prompt
create or replace view fg_s_site_v as
select to_number(t.formid) as site_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SiteName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as Site_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."CUSTOMER_ID",t."SITENAME",t."CLONEID",t."TEMPLATEFLAG",t."FORMCODE_ENTITY",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_SITE_PIVOT t;

prompt
prompt Creating view FG_S_SITE_ALL_V
prompt =============================
prompt
create or replace view fg_s_site_all_v as
select t."SITE_ID",t."FORM_TEMP_ID",t."SITE_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."CUSTOMER_ID",t."SITENAME",t."CLONEID",t."TEMPLATEFLAG",t."FORMCODE_ENTITY",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
from FG_S_SITE_V t;

prompt
prompt Creating view FG_S_UNITS_ALL_V
prompt ==============================
prompt
create or replace view fg_s_units_all_v as
select t."UNITS_ID",t."FORM_TEMP_ID",t."UNITS_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."UNITSNAME",t."SITE_ID",t."CLONEID",t."TEMPLATEFLAG",t."FORMCODE_ENTITY",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
,s.SITENAME, s.SITE_OBJIDVAL, '{"VAL":"' || t.UnitsName||' ('||s.SiteName||')' || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as unit_with_site_objidval
from FG_S_UNITS_V t,
      FG_S_SITE_ALL_V s
where s.SITE_ID(+) = t.SITE_ID;

prompt
prompt Creating view FG_S_LABORATORY_ALL_V
prompt ===================================
prompt
create or replace view fg_s_laboratory_all_v as
select t."LABORATORY_ID",t."FORM_TEMP_ID",t."LABORATORY_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."LAB_MANAGER_ID",t."FORMNUMBERID",t."LABORATORYNAME",t."UNITS_ID",t."SITE_ID"
--t.* end! edit only the code below...
,u.SITENAME,u.SITE_OBJIDVAL, u.UNITSNAME, u.UNITS_OBJIDVAL
from FG_S_LABORATORY_V t,
     fg_s_units_all_v u
where u.SITE_ID(+) = t.SITE_ID
and u.units_id(+) = t.UNITS_ID;

prompt
prompt Creating view FG_AUTHEN_LABORATORY_V
prompt ====================================
prompt
create or replace view fg_authen_laboratory_v as
select "LABORATORY_ID","LABORATORY_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","LABORATORYNAME"
      from FG_S_LABORATORY_ALL_V t;

prompt
prompt Creating view FG_S_PERMISSIONOBJECT_V
prompt =====================================
prompt
create or replace view fg_s_permissionobject_v as
select to_number(t.formid) as permissionobject_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.PermissionObjectName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as PermissionObject_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."PERMISSIONOBJECTNAME",t."LAB",t."SITE",t."UNIT",t."OBJECTSINHERIT",t."CLONEID",t."TEMPLATEFLAG",t."OBJECTSINHERITONCREATE",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_PERMISSIONOBJECT_PIVOT t;

prompt
prompt Creating view FG_S_PERMISSIONOBJECT_ALL_V
prompt =========================================
prompt
create or replace view fg_s_permissionobject_all_v as
select t."PERMISSIONOBJECT_ID",t."FORM_TEMP_ID",t."PERMISSIONOBJECT_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."PERMISSIONOBJECTNAME",t."LAB",t."SITE",t."UNIT",t."OBJECTSINHERIT",t."CLONEID",t."TEMPLATEFLAG",t."OBJECTSINHERITONCREATE",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
 --,decode(t."OBJECTSINHERIT",null,t."PERMISSIONOBJECTNAME", t."PERMISSIONOBJECTNAME" ||','|| "OBJECTSINHERIT") "PERMISSIONOBJECTNAME_EXTEND",
,'{"VAL":"' ||
replace(
    replace(
        replace(
            replace(
                 regexp_replace(decode(t.PermissionObjectName,'InvItemMaterial','InvItemMaterialCm',t.PermissionObjectName),'^InvItem'),
                'RecipeFormulation',
                'Recipe')
        ,'MaterialCm','Material (Chemical)'),
    'MaterialFr','Material (Formulation)')
,'MaterialPr','Material (Premix)')
|| '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as PermissionObjectName_objidval -- effects only the display in the list (set it also in the fg_s_PermissionSRef_DT_v view)
,t."PERMISSIONOBJECTNAME" || decode(t."OBJECTSINHERITONCREATE",null,'', ',' || t."OBJECTSINHERITONCREATE") || decode(t."OBJECTSINHERIT",null,'', ',' || t."OBJECTSINHERIT") AS  "PERMISSIONOBJECTNAME_GROUP"
              from FG_S_PERMISSIONOBJECT_V t;

prompt
prompt Creating view FG_AUTHEN_PERMISSIONOBJECT_V
prompt ==========================================
prompt
create or replace view fg_authen_permissionobject_v as
select "PERMISSIONOBJECT_ID","FORM_TEMP_ID","PERMISSIONOBJECT_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE_ENTITY","FORMCODE","PERMISSIONOBJECTNAME","LAB","SITE","UNIT","OBJECTSINHERIT"
              from FG_S_PERMISSIONOBJECT_ALL_V;

prompt
prompt Creating view FG_S_PERMISSIONPOLICY_V
prompt =====================================
prompt
create or replace view fg_s_permissionpolicy_v as
select to_number(t.formid) as permissionpolicy_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.PermissionPolicyName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as PermissionPolicy_objidval,
             t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."CUSTOMER_ID",t."USERROLE_ID",t."PERMISSIONPOLICYNAME",t."POLICYEXPRESSION",t."POLICYPERMISSION"
      from FG_S_PERMISSIONPOLICY_PIVOT t;

prompt
prompt Creating view FG_S_USERROLE_V
prompt =============================
prompt
create or replace view fg_s_userrole_v as
select to_number(t.formid) as userrole_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.UserRoleName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as UserRole_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."CUSTOMER_ID",t."USERROLENAME",t."FORMCODE_ENTITY",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_USERROLE_PIVOT t;

prompt
prompt Creating view FG_S_USERROLE_ALL_V
prompt =================================
prompt
create or replace view fg_s_userrole_all_v as
select t."USERROLE_ID",t."FORM_TEMP_ID",t."USERROLE_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."CUSTOMER_ID",t."USERROLENAME",t."FORMCODE_ENTITY",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
from FG_S_USERROLE_V t;

prompt
prompt Creating view FG_S_CUSTOMER_V
prompt =============================
prompt
create or replace view fg_s_customer_v as
select to_number(t.formid) as customer_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.CustomerName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as Customer_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."CUSTOMERNAME",t."DESCRIPTION",t."CLONEID",t."TEMPLATEFLAG",t."FORMCODE_ENTITY",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_CUSTOMER_PIVOT t;

prompt
prompt Creating view FG_S_CUSTOMER_ALL_V
prompt =================================
prompt
create or replace view fg_s_customer_all_v as
select t."CUSTOMER_ID",t."FORM_TEMP_ID",t."CUSTOMER_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."CUSTOMERNAME",t."DESCRIPTION",t."CLONEID",t."TEMPLATEFLAG",t."FORMCODE_ENTITY",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
              from FG_S_CUSTOMER_V t;

prompt
prompt Creating view FG_S_PERMISSIONPOLICY_ALL_V
prompt =========================================
prompt
create or replace view fg_s_permissionpolicy_all_v as
select t."PERMISSIONPOLICY_ID",t."FORM_TEMP_ID",t."PERMISSIONPOLICY_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."CUSTOMER_ID",t."USERROLE_ID",t."PERMISSIONPOLICYNAME",t."POLICYEXPRESSION",t."POLICYPERMISSION"
--t.* end! edit only the code below...
      ,c.CUSTOMERNAME, ur.USERROLENAME
from FG_S_PERMISSIONPOLICY_V t,
     fg_s_customer_all_v c,
     fg_s_userrole_all_v ur
where t.USERROLE_ID = ur.USERROLE_ID(+)
and   t.CUSTOMER_ID = c.CUSTOMER_ID(+);

prompt
prompt Creating view FG_AUTHEN_PERMISSIONPOLICY_V
prompt ==========================================
prompt
create or replace view fg_authen_permissionpolicy_v as
select "PERMISSIONPOLICY_ID","FORM_TEMP_ID","PERMISSIONPOLICY_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","PERMISSIONPOLICYNAME"
              from FG_S_PERMISSIONPOLICY_ALL_V t where rownum <= 1;

prompt
prompt Creating view FG_S_PERMISSIONSCHEME_V
prompt =====================================
prompt
create or replace view fg_s_permissionscheme_v as
select to_number(t.formid) as permissionscheme_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.PermissionSchemeName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as PermissionScheme_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."MAINTENANCEFORMLIST",t."PERMISSIONSCHEMENAME",t."PERMISSIONTABLE",t."USERS",t."GROUPSCREW",t."SCREEN"
      from FG_S_PERMISSIONSCHEME_PIVOT t;

prompt
prompt Creating view FG_S_PERMISSIONSCHEME_ALL_V
prompt =========================================
prompt
create or replace view fg_s_permissionscheme_all_v as
select t."PERMISSIONSCHEME_ID",t."FORM_TEMP_ID",t."PERMISSIONSCHEME_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."MAINTENANCEFORMLIST",t."PERMISSIONSCHEMENAME",t."PERMISSIONTABLE",t."USERS",t."GROUPSCREW",t."SCREEN"
--t.* end! edit only the code below...
              from FG_S_PERMISSIONSCHEME_V t;

prompt
prompt Creating view FG_AUTHEN_PERMISSIONSCHEME_V
prompt ==========================================
prompt
create or replace view fg_authen_permissionscheme_v as
select "PERMISSIONSCHEME_ID","FORM_TEMP_ID","PERMISSIONSCHEME_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE_ENTITY","FORMCODE","PERMISSIONSCHEMENAME","USERS","PERMISSIONTABLE","GROUPSCREW","SCREEN"
              from FG_S_PERMISSIONSCHEME_ALL_V;

prompt
prompt Creating view FG_S_PERMISSIONSREF_V
prompt ===================================
prompt
create or replace view fg_s_permissionsref_v as
select to_number(t.formid) as permissionsref_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.PermissionSRefName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as PermissionSRef_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."PARENTID",t."PERMISSION",t."PERMISSIONSREFNAME",t."LAB_ID",t."UNIT_ID",t."SITE_ID",t."TABLETYPE",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_PERMISSIONSREF_PIVOT t;

prompt
prompt Creating view FG_I_LABALLOWN_V
prompt ==============================
prompt
create or replace view fg_i_laballown_v as
select 10 as "LABORATORY_ID", '{"VAL":"All Labs","ID":"10", "ACTIVE":"1"}' as "LABORATORY_OBJIDVAL",'All Labs' as "LABORATORYNAME",lab.UNITS_ID as "UNITS_ID",lab.SITE_ID,'' as labunit_objidval from FG_S_LABORATORY_all_V lab
union
select 10 as "LABORATORY_ID", '{"VAL":"All Labs","ID":"10", "ACTIVE":"1"}' as "LABORATORY_OBJIDVAL",'All Labs' as "LABORATORYNAME",'10' as "UNITS_ID",'10' as "SITE_ID",'' as labunit_objidval from dual
union
select 11 as "LABORATORY_ID", '{"VAL":"Own","ID":"11", "ACTIVE":"1"}' as "LABORATORY_OBJIDVAL" ,'Own' as "LABORATORYNAME",'11' as "UNITS_ID",'11' as "SITE_ID",'' as labunit_objidval from dual
union
select 12 as "LABORATORY_ID", '{"VAL":"Not Own","ID":"12", "ACTIVE":"1"}' as "LABORATORY_OBJIDVAL",'Not Own' as "LABORATORYNAME" ,'12' as "UNITS_ID", '12' as "SITE_ID",'' as labunit_objidval from dual
union
select 11 as "LABORATORY_ID", '{"VAL":"Own","ID":"11", "ACTIVE":"1"}' as "LABORATORY_OBJIDVAL" ,'Own' as "LABORATORYNAME",lab.UNITS_ID as "UNITS_ID",lab.SITE_ID,'' as labunit_objidval from FG_S_LABORATORY_all_V lab
union
select 12 as "LABORATORY_ID", '{"VAL":"Not Own","ID":"12", "ACTIVE":"1"}' as "LABORATORY_OBJIDVAL",'Not Own' as "LABORATORYNAME",lab.UNITS_ID as "UNITS_ID",lab.SITE_ID,'' as labunit_objidval from FG_S_LABORATORY_all_V lab
union all
select "LABORATORY_ID","LABORATORY_OBJIDVAL","LABORATORYNAME","UNITS_ID","SITE_ID",'{"VAL":"'||LABORATORYNAME||' ('||UNITSNAME||')'||'","ID":"'||LABORATORY_ID||'", "ACTIVE":"'||ACTIVE||'"}' as labunit_objidval from
(select  t."LABORATORY_ID",t."LABORATORY_OBJIDVAL",t."LABORATORYNAME",t."UNITS_ID",t.SITE_ID,t.UNITSNAME,t.ACTIVE
from FG_S_LABORATORY_all_V t
order by LABORATORYNAME);

prompt
prompt Creating view FG_I_PERMISSIONCRUD_V
prompt ===================================
prompt
create or replace view fg_i_permissioncrud_v as
select "NAME","CODE","OBJECTID","PERMISSIONOBJECTNAME" from (
with permission_list as (
-- the code is match getCrudlByPermissionItem java function but not in use in this context
select 'All permissions' as name,'_' as code from dual
union
select 'Create' as name,'C' as code from dual
union
select 'Read' as name,'R' as code from dual
union
select 'Update' as name,'U' as code from dual
union
select 'Approval' as name,'A' as code from dual
union
select 'Cancellation' as name,'D' as code from dual
union
select 'Reopen' as name,'O' as code from dual
)
select distinct
       permission_list.*,
       o.permissionobject_id as "OBJECTID", -- for filtering by the parent object id
       o.PermissionObjectName
from permission_list,
     fg_s_permissionobject_v o
)
where 1=1
--approval only in experiment / Template / RecipeFormulation
and (name <> 'Approval' or name||PermissionObjectName = 'ApprovalExperiment' or name||PermissionObjectName = 'ApprovalTemplate' or name||PermissionObjectName = 'ApprovalRecipeFormulation')
and (name <> 'Cancellation' or name||PermissionObjectName = 'CancellationInvItemMaterial' or name||PermissionObjectName = 'CancellationInvItemMaterialFr' or name||PermissionObjectName = 'CancellationInvItemMaterialPr')
and (name <> 'Reopen' or name||PermissionObjectName = 'ReopenExperiment' or name||PermissionObjectName = 'ReopenRecipeFormulation');

prompt
prompt Creating view FG_I_SITEALLOWN_V
prompt ===============================
prompt
create or replace view fg_i_siteallown_v as
select 10 as "SITE_ID", '{"VAL":"All Sites","ID":"10", "ACTIVE":"1"}' as "SITE_OBJIDVAL",'All Sites' as "SITENAME"   from dual
union all
select 11 as "SITE_ID", '{"VAL":"Own","ID":"11", "ACTIVE":"1"}' as "SITE_OBJIDVAL",'Own' as "SITENAME"   from dual
union all
select 12 as "SITE_ID", '{"VAL":"Not Own","ID":"12", "ACTIVE":"1"}' as "SITE_OBJIDVAL",'Not Own' as "SITENAME"   from dual
union all
select "SITE_ID","SITE_OBJIDVAL","SITENAME" from
(select t."SITE_ID",t."SITE_OBJIDVAL",t."SITENAME"
from FG_S_SITE_V t
order by t."SITENAME");

prompt
prompt Creating view FG_I_UNITALLOWN_V
prompt ===============================
prompt
create or replace view fg_i_unitallown_v as
select 10 as "UNITS_ID", '{"VAL":"All Units","ID":"10", "ACTIVE":"1"}' as "UNITS_OBJIDVAL",'All Units' as "UNITSNAME",u.SITE_ID as "SITE_ID",'' as siteunit_objidval from FG_S_UNITS_V u
union
select 10 as "UNITS_ID", '{"VAL":"All Units","ID":"10", "ACTIVE":"1"}' as "UNITS_OBJIDVAL",'All Units' as "UNITSNAME",'10' as "SITE_ID",'' from dual
union
select 11 as "UNITS_ID", '{"VAL":"Own","ID":"11", "ACTIVE":"1"}' as "UNITS_OBJIDVAL",'Own' as "UNITSNAME", '11' as "SITE_ID",''  from dual
union
select 12 as "UNITS_ID", '{"VAL":"Not Own","ID":"12", "ACTIVE":"1"}' as "UNITS_OBJIDVAL",'Not Own' as "UNITSNAME" , '12' as "SITE_ID",''  from dual
union
select 11 as "UNITS_ID", '{"VAL":"Own","ID":"11", "ACTIVE":"1"}' as "UNITS_OBJIDVAL",'Own' as "UNITSNAME",u.SITE_ID as "SITE_ID",'' from FG_S_UNITS_V u
union
select 12 as "UNITS_ID", '{"VAL":"Not Own","ID":"12", "ACTIVE":"1"}' as "UNITS_OBJIDVAL",'Not Own' as "UNITSNAME" ,u.SITE_ID as "SITE_ID",'' from FG_S_UNITS_V u
union all
select "UNITS_ID","UNITS_OBJIDVAL","UNITSNAME","SITE_ID", '{"VAL":"'||UnitsName||' ('||SITENAME||')'||'","ID":"'||UNITS_ID||'", "ACTIVE":"'||ACTIVE||'"}' as siteunit_objidval from
(select t."UNITS_ID" ,t.UNITS_OBJIDVAL,t.UnitsName ,t."SITE_ID",t.SITENAME,t.ACTIVE
from FG_S_UNITS_all_V t
order by unitsname);

prompt
prompt Creating view FG_S_PERMISSIONSREF_ALL_V
prompt =======================================
prompt
create or replace view fg_s_permissionsref_all_v as
select distinct t."PERMISSIONSREF_ID",t."FORM_TEMP_ID",t."PERMISSIONSREF_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."PARENTID",t."PERMISSION",t."PERMISSIONSREFNAME",t."LAB_ID",t."UNIT_ID",t."SITE_ID",t."TABLETYPE",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
,p."PERMISSIONOBJECTNAME"
,s.SITENAME
,u.UNITSNAME
,l.LABORATORYNAME
,ps.permission_single--regexp_substr(t."PERMISSION", '[^,]+', 1, LEVEL) as permission_single
from FG_S_PERMISSIONSREF_V t ,fg_s_permissionobject_all_v p,fg_i_siteallown_v s,fg_i_unitallown_v u,fg_i_laballown_v l--,fg_s_site_all_v s,fg_s_units_all_v u,fg_s_laboratory_all_v l
 --adib 151018 changed the "connect by" query in the column permission_single above to be accepted from instr
 ,/*(select distinct regexp_substr(t."PERMISSION", '[^,]+', 1, LEVEL) as permission_single
  from fg_s_permissionsref_v t
  CONNECT BY REGEXP_SUBSTR(t."PERMISSION", '[^,]+', 1, LEVEL) IS NOT NULL ) ps*/ --> same result but faster (the origin result also old crudl like delete and list that should be ignored)
  (select distinct name as permission_single from FG_I_PERMISSIONCRUD_V t) ps
where s.SITE_ID(+) = t.SITE_ID
 and u.UNITS_ID(+) = t.UNIT_ID
 and l.LABORATORY_ID(+)= t.LAB_ID
 and p.PERMISSIONOBJECT_ID(+)= t.PERMISSIONSREFNAME
 and instr(','||t.PERMISSION||',',','||ps.permission_single||',')>0 --CONNECT BY REGEXP_SUBSTR(t."PERMISSION", '[^,]+', 1, LEVEL) IS NOT NULL;

prompt
prompt Creating view FG_AUTHEN_PERMISSIONSREF_V
prompt ========================================
prompt
create or replace view fg_authen_permissionsref_v as
select "PERMISSIONSREF_ID","FORM_TEMP_ID","PERMISSIONSREF_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE_ENTITY","FORMCODE","PARENTID","PERMISSION","PERMISSIONSREFNAME","LAB_ID","UNIT_ID","SITE_ID","TABLETYPE"
              from FG_S_PERMISSIONSREF_ALL_V;

prompt
prompt Creating view FG_AUTHEN_SITE_V
prompt ==============================
prompt
create or replace view fg_authen_site_v as
select "SITE_ID","SITE_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SITENAME"
      from FG_S_SITE_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFDTCRITERIA_V
prompt ======================================
prompt
create or replace view fg_s_sysconfdtcriteria_v as
select to_number(t.formid) as sysconfdtcriteria_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysConfDTCriteriaName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysConfDTCriteria_objidval,
             t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SYSCONFDTCRITERIANAME",t."ARGFORMCODE",t."ARGSTRUCT",t."SYSCONFSQLPOOL_ID"
      from FG_S_SYSCONFDTCRITERIA_PIVOT t;

prompt
prompt Creating view FG_S_SYSCONFDTCRITERIA_ALL_V
prompt ==========================================
prompt
create or replace view fg_s_sysconfdtcriteria_all_v as
select t."SYSCONFDTCRITERIA_ID",t."FORM_TEMP_ID",t."SYSCONFDTCRITERIA_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SYSCONFDTCRITERIANAME",t."ARGFORMCODE",t."ARGSTRUCT",t."SYSCONFSQLPOOL_ID"
--t.* end! edit only the code below...
              from FG_S_SYSCONFDTCRITERIA_V t;

prompt
prompt Creating view FG_AUTHEN_SYSCONFDTCRITERIA_V
prompt ===========================================
prompt
create or replace view fg_authen_sysconfdtcriteria_v as
select "SYSCONFDTCRITERIA_ID","FORM_TEMP_ID","SYSCONFDTCRITERIA_OBJIDVAL","FORMID","TIMESTAMP","CLONEID","TEMPLATEFLAG","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE_ENTITY","FORMCODE","SYSCONFDTCRITERIANAME","ARGFORMCODE","ARGSTRUCT","SYSCONFSQLPOOL_ID"
              from FG_S_SYSCONFDTCRITERIA_ALL_V;

prompt
prompt Creating view FG_S_SYSCONFEXCELDATA_V
prompt =====================================
prompt
create or replace view fg_s_sysconfexceldata_v as
select to_number(t.formid) as sysconfexceldata_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysConfExcelDataName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysConfExcelData_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SYSCONFEXCELDATANAME",t."EXCELFILE",t."EXCELDATA"
      from FG_S_SYSCONFEXCELDATA_PIVOT t;

prompt
prompt Creating view FG_S_SYSCONFEXCELDATA_ALL_V
prompt =========================================
prompt
create or replace view fg_s_sysconfexceldata_all_v as
select t."SYSCONFEXCELDATA_ID",t."FORM_TEMP_ID",t."SYSCONFEXCELDATA_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SYSCONFEXCELDATANAME",t."EXCELFILE",t."EXCELDATA"
--t.* end! edit only the code below...
              from FG_S_SYSCONFEXCELDATA_V t;

prompt
prompt Creating view FG_AUTHEN_SYSCONFEXCELDATA_V
prompt ==========================================
prompt
create or replace view fg_authen_sysconfexceldata_v as
select "SYSCONFEXCELDATA_ID","FORM_TEMP_ID","SYSCONFEXCELDATA_OBJIDVAL","FORMID","TIMESTAMP","CREATION_DATE","CLONEID","TEMPLATEFLAG","CHANGE_BY","CREATED_BY","SESSIONID","ACTIVE","FORMCODE_ENTITY","FORMCODE","SYSCONFEXCELDATANAME","EXCELDATA"
              from FG_S_SYSCONFEXCELDATA_ALL_V;

prompt
prompt Creating view FG_S_SYSCONFPRESAVECALC_V
prompt =======================================
prompt
create or replace view fg_s_sysconfpresavecalc_v as
select to_number(t.formid) as sysconfpresavecalc_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysConfPreSaveCalcName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysConfPreSaveCalc_objidval,
             t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."ARG3",t."RESULTELEMENT",t."ARG4",t."FORMULAORFUNCTION",t."SYSCONFPRESAVECALCNAME",t."ARG5",t."ARGELEMENT",t."ARG2",t."ARG1"
      from FG_S_SYSCONFPRESAVECALC_PIVOT t;

prompt
prompt Creating view FG_S_SYSCONFPRESAVECALC_ALL_V
prompt ===========================================
prompt
create or replace view fg_s_sysconfpresavecalc_all_v as
select t."SYSCONFPRESAVECALC_ID",t."FORM_TEMP_ID",t."SYSCONFPRESAVECALC_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."ARG3",t."RESULTELEMENT",t."ARG4",t."FORMULAORFUNCTION",t."SYSCONFPRESAVECALCNAME",t."ARG5",t."ARGELEMENT",t."ARG2",t."ARG1"
--t.* end! edit only the code below...
              from FG_S_SYSCONFPRESAVECALC_V t;

prompt
prompt Creating view FG_AUTHEN_SYSCONFPRESAVECALC_V
prompt ============================================
prompt
create or replace view fg_authen_sysconfpresavecalc_v as
select "SYSCONFPRESAVECALC_ID","FORM_TEMP_ID","SYSCONFPRESAVECALC_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","ARG3","RESULTELEMENT","ARG4","FORMULAORFUNCTION","SYSCONFPRESAVECALCNAME","ARG5","ARGELEMENT","ARG2","ARG1"
              from FG_S_SYSCONFPRESAVECALC_ALL_V;

prompt
prompt Creating view FG_S_SYSCONFSQLCRITERIA_V
prompt =======================================
prompt
create or replace view fg_s_sysconfsqlcriteria_v as
select to_number(t.formid) as sysconfsqlcriteria_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysConfSQLCriteriaName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysConfSQLCriteria_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SQLDESCRIPTION",t."ADDITIONALMATCHINFO",t."STRUCTLEVEL",t."SQLTEXT",t."SYSCONFSQLCRITERIANAME",t."IGNORE",t."EXECUTATIONTYPE",t."SCREEN",t."ISDEFAULT"
      from FG_S_SYSCONFSQLCRITERIA_PIVOT t;

prompt
prompt Creating view FG_S_SYSCONFSQLCRITERIA_ALL_V
prompt ===========================================
prompt
create or replace view fg_s_sysconfsqlcriteria_all_v as
select t."SYSCONFSQLCRITERIA_ID",t."FORM_TEMP_ID",t."SYSCONFSQLCRITERIA_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SQLDESCRIPTION",t."ADDITIONALMATCHINFO",t."STRUCTLEVEL",t."SQLTEXT",t."SYSCONFSQLCRITERIANAME",t."IGNORE",t."EXECUTATIONTYPE",t."SCREEN",t."ISDEFAULT"
--t.* end! edit only the code below...
,t.SYSCONFSQLCRITERIANAME || '.' || t.STRUCTLEVEL || '.' || t.SCREEN AS infoName
              from FG_S_SYSCONFSQLCRITERIA_V t;

prompt
prompt Creating view FG_AUTHEN_SYSCONFSQLCRITERIA_V
prompt ============================================
prompt
create or replace view fg_authen_sysconfsqlcriteria_v as
select "SYSCONFSQLCRITERIA_ID","FORM_TEMP_ID","SYSCONFSQLCRITERIA_OBJIDVAL","FORMID","TIMESTAMP","CLONEID","TEMPLATEFLAG","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE_ENTITY","FORMCODE","SQLDESCRIPTION","SYSCONFSQLCRITERIANAME","SQLTEXT","STRUCTLEVEL","IGNORE","EXECUTATIONTYPE","SCREEN","ISDEFAULT","INFONAME"
              from FG_S_SYSCONFSQLCRITERIA_ALL_V;

prompt
prompt Creating view FG_S_SYSCONFSQLPOOL_V
prompt ===================================
prompt
create or replace view fg_s_sysconfsqlpool_v as
select to_number(t.formid) as sysconfsqlpool_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysConfSQLPoolName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysConfSQLPool_objidval,
             t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SQLTYPE",t."SQLDESCRIPTION",t."SYSCONFSQLPOOLNAME",t."SQLTEXT",t."STRUCTLEVEL",t."IGNORE",t."EXECUTATIONTYPE",t."SCREEN",t."ISDEFAULT"
      from FG_S_SYSCONFSQLPOOL_PIVOT t;

prompt
prompt Creating view FG_S_SYSCONFSQLPOOL_ALL_V
prompt =======================================
prompt
create or replace view fg_s_sysconfsqlpool_all_v as
select t."SYSCONFSQLPOOL_ID",t."FORM_TEMP_ID",t."SYSCONFSQLPOOL_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SQLTYPE",t."SQLDESCRIPTION",t."SYSCONFSQLPOOLNAME",t."SQLTEXT",t."STRUCTLEVEL",t."IGNORE",t."EXECUTATIONTYPE",t."SCREEN",t."ISDEFAULT"
--t.* end! edit only the code below...
,t.SYSCONFSQLPOOLNAME || '.' || t.STRUCTLEVEL || '.' || t.SCREEN  AS infoName
              from FG_S_SYSCONFSQLPOOL_V t;

prompt
prompt Creating view FG_AUTHEN_SYSCONFSQLPOOL_V
prompt ========================================
prompt
create or replace view fg_authen_sysconfsqlpool_v as
select "SYSCONFSQLPOOL_ID","FORM_TEMP_ID","SYSCONFSQLPOOL_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","SQLTYPE","SQLDESCRIPTION","SYSCONFSQLPOOLNAME","SQLTEXT","STRUCTLEVEL","EXECUTATIONTYPE","SCREEN","ISDEFAULT"
              from FG_S_SYSCONFSQLPOOL_ALL_V;

prompt
prompt Creating view FG_S_SYSCONFWFNEW_V
prompt =================================
prompt
create or replace view fg_s_sysconfwfnew_v as
select to_number(t.formid) as sysconfwfnew_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysConfWFNewName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysConfWFNew_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."PARAMMAPNAME",t."PARAMMAPVAL",t."SYSCONFWFNEWNAME",t."REMOVEFROMLIST",t."JSONNAME"
      from FG_S_SYSCONFWFNEW_PIVOT t;

prompt
prompt Creating view FG_S_SYSCONFWFNEW_ALL_V
prompt =====================================
prompt
create or replace view fg_s_sysconfwfnew_all_v as
select t."SYSCONFWFNEW_ID",t."FORM_TEMP_ID",t."SYSCONFWFNEW_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."PARAMMAPNAME",t."PARAMMAPVAL",t."SYSCONFWFNEWNAME",t."REMOVEFROMLIST",t."JSONNAME"
                     --t.* end! edit only the code below...
              from FG_S_SYSCONFWFNEW_V t;

prompt
prompt Creating view FG_AUTHEN_SYSCONFWFNEW_V
prompt ======================================
prompt
create or replace view fg_authen_sysconfwfnew_v as
select "SYSCONFWFNEW_ID","FORM_TEMP_ID","SYSCONFWFNEW_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","PARAMMAPNAME","PARAMMAPVAL","SYSCONFWFNEWNAME","REMOVEFROMLIST","JSONNAME"
              from FG_S_SYSCONFWFNEW_ALL_V t where rownum <= 1;

prompt
prompt Creating view FG_S_SYSCONFWFSTATUS_V
prompt ====================================
prompt
create or replace view fg_s_sysconfwfstatus_v as
select to_number(t.formid) as sysconfwfstatus_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysConfWFStatusName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysConfWFStatus_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."WHEREPARTPARMNAME",t."SYSCONFWFSTATUSNAME",t."STATUSFORMCODE",t."STATUSINFCOLUMN",t."JSONNAME"
      from FG_S_SYSCONFWFSTATUS_PIVOT t;

prompt
prompt Creating view FG_S_SYSCONFWFSTATUS_ALL_V
prompt ========================================
prompt
create or replace view fg_s_sysconfwfstatus_all_v as
select t."SYSCONFWFSTATUS_ID",t."FORM_TEMP_ID",t."SYSCONFWFSTATUS_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."WHEREPARTPARMNAME",t."SYSCONFWFSTATUSNAME",t."STATUSFORMCODE",t."STATUSINFCOLUMN",t."JSONNAME"
--t.* end! edit only the code below...
              from FG_S_SYSCONFWFSTATUS_V t;

prompt
prompt Creating view FG_AUTHEN_SYSCONFWFSTATUS_V
prompt =========================================
prompt
create or replace view fg_authen_sysconfwfstatus_v as
select "SYSCONFWFSTATUS_ID","FORM_TEMP_ID","SYSCONFWFSTATUS_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","WHEREPARTPARMNAME","SYSCONFWFSTATUSNAME","STATUSFORMCODE","STATUSINFCOLUMN","JSONNAME"
              from FG_S_SYSCONFWFSTATUS_ALL_V t where rownum <= 1;

prompt
prompt Creating view FG_S_SYSEVENTHANDLERREF_V
prompt =======================================
prompt
create or replace view fg_s_syseventhandlerref_v as
select to_number(t.formid) as syseventhandlerref_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysEventHandlerRefName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysEventHandlerRef_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."HANDLERORDERONFAIL",t."PARENTID",t."TABLETYPE",t."SYSEVENTHANDLERREFNAME",t."HANDLERORDER"
      from FG_S_SYSEVENTHANDLERREF_PIVOT t;

prompt
prompt Creating view FG_S_SYSEVENTHANDLERREF_ALL_V
prompt ===========================================
prompt
create or replace view fg_s_syseventhandlerref_all_v as
select t."SYSEVENTHANDLERREF_ID",t."FORM_TEMP_ID",t."SYSEVENTHANDLERREF_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."HANDLERORDERONFAIL",t."PARENTID",t."TABLETYPE",t."SYSEVENTHANDLERREFNAME",t."HANDLERORDER"
                     --t.* end! edit only the code below...
              from FG_S_SYSEVENTHANDLERREF_V t;

prompt
prompt Creating view FG_AUTHEN_SYSEVENTHANDLERREF_V
prompt ============================================
prompt
create or replace view fg_authen_syseventhandlerref_v as
select "SYSEVENTHANDLERREF_ID","FORM_TEMP_ID","SYSEVENTHANDLERREF_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","HANDLERORDERONFAIL","PARENTID","TABLETYPE","SYSEVENTHANDLERREFNAME","HANDLERORDER"
              from FG_S_SYSEVENTHANDLERREF_ALL_V;

prompt
prompt Creating view FG_S_SYSEVENTHANDLERSET_V
prompt =======================================
prompt
create or replace view fg_s_syseventhandlerset_v as
select to_number(t.formid) as syseventhandlerset_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysEventHandlerSetName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysEventHandlerSet_objidval,
             t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."HANDLERORDERONFAIL",t."HANDLERSETCOMMENT",t."HANDLERORDER",t."SYSEVENTPOINT_ID",t."SYSEVENTHANDLERSETNAME"
      from FG_S_SYSEVENTHANDLERSET_PIVOT t;

prompt
prompt Creating view FG_S_SYSEVENTPOINT_V
prompt ==================================
prompt
create or replace view fg_s_syseventpoint_v as
select to_number(t.formid) as syseventpoint_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysEventPointName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysEventPoint_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."ADDITIONALMATCH",t."FORMCODEMATCH",t."SYSEVENTYPENAME",t."SYSEVENTPOINTNAME"
      from FG_S_SYSEVENTPOINT_PIVOT t;

prompt
prompt Creating view FG_S_SYSEVENTPOINT_ALL_V
prompt ======================================
prompt
create or replace view fg_s_syseventpoint_all_v as
select t."SYSEVENTPOINT_ID",t."FORM_TEMP_ID",t."SYSEVENTPOINT_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."ADDITIONALMATCH",t."FORMCODEMATCH",t."SYSEVENTYPENAME",t."SYSEVENTPOINTNAME"
--t.* end! edit only the code below...
,'{"VAL":"' || t.SYSEVENTYPENAME || '.' || t.FORMCODEMATCH || '.' || t.ADDITIONALMATCH || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SYSEVENTPOINTFullName_objidval,
t.SYSEVENTYPENAME || '.' || t.FORMCODEMATCH || '.' || t.ADDITIONALMATCH AS SYSEVENTPOINTFullName
              from FG_S_SYSEVENTPOINT_V t/*,
              FG_S_SYSEVENTTYPE_ALL_V ET*/
                   --WHERE T.SYSEVENTTYPE_ID = ET.SYSEVENTTYPE_ID(+);

prompt
prompt Creating view FG_S_SYSEVENTHANDLERSET_ALL_V
prompt ===========================================
prompt
create or replace view fg_s_syseventhandlerset_all_v as
select t."SYSEVENTHANDLERSET_ID",t."FORM_TEMP_ID",t."SYSEVENTHANDLERSET_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."HANDLERORDERONFAIL",t."HANDLERSETCOMMENT",t."HANDLERORDER",t."SYSEVENTPOINT_ID",t."SYSEVENTHANDLERSETNAME"
--t.* end! edit only the code below...
,EP.SYSEVENTPOINTFullName
              from FG_S_SYSEVENTHANDLERSET_V t,
                   FG_S_SYSEVENTpoint_ALL_V EP
              WHERE T.SYSEVENTPOINT_ID = EP.SYSEVENTPOINT_ID(+);

prompt
prompt Creating view FG_AUTHEN_SYSEVENTHANDLERSET_V
prompt ============================================
prompt
create or replace view fg_authen_syseventhandlerset_v as
select "SYSEVENTHANDLERSET_ID","FORM_TEMP_ID","SYSEVENTHANDLERSET_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","HANDLERORDERONFAIL","HANDLERSETCOMMENT","HANDLERORDER","SYSEVENTPOINT_ID","SYSEVENTHANDLERSETNAME","SYSEVENTPOINTFULLNAME"
              from FG_S_SYSEVENTHANDLERSET_ALL_V;

prompt
prompt Creating view FG_S_SYSEVENTHANDLER_V
prompt ====================================
prompt
create or replace view fg_s_syseventhandler_v as
select to_number(t.formid) as syseventhandler_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysEventHandlerName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysEventHandler_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."HANDLERVALIDATION",t."CALCARG",t."SYSEVENTHANDLERNAME",t."SYSEVENTPOINTFULLNAME",t."HANDLERORDER",t."HANDLERDESCRIPTION",t."CALCFORMULA",t."HANDLERUNITTEST"
      from FG_S_SYSEVENTHANDLER_PIVOT t;

prompt
prompt Creating view FG_S_SYSEVENTHANDLER_ALL_V
prompt ========================================
prompt
create or replace view fg_s_syseventhandler_all_v as
select t."SYSEVENTHANDLER_ID",t."FORM_TEMP_ID",t."SYSEVENTHANDLER_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."HANDLERVALIDATION",t."CALCARG",t."SYSEVENTHANDLERNAME",t."SYSEVENTPOINTFULLNAME",t."HANDLERORDER",t."HANDLERDESCRIPTION",t."CALCFORMULA",t."HANDLERUNITTEST"
--t.* end! edit only the code below...
      from FG_S_SYSEVENTHANDLER_V t;

prompt
prompt Creating view FG_AUTHEN_SYSEVENTHANDLER_V
prompt =========================================
prompt
create or replace view fg_authen_syseventhandler_v as
select "SYSEVENTHANDLER_ID","FORM_TEMP_ID","SYSEVENTHANDLER_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","HANDLERVALIDATION","SYSEVENTHANDLERNAME","CALCARG","HANDLERORDER","HANDLERDESCRIPTION","HANDLERUNITTEST","CALCFORMULA"
 from FG_S_SYSEVENTHANDLER_ALL_V;

prompt
prompt Creating view FG_S_SYSEVENTHANDLETYPE_V
prompt =======================================
prompt
create or replace view fg_s_syseventhandletype_v as
select to_number(t.formid) as syseventhandletype_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysEventHandleTypeName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysEventHandleType_objidval,
             t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SYSEVENTHANDLETYPENAME",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_SYSEVENTHANDLETYPE_PIVOT t;

prompt
prompt Creating view FG_S_SYSEVENTHANDLETYPE_ALL_V
prompt ===========================================
prompt
create or replace view fg_s_syseventhandletype_all_v as
select t."SYSEVENTHANDLETYPE_ID",t."FORM_TEMP_ID",t."SYSEVENTHANDLETYPE_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SYSEVENTHANDLETYPENAME",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
              from FG_S_SYSEVENTHANDLETYPE_V t;

prompt
prompt Creating view FG_AUTHEN_SYSEVENTHANDLETYPE_V
prompt ============================================
prompt
create or replace view fg_authen_syseventhandletype_v as
select "SYSEVENTHANDLETYPE_ID","FORM_TEMP_ID","SYSEVENTHANDLETYPE_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","SYSEVENTHANDLETYPENAME"
              from FG_S_SYSEVENTHANDLETYPE_ALL_V;

prompt
prompt Creating view FG_AUTHEN_SYSEVENTPOINT_V
prompt =======================================
prompt
create or replace view fg_authen_syseventpoint_v as
select "SYSEVENTPOINT_ID","FORM_TEMP_ID","SYSEVENTPOINT_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","ADDITIONALMATCH","FORMCODEMATCH","SYSEVENTPOINTNAME"
              from FG_S_SYSEVENTPOINT_ALL_V;

prompt
prompt Creating view FG_S_SYSEVENTTYPE_V
prompt =================================
prompt
create or replace view fg_s_syseventtype_v as
select to_number(t.formid) as syseventtype_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.SysEventTypeName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as SysEventType_objidval,
             t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SYSEVENTTYPENAME",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_SYSEVENTTYPE_PIVOT t;

prompt
prompt Creating view FG_S_SYSEVENTTYPE_ALL_V
prompt =====================================
prompt
create or replace view fg_s_syseventtype_all_v as
select t."SYSEVENTTYPE_ID",t."FORM_TEMP_ID",t."SYSEVENTTYPE_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."SYSEVENTTYPENAME",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
              from FG_S_SYSEVENTTYPE_V t;

prompt
prompt Creating view FG_AUTHEN_SYSEVENTTYPE_V
prompt ======================================
prompt
create or replace view fg_authen_syseventtype_v as
select "SYSEVENTTYPE_ID","FORM_TEMP_ID","SYSEVENTTYPE_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","SYSEVENTTYPENAME"
              from FG_S_SYSEVENTTYPE_ALL_V;

prompt
prompt Creating view FG_S_SYSHCODECALC_ALL_V
prompt =====================================
prompt
create or replace view fg_s_syshcodecalc_all_v as
select t."SYSEVENTHANDLER_ID",t."FORM_TEMP_ID",t."SYSEVENTHANDLER_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."HANDLERVALIDATION",t."CALCARG",t."SYSEVENTHANDLERNAME",t."SYSEVENTPOINTFULLNAME",t."HANDLERORDER",t."HANDLERDESCRIPTION",t."CALCFORMULA",t."HANDLERUNITTEST"
--t.* end! edit only the code below...
from FG_S_SYSEVENTHANDLER_V t;

prompt
prompt Creating view FG_AUTHEN_SYSHCODECALC_V
prompt ======================================
prompt
create or replace view fg_authen_syshcodecalc_v as
select "SYSEVENTHANDLER_ID","FORM_TEMP_ID","SYSEVENTHANDLER_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","HANDLERVALIDATION","SYSEVENTHANDLERNAME","CALCARG","HANDLERORDER","HANDLERDESCRIPTION","HANDLERUNITTEST","CALCFORMULA"
              from FG_S_SYSHCODECALC_ALL_V;

prompt
prompt Creating view FG_S_SYSHSIMPLECALC_ALL_V
prompt =======================================
prompt
create or replace view fg_s_syshsimplecalc_all_v as
select t."SYSEVENTHANDLER_ID",t."FORM_TEMP_ID",t."SYSEVENTHANDLER_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."HANDLERVALIDATION",t."CALCARG",t."SYSEVENTHANDLERNAME",t."SYSEVENTPOINTFULLNAME",t."HANDLERORDER",t."HANDLERDESCRIPTION",t."CALCFORMULA",t."HANDLERUNITTEST"
--t.* end! edit only the code below...
--, EP.SYSEVENTYPENAME || '.' || EP.FORMCODEMATCH || '.' || EP.ADDITIONALMATCH AS SYSEVENTPOINTFullName
              from FG_S_SYSEVENTHANDLER_V t/*,
                   FG_S_SYSEVENTPOINT_V EP--,
                 --  FG_S_SYSEVENTTYPE_ALL_V ET
              WHERE 1=1--EP.SYSEVENTTYPENAME = ET.SYSEVENTTYPE_ID(+)
                    and t.SYSEVENTPOINT_ID = ep.syseventpoint_id(+);*/;

prompt
prompt Creating view FG_AUTHEN_SYSHSIMPLECALC_V
prompt ========================================
prompt
create or replace view fg_authen_syshsimplecalc_v as
select "SYSEVENTHANDLER_ID","FORM_TEMP_ID","SYSEVENTHANDLER_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","HANDLERVALIDATION","SYSEVENTHANDLERNAME","CALCARG","HANDLERORDER","HANDLERDESCRIPTION","HANDLERUNITTEST","CALCFORMULA"
              from FG_S_SYSHSIMPLECALC_ALL_V;

prompt
prompt Creating view FG_S_SYSHSIMPLECLAC_ALL_V
prompt =======================================
prompt
create or replace view fg_s_syshsimpleclac_all_v as
select t."SYSEVENTHANDLER_ID",t."FORM_TEMP_ID",t."SYSEVENTHANDLER_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."HANDLERVALIDATION",t."CALCARG",t."SYSEVENTHANDLERNAME",t."SYSEVENTPOINTFULLNAME",t."HANDLERORDER",t."HANDLERDESCRIPTION",t."CALCFORMULA",t."HANDLERUNITTEST"
--t.* end! edit only the code below...
       from  FG_S_SYSEVENTHANDLER_V t;

prompt
prompt Creating view FG_AUTHEN_SYSHSIMPLECLAC_V
prompt ========================================
prompt
create or replace view fg_authen_syshsimpleclac_v as
select "SYSEVENTHANDLER_ID","FORM_TEMP_ID","SYSEVENTHANDLER_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","HANDLERVALIDATION","SYSEVENTHANDLERNAME","CALCARG","HANDLERORDER","HANDLERDESCRIPTION","HANDLERUNITTEST","CALCFORMULA"
              from FG_S_SYSHSIMPLECLAC_ALL_V;

prompt
prompt Creating view FG_AUTHEN_UNITS_V
prompt ===============================
prompt
create or replace view fg_authen_units_v as
select "UNITS_ID","UNITS_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","UNITSNAME"
      from FG_S_UNITS_ALL_V t;

prompt
prompt Creating view FG_S_UNITTESTCONFIG_V
prompt ===================================
prompt
create or replace view fg_s_unittestconfig_v as
select to_number(t.formid) as unittestconfig_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.UnitTestConfigName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as UnitTestConfig_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."UNITTESTCONFIGCOMMENTS",t."GROUPNAME",t."UNITTESTCONFIGNAME",t."UNITTESTACTION",t."ORDEROFEXECUTION",t."IGNORETEST",t."WAITINGTIME",t."ENTITYIMPNAME",t."TESTINGFORMCODE",t."FIELDVALUE"
      from FG_S_UNITTESTCONFIG_PIVOT t;

prompt
prompt Creating view FG_S_UNITTESTCONFIG_ALL_V
prompt =======================================
prompt
create or replace view fg_s_unittestconfig_all_v as
select t."UNITTESTCONFIG_ID",t."FORM_TEMP_ID",t."UNITTESTCONFIG_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."UNITTESTCONFIGCOMMENTS",t."GROUPNAME",t."UNITTESTCONFIGNAME",t."UNITTESTACTION",t."ORDEROFEXECUTION",t."IGNORETEST",t."WAITINGTIME",t."ENTITYIMPNAME",t."TESTINGFORMCODE",t."FIELDVALUE"
--t.* end! edit only the code below...
              from FG_S_UNITTESTCONFIG_V t;

prompt
prompt Creating view FG_AUTHEN_UNITTESTCONFIG_V
prompt ========================================
prompt
create or replace view fg_authen_unittestconfig_v as
select "UNITTESTCONFIG_ID","FORM_TEMP_ID","UNITTESTCONFIG_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE_ENTITY","FORMCODE","UNITTESTCONFIGNAME"
              from FG_S_UNITTESTCONFIG_ALL_V;

prompt
prompt Creating view FG_S_UNITTESTGROUP_V
prompt ==================================
prompt
create or replace view fg_s_unittestgroup_v as
select to_number(t.formid) as unittestgroup_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.UnitTestGroupName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as UnitTestGroup_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."UNITTESTGROUPNAME",t."ORDEROFEXECUTION",t."IGNORE",t."UNITTESTLEVELS",t."COMMENTS",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_UNITTESTGROUP_PIVOT t;

prompt
prompt Creating view FG_S_UNITTESTGROUP_ALL_V
prompt ======================================
prompt
create or replace view fg_s_unittestgroup_all_v as
select t."UNITTESTGROUP_ID",t."FORM_TEMP_ID",t."UNITTESTGROUP_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."UNITTESTGROUPNAME",t."ORDEROFEXECUTION",t."IGNORE",t."UNITTESTLEVELS",t."COMMENTS",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
                     ,'aaa' as DUMMY
              from FG_S_UNITTESTGROUP_V t;

prompt
prompt Creating view FG_AUTHEN_UNITTESTGROUP_V
prompt =======================================
prompt
create or replace view fg_authen_unittestgroup_v as
select "UNITTESTGROUP_ID","FORM_TEMP_ID","UNITTESTGROUP_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE_ENTITY","FORMCODE","UNITTESTGROUPNAME"
              from FG_S_UNITTESTGROUP_ALL_V;

prompt
prompt Creating view FG_S_UOMTYPE_V
prompt ============================
prompt
create or replace view fg_s_uomtype_v as
select to_number(t.formid) as uomtype_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.UOMTypeName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as UOMType_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."UOMTYPENAME",t."FORMCODE_ENTITY",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_UOMTYPE_PIVOT t;

prompt
prompt Creating view FG_S_UOMTYPE_ALL_V
prompt ================================
prompt
create or replace view fg_s_uomtype_all_v as
select t."UOMTYPE_ID",t."FORM_TEMP_ID",t."UOMTYPE_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."UOMTYPENAME",t."FORMCODE_ENTITY",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
              from FG_S_UOMTYPE_V t;

prompt
prompt Creating view FG_AUTHEN_UOMTYPE_V
prompt =================================
prompt
create or replace view fg_authen_uomtype_v as
select "UOMTYPE_ID","FORM_TEMP_ID","UOMTYPE_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","UOMTYPENAME"
              from FG_S_UOMTYPE_ALL_V t where rownum <= 1;

prompt
prompt Creating view FG_S_UOM_V
prompt ========================
prompt
create or replace view fg_s_uom_v as
select to_number(t.formid) as uom_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.UOMName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as UOM_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."ISNORMAL",t."FACTOR",t."PRECISION",t."UOMNAME",t."TYPE",t."FORMCODE_ENTITY",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_UOM_PIVOT t;

prompt
prompt Creating view FG_S_UOM_ALL_V
prompt ============================
prompt
create or replace view fg_s_uom_all_v as
select t."UOM_ID",t."FORM_TEMP_ID",t."UOM_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."ISNORMAL",t."FACTOR",t."PRECISION",t."UOMNAME",t."TYPE",t."FORMCODE_ENTITY",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
, uomtype.UOMTYPENAME,t.TYPE as "UOMTYPE_ID",uomtype.UOMTYPENAME as UOM_TYPE,
first_value(t."UOM_ID") over (partition by uomtype.UOMTYPE_ID order by nvl(t.ISNORMAL,0) desc nulls last) as "UOM_NORMAL_ID"
from FG_S_UOM_V t, fg_s_uomtype_all_v uomtype
where t.TYPE = uomtype.UOMTYPE_ID;

prompt
prompt Creating view FG_AUTHEN_UOM_V
prompt =============================
prompt
create or replace view fg_authen_uom_v as
select "UOM_ID","UOM_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","UOMNAME"
      from FG_S_UOM_ALL_V t;

prompt
prompt Creating view FG_S_USERGUIDEPOOL_V
prompt ==================================
prompt
create or replace view fg_s_userguidepool_v as
select to_number(t.formid) as userguidepool_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.UserGuidePoolName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as UserGuidePool_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."USERGUIDEDESCRIPTION",t."USERGUIDEFILE",t."ITEMORDER",t."USERGUIDEPOOLNAME"
      from FG_S_USERGUIDEPOOL_PIVOT t;

prompt
prompt Creating view FG_FILES_FAST_V
prompt =============================
prompt
create or replace view fg_files_fast_v as
select "FILE_ID","FILE_NAME","CONTENT_TYPE",t.FILE_DISPLAY_ID, t.FILE_CHEM_ID
from FG_FILES t;

prompt
prompt Creating view FG_S_USERGUIDEPOOL_ALL_V
prompt ======================================
prompt
create or replace view fg_s_userguidepool_all_v as
select t."USERGUIDEPOOL_ID",t."FORM_TEMP_ID",t."USERGUIDEPOOL_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."USERGUIDEDESCRIPTION",t."USERGUIDEFILE",t."ITEMORDER",t."USERGUIDEPOOLNAME"
--t.* end! edit only the code below...
      ,f.FILE_NAME, f.CONTENT_TYPE,f.FILE_ID
              from FG_S_USERGUIDEPOOL_V t,
                   fg_files_fast_v f
where t.USERGUIDEFILE = f.FILE_ID(+);

prompt
prompt Creating view FG_AUTHEN_USERGUIDEPOOL_V
prompt =======================================
prompt
create or replace view fg_authen_userguidepool_v as
select "USERGUIDEPOOL_ID","FORM_TEMP_ID","USERGUIDEPOOL_OBJIDVAL","FORMID","TIMESTAMP","CREATION_DATE","CLONEID","TEMPLATEFLAG","CHANGE_BY","CREATED_BY","SESSIONID","ACTIVE","FORMCODE_ENTITY","FORMCODE","USERGUIDEDESCRIPTION","USERGUIDEFILE","USERGUIDEPOOLNAME"
              from FG_S_USERGUIDEPOOL_ALL_V;

prompt
prompt Creating view FG_AUTHEN_USERROLE_V
prompt ==================================
prompt
create or replace view fg_authen_userrole_v as
select "USERROLE_ID","USERROLE_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","USERROLENAME"
      from FG_S_USERROLE_ALL_V t;

prompt
prompt Creating view FG_S_USERSCREW_V
prompt ==============================
prompt
create or replace view fg_s_userscrew_v as
select to_number(t.formid) as userscrew_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.UsersCrewName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as UsersCrew_objidval,
             t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."USERSCREWNAME",t."PARENTID",t."USER_ID",t."DISABLED"
      from FG_S_USERSCREW_PIVOT t;

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
prompt Creating view FG_S_SENSITIVITYLEVEL_V
prompt =====================================
prompt
create or replace view fg_s_sensitivitylevel_v as
select to_number(t.formid) as sensitivitylevel_id,
             t.formid || decode(nvl(t.sessionId,'-1'),'-1',null, '-' || t.sessionId) || decode(nvl(t.active,1),0,'-0') as form_temp_id,
             '{"VAL":"' || t.sensitivityLevelName || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as sensitivityLevel_objidval,
             t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."CUSTOMER_ID",t."SENSITIVITYLEVELNAME",t."DESCRIPTION",t."FORMCODE_ENTITY",t."SENSITIVITYLEVELORDER",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
      from FG_S_SENSITIVITYLEVEL_PIVOT t;

prompt
prompt Creating view FG_S_SENSITIVITYLEVEL_ALL_V
prompt =========================================
prompt
create or replace view fg_s_sensitivitylevel_all_v as
select t."SENSITIVITYLEVEL_ID",t."FORM_TEMP_ID",t."SENSITIVITYLEVEL_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE",t."CUSTOMER_ID",t."SENSITIVITYLEVELNAME",t."DESCRIPTION",t."FORMCODE_ENTITY",t."SENSITIVITYLEVELORDER",t."CLONEID",t."TEMPLATEFLAG",t."CREATED_BY",t."CREATION_DATE"
--t.* end! edit only the code below...
              from FG_S_SENSITIVITYLEVEL_V t;

prompt
prompt Creating view FG_S_USER_ALL_V
prompt =============================
prompt
create or replace view fg_s_user_all_v as
select t."USER_ID",t."FORM_TEMP_ID",t."USER_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."FIRSTNAME",t."USERNAME",t."POSITION",t."CHANGEPASSWORD",t."LASTNAME",t."PERMISSIONTABLE",t."TEAMLEADER_ID",t."CUSTOMER_ID",t."DELETED",t."SENSITIVITYLEVEL_ID",t."LOCKED",t."USERROLE_ID",t."USERLDAP",t."PASSWORDDATE",t."MESSAGECHECKINTERVAL",t."LASTNOTIFICATIONCHECK",t."LABORATORY_ID",t."GROUPSCREW",t."UNIT_ID",t."CHGPASSWORDDATE",t."LAST_BREADCRUMB_LINK",t."PASSWORD",t."SITE_ID",t."LASTRETRY",t."RETRYCOUNT",t."EMAIL",t."LASTPASSWORDDATE"
--t.* end! edit only the code below...
,t1.UserRoleName,lab.SITENAME,unit.UNITSNAME,lab.LABORATORYNAME,
       '{"VAL":"' || nvl(t.FIRSTNAME,t.UserName) || ' ' || t.LASTNAME || '","ID":"' || t.formid || '", "ACTIVE":"' || nvl(t.active,1) || '"}' as UserFullName_objidval
       ,sens.SENSITIVITYLEVELNAME
       ,sens.SENSITIVITYLEVELORDER
from fg_s_user_v t,
     Fg_s_Userrole_v t1,
     FG_S_LABORATORY_ALL_V lab,
     fg_s_units_all_v unit,
     fg_s_sensitivitylevel_all_v sens
where t.userrole_id = t1.userrole_id(+)
and   t.LABORATORY_ID = lab.LABORATORY_ID(+)
and   t.UNIT_ID =  unit.UNITS_ID(+)
and   t.SENSITIVITYLEVEL_ID = sens.SENSITIVITYLEVEL_ID(+);

prompt
prompt Creating view FG_S_USERSCREW_ALL_V
prompt ==================================
prompt
create or replace view fg_s_userscrew_all_v as
select t."USERSCREW_ID",t."FORM_TEMP_ID",t."USERSCREW_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CREATION_DATE",t."CLONEID",t."TEMPLATEFLAG",t."CHANGE_BY",t."CREATED_BY",t."SESSIONID",t."ACTIVE",t."FORMCODE_ENTITY",t."FORMCODE",t."USERSCREWNAME",t."PARENTID",t."USER_ID",t."DISABLED"
--t.* end! edit only the code below...
,pt.LABORATORYNAME, pt.UNITSNAME,pt.SITENAME ,pt.POSITION, pt.USERNAME, pt.USER_ID as "USER_ID_SINGLE" --USER_ID_SINGLE used for single user id id
 ,decode(instr(',' || t.DISABLED|| ',',',' || pt.USER_ID || ','),0,0,1) as "isDisabled"
from FG_S_USERSCREW_V t ,fg_s_user_all_v pt
where 1=1 --',' || t.USER_ID || ',' like '%,' || pt.user_id || ',%'
and instr(',' || t.USER_ID || ',',',' || pt.user_id || ',') > 0;

prompt
prompt Creating view FG_AUTHEN_USERSCREW_V
prompt ===================================
prompt
create or replace view fg_authen_userscrew_v as
select t.FORM_TEMP_ID,"USERSCREW_ID","USERSCREW_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","PARENTID","USERSCREWNAME","USER_ID","DISABLED"
      from FG_S_USERSCREW_ALL_V t;

prompt
prompt Creating view FG_AUTHEN_USER_V
prompt ==============================
prompt
create or replace view fg_authen_user_v as
select t."USER_ID",t."FORM_TEMP_ID",t."USER_OBJIDVAL",t."FORMID",t."TIMESTAMP",t."CHANGE_BY",t."SESSIONID",t."ACTIVE",t."PASSWORD",t."FIRSTNAME",t."USERLDAP",t."USERNAME",t."PASSWORDDATE",t."CHGPASSWORDDATE",t."POSITION",t."LASTNAME",t."USERROLE_ID",t."LASTRETRY",t."EMAIL",t."DELETED",t."RETRYCOUNT",t."CHANGEPASSWORD",t."LOCKED",t."LASTPASSWORDDATE",t."LABORATORY_ID",t."UNIT_ID",t."SITE_ID",t."USERROLENAME",t."SITENAME",t."UNITSNAME",t."LABORATORYNAME",t."USERFULLNAME_OBJIDVAL"
      from FG_S_USER_ALL_V t;

prompt
prompt Creating view FG_FORMDATA_V
prompt ===========================
prompt
CREATE OR REPLACE VIEW FG_FORMDATA_V AS
select formcode_table -- <formcode or NA;<table name or NA>;<view source data (in case if table contains clob's type fiels) or NA (in this case the data taken from table name)>;comment
from (
      SELECT distinct f.formcode || ';' || 'FG_S_' || upper(f.formcode_entity) || '_PIVOT' || ';NA' || ';NA' as formcode_table, 100 order_
      FROM FG_FORM F
      WHERE F.FORM_TYPE = 'MAINTENANCE'
      and f.group_name in ('_System Event Handler','_System Configuration Pool','_System Configuration Report'/* ,'_System Unit Test Pool'*/) -- System Unit Test Pool is config in each ENV. - we can pass it easly by copy the relevant tests
      UNION ALL
      SELECT 'NA;FG_FORM;NA;NA', 1 order_  FROM DUAL
      UNION ALL
      --FG_FORMENTITY using pde (TABLE AS NA)
      /*SELECT 'NA;NA;NA;FG_FORMENTITY - NEED TO BE IMPORT BY PDE FILE (in comply is made automatically in SET_POST_SCRIPT_VERSION_DATA as part of this script)' formcode_table, 0 order_ FROM DUAL
      UNION ALL*/
      SELECT 'NA;FG_FORMENTITY;NA;NA' formcode_table, 10 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;FG_RESOURCE;NA;NA;', 2 order_ FROM DUAL
      /*UNION ALL
      SELECT 'NA;D_NOTIFICATION_CRITERIA;D_NOTIFICATION_CRITERIA_V;NA', 3 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;D_NOTIFICATION_MESSAGE;D_NOTIFICATION_MESSAGE_V;NA', 4 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;P_NOTIFICATION_LISTADDRESGROUP;P_NOTIFICATION_LISTADDRESGRO_V;NA', 5 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;P_NOTIFICATION_MODULE_TYPE;P_NOTIFICATION_MODULE_TYPE_V;NA', 6 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;D_NOTIFICATION_ADDRESSEE;D_NOTIFICATION_ADDRESSEE_V;NA', 7 order_ FROM DUAL
      UNION ALL
      SELECT 'NA;P_NOTIFICATION_LISTSYSTEMDATA;P_NOTIFICATION_LISTSYSTEMDAT_V;NA', 8 order_ FROM DUAL*/
      UNION ALL
      SELECT 'NA;FG_REPORT_LIST;FG_REPORT_LIST_V;NA', 9 order_ FROM DUAL
      )
order by order_;

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
prompt Creating view FG_I_FORMENTITY_V
prompt ===============================
prompt
create or replace view fg_i_formentity_v as
select "ID","FORMCODE","NUMBEROFORDER","ENTITYTYPE","ENTITYIMPCODE","ENTITYIMPCLASS","ENTITYIMPINIT","JSON_ENTITYIMPINIT" from fg_formentity_v;

prompt
prompt Creating view FG_I_MAINTENANCE_FORM_LIST_V
prompt ==========================================
prompt
create or replace view fg_i_maintenance_form_list_v as
select distinct group_ || ',' || listagg(name_, ',') within group (order by name_) OVER (partition by group_) as MAINTENANCE_LIST
from (
select decode(group_name,'General Configuration','1'||t.group_name,t.group_name) as group_--13082019 fixed bug 7592- General Configuration should be at the top of the Maintenance List
, t.formcode as name_
from FG_FORM t
where t.form_type = 'MAINTENANCE'
and   nvl(t.active,1) = 1
and   t.group_name not like '%not in use%'
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
union
select 'Main' as category_, 2 as category_order, 'Main' as sub_category, 0 as sub_category_order, '' as formCode, 'project-management' as class_, '0' as system_
from dual
union
select 'Main' as category_, 2 as category_order, 'Main Screen' as sub_category, 1 as sub_category_order, 'Main' as formCode, '' as class_, '0' as system_
from dual
union
select 'Maintenance' as category_, 5 as category_order, 'Maintenance' as sub_category, 0 as sub_category_order, 'Maintenance' as formCode, 'maintenance' as class_, '0' as system_
from dual
union
select 'System Log' as category_, 7 as category_order, 'System Log' as sub_category, 0 as sub_category_order, '' as formCode, 'systemlosg' as class_, '1' as system_
from dual
union
select 'System Log' as category_, 7 as category_order, 'System Log' as sub_category, 1 as sub_category_order, 'SystemLogReport' as formCode, '' as class_, '1' as system_
from dual
union
/*select 'System Log' as category_, 7 as category_order, 'Unit Test History Log' as sub_category, 2 as sub_category_order, 'UnitTestLogReport' as formCode, '' as class_, '1' as system_
from dual
union*/
select 'System Log' as category_, 7 as category_order, 'Audit Trail (History Report)' as sub_category, 3 as sub_category_order, 'HistoryReport' as formCode, '' as class_, '1' as system_
from dual
union
select 'System Log' as category_, 7 as category_order, 'Notification Configuration Summary' as sub_category, 4 as sub_category_order, 'NotificationSummar' as formCode, '' as class_, '1' as system_
from dual
union
select 'System Log' as category_, 7 as category_order, 'Chem Search Table' as sub_category, 5 as sub_category_order, 'ChemSearchTable' as formCode, '' as class_, '1' as system_
from dual
union
select 'System Log' as category_, 7 as category_order, 'Chem Search Doc Table' as sub_category, 6 as sub_category_order, 'ChemSearchDocRepor' as formCode, '' as class_, '1' as system_
from dual
union
select 'System Log' as category_, 7 as category_order, 'Unit Test Log' as sub_category, 7 as sub_category_order, 'UnitTestLogTable' as formCode, '' as class_, '1' as system_
from dual
union
select 'System Log' as category_, 7 as category_order, 'Scheduler Tasks' as sub_category, 8 as sub_category_order, 'SchedulerTasksRep' as formCode, '' as class_, '1' as system_
from dual
union
select 'System Log' as category_, 7 as category_order, 'Access Log Report' as sub_category, 9 as sub_category_order, 'AccessLogReport' as formCode, '' as class_, '1' as system_
from dual
union
select 'System Log' as category_, 7 as category_order, 'CAS Log Report' as sub_category, 10 as sub_category_order, 'CASResultLogReport' as formCode, '' as class_, '1' as system_
from dual
union
select 'System Log' as category_, 7 as category_order, 'Query Generator' as sub_category, 11 as sub_category_order, 'SQLGenerator' as formCode, '' as class_, '1' as system_
from dual
union
select 'System Log' as category_, 8 as category_order, 'Demo Iframe cube' as sub_category, 12 as sub_category_order, 'DemoIFMain' as formCode, '' as class_, '1' as system_
from dual
union
select 'Messages' as category_, 8 as category_order, 'Messages' as sub_category, 0 as sub_category_order, 'FGMessagesReport' as formCode, 'messages' as class_, '0' as system_
from dual
union
select 'Search' as category_, 9 as category_order, 'Search' as sub_category, 0 as sub_category_order, 'SearchReport' as formCode, 'search-report' as class_, '0' as system_
from dual
/*union
select 'Search Label' as category_, 9 as category_order, 'Search Label' as sub_category, 0 as sub_category_order, 'SearchLabel' as formCode, 'searchLabel' as class_, '0' as system_
from dual*/
)
order by category_order, sub_category_order;

prompt
prompt Creating view FG_I_UNITTESTACTION_V
prompt ===================================
prompt
create or replace view fg_i_unittestaction_v as
select 'Click' as "MainAction" from dual
union all
select 'SetSelect' from dual
union all
select 'SetInput' from dual
union all
select 'Navigate' from dual
union all
select 'Navigate by SQL' from dual
union all
select 'SQL for validation' from dual
union all
select 'Runjavascript' from dual;

prompt
prompt Creating function FG_GET_UOM_BY_UOMTYPE
prompt =======================================
prompt
create or replace function fg_get_Uom_by_uomtype (typename_in in varchar,uomname_in in varchar default 'NORMAL') return varchar2 as
 toReturn varchar2(4000);
 normalUomId_ number;
begin
  if uomname_in = 'NORMAL' then
    select max(t.UOM_NORMAL_ID) into normalUomId_
    from fg_s_uom_all_v t
    where  t.UOMTYPENAME = typename_in
    and    t.ISNORMAL = 1;
   else
     select t.UOM_ID into normalUomId_
     from fg_s_uom_all_v t
     where t.UOMTYPENAME = typename_in
     and lower(t.UOMNAME) = lower(uomname_in);
  end if;
  return normalUomId_;
exception
  when others then
    return null;
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
prompt Creating view FG_I_UOM_METADATA_V
prompt =================================
prompt
create or replace view fg_i_uom_metadata_v as
select t.entityimpcode as column_name, t.formcode,fg_get_value_from_json(json_in => t.entityimpinit,code_in => 'defaultValue') as default_value
,fg_get_uom_by_uomtype(fg_get_value_from_json(json_in => t.entityimpinit,code_in => 'uomTypeName')) normal_value
,fg_get_value_from_json(json_in => t.entityimpinit,code_in => 'uomTypeName') uom_type
from fg_formentity t
where /*t.formcode = 'MaterialRef'
and */t.entityimpclass = 'ElementUOMImp';

prompt
prompt Creating view FG_I_USERS_GROUP_SUMMARYDIST_V
prompt ============================================
prompt
create or replace view fg_i_users_group_summarydist_v as
select "PARENTID","USER_ID","USERNAME" --YP 04032020 IMPROVE PERFORMANCE
from fg_i_users_group_summarydis_mv;

prompt
prompt Creating view FG_I_USERS_GROUP_SUMMARYLIST_V
prompt ============================================
prompt
create or replace view fg_i_users_group_summarylist_v as
select "PARENTID","USER_ID" from fg_i_usersgroup_summarylist_mv;

prompt
prompt Creating view FG_I_USER_GROUP_V
prompt ===============================
prompt
create or replace view fg_i_user_group_v as
select  t.GROUP_ID_SINGLE as group_id, u.formid as user_id, u.username, t."GROUP" as groupname--LISTAGG (u.formid, ',') WITHIN GROUP (ORDER BY T.formid) OVER (PARTITION BY t.GROUP_ID_SINGLE) AS user_id_list
from FG_S_GROUPSCREW_ALL_V t,
     fg_s_user_pivot u
where t.PARENTID = u.formid
and   t.ACTIVE = 1
and t.SESSIONID is null;

prompt
prompt Creating view FG_I_USERS_GROUP_SUMMARY_V
prompt ========================================
prompt
create or replace view fg_i_users_group_summary_v as
select parentid, user_id, username
from (
        --parentid (some struct entity), the user from crew
        select t.parentid, to_char(t.USER_ID_SINGLE) as user_id, username
        from fg_s_userscrew_all_v t
        where nvl(t.active,0) = 1
        and t.sessionid is null
        and t.user_id is not null
        union all
        --parentid (some struct entity), the user from crew by group crew
        select gc.parentid, ug.user_id, ug.username
        from (
                select t.parentid, t.group_id as group_id_list_
                from fg_s_groupscrew_pivot t
                where nvl(t.active,0) = 1
                and t.sessionid is null
                and t.group_id is not null
              ) gc,
              fg_i_user_group_v ug
        where instr(',' || gc.group_id_list_ || ',', ',' || ug.group_id || ',') > 0
);

prompt
prompt Creating view FG_S_PERMISSIONSCHEME_INF_V
prompt =========================================
prompt
create or replace view fg_s_permissionscheme_inf_v as
select 'PermissionScheme' as formCode, t.formid as id, t.PermissionSchemeName as name, t.SCREEN, cu.user_id as user_crew_list,t.MAINTENANCEFORMLIST
              from FG_S_PERMISSIONSCHEME_ALL_V t,
                   fg_i_users_group_summarylist_v cu
              where t.PERMISSIONSCHEME_ID = cu.parentid(+)
              and   t.SESSIONID is null
              and   nvl(t.ACTIVE,0) = 1;

prompt
prompt Creating view FG_I_USER_MAINT_SCREEN_V
prompt ======================================
prompt
create or replace view fg_i_user_maint_screen_v as
select distinct u.formid as userid,
                --s.name as screen,
                LISTAGG(t.MAINTENANCEFORMLIST,',') WITHIN GROUP (ORDER BY t.MAINTENANCEFORMLIST) OVER (PARTITION BY u.formid ) as maintenance_screen_list
from    fg_s_permissionscheme_inf_v t,
        fg_s_user_pivot u
where   instr(',' || t.user_crew_list || ',', ',' || u.formid || ',') > 0;

prompt
prompt Creating view FG_I_USER_SCREEN_V
prompt ================================
prompt
create or replace view fg_i_user_screen_v as
select distinct u.formid as userid,
                --s.name as screen,
                LISTAGG(s.name,',') WITHIN GROUP (ORDER BY s.name) OVER (PARTITION BY u.formid ) as screen_list
               -- LISTAGG(t.MAINTENANCEFORMLIST,',') WITHIN GROUP (ORDER BY t.MAINTENANCEFORMLIST) OVER (PARTITION BY u.formid ) as maintenance_screen_list
from    fg_s_permissionscheme_inf_v t,
        fg_i_screens_v s,
        fg_s_user_pivot u
where instr(',' || t.SCREEN || ',', ',' || s.name || ',') > 0
and   instr(',' || t.user_crew_list || ',', ',' || u.formid || ',') > 0;

prompt
prompt Creating view FG_REPORT_LIST_V
prompt ==============================
prompt
CREATE OR REPLACE VIEW FG_REPORT_LIST_V AS
SELECT "ID","REPORT_CATEGORY","REPORT_SQL","REPORT_DESCRIPTION","CHANGE_BY","ACTIVE","TIMESTAMP","REPORT_USER_ID",
       "REPORT_SCOPE","REPORT_STYLE","REPORT_NAME","REPORT_SAVE_DATA","META_DATA","SYSTEM_ROW"
FROM FG_REPORT_LIST
WHERE "SYSTEM_ROW" = 1;

prompt
prompt Creating view FG_SYS_FORMENTITY_V
prompt =================================
prompt
create or replace view fg_sys_formentity_v as
select t."ID",t."FORMCODE",t."NUMBEROFORDER",t."ENTITYTYPE",t."ENTITYIMPCODE",t."ENTITYIMPCLASS",t."ENTITYIMPINIT",t."COMMENTS",t."FS",t."FS_GAP", f.form_type
from fg_formentity t,
     fg_form f
where t.formcode = f.formcode;

prompt
prompt Creating view FG_SYS_FORM_V
prompt ===========================
prompt
create or replace view fg_sys_form_v as
select "ID","FORMCODE","DESCRIPTION","ACTIVE","FORM_TYPE","TITLE","SUBTITLE","USE_AS_TEMPLATE","GROUP_NAME","NUMBEROFORDER","FORMCODE_ENTITY","IGNORE_NAV","USECACHE"
from fg_form t;

prompt
prompt Creating view FG_S_CUSTOMER_DT_V
prompt ================================
prompt
create or replace view fg_s_customer_dt_v as
select "CUSTOMER_ID" as "ID","CUSTOMER_ID","CUSTOMERNAME", "DESCRIPTION"
              from FG_S_CUSTOMER_ALL_V t;

prompt
prompt Creating view FG_S_CUSTOMER_INF_V
prompt =================================
prompt
create or replace view fg_s_customer_inf_v as
select 'Customer' as formCode, t.formid as id, t.CustomerName as name
              from FG_S_CUSTOMER_ALL_V t;

prompt
prompt Creating view FG_S_DYNAMICREPORTSQL_DT_V
prompt ========================================
prompt
create or replace view fg_s_dynamicreportsql_dt_v as
select "DYNAMICREPORTSQL_ID",
        "DYNAMICREPORTSQLNAME" as "Report SQL Name",
        T.SQLTEXT_CONTENT AS "SQL text",
        decode(nvl(t.SYSTEMREPORT,0),1,'Yes','No') as "System Report",
        decode(nvl(t.active,1),1,'Yes','No') as "Active"
from FG_S_DYNAMICREPORTSQL_ALL_V t;

prompt
prompt Creating view FG_S_DYNAMICREPORTSQL_INF_V
prompt =========================================
prompt
create or replace view fg_s_dynamicreportsql_inf_v as
select 'DynamicReportSql' as formCode, t.formid as id, t.DynamicReportSqlName as name, SQLTEXT_CONTENT
              from FG_S_DYNAMICREPORTSQL_ALL_V t;

prompt
prompt Creating view FG_S_GROUPSCREW_DT_V
prompt ==================================
prompt
create or replace view fg_s_groupscrew_dt_v as
select g."FORMID",g."PARENTID",g."FORM_TEMP_ID",g."hidden_SMARTTOOLTIP",g."Responsibility Group Name",g."Description","Users List"
from (
      select distinct formid,PARENTID,form_temp_id,
             nvl2(t.GROUPSCREW_ID, '{"objectType":"' || 'SMARTTOOLTIP' || '" ,"val":"' || 'Responsibility Group Name: ' || "GROUP" || '"}','') as "hidden_SMARTTOOLTIP",
             "GROUP" as "Responsibility Group Name",Description as "Description",
             LISTAGG (ug.username, ', ') WITHIN GROUP (ORDER BY username) OVER (PARTITION BY GROUPSCREW_ID,GROUP_ID_SINGLE,FORM_TEMP_ID) AS "Users List"
            from FG_S_GROUPSCREW_ALL_V t,
                 FG_I_USER_GROUP_V ug
      where t.GROUP_ID_SINGLE = ug.group_id(+)
     ) g
     order by "Responsibility Group Name";

prompt
prompt Creating view FG_S_GROUPSCREW_INF_V
prompt ===================================
prompt
create or replace view fg_s_groupscrew_inf_v as
select 'GroupsCrew' as formCode, t.formid as id, t.GroupsCrewName as name
      from FG_S_GROUPSCREW_ALL_V t;

prompt
prompt Creating view FG_S_GROUP_DT_V
prompt =============================
prompt
create or replace view fg_s_group_dt_v as
select distinct t."GROUP_ID",
       t.GroupName as "Responsibility Group Name", s.SITENAME as "Site",t.DESCRIPTION as "Description"
       ,LISTAGG (ug.username, ', ') WITHIN GROUP (ORDER BY ug.username) OVER (PARTITION BY t.formid) AS "Users List", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
      from FG_S_GROUP_V t,fg_s_site_all_v s,
           fg_i_user_group_v ug
      where t.SITE_ID = s.SITE_ID(+)
      and   t.FORMID = ug.group_id(+);

prompt
prompt Creating view FG_S_GROUP_INF_V
prompt ==============================
prompt
create or replace view fg_s_group_inf_v as
select 'Group' as formCode, t.formid as id, t.GroupName as name
      from FG_S_GROUP_ALL_V t;

prompt
prompt Creating view FG_S_LABORATORY_DT_V
prompt ==================================
prompt
create or replace view fg_s_laboratory_dt_v as
select "LABORATORY_ID","LABORATORYNAME" as "Laboratory Name",t.SITENAME as "Site Name", t.UNITSNAME as "Units Name", t.FORMNUMBERID AS "Lab ID", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
      from FG_S_LABORATORY_ALL_V t;

prompt
prompt Creating view FG_S_LABORATORY_INF_V
prompt ===================================
prompt
create or replace view fg_s_laboratory_inf_v as
select 'Laboratory' as formCode, t.formid as id, t.LaboratoryName as name, lpad(t.formnumberid,3,0) as formnumberid,t.LAB_MANAGER_ID,t.UNITS_ID
      from FG_S_LABORATORY_ALL_V t;

prompt
prompt Creating view FG_S_PERMISSIONOBJECT_DT_V
prompt ========================================
prompt
create or replace view fg_s_permissionobject_dt_v as
select "PERMISSIONOBJECT_ID","PERMISSIONOBJECTNAME","SITE" "Is Site Allowed","UNIT" "Is Unit Allowed","LAB" as "Is lab Allowed",
"OBJECTSINHERIT" as "Inherited Objects",
t.OBJECTSINHERITONCREATE as "Inherited On Create Objects",
decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
              from FG_S_PERMISSIONOBJECT_ALL_V t;

prompt
prompt Creating view FG_S_PERMISSIONOBJECT_INF_V
prompt =========================================
prompt
create or replace view fg_s_permissionobject_inf_v as
select 'PermissionObject' as formCode, t.formid as id, t.PermissionObjectName as name, t.SITE as isSiteAllowed, t.UNIT as isUnitAllowed,t.LAB as isLabAllowed, t.OBJECTSINHERITONCREATE, t.OBJECTSINHERIT
              from FG_S_PERMISSIONOBJECT_ALL_V t;

prompt
prompt Creating view FG_S_PERMISSIONPOLICY_DT_V
prompt ========================================
prompt
create or replace view fg_s_permissionpolicy_dt_v as
select "PERMISSIONPOLICY_ID" as  "ID","PERMISSIONPOLICY_ID","PERMISSIONPOLICYNAME"
              from FG_S_PERMISSIONPOLICY_ALL_V t;

prompt
prompt Creating view FG_S_PERMISSIONPOLICY_INF_V
prompt =========================================
prompt
create or replace view fg_s_permissionpolicy_inf_v as
select 'PermissionPolicy' as formCode, t.formid as id, t.PermissionPolicyName as name
              from FG_S_PERMISSIONPOLICY_ALL_V t;

prompt
prompt Creating view FG_S_PERMISSIONSCHEME_DT_V
prompt ========================================
prompt
create or replace view fg_s_permissionscheme_dt_v as
select distinct t."PERMISSIONSCHEME_ID",t."PERMISSIONSCHEMENAME" as "Permission Scheme Name",t."SCREEN" as "Screens",
       LISTAGG(u.USERNAME,',') WITHIN GROUP (ORDER BY u.USERNAME) OVER (PARTITION BY t.PERMISSIONSCHEME_ID ) as "User List"
       ,decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
from FG_S_PERMISSIONSCHEME_ALL_V t,
     fg_s_permissionscheme_inf_v t_inf,
     fg_s_user_all_v u
WHERE t_inf.id(+) = T.PERMISSIONSCHEME_ID
AND   instr(',' || t_inf.user_crew_list || ',', ',' || u.user_id(+) || ',') > 0;

prompt
prompt Creating view FG_S_PERMISSIONSREF_DT_V
prompt ======================================
prompt
create or replace view fg_s_permissionsref_dt_v as
select distinct t."FORMID",T.ACTIVE, t."FORM_TEMP_ID",t."PARENTID",t.permissionsref_id,
-- effects only the display in the table (set it also in the fg_s_Permissionobject_all_v view)
replace(
    replace(
        replace(
            replace(
                 regexp_replace(decode(t.PermissionObjectName,'InvItemMaterial','InvItemMaterialCm',t.PermissionObjectName),'^InvItem'),
                'RecipeFormulation',
                'Recipe')
        ,'MaterialCm','Material (Chemical)'),
    'MaterialFr','Material (Formulation)')
,'MaterialPr','Material (Premix)') as Object
,t.SITENAME as Site
,t.UNITSNAME as Unit
,t.LABORATORYNAME as Lab
,t."PERMISSION" as Permission,
decode(T.ACTIVE,0,'NO','YES') AS "Is Active"
from FG_S_PERMISSIONSREF_ALL_V t;

prompt
prompt Creating view FG_S_PERMISSIONSREF_INF_V
prompt =======================================
prompt
create or replace view fg_s_permissionsref_inf_v as
select 'PermissionSRef' as formCode, t.formid as id, ps.PERMISSIONSCHEMENAME, t.PermissionSRefName, po.PERMISSIONOBJECTNAME  as name,
       --nvl((select distinct t1.PERMISSIONOBJECTNAME from fg_s_permissionobject_all_v t1 where instr(',' || nvl(t1.OBJECTSINHERITONCREATE,'na') || ',',','|| po.PERMISSIONOBJECTNAME ||',') > 0),po.PERMISSIONOBJECTNAME) as name_on_create,
       t.SITE_ID, t.UNIT_ID, t.LAB_ID, t.PERMISSION, cu.user_id as user_crew_list,t.ACTIVE, to_char((NVL(t.ACTIVE,1) * NVL(ps.ACTIVE,1))) AS IS_ACTIVE,
       po.PERMISSIONOBJECTNAME_GROUP --holds the list of permission object include inherit rules
              from FG_S_PERMISSIONSREF_ALL_V t,
                   FG_S_PERMISSIONSCHEME_ALL_V ps,
                   fg_s_permissionobject_all_v po,
                   fg_i_users_group_summarylist_v cu
              where t.PARENTID = ps.PERMISSIONSCHEME_ID --yp 08092020 remove (+) from ps.PERMISSIONSCHEME_ID and ps.PERMISSIONSCHEMENAME is not null (add after clone schema develop)
              and   ps.PERMISSIONSCHEMENAME is not null
              and   t.PERMISSIONSREFNAME = po.PERMISSIONOBJECT_ID(+)
              and   ps.PERMISSIONSCHEME_ID = cu.parentid(+)
              and   NVL(t.ACTIVE,1) = 1
              and   NVL(ps.ACTIVE,1) = 1;

prompt
prompt Creating view FG_S_SENSITIVITYLEVEL_DT_V
prompt ========================================
prompt
create or replace view fg_s_sensitivitylevel_dt_v as
select "SENSITIVITYLEVEL_ID" as "ID",
       --"SENSITIVITYLEVEL_ID", t.CUSTOMER_ID as "Customer Id",
       "SENSITIVITYLEVELNAME" as "Name", "DESCRIPTION" as "Description", t.SENSITIVITYLEVELORDER as "Sensitivity Level Order", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
              from FG_S_SENSITIVITYLEVEL_ALL_V t;

prompt
prompt Creating view FG_S_SENSITIVITYLEVEL_INF_V
prompt =========================================
prompt
create or replace view fg_s_sensitivitylevel_inf_v as
select 'sensitivityLevel' as formCode, t.formid as id, t.sensitivityLevelName as name, t.SENSITIVITYLEVELORDER
              from FG_S_SENSITIVITYLEVEL_ALL_V t;

prompt
prompt Creating view FG_S_SITE_DT_V
prompt ============================
prompt
create or replace view fg_s_site_dt_v as
select "SITE_ID",
       t.SiteName as "Site Name", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
      from FG_S_SITE_V t;

prompt
prompt Creating view FG_S_SITE_INF_V
prompt =============================
prompt
create or replace view fg_s_site_inf_v as
select 'Site' as formCode, t.formid as id, t.SiteName as name
      from FG_S_SITE_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFDTCRITERIA_DT_V
prompt =========================================
prompt
create or replace view fg_s_sysconfdtcriteria_dt_v as
select "SYSCONFDTCRITERIA_ID","FORM_TEMP_ID","SYSCONFDTCRITERIA_OBJIDVAL","FORMID","TIMESTAMP","CLONEID","TEMPLATEFLAG","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE_ENTITY","FORMCODE","SYSCONFDTCRITERIANAME","ARGFORMCODE","ARGSTRUCT","SYSCONFSQLPOOL_ID"
              from FG_S_SYSCONFDTCRITERIA_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFDTCRITERIA_INF_V
prompt ==========================================
prompt
create or replace view fg_s_sysconfdtcriteria_inf_v as
select 'SysConfDTCriteria' as formCode, t.formid as id, t.SysConfDTCriteriaName as name
              from FG_S_SYSCONFDTCRITERIA_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFEXCELDATA_DT_V
prompt ========================================
prompt
create or replace view fg_s_sysconfexceldata_dt_v as
select "SYSCONFEXCELDATA_ID",
       "SYSCONFEXCELDATANAME" as "Excel Name",
       "EXCELDATA" as "Excel File (clob) ID",
       decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
              from FG_S_SYSCONFEXCELDATA_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFEXCELDATA_INF_V
prompt =========================================
prompt
create or replace view fg_s_sysconfexceldata_inf_v as
select 'SysConfExcelData' as formCode, t.formid as id, t.SysConfExcelDataName as name
              from FG_S_SYSCONFEXCELDATA_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFPRESAVECALC_DT_V
prompt ==========================================
prompt
create or replace view fg_s_sysconfpresavecalc_dt_v as
select "SYSCONFPRESAVECALC_ID","FORM_TEMP_ID","SYSCONFPRESAVECALC_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","ARG3","RESULTELEMENT","ARG4","FORMULAORFUNCTION","SYSCONFPRESAVECALCNAME","ARG5","ARGELEMENT","ARG2","ARG1"
              from FG_S_SYSCONFPRESAVECALC_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFPRESAVECALC_INF_V
prompt ===========================================
prompt
create or replace view fg_s_sysconfpresavecalc_inf_v as
select 'SysConfPreSaveCalc' as formCode, t.formid as id, t.SysConfPreSaveCalcName as name, t.RESULTELEMENT, t.ARG1, t.ARG2, t.ARG3, t.ARG4 , t.ARG5, t.FORMULAORFUNCTION
              from FG_S_SYSCONFPRESAVECALC_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFSQLCRITERIA_DT_V
prompt ==========================================
prompt
CREATE OR REPLACE VIEW FG_S_SYSCONFSQLCRITERIA_DT_V AS
select "SYSCONFSQLCRITERIA_ID",
       --"SQLDESCRIPTION","STRUCTLEVEL","EXECUTATIONTYPE",
       t.STRUCTLEVEL,"SCREEN", t.SQLDESCRIPTION,"SQLTEXT",
       --t.ADDITIONALMATCHINFO as "Additional Info",
       "SYSCONFSQLCRITERIANAME" as "Criteria Name","ISDEFAULT" as "Is Default",t.IGNORE, t.FORMID, T.TIMESTAMP, decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
       /*, decode(screen,'Main', to_number('.56'), 'BatchFrSelect', to_number('2.10'), 100.67)   as check_num,
       to_number('0.9') as check_num1,
       to_number(to_char_by_precision_rounded('2.10',2)) as check_num2*/
              from FG_S_SYSCONFSQLCRITERIA_ALL_V t
              where 1=1
              --AND  ("STRUCTLEVEL","SYSCONFSQLCRITERIANAME","ISDEFAULT","SQLTEXT","SCREEN") NOT IN (SELECT "STRUCTLEVEL","SYSCONFSQLCRITERIANAME","ISDEFAULT","SQLTEXT","SCREEN" FROM SKYLINE_FORM_SERVER.fg_s_sysconfsqlcriteria_PIVOT T1)
              order by /*T.TIMESTAMP,*/ STRUCTLEVEL;

prompt
prompt Creating view FG_S_SYSCONFSQLCRITERIA_INF_V
prompt ===========================================
prompt
create or replace view fg_s_sysconfsqlcriteria_inf_v as
select 'SysConfSQLCriteria' as formCode, t.formid as id, t.infoName as name, t.SysConfSQLCriteriaName, t.SQLTEXT, t.ISDEFAULT, t.IGNORE, t.ACTIVE, t.ADDITIONALMATCHINFO
              from FG_S_SYSCONFSQLCRITERIA_ALL_V t
              where t.ACTIVE = 1;

prompt
prompt Creating view FG_S_SYSCONFSQLPOOL_DT_V
prompt ======================================
prompt
create or replace view fg_s_sysconfsqlpool_dt_v as
select "SYSCONFSQLPOOL_ID",
       --"SQLDESCRIPTION","SQLTEXT","STRUCTLEVEL","EXECUTATIONTYPE","SCREEN",
       "SQLTYPE" as "SQL Type","SYSCONFSQLPOOLNAME" as "SYS CONF SQL Pool Name","ISDEFAULT" as "Is Default", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
from FG_S_SYSCONFSQLPOOL_ALL_V t
where 1=1--t.FORMCODE = 'SysConfSQLPool';

prompt
prompt Creating view FG_S_SYSCONFSQLPOOL_INF_V
prompt =======================================
prompt
create or replace view fg_s_sysconfsqlpool_inf_v as
select 'SysConfSQLPool' as formCode, t.formid as id, t.infoName as name, t.SysConfSQLPoolName, t.SQLTEXT, t.ISDEFAULT, t.IGNORE
              from FG_S_SYSCONFSQLPOOL_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFWFNEW_DT_V
prompt ====================================
prompt
create or replace view fg_s_sysconfwfnew_dt_v as
select "SYSCONFWFNEW_ID","FORM_TEMP_ID","SYSCONFWFNEW_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","PARAMMAPNAME","PARAMMAPVAL","SYSCONFWFNEWNAME","REMOVEFROMLIST","JSONNAME"
              from FG_S_SYSCONFWFNEW_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFWFNEW_INF_V
prompt =====================================
prompt
create or replace view fg_s_sysconfwfnew_inf_v as
select 'SysConfWFNew' as formCode, t.formid as id, t.SysConfWFNewName as name
              from FG_S_SYSCONFWFNEW_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFWFSTATUS_DT_V
prompt =======================================
prompt
create or replace view fg_s_sysconfwfstatus_dt_v as
select "SYSCONFWFSTATUS_ID",
       t.SysConfWFStatusName as "Form Code", t.JSONNAME as "JSON Name", upper(t.STATUSFORMCODE) as "Status Form Code",
       upper(t.STATUSINFCOLUMN) as "Status Inf Column", t.WHEREPARTPARMNAME as "Where Part Parm Name", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
from FG_S_SYSCONFWFSTATUS_ALL_V t;

prompt
prompt Creating view FG_S_SYSCONFWFSTATUS_INF_V
prompt ========================================
prompt
create or replace view fg_s_sysconfwfstatus_inf_v as
select 'SysConfWFStatus' as formCode, t.formid as id, t.SysConfWFStatusName as name, upper(t.STATUSINFCOLUMN) as STATUSINFCOLUMN, upper(t.STATUSFORMCODE) as STATUSFORMCODE, t.JSONNAME, t.WHEREPARTPARMNAME
              from FG_S_SYSCONFWFSTATUS_ALL_V t;

prompt
prompt Creating view FG_S_SYSEVENTHANDLERREF_DT_V
prompt ==========================================
prompt
create or replace view fg_s_syseventhandlerref_dt_v as
select "SYSEVENTHANDLERREF_ID","FORM_TEMP_ID","SYSEVENTHANDLERREF_OBJIDVAL","FORMID","TIMESTAMP","CHANGE_BY","SESSIONID","ACTIVE","FORMCODE","HANDLERORDERONFAIL","PARENTID","TABLETYPE","SYSEVENTHANDLERREFNAME","HANDLERORDER"
              from FG_S_SYSEVENTHANDLERREF_ALL_V t;

prompt
prompt Creating view FG_S_SYSEVENTHANDLERREF_INF_V
prompt ===========================================
prompt
create or replace view fg_s_syseventhandlerref_inf_v as
select 'SysEventHandlerRef' as formCode, t.formid as id, t.SysEventHandlerRefName as name
              from FG_S_SYSEVENTHANDLERREF_ALL_V t;

prompt
prompt Creating view FG_S_SYSEVENTHANDLERSET_DT_V
prompt ==========================================
prompt
create or replace view fg_s_syseventhandlerset_dt_v as
select t."SYSEVENTHANDLERSET_ID", t.SYSEVENTPOINT_ID, t."ACTIVE",t."HANDLERORDER", T.SYSEVENTPOINTFullName AS "FullName",t."HANDLERORDERONFAIL", t.HANDLERSETCOMMENT
              from FG_S_SYSEVENTHANDLERSET_ALL_V t;

prompt
prompt Creating view FG_S_SYSEVENTHANDLERSET_INF_V
prompt ===========================================
prompt
create or replace view fg_s_syseventhandlerset_inf_v as
select 'SysEventHandlerSet' as formCode, t.formid as id, t.SysEventHandlerSetName as name
              from FG_S_SYSEVENTHANDLERSET_ALL_V t;

prompt
prompt Creating view FG_S_SYSEVENTHANDLER_DT_V
prompt =======================================
prompt
create or replace view fg_s_syseventhandler_dt_v as
select "SYSEVENTHANDLER_ID",
       --"FORMCODE",
       "SYSEVENTHANDLERNAME" as "Sys Event Handler Name","HANDLERDESCRIPTION" as "Handler Description", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
              from FG_S_SYSEVENTHANDLER_ALL_V t
              where t.formcode_entity = 'SysEventHandler';

prompt
prompt Creating view FG_S_SYSEVENTHANDLER_INF_V
prompt ========================================
prompt
create or replace view fg_s_syseventhandler_inf_v as
select 'SysEventHandler' as formCode, t.formid as id, t.SYSEVENTPOINTFULLNAME as name, t.CALCARG, t.HANDLERORDER, t.CALCFORMULA, t.SYSEVENTHANDLERNAME
from FG_S_SYSEVENTHANDLER_ALL_V t;

prompt
prompt Creating view FG_S_SYSEVENTHANDLETYPE_DT_V
prompt ==========================================
prompt
create or replace view fg_s_syseventhandletype_dt_v as
select "SYSEVENTHANDLETYPE_ID", "SYSEVENTHANDLETYPENAME" as "Sys Event Handle Type Name", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
              from FG_S_SYSEVENTHANDLETYPE_ALL_V t;

prompt
prompt Creating view FG_S_SYSEVENTHANDLETYPE_INF_V
prompt ===========================================
prompt
create or replace view fg_s_syseventhandletype_inf_v as
select 'SysEventHandleType' as formCode, t.formid as id, t.SysEventHandleTypeName as name
              from FG_S_SYSEVENTHANDLETYPE_ALL_V t;

prompt
prompt Creating view FG_S_SYSEVENTPOINT_DT_V
prompt =====================================
prompt
create or replace view fg_s_syseventpoint_dt_v as
select "SYSEVENTPOINT_ID",
       t.SYSEVENTPOINTFullName as "Sys Event Point Full Name",
       --"ADDITIONALMATCH" as "Additional Match", t."SYSEVENTPOINTNAME" as "Sysevent Point Name",  t."SYSEVENTTYPE_ID" as "Sysevent Type Id",
       "FORMCODEMATCH" as "Formcode Match", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
              from FG_S_SYSEVENTPOINT_ALL_V t;

prompt
prompt Creating view FG_S_SYSEVENTPOINT_INF_V
prompt ======================================
prompt
create or replace view fg_s_syseventpoint_inf_v as
select 'SysEventPoint' as formCode, t.formid as id, t.SYSEVENTPOINTFullName as name, t.SysEventPointName, t.formCodeMatch --, t.SYSEVENTTYPE_ID
              from FG_S_SYSEVENTPOINT_ALL_V t;

prompt
prompt Creating view FG_S_SYSEVENTTYPE_DT_V
prompt ====================================
prompt
create or replace view fg_s_syseventtype_dt_v as
select "SYSEVENTTYPE_ID",
       --"FORMCODE",
       "SYSEVENTTYPENAME" as "Sys Event Type Name", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
              from FG_S_SYSEVENTTYPE_ALL_V t;

prompt
prompt Creating view FG_S_SYSEVENTTYPE_INF_V
prompt =====================================
prompt
create or replace view fg_s_syseventtype_inf_v as
select 'SysEventType' as formCode, t.formid as id, t.SysEventTypeName as name
              from FG_S_SYSEVENTTYPE_ALL_V t;

prompt
prompt Creating view FG_S_SYSHCODECALC_DT_V
prompt ====================================
prompt
create or replace view fg_s_syshcodecalc_dt_v as
select "SYSEVENTHANDLER_ID",
       ---t.SYSEVENTPOINTFullName, "HANDLERVALIDATION","CALCARG",
       "SYSEVENTHANDLERNAME" as "Sys Event Handler Name","HANDLERORDER" as "Handler Order",
       t.SYSEVENTPOINTFullName "Sys Event Point Name", t.HANDLERVALIDATION as "Handler Validation", "HANDLERDESCRIPTION" as "Handler Description",
       "HANDLERUNITTEST" as "Handler UnitTest","CALCFORMULA" as "Calc Formula", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active", t.FORMID
from FG_S_SYSHCODECALC_ALL_V t
              where t.SYSEVENTHANDLERNAME <> 'SysHSimpleCalc';

prompt
prompt Creating view FG_S_SYSHCODECALC_INF_V
prompt =====================================
prompt
create or replace view fg_s_syshcodecalc_inf_v as
select 'SysHCodeCalc' as formCode, t.formid as id, t.SysEventHandlerName as name
              from FG_S_SYSHCODECALC_ALL_V t;

prompt
prompt Creating view FG_S_SYSHSIMPLECALC_DT_V
prompt ======================================
prompt
create or replace view fg_s_syshsimplecalc_dt_v as
select "SYSEVENTHANDLER_ID",
       --,"SYSEVENTPOINT_ID","CALCARG","CALCFORMULA"
       "SYSEVENTHANDLERNAME" as "Sys Event Handler Name", t.SYSEVENTPOINTFullName as "Sys Event Point Full Name", "HANDLERORDER" as "Handler Order",
       "HANDLERVALIDATION" as "Handler Validation", "HANDLERDESCRIPTION" as "Handler Description", "HANDLERUNITTEST" as "Handler Unittest",
       decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
from FG_S_SYSHSIMPLECALC_ALL_V t
where t.FORMCODE = 'SysHSimpleCalc' or t.SYSEVENTHANDLERNAME = 'SysHSimpleCalc' --SysHSimpleCalc;

prompt
prompt Creating view FG_S_SYSHSIMPLECALC_INF_V
prompt =======================================
prompt
create or replace view fg_s_syshsimplecalc_inf_v as
select 'SysHSimpleCalc' as formCode, t.formid as id, t.SysEventHandlerName, t.SYSEVENTPOINTFullName as name, t.CALCARG, t.HANDLERORDER, t.CALCFORMULA
              from FG_S_SYSHSIMPLECALC_ALL_V t;

prompt
prompt Creating view FG_S_SYSHSIMPLECLAC_DT_V
prompt ======================================
prompt
create or replace view fg_s_syshsimpleclac_dt_v as
select T.SYSEVENTHANDLER_ID,"ACTIVE","FORMCODE","SYSEVENTHANDLERNAME","HANDLERDESCRIPTION"
              from FG_S_SYSHSIMPLECLAC_ALL_V t;

prompt
prompt Creating view FG_S_SYSHSIMPLECLAC_INF_V
prompt =======================================
prompt
create or replace view fg_s_syshsimpleclac_inf_v as
select 'SysHSimpleClac' as formCode, t.formid as id, t.SysEventHandlerName as name
              from FG_S_SYSHSIMPLECLAC_ALL_V t;

prompt
prompt Creating view FG_S_UNITS_DT_V
prompt =============================
prompt
create or replace view fg_s_units_dt_v as
select "UNITS_ID",t.SITENAME AS "Site Name",
        "UNITSNAME"  AS "Unit Name",decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
      from FG_S_UNITS_ALL_V t;

prompt
prompt Creating view FG_S_UNITS_INF_V
prompt ==============================
prompt
create or replace view fg_s_units_inf_v as
select 'Units' as formCode, t.formid as id, t.UnitsName as name
      from FG_S_UNITS_ALL_V t;

prompt
prompt Creating view FG_S_UNITTESTCONFIG_DT_V
prompt ======================================
prompt
create or replace view fg_s_unittestconfig_dt_v as
select "UNITTESTCONFIG_ID",
       t.UNITTESTCONFIGNAME as "Test Name",
       t.GROUPNAME as "Group Name",
       g.UNITTESTLEVELS as "Test Level",
       --decode(g.IGNORE,0,'NO','YES') as "Group Is Ignore",
       t.TESTINGFORMCODE as "Test form code",
       g.ORDEROFEXECUTION as "Group Order",
       t."ORDEROFEXECUTION" as "Order",
       t."UNITTESTACTION" as "Action",
       t.FIELDVALUE as "Value",
       t."IGNORETEST",g.IGNORE,
       decode(NVL(t."IGNORETEST",0) + NVL(g.IGNORE,0),'1','No','Yes') as "Active"
from FG_S_UNITTESTCONFIG_ALL_V t,
     FG_S_UNITTESTGROUP_V g
where    t.GROUPNAME = g.UnitTestGroupName
order by to_number(g.ORDEROFEXECUTION), to_number(t.ORDEROFEXECUTION);

prompt
prompt Creating view FG_S_UNITTESTCONFIG_INF_V
prompt =======================================
prompt
create or replace view fg_s_unittestconfig_inf_v as
select 'UnitTestConfig' as formCode, t.formid as id, t.UNITTESTCONFIGNAME as name
              from FG_S_UNITTESTCONFIG_ALL_V t;

prompt
prompt Creating view FG_S_UNITTESTDATA_V
prompt =================================
prompt
create or replace view fg_s_unittestdata_v as
select t.unittestaction, t.ignoretest, t.waitingtime, t.entityimpname, t.fieldvalue, g.unittestlevels, g.unittestgroupname, t.formid,
       t.unittestaction || ', ' || t.ignoretest || ', ' || t.waitingtime || ', ' || t.entityimpname || ', ' || t.fieldvalue as ALLFIELDS
from FG_S_UNITTESTCONFIG_PIVOT t, FG_S_UNITTESTGROUP_PIVOT g
where  1=1
       and t.orderofexecution is not null
       and t.groupname = g.unittestgroupname
--       and t.unittestgroup_id = '25201'
       and t.ignoretest = '0'
       and g.ignore = '0'
order by g.orderofexecution, t.orderofexecution;

prompt
prompt Creating view FG_S_UNITTESTGROUP_DT_V
prompt =====================================
prompt
create or replace view fg_s_unittestgroup_dt_v as
select "UNITTESTGROUP_ID","UNITTESTGROUPNAME" as "Group name", "ORDEROFEXECUTION" as "Order", "UNITTESTLEVELS" as "Levels", decode(NVL("IGNORE",0),1,'No','Yes') as "Active"
from FG_S_UNITTESTGROUP_ALL_V t
order by t.UNITTESTGROUPNAME, t.IGNORE;

prompt
prompt Creating view FG_S_UNITTESTGROUP_INF_V
prompt ======================================
prompt
create or replace view fg_s_unittestgroup_inf_v as
select 'UnitTestGroup' as formCode, t.formid as id, t.UnitTestGroupName as name
              from FG_S_UNITTESTGROUP_ALL_V t;

prompt
prompt Creating view FG_S_UOMTYPE_DT_V
prompt ===============================
prompt
create or replace view fg_s_uomtype_dt_v as
select "UOMTYPE_ID","UOMTYPENAME" as "UOM Type", decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
              from FG_S_UOMTYPE_ALL_V t;

prompt
prompt Creating view FG_S_UOMTYPE_INF_V
prompt ================================
prompt
create or replace view fg_s_uomtype_inf_v as
select 'UOMType' as formCode, t.formid as id, t.UOMTypeName as name
              from FG_S_UOMTYPE_ALL_V t;

prompt
prompt Creating view FG_S_UOM_DT_V
prompt ===========================
prompt
create or replace view fg_s_uom_dt_v as
select "UOM_ID","UOMNAME" as "UOM Name", t.UOMTYPENAME as "UOM Type name", decode(nvl(t.ISNORMAL,0),1,'Yes','No') as "Is Normal", t.FACTOR as "Factor" , decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
      from FG_S_UOM_ALL_V t;

prompt
prompt Creating view FG_S_UOM_INF_V
prompt ============================
prompt
create or replace view fg_s_uom_inf_v as
select 'UOM' as formCode,
        t.formid as id,
        t.UOMName as name,
        t.ISNORMAL,
        t.FACTOR,
        t.PRECISION,
        t.UOMTYPENAME,
        t.UOMTYPE_ID
from FG_S_UOM_ALL_V t;

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
prompt Creating view FG_S_USERGUIDEPOOL_DT_V
prompt =====================================
prompt
create or replace view fg_s_userguidepool_dt_v as
select "USERGUIDEPOOL_ID", t.USERGUIDEPOOLNAME as "Name", t.USERGUIDEDESCRIPTION as "Description",
       t.ITEMORDER as "Item Order_SMARTNUM",
       t.FILE_NAME as "File Name", t.CONTENT_TYPE as "File Type",decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
from FG_S_USERGUIDEPOOL_ALL_V t
order by decode(instr(lower(t.CONTENT_TYPE),'video'),0,'DOC_UG','VIDEO_UG'), fg_get_numeric(t.itemOrder);

prompt
prompt Creating view FG_S_USERGUIDEPOOL_INF_V
prompt ======================================
prompt
create or replace view fg_s_userguidepool_inf_v as
select 'UserGuidePool' as formCode, t.formid as id, t.UserGuidePoolName as name,
       t.FILE_NAME, t.CONTENT_TYPE, t.FILE_ID, t.ITEMORDER,
       decode(instr(lower(t.CONTENT_TYPE),'video'),0,'DOC_UG','VIDEO_UG') AS CONTENT_CODE_UG,
       T.USERGUIDEDESCRIPTION,
       t.ACTIVE as IS_ACTIVE
              from FG_S_USERGUIDEPOOL_ALL_V t;

prompt
prompt Creating view FG_S_USERROLE_DT_V
prompt ================================
prompt
create or replace view fg_s_userrole_dt_v as
select "USERROLE_ID","USERROLENAME",decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active"
      from FG_S_USERROLE_V t;

prompt
prompt Creating view FG_S_USERROLE_INF_V
prompt =================================
prompt
create or replace view fg_s_userrole_inf_v as
select 'UserRole' as formCode, t.formid as id, t.UserRoleName as name
      from FG_S_USERROLE_ALL_V t;

prompt
prompt Creating view FG_S_USERSCREW_DT_V
prompt =================================
prompt
create or replace view fg_s_userscrew_dt_v as
select DISTINCT FORMID,PARENTID,t.form_temp_id,USERNAME as "User Name",t.POSITION as "Position",t.UNITSNAME as "Units",t.LABORATORYNAME as "Laboratory",t.sitename as "Site"
      from fg_s_userscrew_all_v t
      order by upper(USERNAME);

prompt
prompt Creating view FG_S_USERSCREW_INF_V
prompt ==================================
prompt
create or replace view fg_s_userscrew_inf_v as
select 'UsersCrew' as formCode, t.formid as id, t.UsersCrewName as name, t.PARENTID, t.USER_ID, t.USER_ID_SINGLE
      from FG_S_USERSCREW_ALL_V t
      where t.SESSIONID is null
      and   nvl(t.ACTIVE,0) = 1;

prompt
prompt Creating view FG_S_USER_DTFULLN_V
prompt =================================
prompt
create or replace view fg_s_user_dtfulln_v as
select
       t.user_id, NVL2(t.FIRSTNAME,t.FIRSTNAME ||' '|| t.LASTNAME ,T.UserName||' '|| t.LASTNAME) as "Full Name",
       NVL(t.FIRSTNAME,T.UserName) as "First Name",
       t.LASTNAME  as "Last Name",
       t.POSITION  as "Position",
       s.SITENAME as "Site",
       t.UNITSNAME as "Units",
       t.LABORATORYNAME as "Lab"
from FG_S_USER_ALL_V t,fg_s_site_all_v s
where t.SITE_ID = s.SITE_ID(+);

prompt
prompt Creating view FG_S_USER_DT_V
prompt ============================
prompt
create or replace view fg_s_user_dt_v as
select distinct
       --1 as "IS_CHECKBOX_DISABLED",
       t.user_id,
       T.UserName as "User Name",
       NVL(t.FIRSTNAME,T.UserName) ||' '|| t.LASTNAME  as "Full Name",
       --decode(nvl(t.ACTIVE,1),1,'Yes','No') as "Active",
       t.POSITION  as "Position",
       s.SITENAME as "Site",
       t.UNITSNAME as "Unit",
       t.LABORATORYNAME as "Laboratory",
       LISTAGG (ug.groupname, ', ') WITHIN GROUP (ORDER BY groupname) OVER (PARTITION BY t.USER_ID) AS "Group List",
       decode(decode(nvl(t.deleted,0),0,nvl(t.active,1),0),1,'Yes','No') as "Active"
from FG_S_USER_ALL_V t,fg_s_site_all_v s,
     fg_i_user_group_v ug
where t.SITE_ID = s.SITE_ID(+)
and   lower(t.USERNAME) not in ('system'/*,'admin'*/)
and   t.USER_ID = ug.user_id(+);

prompt
prompt Creating view FG_S_USER_INF_V
prompt =============================
prompt
create or replace view fg_s_user_inf_v as
select 'User' as formCode, t.formId as id, t.UserName as name, t.SITE_ID, t.UNIT_ID, t.LABORATORY_ID, t.LABORATORYNAME,t.USER_ID,
       t.SiteName,  t.USERROLE_ID, t.UserRoleName, t.FirstName, t.LastName, t.Email, t.UnitsName, u.customer_id, t.TEAMLEADER_ID, t.SENSITIVITYLEVEL_ID,t.SENSITIVITYLEVELNAME,t.SENSITIVITYLEVELORDER
      from FG_S_USER_ALL_V t,
           FG_S_USER_V u
WHERE u.user_id = t.user_id;

prompt
prompt Creating view FG_TOOL_INF_V_SUMMARY
prompt ===================================
prompt
CREATE OR REPLACE VIEW FG_TOOL_INF_V_SUMMARY AS
SELECT distinct T.TABLE_NAME, decode(count(t.COLUMN_NAME) over (partition by T.TABLE_NAME),2,1,0) as isIdName,
       ' select name, id, ''' || T.TABLE_NAME || ''' as tablename from ' || T.TABLE_NAME || ' union all ' as sql_
FROM User_Tab_Cols T
WHERE T.TABLE_NAME LIKE 'FG_S_%_INF_V'
and   lower(t.COLUMN_NAME) in ('name','id');

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
prompt Creating package FORM_TOOL
prompt ==========================
prompt
create or replace package form_tool is
  function addFormLabel(formCode_in varchar, bookmarkPrefix_in varchar, noEntityimpcodeList_in varchar) RETURN NUMBER;
  procedure shiftElementsBookmarks(formCode_in varchar, bookmarkPrefix_in varchar, noEntityimpcodeList_in varchar, addNumber number);
  procedure setAllStructTables;
  procedure cleanAllData;
  procedure printTablesRowCount(show_greater_than number);
  procedure removeFormEntityIntProp(formCode varchar, initProp varchar);
  procedure favoriteSqls;
  procedure completeData;
  procedure initFormlastsavevalueHst;
  function updateFormlastsavevalueFromHst (formId_in varchar) RETURN NUMBER;
  PROCEDURE updateServerMaintenanceData;
  --
  FUNCTION FG_SET_ALL_STRUCT_ALL_V return number;
  function FG_output_STRUCT_ALL_V return varchar;
  function FG_output_system_struct_V return varchar;
  function fg_output_materialized_view_v (db_name_in varchar) return varchar;
  function getNextBookMark (str_in varchar) return varchar;
  PROCEDURE setLabelElementByNextBookMark;
  function deleteFormData (formCode_in varchar, deleteFormDef_in number default 0) RETURN NUMBER;
  procedure cleanInvalidData (formCodeIn varchar);
  --procedure updateServerDB (formCode_in varchar);
  --procedure copyFormDataToServer(formCodeEntityIn VARCHAR);
  --procedure setServerMaintenanaceDataId;
 function removeFromIdFromDB(formId_in varchar, formCodeEntity_in varchar, ts_in varchar) return number;
  procedure unpivotFromUnitTestConf;
  procedure removeDTRemoveButtons;
  procedure tool_check_data(db_name_in varchar);
 --procedure deleteAllDataByUserName(userNameIn varchar);
/* procedure updateUnittestDB (formCode_in varchar);
 procedure setUnitTestMaintenanaceDataId;
 procedure copyFormDataToUnittest (formCodeEntityIn VARCHAR);*/
end;
/

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
prompt Creating function FG_SET_STRUCT_PIVOT_TABLE
prompt ===========================================
prompt
CREATE OR REPLACE FUNCTION FG_SET_STRUCT_PIVOT_TABLE (formCode_in varchar2, dropAndCreateTable_in number default 1, formid_in varchar2 default null) return number as

    is_pivot_exists number;

    is_pivot_bu_exists number;

    col_list_pivot_statment varchar2(32767);

    --TYPE ref_cursor IS REF CURSOR;

    --cur REF_CURSOR;

    --cVal_ varchar(100);

    sql_pivot varchar2(32767);

    sql_create_BASIC_v varchar2(32767);

    sql_create_v varchar2(32767);

    isViewExists number;

    CREATE_VIEW_FLAG number := 0;
    CREATE_VIEW_FORMCODE_CSV_LIST varchar2(32767) := '';

    result number;

    formCodeEntity varchar2(100);

    formTypeParam varchar2(100);

    isDevelop number;

    formidToScript varchar2(100);

    systemuser_ varchar2(100);

    /*l_ varchar2(32767);
    lsql_ varchar2(32767);*/

    duplicationCheck number;

    --rollbackFlag number := 0;

begin
    delete from fg_debug;
    insert into fg_debug(comment_info,comments) values('start create table for formcode =' || formCode_in ,''); --commit;

    select nvl(f.formcode_entity,f.formcode),f.form_type into formCodeEntity,formTypeParam from fg_form f where f.formcode = formCode_in;

    select t.userrole_id into systemuser_ from fg_s_user_pivot t where t.username = 'system';

    select count(*) into is_pivot_exists
    from user_tables t
    where upper(t.TABLE_NAME) = upper('FG_S_' || upper(formCodeEntity) || '_PIVOT');

    select nvl(t.is_develop,0) into isDevelop from fg_sys_param t;
    if /*override_is_develop_flag = 1 or*/ isDevelop <> 1 then
      insert into fg_debug(comments) values('not develop');
      raise_application_error( -20001, 'Error in FG_SET_STRUCT_PIVOT_TABLE! try to re-create table when fg_sys_param.isDevelop is not in develop mode');
      return 0;
    end if;

    select COUNT(*) into duplicationCheck from (
    select distinct  t.id, t.entityimpcode, t.formcode,
    count(*) over (partition by upper(t.entityimpcode)) c1, count(*) over (partition by t.entityimpcode) c2
    from fg_formentity t
    where t.formcode in (select f.formcode from fg_form f where UPPER(f.formcode_entity) = upper(formCodeEntity))
    and t.entitytype = 'Element'
    )
    where c1 <> c2;

    if duplicationCheck > 0 then
      insert into fg_debug(comments) values('there is duplication the in column names');
      raise_application_error( -20001, 'Error in FG_SET_STRUCT_PIVOT_TABLE! there is duplication in the column names on the same formcode_entity!');
      return 0;
    end if;

    if is_pivot_exists = 1 and dropAndCreateTable_in = 1  then
    -------------------------------------
    -- updates the pivot table to get the additional columns that has been added to the form
    -------------------------------------
    for col in(
        select distinct upper(t.entityimpcode) as col_name
            from FG_FORMENTITY t
            where t.entitytype = 'Element'
            --remove not needed entityimpclass
            and t.entityimpclass not in ('ElementLabelImp','ElementIreportImp','ElementSmartSearchImp')--decode(formTypeParam,'General','NA','Report','NA','ElementDataTableApiImp'))
            -- incluse element also from other forms which "mapped" to this from (using the same formcode_entity)
            and t.formcode = formCode_in
            minus
            select upper(tc.COLUMN_NAME) from user_tab_columns tc where tc.TABLE_NAME = 'FG_S_' || upper(formCodeEntity) || '_PIVOT'
         )
         loop
           if col_list_pivot_statment is null then
             col_list_pivot_statment:=col.col_name ||' VARCHAR2(' || fg_get_col_size(formCodeEntity, col.col_name)||')';
             else
              col_list_pivot_statment:=col_list_pivot_statment||','||col.col_name ||' VARCHAR2(' || fg_get_col_size(formCodeEntity, col.col_name)||')';
           end if;
         end loop;
         if col_list_pivot_statment is not null then
           insert into fg_debug(comment_info,comments) values('add columns to table FG_S_' || formCodeEntity||'_PIVOT'
           ,'alter table FG_S_' || upper(formCodeEntity) || '_PIVOT add ('|| col_list_pivot_statment||')'); --commit;
           execute immediate 'alter table FG_S_' || upper(formCodeEntity) || '_PIVOT'
           || ' add ('|| col_list_pivot_statment||')';
           commit;
         end if;
        /*for cVal in (
          select distinct * from
            (select distinct upper(t.entityimpcode) as col_name
            from FG_FORMENTITY t
            where t.entitytype = 'Element'
            --remove not needed entityimpclass
            and t.entityimpclass not in ('ElementLabelImp','ElementIreportImp','ElementSmartSearchImp')--decode(formTypeParam,'General','NA','Report','NA','ElementDataTableApiImp'))
            -- incluse element also from other forms which "mapped" to this from (using the same formcode_entity)
            and t.formcode = formCode_in
            union all
            select upper(tc.COLUMN_NAME) from user_tab_columns tc where tc.TABLE_NAME = 'FG_S_' || upper(formCodeEntity) || '_PIVOT'
            )
          )
        LOOP
          ----dbms_output.put_line(  cVal.val_ );
          if col_list_pivot_statment is null then
            col_list_pivot_statment := cVal.col_name || ' as ''' ||  cVal.col_name || '''';
          else
            col_list_pivot_statment := col_list_pivot_statment || ',' || cVal.col_name || ' as ''' ||  cVal.col_name || '''';
          end if;
        END LOOP;*/
    end if;

    col_list_pivot_statment := null;

    -------------------------------------
    --arrange last save data with production <-> develop correction
    -------------------------------------
    for cVal in (
                    select distinct t.entityimpcode as val_
                    from FG_FORMENTITY t
                    where t.entitytype = 'Element'
                    --remove not needed entityimpclass
                    and t.entityimpclass not in ('ElementLabelImp','ElementIreportImp','ElementSmartSearchImp')--decode(formTypeParam,'General','NA','Report','NA','ElementDataTableApiImp'))
                    -- incluse element also from other forms which "mapped" to this from (using the same formcode_entity)
                    and  (t.formcode in (select f1.formcode from fg_form f1 where f1.formcode_entity = formCodeEntity)
                         OR t.formcode = formCode_in)
                    -- remove element with ADDITIONALDATA form builder definition
                    and not exists (select 1 from FG_FORMENTITY t2 WHERE t.id = t2.id and upper(T2.ENTITYIMPINIT) LIKE '%"ADDITIONALDATA":TRUE%')
                    and (
                          is_pivot_exists = 0
                          or
                          1 = (select decode(count(*),0,0,1) from user_tab_columns tc where tc.TABLE_NAME = 'FG_S_' || upper(formCodeEntity) || '_PIVOT' and tc.COLUMN_NAME = upper(t.entityimpcode))
                        )
                  )
    LOOP
      ----dbms_output.put_line(  cVal.val_ );
      if col_list_pivot_statment is null then
        col_list_pivot_statment := cVal.val_ || ' as ''' ||  cVal.val_ || '''';
      else
        col_list_pivot_statment := col_list_pivot_statment || ',' || cVal.val_ || ' as ''' ||  cVal.val_ || '''';
      end if;

     -- insert into fg_debug(comments) values (cVal.val_ || ' as ''' ||  cVal.val_ || '''');
      --commit;
    END LOOP;

    delete from FG_FORMLASTSAVEVALUE_UNPIVOT t where t.formcode_entity = formCodeEntity and t.formid <> nvl(formid_in,'-1');

    --commit;

        if is_pivot_exists = 1 then

      execute immediate '
      update FG_FORMLASTSAVEVALUE_UNPIVOT t set (t.cloneid,t.templateflag) = (
      select t1.cloneid,t1.templateflag from fg_s_' || formCodeEntity || '_pivot t1 where t1.formid = ''' || formid_in || ''' )
      where t.formid = ''' || formid_in || '''';
      commit;

      if formid_in is null then
        formidToScript := '-1';
      else
        formidToScript := formid_in;
      end if;

      sql_pivot := '
      insert into FG_FORMLASTSAVEVALUE_UNPIVOT
                  (id,
                  formid,
                  formcode_entity,
                  entityimpcode,
                  entityimpvalue,
                  userid,
                  sessionid,
                  active,
                  --formidscript,
                  formcode_name,
                  created_by,
                  creation_date,
                  timestamp,
                  change_by,
                  CLONEID,
                  TEMPLATEFLAG )
      select  null,
              formid,
              ''' || formCodeEntity || ''',
              entityimpcode,
              entityimpvalue,
              nvl(change_by,''' || systemuser_ || ''') as userid,
              sessionid,
              active,
              --formidscript,
              formcode,
              created_by,
              creation_date,
              timestamp,
              change_by,
              CLONEID,
              TEMPLATEFLAG
      from FG_S_' || upper(formCodeEntity) || '_PIVOT unpivot include nulls
       (entityimpvalue for(entityimpcode) in
         (
                 ' ||       col_list_pivot_statment || '
         )
        ) where active >= 0 and formid <> ' || formidToScript || ' ';
        insert into fg_debug(comments) values (sql_pivot); commit;
        execute immediate sql_pivot;
    end if;

    --clean col_list_pivot_statment
    col_list_pivot_statment := '';

    --commit;

    -------------------------------------
    -- done arrange last save
    -------------------------------------


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
                    and t.entityimpclass not in ('ElementLabelImp','ElementIreportImp','ElementSmartSearchImp')--decode(formTypeParam,'General','NA','Report','NA','ElementDataTableApiImp'))
                    -- incluse element also from other forms which "mapped" to this from (using the same formcode_entity)
                    and  (t.formcode in (select f1.formcode from fg_form f1 where f1.formcode_entity = formCodeEntity)
                         OR t.formcode = formCode_in)
                    -- remove element with ADDITIONALDATA form builder definition
                    and not exists (select 1 from FG_FORMENTITY t2 WHERE t.id = t2.id and upper(T2.ENTITYIMPINIT) LIKE '%"ADDITIONALDATA":TRUE%')
                  )
    LOOP
      ----dbms_output.put_line(  cVal.val_ );
      col_list_pivot_statment := col_list_pivot_statment || ',''' || cVal.val_ || ''' as ' ||  cVal.val_;
    END LOOP;


    sql_pivot := '
    SELECT * FROM
    (select "FORMID" as formId,
           first_value(t.TIMESTAMP) over (partition by "FORMID" order by t.id desc) as "TIMESTAMP",
           first_value(t.CREATION_DATE) over (partition by "FORMID" order by t.id desc)  as CREATION_DATE,
           CLONEID,
           TEMPLATEFLAG,
           CAST(first_value(CHANGE_BY) over (partition by "FORMID" order by t.id desc) AS varchar2(100) ) as "CHANGE_BY",
           CAST(first_value(CREATED_BY) over (partition by "FORMID" order by t.id desc) AS varchar2(100) ) as "CREATED_BY",
           CAST(null AS varchar2(100) ) as "SESSIONID",
           first_value("ACTIVE") over (partition by "FORMID" order by t.id desc) AS "ACTIVE",
           CAST(first_value(FORMCODE_ENTITY) over (partition by "FORMID" order by t.id desc) AS varchar2(100) ) as FORMCODE_ENTITY,
           CAST(first_value(t.formcode_name) over (partition by "FORMID" order by t.id desc) AS varchar2(100) ) as "FORMCODE",
           "ENTITYIMPCODE" as col_,
           "ENTITYIMPVALUE" as val_
    from FG_FORMLASTSAVEVALUE_UNPIVOT t
    where t.FORMCODE_ENTITY = ''' || formCodeEntity || ''')
    PIVOT (max(val_) FOR col_ IN (' || substr(col_list_pivot_statment,2) || '))';

    --dbms_output.put_line('sql_pivot=' || sql_pivot);

      --  if col_list_pivot is not null then
      if is_pivot_exists = 1 and dropAndCreateTable_in = 1  then
          EXECUTE IMMEDIATE ' CREATE table ' || 'FG_S_' || upper(formCodeEntity) || '_BU AS SELECT * FROM FG_S_' || upper(formCodeEntity) || '_PIVOT';
          EXECUTE IMMEDIATE 'drop table ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT';
          --rollbackFlag := 1;
      end if;

      if dropAndCreateTable_in = 1 then
        insert into fg_debug(comment_info,comments) values(' CREATE table ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT' || ' AS ...', sql_pivot);--commit;
        EXECUTE IMMEDIATE ' CREATE table ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT' || ' AS ' || sql_pivot;
        --rollbackFlag := 0;

        select count(*) into is_pivot_bu_exists
        from user_tables t
        where upper(t.TABLE_NAME) = upper('FG_S_' || upper(formCodeEntity) || '_BU');

        if is_pivot_bu_exists = 1 then
          EXECUTE IMMEDIATE 'drop table ' || 'FG_S_' || upper(formCodeEntity) || '_BU';
        end if;

      end if;

      --commit;

      select count(*) into is_pivot_exists
      from user_tables t
      where upper(t.TABLE_NAME) = upper('FG_S_' || upper(formCodeEntity) || '_PIVOT');

      if is_pivot_exists = 0 then
        return 0; -- not exists
      end if;

      fg_set_col_size(formCodeEntity, 'FG_S_' || upper(formCodeEntity) || '_PIVOT');


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
              --dbms_output.put_line(result);
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
              from FG_S_' || r.formCode_uppercase || '_ALL_V ';
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
/*EXCEPTION
    when others then
      if rollbackFlag = 1 then
        EXECUTE IMMEDIATE ' CREATE table ' || 'FG_S_' || upper(formCodeEntity) || '_PIVOT AS SELECT * FROM FG_S_' || upper(formCodeEntity) || '_BU';
        EXECUTE IMMEDIATE ' DROP TABLE ' || 'FG_S_' || upper(formCodeEntity) || '_BU';
        raise_application_error( -20001, 'Error in FG_SET_STRUCT_PIVOT_TABLE ROLL BACK DATA WAS MADE');
        return 0;
      end if;*/
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
prompt Creating package body FORM_TOOL
prompt ===============================
prompt
create or replace package body form_tool is

   function addFormLabel(formCode_in varchar, bookmarkPrefix_in varchar, noEntityimpcodeList_in varchar) return number as
   begin
          --bu
          EXECUTE IMMEDIATE ' CREATE table FG_FORMENTITY_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from FG_FORMENTITY ';

          --insert lable before elements: 'ElementAutoCompleteIdValDDLImp','ElementInputImp','ElementAutoCompleteDDLImp' with label text as source element on one baook mark left
          insert into FG_FORMENTITY (FORMCODE,NUMBEROFORDER,ENTITYTYPE,ENTITYIMPCODE,ENTITYIMPCLASS,ENTITYIMPINIT)
          select * from
          (

              select formCode_in, --FORMCODE
                     0, --NUMBEROFORDER
                     'Element', --ENTITYTYPE
                     'l' || t.entityimpcode,--ENTITYIMPCODE
                     'ElementLabelImp',--ElementLabelImp
                     -- the expression
                     case
                       when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9][0-9]') > 0 then --4 DIGIT
                          '{"text":"' || nvl(fg_get_value_from_json(t.entityimpinit,'label',null),initcap(t.entityimpcode)) || '","elementName":"' || t.entityimpcode || '","layoutBookMarkItem":"' || bookmarkPrefix_in  || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),4)) -1) || '","keepValueOnParentChange":false,"preventSave":false,"hideAlways":false,"disableAlways":false,"mandatory":false,"invIncludeInGrig":false,"invIncludeInFilter":false}'--ENTITYIMPINIT
                       when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9]') > 0 then -- 3 DIGIT
                          '{"text":"' || nvl(fg_get_value_from_json(t.entityimpinit,'label',null),initcap(t.entityimpcode)) || '","elementName":"' || t.entityimpcode || '","layoutBookMarkItem":"' || bookmarkPrefix_in  || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),3)) -1) || '","keepValueOnParentChange":false,"preventSave":false,"hideAlways":false,"disableAlways":false,"mandatory":false,"invIncludeInGrig":false,"invIncludeInFilter":false}'--ENTITYIMPINIT
                       when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9]') > 0 then --2 DIGIT
                          '{"text":"' || nvl(fg_get_value_from_json(t.entityimpinit,'label',null),initcap(t.entityimpcode)) || '","elementName":"' || t.entityimpcode || '","layoutBookMarkItem":"' || bookmarkPrefix_in  || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),2)) -1) || '","keepValueOnParentChange":false,"preventSave":false,"hideAlways":false,"disableAlways":false,"mandatory":false,"invIncludeInGrig":false,"invIncludeInFilter":false}'--ENTITYIMPINIT
                       when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9]') > 0 then --1 DIGIT
                            '{"text":"' || nvl(fg_get_value_from_json(t.entityimpinit,'label',null),initcap(t.entityimpcode)) || '","elementName":"' || t.entityimpcode || '","layoutBookMarkItem":"' || bookmarkPrefix_in  || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),1)) -1) || '","keepValueOnParentChange":false,"preventSave":false,"hideAlways":false,"disableAlways":false,"mandatory":false,"invIncludeInGrig":false,"invIncludeInFilter":false}'--ENTITYIMPINIT

                     end init_json
              from FG_FORMENTITY t
              where t.formcode = formCode_in
              and   t.entityimpclass in ('ElementAutoCompleteIdValDDLImp','ElementInputImp','ElementAutoCompleteDDLImp','ElementTextareaImp','ElementRichTextEditorImp')
              and   (t.formcode,'l' || t.entityimpcode) not in (select FORMCODE,ENTITYIMPCODE from FG_FORMENTITY)
              and   instr(',' || noEntityimpcodeList_in || ',', ',' || t.entityimpcode || ',') = 0 --not in list
          )
          where init_json is not null;
          commit;
          return 1;
   end;

   procedure shiftElementsBookmarks(formCode_in varchar, bookmarkPrefix_in varchar, noEntityimpcodeList_in varchar, addNumber number) as
   begin
          --bu
          EXECUTE IMMEDIATE ' CREATE table FG_FORMENTITY_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from FG_FORMENTITY ';

          --shift
          update FG_FORMENTITY t
          set t.ENTITYIMPINIT = case
                               when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9][0-9]') > 0 then --4 DIGIT
                                 REGEXP_REPLACE(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9][0-9]',bookmarkPrefix_in || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),4)) + addNumber))
                               when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9]') > 0 then -- 3 DIGIT
                                 REGEXP_REPLACE(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9][0-9]',bookmarkPrefix_in || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),3)) + addNumber))
                               when REGEXP_INSTR(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9]') > 0 then --2 DIGIT
                                 REGEXP_REPLACE(t.entityimpinit,bookmarkPrefix_in || '[0-9][0-9]',bookmarkPrefix_in || (to_number(substr(t.entityimpinit, instr(t.entityimpinit,bookmarkPrefix_in) + length(bookmarkPrefix_in),2)) + addNumber))
                               end
          where t.formcode = formCode_in
          and   t.entitytype = 'Element'
          and   t.entityimpinit like '%' || bookmarkPrefix_in || '_%'
          and   instr(',' || noEntityimpcodeList_in || ',', ',' || t.entityimpcode || ',') = 0; --not in list

          commit;
   end;

   procedure setAllStructTables as
     result number;
   begin
          for r in (
            /*select distinct t.formcode
            from FG_FORM t
            where t.form_type in ('STRUCT','ATTACHMENT','STRUCT')*/
            select distinct t.formcode_entity
            from FG_FORM t where t.formcode <> t.formcode_entity
            and t.formcode_entity in (select distinct  t1.formcode from FG_FORM t1)
          )
          loop
              result := FG_SET_STRUCT_PIVOT_TABLE(r.formcode_entity, 1);
          end loop;

       /*     begin
         \* for r in (
            select distinct t.formcode
            from FG_FORM t
            where t.form_type in ('STRUCT','ATTACHMENT')
            and t.formcode in (select t1.formcode from fg_formlastsavevalue t1)
          )
          loop
            execute immediate ' delete from  FG_S_' || upper(r.formcode) || '_PIVOT ';
            execute immediate ' delete from  fg_formlastsavevalue where formcode = ''' || r.formcode || ''' ';

          end loop;*\

           for r in (
            select distinct t.formcode
            from FG_FORM t
            where t.form_type in ('GENERAL')
            and t.formcode in (select t1.formcode from fg_formlastsavevalue t1)
          )
          loop
            execute immediate ' delete from  fg_formlastsavevalue where formcode = ''' || r.formcode || ''' ';

          end loop;

          commit;
   end;*/

          commit;
   end;

   procedure cleanAllData as
      dummy number;
   begin
          /*
          CREATE TABLE FG_FORMLASTSAVEVALUE AS
          select * from FG_FORMLASTSAVEVALUE_13072017 t
          */

         /* DELETE from FG_FORMLASTSAVEVALUE t where UPPER(t.FORMCODE_ENTITY) NOT in (SELECT UPPER(T1.FORMCODE) FROM FG_FORM T1 WHERE T1.FORM_TYPE = 'MAINTENANCE');

          UPDATE FG_FORMLASTSAVEVALUE T SET T.FORMIDSCRIPT = T.FORMID;

          update FG_FORMLASTSAVEVALUE t set t.formid = ( select ranki
                                                          from (
                                                                 select t1.*, dense_rank() over (order by formid) + 1000 as ranki from FG_FORMLASTSAVEVALUE t1
                                                                ) s
                                                          where s.id = t.id
                                                         );

          UPDATE FG_FORMLASTSAVEVALUE T SET T.ENTITYIMPVALUE = (SELECT DISTINCT T1.FORMID FROM FG_FORMLASTSAVEVALUE T1 WHERE T1.FORMIDSCRIPT = T.ENTITYIMPVALUE)  WHERE T.FORMCODE_ENTITY LIKE '%_ID' OR T.ENTITYIMPCODE = 'formId' OR (T.ENTITYIMPCODE = 'type' and t.FORMCODE_ENTITY = 'UOM');
          */

          for r in (
           select t.TABLE_NAME
            from user_tables t
            where 1=1
            AND (t.TABLE_NAME like 'FG_S_%_PIVOT'
            OR    T.TABLE_NAME IN ('FG_S_FORMULANTREF_ALL_V_PLAN',
                                  'CREATE_SERIES_INDX_DATA_SS',
                                  'FG_ACCESS_LOG',
                                  'FG_ACTIVITY_LOG',
                                  'FG_AUTHEN_REPORT_V',
                                  'FG_CHEM_DOODLE_DATA',
                                  'FG_CLOB_FILES',
                                  'FG_DEBUG',
                                  'FG_FILES',
                                  'FG_FORMID_UNPIVOT_LIST_TMP',
                                  'FG_FORMTEST_DATA',
                                  'FG_R_DEMO_TEST_DATA',
                                  'FG_R_EXPERIMENT_CHEMDRAW_V_PLN',
                                  'FG_RICHTEXT',
                                  'FG_R_SYSTEM_VIEW',
                                  'FG_TOOL_INF_ALL_DATA',
                                  'FG_RESULTS',
                                  'FG_FORMADDITIONALDATA',
                                  'FG_FORMADDITIONALDATA_HST',
                                  'FG_FORMMONITOPARAM_DATA',
                                  'FG_DYNAMICPARAMS'))
            AND   UPPER(REPLACE(REPLACE(T.TABLE_NAME,'FG_S_'),'_PIVOT')) not IN (SELECT UPPER(F.FORMCODE_ENTITY) FROM FG_FORM F WHERE F.FORM_TYPE = 'MAINTENANCE' or f.formcode = 'PermissionSRef')
          )
          loop
               truncate_it(r.TABLE_NAME);
          end loop;

          delete from fg_sequence t where upper(t.formcode) not  in ( SELECT distinct UPPER(F.Formcode) FROM FG_FORM F WHERE F.FORM_TYPE = 'MAINTENANCE'
                                                                      union
                                                                      SELECT distinct UPPER(F.Formcode_Entity) FROM FG_FORM F WHERE F.FORM_TYPE = 'MAINTENANCE');

          delete from fg_formlastsavevalue t where t.formid not in (select id from fg_sequence);

           truncate_it('FG_FORMLASTSAVEVALUE_HST');
           truncate_it('FG_FORMLASTSAVEVALUE_INF');

         /* for r in (
            SELECT  T1.FORMCODE FROM FG_FORM T1 WHERE T1.FORM_TYPE = 'MAINTENANCE'
          )
          loop
              dummy := FG_SET_STRUCT_PIVOT_TABLE(r.formcode);
          end loop;

          delete from FG_SEQUENCE t;

          completeData;

          initFormlastsavevalueHst;*\*/

          commit;

          --update FG_SEQUENCE_system_SEQ to max + 1 in FG_SEQUENCE
          --update FG_SEQUENCE_SEQ to 100000


         /* delete from fg_form t
          where lower(t.formcode) like '%eyal%' or lower(t.formcode) like '%yaron%' or lower(t.formcode) like '%alex%' and lower(t.formcode) not like 'protocoltypeeyal';

          delete from fg_formentity t
          where t.formcode not in (select formcode from fg_form);

          --unlinked values
          delete from fg_formlastsavevalue t
          where t.formcode not in (select formcode from fg_form);

          --developers data (developers will eneter <form>Name value
          delete from fg_formlastsavevalue t
          where t.formid in
          (
            select formid
            from fg_formlastsavevalue t
            where 1=1
            and   t.formcode <> 'User'
            and   lower(t.entityimpcode) = lower(t.formcode) || 'name'
            and   (
                     lower(t.entityimpvalue) like '%eyal%'
                     or lower(t.entityimpvalue) like '%yaron%'
                     or t.entityimpvalue in ('Ptest1')
                  )
          );

          --unlinked doc
          delete from fg_formlastsavevalue t
          where t.formid in (
            select t.formid
            from fg_s_document_all_v t
            where t.PARENTID not in (
            select formid
            from fg_formlastsavevalue t
            )
          );

          --unlinked file
          delete from fg_files t
          where t.file_id not in (
            select t.formid
            from fg_formlastsavevalue t
          );

          commit;

          --MORE CLEAN SELECT...
          \*
          SELECT * FROM FG_RESOURCE T WHERE UPPER(T.CODE) NOT IN (
          select UPPER(T1.VIEW_NAME) from FG_R_SYSTEM_VIEW t1) AND UPPER(T.CODE) NOT IN  (
          SELECT T1.TABLE_NAME FROM USER_TABLES T1
          ) --AND UPPER(T.CODE) NOT IN ( SELECT FG_FORMENTITY.ENTITYIMPINIT FROM FG_FORMENTITY)
          ;

          select *
          from FG_RESOURCE t,
          FG_FORMENTITY F
          WHERE INSTR(UPPER(F.ENTITYIMPINIT),UPPER(T.CODE))  > 0
          AND T.TYPE IS NULL ;
          *\*/
   end;

   procedure printTablesRowCount (show_greater_than number) as

      cursor c_tables_list is
      select TABLE_NAME from user_tables order by TABLE_NAME;

      r_tables_list c_tables_list%rowtype;

      flagCounter number := 0;

      tmpMinusQuery varchar(4000) := '';

   begin

      for r_tables_list in c_tables_list
      loop
        tmpMinusQuery := ' select
                              (select count(*) from ' || r_tables_list.TABLE_NAME || ')
                          from dual ';
        execute immediate tmpMinusQuery  into flagCounter;

        if flagCounter > nvl(show_greater_than,0) then
           dbms_output.put_line( '--' || r_tables_list.TABLE_NAME || ' - ' || NVL(flagCounter,0) || ' rows.' );
           dbms_output.put_line( 'delete from ' || r_tables_list.TABLE_NAME || ';' );
       end if;

      end loop;

   end;

   procedure removeFormEntityIntProp(formCode varchar, initProp varchar) as
   begin
     dbms_output.put_line('dummy' || formCode || initProp); --run update sql as below
     --update FG_FORMENTITY t set t.entityimpinit = replace(t.entityimpinit,'"invIncludeInGrig":false,"invIncludeInFilter":false','') where t.entityimpinit like '%,"invIncludeInGrig":false,"invIncludeInFilter":false%'
   end;

   procedure favoriteSqls as
   begin

     dbms_output.put_line('dummy');

     --session process info
     /* SELECT  S.USERNAME, S.MACHINE, s.LOGON_TIME, s.LOCKWAIT
      FROM V$SESSION_CONNECT_INFO SC , V$SESSION S--, v$process p
      WHERE S.SID = SC.SID
      --and S.MACHINE = 'COMPLYPC161'
      --and s.paddr = p.addr
     */
     --number of main objects per project - month
    /*select  distinct t1.PROJECT_ID, T1.PROJECTNAME, t1.ProjectTypeName, t1.CREATION_DATE AS PROJECT_CREATION_DATE, u.UserName as PROJECT_CREATED_BY,
            t1.formcodeentitylabel entity_name, t1.entity_month,
            count(distinct t1.id) over (partition by t1.PROJECT_ID,lower(t1.formcodeentitylabel), t1.entity_month) as num_entities_project_month
    from (
    select distinct to_char(t.insertdate,'yyyy-MM') as entity_month, t.id, p.PROJECT_ID, p.PROJECTNAME, m.formcodeentitylabel, p.ProjectTypeName, p.CREATION_DATE , p.CREATED_BY
    from FG_SEQUENCE t,
         fg_s_project_all_v p,
         FG_FORMELEMENTINFOATMETA_MV m
    where t.search_match_id1 = p.project_id
    and t.formcode = m.formcode
    --AND   EXISTS (SELECT 1  FROM FG_S_EXPERIMENT_V E where e.experiment_id = t.id and e.TEMPLATEFLAG is null)
    and exists (select 1 from fg_formlastsavevalue_inf f where f.formid = t.id AND F.ACTIVE = 1)
    and m.formcodeentitylabel in ('SubProject','SubSubProject','Experiment','Step','Action','Self-Test','Workup')
    ) t1, fg_s_user_v u where u.user_id = t1.CREATED_BY
    ORDER BY T1.PROJECT_ID, t1.formcodeentitylabel, T1.entity_month;*/

    --number of main objects per project
    /*select  distinct t1.PROJECT_ID, T1.PROJECTNAME, t1.ProjectTypeName, t1.CREATION_DATE, u.UserName as createdby,
            t1.formcodeentitylabel entity_name,
            count(distinct t1.id) over (partition by t1.PROJECT_ID,lower(t1.formcodeentitylabel)) as num_entities_in_project
    from (
    select distinct t.id, p.PROJECT_ID, p.PROJECTNAME, m.formcodeentitylabel, p.ProjectTypeName, p.CREATION_DATE , p.CREATED_BY
    from FG_SEQUENCE t,
         fg_s_project_all_v p,
         FG_FORMELEMENTINFOATMETA_MV m
    where t.search_match_id1 = p.project_id
    and t.formcode = m.formcode
    --AND   EXISTS (SELECT 1  FROM FG_S_EXPERIMENT_V E where e.experiment_id = t.id and e.TEMPLATEFLAG is null)
    and exists (select 1 from fg_formlastsavevalue_inf f where f.formid = t.id AND F.ACTIVE = 1)
    and m.formcodeentitylabel in ('SubProject','SubSubProject','Experiment','Step','Action','Self-Test','Workup')
    ) t1, fg_s_user_v u where u.user_id = t1.CREATED_BY
    ORDER BY T1.PROJECT_ID, t1.formcodeentitylabel;*/

     --user entity files
     /*select distinct t.FORMID,t.PATH_ID, trunc(s.insertdate) as "Entity Creation Date",  trunc(t.CHANGE_DATE), s.formcode, t.formcode_entity,
                      m.formcodeentitylabel as Entity,  unew.UserName as UserName,
                      p.ProjectName, up.UserName as "Project creator",
                      round(DBMS_LOB.getlength(f.file_content)/1024) AS "FILE SIZE [KB]"
      from fg_formlastsavevalue_inf_v t
           ,fg_files f
           ,fg_s_user_v uchange
           ,fg_s_user_v unew
           ,fg_s_user_v up
           ,fg_sequence s
           ,FG_FORMELEMENTINFOATMETA_MV m
           ,fg_s_project_v p
      where 1=1--to_number(t.formid) >= 317101
      and t.ACTIVE = 1
      and t.IS_FILE = 1
      and t.userid = unew.user_id
      and t.CHANGE_BY = uchange.user_id
      and t.ENTITYIMPVALUE = f.file_id
      and t.PATH_ID = s.id
      and p.project_id(+) = s.search_match_id1
      and p.CREATED_BY = up.user_id(+)
      and upper(m.formcode) = upper(s.formcode)
      order by to_number(t.FORMID);*/

    --user entity projects
    /*select distinct t.FORMID,t.PATH_ID, trunc(s.insertdate) as "Entity Creation Date", trunc(t.CHANGE_DATE) as "Change Date", t.formcode_entity AS Entity,
                    p.ProjectName, up.UserName as "Project creator",
                    --m.formcodeentitylabel as Entity,
                    unew.UserName as "Created By", unew.active as "Is Active", uchange.UserName as "Changed By", uchange.active as "Is Active"
    from fg_formlastsavevalue_inf_v t
         ,fg_s_user_v uchange
         ,fg_s_user_v unew
         ,fg_s_user_v up
         ,fg_sequence s
         ,fg_s_project_v p
         --,FG_FORMELEMENTINFOATMETA_MV m
    where  1=1 --to_number(t.formid) >= 317101
    and t.ACTIVE = 1
    and t.PATH_ID is null
    and t.userid = unew.user_id
    and t.CHANGE_BY = uchange.user_id
    and t.FORMID = s.id
    and p.project_id(+) = s.search_match_id1
    and p.CREATED_BY = up.user_id(+);*/

     /* -- csv to table (listtotable/csvtotable)
    SELECT to_number(regexp_substr('1,77,3', '[^,]+', 1, commas.column_value)) as id_
    FROM table(cast(multiset
                      (SELECT LEVEL
                       FROM dual CONNECT BY LEVEL <= LENGTH (regexp_replace('1,77,3', '[^,]+')) + 1) AS sys.OdciNumberList)) commas

    */
     /*
     -- move init dif between forms
     update fg_formentity t
     set t.entityimpinit = (select t1.entityimpinit from fg_formentity t1 where t1.formcode = 'YYY' and t1.entityimpcode = 'action')
     where t.formcode = 'XXXX'
     and t.entityimpcode = 'action'
     */

     /*
     --performance
     select t.timestamp as "Time Stamp_SMARTTIME",
             T.FORMID,
             T.USER_ID,
             t.comments,
             fg_get_numeric(nvl(fg_get_value_from_json(t."ADDITIONALINFO",'SQL_TIME','-1'),fg_get_value_from_json(t."ADDITIONALINFO",'EXE_TIME','-1'))) as "Runtime_NUM" from FG_ACTIVITY_LOG t
      where t.activitylogtype = 'PerformanceSQL'
      and   t.timestamp > sysdate - 1
      and   UPPER(t.comments) not like '%REFRESH%'
      AND   UPPER(T.COMMENTS) NOT LIKE '%FG_FORMLASTSAVEVALUE_INF%'
      AND   UPPER(T.COMMENTS) NOT LIKE '%FG_SET_INF_INIT_DATA_ALL%'
      AND   UPPER(T.COMMENTS) NOT LIKE '%FG_N_%'
      AND   UPPER(T.COMMENTS) NOT LIKE '%FG_R_%'
      AND   UPPER(T.COMMENTS) NOT LIKE '%_DT%' --?
      AND   UPPER(T.COMMENTS) NOT LIKE '%DB_CLEANUP%'
      AND   T.COMMENTS  NOT LIKE '%insert into fg_form_change_list (formcode)%' --?
      AND   T.COMMENTS  NOT LIKE '%FG_SET_SERACH_HANDEL_INF_ID%' --?
      and    T.COMMENTS  NOT LIKE'select DISTINCT f.formcode from user_tab_columns t, FG_FORM F where %'
 */

     /*
     -- FILES TABLE SIZE
      select round(sum(DBMS_LOB.GETLENGTH(t.file_content))/1024/1024,3) || ' MB' as blob_size from FG_FILES_SRC t;
     */

     /*
     --MV refresh time duration
     SELECT-1 as dummy,  mview_name, last_refresh_date, fullrefreshtim, increfreshtim
     FROM user_mview_analysis
*/
     /*
     --delete experiment CP run data
     --workup
    delete from fg_s_workup_pivot where action_id in (
    select t1.formid from fg_s_action_pivot t1 where t1.step_id in (
    select formid from fg_s_step_pivot t where t.runnumber is not null)
    );

    --self test
    delete from fg_s_selftest_pivot where action_id in (
    select t1.formid from fg_s_action_pivot t1 where t1.step_id in (
    select formid from fg_s_step_pivot t where t.runnumber is not null)
    );

    --action
    delete from fg_s_action_pivot t1 where t1.step_id in (
    select formid from fg_s_step_pivot t where t.runnumber is not null);

    --material ref
    delete from fg_s_materialref_pivot t1 where t1.parentid in (
    select formid from fg_s_step_pivot t where t.runnumber is not null);

    --step
    delete from fg_s_step_pivot t where t.runnumber is not null;

    --EXPRUNPLANNING
    delete from FG_S_EXPRUNPLANNING_PIVOT;
    */

     /*
     -- SMART COLUMN
      SELECT * FROM (
      SELECT DISTINCT SUBSTR(T.COLUMN_NAME,INSTR(T.COLUMN_NAME, '_', -1)) AS SMART_NAME
      ,T.TABLE_NAME,T.COLUMN_NAME
      FROM USER_TAB_COLUMNS T WHERE T.COLUMN_NAME LIKE '%_SMART%');
*/
     /*
     --SEQUENCE validation
     select max(t.id) from FG_SEQUENCE_FILES t; --less than -> FG_SEQUENCE_FILES_SEQ
     select max(t.id) from FG_SEQUENCE t; --less than -> FG_SEQUENCE_SEQ
     select max(to_number(t.result_id)) from fg_results t;--less than -> FG_RESULTS_SEQ
*/
     /*
     -- open connection
     select * from (
        select distinct count(*) over () c_all, count(*) over (partition by t.OSUSER) c_user ,t.OSUSER --,t.*
        from sys.V_$SESSION t
      ) order by c_user

     */
     -- clone with minus  active value struct tables
     /*DECLARE
      C_ NUMBER;
      begin
       FOR r in  (

                select distinct t.TABLE_NAME
                from user_tables t, USER_TAB_COLUMNS C
                where t.TABLE_NAME like 'FG_S_%_PIVOT'
                AND T.TABLE_NAME = C.TABLE_NAME
                AND C.COLUMN_NAME = 'ACTIVE')
       LOOP
         EXECUTE IMMEDIATE ' SELECT COUNT(*) FROM ' || R.TABLE_NAME || ' WHERE NVL(ACTIVE,0) < 0' into C_;
         IF C_ > 0 THEN
           DBMS_OUTPUT.put_line('TABLE ' || R.TABLE_NAME || ', COUNT MINUS ACTIVE = ' || C_);
         END IF;
       END LOOP;
      END;*/

     ---------------------------- update maintenance form to close on save if aftersave is missing
     /*
      UPDATE fg_formentity FE SET FE.ENTITYIMPINIT = REPLACE(FE.ENTITYIMPINIT,',"templateType"', ',"afterSave":"Close","templateType"' )
      WHERE FE.ID IN (
      select ID from fg_formentity t
      where t.formcode in (select formcode from fg_form f where f.form_type = 'MAINTENANCE')
      AND ENTITYTYPE = 'Layout'
      AND T.ENTITYIMPINIT NOT LIKE '%afterSave%'
      AND T.ENTITYIMPINIT LIKE '%,"templateType"%')
*/
     --------------------------- correct template flag
     /*
      --experiment
      update fg_s_experiment_pivot t set t.templateflag = 1  where t.formid in (
      select t1.sourceexpno_id from fg_s_template_pivot t1
      )
      and nvl(t.templateflag,0) =  0;

      --step
      update fg_s_step_pivot t set t.templateflag = 1 where t.experiment_id in (
             select t1.formid from fg_s_experiment_pivot t1 where nvl(t1.templateflag,0) = 1
      )
      and nvl(t.templateflag,0) =  0;

      --action
      update fg_s_action_pivot t set t.templateflag = 1 where t.experiment_id in (
             select t1.formid from fg_s_experiment_pivot t1 where nvl(t1.templateflag,0) = 1
      )
      and nvl(t.templateflag,0) =  0;

      - --experiment
      update fg_s_experiment_pivot t set t.templateflag = 1  where t.formid in (
      select t1.sourceexpno_id from fg_s_template_pivot t1
      )
      and nvl(t.templateflag,0) =  0;

      --step
      update fg_s_step_pivot t set t.templateflag = 1 where t.experiment_id in (
             select t1.formid from fg_s_experiment_pivot t1 where nvl(t1.templateflag,0) = 1
      )
      and nvl(t.templateflag,0) =  0;

      --workup
      update fg_s_workup_pivot t set t.templateflag = 1 where t.experiment_id in (
             select t1.formid from fg_s_workup_pivot t1 where nvl(t1.templateflag,0) = 1
      )
      and nvl(t.templateflag,0) =  0;


      --selftest
      update fg_s_selftest_pivot t set t.templateflag = 1 where t.experiment_id in (
             select t1.formid from fg_s_selftest_pivot t1 where nvl(t1.templateflag,0) = 1
      )
      and nvl(t.templateflag,0) =  0;
*/
     /* --------- ************** check layout entity bug(1) (on same form with dif config)
     --create table yaron_check_jsp as
      select --formcode_entity,
             --formcode,
             distinct
             entityimpcode,
             count(htmlName || ',' || afterSave || ',' ||templateName  || ',' || templateType) over (partition by entityimpcode )c1,
             count(distinct htmlName || ',' || afterSave || ',' ||templateName  || ',' || templateType)over (partition by entityimpcode )c2
             from (
            -- htmlName,afterSave,templateName,templateType from (
      select * from (
      select t.entityimpcode, f.formcode_entity, f.formcode, fg_get_value_from_json(t.entityimpinit,'htmlName','na') as htmlName,
      fg_get_value_from_json(t.entityimpinit,'afterSave','na')as afterSave,
      fg_get_value_from_json(t.entityimpinit,'templateName','na') as templateName,
      fg_get_value_from_json(t.entityimpinit,'templateType','na') as templateType,
      t.entityimpinit , count(rownum) over(partition by t.entityimpcode) as counter_ from FG_FORMENTITY t,fg_form f where t.entitytype = 'Layout' and t.formcode = f.formcode )
      where counter_ > 1);

      --------- **************check layout entity bug(2) (on same form with dif config)
      select * from fg_formentity t where t.entityimpcode in (
      select t1.entityimpcode from YARON_CHECK_JSP t1  where t1.c2 > 1 );
*/


/*    yp - not worked as expected !
     create or replace view fg_i_formentity_label_v as
select formcode, entityimpcode, element_code,
       replace(replace(replace(initcap(replace(replace(nvl(nvl(label_element, label_config),label_impcode_parse),'_Id',''),':','')),'_',''),'type',' Type'),'  ',' ') as element_label
from (
    select t.formcode, t.entityimpcode,
           t.formcode || '.' || t.entityimpcode as element_code,
           nullif(fg_get_value_from_json(t.init_l,'text','na'),'na') label_element,
           nullif(fg_get_value_from_json(t.init_e ,'label','na'),'na') label_config,
           DECODE(t.entityimpcode,upper(t.entityimpcode),initcap(replace(lower(t.entityimpcode),'_id','')),fn_tool_camel_to_label(replace(t.entityimpcode,'_Id',''))) label_impcode_parse
    from (
    select e.formcode, e.entityimpcode, el.entityimpinit as init_l, e.entityimpinit as init_e
    from FG_FORMENTITY el,
         FG_FORMENTITY e
    where 1=1--t.formcode = formCode_in
    and   el.entityimpclass(+) = 'ElementLabelImp'
    and   fg_get_value_from_json(el.entityimpinit(+) ,'elementName','na') = e.entityimpcode
    and   el.formcode(+) = e.formcode
    and   UPPER(fg_get_value_from_json(e.entityimpinit ,'hideAlways','na')) <> 'TRUE'
    and   e.entityimpclass not in ('ElementLabelImp',\*'ElementUOMImp'*\'ElementAuthorizationImp','ElementAsyncIframeImp')
    and   e.entitytype = 'Element') t )
where 1=1*/

     --for automatic tests - check if there is missing data in the manual results
     /*select distinct count(*)
    from FG_I_CONNECTION_REQSMPLDEXP_V smplReq,
    fG_S_SAMPLESELECT_ALL_V smpl,
    fg_s_component_all_v c,
    fg_s_manualresultsRef_v mr,
    fg_s_experiment_v ex
    where smpl.PARENTID = smplReq.EXPERIMENTDEST_ID
    and smpl.SAMPLE_ID = smplReq.SAMPLE_ID
    and smpl.PARENTID = ex.experiment_id
    and smpl.sessionid is null and nvl(smpl.active,'1')='1'
    and c.parentId = ex.experiment_id
    and c.sessionid is null and nvl(c.active,'1') = '1'
    and mr.SAMPLE_ID = smpl.SAMPLE_ID
    and mr.COMPONENT_ID = c.COMPONENT_ID
    and mr.parentid = ex.experiment_id
    and nullif(smplReq.REQUEST_ID,to_number(mr.REQUEST_ID))is not null)*/

     --check all file ids connected to fg_files
/*     select * from
 (
 select instractionsFile as FILE_ID from fg_s_selftesttype_pivot
 where sessionid is null
 and nvl(active,'1')='1'
 union all
  select spectrum from fg_s_component_pivot
 where sessionid is null
 and nvl(active,'1')='1'
 union all
  select attachment from fg_s_manualresultsref_pivot
 where sessionid is null
 and nvl(active,'1')='1'
 union all
  select attachment from fg_s_prmanualresultref_pivot
 where sessionid is null
 and nvl(active,'1')='1'
 union all
   select attachment from fg_s_manualresultsmsref_pivot
 where sessionid is null
 and nvl(active,'1')='1'
 union all
   select spectrum from fg_s_invitemmaterial_pivot
   union all
   (
   select d.documentupload from fg_s_Document_pivot d where 1=1 and d.LINK_ATTACHMENT = 'Attachment'
 and d.sessionid is null
 and d.active=1
   )
 union all
   select attachment from fg_s_operationtype_pivot
 where sessionid is null
 and nvl(active,'1')='1'
 ) where file_id is not null
 and file_id not  in (select f.file_id from fg_files f)
 */
    /*

    delete from FG_ACTIVITY_LOG t where t.activitylogtype not in ('Depletion','NotificationEvent')

    --compare FG_FORMENTITY
 select t.formcode,t.entityimpcode,t.entityimpinit from FG_FORMENTITY t where 1=1 and lower(t.formcode) like lower('MaterialRef') and lower(t.entityimpcode) like lower('%purityInf')
 minus
 select t.formcode,t.entityimpcode,t.entityimpinit from skyline_form_server.FG_FORMENTITY t where 1=1 and lower(t.formcode) like lower('MaterialRef') and lower(t.entityimpcode) like lower('%purityInf')
 */
     /*
     --UPDATE ON SERVER fg_formentity rows
     update FG_FORMENTITY t set t.comments = null where t.comments like 'UPDATE ON SERVER CHANGE FROM%';
     select * from FG_FORMENTITY t where t.comments like 'UPDATE ON SERVER CHANGE FROM%';
     */
     /*
      --FORMCODE <> FORMCODEENTITY
      --select * from FG_FORM t where t.formcode <> t.formcode_entity AND t.form_type = 'STRUCT' AND UPPER(FORMCODE) NOT LIKE '%YARON%' AND UPPER(FORMCODE) NOT LIKE '%MAIN'


     --Edit FG_FORME by formcode
select t.*, t.rowid from FG_FORM t where UPPER(t.formcode) like UPPER('%xxx%');

--Edit FG_FORMENTITY by formcode
select t.*, t.rowid from FG_FORMENTITY t where UPPER(t.formcode) like UPPER('%xxx%');

--Edit FG_FORMENTITY by init
select t.*, t.rowid from FG_FORMENTITY t where UPPER(t.entityimpinit) like UPPER('%xxx%');

--Edit FG_FORMENTITY by formcode
select t.*, t.rowid from FG_FORMENTITY t where UPPER(t.entityimpinit) like UPPER('%xxx%');

--Edit FG_RESOURCE by CODE
select t.*, t.rowid from FG_RESOURCE t where UPPER(t.code) like UPPER('%xxx%');

--Edit FG_RESOURCE by type
select t.*, t.rowid from FG_RESOURCE t where UPPER(t.type) like UPPER('%xxx%');

--Edit FG_FORMLASTSAVEVALUE by type
select t.*, t.rowid from FG_FORMLASTSAVEVALUE t where UPPER(t.formcode) like UPPER('%xxx%');

--SEQ
select * from fg_sequence_plus_v t where t.id = to_number('xxx');

-- INSERT VIEW TO RESOURCE
INSERT INTO FG_RESOURCE (TYPE,CODE,VALUE,INFO)
select 'INFORMATION_TABLE', T.VIEW_NAME, T.VIEW_NAME, 'TODO DESC'
from user_views t
where t.VIEW_NAME like 'FG__%'
AND SUBSTR(T.VIEW_NAME,4,1) IN ('M','R','I')
AND SUBSTR(T.VIEW_NAME,5,1) = '_'
AND UPPER(T.VIEW_NAME) NOT IN (SELECT UPPER(T1.CODE) FROM FG_RESOURCE T1)

-- no id in seq
select * from FG_TOOL_INF_ALL_V where  id not in (select id from fg_sequence);
--   NAME  ID  TABLENAME
--4  Room  26885  FG_S_SAMPLETYPE_INF_V
--3  Oven  26884  FG_S_SAMPLETYPE_INF_V
--2  Drying 2  20799  FG_S_MP_INF_V
--1  Drying1  20797  FG_S_MP_INF_V
     */


     --select * from FG_TOOL_INF_ALL_V where  id not in (select id from fg_sequence)
/*  NAME  ID  TABLENAME
4  Room  26885  FG_S_SAMPLETYPE_INF_V
3  Oven  26884  FG_S_SAMPLETYPE_INF_V
2  Drying 2  20799  FG_S_MP_INF_V
1  Drying1  20797  FG_S_MP_INF_V*/
/*
1  Oven  26884  FG_S_SAMPLETYPE_INF_V
2  Room  26885  FG_S_SAMPLETYPE_INF_V*/

--CREATE TABLE YARON_CHECK_SEQ AS
--select * from FG_TOOL_INF_ALL_V T where  UPPER(T.tablename)  not in (select UPPER('FG_S_' || f.formcode || '_INF_V')  from fg_sequence s, fg_form f where  f.formcode = s.formcode AND T.ID = S.ID );
/*
select * from (
select t.*,  fg_get_numeric (t.entityimpvalue) numres
from fg_formlastsavevalue t
where t.active = 1 )
where numres > 1000
and numres is not null
and numres not in (select id from fg_sequence)
and formcode_entity not in ( 'ExperimentSeries' ,'ExpInSeries')
and entityimpcode not in ('formNumberId','SESSIONID','factor','projectNumber','viscosity','quantity','serialNumber')*/


/*DDL INF WHAT IS IN USE...

select fg_get_value_from_json(t.entityimpinit ,'parentElement','na'), t.* from FG_FORMENTITY t
--from FG_FORMENTITY t
where 1=1--t.formcode like 'ExperimentPr%'
--AND   t.formcode <> 'ExperimentPr'
and t.entityimpclass in ('ElementAutoCompleteIdValDDLImp')
--and t.entityimpinit  like '%defaultValue%'
--and t.entityimpinit   like '%disableScript%'
and fg_get_value_from_json(t.entityimpinit ,'parentElement','na')<>'na'
and t.entityimpinit like '%$P{%'*/

--smartsearch element
/*select DISTINCT  f.formcode_entity, T.FORMCODE, T.ENTITYIMPCODE,T.ENTITYIMPCLASS ,T1.ENTITYIMPCODE,T1.ENTITYIMPCLASS
from fg_formentity t,
     fg_formentity t1,
     fg_form f
where t.formcode = t1.formcode
and instr(upper(t1.entityimpinit),upper(t.entityimpcode)) > 0
and   t1.entityimpclass like '%ElementSmartSearchImp%'
 and  t.entityimpclass in ( 'ElementAutoCompleteIdValDDLImp' ,'ElementAutoCompleteIdValDDLImp' )
 and t.formcode = f.formcode*/

 /*--numeric calculation
 select  * from FG_FORMENTITY t
--from FG_FORMENTITY t
where 1=1--t.formcode like 'ExperimentPr%'
--AND   t.formcode <> 'ExperimentPr'
and t.entityimpclass in ('ElementInputImp','ElementApiElementSetterImp')
--and t.entityimpinit  like '%defaultValue%'
 --and t.entityimpinit   like '%disableAlways%'
 and fg_get_value_from_json(t.entityimpinit ,'disableAlways','na') = 'true'
 and fg_get_value_from_json(t.entityimpinit ,'precision','-1') > 0
 and (fg_get_value_from_json(t.entityimpinit ,'type','na') = 'Number' or t.entityimpclass in ('ElementApiElementSetterImp'))
*/

     /*
     -- smart serach sql
     select *
       from FG_FORMENTITY p
       -- elemet that parent is used by search (not ElementInputImp) that has suns
       where (p.formcode, p.entityimpcode) in (
       select f.formcode , fg_get_value_from_json(f.entityimpinit ,'parentElement','na')
       from FG_FORMENTITY f,
       (
      select t.formcode, t.entityimpinit EL From FG_FORMENTITY t
      --from FG_FORMENTITY t
      where 1=1--t.formcode like 'ExperimentPr%'
      and t.entityimpclass in ('ElementSmartSearchImp')
      --and t.entityimpinit  like '%defaultValue%'
       --and t.entityimpinit   like '%disableAlways%'
       and t.formcode not in ('xxx','ExperimentPr')
       --and fg_get_value_from_json(t.entityimpinit ,'parentElement','na') <> 'na'
       ) s -- serach element with suns
       where
       s.formcode = f.formcode
       and f.entityimpclass not in ('ElementInputImp') -- the trigger needed to be work on
       and instr(s.el,f.entityimpcode) > 0 ) */

      /*
      -- data wf sql
        select *
       from FG_FORMENTITY p where (p.formcode, p.entityimpcode) in (
       select f.formcode , fg_get_value_from_json(f.entityimpinit ,'parentElement','na')
       from FG_FORMENTITY f,
       (
      select t.formcode, t.entityimpinit EL From FG_FORMENTITY t
      --from FG_FORMENTITY t
      where 1=1--t.formcode like 'ExperimentPr%'
      and t.entityimpclass in ('ElementSmartSearchImp')
      --and t.entityimpinit  like '%defaultValue%'
       --and t.entityimpinit   like '%disableAlways%'
       and t.formcode not in ('xxx','ExperimentPr')
       --and fg_get_value_from_json(t.entityimpinit ,'parentElement','na') <> 'na'
       ) s
       where
       s.formcode = f.formcode
       and instr(s.el,f.entityimpcode) > 0 )*/

       /*
       -- element  preventSave true -- for search
        select *
          From FG_FORMENTITY t
      --from FG_FORMENTITY t
      where 1=1--t.formcode like 'ExperimentPr%'
     -- and t.entityimpclass in ('ElementSmartSearchImp')
      --and t.entityimpinit  like '%defaultValue%'
       --and t.entityimpinit   like '%disableAlways%'
       and t.formcode not in ('xxx','ExperimentPr')
        and lower(fg_get_value_from_json(t.entityimpinit ,'preventSave','na')) = 'true'
        and lower(fg_get_value_from_json(t.entityimpinit ,'hideAlways','na')) <> 'true'
        and t.entityimpclass not in ('ElementAsyncIframeImp','ElementRadioImp','ElementChemDoodleImp','chemDoodleSearch')
        */
       /*
       --diff in formentity
       select t.formcode,t.entityimpcode,t.entityimpinit from FG_FORMENTITY t
        minus
        select t.formcode,t.entityimpcode,t.entityimpinit from skyline_form_server.FG_FORMENTITY t;

        select t.formcode,t.entityimpcode,t.entityimpinit from skyline_form_server.FG_FORMENTITY t
        minus
        select t.formcode,t.entityimpcode,t.entityimpinit from FG_FORMENTITY t;

        select t.formcode,t.entityimpcode,t.entityimpinit from FG_FORMENTITY t
        minus
        select t.formcode,t.entityimpcode,t.entityimpinit from skyline_form_unittest.FG_FORMENTITY t;

        select t.formcode,t.entityimpcode,t.entityimpinit from skyline_form_unittest.FG_FORMENTITY t
        minus
        select t.formcode,t.entityimpcode,t.entityimpinit from FG_FORMENTITY t;
 */
 /*
        -- columns compare
        select t.COLUMN_NAME from dba_tab_columns t where t.TABLE_NAME = 'FG_S_ACTION_PIVOT' and t.owner = 'SKYLINE_FORM_SERVER'
        minus
        select t.COLUMN_NAME from user_tab_columns t where t.TABLE_NAME = 'FG_S_ACTION_PIVOT'
 */
        -- set all password to 1234 and exp + year
        --update fg_s_user_pivot t set t.password = '81dc9bdb52d04dc20036dbd8313ed055', t.lastpassworddate = TO_CHAR(SYSDATE + 365,'dd/MM/yyyy')
   end;

   procedure completeData as
      sql_ varchar2(32767);
      newline varchar(10) :=  CHR(13) || CHR(10);
      return_ number;
   begin
        --******************************************
        --********** UPDATE fg_tool_inf_all_v FOR fill FG_TOOL_INF_ALL_DATA with id / name / inf_v view name (for the inf_v in the system)
        --******************************************
        --create sql for fg_tool_inf_all_v
        dbms_output.put_line('create sql for fg_tool_inf_all_v');
        for r in (
        select distinct t.sql_ from FG_TOOL_INF_V_SUMMARY t where 1=1 --rownum < 5
        ) loop
        sql_ := sql_ || r.sql_ || newline;
        end loop;
        sql_ := sql_ || ' select null as name, null as id, null as tablename from dual where 1=2 ';

        --create view fg_tool_inf_all_v
        dbms_output.put_line('create view fg_tool_inf_all_v');
        execute immediate ' create or replace view fg_tool_inf_all_v as ' || newline || sql_;

        --delete FG_TOOL_INF_ALL_DATA
        dbms_output.put_line('delete FG_TOOL_INF_ALL_DATA');
        delete from FG_TOOL_INF_ALL_DATA;

        --insert data to FG_TOOL_INF_ALL_DATA
        /*dbms_output.put_line('insert data to FG_TOOL_INF_ALL_DATA');
        insert into FG_TOOL_INF_ALL_DATA (NAME,ID,TABLENAME)
        select t.name,t.id,t.tablename
        from fg_tool_inf_all_v t;*/

        --FG_SEQUENCE_INSERT_TRIG disable
        /*dbms_output.put_line('FG_SEQUENCE_INSERT_TRIG disable');
        execute immediate (' ALTER TRIGGER FG_SEQUENCE_INSERT_TRIG disable ');

        --******************************************
        --********** UPDATE fg_sequence with names
        --******************************************
        --add missing rows in fg_sequence
        dbms_output.put_line('add missing rows in fg_sequence');
        insert into fg_sequence (id,formcode,insertdate,formidname)
        select distinct t.id, f.formcode, sysdate, max(nvl(name,'NA')) over (partition by t.id)
        from FG_TOOL_INF_ALL_DATA t,
             (SELECT distinct * FROM FG_FORM T1 WHERE T1.FORMCODE = T1.FORMCODE_ENTITY and lower(T1.FORMCODE) not like '%yaron%') f
        where 1=1
        AND   upper(REPLACE(REPLACE(T.TABLENAME,'FG_S_',''),'_INF_V')) = upper(f.Formcode)
        and   t.id is not null
        AND   t.id not in (select id from fg_sequence);

        --FG_SEQUENCE_INSERT_TRIG enable
        dbms_output.put_line('FG_SEQUENCE_INSERT_TRIG enable');
        execute immediate (' ALTER TRIGGER FG_SEQUENCE_INSERT_TRIG enable ');*/

        --Done!
        dbms_output.put_line('Done!');

        --put all system view in table
        return_ := FG_output_system_struct_V;

        --for admin report...
        dbms_mview.refresh('FG_R_FORMENTITY_V');

        --add formcode to maintenance
        COMMIT;

    EXCEPTION
      WHEN OTHERS THEN
        --FG_SEQUENCE_INSERT_TRIG enable IN EXCEPTION
        --dbms_output.put_line('FG_SEQUENCE_INSERT_TRIG enable IN EXCEPTION');
        --execute immediate (' ALTER TRIGGER FG_SEQUENCE_INSERT_TRIG enable ');

        --Done WITH ERROR
        dbms_output.put_line('Done WITH ERROR');

    end;

----------------------

function FG_output_STRUCT_ALL_V return varchar  as

  --l_search varchar2(1000) := 'union';
  l_char varchar2(32767);
  --toReturn varchar2(32767);

begin
  --for rec in (select text from user_views where VIEW_NAME = upper(viewName_in)  )
for rec in (select text, VIEW_NAME from user_views where VIEW_NAME like 'FG_S_%_ALL_V'  )
  loop
    l_char := rec.text;
  /*  if instr(l_char,'--t.* end! edit only the code below...') > 0 then
       toReturn :=
      'create or  replace view ' || upper(viewName_in) || ' as ' || chr(10) || '--t.* ' || chr(10) || 'select t.*, '  || chr(10) ||
      substr(l_char,
             instr(l_char,
                   '--t.* end! edit only the code below...')
             );
             dbms_output.put_line(toReturn);
      EXECUTE IMMEDIATE toReturn;
    ELSE*/
               dbms_output.put_line('create or  replace view ' || rec.VIEW_NAME || ' as ' || chr(10) ||
                                     'select t.*' || chr(10) ||
                                     '--t.* end! edit only the code below...' || chr(10) ||
                                     ',' || chr(10) || chr(10) ||
                                     l_char || chr(10) || chr(10));
    /*end if;*/


  end loop;

  return 1;

end;

function FG_output_system_struct_V return varchar  as

  --l_search varchar2(1000) := 'union';
  l_char clob;
  --toReturn varchar2(32767);

begin
  delete from fg_r_system_view;
  --for rec in (select text from user_views where VIEW_NAME = upper(viewName_in)  )
for rec in (select text, VIEW_NAME from user_views where VIEW_NAME like '%'  )
  loop
    l_char := rec.text;
  /*  if instr(l_char,'--t.* end! edit only the code below...') > 0 then
       toReturn :=
      'create or  replace view ' || upper(viewName_in) || ' as ' || chr(10) || '--t.* ' || chr(10) || 'select t.*, '  || chr(10) ||
      substr(l_char,
             instr(l_char,
                   '--t.* end! edit only the code below...')
             );
             dbms_output.put_line(toReturn);
      EXECUTE IMMEDIATE toReturn;
    ELSE*/
               /*dbms_output.put_line('create or  replace view ' || rec.VIEW_NAME || ' as ' || chr(10) ||
                                     'select t.*' || chr(10) ||
                                     '--t.* end! edit only the code below...' || chr(10) ||
                                     ',' || chr(10) || chr(10) ||
                                     l_char || chr(10) || chr(10));     */
                                      --dbms_output.put_line(rec.VIEW_NAME);
                                     if rec.VIEW_NAME <> 'FG_TOOL_INF_ALL_V' then

                                       /*if length(l_char) <= 4000 then*/
                                         INSERT INTO fg_r_system_view (db_name,view_name,view_code,view_snapshot_date)
                                         VALUES('SKYLINE_FORM',rec.VIEW_NAME,l_char,TO_CHAR(SYSDATE,'dd/MM/yyyy hh24:mi:ss' ));
                                       /*else
                                         INSERT INTO fg_r_system_view (db_name,view_name,view_code,view_snapshot_date)
                                         VALUES('SKYLINE_FORM',rec.VIEW_NAME, l_char || '...',TO_CHAR(SYSDATE,'dd/MM/yyyy hh24:mi:ss' ));
                                       end if;*/

                                     end if;

    /*end if;*/


  end loop;
  commit;

  return 1;

end;

--function called from GeneralUtilVersionData
--It is used for put long field to clob field of service table (FG_R_MATERIALIZED_VIEW). Temp table is used for create version data script

function fg_output_materialized_view_v (db_name_in varchar) return varchar  as

  l_char clob;

begin
  delete from FG_R_MATERIALIZED_VIEW;


for rec in (select query, mview_name from sys.all_mviews where lower(owner) = db_name_in order by staleness/* and mview_name like 'DUMMY_MV%'*/ )
  loop
    l_char := rec.query;

           INSERT INTO FG_R_MATERIALIZED_VIEW (db_name,view_name,view_code,view_snapshot_date)
           VALUES(db_name_in,rec.mview_name,l_char,TO_CHAR(SYSDATE,'dd/MM/yyyy hh24:mi:ss' ));

  end loop;
  commit;

  return 1;

end;


FUNCTION FG_SET_ALL_STRUCT_ALL_V return number as

    toReturn number;

begin

     FOR r in  (
         /*select distinct t.formcode
          from fg_form t
          where t.form_type in ('STRUCT','INVITEM','ATTACHMENT','REF','SELECT','MAINTENANCE')
          AND T.FORMCODE NOT IN (select FORMCODE from FG_FORM t WHERE T.FORMCODE <> T.FORMCODE_ENTITY AND T.FORM_TYPE = 'MAINTENANCE')
         and lower(t.formcode) not like 'yaron%'
          and lower(t.formcode) not like 'alex%'
           and lower(t.formcode) not like 'xxx%'
          and lower(t.formcode) not like 'experiment%'
          and lower(t.formcode) not like 'selftestfrmain%'
          and lower(t.formcode) not like 'templateexpselect'
          and lower(t.formcode) not like 'templateselect'
          and lower(t.formcode) not like '%[not in use]%'
          and lower(t.Group_Name) not like 'reftemplate%'
          and lower(t.formcode) not like 'failuretype%'
           and lower(t.formcode) not like 'unittest%'
           and lower(t.formcode) not like 'syseventhandler%'
             and lower(t.formcode) not like 'subprojecttype%'
              and lower(t.formcode_entity) not like 'formtestname%'
                            and lower(t.formcode_entity) not like 'testedentity%'
                               and lower(t.formcode_entity) not like 'customer%'
                               and lower(t.formcode_entity) not like 'prexpimmersion%'
                                and lower(t.formcode_entity) not like 'source%'*/
                            select distinct t.formcode
          from fg_form t
          where t.form_type in ('STRUCT','INVITEM','ATTACHMENT','REF','SELECT','MAINTENANCE')
          AND T.FORMCODE NOT IN (select FORMCODE from FG_FORM t WHERE T.FORMCODE <> T.FORMCODE_ENTITY AND T.FORM_TYPE = 'MAINTENANCE')
        and t.FORMCODE_ENTITY in (select t.formcode_entity from fg_formlastsavevalue t)
        --and EXISTS (select 1 from user_tables t1 where t1.TABLE_NAME = 'FG_S_' || UPPER(t.formcode_entity) || '_PIVOT')
                and lower(t.formcode) not like 'yaron%'
          and lower(t.formcode) not like 'alex%'
           and lower(t.formcode) not like 'xxx%'





         )
     LOOP
       dbms_output.put_line(r.formcode);
       toReturn := fg_set_struct_pivot_table(r.formcode, 1);
     END LOOP;

    return toReturn;
end;

procedure initFormlastsavevalueHst as
  adminUserId number;
begin
  DBMS_OUTPUT.put_line('todo');
/*  select min(t.USER_ID) into adminUserId from FG_S_USER_ALL_V t where t.UserRoleName = 'Admin';

  delete from fg_formlastsavevalue_hst;

  insert into fg_formlastsavevalue_hst (
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
    ACTIVE )
select
    FORMID,
    FORMCODE_ENTITY,
    ENTITYIMPCODE,
    ENTITYIMPVALUE,
    USERID,
    'Init hst',
    adminUserId,
    'I',
    sysdate,
    sessionid,
    ACTIVE
from fg_formlastsavevalue;*/
end;


function updateFormlastsavevalueFromHst (formId_in varchar) RETURN NUMBER as
begin
  dbms_output.put_line('todo');
 delete from fg_formlastsavevalue t where t.formid = formId_in;

insert into fg_formlastsavevalue(formid,FORMCODE_ENTITY,entityimpcode,entityimpvalue,userid,active)
select formid,FORMCODE_ENTITY,entityimpcode,entityimpvalue,userid,active
from (
select
    FORMID,
    FORMCODE_ENTITY,
    ENTITYIMPCODE,
    ENTITYIMPVALUE,
    USERID,
    t.change_date,
    sessionid,
    active,
    max(t.change_date) over (partition by t.formid) as maxDate
from fg_formlastsavevalue_hst t
where  t.formid  = formId_in
)
where maxDate = change_date;
--commit;
RETURN 1;
end;

PROCEDURE updateServerMaintenanceData AS
SQL_ VARCHAR2(4000);
SQL_TEMPLATE_ VARCHAR2(4000) := '/*INSERT INTO FG_S_@FORMCODE@_PIVOT (@COL@)*/ SELECT count(*) /*@COL@*/ FROM skyline_form_server.FG_S_@FORMCODE@_PIVOT S WHERE S.@FORMCODE@NAME NOT IN (SELECT @FORMCODE@NAME FROM FG_S_@FORMCODE@_PIVOT);';
SQL_COL_ VARCHAR2(4000);
SQL_COL_LIST_ VARCHAR2(4000);
SQL_COL_TEMPLATE_ VARCHAR2(4000) := '
SELECT LISTAGG(COLUMN_NAME, '','') WITHIN GROUP(ORDER BY COLUMN_NAME) FROM (
SELECT DISTINCT COLUMN_NAME FROM (
select  t.COLUMN_NAME, COUNT(t.COLUMN_NAME) OVER (PARTITION BY t.COLUMN_NAME) AS VC
from  all_tab_columns t
where t.TABLE_NAME = ''FG_S_@FORMCODE@_PIVOT''
and  t.OWNER  IN (''SKYLINE_FORM_COPY2'',''SKYLINE_FORM_SERVER'')
)
WHERE VC = 2)';

BEGIN
  FOR R IN (
  SELECT UPPER(T1.FORMCODE) AS FROMCODE FROM FG_FORM T1 WHERE T1.FORM_TYPE = 'MAINTENANCE'
  )
  LOOP
    SQL_COL_ := REPLACE(SQL_COL_TEMPLATE_,'@FORMCODE@',R.FROMCODE);
    EXECUTE IMMEDIATE SQL_COL_ into SQL_COL_LIST_;
    IF SQL_COL_LIST_ IS NOT NULL THEN
      SQL_ := REPLACE(REPLACE(SQL_TEMPLATE_,'@FORMCODE@',R.FROMCODE),'@COL@',SQL_COL_LIST_);
      DBMS_OUTPUT.put_line(SQL_);
    END IF;
  END LOOP;

END;

function getNextBookMark (str_in varchar) RETURN varchar as
  toretrun varchar(300);
  begin
    select substr(str_in ,1, length(str_in) - 1) || (substr(str_in ,length(str_in)) + 1) into toretrun from dual;
    return toretrun;
exception
  when others then
    return 'NA';

end;

PROCEDURE setLabelElementByNextBookMark as
  begin
       update FG_FORMENTITY f set f.entityimpinit = (
        select /*s.id,*/ REPLACE(s.entityimpinit,'"layoutBookMarkItem','"elementName":"' || t.entityimpcode || '","layoutBookMarkItem') AS GOOD_LABEL_INIT
        from FG_FORMENTITY t,
             (select *  from FG_FORMENTITY t where   1=1 /*and t.entityimpinit not like'%elementName%'*/ and t.entityimpclass in ('ElementLabelImp')) s
        where 1=1
        and t.entityimpclass in ('ElementAutoCompleteIdValDDLImp','ElementInputImp','ElementAutoCompleteDDLImp','ElementTextareaImp')
        and s.formcode = t.formcode
        --and '%' || lower(t.entityimpcode) || '%' not like lower(s.entityimpinit)
        and s.entityimpinit not like'%elementName%'
        and form_tool.getNextBookMark(fg_get_value_from_entity_int(s."FORMCODE",s.entityimpcode,'layoutBookMarkItem','')) = fg_get_value_from_entity_int(t."FORMCODE",t.entityimpcode,'layoutBookMarkItem','')
        and f.id = s.id
        ), f.comments = entityimpinit
  where f.id in (
        select s.id
        from/* FG_FORMENTITY t,*/
             (select *  from FG_FORMENTITY t where   1=1 /*and t.entityimpinit not like'%elementName%'*/ and t.entityimpclass in ('ElementLabelImp')) s
        where 1=1
       /* and t.entityimpclass in ('ElementAutoCompleteIdValDDLImp','ElementInputImp','ElementAutoCompleteDDLImp','ElementTextareaImp')
        and s.formcode = t.formcode*/
        --and '%' || lower(t.entityimpcode) || '%' not like lower(s.entityimpinit)
        and s.entityimpinit not like'%elementName%'
/*        and form_tool.getNextBookMark(fg_get_value_from_entity_int(s."FORMCODE",s.entityimpcode,'layoutBookMarkItem','')) = fg_get_value_from_entity_int(t."FORMCODE",t.entityimpcode,'layoutBookMarkItem','')
*/


          ) ;

          update FG_FORMENTITY t1 set t1.entityimpinit = t1.comments where t1.entityimpinit is null;
          commit;
    end;

    function deleteFormData (formCode_in varchar, deleteFormDef_in number default 0) return number as
      formcodeEntity_in varchar2(1000);
      is_pivot number;
      begin
        select nvl(t.formcode_entity,t.formcode) into formcodeEntity_in
        from fg_form t
        where t.formcode = formCode_in;
        --Delete Data
        delete from fg_sequence t where lower(t.formcode) =  lower(formCode_in); --TODO deep delete
        delete from fg_formlastsavevalue t where lower(t.FORMCODE_ENTITY) =  lower(formCode_in);
        delete from fg_formlastsavevalue_hst t where lower(t.FORMCODE_ENTITY) =  lower(formCode_in);
        delete from fg_formlastsavevalue_inf t where lower(t.FORMCODE_ENTITY) =  lower(formCode_in);
        select count(*) into is_pivot from user_tables t where t.TABLE_NAME = upper('fg_s_' || formcodeEntity_in || '_pivot');
        if (is_pivot > 0) then
            execute immediate('delete from fg_s_' || formcodeEntity_in || '_pivot');
        end if;
        --Delete Config (if deleteFormDef_in=1)
        if nvl(deleteFormDef_in,0) = 1 then
          --fg_form
          --EXECUTE IMMEDIATE ' CREATE table fg_form_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from fg_form ';
          --fg_formentity
          --EXECUTE IMMEDIATE ' CREATE table fg_formentity_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from fg_formentity ';
          --fg_resource
          --EXECUTE IMMEDIATE ' CREATE table fg_resource_' || to_char(sysdate,'DDMMHH24MISS') || ' as select * from fg_resource ';

          delete from fg_formentity t where lower(t.formcode) =  lower(formCode_in);
          delete from fg_form t where lower(t.formcode) = lower(formCode_in);
          delete from fg_resource t where lower(t.code) = lower('fg_s_' || formCode_in || '_all_v');
          delete from fg_resource t where lower(t.code) = lower('fg_s_' || formCode_in || '_dt_v');
        end if;

        commit;
        return 1;
  end;

procedure cleanInvalidData (formCodeIn varchar) as
      formCodeEntity_ varchar2(500);
      invalidSuffix_ varchar2(500);
 begin
        --init formCodeEntity_
        select t.formcode_entity into formCodeEntity_ from fg_form t where upper(t.formcode) = upper(formCodeIn);
        --select '[inv ' || to_char(sysdate,'ddMMyyyHH24MISS') || ']' into invalidSuffix_ from dual;
        select '[inv '' || FORMID || '']' into invalidSuffix_ from dual;

        --inactive form FG_FORMLASTSAVEVALUE
        --EXECUTE IMMEDIATE
        dbms_output.put_line (
        ' update fg_formlastsavevalue set entityimpvalue = entityimpvalue || ''' || invalidSuffix_ || ''' where upper(entityimpcode) = upper(''' || formCodeEntity_|| 'Name'') and formid in (select formid from FG_S_' || upper(formCodeIn) || '_INVLD);'
        );

        dbms_output.put_line (
        ' update fg_formlastsavevalue set active=0 where formid IN (select formid from FG_S_' || upper(formCodeIn) || '_INVLD);'
        );

        --inactive form pivot
        --EXECUTE IMMEDIATE
        dbms_output.put_line (
        ' update FG_S_' || upper(formCodeEntity_) || '_PIVOT set active = 0, ' || formCodeEntity_|| 'Name = ' || formCodeEntity_|| 'Name ||''' || invalidSuffix_ || ''' where formid in (select formid from FG_S_' || upper(formCodeIn) || '_INVLD);'
        );

        --commit;
 end;

   function removeFromIdFromDB(formId_in varchar, formCodeEntity_in varchar,  ts_in varchar) return number as
      inuse number;
      sql_ varchar2(5000);
   begin
    select count(*) into inuse from fg_sequence t where t.id = formId_in and t.formcode in (select formcode from fg_form f where f.form_type = 'MAINTENANCE' or f.formcode = 'FormulantRef');
    insert into fg_debug (comment_info) values('sql_1 = ' || sql_);
    if inuse <> 1 then
        return 0;
     end if;

     sql_ := 'delete from FG_S_' || formCodeEntity_in || '_PIVOT where formid = ''' || formId_in || ''' ';
     --insert into fg_debug (comment_info) values('sql_2 = ' || sql_);

     execute immediate sql_;
     delete from fg_formlastsavevalue t where t.formid = formId_in;
     delete from fg_formlastsavevalue_hst t where t.formid = formId_in;
     delete from fg_formlastsavevalue_inf t where t.formid = formId_in;
     delete from fg_sequence t where t.id = formId_in;

     return 1;
   end;

procedure unpivotFromUnitTestConf as
      newid varchar(100);
  begin
      --unpivot from  FG_S_UNITTESTCONFIG_PIVOT_TMP to FG_FORMLASTSAVEVALUE_231017_KD; formid as is
      /*insert into FG_FORMLASTSAVEVALUE_231017_KD (formid, formcode_entity, entityimpcode,entityimpvalue, userid, active)
      select  formid, 'UnitTestConfig' as formcode_entity, entityimpcode, entityimpvalue, 23830 as userid, 1 as active
      from FG_S_UNITTESTCONFIG_PIVOT_TMP unpivot-- include nulls
        (entityimpvalue for(entityimpcode) in
          (
          ENTITYIMPNAME as 'entityImpName',
          FIELDVALUE as 'fieldValue',
          IGNORETEST as 'ignoreTest',
          ORDEROFEXECUTION as 'orderOfExecution',
          TESTINGFORMCODE as 'testingFormCode',
          UNITTESTACTION as 'unitTestAction',
          UNITTESTCONFIGNAME as 'unitTestConfigName',
          UNITTESTGROUP_ID as 'UNITTESTGROUP_ID',
          WAITINGTIME as 'waitingTime'
          )
         )
      order by formid, entityimpcode;
      commit;*/

      --copy from temp table FG_FORMLASTSAVEVALUE_231017_KD to fg_formlastsavevalue; insert with new formid
      --declare
        --newid varchar(100);
      --begin
     /*   for r in (
          select distinct t.formid from FG_FORMLASTSAVEVALUE_231017_KD t
        )
        loop
          newid := fg_get_struct_form_id ('UnitTestConfig');
          insert into fg_formlastsavevalue (formid,formcode_entity,entityimpcode,entityimpvalue,userid,active)
          select newid,formcode_entity,entityimpcode,entityimpvalue,userid,active
          from FG_FORMLASTSAVEVALUE_231017_KD t
          where t.formid = r.formid;
        end loop;
        commit;*/
      --end;

      --select * from FG_FORMLASTSAVEVALUE_231017_KD t
      --delete from fg_sequence t where t.formcode = 'UnitTestConfig'

      --delete from fg_formlastsavevalue t where t.formcode_entity = 'UnitTestConfig'

      /*select t.*, t.rowid from fg_formlastsavevalue t
      where 1=1
      and t.formcode_entity like '%UnitTestConfig'
      and t.formid in (select e.formid from fg_formlastsavevalue e where e.entityimpcode = 'testingFormCode' and e.entityimpvalue like 'ExperimentMain%')
      and t.entityimpcode = 'ignoreTest'*/
      /*--declare
        newid varchar(100);
      --begin
        for r in (
          select distinct t.formid from fg_formlastsavevalue_kd161117 t
        )
        loop
          newid := fg_get_struct_form_id ('UnitTestConfig');
          insert into fg_formlastsavevalue (formid,formcode_entity,entityimpcode,entityimpvalue,userid,active)
          select newid,formcode_entity,entityimpcode,entityimpvalue,userid,active
          from fg_formlastsavevalue_kd161117 t
          where 1=1
          and t.formid = r.formid
          and t.formcode_entity like '%UnitTestConfig'
          and t.formid in (select e.formid from fg_formlastsavevalue e where e.entityimpcode = 'testingFormCode' and e.entityimpvalue like 'SubProject')
          and t.formid in (select e.formid from fg_formlastsavevalue e where e.entityimpcode = 'UNITTESTGROUP_ID' and e.entityimpvalue like '25319')
          and t.formid in (select e.formid from fg_formlastsavevalue e where e.entityimpcode = 'orderOfExecution' and (e.entityimpvalue > 250 and e.entityimpvalue < 260));
        end loop;
        --commit;
      --end;*/
            dbms_output.put_line('done!');
  end;

  procedure removeDTRemoveButtons as
  begin
    --set the "Horizontal" button table to hide remove buttons except the maintenance table
    update FG_FORMENTITY t set t.entityimpinit = substr(t.entityimpinit ,1,length(t.entityimpinit)-1) || ',"hideRemoveButton":"True"}', t.comments = 'remove non maintenance remove buttens from DT. BU=' || t.entityimpinit
    where t.entityimpclass = 'ElementDataTableApiImp'
    and fg_get_value_from_json(t.entityimpinit,'hideRemoveButton') = 'NA'
    AND t.entityimpinit LIKE '%}'
    --and t.entityimpinit not like '%"hideRemoveButton":"%'
    --And  t.formcode in (select formcode from fg_form t1 where t1.form_type <> 'MAINTENANCE')
    AND  t.entityimpinit like  '%"actionButtons":"Horizontal"%'
    AND  t.entityimpinit not like  '%"hideButtons":"True"%'
    --and t.entityimpinit not like '%"hideRemoveButton":"True"}'
    And  t.formcode <> 'Maintenance';
  end;

  procedure tool_check_data(db_name_in varchar) as -- db_name_in = db_name to be check against this current user
      --r_tables_list c_tables_list%rowtype;
      --flagCounter number := 0;
     tmpMinusQuery varchar2(4000) := '';
   -- collist_ varchar2 (4000);
     --collistAs_ varchar2 (4000);
     -- validateFromFormId varchar2(100);
      counter_ number;

       collist_ varchar2 (4000);
       collistAs_ varchar2 (4000);
     -- validateFromFormId varchar2(100);

       sqlExcel varchar2 (3600);
       c1 clob := '1';
       c2 clob := '2';

   begin

      for r in (
        select distinct SYSCONFEXCELDATANAME from fg_s_sysconfexceldata_pivot t where t.active = 1
      )

      loop

        sqlExcel := 'select count(*) from ' || db_name_in || '.fg_clob_files t where t.file_id =  (select exceldata from ' || db_name_in || '.fg_s_sysconfexceldata_pivot t where SYSCONFEXCELDATANAME = ''' || r.sysconfexceldataname || ''')';
        execute immediate sqlExcel into counter_;
        if counter_ = 1 then
          sqlExcel := 'select t.file_content from fg_clob_files t where t.file_id =  (select exceldata from fg_s_sysconfexceldata_pivot t where SYSCONFEXCELDATANAME = ''' || r.sysconfexceldataname || ''')';
          execute immediate sqlExcel into c1;

          sqlExcel := 'select t.file_content from ' || db_name_in || '.fg_clob_files t where t.file_id =  (select exceldata from ' || db_name_in || '.fg_s_sysconfexceldata_pivot t where SYSCONFEXCELDATANAME = ''' || r.sysconfexceldataname || ''')';
          execute immediate sqlExcel into c2;

          if(dbms_lob.compare( c1, c2 ) <> 0) then
            dbms_output.put_line('Warning! the excel ' || r.sysconfexceldataname || ' data (in fg_s_sysconfexceldata_pivot) is different!');
          end if;
        else
            dbms_output.put_line('Warning! the excel ' || r.sysconfexceldataname || ' (active row or data) is not exists in ' || db_name_in || '.fg_s_sysconfexceldata_pivot!');
        end if;



      end loop;


      counter_ := '0';
      delete from fg_debug;
      for r in (
            select TABLE_NAME
            from user_tables t
            where 1=1
            AND   UPPER(REPLACE(REPLACE(T.TABLE_NAME,'FG_S_'),'_PIVOT'))
            IN (
                  SELECT UPPER(F.FORMCODE_ENTITY)
                  FROM FG_FORM F
                  WHERE F.FORM_TYPE = 'MAINTENANCE'
                  and f.group_name in ('_System Event Handler','_System Configuration Pool','_System Configuration Report')
                )
      )
      loop
        collistAs_:= gettablecolumnlistnoid(r.TABLE_NAME,'FORMID,TIMESTAMP,CHANGE_BY,ACTIVE,FORMCODE_ENTITY,FORMCODE,CHANGE_BY,SESSIONID,CREATED_BY,CREATION_DATE,EXCELDATA,SQLTEXT',1);
        collist_:= gettablecolumnlistnoid(r.TABLE_NAME,'FORMID,TIMESTAMP,CHANGE_BY,ACTIVE,FORMCODE_ENTITY,FORMCODE,CHANGE_BY,SESSIONID,CREATED_BY,CREATION_DATE,EXCELDATA,SQLTEXT');
        --tmpMinusQuery := ' select count(T1.formid) as formid_counter from ' || r.TABLE_NAME || ' T1 WHERE (' || collist_ || ') IN (' || chr(10) ||
         tmpMinusQuery := ' select count(*) from (' || chr(10) ||
                         ' select ' || collistAs_ || ' from ' || r.TABLE_NAME || chr(10) ||
                         ' minus  ' || chr(10) ||
                         ' select ' || collistAs_ || ' from ' || db_name_in || '.' || r.TABLE_NAME || ')';
        --dbms_output.put_line(tmpMinusQuery);
        insert into fg_debug (comment_info,comments) values ( 'sql maintenanace check', tmpMinusQuery);

        execute immediate tmpMinusQuery into counter_;

        if counter_ > 0 then
          dbms_output.put_line('Warning! ' || r.TABLE_NAME || ' has problem in copy data to the server.');
        end if;

      end loop;

      --fg_form
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
                         ' select formcode, description, active, form_type, title, subtitle, use_as_template, group_name, numberoforder, formcode_entity, ignore_nav, usecache, change_date from fg_form ' || chr(10) ||
                         ' minus  ' || chr(10) ||
                         ' select formcode, description, active, form_type, title, subtitle, use_as_template, group_name, numberoforder, formcode_entity, ignore_nav, usecache, change_date from ' || db_name_in || '.fg_form )';
      --dbms_output.put_line(tmpMinusQuery);
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_form data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_form check', tmpMinusQuery);

      --fg_formentity
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
                         ' select formcode, numberoforder, entitytype, entityimpcode, entityimpclass, entityimpinit, comments, fs, fs_gap from fg_formentity ' || chr(10) ||
                         ' minus  ' || chr(10) ||
                         ' select formcode, numberoforder, entitytype, entityimpcode, entityimpclass, entityimpinit, comments, fs, fs_gap from ' || db_name_in || '.fg_formentity )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_formentity data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_formentity check', tmpMinusQuery);

      --fg_resource
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
                         ' select type, code, value, info from fg_resource ' || chr(10) ||
                         ' minus  ' || chr(10) ||
                         ' select type, code, value, info from ' || db_name_in || '.fg_resource )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_resource data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_resource check', tmpMinusQuery);



    /*--d_notification_message->the process of the inserting is made in the server according to a condition
     tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select "D_NOTIFICATION_MESSAGE_ID", "NOTIFICATION_MODULE_ID", "MESSAGE_TYPE_ID", "DESCRIPTION",
      "EMAIL_SUBJECT",to_char("EMAIL_BODY") as "EMAIL_BODY", "NOTIFICATION_SCHEDULER_ID","SCHEDULER_INTERVAL",
      replace(replace(to_char("WHERE_STATEMENT"), chr(10), ''''),chr(13), '''') as "WHERE_STATEMENT",to_char("SELECTED_FIELDS") as "SELECTED_FIELDS",
      "RESEND","ISACTIVE","ISVISIBLE","ATTACHED_REPORT_NAME","ATTACHED_REPORT_TYPE","ADD_ATTACHMENTS","UPDATED_BY","TIME_STAMP","AUDIT_COMMENT","COLUMN_NUMBER",
      "TRIGGER_TYPE_ID","ON_SAVE_ID" from d_notification_message where isactive = 1 ' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select "D_NOTIFICATION_MESSAGE_ID", "NOTIFICATION_MODULE_ID", "MESSAGE_TYPE_ID", "DESCRIPTION",
      "EMAIL_SUBJECT",to_char("EMAIL_BODY") as "EMAIL_BODY", "NOTIFICATION_SCHEDULER_ID","SCHEDULER_INTERVAL",
      replace(replace(to_char("WHERE_STATEMENT"), chr(10), ''''),chr(13), '''') as "WHERE_STATEMENT",to_char("SELECTED_FIELDS") as "SELECTED_FIELDS",
      "RESEND","ISACTIVE","ISVISIBLE","ATTACHED_REPORT_NAME","ATTACHED_REPORT_TYPE","ADD_ATTACHMENTS","UPDATED_BY","TIME_STAMP","AUDIT_COMMENT","COLUMN_NUMBER",
      "TRIGGER_TYPE_ID","ON_SAVE_ID" from ' || db_name_in || '.d_notification_message where isactive = 1 )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! d_notification_message data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql d_notification_message check', tmpMinusQuery);*/

    /*  tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select addressee_group_id, module_id, addressee_group_title, to_char(addressee_group_select),
      to_char(params_field_names) from p_notification_listaddresgroup ' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select addressee_group_id, module_id, addressee_group_title, to_char(addressee_group_select),
      to_char(params_field_names) from ' || db_name_in || '.p_notification_listaddresgroup )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! p_notification_listaddresgroup data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql p_notification_listaddresgroup check', tmpMinusQuery);

      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select p_notification_module_type_id, module_name, to_char(view_name), isvisible, uniq_id_filed, to_char(order_by), attached_report_name,
      attached_report_type from p_notification_module_type ' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select p_notification_module_type_id, module_name, to_char(view_name), isvisible, uniq_id_filed, to_char(order_by), attached_report_name,
      attached_report_type from ' || db_name_in || '.p_notification_module_type )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! p_notification_module_type data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql p_notification_module_type check', tmpMinusQuery);

      --FG_NOTIFICATION_COMPARE (summerize  all the notification)
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select d_notification_message_id,notification_module_id,message_type_id,description,trigger_type_id,email_subject,email_body,scheduler_interval,where_statement,resend,p_notification_module_type_id,module_name,select_statement,msguniqueidname,order_by,addressee_type_id,send_type,addressee_user_id,params_field_names,addressee_group_select,
        add_attachments,attached_report_name,attached_report_type,isactive from FG_NOTIFICATION_COMPARE '  || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select d_notification_message_id,notification_module_id,message_type_id,description,trigger_type_id,email_subject,email_body,scheduler_interval,where_statement,resend,p_notification_module_type_id,module_name,select_statement,msguniqueidname,order_by,addressee_type_id,send_type,addressee_user_id,params_field_names,addressee_group_select,
        add_attachments,attached_report_name,attached_report_type,isactive from ' || db_name_in ||  '.FG_NOTIFICATION_COMPARE)';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! FG_NOTIFICATION_COMPARE data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql FG_NOTIFICATION_COMPARE check', tmpMinusQuery);

      --REPORT_CATEGORY
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select id, REPORT_CATEGORY, report_sql, report_description, change_by, active, timestamp, report_user_id, report_scope, report_style,
      report_name, report_save_data, meta_data from fg_report_list where system_row = 1' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select id, REPORT_CATEGORY, report_sql, report_description, change_by, active, timestamp, report_user_id, report_scope, report_style,
      report_name, report_save_data, meta_data from ' || db_name_in || '.fg_report_list  where system_row = 1)';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_report_list data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_report_list check', tmpMinusQuery);
*/
     /* tmpMinusQuery := ' select count (*) from (' || chr(10) ||
      ' select  distinct c.parent_id from ' || db_name_in || '.fg_s_invitemmaterial_v m, fg_chem_doodle_data c
      where m.STRUCTURE = c.parent_id and m.active = 1 and c.reaction_all_data_link is not null and c.smiles_data is not null' || chr(10) ||
      'minus ' || chr(10) ||
      ' select distinct t.elementid from ' || db_name_in || '.fg_chem_search t)';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Warning! Table fg_chem_doodle_data presumably contains ' || counter_ || ' records with a duplicated structure');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_s_invitemmaterial_v, fg_chem_search check', tmpMinusQuery);*/

      /*
      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select formid, timestamp, creation_date, cloneid, templateflag, change_by, created_by, sessionid, active, formcode_entity, formcode,
      unittestconfigcomments, groupname, unittestconfigname, unittestaction, orderofexecution, ignoretest, waitingtime, entityimpname,
      testingformcode, fieldvalue from fg_s_unittestconfig_pivot  ' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select formid, timestamp, creation_date, cloneid, templateflag, change_by, created_by, sessionid, active, formcode_entity, formcode,
      unittestconfigcomments, groupname, unittestconfigname, unittestaction, orderofexecution, ignoretest, waitingtime, entityimpname,
      testingformcode, fieldvalue from ' || db_name_in || '.fg_s_unittestconfig_pivot )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_s_unittestconfig_pivot data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_s_unittestconfig_pivot check', tmpMinusQuery);

      tmpMinusQuery := ' select count(*) from (' || chr(10) ||
      ' select formid, timestamp, change_by, sessionid, active, formcode_entity, formcode, unittestgroupname, orderofexecution, ignore,
      unittestlevels, comments, cloneid, templateflag, created_by, creation_date from fg_s_unittestgroup_pivot  ' || chr(10) ||
      ' minus  ' || chr(10) ||
      ' select formid, timestamp, change_by, sessionid, active, formcode_entity, formcode, unittestgroupname, orderofexecution, ignore,
      unittestlevels, comments, cloneid, templateflag, created_by, creation_date from ' || db_name_in || '.fg_s_unittestgroup_pivot )';
      execute immediate tmpMinusQuery into counter_;
      if counter_ > 0 then
          dbms_output.put_line('Error! fg_s_unittestgroup_pivot data different from the server. diff in ' || counter_ || ' records');
      end if;
      insert into fg_debug (comment_info,comments) values ( 'sql fg_s_unittestgroup_pivot check', tmpMinusQuery);
      */

      commit;

      dbms_output.put_line('If all you see is this line we are OK.');

      -- show list of all_v / dt_v views with formid duplication (without views from select forms like userscrew)
      /*   for r in (
            select VIEW_NAME
            from ALL_VIEWS t
          where UPPER(t.VIEW_NAME) like UPPER('fg_s%all_v') or UPPER(t.VIEW_NAME) like UPPER('fg_s%dt_v')

      )
       loop
         tmpMinusQuery := ' select count(*) from (' || chr(10) ||
                         ' select  count(*) as c, count(distinct t.formid) as cd from ' || r.VIEW_NAME ||' t) t1' ||chr(10)||
                         ' where t1.c <> t1.cd ; ' ;
        execute immediate tmpMinusQuery into counter_;
        if counter_ > 0 then
          dbms_output.put_line('Warn: duplication found in '|| r.VIEW_NAME);
        end if;

      end loop;
      */
      -- show list of tables (pivot) with formid that are not in FG_SEQUENCE (name of table and count of formid that are not in FG_SEQUENCE) [expect empty list]
     /* for r in (
            select TABLE_NAME
            from user_tables t
          where UPPER(t.TABLE_NAME) like UPPER('fg_s%pivot')

      )
      loop
         tmpMinusQuery := ' select  count(*) from ' || db_name_in || '.' || r.TABLE_NAME ||' t where t.formid not in (select f.id from FG_SEQUENCE f )' ;
         execute immediate tmpMinusQuery into counter_;
         if counter_ > 0 then
            dbms_output.put_line('Warn: table with missing formid in FG_SEQUENCE:'||r.table_name||'('||counter_||')');
         end if;
      end loop;*/


      -- show list of tables (pivot) with missing form code (we check from validateFromFormId)
     /* for r in (
            select TABLE_NAME
            from user_tables t
          where UPPER(t.TABLE_NAME) like UPPER('fg_s%pivot')

      )
       loop
        tmpMinusQuery := 'select count(*) from ' || db_name_in || '.'||r.TABLE_NAME ||' t where  t.formcode IS NULL';
        execute immediate tmpMinusQuery into counter_;

        if counter_ > 0 then
         dbms_output.put_line('Warn: table with missing formcode :'||r.table_name||'('||counter_||')');
        end if;
      end loop; */

      -- show list of tables (pivot) with missing form code entity (we check from validateFromFormId)
     /* for r in (
            select TABLE_NAME
            from user_tables t
          where UPPER(t.TABLE_NAME) like UPPER('fg_s%pivot')

      )
      loop
         tmpMinusQuery := 'select count(*) from ' || db_name_in || '.'||r.TABLE_NAME ||' t where t.formcode_entity IS NULL';
        execute immediate tmpMinusQuery into counter_;

        if counter_ > 0 then
         dbms_output.put_line('Warn: table with missing formcode_entity :'||r.table_name||'('||counter_||')');
        end if;
      end loop; */

      -- show list of FG_SEQUENCE with formcode not from fg_form or from special element (see FG_GET_STRUCT_FORM_ID call in java and DB)
      --(?) list of invalid DB object
      /*   FOR r IN (select  OBJECT_NAME  from dba_objects where status=upper('invalid'))

         loop
            DBMS_OUTPUT.PUT_LINE(r.OBJECT_NAME ||',');
         end loop;
      */
   end;



end;
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

/*if check_admin_ > 0 then
  select max(t.FORMID) into adminFormId from fg_s_user_pivot t where lower(t.USERNAME) = 'admin';
else
  --insert with password comply2005
  insert into FG_S_USER_PIVOT (formid, timestamp, change_by, sessionid, active, formcode, username, position, firstname, lastname, changepassword, deleted, locked, customer_id, userrole_id, userldap, passworddate, chgpassworddate, laboratory_id, unit_id, password, lastretry, retrycount, site_id, email, lastpassworddate, formcode_entity, teamleader_id, groupscrew, cloneid, permissiontable, sensitivitylevel_id, templateflag, created_by, creation_date)
  values (FG_GET_STRUCT_FORM_ID('User'), sysdate, null, null, 1, 'User', 'admin', null, 'admin', '1', null, '0', '0',null, null, null, '26/01/2017', '26/01/2017', null, null, 'e51e64148c4104bf2fb9f7e7b07837d5', '15-FEB-18', '1', null, 'skyline@comply.co.il', '26/01/2017', 'User', null, null, null, 'NA,ALL,fg_r_permissionUser_v,1,User,0,10,,', null, null, null, null);
  select max(t.FORMID) into adminFormId from fg_s_user_pivot t where lower(t.USERNAME) = 'admin';
end if;

update fg_s_user_pivot t set t.password = 'e51e64148c4104bf2fb9f7e7b07837d5' where t.username = 'admin'; --set to comply2005
*/
--system user
select count(*) into check_system_ from fg_s_user_pivot t where lower(t.USERNAME) = 'system';

if check_system_ = 0 then
  --insert with password sys_comply2005
  insert into FG_S_USER_PIVOT (formid, timestamp, change_by, sessionid, active, formcode, username, position, firstname, lastname, changepassword, deleted, locked, customer_id, userrole_id, userldap, passworddate, chgpassworddate, laboratory_id, unit_id, password, lastretry, retrycount, site_id, email, lastpassworddate, formcode_entity, teamleader_id, groupscrew, cloneid, permissiontable, sensitivitylevel_id, templateflag, created_by, creation_date)
  values (FG_GET_STRUCT_FORM_ID('User'), sysdate, null, null, 1, 'User', 'system', null, 'system', '1', null, '0', '0',null, null, null, '26/01/2017', '26/01/2017', null, null, 'ab5d6cb5ad2b840e5a7e7d02afc60ff6', '15-FEB-18', '1', null, 'skyline@comply.co.il', '26/01/2017', 'User', null, null, null, 'NA,ALL,fg_r_permissionUser_v,1,User,0,10,,', null, null, null, null);
end if;

update fg_s_user_pivot t set t.password = 'e51e64148c4104bf2fb9f7e7b07837d5' where t.username = 'system'; --set to comply2005


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
