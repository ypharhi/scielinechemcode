package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import com.skyline.form.entity.Element;

/**
 *  The Element use WEB_INF/lib/JDiagram.jar file.
 *  Official Website: https://www.mindfusion.eu/
 *  Support: https://www.mindfusion.eu/HelpDesk/index.php
 *  Documentation: https://www.mindfusion.eu/onlinehelp/jdiagram/index.htm
 *  
 *  Diagram is not saved when it is hidden.
 *  On save function the next data are saved in the DB(fg_diagram table):
 *  	- jsonObject: to save full state of the canvas;
 *  	- IMAGE: jpg of the canvas. The image is saved as a pointer in the fg_diagram to the fg_files table that contains the blob data
 *  The attribute elementID is newly rendered on every save(TODO:should change this behavior to create a new elementID only when data has changed)
 */

public class ElementDiagramImp extends Element {

	public ElementDiagramImp() {
		// TODO Auto-generated constructor stub
	}
	
	private String width, height;

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{						
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				height = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "height");

				return "";
			}
			return "Creation failed";
		}
		catch (Exception e) {
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();
		
		String width_ = width;
		String height_ = height;
		width_ = (width.equals("")) ? "800px" : (width.indexOf("%") != -1) || (width.indexOf("px") != -1) ? width: width + "px;";
		height_ = (height.equals("")) ? "200px" : (height.indexOf("%") != -1) || (height.indexOf("px") != -1) ? height: height + "px;";		
		String hidden = (isHidden)? "visibility:hidden;display:none;":"";
		
		String disabled = (isDisabled) ? " class=\"disabledDiagram\" " : "";
		String savedCanvas = value.equals("") ? "" : generalUtilFormState.getDiagramContent(value);

		html.put(layoutBookMark,
				" <div id=\"" + domId + "\" elementID=\"" + value 
									+ "\" name=\"parentDiagramContainer\" style=\"" + hidden +";overflow:visible;height:"+height_+";width:"+width_+"; margin: 1px; padding: 0px;    border: 1px solid #bdbdbd;"
						+ "\" element=\"" + this.getClass().getSimpleName() + "\" "
						+ inputAttribute + disabled + ">" 
				//+ "    <div id=\"content\" style=\"top: 60px; bottom: 24px;\">"
				+ "        <div class=\"diagramControls\" style=\"position: relative; left: 0px; top: 0px; bottom: 0px; width: 15%; border-right: 1px solid #e2e4e7;"
				+ "            overflow: hidden;height:100%\">"
				/*+ "            <div style=\"position: absolute; top: 0px; bottom: 0px; right: 0px; width: 100%;height:40%;"
				+ "                border-bottom: 1px solid #e2e4e7; background-color: #c0c0c0;\">"
				+ "                <canvas id=\""+domId+"_overview\" style=\"width:auto;height:auto\"></canvas>"
				+ "            </div>"*/
				+ "            <div style=\"position: absolute; top: 0px; left: 0px; right: 0px; bottom: 0px; width: 100%;"
				+ "                overflow: auto; background-color: #fafafa;\">"
				+ "                <canvas id=\""+domId+"_nodeList\"></canvas>"
				+ "            </div>"
				+ "       </div>"
				+ "        <div style=\"position: relative; left: 15%; top:0px; right: 0px; bottom: 0px;height:100%;width:85%;transform:translate(0px,-100%);\">"
				+ "            <div style=\"position: absolute; left: 0px; top: 0px; right: 0px; height: 32px; background-color: #fafafa;"
				+ "                border-bottom: 1px solid #e2e4e7; border-right: 1px solid #e2e4e7;\">"
				+ "                <div class=\"diagramToolBox\" style=\"overflow: hidden; height: 100%; margin: 1px; padding: 0px;\">"
				+ "                    <div class=\"toolButton\" onclick=\"onUndo()\" title=\"Undo\">"
				+ "                        <div style=\"background-position: 0px 0px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onRedo()\" title=\"Redo\">"
				+ "                        <div style=\"background-position: -16px 0px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onDelete()\" title=\"Delete\">"
				+ "                        <div style=\"background-position: -32px 0px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolSeparator\">"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onZoomIn()\" title=\"Zoom In\">"
				+ "                        <div style=\"background-position: -48px 0px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onZoomOut()\" title=\"Zoom Out\">"
				+ "                        <div style=\"background-position: -64px 0px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onResetZoom()\" title=\"No Zoom\">"
				+ "                        <div style=\"background-position: -80px 0px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolSeparator\">"
				+ "                    </div>"
				+ "                    <div style=\"float: left; padding-top: 1px;\">"
				+ "                        <select id=\"fontName\" class=\"toolSelect\" style=\"width: 100px;\">"
				+ "                            <option>Arial</option>"
				+ "                            <option>Courier New</option>"
				+ "                            <option>Georgia</option>"
				+ "                            <option>Times New Roman</option>"
				+ "                            <option>Trebuchet MS</option>"
				+ "                            <option>Verdana</option>"
				+ "                        </select>"
				+ "                    </div>"
				+ "                    <div style=\"float: left; padding-top: 1px;\">"
				+ "                        <select id=\"fontSize\" class=\"toolSelect\" style=\"width: 50px;\">"
				+ "                            <option>2</option>"
				+ "                            <option>3</option>"
				+ "                            <option selected=\"selected\">4</option>"
				+ "                            <option>5</option>"
				+ "                            <option>6</option>"
				+ "                            <option>7</option>"
				+ "                            <option>8</option>"
				+ "                            <option>9</option>"
				+ "                        </select>"
				+ "                    </div>"
				+ "	                   <div class=\"toolButton\" onclick=\"onBold()\" title=\"Bold\">"
				+ "                        <div style=\"background-position: -96px 0px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onItalic()\" title=\"Italic\">"
				+ "                        <div style=\"background-position: -112px 0px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onUnderlined()\" title=\"Underlined\">"
				+ "                        <div style=\"background-position: -128px 0px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolSeparator\">"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onLeft()\" title=\"Left\">"
				+ "                        <div style=\"background-position: 0px -16px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onCenter()\" title=\"Center\">"
				+ "                        <div style=\"background-position: -16px -16px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onRight()\" title=\"Right\">"
				+ "                        <div style=\"background-position: -32px -16px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onTop()\" title=\"Top\">"
				+ "                        <div style=\"background-position: -48px -16px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onMiddle()\" title=\"Middle\">"
				+ "                        <div style=\"background-position: -64px -16px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolButton\" onclick=\"onBottom()\" title=\"Bottom\">"
				+ "                        <div style=\"background-position: -80px -16px;\">"
				+ "                        </div>"
				+ "                    </div>"
				+ "                    <div class=\"toolSeparator\">"
				+ "                    </div>"
				+ "                </div>"
				+ "            </div>"
				+ "            <div style=\"position: absolute; left: 0px; top: 33px; right: 0px; bottom: 0px; overflow: auto\">"
				+ "                <canvas id=\""+domId+"_diagram\" style=\"width:auto;height:auto\" >"//width=\"2100\" height=\"2100\
				//+ "                This page requires a browser that supports HTML 5 Canvas element."
				+                   savedCanvas
				+ "                </canvas>"
				+ "            </div>"
				+ "        </div>"
				//+ "    </div>"
				+"</div>");
				
				
				/*"<div class=\"content\" id=\"" + domId + "\" elementID=\"" + value 
									+ "\" style=\"" + hidden +";overflow:visible;height:100%; margin: 1px; padding: 0px;"
						+ "\" element=\"" + this.getClass().getSimpleName() + "\" "
						+ inputAttribute + disabled + ">" 
					+"<div style=\"position: absolute; left: 0px; top: 90px; bottom: 0px; width: 200px; border-right: 1px solid #e2e4e7;"
					+ "	overflow: hidden; vertical-align: top;\">"
					// "<!-- The NodeListView component is bound to the canvas element below -->"
					+ "		<div style=\"width: 200px; height: 100%; overflow-y: auto; overflow-x: hidden; position: absolute;"
					+ "					top: 5px; left: 0px; right: 0px; bottom: 0px;\">"
					+ "			<canvas id=\"nodeList\" width=\"200\">"
					+ "			</canvas>"
					+ "		</div>"
					+ "</div>"
					//<!-- The Diagram component is bound to the canvas element below -->
					+"<div style=\"position: absolute; left: 200px; top: 90px; right: 0px; bottom: 0px; overflow: auto;\">"
					+ "			 <div style=\"height: 40px; width: 100%; margin: 3px 0px 0px 10px; padding: 0px;\">"
					+ "			 <button title=\"New links will be drawn as sequence\" onclick=\"onSequence()\" style=\"padding: 0px; height: 26px;width:60px;\"><img id=\"sequence\" src=\"sequenceOn.png\" width=\"60\" height=\"20\"></button>"
					+ "				   <button  title=\"New links will be drawn as messages\" onclick=\"onMessage()\" style=\"padding: 0px; height: 26px; width:60px;\"><img id=\"message\" src=\"messageOff.png\" width=\"60\" height=\"20\"></button>"
					+ "				   <button title=\"New links will be drawn as associations\" onclick=\"onAssociation()\" style=\"padding: 0px; height: 26px; width:60px;\"><img id=\"association\" src=\"associationOff.png\" width=\"60\" height=\"20\"></button>"
					+ "				   <input type=\"color\" title=\"Choose node background\" value=\"#88b663\" id=\"colorBkgr\">	"
					+ "  		       <button type=\"button\" title=\"Clear all items\" onclick=\"clearItems()\" style=\"padding: 3px 20px; vertical-align: top; border: 1px solid #cecece;\">Clear Items</button>"
					+ "				   <button type=\"button\" title=\"Arrange\" onclick=\"arrange()\" style=\"padding: 3px 20px; vertical-align: top; border: 1px solid #cecece;\">Arrange</button>"
					+ "				   <button type=\"button\" title=\"Save to local storage\" onclick=\"save()\" style=\"padding: 3px 20px; vertical-align: top; border: 1px solid #cecece;\">Save</button>"
					+ "				   <button type=\"button\" title=\"Load from local storage\" onclick=\"load()\" style=\"padding: 3px 20px; vertical-align: top; border: 1px solid #cecece;\">Load</button>"
					+ "			</div>"
					+ "			<div style=\"overflow: visible; height: 100%; margin: 1px; padding: 0px;\">"
					+ "	           <canvas id=\"diagram\" width=\"2100\" height=\"2100\">"
					+ "             This page requires a browser that supports HTML 5 Canvas element."
					+ "            </canvas>"
					+ "         </div>"
					+ "</div>"
				+ "</div>");*/

		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'','domId':'" + domId + "','type':'chemdoodle'});";
		return htmlBody;
	}
	
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 
				
				"	width:{  \r\n" + 
				"      		type:'string',\r\n" + 
				"      		title:'Width'\r\n" +
				"   },\r\n" + 
				"	height:{  \r\n" + 
				"      		type:'string',\r\n" + 
				"      		title:'Height'\r\n" +
				"   },\r\n" +
				"	singleMolecule:{\r\n" + 
				" 			type: 'boolean',\r\n" + 
				" 			title: 'Single Molecule (not implemented)'\r\n" + 
				"   },\r\n" +
				"	includeToolbar:{\r\n" + 
				" 			type: 'boolean',\r\n" + 
				" 			title: 'Include Toolbar (not implemented)'\r\n" + 
				"   }\r\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}

}
