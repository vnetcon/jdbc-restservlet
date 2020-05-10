
getPathVal = function(obj, path) {
    if (typeof obj === "undefined" || obj === null) return;
    path = path.split(/[\.\[\]\"\']{1,2}/);
    for (var i = 0, l = path.length; i < l; i++) {
        if (path[i] === "") continue;
        obj = obj[path[i]];
        if (typeof obj === "undefined" || obj === null) return;
    }
    return obj;
}

mapChildNodes = function(text, c, json){
    var tmp = text;
    for(var i = 0; i < c.length; i++){
        if(c[i].nodeName.toLowerCase() == 'json' || c[i].nodeName.toLowerCase().indexOf('ljson-') > -1){
            var t = '' + c[i].innerHTML;
            var s = '';
            var n = getPathVal(json, c[i].className);
            var tname = c[i].nodeName.toLowerCase();
            console.log(tname);
            if(n !== null && typeof n === 'object' && Array.isArray(n)){
                for(var ii = 0; ii < n.length; ii++){
                    var b = t;
                    var ci = c[i].className;
                    while(b.indexOf('[]') > -1){
                        b = b.replace('\[\]', '[' + ii + ']');
                    }
                    var divbuf = document.createElement("div");
                    divbuf.innerHTML = b;
                    console.log('childNodes: ' + b);
                    var sb = mapChildNodes(b, divbuf.childNodes, json);
                    s += sb;
                    divbuf.remove();
                }
                var re1 = new RegExp('<' + tname);
                var re2 = new RegExp(tname + '>');
                tmp = tmp.replace(re1, '<!-- <' + tname);
                tmp = tmp.replace(re2, tname + '> -->' + s);
            }else{
                tmp = tmp.replace(/<json .*?>/, n).replace(/<\/json>/, '');
            }
        }else{
            if(c[i].childNodes){
                tmp = mapChildNodes(tmp, c[i].childNodes, json);
            }
        }
    }

    tmp = tmp.replace(/tr_curvy/g, 'tr');
    tmp = tmp.replace(/td_curvy/g, 'td');
    tmp = tmp.replace(/table_curvy/g, 'table');
    console.log(tmp);
    return tmp;
}

mapJSON = function(templateid, targetid, json){
    var tmp = document.getElementById(templateid).cloneNode(true).innerHTML;
    var c = document.getElementById(templateid).childNodes;
    tmp = mapChildNodes(tmp, c, json);
    document.getElementById(targetid).innerHTML = tmp;
}


uploadFile = function(fileinput, uploadrret) {
    var file = document.querySelector('#' + fileinput).files[0];
    var data = new FormData();
    data.append('file', document.querySelector('#' + fileinput).files[0]);
    var request = new XMLHttpRequest();
    request.open('post', fileuploadurl); 
    request.upload.addEventListener('progress', function(e) {
	    var percent_complete = (e.loaded / e.total)*100;
	    console.log(percent_complete);
    });

    // AJAX request finished event
    request.addEventListener('load', function(e) {
        // HTTP status message
        console.log(request.status);
        // request.response will hold the response from the server
        console.log(request.response);
        console.log("cleaning file input");
        document.getElementById(fileinput).value = null;
        uploadrret(request.response);
    });

    // send POST request to server side script    
    request.send(data);
}


makeRequest = function(endpoint, fdata, callback) {
    fetch(endpoint, {
        headers: { "Content-Type": "application/x-www-form-urlencoded; charset=utf-8" },
        method: 'post',
        body: fdata
    })
        .then(function (response) {
            return response.json();
        })
        .then(data => callback(true, data))
        .catch(e => callback(false, e));
}