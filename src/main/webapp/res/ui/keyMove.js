var baseIndex = 100;
$(".form").find(".keyRow").each(function(r) {
    $(this).find("input[readonly!='readonly']").each(function(c) {
	    $(this).attr("tabindex", r * 100 + c + baseIndex).addClass("cGridInput");
    });
});
$(".cGridInput").on("keydown", function(evt) {
    var tabIndex = parseInt($(this).attr("tabindex"));
    switch (evt.which) {
        case 38: //上
            tabIndex -= 100;
            break;
        case 40: //下
            tabIndex += 100;
            if($(".cGridInput[tabindex=" + tabIndex + "]").length<1){
	    		tabIndex = parseInt(tabIndex /baseIndex)*baseIndex;
	    	}
            break;
        case 37: //左
            tabIndex--;
            break;
        case 39: //右
            tabIndex++;
            break;
        default:
            return;
    }
    if (tabIndex > 0) {
        $(".cGridInput[tabindex=" + tabIndex + "]").focus();
        return false;
    }
    return true;
});