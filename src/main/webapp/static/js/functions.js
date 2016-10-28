var users;
var txs;
var apiroot = "/GrainnesUserDbWebApp";
$(document).ready(function(){
	//alert("hello");
	
	//$("h1").on("click", function(){
		//$("#dlg").dialog();
	//});
	
	
	// read the user list from the web service
	var url= apiroot + "/users";
	var data={};
	$.getJSON(url, data, function(data, status, xhr){
		users = data;
		
		// update the ui
		populateList();
		
	});
	$("#addTransaction").on("click", function(){
		$("#dlgAddTransaction").dialog({
			title: "Add a Transaction",
			modal: true,
			buttons: {
				"Ok": function(){
					
				var url = apiroot + "/users/user/" + $("#id").val() + "/txs/tx/";
				var method = "PUT";
				var data = {
						id:-1,
						userId: parseInt($("#id").val()),
						description: $("#dlgDescription").val(),
						transactionDate: $("#dlgTransactionDate").val(),
						amount: parseFloat($("#dlgAmount").val())
				}
				$.ajax(url, {
					data:JSON.stringify(data),
					contentType:"application/json",
					method:method,
					success:function(){
						showStatusMessage("Transaction Added");
						//alert("OK");
						$("#dlgAddTransaction").dialog("close");
					},
					error:function(){
						showStatusMessage("Error adding transaction");
						// alert("error");
						$("#dlgAddTransaction").dialog("close");
					}
				});
			//	$(this).dialog("close");
					
				},
				"Cancel": function(){
				$(this).dialog("close");
				}
				
			}
		});
	});
	
	$("#leftUserList").on("change", function(){
		var id= this.value;
		var user = findUser(id);
		showUser(user);
		showTransactions(user.id);
		
		// TBD - undo all edits
		
	});
	
	$(document).on("click", ".deleteTransaction", function(event){
		//alert("clicked" + event.target.id); // to display the id of the clicked trash icon
		if($("#" + event.target.id).hasClass("btnDisabled")){ //event.target is the button, hasClass must be a jquery object
			return;
		}
		$("#dlgConfirm").dialog({
			modal:true,
			buttons:{
				"ok": function(){
					var tid = event.target.id.split("_")[1];
					
					var uid = $("#id").val();
					
					var url= apiroot + "/users/user/" + uid + "/txs/tx/" + tid;
					var method = "DELETE";
					var data = {};
					
				
						$.ajax(url, {
							data:JSON.stringify(data),
							contentType:"application/json",
							method:method,
							success:function(){
							showStatusMessage("Transaction deleted");
								//alert("deleted");
					
							},
							error:function(){
								showStatusMessage("Error Deleting transaction");
								//alert("error");
							
							}
						
					});
					$("#dlgConfirm").dialog("close");
				},
				"Cancel":function() {
					$("#dlgConfirm").dialog("close");
					
					
				}
			}
		});
		
			
		//alert(id);
	}); //add event handler for all items of class delete transaction
	
	$(document).on("click", ".editTransaction", function(event){
		if($("#" + event.target.id).hasClass("btnDisabled")){ //event.target is the button, hasClass must be a jquery object
			return;
		}
		// add controls to edit
		var tid = event.target.id.split("_")[1];
		
		tx = findTransaction(tid);
		
		// 3rd td tag is the description
		$("#tr_" + tid + " :nth-child(3)").html(
				"<input id=\"txDescription_" + tid + "\" type=\"text\"" +
				"value=\"" + tx.description + "\">");
		$("#tr_" + tid + " :nth-child(4)").html(
				"<input id=\"txTransactionDate_" + tid + "\" type=\"text\"" +
				"value=\"" + tx.transactionDate + "\" size=\"8\">");// setting html = to input
		$("#txTransactionDate_" + tid).datepicker({ dateFormat:'yy-mm-dd',
			changeYear: true,
			changeMonth: true,
			yearRange: "-120:+0"});
		$("#tr_" + tid + " :nth-child(5)").html(
				"<input id=\"txAmount_" + tid + "\" type=\"text\"" +
				"value=\"" + tx.amount + "\" size=\"8\">");
		
		$("#et_" + tid).addClass("btnDisabled"); //anytime selecting based on id need #
		$("#st_" + tid).removeClass("btnDisabled");
		$("#ct_" + tid).removeClass("btnDisabled");
	
	}); // end of edit
	
	$(document).on("click", ".cancelTransaction", function(event){
		if($("#" + event.target.id).hasClass("btnDisabled")){ //event.target is the button, hasClass must be a jquery object
			return;
		}
		//alert("cancel");
		// remove controls for editing
	
		var tid = event.target.id.split("_")[1];
		
		tx = findTransaction(tid);
		
		$("#tr_" + tid + " :nth-child(3)").html(tx.description); // setting html = to description
		$("#tr_" + tid + " :nth-child(4)").html(tx.transactionDate);
		$("#tr_" + tid + " :nth-child(5)").html(tx.amount); 
		
		$("#et_" + tid).removeClass("btnDisabled"); //anytime selecting based on id need #
		$("#st_" + tid).addClass("btnDisabled");
		$("#ct_" + tid).addClass("btnDisabled");
	}); // end of cancel
	
	
	
	
	$(document).on("click", ".saveTransaction", function(event){
		if($("#" + event.target.id).hasClass("btnDisabled")){ //event.target is the button, hasClass must be a jquery object
			return;
		}
		//alert("save");
		var tid = event.target.id.split("_")[1];
		
		tx = findTransaction(tid);
		tx.description = $("#txDescription_" + tid).val();
		tx.transactionDate = $("#txTransactionDate_" + tid).val();
		tx.amount = $("#txAmount_" + tid).val();
		
		var url = apiroot + "/users/user/" + $("#id").val() + "/txs/tx/";
		var method = "POST";
		
	
			$.ajax(url, {
				data:JSON.stringify(tx),
				contentType:"application/json",
				method:method,
				success:function(){
					//alert("edited");
					$("#tr_" + tid + " :nth-child(3)").html(tx.description); // setting html = to description
					$("#tr_" + tid + " :nth-child(4)").html(tx.transactionDate);
					$("#tr_" + tid + " :nth-child(5)").html(tx.amount); 
					
					showStatusMessage("Saved");
					
				},
				error:function(){
					showStatusMessage("Error saving transaction");
					
					//alert("error");
				tx = findTransaction(tid);
				}
			
		});
			$("#et_" + tid).removeClass("btnDisabled"); //anytime selecting based on id need #
			$("#st_" + tid).addClass("btnDisabled");
			$("#ct_" + tid).addClass("btnDisabled");
		// using ajax not json as this is the version that takes the method for update we need to use post
		
	}); // end of save transaction
	
	$(document).on("click", "#editUser", function(){
		if($("#editUser").hasClass("btnDisabled")){
			return;
		}
		$("#editUser").addClass("btnDisabled");
		$("#saveUser").removeClass("btnDisabled");
		$("#cancelUserEdit").removeClass("btnDisabled");
				
						
		$("#firstName").attr("readonly", false);
		$("#lastName").attr("readonly", false);
		$("#registered").attr("disabled", false);
		$("#dateOfBirth").attr("readonly", false);
	});
	$(document).on("click", "#saveUser", function(){
		if($("#saveUser").hasClass("btnDisabled")){
			return;
		}
		$("#editUser").removeClass("btnDisabled");
		$("#saveUser").addClass("btnDisabled");
		$("#cancelUserEdit").addClass("btnDisabled");
				
		var id =$("#id").val();
		var user = findUser(id);
		user.firstName = $("#firstName").val();
		user.lastName = $("#lastName").val();
		user.registered = $("#registered").prop("checked");
		user.dateOfBirth = $("#dateOfBirth").val();
		
		// call the api to update user
		var url = apiroot + "/users/user";
		var method ="POST";
		
		$.ajax(url, {
			data:JSON.stringify(user),
			contentType:"application/json",
			method:method,
			success:function(){
				
				showStatusMessage("User Saved");
				$("#firstName").attr("readonly", true);
				$("#lastName").attr("readonly", true);
				$("#registered").attr("disabled", true);
				$("#dateOfBirth").attr("readonly", true);
				
			},
			error:function(){
				showStatusMessage("Error ");
				$("#firstName").attr("readonly", true);
				$("#lastName").attr("readonly", true);
				$("#registered").attr("disabled", true);
				$("#dateOfBirth").attr("readonly", true);
			}
	});
});
	$(document).on("click", "#cancelUserEdit", function(){
		if($("#cancelUserEdit").hasClass("btnDisabled")){
			return;
		}
		$("#editUser").addClass("btnDisabled");
		$("#saveUser").removeClass("btnDisabled");
		$("#cancelUserEdit").removeClass("btnDisabled");
				
		// restore the original values
		var id =$("#id").val();
		var user = findUser(id);
		showUser(user);
		// showTransactions(user.id); // avoid unnecessary xhr calls
		
		$("#firstName").attr("readonly", true);
		$("#lastName").attr("readonly", true);
		$("#registered").attr("disabled", true);
		$("#dateOfBirth").attr("readonly", true);
	});
	
	$(document).on("click", "#deleteUser", function(){
		//alert("clicked" + event.target.id); // to display the id of the clicked trash icon
		if($("#deleteUser").hasClass("btnDisabled")){ //event.target is the button, hasClass must be a jquery object
			return;
		}
		$("#dlgConfirm").dialog({
			modal:true,
			buttons:{
				"ok": function(){
					
					var uid = $("#id").val();
					
					var url = apiroot + "/users/user/" + uid;
					var method = "DELETE";
					var data = {};
					
				
						$.ajax(url, {
							data:JSON.stringify(data),
							contentType:"application/json",
							method:method,
							success:function(){
							showStatusMessage("User deleted");
								//alert("deleted");
					
							},
							error:function(){
								showStatusMessage("Error Deleting User");
								//alert("error");
							
							}
						
					});
					$("#dlgConfirm").dialog("close");
					//write down the delete user function here
					deleteUserFromList(uid);
					populateList();
				},
				"Cancel":function() {
					$("#dlgConfirm").dialog("close");
					
					
				}
			}
		});
		
			
		//alert(id);
	}); //add event handler for all items of class delete transaction
	/*$(document).on("click", "#deleteUser", function(){
		if($("#deleteUser").hasClass("btnDisabled")){
			return;
		}
		alert("delete user");
		
	});*/

	
	
	
}); // end of on ready

function showStatusMessage(message){
	
$("#statusMessage").show(); // will show the greyed out saved word on screen when press save
$("#statusMessage").html(message);
$("#statusMessage").fadeOut(2500, function(){
	$("#statusMessage").html("");
});
} // end of status message

function showTransactions(id){
	url = apiroot + "/users/user/" + id + "/txs";
	data = {};// don't need to pass any data so data is a blank object here
	txs = {}; // make sure last list is emptied before call new one
	$.getJSON(url, data, function(data, status, xhr){
		
		txs = data;
		$("#bottomRightTransactions").empty();
		$.each(data, function(index, transaction){
			
			addTransactionToTable($("#bottomRightTransactions"), transaction);
			
			
		});
	});
}
function addTransactionToTable(table, transaction){
	table.append("<tr id=\"tr_" + transaction.id + "\">" +
			"<td>" + transaction.id + "</td>" +
			"<td>" + transaction.userId + "</td>" +
			"<td>" + transaction.description + "</td>" +
			"<td>" + transaction.transactionDate + "</td>" +
			"<td>" + transaction.amount + "</td>" +
			"<td id=\"dt_" + transaction.id + "\" class='deleteTransaction txButton fa fa-trash-o'></td>" +
			"<td id=\"et_" + transaction.id + "\" class='editTransaction txButton fa fa-edit'></td>" +
			"<td id=\"st_" + transaction.id + "\" class='saveTransaction txButton fa fa-floppy-o btnDisabled'></td>" +
			"<td id=\"ct_" + transaction.id + "\" class='cancelTransaction txButton fa fa-undo btnDisabled'></td>" +
				"</tr>"); // added id to transaction i.e. id = to make sure that id is unique
	
	
}
function showUser(user){
	$("#id").val(user.id);
	$('#firstName').val(user.firstName);
	$('#lastName').val(user.lastName);
	$('#registered').prop("checked", user.registered);
	$('#dateOfBirth').val(user.dateOfBirth);
}
function findUser(id){ // have you got the right id and if you do return that user
	for(var i=0; i<users.length; i++){
		if(users[i].id == id){
			return users[i];
		}
	}
}
function deleteUserFromList(id){
	for(var i=0; i<users.length; i++){
		if(users[i].id == id){
	// remove this user from the users array
			
	users.splice(i, 1); // splice fn available in javascript arrays. splice at char i and 1 user
		}
	}
}
function findTransaction(tid){ 
	for(var i=0; i<txs.length; i++){
		if(txs[i].id == tid){
			return txs[i];
		}
	}
}


function populateList() {
	//alert(users[0].firstName); // popup box that displays the firstName of the 1st user  
	
	$("#leftUserList").empty();
	
	$.each(users, function(index, user){
		
		$("#leftUserList").append("<option value='" + user.id + "'>" +
				user.firstName + " " + user.lastName +
				"</option>");
	});
	
	$("#leftUserList option:first").prop("selected", true);
	
	var user = users[0];
	showUser(user);
	showTransactions(user.id);
}