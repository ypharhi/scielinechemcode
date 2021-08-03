<script src="../skylineFormWebapp/deps/moment.min.js"></script>
<script src="../skylineFormWebapp/js/generalFunc<spring:message code="generalFuncMin" text="." />js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/jquery.cookie.js" ></script>
<script src="../skylineFormWebapp/js/properties.js" ></script>
<script defer src="../skylineFormWebapp/js/chosen.jquery.min.js"></script>

<!-- IMPORTANT note for jquery.dataTables v1.10.20.js library: there are custom compatibility changes in the library code, be careful on upgrade library -->
<script	src="../skylineFormWebapp/deps/jquery.dataTables<spring:message code="jqueryDataTablesMin" text="." />js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/deps/dataTables.jqueryui.js"></script>

<script src="../skylineFormWebapp/deps/dataTables.buttons.min.js"></script>
<!-- <script src="../skylineFormWebapp/deps/buttons.flash.min.js"></script> -->
<script src="../skylineFormWebapp/deps/jszip.min.js"></script>
<script defer src="../skylineFormWebapp/deps/pdfmake.min.js"></script>
<script defer src="../skylineFormWebapp/deps/vfs_fonts.js"></script>
<script defer src="../skylineFormWebapp/deps/arialFontPDF.js"></script>
<script defer src="../skylineFormWebapp/deps/buttons.html5.min.js"></script>
<script src="../skylineFormWebapp/deps/buttons.print.min.js"></script>
<script src="../skylineFormWebapp/deps/datetime-moment.js"></script>
<script src="../skylineFormWebapp/deps/jquery.alphanum.js"></script>
<script defer src="../skylineFormWebapp/deps/bignumber.min.js"></script>
<script defer src="../skylineFormWebapp/js/foundation.min.js" ></script>
<script defer src="../skylineFormWebapp/deps/url.min.js"></script>
<script defer src="../skylineFormWebapp/js/history_navigation.js?<spring:message code="Env" text="" />"></script>
<!-- <script src="../skylineFormWebapp/deps/toastr.js"></script> --> 
<script defer src="../skylineFormWebapp/deps/jquery.ui-contextmenu.min.js"></script>
<!-- IMPORTANT note for dataTables.colResize.js library: there are custom compatibility changes in the library code, be careful on upgrade library -->
<script type="text/javascript" src="../skylineFormWebapp/deps/dataTables.colResize.js?<spring:message code="Env" text="" />"></script>
<script type="text/javascript" src="../skylineFormWebapp/deps/dataTables.colReorder.js?<spring:message code="Env" text="" />"></script> 
<script src="../skylineFormWebapp/deps/dataTables.rowsGroup.js"></script>
<script>
	$(document).ready(function()
	{
		//INIT foundation
    	$(document).foundation();
		
        // update message icon with new message count
        //update with the session result (the server side eval messageCount in every page)
    	var messageCounter = <%= (session.getAttribute("messageCount") != null) ? session.getAttribute("messageCount") : "0"  %>;
    	updateMenuItemUI(messageCounter);
    	
    	//asyn call to update messageCount every checkInterval_
    	var counter = 0;
    	var checkInterval_ = <%= (session.getAttribute("messageCheckInterval") != null) ? session.getAttribute("messageCheckInterval") : "20"  %>;  // 300000 ms = 5 minutes
    	var checkIntervalMS_ = checkInterval_ * 6000000;
    	//console.log("checkIntervalMS_",checkIntervalMS_);
    	var timer = setInterval(function(){
    	    counter++;
    	    checkNotificationMessage();

    	    if (counter >= 10) {
    	       clearInterval(timer);
    	    }
    	}, checkIntervalMS_);
    	
		// handel browser navigation:
		// F5 - NOTE refresh icon is not handaled  in this case the page will reload with another statkey and the bread crunb will start form this from
		$("body").keydown(function(e) {
		    if(e.which==116){
		    	e.preventDefault();
// 		    	alert('f5 clicked');
		    	fgReloadForm(null, true);
		    	return;
// 		        e.preventDefault();
		    }
		});
		 
    	// forword - disabled it
	 	window.addEventListener('next', function(e) {
	 		// do nothing
// 	 		alert('next button clicked');
// 	 		 e.preventDefault();
	  	}, false);
	  
    	// back - will activate the doBack() function
	  	window.addEventListener('previous', function(e) {
// 	  		 alert('back button clicked');
	  		 doBack();
	  		 return;
// 	  		 e.preventDefault();
	  	}, false);

    	
    	//call authzCheckOnDonReady
    	var newurl = url();
    	
    	//remove STATE_KEY
// 		if (history.pushState) {
			
//             if(newurl.indexOf("&stateKey") > 0) {
//             	newurl = newurl.substr(0, newurl.indexOf("&stateKey"));
//             }
// 			//push new URL
//             window.history.pushState({
//                 path: newurl
//             }, '', newurl);
//         }
    	
    	authzCheckOnDonReady(newurl);
    	
    	$(document).unbind('keydown').bind('keydown', function (event) 
		{
		    if (event.keyCode === 9) 
		    {		       
		        event.preventDefault();		        
		    }
		});
		
		$(document).on('click', function (e) 
		{
		    if ($(e.target).closest("div.breadcrumb-dropdown").length === 0) 
		    { 		    			    	
		    	var _divO = $("div#breadcrumbDropdownDiv");
		    	if(_divO.css('display') == 'block')
		    	{
		    		_divO.hide();
		    	}
		    }
		});
	});
	
	

</script> 
