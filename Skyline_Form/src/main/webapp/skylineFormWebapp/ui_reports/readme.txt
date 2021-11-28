Please notes, that in case the whole folder updated/replaced, there should be provided some changes:
- in the file 'main.js' find the next text: '/GeneralUX/skylineForm/getUIReportData.request' and replace it with '../skylineForm/getUIReportData.request'.
- in the file index.html should be compared with file already exists in 'skylineFormWebapp/jsp/webix_reports_main.jsp' in case there were changes.

changes main.js /webix.js
1) use COMPLY_reportTitleVar_ var (evaluate in webix_reports_main.jsp) - it holds the report title (after generate) - and we add it to the file name
  in the code its: 	"...e.filename||COMPLY_reportTitleVar_   ..." in some places
2) call addWebixTooltip("COMPLY_afterScroll") / addWebixTooltip("COMPLY_generate_report") to add tool-tip to report cell