const functions = require("firebase-functions");

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
const admin = require("firebase-admin");
admin.initializeApp();

exports.setWallpaper = functions.https.onCall((data, context) => {
  const groupNum = data.text;
  const topic = "868741";
  const message = {
    data: {
      score: "850",
      time: groupNum,
    },
    topic: topic,
  };

  // Send a message to devices subscribed to the provided topic.
  admin.messaging().send(message)
      .then((response) => {
        // Response is a message ID string.
        console.log("Successfully sent message:", response);
      })
      .catch((error) => {
        console.log("Error sending message:", error);
      });


  return 0;
});
