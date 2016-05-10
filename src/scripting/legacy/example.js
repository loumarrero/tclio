function createFunc(value)
function() value

function iface(map) {
    var ifaceImpl = {}
    for (key in map) ifaceImpl[key] = createFunc(map[key]);
    return ifaceImpl;
}
var Person = Packages.util.Person;

var someoneElse = new Person(iface({
    "firstname": "Someone",
    "lastname": "Else",
    "petNames": ["Fluffy", "Pickle"],
    "favouriteNumber": 5
}));

print(someoneElse);

//var benji = new Person(iface(JSON.parse("benji.js")));

//Person.print(benji);