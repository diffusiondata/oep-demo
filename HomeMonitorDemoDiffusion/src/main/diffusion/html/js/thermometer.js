$(function() {

	$('#slider').change(function() {
		value = $('#slider').val();
		$('#feedback').text(value);
		$('#thermometer .middle').height(value);
	});

});