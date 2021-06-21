 
    <tr>
    	<td id="tdIncludeBreadcrumbJsp" style="display:inline">

		<!--<div class="row expanded page-header">-->
		<div class="expanded page-header">

			<div class="small-4 columns text-left breadcrumbs-container" style="width:33%;">

				&nbsp;

			</div>

			<div class="small-4 columns text-center" style="width:33%;">

				<h1 id="pageTitle"  class="cssPageHeader">${formTitle}</h1>
				<h2 id="pageSubTitle" width="54%" class="cssCurrentPath">${formSubTitle}</h2>

			</div>

			<div class=" text-right toolbar" style="text-align:right;padding-right:3em;">

					 <div>	
					 <button type="button" class="button hollow"  id="saveheaderButton" title="Save" perm_attr="cu" onclick="$('#save_')[0].click()"><i class="fa fa-save" style="font-size:1.3em;" aria-hidden="true"></i></button>	
					 <button id="close_back_header" class="button hollow"  type="button" onclick="parent.$('#prevDialog').dialog('close');"><i class="fa fa-close" style="font-size:1.3em;" aria-hidden="true"></i></button>
					<button type="button" class="button hollow popupSaveDefinitionBtn ignor_data_change" style="display:none" onclick="doSave('','SAVE_USER_SETTINGS')">
						 	 <img src="../skylineFormWebapp/images/save_display.png"/>
						 </button>
						  <button type="button" class="button hollow popupSaveFormAndDefinitionBtn ignor_data_change" style="display:none" onclick="doSave('','SAVE_FORM_AND_USER_SETTINGS')">
						 	 Save Display
						 </button>
					 </div>
				 

			</div>

		</div>

		</td>
           
    </tr>  