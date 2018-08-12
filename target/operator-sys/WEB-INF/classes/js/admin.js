/**
 * Created by Олег on 20.07.2017.
 */
$(document).ready(function(){
    $('#block-search-result').hide();
    $('#search').bind('textchange', function () {
        findTasks(1);
    });

    $('#PrevPage').click(function() {
        var page = parseInt($('#userPage').val());
        goPage(page-1)
    });
    $('#NextPage').click(function() {
        var page = parseInt($('#userPage').val());
        goPage(page+1)
    });
});

function findTasks(page) {
    var input_search = $("#search").val();
    if (input_search.length >= 1 && input_search.length < 30)
    {
        $.ajax({
            type: "POST",
            url: "search_task",
            contentType: "application/json",
            data: JSON.stringify({text: input_search, page: page}),
            dataType: "json",
            cache: false,
            success: function(data) {
                $("#block-search-result").show();
                $(".founded_tasks, #PrevTasks, #NextTasks, #pageCounter").remove();
                data.taskList.forEach(function (task) {
                    $("#list-search-result").append("<tr class='founded_tasks'><td>" + task.name + "</td><td>" + task.description + "</td><td><a href='../todolist/" + task.userID + "?search=" + input_search + "'>Go to user</a></td></tr>");
                });
                $('#block-search-result').append("<button id='PrevTasks'><<< BACK</button><div id='pageCounter'>PAGE " + data.page + " OF " + data.pageNum + "</div><button id='NextTasks'>NEXT >>></button>");
                if(data.page <= 1)
                    $('#PrevTasks').prop('disabled', true);
                else
                    $('#PrevTasks').click(function () {
                        findTasks(data.page-1);
                    });
                if(data.page >= data.pageNum)
                    $('#NextTasks').prop('disabled', true);
                else
                    $('#NextTasks').click(function () {
                        findTasks(data.page+1);
                    });
            }
        });
    }else
    {
        $("#block-search-result").hide();
    }
}

function updateUserList(userList)
{
    $('.userrow').remove();
    userList.forEach(function (user) {
        $('#results').append("<tr class='userrow'><td>" + user.id + "</td><td>" + user.login + "</td>" +
            "<td><button onclick='changeRole(" + user.id + "); return false;'><span id='role" + user.id + "'>Make Admin</span></button></td>" +
            "<td><a href='../todolist/" + user.id + "'>View Tasks</a></td><td><button onclick='deleteUser(" + user.id + "); return false;'>Delete</button></td></tr>");

        if(user.role == 'ROLE_ADMIN')
            $('#role' + user.id).text('Make User');
    });
}

function goPage(page) {
    var pageNum = $('#userPageNum').val();
    if(!(page < 1 || page > pageNum)) {
        $.ajax({
            type: "POST",
            url: "page",
            contentType: "application/json",
            data: JSON.stringify({ page: page }),
            dataType: "json",
            success: function(data) {
                updateUserList(data.userList);
                $('#userPage').val(data.page);
                $('#userPageNum').val(data.pageNum);
                $('#PageUserCounter').text('PAGE ' + data.page + ' OF ' + data.pageNum);
            },
            error: function (error) {
                alert("ERROR!");
            }
        });
    }
}

function deleteUser(userID)
{
    $.ajax({
        type: "POST",
        url: "delete",
        contentType: "application/json",
        data: JSON.stringify({ userID: userID, page: $('#userPage').val() }),
        dataType: "json",
        success: function(data) {
            updateUserList(data.userList);
            $('#userPage').val(data.page);
            $('#userPageNum').val(data.pageNum);
            $('#PageUserCounter').text('PAGE ' + data.page + ' OF ' + data.pageNum);
        },
        error: function (error) {
            alert("ERROR!");
        }
    });
}

function changeRole(userID)
{
    $.ajax({
        type: "POST",
        url: "role",
        contentType: "application/json",
        data: JSON.stringify({ userID: userID, page: $('#userPage').val() }),
        dataType: "json",
        success: function(data) {
            updateUserList(data.userList);
        },
        error: function (error) {
            alert("ERROR!");
        }
    });
}