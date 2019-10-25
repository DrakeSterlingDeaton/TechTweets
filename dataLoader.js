console.log(data);

var length = data.length;

var main = document.getElementsByTagName("main")[0];

for(var j = 0; j < length; j++){
    var projectTitle = document.createElement("h1");
    projectTitle.innerHTML = data[j].projectTitle;
    var studentName = document.createElement("p");
    studentName.innerHTML = data[j].name;
    var link = document.createElement("a");
    link.setAttribute("href", data[j].link);
    link.innerHTML = "Show me the project";


    main.appendChild(projectTitle);
    main.appendChild(studentName);
    main.appendChild(link);
}
