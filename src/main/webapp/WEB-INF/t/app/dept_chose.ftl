<a href="javascript:void(0);" onclick="sel(this,1)" class="on" ><label>全处</label></a>
<a href="javascript:void(0);" onclick="sel(this,1)" orgId="71959b2f7a114efa995ca8b443bd9d0e"><label>少洛</label></a>
<a href="javascript:void(0);" onclick="sel(this,1)" orgId="ab6f0c4c812d47dfba4dfbddb3c6b0a1"><label>济洛</label></a>
<a href="javascript:void(0);" onclick="sel(this,1)" orgId="f69a931d443e4ce6af7afe5eb6044dca"><label>新济</label></a>
<div class="site_center">
<#list depts as dept>
<a onclick="sel(this)" style="display:none;" href="javascript:void(0);" deptId="${dept.departmentid}" orgId="${dept.organizeid}"><label>${dept.fullname}</label></a>
</#list></div>
<script type="text/javascript">
function sel(obj,flag){
	$(obj).siblings().removeClass("on");
	$(obj).addClass("on");
	if(flag){
		$(".site_center a",$(obj).parent()).removeClass("on").hide();
		var orgId = $(obj).attr("orgId");
		$("a[orgId='"+orgId+"']",$(obj).parent()).show();
		_loadData($(obj).parent().attr("id"),'org');
	}else{_loadData($(obj).parent().parent().attr("id"),'dept');}
}
</script>