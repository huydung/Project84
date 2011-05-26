$(document).ready(function(){
	/** Date Picker **/
	$('input.date').each(function(e){
		var format = $(this).attr("data-format");
		var $dp = $(this);
		$dp.datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: format,
			minDate: "+1d"
		})
		//Tricky: We want to display the DatePicker in user's choice of Date format
		//But when we submit the form, we need to set it back to YYYY/MM/dd
		//so Play controllers can automatically parse it
		/*
		.parents('form').submit(function(){
			$dp.datepicker('option', 'dateFormat', 'yy-mm-dd');
		});
		*/
	})
	
	
	//Allow to click on label to select
	$('label,.label').css({cursor:'pointer'}).click(function(){
		$(this).next('input[type="checkbox"]').click().end()
				.prev('input[type="checkbox"]').click();
		return false;
	});
})