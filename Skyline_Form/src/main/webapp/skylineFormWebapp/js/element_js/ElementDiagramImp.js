
var diagram, nodeList, $diagramElement;
var backgroundColor, linkDashStyle, baseShape, headShape, headBrush;
var ContainerNode,Size,AbstractionLayer,AnchorPattern,AnchorPoint,DiagramNode,ShapeNode,MarkStyle,Style,
	Theme,FontStyle,Alignment,Behavior,HandlesStyle,ChangeItemCommand,Events,Diagram,Overview,NodeListView,
	Rect,Shape,Orientation;
/*
 *  Element use DiagramMindfusion web js files.
 *  Official Website: https://www.mindfusion.eu/
 *  Support: https://www.mindfusion.eu/HelpDesk/index.php
 *  Documentation: https://www.mindfusion.eu/onlinehelp/javascript.pack/index.htm
 *  
 *  Diagram is not saved when it is hidden.
 *  On save function the next data are saved in the DB(fg_diagram table):
 *  	- jsonObject: to save full state of the canvas;
 *  	- IMAGE: jpg of the canvas
 */
var ElementDiagramImp = {
 value_: function(val_) {
	 var id = $(val_).attr('id');    
     var elementID = $(val_).attr('elementID');
     var value = getValueForElementDiagram(id, elementID)
     var isHidden =  $diagramElement!=null&&$diagramElement.css("display")=='none';
     var data_ = {
         "value": value,
         "isHidden": isHidden
     };
     return JSON.stringify(data_);
 },
 setvalue_: function(val_) {
  //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
 },
 setDefaultValueForUnitTest_: function (val_) {
}
}; 

function getValueForElementDiagram(id,elementID){
	var toReturn = '';
	try{
		if(diagram!=null){
			toReturn= diagram.toJson() ;
		}
	}
	catch(error){
		console.log('error in the JsDiagram\n'+error);
	}
	
//	console.log("getValueForElementDiagram[domId="+id+"][elementID="+elementID+"] toReturn: " + toReturn);
	return toReturn;
}

function setDisabledDiagram(isDisabled,element){
	if(isDisabled){
		element.addClass('disabledDiagram');
	} else {
		element.removeClass('disabledDiagram');
	}
}

/**
 * return from onAjaxCall()
 * 
 * @param obj
 * @returns
 */
function upDateElementDiagram(obj) {
 if (typeof obj.isHidden !== 'undefined') {
  if (obj.isHidden.toLowerCase() == "false") {
   $('[id="' + obj.domId + '"]').css('visibility', 'visible');
   $('[id="' + obj.domId + '"]').css('display', 'block');
  } else {
   $('[id="' + obj.domId + '"]').css('display', 'none');
  }
 }
 if ((typeof obj.isDisabled !== 'undefined')) {
  if (obj.isDisabled.toLowerCase() == "false") {
   $('[id="' + obj.domId + '"]').removeClass('disabledDiagram');
  } else {
   $('[id="' + obj.domId + '"]').addClass('disabledDiagram');
   $('[id="' + obj.domId + '"]').css('border-color', '');
   $('[id="' + obj.domId + '"]').css('outline', '');
  }
 }
}

document.addEventListener("DOMContentLoaded", function (){
	if ($('[element=ElementDiagramImp]').length>0) {
		$diagramElement = $('[element=ElementDiagramImp]');
		
		try{
		 ContainerNode = MindFusion.Diagramming.ContainerNode;
		 Size = MindFusion.Drawing.Size;
		 AbstractionLayer = MindFusion.AbstractionLayer;
		 AnchorPattern = MindFusion.Diagramming.AnchorPattern;
		 AnchorPoint = MindFusion.Diagramming.AnchorPoint;
		 DiagramNode = MindFusion.Diagramming.DiagramNode;
		 ShapeNode = MindFusion.Diagramming.ShapeNode;
		 MarkStyle = MindFusion.Diagramming.MarkStyle;
		 Style = MindFusion.Diagramming.Style;
		 Theme = MindFusion.Diagramming.Theme;
		 FontStyle = MindFusion.Drawing.FontStyle;
		 Alignment = MindFusion.Diagramming.Alignment;
		 Behavior = MindFusion.Diagramming.Behavior;
		 HandlesStyle = MindFusion.Diagramming.HandlesStyle;
		 ChangeItemCommand = MindFusion.Diagramming.ChangeItemCommand;
		 Events = MindFusion.Diagramming.Events;
		 Diagram = MindFusion.Diagramming.Diagram;
		 Overview = MindFusion.Diagramming.Overview;
		 NodeListView = MindFusion.Diagramming.NodeListView;
		 Rect = MindFusion.Drawing.Rect;
		 Shape = MindFusion.Diagramming.Shape;
		 Orientation = MindFusion.Diagramming.Orientation;
		
		backgroundColor = "#88b663";
		// create a Diagram component that wraps the "diagram" canvas
		diagram = AbstractionLayer.createControl(Diagram, null, null, null, document.getElementById($diagramElement.attr('id')+"_diagram"));
		diagram.setAllowInplaceEdit(true);
		//diagram.setRouteLinks(true);
		diagram.setShowGrid(true);
		diagram.setUndoEnabled(true);
		diagram.setRoundedLinks(true);
		diagram.setLinkHeadShapeSize(2);
		//diagram.setSelectAfterCreate(false);
		//diagram.setShapeHandlesStyle(HandlesStyle.HatchHandles3);
		diagram.setBounds(new Rect(0, 0, 285,100));

		diagram.addEventListener(Events.nodeCreated, onNodeCreated);
		diagram.addEventListener(Events.linkCreating, onLinkCreated);
		diagram.addEventListener(Events.nodeSelected, onNodeSelected);
		diagram.addEventListener(Events.nodeDeselected, onNodeSelected);

		/*// create an Overview component that wraps the "overview" canvas
		overview = AbstractionLayer.createControl(Overview,
	        null, null, null, document.getElementById($diagramElement.attr('id')+"_overview"));
		overview.setDiagram(diagram);*/
		
		// create an NodeListView component that wraps the "nodeList" canvas
		nodeList = AbstractionLayer.createControl(NodeListView, null, null, null, $('#'+$diagramElement.attr('id')+"_nodeList")[0]);	
		//nodeList.setIconSize(new Size(30,30));
		nodeList.setDefaultNodeSize(new Size(10,10));
		 node = new ContainerNode();
		 //node.setCaptionBackBrush({ type: 'SolidBrush', color: '#88b663' });
		 node.setBrush({ type: 'SolidBrush', color: '#ffffff' });
		 nodeList.addNode(node, "Container");	
		 
		 for (var shapeId in Shape.shapes)
		{
			// skip some arrowhead shapes that aren't that useful as node shapes
			 var shape = Shape.shapes[shapeId]; 
			 var node = new MindFusion.Diagramming.ShapeNode(diagram);
			 node.setShape(shapeId);
			 //node.setBrush({ type: 'SolidBrush', color: '#88b663' });
			 var shapeName = shapeId.toString();
			 if(shapeName.match("^Bpmn")){
				 shapeName = shapeName.substring(4);
			 }
			 nodeList.addNode(node, shapeName);
		}		
		nodeList.addEventListener(Events.nodeSelected, onShapeSelected);
		nodeList.setOrientation(Orientation.Vertical);
		$('#'+$diagramElement.attr('id')+' [id=fontName]').change(function ()
		{
			var ChangeItemCommand = MindFusion.Diagramming.ChangeItemCommand;

			diagram.startCompositeOperation();
			for (var i = 0; i < diagram.getSelection().items.length; i++)
			{
				var item = diagram.getSelection().items[i];
				var change = new ChangeItemCommand(diagram, item);

				var style = item.getStyle();
				if (!style)
				{
					style = new Style();
					item.setStyle(style);
				}

				style.setFontName(this.value);
				item.invalidate();

				diagram.executeCommand(change);
			}
			diagram.commitCompositeOperation();
		});

		$('#'+$diagramElement.attr('id')+' [id=fontSize]').change(function ()
		{
			var ChangeItemCommand = MindFusion.Diagramming.ChangeItemCommand;

			diagram.startCompositeOperation();
			for (var i = 0; i < diagram.getSelection().items.length; i++)
			{
				var item = diagram.getSelection().items[i];
				var change = new ChangeItemCommand(diagram, item);

				var style = item.getStyle();
				if (!style)
				{
					style = new Style();
					item.setStyle(style);
				}

				style.setFontSize(this.value);
				item.invalidate();

				diagram.executeCommand(change);
			}
			diagram.commitCompositeOperation();
		});
		onLoaded();
		} catch(error){
			console.log('error in the JsDiagram\n'+error); 
		}
	}
});

function onShapeSelected(sender, e)
{
	var selectedNode = e.getNode();
	if (selectedNode)
		diagram.setDefaultShape(selectedNode.getShape());
}

function onLoaded()
{
	var Rect = MindFusion.Drawing.Rect;
	var AnchorPoint = MindFusion.Diagramming.AnchorPoint;
	var Theme = MindFusion.Diagramming.Theme;
	var DiagramNode = MindFusion.Diagramming.DiagramNode;
	var ShapeNode = MindFusion.Diagramming.ShapeNode;
	var AnchorPattern = MindFusion.Diagramming.AnchorPattern;
	var MarkStyle = MindFusion.Diagramming.MarkStyle;
	var Style = MindFusion.Diagramming.Style;

	anchorPattern = new AnchorPattern([
      new AnchorPoint(50, 0, true, false, MarkStyle.Rectangle, "#ff0000", 1.5),
      new AnchorPoint(0, 50, false, true, MarkStyle.Rectangle, "#008000", 1.5),
      new AnchorPoint(100, 50, false, true, MarkStyle.Rectangle, "#008000", 1.5),
      new AnchorPoint(50, 100, false, true, MarkStyle.Rectangle, "#008000", 1.5)
    ]);

	var theme = new Theme();
	var shapeNodeStyle = new Style();
	shapeNodeStyle.setBrush({ type: 'LinearGradientBrush', color1: '#e0e9e9', color2: '#9caac6', angle: 90 });
	shapeNodeStyle.setStroke("#7F7F7F");
	shapeNodeStyle.setTextColor("#585A5C");
	shapeNodeStyle.setFontName("Verdana");
	shapeNodeStyle.setFontSize(3);
	theme.styles["std:ShapeNode"] = shapeNodeStyle;
	var linkStyle = new Style();
	linkStyle.setStroke("#7F7F7F");
	linkStyle.setTextColor("#585A5C");
	linkStyle.setFontName("Verdana");
	linkStyle.setFontSize(3);
	theme.styles["std:DiagramLink"] = linkStyle;
	var tableStyle = new Style();
	tableStyle.setBrush({ type: 'LinearGradientBrush', color1: '#FCFCFC', color2: '#9DABB4', angle: 90 });
	tableStyle.setStroke("#7F7F7F");
	tableStyle.setTextColor("#585A5C");
	tableStyle.setFontName("Verdana");
	tableStyle.setFontSize(3);
	theme.styles["std:TableNode"] = tableStyle;
	diagram.setTheme(theme);

	var original = DiagramNode.prototype.createAnchorPointVisual;
	ShapeNode.prototype.createAnchorPointVisual = function (point)
	{
		var result = original.apply(this, [point]);
		result.brush = point.getColor();
		result.pen = '#7F7F7F';
		return result;
	};

	if(diagram._element.innerHTML.trim()!=""){
		var diagramString = diagram._element.innerHTML;
		if(checkIfJSON("{"+diagramString+"}")){
		    diagram.fromJson(diagramString);//funcParseJSONData(diagramString,true)
		}
	}
}

function onNodeCreated(sender, args)
{
	var AnchorPattern = MindFusion.Diagramming.AnchorPattern;
	var AnchorPoint = MindFusion.Diagramming.AnchorPoint;
	var HandlesStyle = MindFusion.Diagramming.HandlesStyle;
	var MarkStyle = MindFusion.Diagramming.MarkStyle;
	/*anchorPattern = new AnchorPattern([
	                                   new AnchorPoint(50, 0, true, false, MarkStyle.Rectangle, "#ff0000", 1.5),
	                                   new AnchorPoint(0, 50, false, true, MarkStyle.Rectangle, "#008000", 1.5),
	                                   new AnchorPoint(100, 50, false, true, MarkStyle.Rectangle, "#008000", 1.5),
	                                   new AnchorPoint(50, 100, false, true, MarkStyle.Rectangle, "#008000", 1.5)
	                                 ]);*/
	var node = args.getNode();
	node.setBrush(); // Reset brush
	//node.setAnchorPattern(anchorPattern);
	node.setHandlesStyle(HandlesStyle.HatchHandles3);
	node.setFont(null);
}

function onLinkCreated(sender, args)
{
	var link = args.getLink();
	//link.setHeadShape("Triangle");
	link.setHeadShapeSize(2);
}

function onNodeSelected(sender, args)
{
	var node;
	if (diagram.getSelection().nodes.length > 0)
		node = diagram.getSelection().nodes[0];
	if (node && node.getStyle() !== undefined)
	{
		var style = node.getStyle();
		if (style.getFontName())
			document.getElementById('fontName').value = (style.getFontName());
		else
			document.getElementById('fontName').value =('Verdana');
		if (style.getFontSize())
			document.getElementById('fontSize').value =(style.getFontSize());
		else
			document.getElementById('fontSize').value =('3');
	}
	else
	{
		document.getElementById('fontName').value =('Verdana');
		document.getElementById('fontSize').value =('3');
	}
}


function onUndo()
{
	diagram.undo();
}

function onRedo()
{
	diagram.redo();
}

function onDelete()
{
	diagram.startCompositeOperation();
	for (var i = diagram.getSelection().items.length - 1; i >= 0; i--)
	{
		diagram.removeItem(diagram.getSelection().items[i]);
	}
	diagram.commitCompositeOperation();
}

function onZoomIn()
{
	diagram.setZoomFactor(Math.min(800, diagram.getZoomFactor() + 10));
}

function onZoomOut()
{
	diagram.setZoomFactor(Math.max(10, diagram.getZoomFactor() - 10));
}

function onResetZoom()
{
	diagram.setZoomFactor(100);
}

function onBold()
{
	var ChangeItemCommand = MindFusion.Diagramming.ChangeItemCommand;

	diagram.startCompositeOperation();
	for (var i = 0; i < diagram.getSelection().items.length; i++)
	{
		var item = diagram.getSelection().items[i];
		var change = new ChangeItemCommand(diagram, item);

		var style = item.getStyle();
		if (!style)
		{
			style = new Style();
			item.setStyle(style);
		}

		if (style.getFontStyle() === undefined)
		{
			style.setFontStyle(FontStyle.Bold);
		}
		else if ((style.getFontStyle() & FontStyle.Bold) != FontStyle.Bold)
		{
			style.setFontStyle(style.getFontStyle() | FontStyle.Bold);
		}
		else
		{
			style.setFontStyle(style.getFontStyle() & ~FontStyle.Bold);
		}

		item.invalidate();

		diagram.executeCommand(change);
	}
	diagram.commitCompositeOperation();
}

function onItalic()
{
	var ChangeItemCommand = MindFusion.Diagramming.ChangeItemCommand;

	diagram.startCompositeOperation();
	for (var i = 0; i < diagram.getSelection().items.length; i++)
	{
		var item = diagram.getSelection().items[i];
		var change = new ChangeItemCommand(diagram, item);

		var style = item.getStyle();
		if (!style)
		{
			style = new Style();
			item.setStyle(style);
		}

		if (style.getFontStyle() === undefined)
		{
			style.setFontStyle(FontStyle.Italic);
		}
		else if ((style.getFontStyle() & FontStyle.Italic) != FontStyle.Italic)
		{
			style.setFontStyle(style.getFontStyle() | FontStyle.Italic);
		}
		else
		{
			style.setFontStyle(style.getFontStyle() & ~FontStyle.Italic);
		}

		item.invalidate();

		diagram.executeCommand(change);
	}
	diagram.commitCompositeOperation();
}

function onUnderlined()
{
	var ChangeItemCommand = MindFusion.Diagramming.ChangeItemCommand;

	diagram.startCompositeOperation();
	for (var i = 0; i < diagram.getSelection().items.length; i++)
	{
		var item = diagram.getSelection().items[i];
		var change = new ChangeItemCommand(diagram, item);

		var style = item.getStyle();
		if (!style)
		{
			style = new Style();
			item.setStyle(style);
		}

		if (style.getFontStyle() === undefined)
		{
			style.setFontStyle(FontStyle.Underline);
		}
		else if ((style.getFontStyle() & FontStyle.Underline) != FontStyle.Underline)
		{
			style.setFontStyle(style.getFontStyle() | FontStyle.Underline);
		}
		else
		{
			style.setFontStyle(style.getFontStyle() & ~FontStyle.Underline);
		}

		item.invalidate();

		diagram.executeCommand(change);
	}
	diagram.commitCompositeOperation();
}

function onLeft()
{
	var ChangeItemCommand = MindFusion.Diagramming.ChangeItemCommand;

	diagram.startCompositeOperation();
	for (var i = 0; i < diagram.getSelection().items.length; i++)
	{
		var item = diagram.getSelection().items[i];
		var change = new ChangeItemCommand(diagram, item);
		item.setTextAlignment(Alignment.Near);
		diagram.executeCommand(change);
	}
	diagram.commitCompositeOperation();
}

function onCenter()
{
	var ChangeItemCommand = MindFusion.Diagramming.ChangeItemCommand;

	diagram.startCompositeOperation();
	for (var i = 0; i < diagram.getSelection().items.length; i++)
	{
		var item = diagram.getSelection().items[i];
		var change = new ChangeItemCommand(diagram, item);
		item.setTextAlignment(Alignment.Center);
		diagram.executeCommand(change);
	}
	diagram.commitCompositeOperation();
}

function onRight()
{
	var ChangeItemCommand = MindFusion.Diagramming.ChangeItemCommand;

	diagram.startCompositeOperation();
	for (var i = 0; i < diagram.getSelection().items.length; i++)
	{
		var item = diagram.getSelection().items[i];
		var change = new ChangeItemCommand(diagram, item);
		item.setTextAlignment(Alignment.Far);
		diagram.executeCommand(change);
	}
	diagram.commitCompositeOperation();
}

function onTop()
{
	var ChangeItemCommand = MindFusion.Diagramming.ChangeItemCommand;

	diagram.startCompositeOperation();
	for (var i = 0; i < diagram.getSelection().items.length; i++)
	{
		var item = diagram.getSelection().items[i];
		var change = new ChangeItemCommand(diagram, item);
		item.setLineAlignment(Alignment.Near);
		diagram.executeCommand(change);
	}
	diagram.commitCompositeOperation();
}

function onMiddle()
{
	var ChangeItemCommand = MindFusion.Diagramming.ChangeItemCommand;

	diagram.startCompositeOperation();
	for (var i = 0; i < diagram.getSelection().items.length; i++)
	{
		var item = diagram.getSelection().items[i];
		var change = new ChangeItemCommand(diagram, item);
		item.setLineAlignment(Alignment.Center);
		diagram.executeCommand(change);
	}
	diagram.commitCompositeOperation();
}

function onBottom()
{
	diagram.startCompositeOperation();
	for (var i = 0; i < diagram.getSelection().items.length; i++)
	{
		var item = diagram.getSelection().items[i];
		var change = new ChangeItemCommand(diagram, item);
		item.setLineAlignment(Alignment.Far);
		diagram.executeCommand(change);
	}
	diagram.commitCompositeOperation();
}


function arrange()
{
	var lLayout = new MindFusion.Graphs.LayeredLayout();
	diagram.arrange(lLayout);
	diagram.routeAllLinks();
	
	var cBounds = diagram.getContentBounds();
	
	if(cBounds.width > diagram.getBounds().width || cBounds.height > diagram.getBounds().height)
	diagram.resizeToFitItems();	
	//diagram.setBounds(new Rect(0, 0, 297,210));
}

function clearItems()
{
	diagram.clearAll();
}

function save()
{
	localStorage.setItem('jsDiagram', diagram.toJson());
}

function load()
{
	var diagramString = localStorage.getItem('jsDiagram');
	if(diagramString)
		diagram.fromJson(diagramString);
}


function updateBackground(event) {
   backgroundColor = event.target.value;
   var selectedItem = diagram.selection.items[0];
		if(selectedItem)
			selectedItem.setBrush({ type: 'SolidBrush', color: backgroundColor });  
}

function onSequence()
{
	var DashStyle = MindFusion.Drawing.DashStyle;
	var btnSrc = document.getElementById("sequence").src; 	
	linkDashStyle = DashStyle.Solid;
	headShape = "Triangle";
	baseShape = null;
	headBrush = "#7F7F7F";
	document.getElementById("sequence").src = "sequenceOn.png";
	document.getElementById("message").src = "messageOff.png";
	document.getElementById("association").src = "associationOff.png";
}

function onMessage()
{
	var DashStyle = MindFusion.Drawing.DashStyle;
	var btnSrc = document.getElementById("message").src; 
	linkDashStyle = DashStyle.Dash;
	headShape = "Triangle";
	baseShape = "Circle";
	headBrush = "white";
	document.getElementById("message").src = "messageOn.png";
	document.getElementById("sequence").src = "sequenceOff.png";
	document.getElementById("association").src = "associationOff.png";
		
}

function onAssociation()
{
	var DashStyle = MindFusion.Drawing.DashStyle;
	var btnSrc = document.getElementById("association").src; 
	linkDashStyle = DashStyle.Dash;
	headShape = null;
	baseShape = null;
	document.getElementById("association").src = "associationOn.png";
	document.getElementById("sequence").src = "sequenceOff.png";
	document.getElementById("message").src = "messageOff.png";
		
}

/*function onNodeCreated(sender, args)
{
	var ContainerNode = MindFusion.Diagramming.ContainerNode;
	var node = args.getNode();
	node.setBrush({ type: 'SolidBrush', color: backgroundColor });
	
	
	if( node instanceof ContainerNode )
	{
		node.setCaptionBackBrush({ type: 'SolidBrush', color: backgroundColor });
	    node.setBrush({ type: 'SolidBrush', color: '#ffffff' });
	}		
	
}
*/

/*function onLinkCreated(sender, args)
{
	var link = args.getLink();
	link.setStrokeDashStyle (linkDashStyle);
	link.setHeadShape(headShape);
	link.setBaseShape(baseShape);
	link.setHeadShapeSize(3.0);
	link.setBaseShapeSize(3.0);
	link.setHeadBrush({ type: 'SolidBrush', color: headBrush });
	link.setBaseBrush({ type: 'SolidBrush', color: '#FFFFFF' });
	link.setTextAlignment(MindFusion.Diagramming.Alignment.Near);
}*/