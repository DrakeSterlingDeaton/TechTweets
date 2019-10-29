console.log(data);

var length = data.length;

var main = document.getElementsByTagName("main")[0];

for(var j = 0; j < length; j++){
    var projectTitle = document.createElement("h1");    
    projectTitle.innerHTML = data[j].projectTitle;
    var studentName = document.createElement("p");
    studentName.innerHTML = data[j].name;
    var link = document.createElement("a");
    link.href = data[j].link;
    link.innerHTML = "Show me the project";


    main.appendChild(projectTitle);
    main.appendChild(studentName);
    main.appendChild(link);
}

var projectsOuterBox = document.createElement("div");
projectsOuterBox.setAttribute("class", "projectsOuterBox");
var projectsInnerBox = document.createElement("div");
projectsInnerBox.setAttribute("class", "projectsInnerBox");
var wholeList = document.createElement("ul");
wholeList.setAttribute("class", "wholeList");

for(var j = 0; j < length; j++){
    var studentList = document.createElement("li");
    studentList.setAttribute("class", "studentList");

    var studentNameBox = document.createElement("div");
    studentNameBox.setAttribute("class", "studentNameBox");

    var studentName = document.createElement("p");
    studentName.setAttribute("class", "studentName");

    var studentPDBox = document.createElement("div");
    studentPDBox.setAttribute("class", "studentPDBox");

    var studentProDesc = document.createElement("p");
    studentProDesc.setAttribute("class", "studentProDesc");

    var link = document.createElement("a");
    link.href = data[j].link;
    link.innerHTML = "Show me the project";

    studentName.innerHTML = data[j].name;
    studentProDesc.innerHTML = data[j].projectTitle;

    studentNameBox.appendChild(studentName);
    studentPDBox.appendChild(studentProDesc);

    studentList.appendChild(studentNameBox);
    studentList.appendChild(studentPDBox);
    studentList.appendChild(link);

    wholeList.appendChild(studentList);
}

projectsInnerBox.appendChild(wholeList);
projectsOuterBox.appendChild(projectsInnerBox);
main.appendChild(projectsOuterBox);




