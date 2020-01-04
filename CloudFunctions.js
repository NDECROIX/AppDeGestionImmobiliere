// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

// Listen for changes in all documents in the 'agents' collection
exports.newAgent = functions.firestore
    .document('agents/{agentId}')
    .onCreate((snap, context) => {
		// Get an object representing the document
		// e.g. {'firstName': 'mario', 'lastName': 'Bross'}
		const newValue = snap.data();

		// access a particular field as you would any JS property
		const agentFirstName = newValue.firstName;
		const agentLastName = newValue.lastName;

		// The topic name can be optionally prefixed with "/topics/".
		var topic = 'newAgent';

		var message = {
		  data: {
			firstName: agentFirstName,
			lastName: agentLastName
		  },
		  topic: topic
		};

		// Send a message to devices subscribed to the provided topic.
		admin.messaging().send(message)
		  .then((response) => {
			// Response is a message ID string.
			console.log('Successfully sent message:', response);
		  })
		  .catch((error) => {
			console.log('Error sending message:', error);
		  });
    });
	
// Listen for changes in all documents in the 'properties' collection
exports.newProperty = functions.firestore
    .document('properties/{propertyId}')
    .onCreate((snap, context) => {
		// Get an object representing the document
		// e.g. {'type': 'Flat', 'price': 5 000 000}
		const newValue = snap.data();

		// access a particular field as you would any JS property
		const propertyType = newValue.type;
		const propertyPrice = newValue.price.toString();

		// The topic name can be optionally prefixed with "/topics/".
		var topic = 'newProperty';

		var message = {
		  data: {
			type: propertyType,
			price: propertyPrice
		  },
		  topic: topic
		};

		// Send a message to devices subscribed to the provided topic.
		admin.messaging().send(message)
		  .then((response) => {
			// Response is a message ID string.
			console.log('Successfully sent message:', response);
		  })
		  .catch((error) => {
			console.log('Error sending message:', error);
		  });
    });