$(document).ready(function(){
	//列表下拉
	$('img[nc_type="flex"]').click(function(){
		var status = $(this).attr('status');
		if(status == 'open'){
			var pr = $(this).parent('td').parent('tr');
			var id = $(this).attr('fieldid');
            var level = $(this).attr('level');
			var obj = $(this);
			$(this).attr('status','none');
			//ajax
			$.ajax({
				url: APP_BASE+'/website/class/child?id='+id+'&level='+(Number(level)+1),
				dataType: 'json',
				success: function(data){
					var src='';
					for(var i = 0; i < data.length; i++){
						var tmp_vertline = "<img class='preimg' src='"+APP_BASE+"/res/images/vertline.gif'/>";
						src += "<tr class='"+pr.attr('class')+" row"+id+"'>";
						src += "<td class='w36'><input type='checkbox' name='check_ac_id[]' value='"+data[i].acId+"' class='checkitem'>";
						if(data[i].have_child == 1){
							src += "<img fieldid='"+data[i].acId+"' status='open' nc_type='flex' src='"+APP_BASE+"/res/images/tv-expandable.gif' />";
						}else{
							src += "<img fieldid='"+data[i].acId+"' status='none' nc_type='flex' src='"+APP_BASE+"/res/images/tv-item.gif' />";
						}
						//图片
						src += "</td><td class='w48 sort'>";
						//排序
						src += "<span title='可编辑'  datatype='number' fieldid='"+data[i].acId+"' modUrl='"+APP_BASE+"/website/classs/modifySort' fieldname='ac_sort' nc_type='inline_edit' class='editable'>"+data[i].acSort+"</span></td>";
						//名称
						src += "<td class='name'>";
						for(var tmp_i=1; tmp_i < (data[i].deep-1); tmp_i++){
							src += tmp_vertline;
						}
						if(data[i].have_child == 1){
							src += " <img fieldid='"+data[i].acId+"' status='open' nc_type='flex' src='"+APP_BASE+"/res/images/tv-item1.gif' />";
						}else{
							src += " <img fieldid='"+data[i].acId+"' status='none' nc_type='flex' src='"+APP_BASE+"/res/images/tv-expandable1.gif' />";
						}
						src += " <span title='可编辑' required='1' fieldid='"+data[i].acId+"' modUrl='"+APP_BASE+"/website/classs/modifyName' fieldname='ac_name' nc_type='inline_edit' class='editable'>"+data[i].acName+"</span>";
						src += "</td>";
						
						//操作
						src += "<td class='w84'>";
                        src += "<span><a href=" + APP_BASE + "/website/class/editFind?id=" + data[i].acId + ">编辑</a>";
						src += " | <a href=\"javascript:if(confirm('删除该分类将会同时删除该分类的所有下级分类，您确定要删除吗'))window.location = '"+APP_BASE+"/website/class/delete?ids="+data[i].acId+"';\">删除</a>";
						src += "</td>";
						src += "</tr>";
					}
					//插入
					pr.after(src);
					obj.attr('status','close');
					obj.attr('src',obj.attr('src').replace("tv-expandable","tv-collapsable"));
					$('img[nc_type="flex"]').unbind('click');
					$('span[nc_type="inline_edit"]').unbind('click');
					//重现初始化页面
                    $.getScript(APP_BASE+"/res/js/jquery.edit.js");
					$.getScript(APP_BASE+"/res/js/jquery.article_class.js");
					$.getScript(APP_BASE+"/res/js/admincp.js");
				},
				error: function(){
					alert('获取信息失败');
				}
			});
		}
		if(status == 'close'){
			$(".row"+$(this).attr('fieldid')).remove();
			$(this).attr('src',$(this).attr('src').replace("tv-collapsable","tv-expandable"));
			$(this).attr('status','open');
		}
	})
});