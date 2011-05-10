$(document).ready(function(){
	$('input.date').each(function(e){
		var format = $(this).attr("data-format");
		$(this).datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'yy-mm-dd',
			minDate: "+1d"
		});	
	});
	
	//Allow to click on label to select
	$('label,.label').css({cursor:'pointer'}).click(function(){
		$(this).next('input[type="checkbox"]').click().end()
				.prev('input[type="checkbox"]').click();
		return false;
	});
})