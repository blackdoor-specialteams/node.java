var express = require('express')
var app = express();
//var http = require("http");

function fibN(z){
		if(z <= 1)
    		return z;
    	return fibN(z-2) + fibN(z-1);
	}



app.get('/fib/:n', function(req, res){

	n = req.params.n;
	s = ""
	for(i=0; i < n; i++){
		s = s + fibN(i).toString() + "\n";
	}
	res.end(s);
})

var server = app.listen(8080, function () {

  var host = server.address().address
  var port = server.address().port
  console.log("Example app listening at http://%s:%s", host, port)

})
