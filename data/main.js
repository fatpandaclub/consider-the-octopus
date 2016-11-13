$(document).ready(function() {
    $('.navigation-item').on('click', function() {
        var navId = $(this).attr('id');
        $('.shown').removeClass('shown');
        $('.tool-container[data-name=' + navId + ']').addClass('shown');
    });

    $('#switch-submit').on('click', function() {
        createSwitch();
    });

    $('.add-parameter').on('click', function() {
        var parameterClone = $('.parameter-item:first').clone();
        parameterClone.find('input').val('');
        $('.parameter-container').append(parameterClone);
    });

    $('#return-parameter').on('change', function() {
        if($(this).is(':checked')) {
            $('.return-container').show();
        } else {
            $('.return-container').hide();
        }
    });

    $('#documentation-submit').on('click', function() {
        generateDocumentation();
    });

    $('.error-text').text('app is: ' + app).show();

});

function confirmReception() {
    $('.switch-error').text('Sending to Processing...').show();
}

function createSwitch() {
    $('#switch-error').text('WE CALLED IT').show();
    var noOfStatements = $('#no-of-cases').val();
    var varName = $('#name-of-variable').val();
    if(noOfStatements.length) {
        if(varName) {
            $('#switch-error').hide();
            $('#switch-error').text('Sending to Processing...').show();
            app.generateSwitch(noOfStatements, varName);
        }
        else {
            $('#switch-error').text('Please give your variable a name').show();
        }
    } else {
        $('#switch-error').text('Please write a number').show();
    }
}

function generateDocumentation() {
    var methodDescription = $('#documentation-description').val();

    var documentationString =
        '/**\n'
        + ' * ' + methodDescription + '\n'
        + ' *\n';

    var parameterArr = [];
    $('.parameter-item').each(function(index, elem) {
        var name = $(elem).find('#parameter-name').val();
        var description = $(elem).find('#parameter-description').val();

        var item = {
            name : name,
            description : description
        };

        parameterArr.push(item);

        if(name && description) {
            documentationString += ' * @param ' + name + ' ' + description + '\n';
        }
    });

    var returnDescription;
    if( $('#return-parameter').is(':checked') ) {
        returnDescription = $('#return-description').val();

        documentationString += ' * @return ' + returnDescription + '\n';
    }

    documentationString += ' */\n';

    $('#documentation-error').html(documentationString).show();

    app.insertDocumentation(documentationString);
}