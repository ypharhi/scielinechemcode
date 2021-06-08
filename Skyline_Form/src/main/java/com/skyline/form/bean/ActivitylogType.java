package com.skyline.form.bean;

/**
 * Activity log types for the log events in the dydtem.
 * Impotent - activity logs that where defined as part of the system should have level type Other in log write call
 * @author YPharhi
 *
 */
public enum ActivitylogType {
	/* 'NotificationEvent' type changes referred to fg_r_messages table */
	NotificationEvent("NotificationEvent"), 
	OpenForm("OpenForm"), // write to log every time a user open a form
	Registration("Registration"), Depletion("Depletion"), Consumption("Consumption"), Permission("Permission"),
	WorkFlowNew("WorkFlow New"),WorkFlowStatus("WorkFlow Status"), WorkFlowGeneral("WorkFlowGeneral"), DataTable("Data Table"),Calculation("Calculation"),SaveEvent("Save Event"),SaveException("Save Exception"),AspectException("Aspect Exception"),
	GeneralError("General Error"),FormInitiation("Form Initiation"),PerformanceSQL("PerformanceSQL"),UiReport("UiReport"),SQLError("SQLError"),SQLInit("SQLInit"),SQLLastSave("SQLLastSave"),InfoLookUp("InfoLookUp"),ChemMol("ChemMol"),
	Creation("Creation"),InstrumentEquipment("Instrument Equipment"),RemovedFomExperiment("Removed From Experiment"), PerformanceJava("PerformanceJava"),ChemMolSearchTask("ChemMolSearchTask"),ReactionDataUI("Reaction Data UI Before Save"),
	/**
	 * description:Cancelled Request - If an experiment was already created from the request, a message should be sent to
	 *  the experiment owner �Request No. XXX has been canceled. Please cancel related experiments". 
	 * JsonObject contains: RequestNumber. experimentOwner_id,
	 */
	CancelledRequest("CancelledRequest"), 
	Scheduler("Scheduler"), DuplicatedMaterials("Duplicated Materials"), ManualResultsUpdate("Update Manual Results"), GetTreeNodes("Get Tree Nodes"), SQLEvent("SQLEvent"), RequestStatusChanged("RequestStatusChanged"),StartRun("startRun"),ActivateStep("ActivateStep"),ChemMatrixMap("ChemMatrixMap"),Login("Login");

	private String typeName;

	private ActivitylogType(String s) {
		typeName = s;
	}

	public String getActLogTypeName() {
		return typeName;
	}
	
	@Override
	public String toString() {
		return " ActivitylogType[typeName=" + typeName + "]";
	}
}
