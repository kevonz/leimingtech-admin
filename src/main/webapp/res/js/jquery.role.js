$(document).ready(
		function() {
			//列表下拉
			$('img[nc_type="flex"]').click(
				function() {
					var status = $(this).attr('status');
					if (status == 'open') {
						var pr = $(this).parent('td').parent('tr');
						var id = $(this).attr('fieldid');
						var obj = $(this);
						var level = $(this).attr('level');
						
						$(this).attr('status', 'none');
						//ajax
						$.ajax({
							url : APP_BASE + '/frontMenu/child?id='	+ id + '&level=' + (Number(level) + 1),
							dataType : 'json',
							success : function(data) {
								var src = '';
								for ( var i = 0; i < data.length; i++) {
									var tmp_vertline = "<img class='preimg' src='templates/images/vertline.gif'/>";
									src += "<tr class='" + pr.attr('class')	+ " row" + id + "'>";
									var midsarr = ($("#mids").val()).split(',');
									var arra = 'success';
									for(var j=0;j<midsarr.length;j++){
										if(data[i].id==midsarr[j]){
											arra = 'false';
										}
									}
									if(arra == 'success'){
										src += "<td class='w36'><input type='checkbox' name='menuId' value='"+data[i].id+"' class='checkitem' />";
									}else{
										src += "<td class='w36'><input type='checkbox' checked='checked' name='menuId' value='"+data[i].id+"' class='checkitem' />";
									}
									
									if (data[i].hasChild != 0) {
										src += "<img fieldid='"+data[i].id+"' status='open' nc_type='flex' src='"+APP_BASE+"/res/images/tv-expandable.gif' />";
									} else {
										src += "<img fieldid='"+data[i].id+"' status='none' nc_type='flex' src='"+APP_BASE+"/res/images/tv-item.gif' />";
									} 
									
									src += "</td><td class='name'>";
									//排序
									src += "<span class='editable'>"	+ data[i].name	+ "</span>";
								}
								
								//插入
								pr.after(src);
								obj.attr('status','close');
								obj.attr('src',	obj.attr('src').replace(
									"tv-expandable",
									"tv-collapsable"));
								$('img[nc_type="flex"]').unbind('click');
								$('span[nc_type="inline_edit"]').unbind('click');
								//重现初始化页面
								$.getScript(APP_BASE + "/res/js/jquery.edit.js");
								$.getScript(APP_BASE + "/res/js/jquery.role.js");
								$.getScript(APP_BASE + "/res/js/admincp.js"); 
							},
							error : function() {
								alert('获取信息失败');
							}
						});
					}
					if (status == 'close') {
						$(".row" + $(this).attr('fieldid')).remove();
						$(this).attr('src',$(this).attr('src').replace("tv-collapsable","tv-expandable"));
							$(this).attr('status', 'open');
					}
		});
});
