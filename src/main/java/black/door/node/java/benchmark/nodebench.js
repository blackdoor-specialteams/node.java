var express = require('express');
var app = express();
var uuid = require('node-uuid');
var fs = require('fs');
//var http = require("http");

function fibN(z){
		if(z <= 1)
    		return z;
    	return fibN(z-2) + fibN(z-1);
	}



app.get('/fib/:n', function(req, res){
    var sleep = req.query.sleep

    //make this use promises
    setTimeout(function() {
	    var n = req.params.n;
	    var s = "";
	    for(i=0; i < n; i++){
	        s = s + fibN(i).toString() + "\n";
	    }
	    fs.writeFile("outs/" + uuid.v4(), s, function(){
	        res.end(s);
	    });
    }, sleep);

})

var server = app.listen(8080, function () {

  var host = server.address().address
  var port = server.address().port
  console.log("Example app listening at http://%s:%s", host, port)

})
