
$(document).ready(function(){
    reloadTable();

    var page = 1;
    $('#PrevPage').click(function() {
        goPage(page-1)
    });
    $('#NextPage').click(function() {
        goPage(page+1)
    });
    $(':checkbox').click(function(){
        reloadTable();
    });
});

function reloadTable() {
    var filters = getFilters();
    if(filters.length == 0) {
        $.msgGrowl({
            type: 'error',
            title: 'Filter',
            text: 'You should check at least one filter'
        });
    }
    else {
        $.post('/order_list',
            { page: $('#page').val(), sort_field: $('#sort').val(), reversed: $('#reversed').val(), filters: filters, namePart: $('#search').val() }, function (data) {
                updateTable(data);
            });
    }
}

function sort(field)
{
    $('#page').val(1);
    if($('#sort').val() == field) {
        $('#reversed').val($('#reversed').val() == '0' ? '1' : '0');
    }
    else {
        $('#sort').val(field);
        $('#reversed').val('0');
    }
    reloadTable();
}

function updateTable(data) {
    $('.order_row').remove();
    var list = data.orderList;
    list.forEach(function (order) {
        addRow(order);
    });
    $('#pageNum').val(data.pageNum);
    $('#PrevPage').unbind('click').click(function() {
        goPage(data.page-1)
    });
    $('#NextPage').unbind('click').click(function() {
        goPage(data.page+1)
    });
    $('#PageCounter').html("PAGE " + data.page + " OF " + data.pageNum);
    if(parseInt($('#page').val()) > parseInt($('#pageNum').val()) && parseInt($('#page').val()) != 1) {
        $('#page').val($('#pageNum').val());
        reloadTable();
    }
}

function addRow(order)
{
    $('#table_body').append('<tr id="' + order.id + '" class="order_row"></tr>');
    $('#' + order.id).append('<td>' + order.id + '</td>');
    $('#' + order.id).append('<td>' + order.price + '$</td>');
    $('#' + order.id).append('<td>' + formatDateUNIX(order.date) + '</td>');
    $('#' + order.id).append('<td id="status'+order.id+'">' + getStatus(order.status) + '</td>');
    $('#' + order.id).append('<td><button type="button" class="btn btn-info" onclick="showInfo('+order.id+')">Info</button></td>');
}

function goPage(page)
{
    var pageNum = $('#pageNum').val();
    if(!(page < 1 || page > pageNum)) {
        $('#page').val(page);
        reloadTable();
    }
}

function getStatus(status) {
    if(status == 0)
        return "Waiting";
    else if(status == 1)
        return "In processing";
    else if(status == 2)
        return "Completed";
}

function formatDateUNIX(date) {
    var newDate = new Date(date).toString();
    return newDate.split(' ')[2] + ' ' + newDate.split(' ')[1] + ' ' + newDate.split(' ')[3];
}

function showInfo(id) {
    $.post('/order_info', {id: id}, function (data) {
        if(data == null) {
            $.msgGrowl({
                type: 'error',
                title: 'Order',
                text: 'This order does not exist. Please reload the page'
            });
        }
        else
            showOrderInfo(data);
    });
}

function showOrderInfo(data) {
    $('#window_body').empty();
    $('#window_body').append("<b>Order ID:</b> " + data.id + "</br>");
    $('#window_body').append("<b>User Login:</b> " + data.user.login + "</br>");
    $('#window_body').append("<b>Name: </b>" + data.name + "</br>");
    $('#window_body').append("<b>EMail: </b>" + data.email + "</br>");
    $('#window_body').append("<b>Telephone: </b>" + data.number + "</br>");
    $('#window_body').append("<b>Address: </b>" + data.address + "</br>");
    $('#window_body').append("<b>Pay type: </b>" + data.payType + "</br>");
    $('#window_body').append("<b>Price: </b>" + data.price + "$</br>");
    $('#window_body').append("<b>Date: </b>" + formatDateUNIX(data.date) + "$</br>");
    $('#window_body').append("<b>Items: </b><br/>");
    $('#window_body').append("<ul id='items_list'></ul>");
    $.each(data.orderItems, function(i, item) {
        $('#items_list').append("<li><b>" + item.articleTypeName + "</b> | Num: " + item.num + " | Price: " + item.price + "$</li>");
    });
    $('#window_body').append("<br/><b>Status:</b><br/><select id='status'></select>");
    $('#status').append("<option value='0'>Waiting</option>");
    $('#status').append("<option value='1'>In processing</option>");
    $('#status').append("<option value='2'>Completed</option>");
    $('#window_body').append("    <a class='btn btn-primary' onclick='changeStatus(" + data.id + ")'>CHANGE</a>");
    $('#status').val(data.status);
    $("#order_info").modal('show');
}

function changeStatus(id) {
    var newstatus = $('#status').val();
    if(newstatus < 0 || newstatus > 2) {
        $.msgGrowl({
            type: 'error',
            title: 'Status edit',
            text: 'Incorrect status value'
        });
    }
    else {
        $.post('/order_status', {id: id, status: newstatus}, function (data) {
            if(data.error == true) {
                $.msgGrowl({
                    type: 'error',
                    title: 'Change status',
                    text: data.text
                });
            }
            else {
                $("#order_info").modal('hide');
                $.msgGrowl({
                    type: 'success',
                    title: 'Change status',
                    text: 'Status has been successfully changed'
                });
                if(getFilters().length == 5)
                    $("#status"+id).text(getStatus(newstatus));
                else
                    reloadTable();
            }
        });
    }
}

function getFilters() {
    var str = "";
    if($('#showWaiting').prop('checked')) str += "0 ";
    if($('#showProcessing').prop('checked')) str += "1 ";
    if($('#showCompleted').prop('checked')) str += "2 ";
    return str.trim();
}

function searchArticle() {
    $('#page').val(1);
    $('#sort').val('object_id');
    $('#reversed').val('0');
    reloadTable();
}