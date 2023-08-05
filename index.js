function resizeLocations() {
    var width = document.getElementsByClassName("Square").item(0).clientWidth;
    var locations = document.getElementsByClassName("Location");
    for(var i = 0; i < locations.length; i++)
    {
        locations.item(i).style.fontSize = "" + (width * .175) + "px";
    }
}