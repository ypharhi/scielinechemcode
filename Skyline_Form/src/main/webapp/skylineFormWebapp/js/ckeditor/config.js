/**
 * @license Copyright (c) 2003-2016, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	
	// %REMOVE_START%
	// The configuration options below are needed when running CKEditor from source files.
	config.plugins = 'dialogui,dialog,about,a11yhelp,basicstyles,blockquote,clipboard,panel,floatpanel,menu,contextmenu,resize,button,toolbar,elementspath,enterkey,entities,popup,filebrowser,floatingspace,listblock,richcombo,format,horizontalrule,htmlwriter,wysiwygarea,image,indent,indentlist,fakeobjects,link,list,magicline,maximize,pastetext,pastefromword,removeformat,showborders,sourcearea,specialchar,menubutton,scayt,stylescombo,tab,table,tabletools,undo,wsc,bidi,panelbutton,colorbutton,colordialog,justify';
	config.skin = 'moono-lisa';
	// %REMOVE_END%

	// Define changes to default configuration here.
	// For complete reference see:
	// http://docs.ckeditor.com/#!/api/CKEDITOR.config
	
	config.toolbar = [
		{ name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },
		{ name: 'editing',     groups: [ 'find', 'replace', 'selection', 'spellchecker' ] },
		{ name: 'links', items: [ 'Link', 'Unlink', 'Anchor'] },
		{ name: 'insert', items: [ 'Table', 'HorizontalRule', 'SpecialChar', 'Image' ] },
		{ name: 'tools', items: [ 'Maximize'] }, //'ShowBlocks'
		{ name: 'document', groups: [ 'mode', 'document', 'doctools' ]}, //items: [ 'Source' ]
		{ name: 'others', items: [ '-' ] },
		'/',
		{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike', '-', 'RemoveFormat' ] },
		{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl']},
		{ name: 'styles', items: [ 'Format', 'FontSize', 'Styles','Font' ] },
		{ name: 'colors', items : [ 'TextColor','BGColor' ] }
	];
	
	// The toolbar groups arrangement, optimized for two toolbar rows.
	config.toolbarGroups = [
		{ name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
		{ name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
		{ name: 'editing',     items : [ 'Find','Replace','-','SelectAll' ] },
		{ name: 'links' },
		{ name: 'insert' },
		{ name: 'forms' },
		{ name: 'tools' },
		{ name: 'document',	   groups: [ 'mode', 'document', 'doctools' ] },
		{ name: 'others' },
		'/',
		{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
		{ name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ] }, 
		{ name: 'styles' },
		{ name: 'colors', groups: [ 'colors' ]}
		//{ name: 'about' }
	];
	
	// Remove some buttons provided by the standard plugins, which are
	// not needed in the Standard(s) toolbar.
	//config.removeButtons = 'Underline,Subscript,Superscript';
	
	//config.disallowedContent='br';
	
	// Set the most common block elements.
	config.format_tags = 'p;h1;h2;h3;pre';

	// Simplify the dialog windows.
	config.removeDialogTabs = 'image:advanced;link:advanced';
	
//	config.extraPlugins = 'font';
	
	config.disableNativeSpellChecker = false;
	
	config.removePlugins = 'elementspath';
	
	///config.enterMode = 1;
	
	 config.enterMode = CKEDITOR.ENTER_BR // pressing the ENTER KEY input <br/>
/*     config.shiftEnterMode = CKEDITOR.ENTER_P; //pressing the SHIFT + ENTER KEYS input <p>
	 alert('A');
     config.autoParagraph = true; // stops automatic insertion of <p> on focus
     
*/
	
	// Remove multiple plugins.
	//config.removePlugins = 'magicline';
	//CKEDITOR.config.magicline_color = '#0000FF';
	
};

/* Patch work in Chrome only
 * CKEDITOR.on('instanceReady', function(ev) {						
	//catch ctrl+clicks on <a>'s in edit mode to open hrefs in new tab/window
	$('iframe').contents().click(function(e) 
	{		
		alert(e.keyCode);
		//alert(e.target.href + " : e.ctrlKey: " + e.ctrlKey);
		if(typeof e.target.href != 'undefined' && e.ctrlKey == true) 
		{			
			window.open(e.target.href, 'new' + e.screenX);
		}
	});	
});*/





