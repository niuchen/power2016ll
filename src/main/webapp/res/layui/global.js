/**
*/

layui.define(['layer'], function(exports){
  var $ = layui.jquery
  ,layer = layui.layer
  ,device = layui.device();

  //阻止IE7以下访问
  if(device.ie && device.ie < 8){
    layer.alert('Layui最低支持ie8，您当前使用的是古老的 IE'+ device.ie + '，你丫的肯定不是程序猿！');
  }
  
  exports('global', {});
});