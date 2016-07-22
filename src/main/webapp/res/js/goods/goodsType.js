// 隐藏未选择的规格
function spec_hide(str) {
    $('#'+str+'_table').find('h6').hide();
    $('#'+str+'_table').find('input[type="checkbox"]').parents('tr').hide();
    $('#'+str+'_table').find('input[type="checkbox"]:checked').parents('tr').show();
    $('a[nctype="'+str+'_hide"]').attr('nctype',str+'_show').children().html('全部显示');
}
// 显示全部的规格
function spec_show(str) {
    $('#'+str+'_table').find('h6').show().end().find('tr').show();
    $('a[nctype="'+str+'_show"]').attr('nctype',str+'_hide').children().html('隐藏未选项');
}