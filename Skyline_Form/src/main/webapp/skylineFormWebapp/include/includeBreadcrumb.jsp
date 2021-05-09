<div id="tdIncludeBreadcrumbJsp" style="display:inline;width: 100%;position: relative;float: left;">
		<div class="row expanded page-header">
			<div class="small-4 columns text-left breadcrumbs-container" style="width:32%;">
			<% if(session != null && session.getAttribute("SHOW_RESTORE_BREADCRUMB_BUTTON").toString().equals("1")) { %>
				<div style="padding-right:3em;">
					<button type="button" class="button" style="display:inline-block;width:180px;height:25px;font-size:12px;" id="restoreLastSessionBtn" onclick="restoreUserBreadcrumbsFromLastSession()">Restore Breadcrumb</button>
				</div>
			<% 
				session.setAttribute("SHOW_RESTORE_BREADCRUMB_BUTTON","0");
			} else {} 
			%>
				
				${breadCrumbHtml}
				<span><img src="../skylineFormWebapp/images/navigation_tree_open.png" id="navigationTree"  class="mainNavigationTreeBtn" onclick="openNavigationTree()" style="width: 23px;cursor:pointer;display:none;"/></span> 
			</div>
			<div class="small-4 columns text-center" style="width:7%;">
                <select id="headerSelect" class="chosen-select" data-placeholder="Choose:" lastvalue="" style="display:none;"></select>
			</div>
			<div class="small-4 columns text-center" style="width:32%;">
                <h1 id="pageTitle"  style = "white-space:nowrap;${formTitleCSS}" ${formTitleTooltip}>${favorite}${formTitle}</h1>
                <h2 id="pageSubTitle" width="54%" ${formSubTitleTooltip}>${formSubTitle}</h2>
                ${formPath}
				
			</div>
			<div class=" text-right toolbar" style="text-align:right;padding-right:3em;">
				 <button type="button" class="button hollow mainPlusMinusBtn" style="display:none" id="mainPlusButton">Add Table</button>
				 <button type="button" class="button hollow mainPlusMinusBtn" style="display:none" id="mainMinusButton">Remove Last Table</button>
				 <button type="button" class="button hollow mainSaveDefinitionBtn ignor_data_change" style="display:none" onclick="doSave('','SAVE_USER_SETTINGS')">
				 	<!-- <i class="fa fa-file-text-o" aria-hidden="true"></i> --> Save Display
				 </button>
				<button type="button" class="button hollow mainSaveFormAndDefinitionBtn ignor_data_change" style="display:none" onclick="doSave('','SAVE_FORM_AND_USER_SETTINGS')">
				 	<!-- <i class="fa fa-file-text-o" aria-hidden="true"></i> --> Save Display
				 </button>
				 <button type="button" class="button hollow mainSaveFormAndDefinitionByNameBtn ignor_data_change" style="display:none" onclick="openSaveReportDialog()">
				 	<!-- <i class="fa fa-file-text-o" aria-hidden="true"></i> --> Save Report
				 </button>
				  <button type="button" class="button hollow mainClearSearchBtn ignor_data_change" style="display:none" onclick="doSave('RELOAD','REMOVE_FORM_AND_USER_SETTINGS')">
				 	<!-- <i class="fa fa-file-text-o" aria-hidden="true"></i> --> Clear Search
				 </button>
				 <input type="hidden" id="useLoginsessionidScopeFlag" name="useLoginsessionidScopeFlag" value="0">				
		    </div>
		    <div class="floatingButtonsPanelContainer" id="divFloatingButtonsPanelContainer">
				<div class="floatingButtonsShowHideIcon" id="divFloatingButtonsShowHideIcon"><i class="fa fa-angle-right" aria-hidden="true" onclick="floatingButtonPanelToggleClick(this)"></i></div>
				<div class="floatingButtonsPanel" style="display:none;">
					<button type="button" class="button new-main-button floating-button" id="newFloatingButton" title="New" onclick="$('#newButton')[0].click()"><i class="fa fa-plus" aria-hidden="true"></i></button>	
					<button type="button" class="button save-main-button floating-button" id="saveFloatingButton" title="Save" perm_attr="cu" onclick="$('#saveButton')[0].click()"><i class="fa fa-save" aria-hidden="true"></i></button>		
					<button type="button" class="button close_back-main-button floating-button" id="close_backFloatingButton" title="Close" onclick="$('#close_back')[0].click()"><i class="fa fa-close" aria-hidden="true"></i></button>
					<button type="button" class="button ignor_data_change save-definition-button floating-button" id="mainSaveDefinitionFloatingBtn" title="Save display" onclick="doSave('','SAVE_USER_SETTINGS')" >
						<img src="../skylineFormWebapp/images/save_display.png"/>
					</button>	
				</div>
			</div>		
			<!-- ab 22122020: code for Demo floating tabs 
			<div class="floatingTabsPanelContainer" id="divFloatingTabsPanelContainer">
				<div class="floatingTabsShowHideIcon" id="divFloatingTabsShowHideIcon"><i class="fa fa-angle-right" aria-hidden="true" onclick="floatingTabsPanelToggleClick(this)"></i></div>				
				<div class="floatingTabsViewPanel" id="divFloatingTabsViewPanel">
					<iframe style="border: 0px;width:100%;height:100%" src="about:blank"></iframe>
				</div>
			</div> -->		
		</div>
</div>

