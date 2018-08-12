/**
 * Created by Олег on 09.06.2018.
 */
$(document).ready(function(){
    $('.delete_attr').click(function() {
        if(confirm("Are you sure you want to delete this param?") === true) {
            deleteAttr($(this).attr('id'));
        }
    });
});


function add_param() {
    if($('#newparam').val().length == 0) {
        $.msgGrowl({
            type: 'error',
            title: 'Adding',
            text: 'Param name is empty'
        });
    }
    else {
        $.post('/params/addparam', {name: $('#newparam').val()}, function (data) {
            if(data.error == true) {
                $.msgGrowl({
                    type: 'error',
                    title: 'Adding',
                    text: data.text
                });
            }
            else {
                window.location.reload();
            }
        });
    }
}

function deleteAttr(id)
{
    $.post('/params/delete', {id: id}, function (data) {
        if(data.error == true){
            $.msgGrowl({
                type: 'error',
                title: 'Delete',
                text: data.text
            });
        }
        else {
            $('#attr_' + id).remove();
            $.msgGrowl({
                type: 'success',
                title: 'Delete',
                text: 'Attribute deleted succesfully!'
            });
        }
    });
}