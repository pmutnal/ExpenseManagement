<!DOCTYPE html>
<html lang="en">
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<head>

    <title>Department Head Master</title>
    
 	<script type="text/javascript" src=<spring:url value="/scripts/commonJS.js"/> ></script>
    <script type="text/javascript" src=<spring:url value="/grid/pqgrid.min.js"/> ></script>
    <link rel="stylesheet" href=<spring:url value="/grid/pqgrid.min.css"/> />

   <script type="text/javascript">
   
   var departmentList=${departmentList};
   var employeeList=${employeeList};
   
   $(function () {
	   
        var colM = [
            { title: "", minWidth: 27, width: 27, type: "detail", resizable: false, editable:false },
            { title: "Branch Code", width: 100, dataIndx: "branchCode" },
            { title: "Branch Name", width: 100, dataIndx: "branchName",
            	filter: { type: "select",
    		        condition: 'equal',
    		        prepend: { '': '--Select--' },
    		        valueIndx: "branchName",
    		        labelIndx: "branchName",
    		        listeners: ['change']
    		    }
            },
            { title: "Active/Inactive", width: 100, dataType: "bool", align: "center", dataIndx: "status",
                editor: { type: "checkbox", style: "margin:3px 5px;" },
                render: function (ui) {
                    if(ui.cellData == true) return "Active";
                    else return "Inactive";
                 	
                }
            }
        ];

        var dataModel = {
            location: "remote",
            sorting: "local",            
            dataType: "JSON",
            method: "POST",
            recIndx: "branchId",
            rPPOptions: [1, 10, 20, 30, 40, 50, 100, 500, 1000],
            url: "branchList",
            getData: function (dataJSON) {
            	var data = dataJSON;
                //expand the first row.
                data[0]['pq_detail'] = { 'show': true };
                return { curPage: dataJSON.curPage, totalRecords: dataJSON.totalRecords, data: data };
            }
        }

        var $gridMain = $("div#grid_md").pqGrid({
            width: '100%', height: '100%-5',
            flexHeight: true,
            dataModel: dataModel,
            virtualX: true, virtualY: true,
            editable: false,
            colModel: colM,
            wrap: false,
            hwrap: false,            
            numberCell: { show: false },
            title: "<b>Branch Department Head Master</b>",                        
            resizable: true,
            freezeCols: 1,            
            selectionModel: { type: 'cell' },
            filterModel: { on: true, mode: "AND", header: true },
            detailModel: {
                cache: true,
                collapseIcon: "ui-icon-plus",
                expandIcon: "ui-icon-minus",
                init: function (ui) {
                    var rowData = ui.rowData,                        
                        detailobj = gridDetailModel( $(this), rowData ), //get a copy of gridDetailModel                        
                        $grid = $("<div></div>").pqGrid( detailobj ); //init the detail grid.
                        $grid.data( 'branchId' , rowData.branchId);
                    return $grid;
                }
            }
        });

        /* 
        * another grid in detail view.
        * returns a new copy of detailModel every time the function is called.
        * @param $gridMain {jQuery object}: reference to parent grid
        * @param rowData {Plain Object}: row data of parent grid
        */
    	
        var gridDetailModel = function( $gridMain, rowData ){
            return {
            	scrollModel: {
                    autoFit: true
                },
            	wrap: false,
                hwrap: false,
                resizable: false,
                columnBorders: false,
                sortable: false,
                numberCell: { show: false },
                track: true, //to turn on the track changes.
                flexHeight: true,
                toolbar: {
                    items: [
                        { type: 'button', icon: 'ui-icon-plus', label: 'Add New Department Head', listeners: [
                            { "click": function (evt, ui) {
                                var $grid = $(this).closest('.pq-grid');
                                addRow($grid);
                                //debugger;
                            }
                            }
                        ]
                        },
                        {
                            type: '</br><span style="color:red;font-weight:bold;font-size:20px" class="customMessage"></span>'
                        }
                    ]
                },
                dataModel: {
                    location: "remote",
                    dataType: "json",
                    method: "POST",
                    recIndx: "branchId",
                    getUrl: function() {
                        return { url: "departmentHeadList", data: "{\"branchId\":"+rowData.branchId+"}" };
                    },
                   
                    mimeType : 'application/json',
                     async: true,
               	    beforeSend: function(xhr) {   
                           xhr.setRequestHeader("Accept", "application/json; charset=UTF-8");
                           xhr.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
                       },
                    error: function (data) {
                        $gridMain.pqGrid( 'rowInvalidate', { rowData: rowData });
                    }
                    //url = "/pro/orderdetails.php?orderId=" + orderID //for PHP
                },
                colModel: [
                    
                    { title: "Department Head Id", dataType: "integer", dataIndx: "deptHeadId", hidden:true, width: 80 },
                    { title: "Department", dataIndx: "departmentId", width: 150,
                         editor: {                    
                            type: "select",
                            valueIndx: "departmentId",
                            labelIndx: "departmentName",
                            options: departmentList,
                            
                        } ,
                         render: function (ui) {
          			       var options = ui.column.editor.options,
          			           cellData = ui.cellData;
  	       			       for (var i = 0; i < options.length; i++) {
  	       			           var option = options[i];
  	       			           if (option.departmentId == ui.rowData.departmentId) {
  	       			               return option.departmentName;
  	       			           } 
  	       			       }
          			   }   
                    },
                    { title: "Department Head Name", dataIndx: "employeeId", width: 150,
                         editor: {                    
                            type: "select",
                            valueIndx: "employeeId",
                            labelIndx: "fullName",
                            options: employeeList,
                            
                        } ,
                         render: function (ui) {
          			       var options = ui.column.editor.options,
          			           cellData = ui.cellData;
  	       			       for (var i = 0; i < options.length; i++) {
  	       			           var option = options[i];
  	       			           if (option.employeeId == ui.rowData.employeeId) {
  	       			               return option.fullName;
  	       			           } 
  	       			       }
          			   }   
                    },
                    { title: "Active/Inactive", width: 100, dataType: "bool", align: "center", dataIndx: "status",
                        editor: { type: "checkbox", style: "margin:3px 5px;" },
                        render: function (ui) {
                            if(ui.cellData == true) return "Active";
                            else return "Inactive";
                         	
                        }
                    },
                    { title: "", width: 100, dataIndx: "status", hidden:true },
                    { title: "", width: 100, dataIndx: "createdBy", hidden:true },
                    { title: "", width: 100, dataIndx: "createdDate", hidden:true },
                    { title: "", editable: false, minWidth: 150, sortable: false, render: function (ui) {
                        return "<button type='button' class='edit_btn'>Edit</button>\
                            <button type='button' class='delete_btn'>Delete</button>";
                    	}
                    }

		        ],
                /*editable: true, 
                groupModel: {
                    dataIndx: ["branchId"],
                    dir: ["up"],
                    title: ["{0} - {1} product(s)"],
                    icon: [["ui-icon-triangle-1-se", "ui-icon-triangle-1-e"]]
                },       */
                scrollModel: {
                    autoFit: true
                },
                selectionModel: {
                    //type: 'cell'
                    type: 'none'
                },
                hoverMode: 'cell',
                editModel: {
                    //onBlur: 'validate',
                    saveKey: $.ui.keyCode.ENTER
                },
                editor: { type: 'textbox', select: true, style: 'outline:none;' },
                validation: {
                    icon: 'ui-icon-info'
                },
                pageModel: { type: "local" },
                cellBeforeSave: function (evt, ui) {
                    var $grid = $(this);
                    var isValid = $grid.pqGrid("isValid", ui);
                    if (!isValid.valid) {
                        return false;
                    }
                },
                refresh: function () {
                    //debugger;
                    var $grid = $(this);

                    //delete button
                    $grid.find("button.delete_btn").button({ icons: { primary: 'ui-icon-close'} })
                    .unbind("click")
                    .bind("click", function (evt) {
                        if (isEditing($grid)) {
                            return false;
                        }
                        var $tr = $(this).closest("tr"),
                            rowIndx = $grid.pqGrid("getRowIndx", { $tr: $tr }).rowIndx;
                        deleteRow(rowIndx, $grid);
                    });
                    //edit button
                    $grid.find("button.edit_btn").button({ icons: { primary: 'ui-icon-pencil'} })
                    .unbind("click")
                    .bind("click", function (evt) {
                        if (isEditing($grid)) {
                            return false;
                        }
                        var $tr = $(this).closest("tr"),
                            rowIndx = $grid.pqGrid("getRowIndx", { $tr: $tr }).rowIndx;
                        editRow(rowIndx, $grid);
                        return false;
                    });

                    //rows which were in edit mode before refresh, put them in edit mode again.
                    var rows = $grid.pqGrid("getRowsByClass", { cls: 'pq-row-edit' });
                    if (rows.length > 0) {
                        var rowIndx = rows[0].rowIndx;
                        editRow(rowIndx, $grid);
                    }
                },
                //make rows editable selectively.
                editable: function (ui) {
                    var $grid = $(this);
                    var rowIndx = ui.rowIndx;
                    if ($grid.pqGrid("hasClass", { rowIndx: rowIndx, cls: 'pq-row-edit' }) == true) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            };
        };
        
      //called by add button in toolbar.
        function addRow($grid) {
    	  debugger;
        	var branchId = $grid.data( 'branchId' );
     	   $(".customMessage").text("");
     	   
            if (isEditing($grid)) {
                return false;
            }
            //append empty row in the first row.
            var rowData = {branchId:branchId }; //empty row template
            $grid.pqGrid("addRow", { rowIndxPage: 0, rowData: rowData });

            var $tr = $grid.pqGrid("getRow", { rowIndxPage: 0 });
            if ($tr) {
                //simulate click on edit button.
                $tr.find("button.edit_btn").click();
            }
        }
      
      //to check whether any row is currently being edited.
        function isEditing($grid) {
            var rows = $grid.pqGrid("getRowsByClass", { cls: 'pq-row-edit' });
            if (rows.length > 0) {
                //focus on editor if any 
                $grid.find(".pq-editor-focus").focus();
                return true;
            }
            return false;
        }
      
        //called by edit button.
        function editRow(rowIndx, $grid) {
     	   $(".customMessage").text("");
     	   
            $grid.pqGrid("addClass", { rowIndx: rowIndx, cls: 'pq-row-edit' });
            $grid.pqGrid("editFirstCellInRow", { rowIndx: rowIndx });

            //change edit button to update button and delete to cancel.
            var $tr = $grid.pqGrid("getRow", { rowIndx: rowIndx }),
                $btn = $tr.find("button.edit_btn");
            
            $btn.button("option", { label: "Update", "icons": { primary: "ui-icon-check"} })
                .unbind("click")
                .click(function (evt) {
                    evt.preventDefault();
                    return update(rowIndx, $grid);
                });
            $btn.next().button("option", { label: "Cancel", "icons": { primary: "ui-icon-cancel"} })
                .unbind("click")
                .click(function (evt) {
                    $grid.pqGrid("quitEditMode");
                    $grid.pqGrid("removeClass", { rowIndx: rowIndx, cls: 'pq-row-edit' });
                    $grid.pqGrid("refreshRow", { rowIndx: rowIndx });
                    $grid.pqGrid("rollback");
                });
        }
        

        //called by update button.
        function update(rowIndx, $grid) {
    	  
           if ($grid.pqGrid("saveEditCell") == false) {
               return false;
           }
           var isValid = $grid.pqGrid("isValid", { rowIndx: rowIndx }).valid;
           
           if (!isValid) {
               return false;
           }
           var isDirty = $grid.pqGrid("isDirty");
           //var isDirty = true;
           if (isDirty) {
        	   var jsonToBeSend=new Object();
               var url,
                   rowData = $grid.pqGrid("getRowData", { rowIndx: rowIndx }),
                   recIndx = $grid.pqGrid("option", "dataModel.recIndx");
               $grid.pqGrid("removeClass", { rowIndx: rowIndx, cls: 'pq-row-edit' });
 	
               jsonToBeSend["branchId"] = rowData.branchId;
               jsonToBeSend["departmentId"] = rowData.departmentId;
               jsonToBeSend["employeeId"] = rowData.employeeId;
               jsonToBeSend["status"] = rowData.status;
               url = "addDepartmentHead";
               
               if (rowData[recIndx] == null || rowData[recIndx] == "") {
             	  //For new record
               }
               else {
             	  // For update
             	  jsonToBeSend["createdBy"] = rowData.createdBy;
               	  jsonToBeSend["createdDate"] = rowData.createdDate;
             	  jsonToBeSend["deptHeadId"] = rowData.deptHeadId;
               }
               
               $.ajax($.extend({}, ajaxObj, { 
               	context: $grid, 
           	    url: url, 
           	    type: 'POST', 
           	    data: JSON.stringify(jsonToBeSend),
           	    success: function(data) { 
           	    	if(data.serviceStatus=="SUCCESS"){
 	          	    	var recIndx = $grid.pqGrid("option", "dataModel.recIndx");
 	                    if (rowData[recIndx] == null || rowData[recIndx] == "") {
 	                       rowData.departmentHeadId= data.departmentHeadId;
 	                    } 
 	          	    	$grid.pqGrid("removeClass", { rowIndx: rowIndx, cls: 'pq-row-edit' });
 	          	    	$grid.pqGrid("commit");
           	    	}
           	    	else{
           	    		
           	    		$grid.pqGrid("rollback");
           	    	}
           	    	$(".customMessage").text(data.message);
           	    	
           	    },
           	    error:function(data) { 
           	    	$(".customMessage").text(data.message);
           	    }
           	    
           	}));
               
           }
           else {
        	   
               $grid.pqGrid("quitEditMode");
               $grid.pqGrid("removeClass", { rowIndx: rowIndx, cls: 'pq-row-edit' });
               $grid.pqGrid("refreshRow", { rowIndx: rowIndx });
           }
        } 
    });
   
   

</script>
</head>
<body>
  <div id="grid_md" style="margin:5px auto;"></div>
       
</body>
</html>