(function () {
    'use strict';

    var designer = GC.Spread.Sheets.Designer;
    var en_res = {};


    en_res.title = "SpreadJS Designer";
    en_res.defaultFont = "11pt Calibri";
    en_res.ok = "OK";
    en_res.yes = "Yes";
    en_res.no = "No";
    en_res.apply = "Apply";
    en_res.cancel = "Cancel";
    en_res.close = "Close";
    en_res.fileAPINotSupported = "Browsers don't support File API";
    en_res.blobNotSupported = "Browsers don't support Blob object";

    en_res.saveFileDialogTitle = "Save As";
    en_res.openFileDialogTitle = "Open";
    en_res.allSpreadFileFilter = 'All Spreadsheet files (*.ssjson *.xlsx)';
    en_res.spreadFileFilter = 'SpreadJS files (*.ssjson)';
    en_res.ssJSONToJSFilter = 'Javascript files (*.js)';
    en_res.allExcelFilter = "All Excel files (*.xlsx)";
    en_res.excelFileFilter = 'Excel Workbook (*.xlsx)';
    en_res.csvFileFilter = "CSV Files (*.csv)";
    en_res.pdfFileFilter = "PDF Files (*.pdf)";
    en_res.allFileFilter = 'All Files (*.*)';
    en_res.importFileDialogTitle = "Import";
    en_res.exportFileDialogTitle = "Export";

    en_res.insertCellInSheet = "Cannot shift cells off of whole sheet";
    en_res.insertCellInMixtureRange = "This command cannot be used with selections that contain entire rows, or columns,and also other cells. Try select only entire rows, entire columns, or just groups of cells.";
    en_res.NotExecInMultiRanges = "The command you chose cannot be performed with multiple selections. Select a single range and click the command again";
    en_res.unsavedWarning = "The file has not been saved. Save it? ";
    en_res.errorGroup = "Sheet has outline-column , Whether to continue operation ?";

    en_res.requestTemplateFail = "Template file request error.";
    en_res.requestTemplateConfigFail = "Template config file request error.";
    en_res.openFileFormatError = "File format is not correct.";

    en_res.closingNotification = "Warning: Current file has been modified.\nDo you want to save your changes?";


    en_res.sameSlicerName = "Slicer name already in use. Please enter a unique name.";
    en_res.nullSlicerName = "Slicer name is not valid.";

    en_res.changePartOfArrayWarning = "Can not change part of an array.";
    en_res.changePartOfTableWarning = "This won't work because it would move cells in a table on your worksheet.";
    en_res.exportCsvSheetIndexError = "Sheet is not found.";

    en_res.fontPicker = {
        familyLabelText: 'Font:',
        styleLabelText: 'Font style:',
        sizeLabelText: 'Size:',
        weightLabelText: 'Weight:',
        colorLabelText: 'Color:',
        normalFontLabelText: 'Normal font',
        previewLabelText: 'Preview',
        previewText: 'AaBbCcYyZz',
        effects: "Effects",
        underline: "Underline",
        doubleUnderline: "Double Underline",
        strikethrough: "Strikethrough",
        //
        // Fonts shown in font selector.
        //
        // the property's name means the font family name.
        // the property's value means the text shown in drop down list.
        //
        fontFamilies: {
            "Arial": "Arial",
            "'Arial Black'": "Arial Black",
            "Calibri": "Calibri",
            "Cambria": "Cambria",
            "Candara": "Candara",
            "Century": "Century",
            "'Courier New'": "Courier New",
            "'Comic Sans MS'": "Comic Sans MS",
            "Garamond": "Garamond",
            "Georgia": "Georgia",
            "'Malgun Gothic'": "Malgun Gothic",
            "Mangal": "Mangal",
            "Tahoma": "Tahoma",
            "Times": "Times",
            "'Times New Roman'": "Times New Roman",
            "'Trebuchet MS'": "Trebuchet MS",
            "Verdana": "Verdana",
            "Wingdings": "Wingdings",
            "Meiryo": "Meiryo",
            "'MS Gothic'": "MS Gothic",
            "'MS Mincho'": "MS Mincho",
            "'MS PGothic'": "MS PGothic",
            "'MS PMincho'": "MS PMincho"
        },
        fontStyles: {
            'normal': 'Normal',
            'italic': 'Italic',
            'oblique': 'Oblique'
        },
        fontWeights: {
            'normal': 'Normal',
            'bold': 'Bold',
            'bolder': 'Bolder',
            'lighter': 'Lighter'
        },
        alternativeFonts: "Arial,'Segoe UI',Thonburi,Verdana,Sans-Serif",
        defaultSize: '10'
    };

    en_res.commonFormats = {
        Number: {
            format: "0.00",
            label: "Number"
        },
        Currency: {
            format: "$#,##0.00",
            label: "Currency"
        },
        Accounting: {
            format: "_($* #,##0.00_);_($* (#,##0.00);_($* \"-\"??_);_(@_)",
            label: "Accounting"
        },
        ShortDate: {
            format: "m/d/yyyy",
            label: "Short Date"
        },
        LongDate: {
            format: "dddd, mmmm dd, yyyy",
            label: "Long Date"
        },
        Time: {
            format: "h:mm:ss AM/PM",
            label: "Time"
        },
        Percentage: {
            format: "0.00%",
            label: "Percentage"
        },
        Fraction: {
            format: "# ?/?",
            label: "Fraction"
        },
        Scientific: {
            format: "0.00E+00",
            label: "Scientific"
        },
        Text: {
            format: "@",
            label: "Text"
        },
        Comma: {
            format: '_(* #,##0.00_);_(* (#,##0.00);_(* "-"??_);_(@_)',
            label: "Comma"
        }
    };
    en_res.customFormat = "Custom";
    en_res.generalFormat = "General";

    en_res.colorPicker = {
        themeColorsTitle: "Theme Colors",
        standardColorsTitle: "Standard Colors",
        noFillText: "No Color",
        moreColorsText: "More Colors...",
        colorDialogTitle: "Color",
        red: "Red: ",
        green: "Green: ",
        blue: "Blue: ",
        newLabel: "New",
        currentLabel: "Current"
    };

    en_res.formatDialog = {
        ellipsis: "Show Ellipsis",
        title: "Format Cells",
        number: 'Number',
        alignment: 'Alignment',
        fonts: "Fonts",
        font: 'Font',
        border: 'Border',
        padding: 'Padding',
        label: 'Label',
        cellContent: "Cell Content",
        labelContent: "Label Content",
        text: "Text",
        margin: "Margin",
        fill: 'Fill',
        protection: 'Protection',
        category: 'Category:',
        backColor: 'Background Color',
        textAlignment: 'Text alignment',
        horizontalAlignment: 'Horizontal:',
        verticalAlignment: 'Vertical:',
        indent: 'Indent:',
        degrees: 'Degrees',
        rotateText: "Text ",
        orientation: "Orientation",
        textControl: 'Text control',
        wrapText: 'Wrap text',
        shrink: 'Shrink to fit',
        merge: 'Merge cells',
        top: 'Top',
        bottom: 'Bottom',
        left: 'Left',
        right: 'Right',
        center: 'Center',
        general: 'General',
        sampleText: 'Text',
        cantMergeMessage: 'Cannot merge overlapping ranges.',
        lock: "Locked",
        lockComments: "Locking cells has no effect until you protect the worksheet(Home tab, Cells group, 'Protect Sheet' button in Format drop-down list).",
        backGroundColor: "Background Color:",
        moreColorsText: "More Colors",
        sample: "Sample",
        preview: "Preview",
        paddingPreviewText: "Content",
        visibility: "Visibility",
        labelVisibility: {
            visible: "Visible",
            hidden: "Hidden",
            auto: "Auto"
        },
        vertical: "Vertical text",
        cellButton: "Cell Buttons",
        addButton: "Add",
        deleteButton: "Delete",
        cellButtonImageType: "Image Type",
        cellButtonCommand: "Command",
        cellButtonUserButtonStyle: "UseButtonStyle",
        cellButtonVisibility: "Visibility",
        cellButtonPosition: "Position",
        cellButtonEnable: "Enable",
        cellButtonWidth: "Width",
        cellButtonCaption: "Caption",
        cellButtonImageSrc: "Image Src",
        cellButtonImageLoad: "Load",
        cellButtonCaptionAlign: "CaptionAlign",
        cellButtonImageWidth: "ImageWidth",
        cellButtonImageHeight: "ImageHeight",
        cellButtonCommands: {
            openColorPicker: "Color Picker",
            openDateTimePicker: "DateTime Picker",
            openTimePicker: "Time  Picker",
            openCalculator: "Calculator",
            openMonthPicker: "Month  Picker",
            openList: "List",
            openSlider: "Slider",
            openWorkflowList: "WorkflowList",
        },
        cellButtonImageTypes: {
            custom: "Custom",
            clear: "Clear",
            cancel: "Cancel",
            ok: "OK",
            dropdown: "Dropdown",
            ellipsis: "Ellipsis",
            left: "Left",
            right: "Right",
            plus: "Plus",
            minus: "Minus",
            undo: "Undo",
            redo: "Redo",
            search: "Search",
            separator: "Separator",
            spinLeft: "SpinLeft",
            spinRight: "SpinRight",
        },
        cellButtonVisibilitys: {
            always: "Alway",
            onseleciton: "On Selection",
            onedit: "On Edit"
        },
    };
    en_res.dropdownDialog = {
        width: "Width",
        height: "Height",
        value: "Value",
        text: "Text",
        min: "Min",
        max: "Max",
        step: "Step",
        direction: "Direction",
        horizontal: "Horizontal",
        vertical: "Vertical",
        list: {
            title: "List",
            hasChild: "Has Children",
            wrap: "Wrap",
            displayAs: "DisplayAs",
            inline: "Inline",
            popup: "Popup",
            tree: "Tree",
            collapsible: "Collapsible",
            icon: "Icon",
            isBigIcon: "IsBigIcon",
            multiSelect: "Multi-select",
            valueType: "Value Type:",
            string: "String",
            array: "Array",
        load: "Load",
        ok: "OK",
        cancel: "Cancel"
        },
        datetimepicker: {
            title: "DateTime Picker",
            startDay: "StartDay",
            monday: "Monday",
            tuesday: "Tuesday",
            wednesday: "Wednesday",
            thursday: "Thursday",
            friday: "Friday",
            saturday: "Saturday",
            sunday: "Sunday",
            calendarPage: "CalendarPage",
            day: "Day",
            year: "Year",
            month: "Month",
            showTime: "ShowTime"
        },
        timepicker: {
            title: "Time Picker",
            hour: "Hour",
            minute: "Minute",
            second: "Second",
            format: "Format",
            formatters: [
                "[$-409]h:mm:ss AM/PM",
                "h:mm;@",
                "[$-409]h:mm AM/PM;@",
                "h:mm:ss;@",
                "[$-409]h:mm:ss AM/PM;@",
                "mm:ss.0;@",
                "[h]:mm:ss;@",
                "[$-409]m/d/yy h:mm AM/PM;@",
                "m/d/yy h:mm;@",
                'h"时"mm"分";@',
                'h"时"mm"分"ss"秒";@',
                '[$-804]AM/PM h"时"mm"分";;@',
                '[$-804]AM/PM h"时"mm"分"ss"秒";@',
                '[DBNum1][$-804]h"时"mm"分";@',
                '[DBNum1][$-804]AM/PM h"时"mm"分";@',
                'h"時"mm"分";@',
                'h"時"mm"分"ss"秒";@',
                "[$-412]AM/PM h:mm;@",
                "[$-412]AM/PM h:mm:ss;@",
                "[$-409]h:mm AM/PM;@",
                "[$-409]h:mm:ss AM/PM;@",
                'yyyy"-"m"-"d h:mm;@',
                '[$-412]yyyy"-"m"-"d AM/PM h:mm;@',
                '[$-409]yyyy"-"m"-"d h:mm AM/PM;@',
                'h"시" mm"분";@',
                'h"시" mm"분" ss"초";@',
                '[$-412]AM/PM h"시" mm"분";@',
                '[$-412]AM/PM h"시" mm"분" ss"초";@'
            ]
        },
        monthpicker: {
            title: "Month Picker",
            startYear: "StartYear",
            stopYear: "StopYear",
        },
        slider: {
            title: "Slider",
            scaleVisible: "ScaleVisible",
            tooltipVisible: "TooltipVisible",
            marks: "Marks",
            formatter: "Format",
            formatters: [
                "0",
                "0.00",
                "#,##0",
                "#,##0.00",
                "#,##0;(#,##0)",
                "#,##0.00;(#,##0.00)",
                "$#,##0;($#,##0)",
                "$#,##0.00;($#,##0.00)",
                "0%",
                "0.00%",
                "0.00E+00",
                "##0.0E+0",
                ' $* #,##0.00 ; $* #,##0.00 ; $* "-" ; @ ',
                '_-[$¥-804]* #,##0.00_-;-[$¥-804]* #,##0.00_-;_-[$¥-804]* "-"_-;_-@_-',
                '_-[$¥-411]* #,##0.00_-;-[$¥-411]* #,##0.00_-;_-[$¥-411]* "-"_-;_-@_-',
                '_-[$₩-412]* #,##0.00_-;-[$₩-412]* #,##0.00_-;_-[$₩-412]* "-"_-;_-@_-'
            ]
        },
        workflowlist: {
            title: "Workflow List",
            transitions: "Transitions",
            statusSetting: "Status Settings",
            processFlow: "Process Flow"
        },
        colorpicker: {
            title: "Color Picker",
            colorWidth: "Block Width",
            themeColor: "Theme Color",
            standardColor: "Standard Color"
        }
    };

    en_res.formatComment = {
        title: "Format Comment",
        protection: "Protection",
        commentLocked: "Locked",
        commentLockText: "Lock text",
        commentLockComments: "Locking objects has no effect unless the sheet is protected. To help protect the sheet, choose Format on the Home tab, and then choose Protect Sheet.",
        properties: "Properties",
        positioning: "Object positioning",
        internalMargin: "Internal margin",
        moveSize: "Move and size with cells",
        moveNoSize: "Move but don't size with cells",
        noMoveSize: "Don't move or size with cells",
        automatic: "Automatic",
        autoSize: "Automatic size",
        colors: "Colors and Lines",
        size: "Size",
        fill: "Fill",
        line: "Line",
        height: "Height",
        width: "Width",
        lockRatio: "Lock aspect ratio",
        color: "Color",
        transparency: "Transparency",
        style: "Style",
        dotted: "Dotted",
        dashed: "Dashed",
        solid: "Solid",
        double: "Double",
        none: "None",
        groove: "Groove",
        ridge: "Ridge",
        inset: "Inset",
        outset: "Outset",
        px: "px"
    };

    en_res.categories = {
        general: "General",
        numbers: "Number",
        currency: "Currency",
        accounting: "Accounting",
        date: "Date",
        time: "Time",
        percentage: "Percentage",
        fraction: "Fraction",
        scientific: "Scientific",
        text: "Text",
        special: "Special",
        custom: "Custom"
    };

    en_res.formatNumberComments = {
        generalComments: "General format cells have no specific number format.",
        numberComments: "Number is used for general display of numbers. Currency and Accounting offer specialized formatting for monetary values.",
        currencyComments: "Currency formats are used for general monetary values. Use Accounting formats to align decimal points in a column.",
        accountingComments: "Accounting formats line up the currency symbols and decimal points in a column.",
        dateComments: "Date formats display date and time serial numbers as date values.",
        timeComments: "Time formats display date and time serial numbers as date values.",
        percentageComments: "Percentage formats multiply the cell value by 100 and display the result with a percent symbol.",
        textComments: "Text format cells are treated as text even when a number is in the cell. The cell is displayed exactly as entered.",
        specialComments: "Special formats are useful for tracking list and database values.",
        customComments: "Type the number format code, using one of the existing codes as a starting point."
    };

    en_res.formatNumberPickerSetting = {
        type: "Type:",
        decimalPlaces: "Decimal places:",
        symbol: "Symbol:",
        negativeNumber: "Negative numbers:",
        separator: "Use 1000 Separator(,)",
        deleted: "Delete",
        locale: "locale (location):",
        calendar: "Calendar type:",
        showEraFirstYear: "Use Gannen to display 1st year",
    };

    en_res.localeType = {
        en_us: "English(U.S.)",
        ja_jp: "Japanese",
        zh_cn: "Chinese",
        ko_kr: "Korean"
    };

    en_res.calendarType = {
        western: "Western",
        JER: "Japanese Emperor Reign"
    };

    en_res.fractionFormats = [
        "# ?/?",
        "# ??/??",
        "# ???/???",
        "# ?/2",
        "# ?/4",
        "# ?/8",
        "# ??/16",
        "# ?/10",
        "# ??/100"
    ];

    en_res.numberFormats = [
        "0",
        "0;[Red]0",
        "0_);(0)",
        "0_);[Red](0)",
        "#,##0",
        "#,##0;[Red]#,##0",
        "#,##0_);(#,##0)",
        "#,##0_);[Red](#,##0)"
    ];

    en_res.dateFormats = [
        "m/d/yyyy",
        "[$-409]dddd, mmmm dd, yyyy",
        "m/d;@",
        "m/d/yy;@",
        "mm/dd/yy;@",
        "[$-409]d-mmm;@",
        "[$-409]d-mmm-yy;@",
        "[$-409]dd-mmm-yy;@",
        "[$-409]mmm-yy;@",
        "[$-409]mmmm-yy;@",
        "[$-409]mmmm d, yyyy;@",
        "[$-409]m/d/yy h:mm AM/PM;@",
        "m/d/yy h:mm;@",
        "[$-409]mmmmm;@",
        "[$-409]mmmmm-yy;@",
        "m/d/yyyy;@",
        "[$-409]d-mmm-yyyy;@"
    ];

    en_res.chinaDateFormat = [
        "yyyy-mm-dd;@",
        '[DBNum1][$-804]yyyy"年"m"月"d"日";@',
        '[DBNum1][$-804]yyyy"年"m"月";@',
        '[DBNum1][$-804]m"月"d"日";@',
        "[$-409]yyyy/m/d h:mm AM/PM;@",
        'yyyy"年"m"月"d"日";@',
        'yyyy"年"m"月";@',
        'm"月"d"日";@',
        "mm/dd/yy;@",
        "m/d/yy;@",
        "yyyy/m/d h:mm AM/PM;@",
        "yyyy/m/d h:mm;@",
        "[$-409]d-mmm;@",
        "[$-409]d-mmm-yy;@",
        "[$-409]dd-mmm-yy;@",
        "[$-409]mmm-yy;@",
        "[$-409]m",
        "[$-409]m-d;@"
    ];
    en_res.koreanDateFormat = [
        "yyyy-mm-dd;@",
        'yyyy"년" m"월" d"일";@',
        'yy"年" m"月" d"日";@',
        'yyyy"년" m"월";@',
        'm"월" d"일";@',
        "yy-m-d;@",
        "yy-m-d h:mm;@",
        'm"月"d"日";@',
        "[$-412]yy-m-d AM/PM h:mm;@",
        "yy/m/d;@",
        "yyyy/m/d h:mm;@",
        "m/d;@",
        "m/d/yy;@",
        "mm/dd/yy;@",
        "[$-409]d-mmm;@",
        "[$-409]d-mmm-yy;@",
        "[$-409]dd-mmm-yy;@",
        "[$-409]mmm-yy;@",
        "[$-409]m",
        "[$-409]m-d;@"
    ];
    en_res.japanWesternDateFormat = [
        'yyyy"年"m"月"d"日";@',
        'yyyy"年"m"月";@',
        'm"月"d"日";@',
        "yyyy/m/d;@",
        "[$-409]yyyy/m/d h:mm AM/PM;@",
        "yyyy/m/d h:mm;@",
        "m/d;@",
        "m/d/yy;@",
        "mm/dd/yy;@",
        "[$-409]d-mmm;@",
        "[$-409]d-mmm-yy;@",
        "[$-409]dd-mmm-yy;@",
        "[$-409]mmm-yy;@",
        "[$-409]mmmm-yy;@",
        "[$-409]mmmmm;@",
        "[$-409]mmmmm-yy;@"
    ];

    en_res.japanEmperorReignDateFormat = [
        "[$-411]ge.m.d",
        '[$-411]ggge"年"m"月"d"日"'
    ];
    en_res.japanEmperorReignFirstYearDateFormat = [
        "[$-411]ge.m.d",
        '[$-ja-JP-x-gannen]ggge"年"m"月"d"日"'
    ];

    en_res.timeFormats = [
        "[$-409]h:mm:ss AM/PM",
        "h:mm;@",
        "[$-409]h:mm AM/PM;@",
        "h:mm:ss;@",
        "[$-409]h:mm:ss AM/PM;@",
        "mm:ss.0;@",
        "[h]:mm:ss;@",
        "[$-409]m/d/yy h:mm AM/PM;@",
        "m/d/yy h:mm;@"
    ];

    en_res.chinaTimeFormats = [
        "h:mm;@",
        "[$-409]h:mm AM/PM;@",
        "h:mm:ss;@",
        'h"时"mm"分";@',
        'h"时"mm"分"ss"秒";@',
        '[$-804]AM/PM h"时"mm"分";;@',
        '[$-804]AM/PM h"时"mm"分"ss"秒";@',
        '[DBNum1][$-804]h"时"mm"分";@',
        '[DBNum1][$-804]AM/PM h"时"mm"分";@'
    ];
    en_res.koreanTimeFormats = [
        "h:mm;@",
        "h:mm:ss;@",
        "[$-412]AM/PM h:mm;@",
        "[$-412]AM/PM h:mm:ss;@",
        "[$-409]h:mm AM/PM;@",
        "[$-409]h:mm:ss AM/PM;@",
        'yyyy"-"m"-"d h:mm;@',
        '[$-412]yyyy"-"m"-"d AM/PM h:mm;@',
        '[$-409]yyyy"-"m"-"d h:mm AM/PM;@',
        'h"시" mm"분";@',
        'h"시" mm"분" ss"초";@',
        '[$-412]AM/PM h"시" mm"분";@',
        '[$-412]AM/PM h"시" mm"분" ss"초";@'
    ];
    en_res.japanTimeFormats = [
        "h:mm;@",
        "[$-409]h:mm AM/PM;@",
        "h:mm:ss;@",
        "[$-409]h:mm:ss AM/PM;@",
        "[$-409]yyyy/m/d h:mm AM/PM;@",
        "yyyy/m/d h:mm;@",
        'h"時"mm"分";@',
        'h"時"mm"分"ss"秒";@'
    ];

    en_res.textFormats = [
        "@"
    ];

    en_res.specialFormats = [
        "00000",
        "00000-0000",
        "[<=9999999]###-####;(###) ###-####",
        "000-00-0000"
    ];

    en_res.specialJapanFormats = [
        "[<=999]000;[<=9999]000-00;000-0000",
        "[<=99999999]####-####;(00) ####-####",
        "'△' #,##0;'▲' #,##0",
        "[DBNum1][$-411]General",
        "[DBNum2][$-411]General",
        "[DBNum3][$-411]0",
        "[DBNum3][$-411]#,##0"
    ];

    en_res.specialKoreanFormats = [
        "000-000",
        "[<=999999]####-####;(0##) ####-####",
        "[<=9999999]###-####;(0##) ###-####",
        "000000-0000000",
        "[DBNum1][$-412]General",
        "[DBNum2][$-412]General",
        "[$-412]General"
    ];
    en_res.specialChinaFormats = [
        "000000",
        "[DBNum1][$-804]General",
        "[DBNum2][$-804]General"
    ];
    en_res.currencyFormats = [
        "#,##0",
        "#,##0;[Red]#,##0",
        "#,##0;-#,##0",
        "#,##0;[Red]-#,##0"
    ];

    en_res.percentageFormats = [
        "0%"
    ];

    en_res.scientificFormats = [
        "0E+00"
    ];

    en_res.accountingFormats = [
        '_(* #,##0_);_(* (#,##0);_(* \"-\"?_);_(@_)',
        '_($* #,##0_);_($* (#,##0);_($* \"-\"?_);_(@_)',
        '_ [$¥-804]* #,##0_ ;_ [$¥-804]* \\-#,##0_ ;_ [$¥-804]* "-"?_ ;_ @_ ',
        '_-[$¥-411]* #,##0_-;\\-[$¥-411]* #,##0_-;_-[$¥-411]* "-"?_-;_-@_-',
        '_-[$₩-412]* #,##0_-;\\-[$₩-412]* #,##0_-;_-[$₩-412]* "-"?_-;_-@_-'
    ];

    en_res.customFormats = [
        "General",
        "0",
        "0.00",
        "#,##0",
        "#,##0.00",
        "#,##0;(#,##0)",
        "#,##0;[Red](#,##0)",
        "#,##0.00;(#,##0.00)",
        "#,##0.00;[Red](#,##0.00)",
        "$#,##0;($#,##0)",
        "$#,##0;[Red]($#,##0)",
        "$#,##0.00;($#,##0.00)",
        "$#,##0.00;[Red]($#,##0.00)",
        "0%",
        "0.00%",
        "0.00E+00",
        "##0.0E+0",
        "# ?/?",
        "# ??/??",
        "m/d/yyyy",
        "d-mmm-yy",
        "d-mmm",
        "mmm-yy",
        "h:mm AM/PM",
        "h:mm:ss AM/PM",
        "hh:mm",
        "hh:mm:ss",
        "m/d/yyyy hh:mm",
        "mm:ss",
        "mm:ss.0",
        "@",
        "[h]:mm:ss",
        "$ #,##0;$ (#,##0);$ \"-\";@",
        " #,##0; (#,##0); \"-\";@",
        "$ #,##0.00;$ (#,##0.00);$ \"-\"??;@",
        " #,##0.00; (#,##0.00); \"-\"??;@",
        "hh:mm:ss",
        "00000",
        "# ???/???",
        "000-00-0000",
        "dddd, mmmm dd, yyyy",
        "m/d;@",
        "[<=9999999]###-####;(###) ###-####",
        "# ?/8"
    ];

    en_res.accountingSymbol = [
        ["None", null, null],
        ["$", "$", "en-US"],
        ["¥(Chinese)", "¥", "zh-cn"],
        ["¥(Japanese)", "¥", "ja-jp"],
        ["₩(Korean)", "₩", "ko-kr"]
    ];

    en_res.specialType = [
        "Zip Code",
        "Zip Code + 4",
        "Phone Number",
        "Social Security Number"
    ];

    en_res.specialJapanType = [
        "郵便番号",
        "電話番号（東京)",
        "正負記号 （+ = △; - = ▲)",
        "漢数字（十二万三千四百）",
        "大字 (壱拾弐萬参阡四百)",
        "全角 (１２３４５)",
        "全角 桁区切り（１２,３４５）"
    ];
    en_res.specialKoreanType = [
        "우편 번호",
        "전화 번호 (국번 4자리)",
        "전화 번호 (국번 3자리)",
        "주민등록번호",
        "숫자(한자)",
        "숫자(한자-갖은자)",
        "숫자(한글)"
    ];
    en_res.specialChinaType = [
        "邮政编码",
        "中文小写字母",
        "中文大写字母"
    ];

    en_res.fractionType = [
        "Up to one digit (1/4)",
        "Up to two digits (21/25)",
        "Up to three digits (312/943)",
        "As halves (1/2)",
        "As quarters (2/4)",
        "As eighths (4/8)",
        "As sixteenths (8/16)",
        "As tenths (3/10)",
        "As hundredths (30/100)"
    ];

    en_res.negativeNumbers = {
        "-1234.10": "-1234.10",
        "red:1234.10": "1234.10",
        "(1234.10)": "(1234.10)",
        "red:(1234.10)": "(1234.10)"
    };

    en_res.currencyNegativeNumbers = {
        "number1": "-1,234.10",
        "red:number2": "1,234.10",
        "number3": "-1,234.10",
        "red:number4": "-1,234.10"
    };

    en_res.passwordDialog = {
        title: "Password",
        passwordLabel: "Password:"
    };
    en_res.rowHeightDialog = {
        title: "Row Height",
        rowHeight: "Row height:",
        exception: "The row height must be a number or dynamic size (number with star like 3*)..",
        exception2: "The row height must between 0 and 9999999."
    };

    en_res.chart = {
        formatChartArea: "Format",
        properties: 'Properties',
        moveAndSizeWithCells: 'Move And Size With Cells',
        moveButDoNotSizeWithCells: 'Move But Do Not Size With Cells',
        locked: 'Locked',
        color: 'Color',
        transparency: 'Transparency',
        selectedOption: {
            series: 'Series Options',
            dataPoints: "Series Options",
            chartArea: 'Chart Options',
            chartTitle: 'Title Options',
            legend: 'Legend Options',
            label: 'Label Options',
            errorBar: 'Error Bar Options',
            trendline: 'Trendline Options',
            plotArea: 'Plot Area Options',
            dataLabels: 'Label Options',
            primaryCategory: 'Axis Options',
            primaryValue: 'Axis Options',
            primaryCategoryTitle: 'Title Options',
            primaryValueTitle: 'Title Options',
            primaryCategoryMajorGridLine: 'Major GridLines Options',
            primaryValueMajorGridLine: 'Major GridLines Options',
            primaryCategoryMinorGridLine: 'Minor GridLines Options',
            primaryValueMinorGridLine: 'Minor GridLines Options',
            primaryValueUnitsLabel: 'Label Options',
            secondaryCategory: 'Axis Options',
            secondaryValue: 'Axis Options',
            secondaryCategoryTitle: 'Title Options',
            secondaryValueTitle: 'Title Options',
            secondaryCategoryMajorGridLine: 'Major GridLines Options',
            secondaryValueMajorGridLine: 'Major GridLines Options',
            secondaryCategoryMinorGridLine: 'Minor GridLines Options',
            secondaryValueMinorGridLine: 'Minor GridLines Options',
            secondaryValueUnitsLabel: 'Label Options',
            dataLabels: 'Data Labels'
        },
        selectedText: {
            series: 'Series',
            errorBar: 'Error Bar',
            trendline: 'Trendline',
            dataPoints: 'Data Points',
            chartArea: 'Chart Area',
            chartTitle: 'Chart Title',
            legend: 'Legend',
            dataLabels: 'Data Labels',
            plotArea: 'Plot Area',
            primaryCategory: 'Horizontal (Category) Axis',
            primaryValue: 'Vertical (Value) Axis',
            primaryCategoryTitle: 'Horizontal (Category) Axis Title',
            primaryValueTitle: 'Vertical (Value) Axis Title',
            primaryCategoryMajorGridLine: 'Horizontal (Category) Axis Major GridLines',
            primaryValueMajorGridLine: 'Vertical (Value) Axis Major GridLines',
            primaryCategoryMinorGridLine: 'Horizontal (Category) Axis Minor GridLines',
            primaryValueMinorGridLine: 'Vertical (Value) Axis Minor GridLines',
            primaryValueUnitsLabel: 'Vertical (Value) Axis Display Units Label',
            secondaryCategory: 'Secondary Horizontal (Category) Axis',
            secondaryValue: 'Secondary Vertical (Value) Axis',
            secondaryCategoryTitle: 'Secondary Horizontal (Category) Axis Title',
            secondaryValueTitle: 'Secondary Vertical (Value) Axis Title',
            secondaryCategoryMajorGridLine: 'Secondary Horizontal (Category) Axis Major GridLines',
            secondaryValueMajorGridLine: 'Secondary Vertical (Value) Axis Major GridLines',
            secondaryCategoryMinorGridLine: 'Secondary Horizontal (Category) Axis Minor GridLines',
            secondaryValueMinorGridLine: 'Secondary Vertical (Value) Axis Minor GridLines',
            secondaryValueUnitsLabel: 'Secondary Vertical (Value) Axis Display Units Label'
        },
        selectedRadarChartText: {
            primaryCategory: 'Category Labels',
        },
        selectedBarChartText: {
            primaryCategory: 'Vertical (Category) Axis',
            primaryValue: 'Horizontal (Value) Axis',
            primaryCategoryTitle: 'Vertical (Category) Axis Title',
            primaryValueTitle: 'Horizontal (Value) Axis Title',
            primaryCategoryMajorGridLine: 'Vertical (Category) Axis Major GridLines',
            primaryValueMajorGridLine: 'Horizontal (Value) Axis Major GridLines',
            primaryCategoryMinorGridLine: 'Vertical (Category) Axis Minor GridLines',
            primaryValueMinorGridLine: 'Horizontal (Value) Axis Minor GridLines',
            primaryValueUnitsLabel: 'Horizontal (Value) Axis Display Units Label',
            secondaryCategory: 'Secondary Vertical (Category) Axis',
            secondaryValue: 'Secondary Horizontal (Value) Axis',
            secondaryCategoryTitle: 'Secondary Vertical (Category) Axis Title',
            secondaryValueTitle: 'Secondary Horizontal (Value) Axis Title',
            secondaryCategoryMajorGridLine: 'Secondary Vertical (Category) Axis Major GridLines',
            secondaryValueMajorGridLine: 'Secondary Horizontal (Value) Axis Major GridLines',
            secondaryCategoryMinorGridLine: 'Secondary Vertical (Category) Axis Minor GridLines',
            secondaryValueMinorGridLine: 'Secondary Horizontal (Value) Axis Minor GridLines',
            secondaryValueUnitsLabel: 'Secondary Horizontal (Value) Axis Display Units Label'
        },
        formatChart: {
            dataSeries: ' Data Series',
            errorBar: ' Error Bars',
            trendline: ' Trendlines',
            dataPoints: ' Data Points',
            axis: ' Axis',
            legend: ' Legend',
            dataLable: ' Data Label',
            chartTitle: ' Chart Title',
            plotArea: ' Plot Area',
            chartArea: ' Chart Area',
            unitsLabel: ' Display Units Label'
        }
    };

    en_res.chartSliderPanel = {
        tick: {
            cross: "Cross",
            inside: 'Inside',
            none: 'None',
            outSide: 'OutSide'
        },
        axisFormat: {
            General: "General",
            Number: "Number",
            Currency: "Currency",
            Accounting: "Accounting",
            Date: "Date",
            Time: "Time",
            Percentage: "Percentage",
            Fraction: "Fraction",
            Scientific: "Scientific",
            Text: "Text",
            Special: "Special",
            Custom: "Custom",
            Add: "Add",
            formatCode: "Format Code",
            category: "Category"
        },
        noLine: 'No Line',
        solidLine: "Solid Line",
        width: "Width",
        fontFamily: "Font Family",
        fontSize: "Font Size",
        noFill: 'No Fill',
        solidFill: "Solid Fill",
        auto: "Auto",
        reset: "Reset",
        automatic: 'Automatic',
        custom: 'Custom',
        color: 'Color',
        text: 'Text',
        majorType: 'Major type',
        minorType: 'Minor type',
        textAxis: 'Text Axis',
        dateAxis: 'Date Axis',
        unitsMajor: 'Units Major',
        unitsMinor: 'Units Minor',
        dateUnitsMajor: 'Major',
        dateUnitsMinor: 'Minor',
        dateBaseUnit: 'Base',
        maximum: 'Maximum',
        minimum: 'Minimum',
        height: 'Height',
        top: 'Top',
        bottom: 'Bottom',
        left: 'Left',
        right: 'Right',
        topRight: 'Top Right',
        primaryAxis: 'Primary Axis',
        secondaryAxis: 'Secondary Axis',
        tickMarks: 'Tick Marks',
        axisOptions: 'Axis Options',
        line: 'Line',
        font: 'Font',
        textFill: 'Text Fill',
        textEditor: 'Text Editor',
        size: 'Size',
        fill: 'Fill',
        legendPosition: 'Legend Position',
        seriesOptions: 'Series Options',
        border: 'Border',
        transparency: 'Transparency',
        none: 'None',
        builtIn: 'Built-in',
        shape: 'Shape',
        lintType: 'Dash type',
        markOptions: 'Marker Options',
        markFill: 'Marker Fill',
        markBorder: 'Marker Border',
        logarithmicScale: 'logarithmic Scale',
        logBase: 'Base',
        dashStyle: 'Dash type',
        exponential: 'Exponential',
        linear: 'Linear',
        logarithmic: 'Logarithmic',
        polynomial: 'Polynomial',
        power: 'Power',
        movingAverage: 'Moving Average',
        verticalErroeBar: 'Vertical Error Bar',
        horizontalErrorBar: 'Horizontal Error Bar',
        both: 'Both',
        minus: 'Minus',
        plus: 'Plus',
        noCap: 'No Cap',
        cap: 'Cap',
        fixed: 'Fixed Value',
        percentage: 'Percentage',
        standardDev: 'Standard Deviations',
        standardErr: 'Standard Error',
        specifyValue: 'Specify Value',
        direction: 'Direction',
        endStyle: 'End Style',
        errorAmount: 'Error Amount',
        bounds: "Bounds",
        units: "Units",
        dateScales: {
            day: "Day",
            month: "Month",
            year: "Year"
        },
        displayUnits: "Display Units",
        displayUnit: {
            none: "None",
            hundreds: "Hundreds",
            thousands: "Thousands",
            tenThousands: "10,000",
            hundredThousands: "100,000",
            millions: "Millions",
            tenMillions: "10,000,000",
            hundredMillions: "100,000,000",
            billions: "Billions",
            trillions: "Trillions",
        },
        showDisplayUnitsLabel: "Show display units label on chart",
        trendline: {
            exponential: 'Exponential',
            linear: 'Linear',
            logarithmic: 'Logarithmic',
            polynomial: 'Polynomial',
            power: 'Power',
            movingAverage: 'Moving Average',
            name: "Trendline Name",
            forecast: "Forecast",
            forward: "Forward",
            backward: "Backward",
            intercept: "Set Intercept",
            displayEquation: "Display Equation on chart",
            displayRSquared: "Display R-squared value on chart",
        },
        legend: {
            layout: 'Legend Layout',
            x: 'Legend X(% of Chart)',
            y: 'Legend Y(% of Chart)',
            width: 'Legend Width(% of Chart)',
            height: 'Legend Height(% of Chart)',
            overlapping: 'Show the legend without overlapping the chart'
        },
        dataLabels: {
            showSeriesName: "Series Name",
            showCategoryName: "Category Name",
            showValue: "Value",
            showPercentage: "Percentage",
            separator: "Separator",
            labelPosition: "labelPosition",
            center: "Center",
            insideEnd: "Inside End",
            insideBase: "Inside Base",
            outsideEnd: "Outside End",
            bestFit: "Best Fit",
            below: "Below",
            left: "Left",
            right: "Right",
            above: "Above",
            labelOptions: "Label Options",
            format: "Category",
            numberFormat: "Number",
            comma: ", (comma)",
            semicolon: "; (semicolon)",
            period: ". (period)",
            newLine: "\n (New Line)",
            space: "  (space)",
            labelPositionText: "Label Position ",
            labelContainsText: "Label Contains"
        }
    };

    en_res.moveChartDialog = {
        title: "Move Chart",
        description: "Choose where you want the chart to be placed:",
        newSheet: "New sheet:",
        existingSheet: "Object in:",
        errorPrompt: {
            sameSheetNameError: "This sheet exists and your chart is embedded in it.Specify a different sheet name."
        }
    };

    en_res.selectChartDialog = {
        title: "Insert Chart",
        insertChart: "Insert Chart",
        changeChartType: "Change Chart Type",
        defaultRowColumn: "Default Row Column Layout",
        switchedRowColumn: "Switched Row Column Layout",
        column: "Column",
        columnClustered: "Clustered Column",
        columnStacked: "Stacked Column",
        columnStacked100: "100% Stacked Column",
        line: "Line",
        lineStacked: "Stacked Line",
        lineStacked100: '100% Stacked Line',
        lineMarkers: "Line with Markers",
        lineMarkersStacked: "Stacked Line with Markers",
        lineMarkersStacked100: "100% Stacked Line with Markers",
        pie: "Pie",
        doughnut: "Doughnut",
        bar: "Bar",
        area: "Area",
        XYScatter: "X Y(Scatter)",
        stock: "Stock",
        combo: "Combo",
        radar: "Radar",
        sunburst: "Sunburst",
        treemap: "Treemap",
        barClustered: "Clustered Bar",
        barStacked: "Stacked Bar",
        barStacked100: "100% Stacked Bar",
        areaStacked: "Stacked Area",
        areaStacked100: "100% Stacked Area",
        xyScatter: "Scatter",
        xyScatterSmooth: "Scatter with Smooth Lines and Markers",
        xyScatterSmoothNoMarkers: "Scatter with Smooth Lines",
        xyScatterLines: "Scatter with Straight Lines and Markers",
        xyScatterLinesNoMarkers: "Scatter with Straight Lines",
        bubble: "Bubble",
        stockHLC: "High-Low-Close",
        stockOHLC: "Open-High-Low-Close",
        stockVHLC: "Volume-High-Low-Close",
        stockVOHLC: "Volume-Open-High-Low-Close",
        columnClusteredAndLine: "Clustered Column - Line",
        columnClusteredAndLineOnSecondaryAxis: "Clustered Column - Line On Secondary Axis",
        stackedAreaAndColumnClustered: "Stacked Area - Clustered Column",
        customCombination: "Custom Combination",
        radarMarkers: "Radar with Markers",
        radarFilled: "Filled Radar",
        seriesModifyDescription: "Choose the chart type and axis for your data series:",
        seriesName: "Series Name",
        chartType: "Chart Type",
        secondaryAxis: "Secondary Axis",
        errorPrompt: {
            stockHLCErrorMsg: "To create this stock chart, arrange the data on your sheet in this order: high price, low price, closing price. Use dates as labels.",
            stockOHLCErrorMsg: "To create this stock chart, arrange the data on your sheet in this order: opening price, high price, low price, closing price. Use dates as labels.",
            stockVHLCErrorMsg: "To create this stock chart, arrange the data on your sheet in this order: volume traded, high price, low price, closing price. Use dates as labels.",
            stockVOHLCErrorMsg: "To create this stock chart, arrange the data on your sheet in this order: volume traded, opening price, high price, low price, closing price. Use dates as labels.",
            emptyDataErrorMsg: "To create a chart,select the cells that contain the data you'd like to use.If you have names for the rows and columns and you'd like to use them as labels,include them in your selection.",
            unexpectedErrorMsg: "Some unknown error happens,please try again.If this happens again,please contact our support department"
        }
    };

    en_res.columnWidthDialog = {
        title: "Column Width",
        columnWidth: "Column width:",
        exception: "The column width must be a number or dynamic size (number with star like 3*).",
        exception2: "The column width must between 0 and 9999999."
    };
    en_res.standardWidthDialog = {
        title: "Standard Width",
        columnWidth: "Standard column width:",
        exception: "Your entry cannot be used. An integer or decimal number may be required."
    };
    en_res.standardHeightDialog = {
        title: "Standard Height",
        rowHeight: "Standard row height:",
        exception: "Your entry cannot be used. An integer or decimal number may be required."
    };
    en_res.insertCellsDialog = {
        title: "Insert",
        shiftcellsright: "Shift cells right",
        shiftcellsdown: "Shift cells down",
        entirerow: "Entire row",
        entirecolumn: "Entire column"
    };
    en_res.deleteCellsDialog = {
        title: "Delete",
        shiftcellsleft: "Shift cells left",
        shiftcellsup: "Shift cells up",
        entirerow: "Entire row",
        entirecolumn: "Entire column"
    };
    en_res.groupDialog = {
        title: "Group",
        rows: "Rows",
        columns: "Columns"
    };
    en_res.ungroupDialog = {
        title: "Ungroup"
    };
    en_res.subtotalDialog = {
        title: "Subtotal",
        remove: "Remove",
        groupNameSelectionLabel: "At each change in:",
        subtotalFormulaItemLabel: "Use function:",
        subtotalFormulaSum: "Sum",
        subtotalFormulaCount: "Count",
        subtotalFormulaAverage: "Average",
        subtotalFormulaMax: "Max",
        subtotalFormulaMin: "Min",
        subtotalFormulaProduct: "Product",
        addSubtotalColumnItem: "Add subtotal to:",
        replaceCurrent: "Replace current subtotals",
        breakPageByGroups: "Page break between groups",
        summaryBelowData: "Summary below data"
    };
    en_res.findDialog = {
        title: "Find",
        findwhat: "Find what:",
        within: "Within:",
        matchcase: "Match case",
        search: "Search:",
        matchexactly: "Match exactly",
        lookin: "Look in:",
        usewildcards: "Use wildcards",
        option: "Option",
        findall: "Find All",
        findnext: "Find Next",
        exception: "Spread cannot find the data you are searching for."
    };
    en_res.gotoDialog = {
        title: "Goto",
        goto: "Go to:",
        reference: "Reference:",
        exception: "The text you entered is not a valid reference or defined name.",
        wrongName: "Executing operation failed."
    };
    en_res.richTextDialog = {
        title: 'Rich Text Dialog',
        fontFamilyTitle: 'Font Family',
        fontSizeTitle: 'Font Size',
        boldTitle: 'Bold',
        italicTitle: 'Italic',
        underlineTitle: 'Underline',
        strikethroughTitle: 'Strikethrough',
        colorPickerTitle: 'Font Color',
        superScriptTitle: 'Superscript',
        subScriptTitle: 'Subscript'
    };
    en_res.nameManagerDialog = {
        title: "Name Manager",
        newName: "New...",
        edit: "Edit...",
        deleteName: "Delete",
        filterWith: {
            title: "Filter with:",
            clearFilter: "Clear Filter",
            nameScopedToWorkbook: "Name Scoped to Workbook",
            nameScopedToWorksheet: "Name Scoped to Worksheet",
            nameWithErrors: "Name With Errors",
            nameWithoutErrors: "Name Without Errors"
        },
        nameColumn: "Name",
        valueColumn: "Value",
        refersToColumn: "Refers To",
        scopeColumn: "Scope",
        commentColumn: "Comment",
        exception1: "The name you entered is not valid.",
        exception2: "The name you entered already exists. Enter a unique name.",
        deleteAlert: "Are you sure you want to delete the name {0}?"
    };
    en_res.newNameDialog = {
        titleNew: "New Name",
        titleEdit: "Edit Name",
        name: "Name:",
        scope: {
            title: "Scope:",
            workbook: "Workbook"
        },
        referTo: "Refer to:",
        comment: "Comment:",
        wrongName: "The name you entered is not valid (wrong syntax or conflict with an existing name)."
    };
    en_res.insertFunctionDialog = {
        title: "Insert Function",
        functionCategory: "Function Category:",
        functionList: "Function List:",
        formula: "Formula:",
        functionCategorys: "All,Database,Date and Time,Engineering,Financial,Information,Logical,Lookup and Reference,Math and Trigonometry,Statistical,Text"
    };
    en_res.buttonCellTypeDialog = {
        title: "Button CellType",
        marginGroup: "Margin:",
        left: "Left:",
        top: "Top:",
        right: "Right:",
        bottom: "Bottom:",
        text: "Text:",
        backcolor: "BackColor:",
        other: "Other:"
    };
    en_res.checkBoxCellTypeDialog = {
        title: "CheckBox CellType",
        textGroup: "Text:",
        "true": "True:",
        indeterminate: "Indeterminate:",
        "false": "False:",
        align: "Align:",
        other: "Other:",
        caption: "Caption:",
        isThreeState: "IsThreeState",
        checkBoxTextAlign: {
            top: "Top",
            bottom: "Bottom",
            left: "Left",
            right: "Right"
        }
    };
    en_res.comboBoxCellTypeDialog = {
        title: "ComboBox CellType",
        editorValueTypes: "EditorValueType:",
        items: "Items:",
        itemProperties: "ItemProperties:",
        text: "Text:",
        value: "Value:",
        add: "Add",
        remove: "Remove",
        editorValueType: {
            text: "Text",
            index: "Index",
            value: "Value"
        },
        editable: "Editable",
        itemHeight: "Items Height"
    };
    en_res.hyperLinkCellTypeDialog = {
        title: "HyperLink CellType",
        link: "Link:",
        visitedlink: "VisitedLink:",
        text: "Text:",
        linktooltip: "LinkToolTip:",
        color: "Color:",
        other: "Other:"
    };
    en_res.checkListCellTypeDialog = {
        title1: "Checkbox List CellType",
        title2: "Radio List CellType",
        direction: "Direction:",
        horizontal: "Horizontal",
        vertical: "Vertical",
        items: "Items:",
        itemProperties: "ItemProperties:",
        text: "Text:",
        value: "Value:",
        add: "Add",
        remove: "Remove",
        isWrap: "isFlowLayout",
        rowCount: "Row Count:",
        colCount: "Column Count:",
        vSpace: "Vertical Spacing:",
        hSpace: "Horizontal Spacing:",
        textAlign: "Text Align:",
        checkBoxTextAlign: {
            left: "Left",
            right: "Right"
        },
        exception: "Please add items for celltype."
    };
    en_res.buttonListCellTypeDialog = {
        title: "ButtonList CellType",
        backColor: "BackColor:",
        foreColor: "ForeColor:",
        marginGroup: "Padding:",
        left: "Left:",
        top: "Top:",
        right: "Right:",
        bottom: "Bottom:",
        selectMode: "Selection Mode:",
        singleSelect: "Single",
        multiSelect: "Multiple",
        exception: "Please add items for celltype."
    };
    en_res.headerCellsDialog = {
        title: "Header Cells",
        rowHeader: "Row Header",
        columnHeader: "Column Header",
        backColor: "BackColor",
        borderBottom: "BorderBottom",
        borderLeft: "BorderLeft",
        borderRight: "BorderRight",
        borderTop: "BorderTop",
        diagonalUp: "DiagonalUp",
        diagonalDown: "DiagonalDown",
        font: "Font",
        foreColor: "ForeColor",
        formatter: "Formatter",
        hAlign: "HAlign",
        height: "Height",
        locked: "Locked",
        resizable: "Resizable",
        shrinkToFit: "ShrinkToFit",
        value: "Value",
        textIndent: "TextIndent",
        vAlign: "VAlign",
        visible: "Visible",
        width: "Width",
        wordWrap: "WordWrap",
        popUp: "...",
        merge: "Merge",
        unmerge: "Unmerge",
        insertRows: "Insert Rows",
        addRows: "Add Rows",
        deleteRows: "Delete Rows",
        insertColumns: "Insert Columns",
        addColumns: "Add Columns",
        deleteColumns: "Delete Columns",
        clear: "Clear",
        top: 'Top',
        bottom: 'Bottom',
        left: 'Left',
        right: 'Right',
        center: 'Center',
        general: 'General',
        verticalText: "Vertical Text",
        exception: "The setting is invalid. Please check the red parts."
    };
    en_res.fontPickerDialog = {
        title: "Font"
    };
    en_res.fillDialog = {
        title: "Series"
    };
    en_res.hyperlinkDialog = {
        title: "Hyperlink",
        textDisplay: "Text to display:",
        screenTip: "ScreenTip:",
        drawUnderline: "Draw underline:",
        linkColor: "Link Color:",
        visitedLinkColor: "Visited Link Color:",
        target: "Target:",
        blank: "blank",
        self: "self",
        parent: "parent",
        top: "top",
        existingFileOrWebPage: "Web Page or File",
        placeInThisDocument: "This Document",
        emailAddress: "Email Address",
        linkToTheWebPage: "Link to an existing file path or web page.",
        address: "Address:",
        select: "Select...",
        typeCellReference: "Type the cell reference:",
        selectPlaceInDocument: "Or select a place in this document:",
        cellReference: "Cell Reference",
        definedNames: "Defined Names",
        emailAddressString: "E-mail address:",
        emailSubject: "Subject:",
        removeLink: "Remove"
    };

    en_res.zoomDialog = {
        title: "Zoom",
        double: "200%",
        normal: "100%",
        threeFourths: "75%",
        half: "50%",
        quarter: "25%",
        fitSelection: "Fit selection",
        custom: "Custom:",
        exception: "Your entry cannot be used. An integer or decimal number may be required.",
        magnification: "Magnification",
        percent: "%"
    };
    en_res.contextMenu = {
        cut: "Cut",
        copy: "Copy",
        paste: "Paste Options:",
        pasteAll: 'Paste All',
        pasteFormula: 'Paste Formula',
        pasteValue: 'Paste Value',
        pasteFormatting: 'Paste Formatting',
        insertDialog: "Insert...",
        deleteDialog: "Delete...",
        clearcontents: "Clear Contents",
        filter: "Filter",
        totalRow: "Totals Row",
        toRange: "Convert to Range",
        sort: "Sort",
        table: "Table",
        sortAToZ: "Sort A to Z",
        sortZToA: "Sort Z to A",
        customSort: "Custom Sort...",
        formatCells: "Format Cells...",
        editCellType: "Edit CellType...",
        editCellDropdows: "Edit Cell Dropdowns...",
        link: "Link...",
        editHyperlink: "Edit Hyperlink...",
        openHyperlink: "Open Hyperlink",
        removeHyperlink: "Remove Hyperlink",
        removeHyperlinks: "Remove Hyperlinks",
        richText: "Rich text...",
        defineName: "Define Name...",
        tag: "Tag...",
        rowHeight: "Row Height...",
        columnWidth: "Column Width...",
        hide: "Hide",
        unhide: "Unhide",
        headers: "Headers...",
        insert: "Insert",
        delete: "Delete",
        tableInsert: "Insert",
        tableInsertRowsAbove: "Table Rows Above",
        tableInsertRowsBelow: "Table Row Below",
        tableInsertColumnsLeft: "Table Columns to the Left",
        tableInsertColumnsRight: "Table Columns to the Right",
        tableDelete: "Delete",
        tableDeleteRows: "Table Rows",
        tableDeleteColumns: "Table Columns",
        protectsheet: "Protect Sheet...",
        unprotectsheet: "Unprotected Sheet...",
        comments: "A workbook must contain at least one visible worksheet.",
        insertComment: "Insert Comment",
        editComment: "Edit Comment",
        deleteComment: "Delete Comment",
        hideComment: "Hide Comment",
        editText: "Edit Text",
        exitEditText: "Exit Edit Text",
        formatComment: "Format Comment...",
        unHideComment: "Show/Hide Comments",
        sheetTabColor: "Tab Color",
        remove: "Remove",
        slicerProperty: "Size and Properties...",
        slicerSetting: "Slicer Settings...",
        changeChartType: "Change Chart Type...",
        selectData: "Select Data...",
        moveChart: "Move Chart...",
        resetChartColor: "Reset to Match Style",
        formatChart: {
            chartArea: "Format Chart Area...",
            series: "Format Data Series...",
            axis: "Format Axis...",
            legend: "Format Legend...",
            dataLabels: "Format Data Labels...",
            chartTitle: "Format Chart Title...",
            trendline: "Format Trendline...",
            errorBar: "Format Error Bars...",
            unitsLabel: "Format Display Unit...",
        },
        groupShapes: 'Group',
        ungroupShapes: 'Ungroup',
        pasteShape: 'Paste',
        formatShapes: 'Format Shape...',
        pasteValuesFormatting: 'Values & Formatting',
        pasteFormulaFormatting: 'Formula & Formatting',
        outlineColumn: 'Outline Column...',
        insertCopiedCells: 'Insert Copied Cells...',
        insertCutCells: 'Insert Cut Cells...',
        shiftCellsRight: 'Shift Cells Right',
        shiftCellsDown: 'Shift Cells Down',
        headerInsertCopiedCells: 'Insert Copied Cells',
        headerInsertCutCells: 'Insert Cut Cells'
    };
    en_res.tagDialog = {
        cellTagTitle: "Cell Tag Dialog",
        rowTagTitle: "Row Tag Dialog",
        columnTagTitle: "Column Tag Dialog",
        sheetTagTitle: "Sheet Tag Dialog",
        tag: "Tag:"
    };
    en_res.borderPicker = {
        lineStyleTitle: "Line:",
        borderColorTitle: "Color:",
        none: "None"
    };
    en_res.borderDialog = {
        border: "Border",
        presets: "Presets",
        none: "None",
        outline: "Outline",
        inside: "Inside",
        line: "Line",
        text: "Text",
        comments: "The selected border style can be applied by clicking the presets, preview diagram, or the buttons above."
    };

    en_res.conditionalFormat = {
        highlightCellsRules: "Highlight Cells Rules",
        topBottomRules: "Top/Bottom Rules",
        dataBars: "Data Bars",
        colorScales: "Color Scales",
        iconSets: "Icon Sets",
        newRule: "New Rule...",
        clearRules: "Clear Rules...",
        manageRules: "Manage Rules...",
        greaterThan: "Greater Than...",
        lessThan: "Less Than...",
        between: "Between...",
        equalTo: "Equal To...",
        textThatContains: "Text that Contains...",
        aDateOccurring: "A Date Occurring...",
        duplicateValues: "Duplicate Values...",
        moreRules: "More Rules...",
        top10Items: "Top 10 Items...",
        bottom10Items: "Bottom 10 Items...",
        aboveAverage: "Above Average...",
        belowAverage: "Below Average...",
        gradientFill: "Gradient Fill",
        solidFill: "Solid Fill",
        directional: "Directional",
        shapes: "Shapes",
        indicators: "Indicators",
        ratings: "Ratings",
        clearRulesFromSelectedCells: "Clear Rules from Selected Cells",
        clearRulesFromEntireSheet: "Clear Rules from Entire Sheet"
    };

    en_res.fileMenu = {
        new: "New",
        open: "Open",
        save: "Save",
        saveAs: "Save As",
        export: "Export",
        import: "Import",
        exit: "Exit",
        recentWorkbooks: "Recent Workbooks",
        computer: "Computer",
        currentFolder: "Current Folder",
        recentFolders: "Recent Folders",
        browse: "Browse",
        spreadSheetJsonFile: "SpreadSheet File (JSON)",
        excelFile: "Excel File",
        csvFile: "CSV File",
        pdfFile: "PDF File",
        importSpreadSheetFile: "Import SSJSON File",
        importExcelFile: "Import Excel File",
        importCsvFile: "Import CSV File",
        exportSpreadSheetFile: "Export SSJSON File",
        exportExcelFile: "Export Excel File",
        exportCsvFile: "Export CSV File",
        exportPdfFile: "Export PDF File",
        exportJSFile: "Export Javascript File",
        openFlags: "Open Flags",
        importIgnoreStyle: 'Do not import style',
        importIgnoreFormula: 'Do not import formula',
        importDoNotRecalculateAfterLoad: "Do not auto-calculate formulas after importing",
        importRowAndColumnHeaders: "Import both frozen columns and rows as headers",
        importRowHeaders: "Import frozen rows as column headers",
        importColumnHeaders: "Import frozen columns as row headers",
        importPassword: "Password",
        importIncludeRowHeader: "Import row header",
        importIncludeColumnHeader: "Import column header",
        importUnformatted: "Leave the data unformatted",
        importImportFormula: "Import cell formulas",
        importRowDelimiter: "Row Delimiter",
        importColumnDelimiter: "Column Delimiter",
        importCellDelimiter: "Cell Delimiter",
        importEncoding: "File Encoding",
        saveFlags: "Save Flags",
        exportIgnoreStyle: "Do not export style",
        exportIgnoreFormulas: "Do not export formulas",
        exportAutoRowHeight: "Auto fit row height",
        exportSaveAsFiltered: "Export as filtered",
        exportSaveAsViewed: "Export as viewed",
        exportSaveBothCustomRowAndColumnHeaders: "Export row header as Excel frozen columns and column header as Excel frozen rows",
        exportSaveCustomRowHeaders: "Export row header as Excel frozen columns",
        exportSaveCustomColumnHeaders: "Export column header as Excel frozen rows",
        exportPassword: "Password",
        exportIncludeRowHeader: "Include row headers",
        exportIncludeColumnHeader: "Include column headers",
        exportUnFormatted: "Do not include any style information",
        exportFormula: "Include formulas",
        exportAsViewed: "Export as viewed",
        exportSheetIndex: "Sheet Index",
        exportEncoding: "Encoding",
        exportRow: "Row Index",
        exportColumn: "Column Index",
        exportRowCount: "Row Count",
        exportColumnCount: "Column Count",
        exportRowDelimiter: "Row Delimiter",
        exportColumnDelimiter: "Column Delimiter",
        exportCellDelimiter: "Cell Delimiter",
        exportVisibleRowCol: "Only include visible rows and columns",
        pdfBasicSetting: "Basic Settings",
        pdfTitle: "Title:",
        pdfAuthor: "Author:",
        pdfApplication: "Application:",
        pdfSubject: "Subject:",
        pdfKeyWords: "Key words:",
        pdfExportSetting: "Export Settings",
        exportSheetLabel: "Choose the sheet to export:",
        allSheet: "All",
        pdfDisplaySetting: "Display Settings",
        centerWindowLabel: "Center window",
        showTitleLabel: "Show title",
        showToolBarLabel: "Show toolbar",
        fitWindowLabel: "Fit window",
        showMenuBarLabel: "Show menu bar",
        showWindowUILabel: "Show window UI",
        destinationTypeLabel: "Destination type:",
        destinationType: {
            autoDestination: "Auto",
            fitPageDestination: "FitPage",
            fitWidthDestination: "FitWidth",
            fitHeightDestination: "FitHeight",
            fitBoxDestination: "FitBox"
        },
        openTypeLabel: "Open type:",
        openType: {
            autoOpen: "Auto",
            useNoneOpen: "UseNone",
            useOutlinesOpen: "UseOutlines",
            useThumbsOpen: "UseThumbs",
            fullScreenOpen: "FullScreen",
            useOCOpen: "UseOC",
            useAttachmentsOpen: "UseAttachments"
        },
        pdfPageSetting: "Page Settings",
        openPageNumberLabel: "Open page number:",
        pageLayoutLabel: "Page layout:",
        pageLayout: {
            autoLayout: "Auto",
            singlePageLayout: "SinglePage",
            oneColumnLayout: "OneColumn",
            twoColumnLeftLayout: "TwoColumnLeft",
            twoColumnRightLayout: "TwoColumnRight",
            twoPageLeftLayout: "TwoPageLeft",
            twoPageRight: "TwoPageRight"
        },
        pageDurationLabel: "Page duration:",
        pageTransitionLabel: "Page transition:",
        pageTransition: {
            defaultTransition: "Default",
            splitTransition: "Split",
            blindsTransition: "Blinds",
            boxTransition: "Box",
            wipeTransition: "Wipe",
            dissolveTransition: "Dissolve",
            glitterTransition: "Glitter",
            flyTransition: "Fly",
            pushTransition: "Push",
            coverTransition: "Cover",
            uncoverTransition: "Uncover",
            fadeTransition: "Fade"
        },
        printerSetting: "Printer Setting...",
        printerSettingDialogTitle: "Printer Setting",
        copiesLabel: "Copies:",
        scalingTypeLabel: "Scaling Type:",
        scalingType: {
            appDefaultScaling: "AppDefault",
            noneScaling: "None"
        },
        duplexModeLabel: "Duplex Mode:",
        duplexMode: {
            defaultDuplex: "Default",
            simplexDuplex: "Simplex",
            duplexFlipShortEdge: "DuplexFlipShortEdge",
            duplexFlipLongEdge: "DuplexFlipLongEdge"
        },
        choosePaperSource: "Choose page source by pdf page size",
        printRanges: "Print ranges",
        indexLabel: "Index",
        countLabel: "Count",
        addRange: "Add",
        removeRange: "Remove",
        addRangeException: "Invalid value, the index must be greater than or equal to 0 and the count must be greater than 0.",
        noRecentWorkbooks: "No recent workbooks. Please open a workbook first.",
        noRecentFolders: "No recent folders. Please open a workbook first."
    };

    en_res.formatTableStyle = {
        name: "Name:",
        tableElement: "Table Element:",
        preview: "Preview",
        format: "Format",
        tableStyle: "Table Style",
        clear: "Clear",
        stripeSize: "Stripe Size",
        light: "Light",
        medium: "Medium",
        dark: "Dark",
        newTableStyle: "New Table Style...",
        clearTableStyle: "Clear",
        custom: "Custom",
        exception: "This style name is already exists.",
        title: "SpreadJS Designer"
    };
    en_res.tableElement = {
        wholeTableStyle: "Whole Table",
        firstColumnStripStyle: "First Column Strip",
        secondColumnStripStyle: "Second Column Strip",
        firstRowStripStyle: "First Row Strip",
        secondRowStripStyle: "Second Row Strip",
        highlightLastColumnStyle: "Last Column",
        highlightFirstColumnStyle: "First Column",
        headerRowStyle: "Header Row",
        footerRowStyle: "Total Row",
        firstHeaderCellStyle: "First Header Cell",
        lastHeaderCellStyle: "Last Header Cell",
        firstFooterCellStyle: "First Footer Cell",
        lastFooterCellStyle: "Last Footer Cell"
    };
    en_res.conditionalFormatting = {
        common: {
            'with': "with",
            selectedRangeWith: "for the selected range with",
            and: "and"
        },
        greaterThan: {
            title: "Greater Than",
            description: "Format cells that are GREATER THAN:"
        },
        lessThan: {
            title: "Less Than",
            description: "Format cells that are LESS THAN:"
        },
        between: {
            title: "Between",
            description: "Format cells that are BETWEEN:"
        },
        equalTo: {
            title: "Equal To",
            description: "Format cells that are EQUAL TO:"
        },
        textThatContains: {
            title: "Text That Contains",
            description: "Format cells that contain the text:"
        },
        dateOccurringFormat: {
            title: "A Date Occurring",
            description: "Format cells that contain a date occurring:",
            date: {
                yesterday: "Yesterday",
                today: "Today",
                tomorrow: "Tomorrow",
                last7days: "In the last 7 days",
                lastweek: "Last week",
                thisweek: "This week",
                nextweek: "Next week",
                lastmonth: "Last month",
                thismonth: "This month",
                nextmonth: "Next month"
            }
        },
        duplicateValuesFormat: {
            title: "Duplicate Values",
            description: "Format cells that contain:",
            type: {
                duplicate: "Duplicate",
                unique: "Unique"
            },
            valueswith: "values with"
        },
        top10items: {
            title: "Top 10 Items",
            description: "Format cells that rank in the TOP:"
        },
        bottom10items: {
            title: "Bottom 10 Items",
            description: "Format cells that rank in the BOTTOM:"
        },
        aboveAverage: {
            title: "Above Average",
            description: "Format cells that are ABOVE AVERAGE:"
        },
        belowAverage: {
            title: "Below Average",
            description: "Format cells that are BELOW AVERAGE:"
        },
        newFormattingRule: {
            title: "New Formatting Rule",
            title2: "Edit Formatting Rule",
            description1: "Select a Rule Type:",
            description2: "Edit the Rule Description:",
            ruleType: {
                formatOnValue: "►Format all cells based on their values",
                formatContain: "►Format only cells that contain",
                formatRankedValue: "►Format only top or bottom ranked values",
                formatAbove: "►Format only values that are above or below average",
                formatUnique: "►Format only unique or duplicate values",
                useFormula: "►Use a formula to determine which cells to format"
            },
            formatOnValue: {
                description: "Format all cells based on their values:",
                formatStyle: "Format style:",
                formatStyleSelector: {
                    color2: "2-Color Scale",
                    color3: "3-Color Scale",
                    dataBar: "Data Bar",
                    iconSets: "Icon Sets"
                },
                color2: {
                    min: "Minimum",
                    max: "Maximum",
                    type: "Type:",
                    value: "Value:",
                    color: "Color:",
                    preview: "Preview",
                    minSelector: {
                        lowest: "Lowest Value"
                    },
                    maxSelector: {
                        highest: "Highest Value"
                    }
                },
                color3: {
                    mid: "MidPoint"
                },
                dataBar: {
                    showBarOnly: "Show Bar Only",
                    auto: "Automatic",
                    description2: "Bar Appearance:",
                    fill: "Fill",
                    color: "Color",
                    border: "Border",
                    fillSelector: {
                        solidFill: "Solid Fill",
                        gradientFill: "Gradient Fill"
                    },
                    borderSelector: {
                        noBorder: "No Border",
                        solidBorder: "Solid Border"
                    },
                    negativeBtn: "Negative value and Axis...",
                    barDirection: "Bar Direction:",
                    barDirectionSelector: {
                        l2r: "Left-to-Right",
                        r2l: "Right-to-left"
                    },
                    preview: "Preview",
                    negativeDialog: {
                        title: "Negative Value and Axis Settings",
                        group1: {
                            title: "Negative bar fill color",
                            fillColor: "Fill Color:",
                            apply: "Apply same fill color as positive bar"
                        },
                        group2: {
                            title: "Negative bar border color",
                            borderColor: "Border Color:",
                            apply: "Apply same fill color as positive bar"
                        },
                        group3: {
                            title: "Axis settings",
                            description: "Select axis position in cell to change the appearance of bars for negative values",
                            radio: {
                                auto: "Automatic(display at variable position based on negative values)",
                                cell: "Cell midpoint",
                                none: "None(show negative value bars in same direction as positive)"
                            },
                            axisColor: "Axis color:"
                        }
                    }
                },
                iconSets: {
                    iconStyle: "Icon style:",
                    showIconOnly: "Show Icon Only",
                    reverseIconOrder: "Reverse Icon Order",
                    display: "Display each icon according to these rules:",
                    icon: "Icon",
                    value: "Value",
                    type: "Type",
                    description1: "when value is",
                    description2: "when < ",
                    operator: {
                        largeOrEqu: "> =",
                        large: ">"
                    },
                    customIconSet: "Custom",
                    noCellIcon: "No Cell Icon"
                },
                commonSelector: {
                    num: "Number",
                    percent: "Percent",
                    formula: "Formula",
                    percentile: "Percentile"
                }
            },
            formatContain: {
                description: "Format only cells with:",
                type: {
                    cellValue: "Cell Value",
                    specificText: "Specific Text",
                    dateOccurring: "Dates Occurring",
                    blanks: "Blanks",
                    noBlanks: "No Blanks",
                    errors: "Errors",
                    noErrors: "No Errors"
                },
                operator_cellValue: {
                    between: "between",
                    notBetween: "not between",
                    equalTo: "equal to",
                    notEqualTo: "not equal to",
                    greaterThan: "greater than",
                    lessThan: "less than",
                    greaterThanOrEqu: "greater than or equal to",
                    lessThanOrEqu: "less than or equal to"
                },
                operator_specificText: {
                    containing: "containing",
                    notContaining: "not Containing",
                    beginningWith: "beginning with",
                    endingWith: "ending with"
                }
            },
            formatRankedValue: {
                description: "Format cells that rank in the:",
                type: {
                    top: "Top",
                    bottom: "Bottom"
                }
            },
            formatAbove: {
                description: "Format cells that are:",
                type: {
                    above: "above",
                    below: "below",
                    equalOrAbove: "equal or above",
                    equalOrBelow: "equal or below",
                    std1Above: "1 std dev above",
                    std1Below: "1 std dev below",
                    std2Above: "2 std dev above",
                    std2Below: "2 std dev below",
                    std3Above: "3 std dev above",
                    std3Below: "3 std dev below"
                },
                description2: "the average for the selected range"
            },
            formatUnique: {
                description: "Format all:",
                type: {
                    duplicate: "duplicate",
                    unique: "unique"
                },
                description2: "values in the selected range"
            },
            useFormula: {
                description: "Format values where this formula is true:"
            },
            preview: {
                description: "Preview:",
                buttonText: "Format...",
                noFormat: "No Format Set",
                hasFormat: "AaBbCcYyZz"
            }
        },
        withStyle: {
            lightRedFill_DarkRedText: "Light Red Fill with Dark Red Text",
            yellowFill_DrakYellowText: "Yellow Fill with Dark Yellow Text",
            greenFill_DarkGreenText: "Green Fill with Dark Green Text",
            lightRedFill: "Light Red Fill",
            redText: "Red Text",
            redBorder: "Red Border",
            customFormat: "Custom Format..."
        },
        exceptions: {
            e1: "The value you entered is not a valid number, date, time, or string.",
            e2: "Enter a value.",
            e3: "Enter a whole number between 1 and 1000.",
            e4: "The value you entered cannot be empty.",
            e5: "The type of reference cannot be used in a Conditional Formatting formula.\nChange the reference to a single cell, or use the reference with a worksheet function, such as = SUM(A1:E5).",
            e6: "The source range of a formula rule can only be a single range!",
            e7: "The reference is not valid. References must be a singlecell, row, or column."
        }
    };

    en_res.formattingRulesManagerDialog = {
        title: "Conditional Formatting Rules Manager",
        rulesScopeLabel: "Formatting rules for this worksheet: ",
        rulesScopeForSelection: "Current Selection",
        rulesScopeForWorksheet: "This Worksheet",
        newRule: "New Rule...",
        editRule: "Edit Rule...",
        deleteRule: "Delete Rule...",
        gridTitleRule: "Rule (applied in order shown)",
        gridTitleFormat: "Format",
        gridTitleAppliesTo: "Applies to",
        gridTitleStopIfTrue: "Stop If True",
        ruleDescriptions: {
            valueBetween: 'Cell value between {0} and {1}',
            valueNotBetween: 'Cell value no between {0} and {1}',
            valueEquals: 'Cell value = {0}',
            valueNotEquals: 'Cell value <> {0}',
            valueGreateThan: 'Cell value > {0}',
            valueGreateThanOrEquals: 'Cell value >= {0}',
            valueLessThan: 'Cell value < {0}',
            valueLessThanOrEquals: 'Cell value <= {0}',
            valueContains: 'Cell value contains "{0}"',
            valueNotContains: 'Cell value does not contains "{0}"',
            valueBeginsWith: 'Cell value begins with "{0}"',
            valueEndsWith: 'Cell value ends with "{0}"',
            last7Days: 'Last 7 days',
            lastMonth: 'Last Month',
            lastWeek: 'Last week',
            nextMonth: 'Next month',
            nextWeek: 'Next week',
            thisMonth: 'This month',
            thisWeek: 'This week',
            today: 'Today',
            tomorrow: 'Tomorrow',
            yesterday: 'Yesterday',
            duplicateValues: 'Duplicate Values',
            uniqueValues: 'Unique Values',
            top: 'Top {0}',
            bottom: 'Bottom {0}',
            above: 'Above Average',
            above1StdDev: '1 std dev above Average',
            above2StdDev: '2 std dev above Average',
            above3StdDev: '3 std dev above Average',
            below: 'Below Average',
            below1StdDev: '1 std dev below Average',
            below2StdDev: '2 std dev below Average',
            below3StdDev: '3 std dev below Average',
            equalOrAbove: 'Equals to or above Average',
            equalOrBelow: 'Equals to or below Average',
            dataBar: 'Data Bar',
            twoScale: 'Graded Color Scale',
            threeScale: 'Graded Color Scale',
            iconSet: 'Icon Set',
            formula: 'Formula: {0}'
        },
        previewText: 'AaBbCcYyZz'
    };

    en_res.cellStylesDialog = {
        cellStyles: "Cell Styles",
        custom: "Custom",
        cellButtonStyleTitle: "Color, Datetime and Other Cell Type Style",
        goodBadAndNeutral: "Good, Bad and Neutral",
        dataAndModel: "Data And Model",
        titlesAndHeadings: "Titles and Headings",
        themedCellStyle: "Themed Cell Style",
        numberFormat: "Number Format",
        cellButtonsStyles: {
            "colorpicker-cellbutton": "Color",
            "datetimepicker-cellbutton": "Datetime",
            "timepicker-cellbutton": "Time",
            "calculator-cellbutton": "Calculator",
            "monthpicker-cellbutton": "Month",
            "slider-cellbutton": "Slider",
            "okcancel-cellbutton": "OKCancel",
            "clear-cellbutton": "Clear"
        },
        goodBadAndNeutralContent: {
            "Normal": "Normal",
            "Bad": "Bad",
            "Good": "Good",
            "Neutral": "Neutral"
        },
        dataAndModelContent: {
            "Calculation": "Calculation",
            "Check Cell": "Check Cell",
            "Explanatory Text": "Explanatory...",
            "Input": "Input",
            "Linked Cell": "Linked Cell",
            "Note": "Note",
            "Output": "Output",
            "Warning Text": "Warning Text"
        },
        titlesAndHeadingsContent: {
            "Heading 1": "Heading 1",
            "Heading 2": "Heading 2",
            "Heading 3": "Heading 3",
            "Heading 4": "Heading 4",
            "Title": "Title",
            "Total": "Total"
        },
        themedCellStyleContent: {
            "20% - Accent1": "20% - Accent1",
            "20% - Accent2": "20% - Accent2",
            "20% - Accent3": "20% - Accent3",
            "20% - Accent4": "20% - Accent4",
            "20% - Accent5": "20% - Accent5",
            "20% - Accent6": "20% - Accent6",
            "40% - Accent1": "40% - Accent1",
            "40% - Accent2": "40% - Accent2",
            "40% - Accent3": "40% - Accent3",
            "40% - Accent4": "40% - Accent4",
            "40% - Accent5": "40% - Accent5",
            "40% - Accent6": "40% - Accent6",
            "60% - Accent1": "60% - Accent1",
            "60% - Accent2": "60% - Accent2",
            "60% - Accent3": "60% - Accent3",
            "60% - Accent4": "60% - Accent4",
            "60% - Accent5": "60% - Accent5",
            "60% - Accent6": "60% - Accent6",
            "Accent1": "Accent1",
            "Accent2": "Accent2",
            "Accent3": "Accent3",
            "Accent4": "Accent4",
            "Accent5": "Accent5",
            "Accent6": "Accent6"
        },
        numberFormatContent: {
            "Comma": "Comma",
            "Comma [0]": "Comma [0]",
            "Currency": "Currency",
            "Currency [0]": "Currency [0]",
            "Percent": "Percent"
        },
        newCellStyle: "New Cell Style..."
    };

    en_res.newCellStyleDialog = {
        style: "Style",
        styleName: "Style name:",
        defaultStyleName: "Style 1",
        format: "Format...",
        message: "This style name already exists"
    };

    en_res.cellStyleContextMenu = {
        "delete": "Delete",
        modify: "Modify"
    };

    en_res.insertPictureDialogTitle = "Insert Picture";
    en_res.pictureFormatFilter = {
        jpeg: "JPEG File Interchange Format(*.jpg;*.jpeg)",
        png: "Portable Network Graphics(*.png)",
        bmp: "Windows Bitmap(*.bmp)",
        allFiles: "All Files(*.*)"
    };

    en_res.ribbon = {
        accessBar: {
            undo: "Undo",
            redo: "Redo",
            save: "Save",
            New: "New",
            open: "Open",
            active: "Active",
            tipWidth: 660
        },
        home: {
            file: "FILE",
            home: "HOME",
            clipboard: "Clipboard",
            fonts: "Fonts",
            alignment: "Alignment",
            numbers: "Numbers",
            cellType: "Cell Type",
            styles: "Styles",
            cells: "Cells",
            editing: "Editing",
            paste: "Paste",
            all: "All",
            formulas: "Formulas",
            values: "Values",
            formatting: "Formatting",
            valuesAndFormatting: "Values & Formatting",
            formulasAndFormatting: "Formulas & Formatting",
            cut: "Cut",
            copy: "Copy",
            fontFamily: "Font Family",
            fontSize: "Font Size",
            increaseFontSize: "Increase Font Size",
            decreaseFontSize: "Decrease Font Size",
            bold: "Bold",
            italic: "Italic",
            underline: "Underline",
            doubleUnderline: "Double Underline",
            border: "Border",
            bottomBorder: "Bottom Border",
            topBorder: "Top Border",
            leftBorder: "Left Border",
            rightBorder: "Right Border",
            noBorder: "No Border",
            allBorder: "All Border",
            outsideBorder: "Outside Border",
            thickBoxBorder: "Thick Box Border",
            bottomDoubleBorder: "Bottom Double Border",
            thickBottomBorder: "Thick Bottom Border",
            topBottomBorder: "Top and Bottom Border",
            topThickBottomBorder: "Top and Thick Bottom Border",
            topDoubleBottomBorder: "Top and Double Bottom Border",
            moreBorders: "More Borders...",
            backColor: "Background Color",
            fontColor: "Font Color",
            topAlign: "Top Align",
            middleAlign: "Middle Align",
            bottomAlign: "Bottom Align",
            leftAlign: "Left Align",
            centerAlign: "Center Align",
            rightAlign: "Right Align",
            increaseIndent: "Increase Indent",
            decreaseIndent: "Decrease Indent",
            wrapText: "Wrap Text",
            mergeCenter: "Merge & Center",
            mergeAcross: "Merge Across",
            mergeCells: "Merge Cells",
            unMergeCells: "Unmerge Cells",
            numberFormat: "Number Format",
            general: "General",
            Number: "Number",
            currency: "Currency",
            accounting: "Accounting",
            shortDate: "Short Date",
            longDate: "Long Date",
            time: "Time",
            percentage: "Percentage",
            fraction: "Fraction",
            scientific: "Scientific",
            text: "Text",
            moreNumberFormat: "More Number Formats...",
            percentStyle: "Percent Style",
            commaStyle: "CommaStyle",
            increaseDecimal: "Increase Decimal",
            decreaseDecimal: "Decrease Decimal",
            buttonCellType: "Button",
            checkboxCellType: "Checkbox",
            comboBoxCellType: "Combobox",
            hyperlinkCellType: "Hyperlink",
            checkboxListCellType: "Checkbox List",
            radioListCellType: "Radiobutton List",
            buttonList: "Button List",
            list: "List",
            buttonListCellType: "ButtonList",
            clearCellType: "Clear CellType",
            clearCellButton: "Clear",
            cellDropdowns: "Cell Dropdowns",
            conditionFormat: "Conditional Format",
            conditionFormat1: "Conditional Format",
            formatTable: "Format Table",
            formatTable1: "Format Table",
            insert: "Insert",
            insertCells: "Insert Cells...",
            insertSheetRows: "Insert Sheet Rows",
            insertSheetColumns: "Insert Sheet Columns",
            insertSheet: "Insert Sheet",
            Delete: "Delete",
            deleteCells: "Delete Cells...",
            deleteSheetRows: "Delete Sheet Rows",
            deleteSheetColumns: "Delete Sheet Columns",
            deleteSheet: "Delete Sheet",
            format: "Format",
            rowHeight: "Row Height...",
            autofitRowHeight: "AutoFit Row Height",
            defaultHeight: "Default Height...",
            columnWidth: "Column Width...",
            autofitColumnWidth: "AutoFit Column Width",
            defaultWidth: "Default Width...",
            hideRows: "Hide Rows",
            hideColumns: "Hide Columns",
            unHideRows: "Unhide Rows",
            unHideColumns: "Unhide Columns",
            protectSheet: "Protect Sheet...",
            unProtectSheet: "Unprotect Sheet...",
            lockCells: "Lock Cells",
            unLockCells: "Unlock Cells",
            autoSum: "Auto Sum",
            sum: "Sum",
            average: "Average",
            countNumbers: "Count Numbers",
            max: "Max",
            min: "Min",
            fill: "Fill",
            down: "Down",
            right: "Right",
            up: "Up",
            left: "Left",
            series: "Series...",
            clear: "Clear",
            clearAll: "Clear All",
            clearFormat: "Clear Format",
            clearContent: "Clear Content",
            clearComments: "Clear Comments",
            sortFilter: "Sort & Filter",
            sortFilter1: "Sort & Filter",
            sortAtoZ: "Sort A to Z",
            sortZtoA: "Sort Z to A",
            customSort: "Custom Sort...",
            filter: "Filter",
            clearFilter: "Clear Filter",
            reapply: "Reapply",
            find: "Find",
            find1: "Find...",
            goto: "Go to...",
            rotateText: "Rotate Text",
            orientation: "Orientation",
            angleCounterclockwise: "Angle Counterclockwise",
            angleClockwise: "Angle Clockwise",
            verticalText: "Vertical Text",
            rotateTextUp: "Rotate Text Up",
            rotateTextDown: "Rotate Text Down",
            formatCellAlignment: "Format Cell Alignment",
        },
        insert: {
            insert: "INSERT",
            table: "Table",
            chart: "Chart",
            sparklines: "Sparklines",
            line: "Line",
            column: "Column",
            winloss: "Win/Loss",
            insertTable: "Insert Table",
            insertChart: "Insert Chart",
            insertShapes: "Insert Shapes",
            insertBarcode: "Insert Barcode",
            insertHyperlink: "Insert Hyperlink",
            insertPicture: "Insert Picture",
            insertLineSparkline: "Insert Line Sparkline",
            insertColumnSparkline: "Insert Column Sparkline",
            insertWinlossSparkline: "Insert Win/Loss Sparkline",
            picture: "Picture",
            illustrations: "Illustr..",
            shapes: "Shapes",
            barcode: "Barcode",
            hyperlink: "Hyperlink",
            insertPieSparkline: "Insert Pie Sparkline",
            insertAreaSparkline: "Insert Area Sparkline",
            insertScatterSparkline: "Insert Scatter Sparkline",
            pie: "Pie",
            area: "Area",
            scatter: "Scatter",
            insertBulletSparkline: "Insert Bullet Sparkline",
            bullet: "Bullet",
            insertSpreadSparkline: "Insert Spread Sparkline",
            spread: "Spread",
            insertStackedSparkline: "Insert Stacked Sparkline",
            stacked: "Stacked",
            insertHbarSparkline: "Insert Hbar Sparkline",
            hbar: "Hbar",
            insertVbarSparkline: "Insert Vbar Sparkline",
            vbar: "Vbar",
            insertVariSparkline: "Insert Variance Sparkline",
            variance: "Variance",
            insertBoxPlotSparkline: "Insert BoxPlot Sparkline",
            boxplot: "BoxPlot",
            insertCascadeSparkline: "Insert Cascade Sparkline",
            cascade: "Cascade",
            insertParetoSparkline: "Insert Pareto Sparkline",
            pareto: "Pareto",
            insertMonthSparkline: "Insert Month Sparkline",
            month: "Month",
            insertYearSparkline: "Insert Year Sparkline",
            year: "Year"
        },
        formulas: {
            formulas: "FORMULAS",
            insertFunction: "Insert Function",
            insertFunction1: "Insert Function",
            functions: "Functions",
            names: "Names",
            nameManager: "Name Manager",
            nameManager1: "Name Manager",
            text: "Text",
            financial: "Financial",
            logical: "Logical",
            datetime: "Data & Time",
            lookupreference: "Lookup& Reference",
            mathtrig: "Math & Trig",
            more: "More Functions",
            statistical: "Statistical",
            engineering: "Engineering",
            information: "Information",
            database: "Database",
            autoSum: "AutoSum",
            formulaAuditing: "Formula Auditing",
            showFormulas: "Show Formulas ",
            functionsLibrary: "Functions Library"
        },
        data: {
            data: "DATA",
            sortFilter: "Sort & Filter",
            dataTools: "Data Tools",
            outline: "Outline",
            sortAtoZ: "Sort A to Z",
            sortZtoA: "Sort Z to A",
            sort: "Sort",
            customSort: "Custom Sort...",
            filter: "Filter",
            clear: "Clear",
            clearFilter: "Clear Filter",
            reapply: "Reapply",
            dataValidation: "Data Validation",
            dataValidation1: "Data Validation",
            circleInvalidData: "Circle Invalid Data",
            clearInvalidCircles: "Clear Invalid Circles",
            group: "Group",
            unGroup: "Ungroup",
            subtotal: "Subtotal",
            showDetail: "Show Detail",
            hideDetail: "Hide Detail",
            designMode: "Design Mode",
            enterTemplate: "Enter Template Design Mode",
            template: "Template",
            bindingPath: "BindingPath",
            loadSchemaTitle: "Load schema to get a tree view",
            loadSchema: "Load Schema",
            loadDataSourceFilter: {
                json: "JSON File(*.json)",
                txt: "Normal text file(*.txt)"
            },
            saveDataSourceFilter: {
                json: "JSON File(*.json)"
            },
            saveSchemaTitle: "Save schema into a json file",
            saveSchema: "Save Schema",
            autoGenerateColumns: "AutoGenerateColumns",
            columns: "Columns",
            name: "Name",
            details: "Details",
            ok: "Ok",
            cancel: "Cancel",
            loadDataError: "Please load a json file.",
            addNode: "add node",
            remove: "remove",
            rename: "rename",
            table: "Table",
            selectOptions: "select options",
            clearBindingPath: "Clear BindingPath",
            dataField: "DataField",
            warningTable: "The column count of the table will change. Do you want to continue?",
            warningDataField: "Would you like to change the \"autoGenerateColumns\" to 'false' and set the datafield anyway?",
            checkbox: "CheckBox",
            hyperlink: "HyperLink",
            combox: "Combox",
            button: "Button",
            text: "Text",
            autoGenerateLabel: "AutoGenerateLabel",
            autoGenerateLabelTip: "Automatically Generate Data Label",
            unallowableTableBindingTip: "DataField only can be set on table. Please select a table.",
            overwriteCellTypeWarning: "Overwrite current cell type?",
            removeNodeWarning: "The removing node has some children nodes. Would you like to remove them?",
            unallowComboxBindingTip: "Combox items only can be set in Combox. Please select a combox.",
            rowOutline: "RowOutline",
            unallowOneRowSubtotal: "This can't be applied to the selected range. Select more than one row for range and try again.",
            unallowTableSubtotal: "Subtotals are not supported in tables.",
            canNotAppliedRange: "This can't be applied to the selected range. Select a single cell in a range and try again."
        },
        view: {
            view: "VIEW",
            showHide: "Show/Hide",
            zoom: "Zoom",
            viewport: "Viewport",
            rowHeader: "Row Header",
            columnHeader: "Column Header",
            verticalGridline: "Vertical Gridlines",
            horizontalGridline: "Horizontal Gridlines",
            tabStrip: "TabStrip",
            newTab: "NewTab",
            rowHeaderTip: "Show/Hide RowHeader",
            columnHeaderTip: "Show/Hide ColumnHeader",
            verticalGridlineTip: "Show/Hide Vertical Gridlines",
            horizontalGridlineTip: "Show/Hide Horizontal Gridlines",
            tabStripTip: "Show/Hide TabStrip",
            newTabTip: "Show/Hide NewTab",
            zoomToSelection: "ZoomTo Selection",
            zoomToSelection1: "ZoomTo Selection",
            freezePane: "Freeze Pane",
            freezePane1: "Freeze Pane",
            freezeTopRow: "Freeze Top Row",
            freezeFirstColumn: "Freeze First Column",
            freezeBottomRow: "Freeze Bottom Row",
            freezeLastColumn: "Freeze Last Column",
            unFreezePane: "Unfreeze Pane",
            unFreezePane1: "Unfreeze Pane"
        },
        setting: {
            setting: "SETTINGS",
            spreadSetting: "Spread Settings",
            sheetSetting: "Sheet Settings",
            general: "General",
            generalTip: "General Setting",
            scrollBars: "ScrollBars",
            tabStrip: "TabStrip",
            gridLines: "GridLines",
            calculation: "Calculation",
            headers: "Headers"
        },
        sparkLineDesign: {
            design: "DESIGN",
            type: "Type",
            show: "Show",
            style: "Style",
            groups: "Groups",
            line: "Line",
            column: "Column",
            winLoss: "Win/Loss",
            lineTip: "Line Sparkline",
            columnTip: "Column Sparkline",
            winLossTip: "Win/Loss Sparkline",
            highPoint: "High Point",
            lowPoint: "Low Point",
            negativePoint: "Negative Point",
            firstPoint: "First Point",
            lastPoint: "Last Point",
            markers: "Markers",
            highPointTip: "Toggle Sparkline High Point",
            lowPointTip: "Toggle Sparkline Low Point",
            negativePointTip: "Toggle Sparkline Negative Point",
            firstPointTip: "Toggle Sparkline First Point",
            lastPointTip: "Toggle Sparkline Last Point",
            markersTip: "Toggle Sparkline Marker Point ",
            sparklineColor: "Sparkline Color",
            markerColor: "Marker Color",
            sparklineWeight: "Sparkline Weight",
            customWeight: "Custom Weight...",
            group: "Group",
            groupTip: "Group Selected Sparkline",
            unGroupTip: "Ungroup Selected Sparkline",
            unGroup: "Ungroup",
            clear: "Clear",
            clearSparkline: "Clear Selected Sparkline",
            clearSparklineGroup: "Clear Selected Sparkline Groups"
        },
        formulaSparklineDesign: {
            design: "DESIGN",
            argument: "Argument",
            settings: "Settings"
        },
        tableDesign: {
            design: "DESIGN",
            tableName: "Table Name",
            resizeTable: "Resize Table",
            reszieHandler: 'Resize Handle',
            tableOption: "Table Style Options",
            property: "Properties",
            headerRow: "Header Row",
            totalRow: "Total Row",
            bandedRows: "Banded Rows",
            firstColumn: "First Column",
            lastColumn: "Last Column",
            bandedColumns: "Banded Columns",
            filterButton: "Filter Button",
            tableStyle: "Table Styles",
            style: "Styles",
            tools: "Tools",
            insertSlicer: "Insert Slicer",
            toRange: "Convert to Range",
            totalRowList: "TotalRow List",
            moreFunctions: "More Functions...",
            allowAutoExpand: "Allow Auto Expand"
        },
        chartDesign: {
            design: "DESIGN",
            chartLayouts: "Chart Layouts",
            addChartElement: "Add Chart Element",
            addChartElement1: "Add Chart Element",
            quickLayout: "Quick Layout",
            changeColors: "Change Colors",
            axes: "Axes",
            chartStyles: "Chart Styles",
            switchRowColumn: "Switch Row/Column",
            selectData: "Select Data",
            data: "Data",
            changeChartType: "Change Chart Type",
            changeChartType1: "Change Chart Type",
            type: "Type",
            moveChart: "Move Chart",
            location: "Location",
            chartTemplate: "Chart Template"
        },
        shapeDesign: {
            design: "DESIGN",
            shape: "Shape",
            changeShapeStyle: "Shape Styles",
            changeShapeType: "Change Shape",
            insertShape: "Insert Shape",
            backColor: "Shape Fill",
            fontColor: "Text Fill",
            color: "Color",
            themeStyle: "Theme Styles",
            presets: "Presets",
            size: "Size",
            width: "Width",
            height: "Height",
            rotate: "Rotate",
            arrange: "Arrange",
            group: "Group",
            regroup: "Regroup",
            ungroup: "Ungroup",
            rotateright90: "Rotate Right 90°",
            rotateleft90: "Rotate Left 90°"
        },
        insertShapeDialog: {
            errorPrompt: {
                unexpectedErrorMsg: "error",
            }
        },
        fontFamilies: {
            ff1: { name: "Arial", text: "Arial" },
            ff2: { name: "Arial Black", text: "Arial Black" },
            ff3: { name: "Calibri", text: "Calibri" },
            ff4: { name: "Cambria", text: "Cambria" },
            ff5: { name: "Candara", text: "Candara" },
            ff6: { name: "Century", text: "Century" },
            ff7: { name: "Courier New", text: "Courier New" },
            ff8: { name: "Comic Sans MS", text: "Comic Sans MS" },
            ff9: { name: "Garamond", text: "Garamond" },
            ff10: { name: "Georgia", text: "Georgia" },
            ff11: { name: "Malgun Gothic", text: "Malgun Gothic" },
            ff12: { name: "Mangal", text: "Mangal" },
            ff13: { name: "Meiryo", text: "Meiryo" },
            ff14: { name: "MS Gothic", text: "MS Gothic" },
            ff15: { name: "MS Mincho", text: "MS Mincho" },
            ff16: { name: "MS PGothic", text: "MS PGothic" },
            ff17: { name: "MS PMincho", text: "MS PMincho" },
            ff18: { name: "Tahoma", text: "Tahoma" },
            ff19: { name: "Times", text: "Times" },
            ff20: { name: "Times New Roman", text: "Times New Roman" },
            ff21: { name: "Trebuchet MS", text: "Trebuchet MS" },
            ff22: { name: "Verdana", text: "Verdana" },
            ff23: { name: "Wingdings", text: "Wingdings" }
        },
        slicerOptions: {
            options: "OPTIONS",
            slicerCaptionShow: "Slicer Caption:",
            slicerCaption: "Slicer Caption",
            slicerSettings: "Slicer Settings",
            slicer: "Slicer",
            styles: "Styles",
            slicerStyles: "Slicer Styles",
            columnsShow: "Columns:",
            heightShow: "Height:",
            widthShow: "Width:",
            columns: "Columns",
            height: "Height",
            width: "Width",
            buttons: "Buttons",
            size: "Size",
            shapeHeight: "Shape Height",
            shapeWidth: "Shape Width"
        }
    };
    en_res.shapeSliderPanel = {
        fillAndLine: "Fill & Line",
        formatShape: "Format Shape",
        shapeOptions: "Shape Options",
        textOptions: "Text Options",
        textFill: "Text Fill",
        textbox: "Text Box",
        fill: "Fill",
        noFill: "No fill",
        solidFill: "Solid fill",
        color: "Color",
        transparency: "Transparency",
        line: "Line",
        noLine: "No line",
        solidLine: "Solid line",
        dashType: "Dash type",
        capType: "Cap type",
        joinType: "Join type",
        beginArrowType: "Begin Arrow type",
        beginArrowSize: "Begin Arrow size",
        endArrowType: "End Arrow type",
        endArrowSize: "End Arrow size",
        width: "Width",
        height: "Height",
        size: "Size",
        rotation: "Rotation",
        moveSizeWithCells: "Move and size with cells",
        moveNoSizeWithCells: "Move but don't size with cells",
        noMoveNoSizeWithCells: "Don't move or size with cells",
        printObject: "Print object",
        locked: "Locked",
        allowResize: "Allow Resize",
        allowRotate: "Allow Rotate",
        allowMove: "Allow Move",
        showHandle: "Show Handle",
        vAlign: "Vertical Alignment",
        hAlign: "Horizontal Alignment",
        flat: "Flat",
        square: "Square",
        round: "Round",
        miter: "Miter",
        bevel: "Bevel",
        solid: "Solid",
        squareDot: "SquareDot",
        dash: "Dash",
        longDash: "LongDash",
        dashDot: "DashDot",
        longDashDot: "LongDashDot",
        longDashDotDot: "LongDashDotDot",
        sysDash: "SysDash",
        sysDot: "SysDot",
        sysDashDot: "SysDashDot",
        dashDotDot: "DashDotDot",
        roundDot: "RoundDot",
        center: "Center",
        bottom: "Bottom",
        top: "Top",
        left: "Left",
        right: "Right",
        textEditor: "Text Editor",
        text: "Text",
        font: "Font",
        properties: "Properties",
        normal: "normal",
        italic: "italic",
        oblique: "oblique",
        bold: "bold",
        bolder: "bolder",
        lighter: 'lighter',
        fontSize: 'Font Size',
        fontFamily: 'Font Family',
        fontStyle: 'Font Style',
        fontWeight: 'Font Weight',
    };
    en_res.insertShapeDialog = {
        lines: "Lines",
        rectangles: "Rectangles",
        basicShapes: "Basic Shapes",
        blockArrows: "Block Arrows",
        equationShapes: "Equation Shapes",
        flowChart: "Flowchart",
        starsAndBanners: "Stars and Banners",
        callouts: "Callouts"
    };
    en_res.shapeType = {
        actionButtonBackorPrevious: "Action Button Back or Previous",
        actionButtonBeginning: "Action Button Beginning",
        actionButtonCustom: "Action Button Custom",
        actionButtonDocument: "Action Button Document",
        actionButtonEnd: "Action Button End",
        actionButtonForwardorNext: "Action Button Forward or Next",
        actionButtonHelp: "Action Button Help",
        actionButtonHome: "Action Button Home",
        actionButtonInformation: "Action Button Information",
        actionButtonMovie: "Action Button Movie",
        actionButtonReturn: "Action Button Return",
        actionButtonSound: "Action Button Sound",
        arc: "Arc",
        balloon: "Balloon",
        bentArrow: "Bent Arrow",
        bentUpArrow: "Bent Up Arrow",
        bevel: "Bevel",
        blockArc: "Block Arc",
        can: "Can",
        chartPlus: "Chart Plus",
        chartStar: "Chart Star",
        chartX: "Chart X",
        chevron: "Chevron",
        chord: "Chord",
        circularArrow: "Circular Arrow",
        cloud: "Cloud",
        cloudCallout: "Cloud Callout",
        corner: "Corner",
        cornerTabs: "Corner Tabs",
        cross: "Cross",
        cube: "Cube",
        curvedDownArrow: "Curved Down Arrow",
        curvedDownRibbon: "Curved Down Ribbon",
        curvedLeftArrow: "Curved Left Arrow",
        curvedRightArrow: "Curved Right Arrow",
        curvedUpArrow: "Curved Up Arrow",
        curvedUpRibbon: "Curved Up Ribbon",
        decagon: "Decagon",
        diagonalStripe: "Diagonal Stripe",
        diamond: "Diamond",
        dodecagon: "Dodecagon",
        donut: "Donut",
        doubleBrace: "Double Brace",
        doubleBracket: "Double Bracket",
        doubleWave: "Double Wave",
        downArrow: "Down Arrow",
        downArrowCallout: "Down Arrow Callout",
        downRibbon: "Down Ribbon",
        explosion1: "Explosion 1",
        explosion2: "Explosion 2",
        flowchartAlternateProcess: "Flowchart: Alternate Process",
        flowchartCard: "Flowchart: Card",
        flowchartCollate: "Flowchart: Collate",
        flowchartConnector: "Flowchart: Connector",
        flowchartData: "Flowchart: Data",
        flowchartDecision: "Flowchart: Decision",
        flowchartDelay: "Flowchart: Delay",
        flowchartDirectAccessStorage: "Flowchart: Direct Access Storage",
        flowchartDisplay: "Flowchart: Display",
        flowchartDocument: "Flowchart: Document",
        flowchartExtract: "Flowchart: Extract",
        flowchartInternalStorage: "Flowchart: Internal Storage",
        flowchartMagneticDisk: "Flowchart: Magnetic Disk",
        flowchartManualInput: "Flowchart: Manual Input",
        flowchartManualOperation: "Flowchart: Manual Operation",
        flowchartMerge: "Flowchart: Merge",
        flowchartMultidocument: "Flowchart: Multidocument",
        flowchartOfflineStorage: "Flowchart: Offline Storage",
        flowchartOffpageConnector: "Flowchart: Offpage Connector",
        flowchartOr: "Flowchart: Or",
        flowchartPredefinedProcess: "Flowchart: Predefined Process",
        flowchartPreparation: "Flowchart: Preparation",
        flowchartProcess: "Flowchart: Process",
        flowchartPunchedTape: "Flowchart: PunchedTape",
        flowchartSequentialAccessStorage: "Flowchart: Sequential Access Storage",
        flowchartSort: "Flowchart: Sort",
        flowchartStoredData: "Flowchart: Stored Data",
        flowchartSummingJunction: "Flowchart: Summing Junction",
        flowchartTerminator: "Flowchart: Terminator",
        foldedCorner: "Folded Corner",
        frame: "Frame",
        funnel: "Funnel",
        gear6: "Gear 6",
        gear9: "Gear 9",
        halfFrame: "Half Frame",
        heart: "Heart",
        heptagon: "Heptagon",
        hexagon: "Hexagon",
        horizontalScroll: "Horizontal Scroll",
        isoscelesTriangle: "Isosceles Triangle",
        leftArrow: "Left Arrow",
        leftArrowCallout: "Left Arrow Callout",
        leftBrace: "Left Brace",
        leftBracket: "Left Bracket",
        leftCircularArrow: "Left Circular Arrow",
        leftRightArrow: "Left-Right Arrow",
        leftRightArrowCallout: "Left-Right Arrow Callout",
        leftRightCircularArrow: "left-Right Circular Arrow",
        leftRightRibbon: "Left-Right Ribbon",
        leftRightUpArrow: "Left-Right-Up Arrow",
        leftUpArrow: "Left-Up Arrow",
        lightningBolt: "Lightning Bolt",
        lineCallout1: "Line Callout 1",
        lineCallout1AccentBar: "Line Callout 1 (Accent Bar)",
        lineCallout1BorderandAccentBar: "Line Callout 1 (Border and Accent Bar)",
        lineCallout1NoBorder: "Line Callout1 (No Border)",
        lineCallout2: "Line Callout 2",
        lineCallout2AccentBar: "Line Callout 2 (Accent Bar)",
        lineCallout2BorderandAccentBar: "Line Callout 2 (Border and Accent Bar)",
        lineCallout2NoBorder: "Line Callout 2 (No Border)",
        lineCallout3: "Line Callout3",
        lineCallout3AccentBar: "Line Callout 3 (Accent Bar)",
        lineCallout3BorderandAccentBar: "Line Callout 3 (Border and Accent Bar)",
        lineCallout3NoBorder: "Line Callout 3 (No Border)",
        lineCallout4: "Line Callout 4",
        lineCallout4AccentBar: "Line Callout 4 (Accent Bar)",
        lineCallout4BorderandAccentBar: "Line Callout 4 (Border and Accent Bar)",
        lineCallout4NoBorder: "Line Callout 4 (No Border)",
        lineInverse: "Line Inverse",
        mathDivide: "Division",
        mathEqual: "Equal",
        mathMinus: "Minus",
        mathMultiply: "Multiply",
        mathNotEqual: "Not Equal",
        mathPlus: "Plus",
        moon: "Moon",
        noSymbol: "\"No\" Symbol",
        nonIsoscelesTrapezoid: "Non Isosceles Trapezoid",
        notchedRightArrow: "Notched Right Arrow",
        octagon: "Octagon",
        oval: "Oval",
        ovalCallout: "Oval Callout",
        parallelogram: "Parallelogram",
        pentagon: "Pentagon",
        pie: "Pie",
        pieWedge: "Pie Wedge",
        plaque: "Plaque",
        plaqueTabs: "Plaque Tabs",
        quadArrow: "Quad Arrow",
        quadArrowCallout: "Quad Arrow Callout",
        rectangle: "Rectangle",
        rectangularCallout: "Rectangular Callout",
        regularPentagon: "Regular Pentagon",
        rightArrow: "Right Arrow",
        rightArrowCallout: "Right Arrow Callout",
        rightBrace: "Right Brace",
        rightBracket: "Right Bracket",
        rightTriangle: "Right Triangle",
        round1Rectangle: "Round Single Corner Rectangle",
        round2DiagRectangle: "Round Diagonal Corner Rectangle",
        round2SameRectangle: "Round Same Side Corner Rectangle",
        roundedRectangle: "Rounded Rectangle",
        roundedRectangularCallout: "Rounded Rectangular Callout",
        shape4pointStar: "4-Point Star",
        shape5pointStar: "5-Point Star",
        shape8pointStar: "8-Point Star",
        shape16pointStar: "16-Point Star",
        shape24pointStar: "24-Point Star",
        shape32pointStar: "32-Point Star",
        smileyFace: "Smiley Face",
        snip1Rectangle: "Snip Single Corner Rectangle",
        snip2DiagRectangle: "Snip Diagonal Corner Rectangle",
        snip2SameRectangle: "Snip Same Side Corner Rectangle",
        snipRoundRectangle: "Snip and Round Single Corner Rectangle",
        squareTabs: "Square Tabs",
        star6Point: "6-Point Star",
        star7Point: "7-Point Star",
        star10Point: "10-Point Star",
        star12Point: "12-Point Star",
        stripedRightArrow: "Striped Right Arrow",
        sun: "Sun",
        swooshArrow: "Swoosh Arrow",
        tear: "Tear",
        trapezoid: "Trapezoid",
        uTurnArrow: "u-Turn Arrow",
        upArrow: "Up Arrow",
        upArrowCallout: "Up Arrow Callout",
        upDownArrow: "Up-Down Arrow",
        upDownArrowCallout: "Up-Down Arrow Callout",
        upRibbon: "Up Ribbon",
        verticalScroll: "Vertical Scroll",
        wave: "Wave",
        line: "Line",
        lineArrow: "Line Arrow",
        lineArrowDouble: "Line Double Arrow",
        elbow: "Elbow Connector",
        elbowArrow: "Elbow Arrow Connector",
        elbowArrowDouble: "Elbow Double-Arrow Connector"
    };
    en_res.seriesDialog = {
        series: "Series",
        seriesIn: "Series in",
        rows: "Rows",
        columns: "Columns",
        type: "Type",
        linear: "Linear",
        growth: "Growth",
        date: "Date",
        autoFill: "AutoFill",
        dateUnit: "Date unit",
        day: "Day",
        weekday: "Weekday",
        month: "Month",
        year: "Year",
        trend: "Trend",
        stepValue: "Step Value",
        stopValue: "Stop Value"
    };

    en_res.customSortDialog = {
        sort: "Sort",
        addLevel: "Add Level",
        deleteLevel: "Delete Level",
        copyLevel: "Copy Level",
        options: "Options...",
        sortBy: "SortBy",
        sortBy2: "Sort by",
        thenBy: "Then by",
        sortOn: "SortOn",
        sortOrder: "SortOrder",
        sortOptions: "Sort Options",
        orientation: "Orientation",
        sortTopToBottom: "Sort top to bottom",
        sortLeftToRight: "Sort left to right",
        column: "Column ",
        row: "Row ",
        values: "Values",
        ascending: "A to Z",
        descending: "Z to A"
    };

    en_res.createTableDialog = {
        createTable: "Create Table",
        whereYourTable: "Where is the data for your table?"
    };

    en_res.createSparklineDialog = {
        createSparkline: "Create Sparklines",
        dataRange: "Data Range",
        locationRange: "Location Range",
        chooseData: "Choose the data that you want",
        chooseWhere: "Choose where you want the sparkline to be placed",
        warningText: "Location reference is not valid because the cells are not all in the same column or row. Select cells that are all in a single column or row",
        notSingleCell: "Location reference does not support cell range, it should be single cell"
    };

    en_res.dataValidationDialog = {
        dataValidation: "Data Validation",
        settings: "Settings",
        validationCriteria: "Validation Criteria",
        allow: "Allow",
        anyValue: "Any value",
        wholeNumber: "Whole number",
        decimal: "Decimal",
        list: "List",
        date: "Date",
        textLength: "Text length",
        custom: "Custom",
        data: "Data",
        dataLabel: "Data:",
        between: "between",
        notBetween: "not between",
        equalTo: "equal to",
        notEqualTo: "not equal to",
        greaterThan: "greater than",
        lessThan: "less Than",
        greaterEqual: "greater than or equal to",
        lessEqual: "less than or equal to",
        minimum: "Minimum:",
        maximum: "Maximum:",
        value: "Value:",
        startDate: "Start Date:",
        endDate: "End Date:",
        dateLabel: "Date:",
        length: "Length:",
        source: "Source:",
        formula: "Formula:",
        ignoreBlank: "Ignore Blank",
        inCellDropDown: "In-cell dropdown",
        inputMessage: "Input Message",
        highlightStyle: "Highlight Style",
        errorAlert: "Error Alert",
        showMessage: "Show input message when cell is selected",
        showMessage2: "When cell is selected, show this input message: ",
        title: "Title",
        showError: "Show error alert after invalid data is entered",
        showError2: "When user enters invalid data, show this error alert:",
        style: "Style:",
        stop: "Stop",
        warning: "Warning",
        information: "Information",
        errorMessage: "Error Message",
        clearAll: "Clear All",
        valueEmptyMessage: "Value cannot be empty.",
        minimumMaximumMessage: "The Maximum must be greater than or equal to the Minimum.",
        errorMessage1: "This value doesn't match the data validation restrictions defined for this cell.",
        errorMessage2: "This value doesn't match the data validation restrictions defined for this cell.\r\nContinue?",
        circle: "Circle",
        dogear: "Dogear",
        icon: "Icon",
        topLeft: "Top Left",
        topRight: "Top Right",
        bottomRight: "Bottom Right",
        bottomLeft: "Bottom Left",
        outsideRight: "Outside Right",
        outsideLeft: "Outside Left",
        color: "Color",
        position: "Position",
        selectIcon: "Select Icon",
        selectIcons: "Add Icon"
    };
    en_res.outlineColumnDialog = {
        collapsed: "Collapsed",
        expanded: "Expanded",
        custom: "Custom",
        maxLevel: "Max Level",
        showIndicator: "Show Indicator",
        customImage: "Custom Indicator Image",
        showImage: "Show Image",
        showCheckBox: "Show CheckBox",
        title: "Outline Column",
        indicatorImage: "Indicator Image :",
        itemImages: "Item Images :",
        icon: "Icon"
    };

    en_res.spreadSettingDialog = {
        spreadSetting: "Spread Settings",
        general: "General",
        settings: "Settings",
        allowDragMerge: "Allow Drag and Merge",
        allowDragDrop: "Allow Drag and Drop",
        allowFormula: "Allow User to Enter Formulas",
        allowDragFill: "Allow Drag and Fill",
        allowZoom: "Allow Zoom",
        allowUndo: "Allow Undo",
        allowOverflow: "Allow Overflow",
        scrollBars: "ScrollBars",
        visibility: "Visibility",
        verticalScrollBar: "Vertical ScrollBar",
        horizontalScrollBar: "Horizontal ScrollBar",
        scrollbarShowMax: "ScrollBar ShowMax",
        scrollbarMaxAlign: "ScrollBar MaxAlign",
        mobileScrollbar: "Use Mobile Scrollbar",
        tabStrip: "Tab Strip",
        tabStripVisible: "Tab Strip Visible",
        tabStripEditable: "Tab Strip Editable",
        newTabVisible: "New Tab Visible",
        tabStripRatio: "Tab Strip Ratio(as percentage)",
        clipboard: "Clipboard",
        allowCopyPasteExcelStyle: "Allow Copy Paste Excel Style",
        allowExtendPasteRange: "Allow Paste Extend Range",
        headerOptions: "Header Options",
        noHeaders: "No Headers",
        rowHeaders: " Row Headers",
        columnHeaders: "Column Headers",
        allHeaders: "All Headers",
        customListsTitle: "Custom Lists",
        customLists: "Custom lists:",
        listEntries: "List entries:",
        add: "Add",
        delete: "Delete",
        calcOnDemand: "Calculate on Demand",
        newList: "NEW LIST",
        deleteNotification: "List will be permanently deleted.",
        scrollByPixel: "Pixel Scrolling",
        scrollPixel: "Scroll by <pixels>",
        allowDynamicArray: "Allow DynamicArray",
        normalResizeMode: "normal",
        splitResizeMode: "split",
        rowResizeMode: "Row Resize Mode",
        columnResizeMode: "Column Resize Mode",
        allowAutoCreateHyperlink: "Allow Auto Create Hyperlink"
    };

    en_res.sheetSettingDialog = {
        sheetSetting: "Sheet Settings",
        general: "General",
        columnCount: "Column Count",
        rowCount: "Row Count",
        frozenColumnCount: "Frozen Column Count",
        frozenRowCount: "Frozen Row Count",
        trailingFrozenColumnCount: "Trailing Frozen Column Count",
        trailingFrozenRowCount: "Trailing Frozen Row Count",
        selectionPolicy: "Selection Policy",
        singleSelection: "Single Select",
        rangeSelection: "Range Select",
        multiRangeSelection: "MultiRange Select",
        protect: "Protect",
        gridlines: "Grid Lines",
        horizontalGridline: "Horizontal Gridlines",
        verticalGridline: "Vertical Gridlines",
        gridlineColor: "Gridline Color",
        calculation: "Calculation",
        referenceStyle: "Reference Style",
        a1: "A1",
        r1c1: "R1C1",
        headers: "Headers",
        columnHeaders: "Column Headers",
        rowHeaders: "Row Headers",
        columnHeaderRowCount: "Column Header Row Count",
        columnHeaderAutoText: "Column Header Auto Text",
        columnHeaderAutoIndex: "Column Header Auto Index",
        defaultRowHeight: "Default Row Height",
        columnHeaderVisible: "Column Header Visible",
        blank: "Blank",
        numbers: "Numbers",
        letters: "Letters",
        rowHeaderColumnCount: "Row Header Column Count",
        rowHeaderAutoText: "Row Header Auto Text",
        rowHeaderAutoIndex: "Row Header Auto Index",
        defaultColumnWidth: "Default Column Width",
        rowHeaderVisible: "Row Header Visible",
        sheetTab: "SheetTab",
        sheetTabColor: "Sheet Tab Color:"
    };

    en_res.groupDirectionDialog = {
        settings: "Settings",
        direction: "Direction",
        rowDirection: "Summary rows below detail",
        columnDirection: "Summary columns to right of detail",
        showrow: "Show Row Outline",
        showcol: "Show Column Outline"
    };

    en_res.insertSparklineDialog = {
        createSparklines: "Create Sparklines",
        dataRange: "Data Range:",
        dataRangeTitle: "Choose the data that you want",
        locationRange: "Location Range",
        locationRangeTitle: "Choose where you want the sparkline to be placed",
        errorDataRangeMessage: "Please input a valid range",
        isFormulaSparkline: "isFormulaSparkline"
    };
    en_res.cellStates = {
        cellStates: "Cell States",
        add: "Add Cell States",
        remove: "Remove Cell State",
        manage: "Manage Cell States",
        selectStateType: "Select a cell state type",
        normal: "normal",
        hover: "hover",
        invalid: "invalid",
        edit: "edit",
        readonly: "readonly",
        active: "active",
        selected: "selected",
        dirty: "dirty",
        selectRange: "Choose Range",
        range: "Range:",
        selectStyle: "Set style",
        formatStyle: "Format...",
        headRange: "Range",
        headStyle: "Style",
        headState: "State Type",
        title: "Create Cell States",
        list: "Cell State list:",
        forbidCorssSheet: "Please choose range in current worksheet",
        errorStateType: "Please select a valid cell state",
        errorStyle: "Please set a valid style for selected range",
        errorDataRangeMessage: "Please input a valid range"
    };

    en_res.sparklineWeightDialog = {
        sparklineWeight: "Sparkline Weight",
        inputWeight: "Enter the Sparkline weight(pt)",
        errorMessage: "Please input a valid weight."
    };

    en_res.sparklineMarkerColorDialog = {
        sparklineMarkerColor: "Sparkline Marker Color:",
        negativePoints: "Negative Points:",
        markers: "Markers:",
        highPoint: "High Point:",
        lowPoint: "Low Point:",
        firstPoint: "First Point:",
        lastPoint: "Last Point:"
    };

    en_res.resizeTableDialog = {
        title: "Resize Table",
        dataRangeTitle: "Select the new data range for your table:",
        note: "Note: The headers must remain in the same row,\r\nand the resulting table range must overlap \r\nthe original table range."
    };

    en_res.saveAsDialog = {
        title: "Save File",
        fileNameLabel: "File Name:"
    };
    en_res.statusBar = {
        zoom: 'Zoom',
        toolTipZoomPanel: 'Zoom level. Click to open the Zoom dialog box.'
    };

    en_res.calendarSparklineDialog = {
        calendarSparklineDialog: "CalendarSparklineDialog",
        monthSparklineDialog: "MonthSparkline Dialog",
        yearSparklineDialog: "YearSparkline Dialog",
        emptyColor: "Empty Color",
        startColor: "Start Color",
        middleColor: "Middle Color",
        endColor: "End Color",
        rangeColor: "Range Color",
        year: "Year",
        month: "Month"
    };
    en_res.barcodeDialog = {
        barcodeDialog: "Barcode Dialog",
        locationReference: "Location Reference",
        showLabel: "Show Label",
        barcodetype: "Barcode Type",
        value: "Value",
        color: "Color",
        errorCorrectionLevel: "Error Correction Level",
        backgroudColor: "Background Color",
        version: "Version",
        model: "Model",
        mask: "Mask",
        connection: "Connection",
        charCode: "CharCode",
        connectionNo: "Connection No",
        charset: "Charset",
        quietZoneLeft: "Quiet Zone Left",
        quietZoneRight: "Quiet Zone Right",
        quietZoneTop: "Quiet Zone Top",
        quietZoneBottom: "Quiet Zone Bottom",
        labelPosition: "Label Position",
        addOn: "AddOn",
        addOnLabelPosition: "AddOn Label Position",
        fontFamily: "Font Family",
        fontStyle: "Font Style",
        fontWeight: "Font Weight",
        fontTextDecoration: "Font TextDecoration",
        fontTextAlign: "Font TextAlign",
        fontSize: "Font Size",
        fileIdentifier: "File Identifier",
        structureNumber: "Structure Number",
        structureAppend: "Structure Append",
        ecc00_140Symbole: "Ecc000_140 Symbol Size",
        ecc200EndcodingMode: "Ecc200 Endcoding Mode",
        ecc200SymbolSize: "Ecc200 Symbol Size",
        eccMode: "Ecc Mode",
        compact: "Compact",
        columns: "Columns",
        rows: "Rows",
        groupNo: "GroupNo",
        grouping: "Grouping",
        codeSet: "Code Set",
        fullASCII: "Full ASCII",
        checkDigit: "Check Digit",
        nwRatio: "Wide And Narrow Bar Ratio",
        labelWithStartAndStopCharacter: "Label With Start And Stop Character"
    };
    en_res.pieSparklineDialog = {
        percentage: "Percentage",
        color: "Color",
        addColor: "Add Color",
        pieSparklineSetting: "PieSparkline Setting"
    };

    en_res.areaSparklineDialog = {
        title: "AreaSparkline Formula",
        points: "Points",
        min: "Minimum Value",
        max: "Maximum Value",
        line1: "Line 1",
        line2: "Line 2",
        positiveColor: "Positive Color",
        negativeColor: "Negative Color",
        areaSparklineSetting: "AreaSparkline Setting"
    };

    en_res.scatterSparklineDialog = {
        points1: "Points1",
        points2: "Points2",
        minX: "MinX",
        maxX: "MaxX",
        minY: "MinY",
        maxY: "MaxY",
        hLine: "HLine",
        vLine: "VLine",
        xMinZone: "XMinZone",
        yMinZone: "YMinZone",
        xMaxZone: "XMaxZone",
        yMaxZone: "YMaxZone",
        tags: "Tags",
        drawSymbol: "Draw Symbol",
        drawLines: "Draw Lines",
        color1: "Color 1",
        color2: "Color 2",
        dash: "Dash Line",
        scatterSparklineSetting: "ScatterSparkline Setting"
    };

    en_res.compatibleSparklineDialog = {
        title: "CompatibleSparkline Formula",
        style: "Style",
        show: "Show",
        group: "Group",
        data: "Data",
        dataOrientation: "DataOrientation",
        dateAxisData: "DateAxisData",
        dateAxisOrientation: "DateAxisOrientation",
        settting: "Setting",
        axisColor: "Axis",
        firstMarkerColor: "First Marker",
        highMarkerColor: "High Marker",
        lastMarkerColor: "Last Marker",
        lowMarkerColor: "Low Marker",
        markersColor: "Markers",
        negativeColor: "Negative",
        seriesColor: "Series",
        displayXAxis: "Display XAxis",
        showFirst: "Show First",
        showHigh: "Show High",
        showLast: "Show Last",
        showLow: "Show Low",
        showNegative: "Show Negative",
        showMarkers: "Show Markers",
        lineWeight: "Line Weight",
        displayHidden: "Show data in hidden rows and columns",
        displayEmptyCellsAs: "DisplayEmptyCellsAs",
        rightToLeft: "RightToLeft",
        minAxisType: "MinAxisType",
        maxAxisType: "MaxAxisType",
        manualMax: "ManualMax",
        manualMin: "ManualMin",
        gaps: "Gaps",
        zero: "Zero",
        connect: "Connect",
        vertical: "Vertical",
        horizontal: "Horizontal",
        stylesetting: "Style Setting",
        individual: "Individual",
        custom: "Custom",
        compatibleSparklineSetting: "CompatibleSparkline Setting",
        styleSetting: "Style Setting",
        errorMessage: "Please input a valid range."
    };

    en_res.bulletSparklineDialog = {
        bulletSparklineSetting: "BulletSparkline Setting",
        measure: "Measure",
        target: "Target",
        maxi: "Maxi",
        good: "Good",
        bad: "Bad",
        forecast: "Forecast",
        tickunit: "Tickunit",
        colorScheme: "ColorScheme",
        vertical: "Vertical"
    };

    en_res.spreadSparklineDialog = {
        spreadSparklineSetting: "SpreadSparkline Setting",
        points: "Points",
        showAverage: "ShowAverage",
        scaleStart: "ScaleStart",
        scaleEnd: "ScaleEnd",
        style: "Style",
        colorScheme: "ColorScheme",
        vertical: "Vertical",
        stacked: "Stacked",
        spread: "Spread",
        jitter: "Jitter",
        poles: "Poles",
        stackedDots: "StackedDots",
        stripe: "Stripe"
    };

    en_res.stackedSparklineDialog = {
        stackedSparklineSetting: "StackedSparkline Setting",
        points: "Points",
        colorRange: "ColorRange",
        labelRange: "LabelRange",
        maximum: "Maximum",
        targetRed: "TargetRed",
        targetGreen: "TargetGreen",
        targetBlue: "TargetBlue",
        targetYellow: "TargetYellow",
        color: "Color",
        highlightPosition: "HighlightPosition",
        vertical: "Vertical",
        textOrientation: "TextOrientation",
        textSize: "TextSize",
        textHorizontal: "Horizontal",
        textVertical: "Vertical",
        px: "px"
    };

    en_res.barbaseSparklineDialog = {
        hbarSparklineSetting: "HbarSparkline Setting",
        vbarSparklineSetting: "VbarSparkline Setting",
        value: "Value",
        colorScheme: "ColorScheme"
    };

    en_res.variSparklineDialog = {
        variSparklineSetting: "VariSparkline Setting",
        variance: "Variance",
        reference: "Reference",
        mini: "Mini",
        maxi: "Maxi",
        mark: "Mark",
        tickunit: "TickUnit",
        legend: "Legend",
        colorPositive: "ColorPositive",
        colorNegative: "ColorNegative",
        vertical: "Vertical"
    };
    en_res.boxplotSparklineDialog = {
        boxplotSparklineSetting: "BoxPlotSparkline Setting",
        points: "Points",
        boxPlotClass: "BoxPlotClass",
        showAverage: "Show Average",
        scaleStart: "ScaleStart",
        scaleEnd: "ScaleEnd",
        acceptableStart: "AcceptableStart",
        acceptableEnd: "AcceptableEnd",
        colorScheme: "ColorScheme",
        style: "Style",
        vertical: "Vertical",
        fiveNS: "5NS",
        sevenNS: "7NS",
        tukey: "Tukey",
        bowley: "Bowley",
        sigma: "Sigma3",
        classical: "Classical",
        neo: "Neo"
    };
    en_res.cascadeSparklineDialog = {
        cascadeSparklineSetting: "CascadeSparkline Setting",
        pointsRange: "PointsRange",
        pointIndex: "PointIndex",
        labelsRange: "LabelsRange",
        minimum: "Minimum",
        maximum: "Maximum",
        colorPositive: "ColorPositive",
        colorNegative: "ColorNegative",
        vertical: "Vertical"
    };

    en_res.multiCellFormula = {
        warningText: "It may contain different formula type in selected range. Please select a new range."
    };

    en_res.paretoSparklineDialog = {
        paretoSparklineSetting: "ParetoSparkline Setting",
        points: "Points",
        pointIndex: "PointIndex",
        colorRange: "ColorRange",
        target: "Target",
        target2: "Target2",
        highlightPosition: "HighlightPosition",
        label: "Label",
        vertical: "Vertical",
        none: "None",
        cumulated: "Cumulated",
        single: "Single"
    };

    en_res.sliderPanel = {
        title: "Field List"
    };

    en_res.protectionOptionDialog = {
        title: "Protect Sheet",
        label: "Allow all users of this worksheet to:",
        allowSelectLockedCells: "Select locked cells",
        allowSelectUnlockedCells: "Select unlocked cells",
        allowSort: "Sort",
        allowFilter: "Use AutoFilter",
        allowResizeRows: "Resize rows",
        allowResizeColumns: "Resize columns",
        allowEditObjects: "Edit objects",
        allowDragInsertRows: "Drag insert rows",
        allowDragInsertColumns: "Drag insert columns",
        allowInsertRows: "Insert rows",
        allowInsertColumns: "Insert columns",
        allowDeleteRows: "Delete rows",
        allowDeleteColumns: "Delete columns"
    };

    en_res.insertSlicerDialog = {
        insertSlicer: "Insert Slicer"
    };

    en_res.formatSlicerStyle = {
        custom: "Custom",
        light: "Light",
        dark: "Dark",
        other: "Other",
        newSlicerStyle: "New Slicer Style...",
        slicerStyle: "Slicer Style",
        name: "Name",
        slicerElement: "Slicer Element",
        format: "Format",
        clear: "Clear",
        preview: "Preview",
        exception: "This style name is already exists."
    };

    en_res.slicerElement = {
        wholeSlicer: "Whole Slicer",
        header: "Header",
        selectedItemWithData: "Selected Item With Data",
        selectedItemWithNoData: "Selected Item With No Data",
        unselectedItemWithData: "Unselected Item With Data",
        unselectedItemWithNoData: "Unselected Item With No Data",
        hoveredSelectedItemWithData: "Hover Selected Item With Data",
        hoveredSelectedItemWithNoData: "Hover Selected Item With No Data",
        hoveredUnselectedItemWithData: "Hover Unselected Item With Data",
        hoveredUnselectedItemWithNoData: "Hover Unselected Item With No Data"
    };

    en_res.slicerSettingDialog = {
        slicerSetting: "Slicer Settings",
        sourceName: "Source Name:",
        name: "Name:",
        header: "Header",
        display: "Display header",
        caption: "Caption:",
        items: "Item Sorting and Filtering",
        ascending: "Ascending(A to Z)",
        descending: "Descending(Z to A)",
        customList: "Use Custom Lists when sorting",
        hideItem: "Hide items with no data",
        visuallyItem: "Visually indicate items with no data",
        showItem: "Show items with no data last"
    };

    en_res.slicerPropertyDialog = {
        formatSlicer: "Format Slicer",
        position: "Position and Layout",
        size: "Size",
        properties: "Properties",
        pos: "Position",
        horizontal: "Horizontal:",
        vertial: "Vertial:",
        disableResizingMoving: "Disable resizing and moving",
        layout: "Layout",
        numberColumn: "Number of columns:",
        buttonHeight: "Button height:",
        buttonWidth: "Button width:",
        height: "Height:",
        width: "Width:",
        scaleHeight: "Scale Height:",
        scaleWidth: "Scale Width:",
        moveSize: "Move and size with cells",
        moveNoSize: "Move and don't size with cells",
        noMoveSize: "Don't move and size with cells",
        locked: "Locked"
    };
    en_res.errorGroupDialog = {
        errorGroup: "Error Group/Ungroup",
        errorGroupMessage: "Sheet has outline-column , Whether to continue operation ?"
    };
    en_res.tableErrDialog = {
        tableToRange: "Do you want to convert the table to a normal range ?",
        insertTableInArrayFormula: "Multi-cell Array formulas aren't allowed in tables"
    };

    en_res.selectData = {
        changeDataRange: 'Chart data range:',
        switchRowColumn: 'Switch Row/Column',
        legendEntries: 'Legend Entries (Series)',
        moveUp: 'Move Up',
        moveDown: 'Move Down',
        horizontalAxisLabels: 'Horizontal (Category) Axis Labels',
        add: 'Add',
        edit: 'Edit',
        remove: 'Remove',
        selectDataSource: 'Select Data Source',
        addSeries: 'Add Series',
        editSeries: 'Edit Series',
        editSeriesName: 'Edit Series Name',
        editSeriesValue: 'Edit Series Value',
        seriesName: 'Series name',
        seriesYValue: 'Series yValues',
        seriesXValue: 'Series xValues',
        seriesSize: 'Series size',
        errorPrompt: {
            cantRemoveLastSeries: "You can't remove the last series",
            seriesValueIsIllegal: 'Series value is illegal',
            cantSwitchRowColumn: "You can't switch row/column",
            categoryValueIsIllegal: "Category value is illegal",
            connectorShapeChangeShapeType: "You can't change line shape type"
        },
        noDataRange: 'The data range is too complex to be display. If a new range is selected, it will replace all of the series in the Series panel.',
        hiddenEmptyButton: "Hidden and Empty Cells",
        gaps: "Gaps",
        zero: "Zero",
        connectData: "Connect data points with line",
        showEmptyCell: "Show empty cell as:",
        chartHiddenEmptyCell: "Hidden and Empty Cell Settings",
        showNAasEmptyCell: "Show #N/A as an empty cell",
        showDataInHiddenRowsAndColumns: "Show data in hidden rows and columns",
        positive: "Positive Error Value",
        negative: "Negative Error Value"
    };

    en_res.addChartElement = {
        axes: {
            axes: 'Axes',
            moreAxisOption: 'More Axis Option'
        },
        axisTitles: {
            axisTitles: 'Axis Titles',
            moreAxisTitlesOption: 'More Axis Titles Option'
        },
        chartTitle: {
            chartTitle: 'Chart Title',
            moreChartTitleOption: 'More Chart Title Option'
        },
        gridLines: {
            gridLines: 'GridLines',
            moreGridLinesOption: 'More GridLines Option'
        },
        dataLabels: {
            dataLabels: 'Data Labels',
            moreDataLabelsOption: 'More Data Labels Option'
        },
        legend: {
            legend: 'Legend',
            moreLegendOption: 'More Legend Option'
        },
        trendline: {
            trendline: 'Trendline',
            moreTrendlineOption: 'More Trendline Options'
        },
        errorBar: {
            errorBar: 'Error Bars'
        },
        primaryHorizontal: 'Primary Horizontal',
        primaryVertical: 'Primary Vertical',
        secondaryHorizontal: 'Secondary Horizontal',
        secondaryVertical: 'Secondary Vertical',
        none: 'None',
        aboveChart: 'Above Chart',
        primaryMajorHorizontal: 'Primary Major Horizontal',
        primaryMajorVertical: 'Primary Major Vertical',
        primaryMinorHorizontal: 'Primary Minor Horizontal',
        primaryMinorVertical: 'Primary Minor Vertical',
        secondaryMajorHorizontal: 'Secondary Major Horizontal',
        secondaryMajorVertical: 'Secondary Major Vertical',
        secondaryMinorHorizontal: 'Secondary Minor Horizontal',
        secondaryMinorVertical: 'Secondary Minor Vertical',
        center: 'Center',
        insideEnd: 'Inside End',
        outsideEnd: 'Outside End',
        bestFit: 'Best Fit',
        above: 'Above',
        below: 'Below',
        show: 'Show',
        right: 'Right',
        top: 'Top',
        left: 'Left',
        bottom: 'Bottom',
        errorBarStandardError: 'Standard Error',
        errorBarPercentage: 'Percentage',
        errorBarStandardDeviation: 'Standard Deviation',
        moreErrorBarOption: 'More Error Bars Options...',
        trendlineLinear: 'Linear',
        trendlineExponential: 'Exponential',
        trendlineLinearForecast: 'Linear Forecast',
        trendlineMovingAverage: 'Moving Average'
    };
    en_res.InsertFunctionsChildrenDialog = {
        title: "Function Arguments",
        formula: "Formula"
    };
    en_res.chartErrorBar = {
        title: "Custom Error Bar"
    };
    en_res.chartErrorBarsDialog = {
        title: "Add Error Bars",
        label: "Add a Error Bars based on Series:"
    };
    en_res.chartTrendlineDialog = {
        title: "Add Trendline",
        label: "Add a Trendline based on Series:"
    };
    en_res.selectionError = {
        selectEmptyArea: "Select Area Error"
    };


    en_res.name = "en";

    designer.res = en_res;

})();

