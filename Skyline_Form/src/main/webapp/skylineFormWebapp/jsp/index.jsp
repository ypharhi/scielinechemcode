<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>Getting started with JSON Form using
	https://github.com/joshfire/jsonform</title>

<link rel="stylesheet" style="text/css" href="./deps/opt/bootstrap.css" />

<script type="text/javascript" src="./deps/jquery.min.js"></script>
<script type="text/javascript" src="./deps/underscore.js"></script>
<script type="text/javascript" src="./deps/opt/jsv.js"></script>
<script type="text/javascript" src="./lib/jsonform.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$('form').jsonForm({
			"schema" : {
				// 				message: 'Disabled',
				// 				Disable : {
				// 					type : 'array',
				// 					items : {
				// 						enum : [ '1', '22' ],
				// 						title : 'When No value in element:'
				// 					}
				// 				}
				"disabled" : {
					"type" : "array",
					"items" : {
						"type" : "object",
						"title" : "Disabled",
						"properties" : { 
							"gender" : {
								"type" : "string",
								"title" : "When no value in element:",
								"enum" : [ "male", "female", "alien" ]
							}
						}
					}
				}
			}
		});
	});
	// 	function getResourceByType(type) {
	// 		return JSON.parse($.ajax({ //http://hmkcode.com/spring-mvc-json-json-to-java/
	// 			type : 'POST',
	// 			data : '{"action" : "getResourceValueByType","data":[{"val":"'
	// 					+ type + '"}],"errorMsg":""}',
	// 			//data : '{"action" : "getResourceByType","data":[],"errorMsg":""}',
	// 			url : "getResourceValueByType.request",
	// 			contentType : 'application/json',
	// 			dataType : 'json',
	// 			async : false
	// 		}).responseText).data[0].val.split(",");
	// 	}
</script>


</head>
CATALOG.ORACLE.TABLE
<body>



	<div>
		<form></form>
		<div id="res"></div>
	</div>



</body>
</html>