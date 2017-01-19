'use strict';

App.controller('UserController', ['$scope', 'UserService', function($scope, UserService) {
	 $scope.loader = {
		       loading: false,
	 };
	 
	var self = this;
	self.user={id:null, username:'', address:'', email:'', processName:'SimpleProcess'};
	self.task={processId:null, caseId:'', type:'', priority:'', state:'', dueDate:'', lastUpdateDate:'', displayName:''};
	self.users=[];
	$scope.transactions=[];
	$scope.tasks=[];
	
	self.refreshResults = function(){
		$scope.loader.loading = true;
		self.fetchAllTransactions();
		self.fetchAllUsers();
		self.getTasks();
		$scope.loader.loading = false;
	};
	
	self.approve = function(task){
	};
	
	self.reject = function(task){
	};
	
	self.getTasks = function(){
		UserService.getTasks()
		.then(
				function(d) {
					$scope.tasks = d;
				},
				function(errResponse){
					console.error('Error while fetching Tasks: '+errResponse);
				}
		);
	};
	
	self.fetchAllTransactions = function(){
		UserService.fetchAllTransactions()
		.then(
				function(d) {
					$scope.transactions = d;
				},
				function(errResponse){
					console.error('Error while fetching Transactions: '+errResponse);
				}
		);
		//$scope.loader.loading = false;
	};
	
	self.fetchAllUsers = function(){
		UserService.fetchAllUsers()
		.then(
				function(d) {
					self.users = d;
				},
				function(errResponse){
					console.error('Error while fetching Users: '+errResponse);
				}
		);
	};

	self.createUser = function(user){
		$scope.loader.loading = true;
		UserService.createUser(user)
		.then(
				self.refreshResults	
		);
		
	};

	self.updateUser = function(user, id){
		$scope.loader.loading = true;
		UserService.updateUser(user, id)
		.then(
				self.refreshResults
		);
				
		
	};

	self.deleteUser = function(id){
		$scope.loader.loading = true;
		UserService.deleteUser(id)
		.then(
				self.refreshResults
		);
		
	};

	self.refreshResults();
	
	self.submit = function() {
		if(self.user.id==null){
			self.createUser(self.user);
		}else{
			self.updateUser(self.user, self.user.id);
		}
		self.reset();
	};

	self.edit = function(id){
		for(var i = 0; i < self.users.length; i++){
			if(self.users[i].id === id) {
				self.user = angular.copy(self.users[i]);
				break;
			}
		}
	};

	self.remove = function(id){
		if(self.user.id === id) {
			self.reset();
		}
		self.deleteUser(id);
	};


	self.reset = function(){
		self.user={id:null,username:'',address:'',email:'',processName:'SimpleProcess' };
		$scope.myForm.$setPristine(); 
	};

}]);
