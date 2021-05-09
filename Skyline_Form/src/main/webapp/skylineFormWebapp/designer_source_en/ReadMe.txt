SpreadJS Online Designer v13 Source
------------------------------------
 
Summary
-------
The Online Designer is based on HTML and is a pure client-side web application.
 
Component
---------
3rd party JavaScript libraries:
1. Knockout: Used for data-binding and to support resource localization.
2. JQuery: Used to operate HTML DOM element.
3. JQuery UI: Provides menu and dialog widgets for the UI.
4. File Saver: Used to save the designed product locally.
5. Z-Tree: Provides the tree structure appearance and behavior.
6. Spread.Sheets: A pure client-side web JavaScript control that provides the Excel-like spreadsheet functionality.
7. Spread.Sheets Client Side Excel IO: Provides the pure client-side Excel file import/export functionality.
 
Built-in JavaScript component:
1. Ribbon: Provides Excel-like UI and commands (./src/ribbon folder);
2. Spread.Sheets Designer Wrapper: The Spread.Sheets wrapper (./src/spreadWrapper folder);
3. Color Picker: The widget used to customize color (./src/widgets/colorpicker folder);
4. Border Picker: The widget used to customize sheets border (./src/widgets/borderpicker folder)；
5. Status Bar: The widget used to show sheet status (./src/statusBar folder);
6. Others ...
 
Development
-----------
Spread.Sheets is implemented with pure client-side technology and can run in HTML5-supported browsers without a server side plug-in.
 
Browser limitation
------------------
Based on web applications, there are some limitations for local access, so an HTML5-supported browser is necessary for FILE API. (IE 10+, Chrome, FireFox).
 
Localization
------------
1.All the resources are defined in "./common/resources.js".
 
How to use
----------
1. Copy gc.spread.sheets.all.*.*.*.min.js to "./src/lib/spread".
2. Copy gc.spread.sheets.excel2013white.*.*.*.css to "./src/lib/spread".
3. Copy gc.spread.sheets.print.*.*.*.min.js to "./src/lib/spread/pluggable".
4. Copy gc.spread.excelio.*.*.*.js to "./src/lib/spread/interop".
5. Deploy the whole src folder to web server. such as "localhost:xxx/designer".
6. Access "http://localhost:xxx/designer/index.html".