<!DOCTYPE html>
<html lang="en">
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<head>

    <title>Company Master</title>
   
 	<script type="text/javascript" src=<spring:url value="/scripts/commonJS.js"/> ></script>
    <script type="text/javascript" src=<spring:url value="/grid/pqgrid.min.js"/> ></script>
    <link rel="stylesheet" href=<spring:url value="/grid/pqgrid.min.css"/> />

   <script type="text/javascript">
   //var data = [{ "companyId": 1, "companyCode": "Exxon Mobil", "companyName": "339938.0" }];
   var companyList= ${companyList};
   $(function () {
		
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
       //called by add button in toolbar.
       function addRow($grid) {
    	   $(".alert").hide();
    	   
           if (isEditing($grid)) {
               return false;
           }
           //append empty row in the first row.                            
           var rowData = { companyId:"", companyCode: "", companyName:"", status:""}; //empty row template
           $grid.pqGrid("addRow", { rowIndxPage: 0, rowData: rowData });

           var $tr = $grid.pqGrid("getRow", { rowIndxPage: 0 });
           if ($tr) {
               //simulate click on edit button.
               $tr.find("button.edit_btn").click();
           }
       }
       //called by delete button.
       function deleteRow(rowIndx, $grid) {
    	   $(".alert").hide();
    	   
           $grid.pqGrid("addClass", { rowIndx: rowIndx, cls: 'pq-row-delete' });
           var rowData = $grid.pqGrid("getRowData", { rowIndx: rowIndx });
           var ans = window.confirm("Are you sure to delete row No " + (rowIndx + 1) + "?");

           if (ans) {
        	   var jsonToBeSend=new Object();
               $grid.pqGrid("deleteRow", { rowIndx: rowIndx, effect: true });

               var data = $grid.pqGrid("getRecId", { rowIndx: rowIndx });
               jsonToBeSend["companyId"] = data;
               $.ajax($.extend({}, ajaxObj, {
            	   type: 'DELETE', 
                   context: $grid,
                   dataType: 'json', 
                   url: "/SpringMVCSecruityMavenApp/deleteDepartment",
                   //url: "/pro/products.php?pq_delete=1",//for PHP
                   data: JSON.stringify(jsonToBeSend),
                   async: true,
             	   beforeSend: function(xhr) {                 
                       xhr.setRequestHeader("Accept", "application/json");
                       xhr.setRequestHeader("Content-Type", "application/json");
                   },
            	   success: function(data) { 
                      this.pqGrid("commit");
                      this.pqGrid("refreshDataAndView");
                   },
                   error: function () {
                       //debugger;
                       this.pqGrid("removeClass", { rowData: rowData, cls: 'pq-row-delete' });
                       this.pqGrid("rollback");
                   }
               }));
           }
           else {
               $grid.pqGrid("removeClass", { rowIndx: rowIndx, cls: 'pq-row-delete' });
           }
       }
       //called by edit button.
       function editRow(rowIndx, $grid) {
    	   $(".alert").hide();
    	   
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
	
              jsonToBeSend["companyName"] = rowData.companyName;
              jsonToBeSend["companyCode"] = rowData.companyCode;
              jsonToBeSend["status"] = rowData.status;
              url = "addCompany";
              
              if (rowData[recIndx] == null || rowData[recIndx] == "") {
            	  //For new record
              }
              else {
            	  // For update
            	  jsonToBeSend["createdBy"] = rowData.createdBy;
              	  jsonToBeSend["createdDate"] = rowData.createdDate;
            	  jsonToBeSend["companyId"] = rowData.companyId;
              }
              $.ajax($.extend({}, ajaxObj, { 
              	context: $grid,
          	    url: url, 
          	    type: 'POST', 
          	    dataType: 'json', 
          	    data: JSON.stringify(jsonToBeSend),
          	    async: true,
          	    beforeSend: function(xhr) {                 
                      xhr.setRequestHeader("Accept", "application/json");
                      xhr.setRequestHeader("Content-Type", "application/json");
                  },
          	    success: function(data) { 
          	    	if(data.serviceStatus=="SUCCESS"){
	          	    	var recIndx = $grid.pqGrid("option", "dataModel.recIndx");
	                    if (rowData[recIndx] == null || rowData[recIndx] == "") {
	                       rowData.companyId= data.companyId;
	                    } 
	          	    	$grid.pqGrid("removeClass", { rowIndx: rowIndx, cls: 'pq-row-edit' });
	          	    	$grid.pqGrid("commit");
	          	    	$grid.pqGrid("refreshRow", { rowIndx: rowIndx });
	          	    	$(".alert").addClass("alert-success").text(data.message).show();
          	    	}
          	    	else{
          	    		$(".alert").addClass("alert-danger").text(data.message).show();
          	    		$grid.pqGrid("rollback");
          	    	}
          	    	
          	    },
          	    error:function(data) { 
          	    	$(".alert").addClass("alert-danger").text(data.message).show();
          	    }
          	    
          	}));
              
          }
          else {
       	   
              $grid.pqGrid("quitEditMode");
              $grid.pqGrid("removeClass", { rowIndx: rowIndx, cls: 'pq-row-edit' });
              $grid.pqGrid("refreshRow", { rowIndx: rowIndx });
          }
       }
       //define the grid.
       var obj = {                        
           wrap: false,
           hwrap: false,
           resizable: true,
           columnBorders: false,
           sortable: false,
           numberCell: { show: false },
           filterModel: { on: true, mode: "AND", header: true },
           track: true, //to turn on the track changes.
           flexHeight: true,
           toolbar: {
               items: [
                   { type: 'button', icon: 'ui-icon-plus', label: 'Add New Company', listeners: [
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
           title: "<h1><b>Company Master</b></h1>",

           colModel: [
                  { title: "Company Id", dataType: "integer", dataIndx: "companyId", hidden:true},
                  { title: "Company Code", width: 140, dataType: "string", align: "right", dataIndx: "companyCode",
                	  filter: { type: 'textbox', attr: 'placeholder="Search Company Code"', condition: 'contain', listeners: ['keyup'] },
                       validations: [
                          { type: 'minLen', value: 1, msg: "Required." },
                          { type: 'maxLen', value: 40, msg: "length should be <= 40" }
                      ]
                  },
                  { title: "Company Name", width: 165, dataType: "string", dataIndx: "companyName",
                	  filter: { type: 'textbox', attr: 'placeholder="Search Company Name"', condition: 'contain', listeners: ['keyup'] },
                         validations: [
                          { type: 'minLen', value: 1, msg: "Required" },
                          { type: 'maxLen', value: 40, msg: "length should be <= 40" }
                      ]
                  },
                  
                  { title: "Active/Inactive", width: 100, dataType: "bool", align: "center", dataIndx: "status",
                	  filter: { type: "checkbox", subtype: 'triple', condition: "equal", listeners: ['click'] },
                      editor: { type: "checkbox", style: "margin:3px 5px;" },
                      render: function (ui) {
                          if(ui.cellData == true) return "Active";
                          else return "Inactive";
                       	
                      }
                  },
                  { title: "", width: 100, dataIndx: "createdBy", hidden:true },
                  { title: "", width: 100, dataIndx: "createdDate", hidden:true },
                  { title: "", editable: false, minWidth: 165, sortable: false, render: function (ui) {
                      return "<button type='button' class='edit_btn'>Edit</button>\
                          <button type='button' class='delete_btn'>Delete</button>";
                  }
                  }
          ],
           dataModel: {
               dataType: "JSON",
               location: "local",
               recIndx: "companyId",
               data: companyList
           },
           pageModel: { type: "local" },
           cellBeforeSave: function (evt, ui) {
               var $grid = $(this);
               var isValid = $grid.pqGrid("isValid", ui);
               if (!isValid.valid) {
                   return false;
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
       var $grid = $("#grid_editing").pqGrid(obj);
       //use refresh & refreshRow events to display jQueryUI buttons and bind events.
       $grid.on('pqgridrefresh pqgridrefreshrow', function () {
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
       }); 
		$("#grid_editing").pqGrid("refresh")
   });


</script>
</head>
<body>
  <div id="grid_editing" style="margin: auto;">
</div>
       
</body>
</html>