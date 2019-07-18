// Set the configuration for your app
// TODO: Replace with your project's config object
const config = {
    apiKey: "AIzaSyDjt5gAQA6Zzc5ffEIpL5vdU9xmZJWWhdY",
    authDomain: "projectstl-bf858.firebaseapp.com",
    databaseURL: "https://projectstl-bf858.firebaseio.com",
    storageBucket: "gs://projectstl-bf858.appspot.com",
};
firebase.initializeApp(config);

const preObject = document.getElementById('object');

const dbRefObject = firebase.database().ref().child('Dust');
const dbRefObjecttraffic_1 = firebase.database().ref().child('Traffic_1');
const dbRefObjecttraffic_2 = firebase.database().ref().child('Traffic_2');

dbRefObjecttraffic_1.on('value', function(snap){
    console.log(snap.val());
    $(".traffic_1").text(snap.val());
    if (snap.val() >= 10) {
        $("#good").css("opacity", "0");
        $("#soso").css("opacity", "0");
        $("#bad").css("position", "relative");
    }
    else if (snap.val() > 3 && snap.val() < 10) {
        $("#good").css("opacity", "0");
        $("#soso").css("position", "relative");
        $("#bad").css("opacity", "0");
    }
    else if (snap.val() < 3) {
        $("#good").css("position", "relative");
        $("#soso").css("opacity", "0");
        $("#bad").css("opacity", "0");
    }
});

dbRefObjecttraffic_2.on('value', function (snap) {
    console.log(snap.val());
    $(".traffic_2").text(snap.val());
})

