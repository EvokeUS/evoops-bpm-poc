<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>  
    <title>EVOOPS Demo</title>  
    <style>
      .source {
      	color: #0099cc;
      }      
      .username.ng-valid {
          background-color: lightgreen;
      }
      .username.ng-dirty.ng-invalid-required {
          background-color: red;
      }
      .username.ng-dirty.ng-invalid-minlength {
          background-color: yellow;
      }

      .email.ng-valid {
          background-color: lightgreen;
      }
      .email.ng-dirty.ng-invalid-required {
          background-color: red;
      }
      .email.ng-dirty.ng-invalid-email {
          background-color: yellow;
      }
      
      .bgimg {
    		background-image: url('/static/images/evoops.png');
    		background-repeat: no-repeat;
	   }
	   
	   #img-container {
  			width: 100%;
   			height: 100%;
		}

		#img-container img {
   			width: 100%;
		}
    </style>
     <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
     <link rel="stylesheet" href="/hr-web/static/css/spinner.css"/>
     <link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
  </head>
  <body ng-app="myApp" class="ng-cloak">
      <div class="generic-container" ng-controller="UserController as ctrl">
          <div class="panel panel-default">
              <div class="panel-heading"><span class="lead">User Registration Form (Updated) </span></div>
              <div class="formcontainer">
                  <form ng-submit="ctrl.submit()" name="myForm" class="form-horizontal">
                      <input type="hidden" ng-model="ctrl.user.id" />
                      <div class="row">
                          <div class="form-group col-md-12">
                              <label class="col-md-2 control-lable" for="file">Name:</label>
                              <div class="col-md-7">
                                  <input type="text" ng-model="ctrl.user.username" name="uname" class="username form-control input-sm" placeholder="Enter User's name" required ng-minlength="3"/>
                                  <div class="has-error" ng-show="myForm.$dirty">
                                      <span ng-show="myForm.uname.$error.required">This is a required field</span>
                                      <span ng-show="myForm.uname.$error.minlength">Minimum length required is 3</span>
                                      <span ng-show="myForm.uname.$invalid">This field is invalid </span>
                                  </div>
                              </div>
                          </div>
                      </div>
                      
                      <div class="row">
                          <div class="form-group col-md-12">
                              <label class="col-md-2 control-lable" for="file">Location Updated!!!!:</label>
                              <div class="col-md-7">
                                  <input type="text" ng-model="ctrl.user.address" class="form-control input-sm" placeholder="Enter User's Location"/>
                              </div>
                          </div>
                      </div>

                      <div class="row">
                          <div class="form-group col-md-12">
                              <label class="col-md-2 control-lable" for="file">Email:</label>
                              <div class="col-md-7">
                                  <input type="email" ng-model="ctrl.user.email" name="email" class="email form-control input-sm" placeholder="Enter User's Email" required/>
                                  <div class="has-error" ng-show="myForm.$dirty">
                                      <span ng-show="myForm.email.$error.required">This is a required field</span>
                                      <span ng-show="myForm.email.$invalid">This field is invalid </span>
                                  </div>
                              </div>
                          </div>
                      </div>
                      
                      <div class="row">
                          <div class="form-group col-md-12">
                              <label class="col-md-2 control-lable" for="file">BPM Process Name:</label>
                              <div class="col-md-7">
                                  <input type="text" ng-model="ctrl.user.processName" ng-init="ctrl.user.processName = 'SimpleProcess'" name="processName" class="form-control input-sm" placeholder="BPM Process Name"/>
                              </div>
                          </div>
                      </div>

                      <div class="row">
                          <div class="form-actions">
                          <table>
                          <tr>
                          	<td width="255px">&nbsp;</td>
                          	<td><input type="submit"  value="{{!ctrl.user.id ? 'Add' : 'Update'}}" class="btn btn-primary btn-sm" ng-disabled="myForm.$invalid">&nbsp;
                              <button type="button" ng-click="ctrl.reset()" class="btn btn-warning btn-sm" ng-disabled="myForm.$pristine">Reset</button>&nbsp;
                               <button type="button" ng-click="ctrl.refreshResults()" class="btn btn-warning btn-sm">Refresh</button>
                          	</td>
                          </tr>
                          </table>
                          </div>
                      </div>
                  </form>
              </div>
          </div>
           <!--spinner-->
        <dov class="row" col-xs-12>
            <div id="spinnerDiv" ng-show="loader.loading">
                <button ng-show="loader.loading" class="btn btn-success spinner">
                    Loading...
                    <i class="fa fa-spinner fa-spin"></i>
                </button>
            </div>
        </dov>
        
          <div class="panel panel-default">
                <!-- Default panel contents -->
              <div class="panel-heading"><span class="lead"><nobr>Transactions History: <span class="source">Mongo DB</span></nobr></span></div>
              <div class="tablecontainer">
                  <table class="table table-hover">
                      <thead>
                          <tr>
                              <th>Transaction ID</th>
                              <th>Request Type</th>
                              <th>Name</th>
                              <th>Address</th>
                              <th>Email</th>
                              <th>Requested By</th>
                              <th>Requested Time</th>
                          </tr>
                      </thead>
                      <tbody>
                          <tr ng-repeat="t in transactions">
                              <td><span ng-bind="t.id"></span></td>
                              <td><span ng-bind="t.requestType"></span></td>
                              <td><span ng-bind="t.username"></span></td>
                              <td><span ng-bind="t.address"></span></td>
                              <td><span ng-bind="t.email"></span></td>
                              <td><span ng-bind="t.requestedBy"></span></td>
                              <td><span ng-bind="t.requestTime"></span></td>
                          </tr>
                      </tbody>
                  </table>
              </div>
          </div>
          
          <div class="panel panel-default">
                <!-- Default panel contents -->
              <div class="panel-heading"><span class="lead">Tasks: <span class="source">Bonita REST API</span></span></div>
              <div class="tablecontainer">
                  <table class="table table-hover">
                      <thead>
                          <tr>
                          	  <th>Process ID</th>
                              <th>Case ID</th>
                              <th>Type</th>
                              <th>Priority</th>
                              <th>State</th>
                              <th>Due Date</th>
                              <th>Last Updated Date</th>
                              <th>Display Name</th>
                          </tr>
                      </thead>
                      <tbody>
                          <tr ng-repeat="ts in tasks">
                              <td><span ng-bind="ts.processId"></span></td>
                              <td><span ng-bind="ts.caseId"></span></td>
                              <td><span ng-bind="ts.type"></span></td>
                              <td><span ng-bind="ts.priority"></span></td>
                              <td><span ng-bind="ts.state"></span></td>
                              <td><span ng-bind="ts.dueDate"></span></td>
                              <td><span ng-bind="ts.lastUpdateDate"></span></td>
                              <td><span ng-bind="ts.displayName"></span></td>
                          </tr>
                      </tbody>
                  </table>
              </div>
          </div>
          
          <div class="panel panel-default">
                <!-- Default panel contents -->
              <div class="panel-heading"><span class="lead">Active Users: <span class="source">Oracle DB</span></span>&nbsp;(Table: <strong>UserData</strong>)</div>
              <div class="tablecontainer">
                  <table class="table table-hover">
                      <thead>
                          <tr>
                              <th>Id</th>
                              <th>Name</th>
                              <th>Address</th>
                              <th>Email</th>
                              <th width="20%"></th>
                          </tr>
                      </thead>
                      <tbody>
                          <tr ng-repeat="u in ctrl.users">
                              <td><span ng-bind="u.id"></span></td>
                              <td><span ng-bind="u.username"></span></td>
                              <td><span ng-bind="u.address"></span></td>
                              <td><span ng-bind="u.email"></span></td>
                              <td>
                              <button type="button" ng-click="ctrl.edit(u.id)" class="btn btn-success custom-width">Edit</button> 
                              <button type="button" ng-click="ctrl.remove(u.id)" class="btn btn-danger custom-width">Remove</button>
                              </td>
                          </tr>
                      </tbody>
                  </table>
              </div>
          </div>
      </div>
      <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.4/angular.js"></script>
      <script src="<c:url value='/static/js/app.js' />"></script>
      <script src="<c:url value='/static/js/service/user_service.js' />"></script>
      <script src="<c:url value='/static/js/controller/user_controller.js' />"></script>
  </body>
</html>