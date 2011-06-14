var currencies = ["AED", "ANG", "ARS", "AUD", "BDT", "BGN", "BHD", "BND", "BOB", "BRL", "BWP", "CAD", "CHF", "CLP", "CNY", "COP", "CRC", "CZK", "DKK", "DOP", "DZD", "EEK", "EGP", "EUR", "FJD", "GBP", "HKD", "HNL", "HRK", "HUF", "IDR", "ILS", "INR", "ISK", "JMD", "JOD", "JPY", "KES", "KRW", "KWD", "KYD", "KZT", "LBP", "LKR", "LTL", "LVL", "MAD", "MDL", "MKD", "MUR", "MVR", "MXN", "MYR", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG", "QAR", "RON", "RSD", "RUB", "SAR", "SCR", "SEK", "SGD", "SKK", "SLL", "SVC", "THB", "TND", "TRY", "TTD", "TWD", "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "VEF", "VND", "XOF", "YER", "ZAR", "ZMK"];

$(document).ready(function(){
	var processPage = function(){
		/** Default gritter options **/
		$.gritter.options = {		
			position: 'bottom-right',
			time: 10000
		};
		
		/** Convert all flash messages to gritter **/
		$('#success,#warning,#info,#error').each(function(){
			var id = $(this).attr('id');
			$.gritter.add({
				title: id,
				text: $(this).html(),
				class_name: id,						
			});
		}).remove();
		
		/** Date Picker **/
		$('input.date').each(function(e){
			var format = $(this).attr("data-format");
			var $dp = $(this);
			$dp.datepicker({
				changeMonth: true,
				changeYear: true,
				dateFormat: format,
				minDate:  $dp.attr('data-range-limit') == 'false' ? null : "+1d"
			})
		});

		/** Accordion & Tabs **/
		var stop = false;
		$( "#accordions .header" ).click(function( event ) {
			if ( stop ) {
				event.stopImmediatePropagation();
				event.preventDefault();
				stop = false;
			}
		});
		$('#accordions').accordion({
			header: '.header',
			collapsible: true,
			active: false,
			autoHeight: false
		});
		$('#form-item #accordions').accordion("activate", 0);
		
		/** Numeric **/
		/*
		$('input.numeric').numeric({
			minValue: 0,
			increment: $(this).attr('data-inc'),
			format: $(this).attr('data-format')
		});
		*/
		
		/** PROJECT LISTINGS SORTING **/
		$('.project_listings').sortable({
			axis: "y",
			handle: "h3.header",
			stop: function() {
				stop = true;
				var ids = [];
				var nav_els = [];
				$('.project_listings .listing-panel').each(function(){
					var id = $(this).attr('data-id');
					ids.push( id );
					var nav_el = $('.listing_nav[data-id='+ id +']').clone();
					nav_els.push(nav_el);
				});
				var project_id = $(this).attr('data-id');
				$.post(
					'/project/'+project_id+'/listings/orderings/'+ids.join(','),
					null,
					function(){	
						$('.listing_nav').remove();
						$firstNav = $('#menu_group_main .first');
						for( var i = nav_els.length - 1; i > -1; i-- ){
							$firstNav.after(nav_els[i]);
						};					
					}
				);
			}
		});
		$('#tabs').tabs();
		
		/** Hide elements that will be enhanced or reveal by Javascript **/
		$('.enhanced, .reveal').hide();
		$('.reveal-toggle').click(function(){
			$target = $($(this).attr('href'));
			$target.is(':visible') ? $target.hide() :  $target.show();
			return false;
		});
		
		
		/** Allow to click on label to select **/
		$('label,.label').css({cursor:'pointer'}).click(function(){
			$(this).next('input[type="checkbox"]').click().end()
					.prev('input[type="checkbox"]').click();
			return false;
		});
		
		/** Convert delete action link to have confirmation **/
		$('.need-confirm')
			.click(function(){
				window.location.href = $(this).attr('href');
				return false;
			})
			.confirm({
				timeout:5000,
				wrapper:'<span class="confirmation fright"></span>'
			});
		
		/** Convert modal actions in widget to use AJAX **/
		$('.modal').click(function(){
			$this = $(this);
			$('body').append('<div id="modal-dialog"></div>');
			$('#modal-dialog')
				.load($this.attr('href'), function(response, status, xhr){
					if(status != "error"){			
						$('#modal-dialog').dialog({
							modal: true,
							title: $this.text(),
							close: function(){
								$('#modal-dialog').remove()
							},
							width: 600
						});
					}else{
						//See documentation: http://boedesign.com/blog/2009/07/11/growl-for-jquery-gritter/
						$.gritter.add({
							title: 'Oops',
							text: response,
							class_name: 'error',						
						});
					}	
				});
			return false;			
		});
		
		/** Icon Chooser Components **/
		//Fills Icons in the modal-dialog
		var $modal = $('#modal-dialog');
		if($modal.length <= 0){
			$('body').append('<div id="modal-dialog"></div>');
			$modal = $('#modal-dialog');
		}
		var $iconChoosers = $('.iconChooser select');
		if( $iconChoosers.length > 0 ){
			var str = '<ul class="grid-items">';
			$iconChoosers.eq(0).find('option').each(function(){
				str += '<li><img src="'+ $(this).val() +'" width="32" height="32" /></li>';
				
			});
			str +='</div>';
			$modal.html(str);
			$modal.dialog({
				autoOpen: false,
				title: "Icon Chooser",
				width: 600,
				height: 420,
				modal: true
			});
			$iconChoosers.parents('a').click(function(){
				$modal.data('caller_id', $(this).attr('id')).dialog('open');
				return false;
			});
			$modal.find('li').click(function(){
				var id = $modal.data('caller_id');
				var src = $(this).find('img').attr('src');
				$('#'+id)
					.find('select').val( src )
					.end()
					.find('img').attr('src', src);
				$modal.dialog('close');
			});
		};
		
		/** Checkbox interactivity in Project Configuration page **/
		$('.listing-fields-configuration input[type=checkbox]').click(function(){
			var $this = $(this);
			if( $this.is(':checked') ){
				$this.parents('tr')
				.addClass('used')
					.find('input[type=text]').each(function(){
						$(this).val($(this).attr('placeholder'));
					});
			}else{
				$this.parents('tr').removeClass('used');
			}
		});
		
		/** sortable **/
		var fixHelper = function(e, ui) {
		    ui.children().each(function() {
		        $(this).width($(this).width());
		    });
		    return ui;
		};
		//alert('Hey');
		$('.sortable tbody,ul.sortable').sortable({
			'handle': '.drag-handler',
			helper: fixHelper,
			placeholder: 'ui-sortable-placeholder'
		}).disableSelection();
		
		
		/** Ajaxify Form **/
		/*
		$('form.ajaxify button[type=submit], form.ajaxify input[type=submit]').click(function(){
			$form = $(this).parent('form');
			$.ajax({
				type: $form.attr('method'),
				url: $form.attr('action'),
				dataType: html,
				success: function(data, textStatus, jqXHR){
					$form.parent('.accordion-panel').html(data);
				},
				error: function(){
					alert('Error');
				}
			});
			return false;
		});*/
		
		/** LISTING FILTERS **/
		/*$('#form-filters button').hide();
		
		$('.filter-field').change(function(e){
			$this = $(this);			
			$this.parents('form').submit();			
		});
		*/
		
		/** CHECK-UNCHECK ALL **/
		$('.uncheck-all').click(function(){
			$(this).parents('.checkboxs-holder')
				.find('input[type="checkbox"]:checked')
				.click();
			return false;
		});
		$('.check-all').click(function(){
			$(this).siblings('.uncheck-all').click();
			$(this).parents('.checkboxs-holder')
			.find('input[type="checkbox"]').attr('checked', null).click();
			return false;
		})
		
		/** INLINE-EDIT **/
		$('.inline-editable .inline-edit').hide();
		$('.inline-editable').dblclick(function(){
			$(this)
				.find('.inline-edit').show().end()
				.find('.inline-ui').hide().end()
				.find('input[type=text]').focus().end()
				.find('.icon-link-cancel').click(function(){
					$(this).parents('.inline-editable')
						.find('.inline-edit').hide().end()
						.find('.inline-ui').show().end();
				});
		});
		
		/** CURRENCY AUTO COMPLETE **/
		$('.item_field_currency').autocomplete({
			source: currencies
		});
		var categoriesSourceUrl = $('.field-category input.category').attr('data-source');
		if( categoriesSourceUrl ){
			$.ajax({
				url: categoriesSourceUrl,
				dataType: 'json',
				success: function(data, status, xhr){
					var categories = data;
					console.log(categories);
					$('.field-category input.category').autocomplete({
						source: categories,
						minLength: 0
					});
				}
			})
		}
		
		/** Map updater **/
		$('.map-updater').click(function(){
			var address = $(this).siblings('input.address,textarea.address').val();
			
			return false;
		})
	};
	processPage();
});

