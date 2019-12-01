function storChk(type) {   // Function adapted from https://developer.mozilla.org/en-US/docs/Web/API/Web_Storage_API/Using_the_Web_Storage_API on 05/11/2019
    // Checking localStorage works
    let storage;
    try {
        storage = window[type];
        let x = '__storage_test__';
        storage.setItem(x, x);
        storage.removeItem(x);
        return true;
    }
    catch(e) {
        console.log("No storage available");
        return false;
    }
}

function getUsrNme() {
    //gets usrnme from localStorage, else returns null"
    let nme = localStorage.getItem("username");
    if (nme != null) {
        return nme
    } else {
        // ADD USERNAME HERE IF NEEDED AFTER LOGIN PAGE IS CREATED
        return "Guest"
    }
}

function asgnUsrNme() {
    // Assigning the var "usrName"
    if (storChk('localStorage')){       // if localStorage works...
        console.log("GETTING USRNME");
        return getUsrNme();               // get username from local storage
    } else {                                 //  else assign their username as 'guest'
        return "Guest";
    }
}

const usrNme = asgnUsrNme();

let name = document.getElementById("username");         // find "username" id in HTML doc
name.innerHTML = "Hello, " + usrNme + "!";                   // Rewrite innerHTML as their username