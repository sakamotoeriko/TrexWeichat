var express = require('express');
var app = express();

// HTTPリクエストを受け取る部分
app.get('/test',function(req,res){
    console.log('test');
});
app.get('/avatar/:id', function (req, res) {
    var filePath = '../avatar/'+req.params.id+'/'; // Or format the path using the `id` rest param
    var fileName = 'avatar.png'; // The default name the browser will use

    console.log('file:'+filePath+fileName);
    res.download(filePath+fileName, fileName,function(err){
      if (err) {
        console.log('err:'+err);
      }else{

      }
    });

});

// サーバーを起動する部分
var server = app.listen(5110, function () {
  var host = server.address().address;
  var port = server.address().port;
  console.log('Example app listening at http://%s:%s', host, port);
});
