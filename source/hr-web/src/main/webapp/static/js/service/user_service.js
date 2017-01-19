'use strict';

App.factory('UserService', ['$http', '$q', function($http, $q){

	return {
		getTasks: function() {
			return $http.get('http://192.168.99.100:8181/hr-rest/hrrest/bpm/task')
			.then(
					function(response){
						return response.data;
					}, 
					function(errResponse){
						console.error('Error while fetching tasks');
						return $q.reject(errResponse);
					}
			);
		},
		
		fetchAllTransactions: function() {
			return $http.get('http://192.168.99.100:8181/hr-rest/hrrest/transactions')
			.then(
					function(response){
						return response.data;
					}, 
					function(errResponse){
						console.error('Error while fetching transactions');
						return $q.reject(errResponse);
					}
			);
		},
		
		fetchAllUsers: function() {
			return $http.get('http://192.168.99.100:8181/hr-rest/hrrest/users')
			.then(
					function(response){
						return response.data;
					}, 
					function(errResponse){
						console.error('Error while fetching users');
						return $q.reject(errResponse);
					}
			);
		},

		createUser: function(user){
			return $http.post('http://192.168.99.100:8181/hr-rest/hrrest/create/', user)
			.then(
					function(response){
						return response.data;
					}, 
					function(errResponse){
						console.error('Error while creating user');
						return $q.reject(errResponse);
					}
			);
		},

		updateUser: function(user, id){
			return $http.post('http://192.168.99.100:8181/hr-rest/hrrest/update', user)
			.then(
					function(response){
						return response.data;
					}, 
					function(errResponse){
						console.error('Error while updating user: '+id);
						return $q.reject(errResponse);
					}
			);
		},

		deleteUser: function(id){
			return $http.post('http://192.168.99.100:8181/hr-rest/hrrest/remove',id)
			.then(
					function(response){
						return response.data;
					}, 
					function(errResponse){
						console.error('Error while deleting user');
						return $q.reject(errResponse);
					}
			);
		}

	};
}]);
