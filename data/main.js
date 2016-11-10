
$(document).ready(function() {
	$('.navigation-item').on('click', function() {
		var hej = $(this).attr('id');
		$('.shown').removeClass('shown');
		$('.tool-container[data-name=' + hej + ']').addClass('shown');
	});
});

function createSwitch() {
    var noOfStatements = $('#no-of-statements').val();
    if(noOfStatements.length) {
    	$('#error-text').hide();
    } else {
    	$('#error-text').text('Please write a number').show();
    }
}