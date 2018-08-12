/**
 * Created by Олег on 09.03.2017.
 */

var params = [];

$(document).ready(function(){
    $(".article_row").click(function () {
        $.post('/article', {'id': this.id}, function (data) {
            if(data == "") {
                $.msgGrowl({
                    type: 'error',
                    title: 'Article select',
                    text: 'The article cannot be read'
                });
            }
            else
                showArticleInfo(data);
        });
    });

    var page = parseInt($('#page').val());
    $('#PrevPage').click(function() {
        goPage(page-1)
    });
    $('#NextPage').click(function() {
        goPage(page+1)
    });
    $(".delbasket").click(function () {
        if(confirm("Are you sure you want to delete this item from your basket?") === true) {
            deleteFromBasket(this.id.substr(14));
        }
    });
    $(':checkbox').click(function(){
        reloadTable();
    });
});

function showArticleInfo(article)
{
    $('#description').empty();
    $('#description').append("<b>Model:</b> " + article.type + "</br>");
    $('#description').append("<b>Price: </b>" + article.price + "$</br>");
    $('#description').append("<b>On stock: </b>" + article.num + "</br>");
    $.each(article.details, function(key, value) {
        $('#description').append("<b>" + key + ": </b>" + value + "</br>");
    });
    $('#description').append("<br/><input id='number_basket' type='number' value='1' min='0' max='" + article.num + "'><button id='basket'>ADD TO BASKET</button><br/><br/>");
    if($('#isManager').val() === "true") {
        $('#description').append("<button id='edit'>EDIT</button><br/><br/>");
    }
    if($('#isAdmin').val() === "true") {
        $('#description').append("<button id='delete'>DELETE</button><br/><br/>");
    }
    $('#basket').click(function () {
        addToBasket(article);
    });
    $('#edit').click(function () {
        showEdit(article);
    });
    $('#delete').click(function () {
        if(confirm("Are you sure you want to delete this article (" + article.type + ")?") === true) {
            $.post('/index/delete', {id: article.id}, function (data) {
                if(data.error == true) {
                    $.msgGrowl({
                        type: 'error',
                        title: 'Delete',
                        text: data.text
                    });
                }
                else {
                    $.msgGrowl({
                        type: 'success',
                        title: 'Delete',
                        text: 'Article has been successfully deleted'
                    });
                    reloadTable();
                }
            });
        }
    });
}

function showEdit(article) {
    params = ['ID', 'Type', 'Price', 'Num'];
    $('input#edit_ID').prop('value', article.id);
    $('input#edit_Type').prop('value', article.type);
    $('input#edit_Price').prop('value', article.price);
    $('input#edit_Num').prop('value', article.num);
    $.each(article.details, function(key, value) {
        $('input#edit_' + key).prop('value', value);
        params.push(key);
    });
    $("#popup_edit").modal('show');
    return false;
}

function editArticle()
{
    $.post('/index/edit', $("#edit_form_post").serialize(), function (data) {
        if(data.error == true) {
            $.msgGrowl({
                type: 'error',
                title: 'Edit',
                text: data.text
            });
        }
        else {
            $.msgGrowl({
                type: 'success',
                title: 'Edit',
                text: 'Article has been changed'
            });
            $("#popup_edit").modal('hide');
            $('#popup_edit tr input').val("");
            reloadTable();
        }
    });
}

function addArticle()
{
    $.post('/index/add', $("#add_form_post").serialize(), function (data) {
        if(data.error == true) {
            $.msgGrowl({
                type: 'error',
                title: 'Add new article',
                text: data.text
            });
        }
        else {
            $.msgGrowl({
                type: 'success',
                title: 'Add new article',
                text: 'Article has been added'
            });
            $("#popup_add").modal('hide');
            $('#popup_add tr input').val("");
            reloadTable();
        }
    });
}

function reloadTable() {
    $.post('/get_articles', {page: $('#page').val(), sort: $('#sort').val(), reversed: $('#reversed').val(), search: $('#search').val(), filter: $('#hideEmpty').prop('checked')}, function (data) {
        updateTable(data);
    });
}

function addToBasket(article) {
    var num = Number.parseInt($('#number_basket').val());
    if(num <= 0 || num > article.num) {
        $.msgGrowl({
            type: 'error',
            title: 'Incorrect num',
            text: 'Input number is incorrect or out of stock'
        });
    }
    else {
        $.post('/add_to_basket', {num: num, article_id: article.id}, function (data) {
            if(data.error === true) {
                $.msgGrowl({
                    type: 'error',
                    title: 'Basket',
                    text: data.text
                });
            }
            else {
                $.msgGrowl({
                    type: 'success',
                    title: 'Basket',
                    text: 'Item was added in your basket. Go there to make on order'
                });
            }
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

function filterTable(field) {
    var val;
    if(field == 'status')
        val = parseInt($('#filterStatus').val());
    else if(field == 'priority')
        val = parseInt($('#filterPriority').val());
    $.ajax({
        type: "POST",
        url: "filter",
        contentType: "application/json",
        data: JSON.stringify({field: field, value: val}),
        dataType: "json",
        success: function(data) {
            updateTable(data);
        },
        error: function (error) {
            alert("ERROR!");
        }
    });
}

function searchArticle() {
    $('#page').val(1);
    $('#sort').val('object_id');
    $('#reversed').val('0');
    reloadTable();
}

function goPage(page)
{
    var pageNum = $('#pageNum').val();
    if(!(page < 1 || page > pageNum)) {
        $('#page').val(page);
        reloadTable();
    }
}

function updateTable(data) {
    $('.article_row').remove();
    var list = data.articleList;
    list.forEach(function (task) {
        addRow(task);
    });
    //$('#page').val(data.page);
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

function addRow(article)
{
    $('#table_body').append('<tr id="' + article.id + '" class="article_row"></tr>');
    $('#' + article.id).append('<td>' + article.id + '</td>');
    $('#' + article.id).append('<td class="wordwrap">' + article.type + '</td>');
    $('#' + article.id).append('<td class="wordwrap">' + article.price + '$</td>');
    $('#' + article.id).append('<td class="wordwrap">' + article.num + '</td>');
    $('#' + article.id).click(function () {
        $.post('/article', {'id': this.id}, function (data) {
            showArticleInfo(data);
        });
    });
}

function formatDate(datestr) {
    return datestr.split(' ')[2] + ' ' + datestr.split(' ')[1] + ' ' + datestr.split(' ')[5];
}

function formatDateUNIX(date) {
    var newDate = new Date(date).toString();
    return newDate.split(' ')[2] + ' ' + newDate.split(' ')[1] + ' ' + newDate.split(' ')[3];
}

function checkDate(date)
{
    var splitted = date.split(' ');
    if(splitted.length != 3)
        return false;
    if(parseInt(splitted[0]) < 1 || parseInt(splitted[0]) > 31)
        return false;
    if(parseInt(splitted[2]) < 1970 || parseInt(splitted[2]) > 2100)
        return false;
    return in_array(splitted[1], ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']);
}

function in_array(value, array)
{
    for(var i = 0; i < array.length; i++)
    {
        if(array[i] == value) return true;
    }
    return false;
}

/*function findUsers() {
    $('#submitEdit').prop('disabled', true);
    var input_search = $("#user").val();
    if (input_search.length >= 2 && input_search.length < 30)
    {
        $.ajax({
            type: "POST",
            url: "search_account",
            contentType: "application/json",
            data: JSON.stringify({text: input_search}),
            dataType: "json",
            cache: false,
            success: function(data) {
                $("#block-search-result").show();
                $(".founded_users").remove();
                data.forEach(function (user) {
                    $("#list-search-result").append("<li id='FU" + user.id + "' class='founded_users' value='" + user.id + "'>" + user.login + "</li>");
                    $('#FU' + user.id).click(function () {
                        $('#user').prop('value', user.login);
                        $(".founded_users").remove();
                        $('#submitEdit').prop('disabled', false);
                    })
                });
            }
        });
    }else
    {
        $("#block-search-result").hide();
    }
}

function selectProcrastinatedTasks()
{
    $('.tabrow').each(function () {
        if($('#oper_project-' + $(this).prop('id') + '-status').text() == getStatus(3))
        {
            $(this).css({'background-color': 'gray'});
        }
        else if(Date.parse($("#oper_project-" + $(this).prop('id') + "-duedate").text()) - Date.parse(new Date()) < 0)
        {
            $(this).css({'background-color': 'tomato'});
        }
    })
}*/

function deleteFromBasket(id) {
    $.ajax({
        type: "POST",
        url: "/delete_from_basket",
        contentType: "application/json",
        data: {"id": id},
        dataType: "json",
        cache: false,
        success: function(data) {
            if(data.error === true) {
                $.msgGrowl({
                    type: 'error',
                    title: 'Basket',
                    text: data.text
                });
            }
            else {
                getOverallPrice();
                $.msgGrowl({
                    type: 'success',
                    title: 'Basket',
                    text: 'Item was successfully removed from your basket'
                });
                $('#bi_row_'+id).remove();
            }
        }
    });
}

function checkBasket() {
    $.ajax({
        type: "POST",
        url: "/check_for_order",
        contentType: "application/json",
        dataType: "json",
        success: function(data) {
            if(data.error === true) {
                $.msgGrowl({
                    type: 'error',
                    title: 'Creating the order',
                    text: data.text
                });
            }
            else {
                $('#make_order_popup').modal();
            }
        }
    });
}

function make_order() {
    $.post('/order', {name: $("#input_name").val(), email: $("#input_email").val(), telephone: $("#input_telephone").val(), address: $("#input_address").val(), pay_type: $('input[name=pay_type]:checked').val() }, function (data) {
        if(data.error === true) {
            $.msgGrowl({
                type: 'error',
                title: 'Creating the order',
                text: data.text
            });
        }
        else {
            $('#make_order_popup').modal('hide');
            $.msgGrowl({
                type: 'success',
                title: 'Creating the order',
                text: 'Order have been created. Please, wait a telephone call from our operator.'
            });
            getOverallPrice();
            $('.bi_row').remove();
        }
    });

}

function getOverallPrice() {
    $.post('/basket_price', function (data) {
        $('#ov_price').text(data);
    });
}

function showMyOrders(login) {
    $.post('/my_orders', {login: login}, function (data) {
        $('#my_orders_body').empty();
        data.forEach(function (order) {
            $('#my_orders_body').append('<tr id="' + order.id + '" class="order_row"></tr>');
            $('#' + order.id).append('<td>' + order.id + '</td>');
            $('#' + order.id).append('<td>' + order.price + '$</td>');
            $('#' + order.id).append('<td>' + formatDateUNIX(order.date) + '</td>');
            $('#' + order.id).append('<td id="status'+order.id+'">' + getStatus(order.status) + '</td>');
        });
    });
}

function getStatus(status) {
    if(status == 0)
        return "Waiting";
    else if(status == 1)
        return "In processing";
    else if(status == 2)
        return "Completed";
}