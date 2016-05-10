
function Person(firstName, lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.getFullName = function() {
        return this.firstName + " " + this.lastName;
    }
}

//<vendor name="*">
//    <family value=".*">
//        <shellPrompt>[#>$]</shellPrompt>
//        <loginPrompt>[\\S]*(login|Username|login as):[\\S]*</loginPrompt>
//        <passwordPrompt>[\\S]*password:[\\S]*</passwordPrompt>
//        <morePrompt>
//            <prompt>Press|to continue or|to quit:|--More--</prompt>
//            <response>y</response>
//        </morePrompt>
//        <saveConfigCommand>save\r\nyes</saveConfigCommand>
//        <CommandProperty command=".*">
//            <errors>
//                <messagePattern>$ERROR</messagePattern>
//            </errors>
//        </CommandProperty>
//    </family>
//</vendor>
function VendorRule(vendor){
    this.vendor = vendor;
    this.family = [1,2,3,4];
    this.prompts = {
       login: "1",
       password: "2",
       shell: "3",
       more:[ { prompt : "4" , reply: "5" }]
    };
}

//var foo={
//   firstName: "Luis",
//   lastName:  "Marrero",
//   getFullName: function() {
//       return this.firstName+" "+this.lastName;
//   }
//};
//
//var goo =  Object.create(foo, { age: { value: 42 } } );

var MyJavaClass = Packages.util.JSONParsingTest;
//MyJavaClass.fun4(new Person("Luis", "Parker"));
//MyJavaClass.fun4(new Person("Kiara", "Parker"));
//MyJavaClass.fun4(new Person("Caleb", "Parker"));
//MyJavaClass.fun4(new Person("Josh", "Parker"));
MyJavaClass.fun5(new VendorRule("Josh"));