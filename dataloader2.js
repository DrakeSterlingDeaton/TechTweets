var length = data.length;

var main = document.getElementsByTagName("main")[0];

var projectsOuterBox = document.getElementById("projectsOuterBox");

var nameList = [];

for(var j = 0; j < length; j++){ 
    
    // if name (data[j].name) is not in list of names (nameList):
    //      add name to nameList
    //
    //      create new div w/ className "projectsInnerBox"
    //      create new div w/ className "studentNameBox"
    //      create new p w/ className "studentName"
    //      create new div w/ className "studentProOuterBox""
    //      Atatch boxes to eachother
    //
    //      for project 'PROJECTtitle' in projectList (data[j].projectList):
    //            for (var i = 0; i < length; i++):
    //            if data[i].title == PROJECTtitle:
    //                      
    //                  create div w/ className "sProInnerB"
    //                  create div w/ className "proTBox"
    //                  create p w/ className "proT"
    //                  create div w/ className "proLBox"
    //                  create p w/ className "proL"
    //                  create div w/ className "proDBox"
    //                  create p w/ className "proD"
    //                  Atatch boxes to eachother
    // 
    //                  data[i].title ...        (add to "proT")
    //                  data[i].link ...         (add to "proL")
    //                  data[i].description ...  (add to "proD")
    //
    //                  Atatch "sProInnerB" to "studentProOuterBox"
    // 
    // else:
    //      do nothing (can skip the name because it's already been handled)
}

main.appendChild(projectsOuterBox);
