package com.skyline.form.bean;


/**
 * @author YPharhi
 *
 */
public enum ValidationCode {

	GENERAL, 
	/**
	 * description: validate (auto) Number
	 * we check: The the format of the Number entities
	 * validateValueObject: String represent the Number
	 */
	INVALID_PROJECT_FORM_NUMBER_ID,
	/**
	 * description: validate (auto) Number
	 * we check: The the format of the Number entities
	 * validateValueObject: String represent the Number
	 */
	INVALID_SUBPROJECT_FORM_NUMBER_ID,
	/**
	 * description: validate (auto) Number
	 * we check: The the format of the Number entities
	 * validateValueObject: String represent the Number
	 */
	INVALID_SUBSUBPROJECT_FORM_NUMBER_ID,
	/**
	 * description: validate Project Type Name. 
	 * Only Chemistry or Formulation possible
	 */
	CHECK_VALID_PROJECTTYPENAME,
	/**
	 * description: check if material in use. 
	 */
	INVALID_MATERIAL,
	CHECK_SELEFTEST_VALIDATION,
	/**
	 * description: check if all self tests under specific step were filled . 
	 */
	INVALID_BATCHDEFINITION_DUPLICATION,
	/**
	 * description: validates batch definition value in the sample
	 * we check: The batch is defined at most once in the sample
	 * validateValueObject: String represents the Number
	 */
	INVALID_CONSUMED_QUANTITY,
	/**
	 * description: Validates consumed quantity
	 * we check: The consumed quantity is smaller than the source quantity
	 * validateValueObject: String represents the [consumed quantity,source quantity] (that should be depleted)
	 */
	SPLITED_FAILED,
	/**
	 * description: Validates consumed quantity
	 * we check: The consumed quantity is smaller than the source quantity
	 * validateValueObject: String represents the [consumed quantity,source quantity] (that should be depleted)
	 */
	INVALID_EXPERIMENT_NONFAMILIAR_STATUS, INVALID_MCW_PROJECT, INVALID_MCW_SUBPROJECT,
	/**
	 * description: Validates that the step status change to active is valid
	 * we check: All the users under the experiment are familiar with all the inventory in the experiment
	 * validateValueObject: String represents the [consumed quantity,source quantity] (that should be depleted)
	 */
	
	NOT_TEMPLATE_APPROVER,
	/**
	 * description: Validates that the approve status made by team leader
	 * we check: as in description
	 * validateValueObject: error message
	 */
	INVALID_STEPSTATUS_EMPTYBATCH
	/**
	 * description: Validates that all the reactants have batches
	 * we check: as in description
	 * validateValueObject: empty string
	 */, 
	 INVALID_STEPSFRTATUS_EMPTYBATCH
	 /**
	 * description: Validates that all the formulants (NOT MIXTURE) have batches
	 * we check: as in description
	 * validateValueObject: empty string
	 */,
	 INVALID_AI_QUANTITY
	 
	 /**
	  * description: Validates that all the AI materials quantities >0 
	  * we check: as in description
	  * validateValueObject: empty string
	  */,
	 CHECK_MANDATORY_FIELDS_WUFILTRAWASHINGREF
	 /**
	  * description: Validates "Pressure" and "Centrifuge Speed" can't be mandatory together in WuFiltraWashingRef  
	  * we check: as in description
	  * validateValueObject: empty string
	  */,
	  CHECK_MANDATORY_FIELDS_INVITEMCOLUMN
		 /**
		  * description: Validates that  at least one of 3 fields is filled for InvItemColumn - Catalog Number / Serial Number / Batch Number  
		  * we check: as in description
		  * validateValueObject: empty string
		  */,
	  CHECK_SELEFTESTTYPE_FILE_EXTENSION
	  /**
	   * description: Validates that file uploaded from SelfTestType has 'PDF' as extension
	   * we check: as in description
	   * validateValueObject: error message
	   */,
	  CHECK_INVALID_NEGATIVE_RESULT
	  /**
	   * description: Validates that the result is not negative
	   * we check: as in description
	   * validateValueObject: error message
	   */,
	   INVALID_REMOVED_SELFTEST_INSTRUMENT
	   /**
	   * description: Validates that the instruments related to the results in  the selftest are connected to the selftest
	   * we check: as in description
	   * validateValueObject: error message
	   */,
	   INVALID_ACTION_ENDTINE
	   /**
	   * description: Validates that end time not greater than start time 
	   * we check: as in description
	   * validateValueObject: error message
	   */,
	   CHECK_SAMPLES_INUSE_REMOVED
	   /**
	    * description: Validates that samples used in the mass balance were not removed
	    */, 
	   CHECK_CONSUMED_QUANTITY
	   /**
		 * description: Validates consumed quantity
		 * we check: The consumed quantity is smaller than the source quantity
		 * validateValueObject: String represents the [consumed quantity,source quantity] (that should be depleted)
		 */, 
		 CONFIRM_NEW_EXPERIMENT
	   /**
		 * description:  warning message is needed if the user is sure that an additional experiment is needed.
		 * we check: If an experiment is already created on the request base and another one is created
		 * validateValueObject: confirm message
		 */,
		 RELATED_SAMPLE_BATCH
		 /**
		 * description:  Validates SPECIFICATIONS criteria
		 * validateValueObject: confirm message
		 */,
	    INVALID_SPECIFICATIONS
	    ,
	    /**
		 * description: validates when saving co-formulant material 
		 * we check: duplication name on co-formulant chemical material scope and show it as an link in case we found one
		 * validateValueObject: String represents the current material name
		 * Note: we have also index on the DB for race condition
		 */
	    CHECK_COFORMULANT_MATERIAL_NAME_DUPLICATION,
	    JCHEM_DUPLICATION
	    ,
	    INVALID_EXPERIMENT_FROM_SEVERAL_REQUESTS
	    ,
	    INVALID_EXPERIMENT_FROM_SEVERAL_REQUESTS_PROJ
	    ,
	    INVALID_EXPERIMENT_FROM_SEVERAL_REQUESTS_OPT
	    , 
	    VALIDATE_MATERIALRESULT_FILLED, DUPLICATE_OPERATIONTYPE
	    
	    , EMPTY_OPERATIONTYPE
	    
	    , REQUESTTYPE_OPERATIONTYPE
	    
	    , PROJECT_NUMBER_DEVIATION, SUBPROJECT_NUMBER_DEVIATION
	    , CHARACTERIZED_SAMPLE_EMPTY
	    ,INVALID_DATE, VALIDATE_REPORTNAME_EXIST,VALIDATE_REPORTDESIGNNAME_EXIST
	    , DEPLETE_BEFORE_CANCELL_MATERIAL
	    ,CHECKIFRECIPECOMPOSITION_BEFORE_CANCELL_MATERIAL
	    ,INVALID_USER_CLONE_EXP, VALIDATE_PLANNED_ACTUAL_SIMILAR
	    , CHECK_MATERIAL_DUPLICATION,ActionNameMandatory,
	    /**
	     * description: Validates if there are invalid consumption while updating the material density and recalculating the reaction using it
	     */
	    INVALID_CONSUMED_QUANTITY_MATERIAL, MpToBigger, ThermalDegToBigger, EMPTY_PROCEDURE_OPT,
	    /**
	     * check single active row in the maintenance form numstepdesign
	     */
	    MAINTENANCE_NUMSTEPDESIGN_SINGLE_ROW
	    ,INVALID_CONSUMED_QUANTITY_COMPLETED_EXP, CHECK_PARAMETERS_EXIST, VALIDATE_RUN_TURNS_ACTIVE, INVALID_FILE_UPLOAD,
	    ALERT_NEW_STEPFR_ACTION,
	    CHECK_HASCOMPONENT, CHECK_TESTED_COMPONENT_MANDATORY,
	    CHEKIFCOMPONENT_BEFORE_CANCELL_MATERIAL, CHECK_COMPOSITION_HAS_FILLER, CHECK_COMPOSITION_HAS_CANCELLED_MATERIALS, CHECK_BATCH_RECIPE_VALID, ALERT_NEW_FORMULATION_STEP
	    /**
	     * check that a list  of materials contains valid inventory materials
	     */
	    , INVALID_MATERIAL_NAME
	    , INVALID_RESULT_TYPE, INVALID_RESULT_SAMPLE, INVALID_UNKNOWN_MATERIAL, INVALID_UOM, INVALID_SPREADSHEETRESULT_MISSING_DATA,
}
