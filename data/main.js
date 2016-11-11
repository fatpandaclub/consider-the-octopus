$(document).ready(function() {
	$('.navigation-item').on('click', function() {
		var hej = $(this).attr('id');
		$('.shown').removeClass('shown');
		$('.tool-container[data-name=' + hej + ']').addClass('shown');
	});
	
	$('#switch-submit').on('click', function() {
		createSwitch();
	});
	
	$('#error-text').text("app is: " + app).show();
	
	function createSwitch() {
		$('#error-text').text('WE CALLED IT').show();
	    var noOfStatements = $('#no-of-cases').val();
	    var varName = $('#name-of-variable').val();
	    if(noOfStatements.length) {
	    	if(varName) {
	    		$('#error-text').hide();
	    		$('#error-text').text('Sending to Processing...').show();
	    		app.generateSwitch(noOfStatements, varName);
	    	}
	    	else {
	    		$('#error-text').text('Please give your variable a name').show();
	    	}
	    } else {
	    	$('#error-text').text('Please write a number').show();
	    }
	}
});

function confirmReception() {
	$('#error-text').text('Sending to Processing...').show();
}