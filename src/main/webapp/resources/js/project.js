/**
 * Created by josh on 2014-07-16.
 */
$(function() {
    $(".authorized").qtip({
        content: {
            text: /*[[#{project.associated.rights}]]*/ 'You can view this project.'
        },
        position: {
            at: 'right center',
            my: 'left, center',
            adjust: {
                x: -8
            }
        },
        style: {
            classes: "qtip-green"
        }
    });
    $(".unauthorized").qtip({
        content: {
            text: /*[[#{project.associated.norights}]]*/ 'You do not have authorization to view this project.'
        },
        position: {
            at: 'right center',
            my: 'left, center',
            adjust: {
                x: -8
            }
        },
        style: {
            classes: "qtip-red"
        }
    });
});