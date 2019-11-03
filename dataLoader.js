console.log(data);

var length = data.length;

var main = document.getElementsByTagName("main")[0];

// for(var j = 0; j < length; j++){
//     var projectTitle = document.createElement("h1");    
//     projectTitle.innerHTML = data[j].projectTitle;
//     var studentName = document.createElement("p");
//     studentName.innerHTML = data[j].name;
//     var link = document.createElement("a");
//     link.href = data[j].link;
//     link.innerHTML = "Show me the project";


//     main.appendChild(projectTitle);
//     main.appendChild(studentName);
//     main.appendChild(link);
// }

var projectsOuterBox = document.getElementById("projectsOuterBox");

for(var j = 0; j < length; j++){    

    var projectsInnerBox = document.createElement("div");
    projectsInnerBox.setAttribute("class", "projectsInnerBox");

    var studentProTitleBox = document.createElement("div");
    studentProTitleBox.setAttribute("class", "studentProTitleBox");

    var studentProTitle = document.createElement("p");
    studentProTitle.setAttribute("class", "studentProTitle");
    
    var studentNameBox = document.createElement("div");
    studentNameBox.setAttribute("class", "studentNameBox");

    var studentName = document.createElement("p");
    studentName.setAttribute("class", "studentName");

    var studentLinkBox = document.createElement("div");
    studentLinkBox.setAttribute("class", "studentLinkBox"); 

    var studentProjectLink = document.createElement("a");
    studentProjectLink.setAttribute("class", "studentProjectLink");
    studentProjectLink.innerHTML = "Show me the project";

    var studentPDBox = document.createElement("div");
    studentPDBox.setAttribute("class", "studentPDBox");

    var studentProDesc = document.createElement("p");
    studentProDesc.setAttribute("class", "studentProDesc");    

    studentName.innerHTML = data[j].name;
    studentProTitle.innerHTML = data[j].projectTitle;
    studentProjectLink.href = data[j].link;
    studentProDesc.innerHTML = data[j].description;

    studentNameBox.appendChild(studentName);
    studentProTitleBox.appendChild(studentProTitle);
    studentLinkBox.appendChild(studentProjectLink);
    studentPDBox.appendChild(studentProDesc);    

    projectsInnerBox.appendChild(studentProTitleBox);
    projectsInnerBox.appendChild(studentNameBox);
    projectsInnerBox.appendChild(studentLinkBox);
    projectsInnerBox.appendChild(studentPDBox);
    
    projectsOuterBox.appendChild(projectsInnerBox);
}

main.appendChild(projectsOuterBox);




