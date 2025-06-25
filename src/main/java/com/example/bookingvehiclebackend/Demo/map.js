function initMap() {
    var myLocation = { lat: 10.762622, lng: 106.660172 };
    var map = new google.maps.Map(document.getElementById("map"), {
        zoom: 15,
        center: myLocation
    });
    var marker = new google.maps.Marker({
        position: myLocation,
        map: map
    });
}