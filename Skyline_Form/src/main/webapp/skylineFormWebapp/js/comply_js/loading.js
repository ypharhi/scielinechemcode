
/********   START
    display mask with image on page load and fade it out after whole page is loaded
*/
$(function ()
{
        var bodyElem = $('body')[0]; 
	if (bodyElem == null)
		return;
	if($('#loading_div_background_prev') != null)
        {
            $('#loading_div_background_prev').css('display', 'none');
        }
       // alert($('#show_loading_background').length);
//        if($('#show_loading_background').length > 0 && $('#show_loading_background').val() == 0)
//        {
//            return;
//        }                
	var mainDiv = document.createElement('div');
	$(mainDiv).attr({'id':'loading_div_background', 'class':'loading-div-background'});
        
        //alert($('#loading_div', window.parent.document).length);
        if($('#loading_div', window.parent.document).length == 0)
        {
            var subDiv = document.createElement('div');
            $(subDiv).attr({'id':'loading_div', 'class':'loading-div'});
    
            $(subDiv).append($('<img>').attr('src', '../skylineFormWebapp/images/page-loader.gif'))
                     .append($('<h2>').attr('class', 'cssUserMessage')
                                      .text('Please wait....'));	
    
            mainDiv.appendChild(subDiv); 	
        }
        else
        {
            $('#loading_div', window.parent.document).remove();
        }
        
	bodyElem.appendChild(mainDiv);

});

$(window).on('load', function() 
{
     $("#loading_div_background").delay(250).fadeOut('slow');
     //$("#loading_div_background").fadeOut('slow');
});
/*********** END **************/

