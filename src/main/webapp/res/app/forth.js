window.addEventListener('load', function() {
	FastClick.attach(document.body);
}, false);

var interval;
var loadnews = false;
var loadmember = false;

var shownow = 0;
var message = "";
$(function() {
	$('#publish_form dl input').poshytip({
		className: 'tip-yellowsimple',
		showOn: 'none',
		alignTo: 'target',
		alignX: 'inner-left',
		offsetX: 0,
		offsetY: 5
	});
	if(token) {
		var url = loxia.encodeUrl(_contextPath+"/news/list.json",true); 
		loxia.asyncXhrPost(url,{"token":token},{success:function(data){
			if(data) {
	           $("#news_list").empty().html(data);
	           loadnews = true;
			}
		}});
	}
	
	if(token) {
		var url = loxia.encodeUrl(_contextPath+"/common/conctor.json",true); 
		loxia.asyncXhrPost(url,{"token":token},{success:function(data){
			if(data) {
	           $("#conctor_list").empty().html(data);
	           loadmember = true;
			}
		}});
	}
	//$(".flipster").flipster({ style: 'carousel', start: 0 }); 
	//
	interval = setInterval("startInit()",100);
	$("#form_token").attr("value",token);
	
	var index = GetQueryString("show");
	message =  $("#message").val();
    if(index && index=="info") {
    	shownow = 2;
    }
});

function startInit() {
	if(loadnews && loadmember) {
		clearInterval(interval);
       	var swiper = new Swiper('.swiper-container', {
    		loop : true,
    		initialSlide : shownow,
    		nextButton : '.swiper-button-next',
    		prevButton : '.swiper-button-prev'
    	});
       	if(message) {
       		alert(message);
       	}
	}
}


function saveOrUpdate(){
	if(token) {
		var title =  $("#infotitle").val();
		var content =  $("#textarea_id").val();
		if(title=="") {
			alert("请输入标题");
			return;
		}
		if(content=="") {
			alert("请输入内容");
			return;
		}
		var url = loxia.encodeUrl(_contextPath+"/news/save.json",true); 
		loxia.asyncXhrPost(url,{"title":title,"content":content,"token":token},{success:function(data){
			if(data && data.result) {
				alert("添加成功");
				var url = $(".foot .btn:eq(3)").find("a").attr("href");
				window.location.href = url;
			}
	   }});
	}
}

function isInteger(obj) {
	 return obj%1 === 0
}

function confirm() {
	$("#publish_form dl input").each(function(){
		$(this).poshytip('hide');
	});
	var isvalator = true;
	$("#publish_form dl input").each(function() {
		var thisval = $(this).val();
		if(thisval == "") {
			$(this).poshytip('show');
			isvalator = false;
			return false;
		}
		var zhengshu = $(this).hasClass("zhengshu");
		if(zhengshu) {
			var iszhengshu = isInteger(thisval);
			if(!iszhengshu) {
				$(this).poshytip('show');
				isvalator = false;
				return false;	
			}
		}else {
			if(isNaN(thisval)) {
				$(this).poshytip('show');
				isvalator = false;
				return false;
			}
		}
	});
	if(isvalator) {
		$("#publish_form").submit();	
	}
}
