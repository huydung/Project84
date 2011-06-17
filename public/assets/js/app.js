var currencies = ["AED", "ANG", "ARS", "AUD", "BDT", "BGN", "BHD", "BND", "BOB", "BRL", "BWP", "CAD", "CHF", "CLP", "CNY", "COP", "CRC", "CZK", "DKK", "DOP", "DZD", "EEK", "EGP", "EUR", "FJD", "GBP", "HKD", "HNL", "HRK", "HUF", "IDR", "ILS", "INR", "ISK", "JMD", "JOD", "JPY", "KES", "KRW", "KWD", "KYD", "KZT", "LBP", "LKR", "LTL", "LVL", "MAD", "MDL", "MKD", "MUR", "MVR", "MXN", "MYR", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG", "QAR", "RON", "RSD", "RUB", "SAR", "SCR", "SEK", "SGD", "SKK", "SLL", "SVC", "THB", "TND", "TRY", "TTD", "TWD", "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "VEF", "VND", "XOF", "YER", "ZAR", "ZMK"];
/** Go To Top Link Plugins **/
jQuery.fn.topLink = function(settings) {
  settings = jQuery.extend({
    min: 1,
    fadeSpeed: 200
  }, settings);
  return this.each(function() {
    //listen for scroll
    var el = $(this);
    el.hide(); //in case the user forgot
    $(window).scroll(function() {
      if($(window).scrollTop() >= settings.min)
      {
        el.fadeIn(settings.fadeSpeed);
      }
      else
      {
        el.fadeOut(settings.fadeSpeed);
      }
    });
  });
};

$(document).ready(function(){
	
	/** default ajax callback **/
	var successAjax = function(data, status, xhr){
		$.gritter.add({
			title: 'Success',
			text: data,
			class_name: 'success'
		});
	};
	
	function processListingConfigurationPage(){
		/** sortable **/
		var fixHelper = function(e, ui) {
		    ui.children().each(function() {
		        $(this).width($(this).width());
		    });
		    return ui;
		};
		$('.sortable tbody,ul.sortable').sortable({
			'handle': '.drag-handler',
			helper: fixHelper,
			placeholder: 'ui-sortable-placeholder'
		}).disableSelection();
		
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
				$.post( '/project/'+project_id+'/listings/orderings/'+ids.join(','), null,
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
	};
	
	function processDefault(){
		
		$('body').ajaxError( function(event, xhr, settings){
			$.gritter.add({
				title: 'Error',
				text: 'Error occured when contacting ' + xhr.url,
				class_name: 'error'
			});
		});
		
		/** Default gritter options **/
		$.gritter.options = {position: 'bottom-right',time: 4000};
				
		/** Convert all flash messages to gritter **/
		$('#success,#warning,#info,#error').each(function(){
			var id = $(this).attr('id');
			$.gritter.add({
				title: id,
				text: $(this).html(),
				class_name: id,						
			});
		}).remove();
		
		/** Project switcher **/
		  $('.project_switcher select').change(function(){
		  		window.location.href = $(this).val();
		  });
		  
		  /** Enhance the menu in case of too many listings **/
		  var $menu = $('#menu');
		  var $menuul = $menu.find('ul.group');
		  var $menulis = $menuul.find('li');
		  
		  $menuul.width( ($menulis.width()) * $menulis.length );
		  //alert($menuul);
		  var margin = (($menu.width() - $menuul.width()) / ($menulis.length - 1));
		  //lay lis over each other to fit the room
		  if( margin < 0 ){ 
			  $menulis.each(function(){
				  if( !$(this).is('.first') ){
					  $(this).css('margin-left', margin);
				  };		  
			  });
		  };
		
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
		
		/** Fixed header of permissions table **/
		$('.permissions table').fixedtableheader();
		  
		/** Accordion & Tabs **/
		var stop = false;
		$( "#accordions .header" ).click(function( event ) {
			if ( stop ) {
				event.stopImmediatePropagation();
				event.preventDefault();
				stop = false;
			};
		});
		$('#accordions').accordion({
			header: '.header',
			collapsible: true,
			active: false,
			autoHeight: false
		});
		$('#form-item #accordions').accordion("activate", 0);
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
		$('.need-confirm').confirm({
			timeout:5000,
			wrapper:'<span class="confirmation fright"></span>'
		});		
		
		/** SHOW ALL/HIDE ALL **/
		$('a.showhide').each(function(){
			$this = $(this);
			var showText = $this.attr('data-showText');
			var hideText = $this.attr('data-hideText');
			var clazz = $this.attr('data-class');
			$this.click(function(){
				if( $this.text() === showText ){
					$('.' + clazz).show();
					$this.text(hideText)
						.addClass('icon-link-open')
						.removeClass('icon-link-collapse');					
				}else{
					$('.' + clazz).hide();
					$this.text(showText)
						.removeClass('icon-link-open')
						.addClass('icon-link-collapse');	
				}
			});			
			return false;
		});
		
		/** Convert modal actions in widget to use AJAX **/
		$('.modal').click(function(){
			$this = $(this);
			$('body').append('<div id="modal-dialog"></div>');
			$('#modal-dialog').load($this.attr('href'), function(response, status, xhr){
				if(status != "error"){			
					$('#modal-dialog').dialog({
						modal: true,
						title: $this.text(),
						close: function(){
							$('#modal-dialog').remove();
						},
						width: 600
					});
				}else{					
					$.gritter.add({
						title: 'Oops',
						text: response,
						class_name: 'error'						
					});
				};	
			});
			return false;			
		});
		
		$('.gototop').topLink({
			min: 500,
			fadeSpeed: 500
		});
	};
	
	function processProjectDashboard(){
		/** Make all portlets equal in height **/
		var portlet_max_height = 0;
		$('#portlets .widget-container').each(function(){
			var h = $(this).height();
			portlet_max_height = portlet_max_height < h ? h : portlet_max_height;
			//alert(portlet_max_height + 30);
		}).height(portlet_max_height + 30);
	};
	
	function bindEventToItem(el){
		$item = $(el);
		/** INLINE EDIT **/
		$item.find('.inline-edit').hide().end()
		.dblclick(function(){
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
		
		/** When item is checked **/
		$('input[type=checkbox]', $item).click(function(){
			var $this = $(this);

			//submit to server
			var lid = $this.parents('ul.items').attr('data-listing-id');
			var pid = $this.parents('ul.items').attr('data-project-id');
			var id = $this.parents('li').attr('data-id');
			var checked = $this.is(':checked');
			$.post(
				hd.itemCheckAction({
					project_id: pid, listing_id: lid, item_id: id, checked: checked
				}), null,successAjax
			);
			//add/remove class on the wrapper
			if( checked ){
				$this.parents('li').addClass('checked');
			}else{
				$this.parents('li').removeClass('checked');
			};	

		});
		
		/** CLICK TITLE TO TOGGLE SUBINFO **/
		$('.title', $item).click(function(){
			var si = $(this).siblings('.subinfo');
			if( si.is(':visible') ){
				si.hide();
			}else{
				si.show();
			}					
		});
		
		/** DROPPABLE **/
		$item.droppable({
		  	hoverClass: "item-drop-active",
			drop: function( event, ui ) {
				//console.log(ui);
				var data = $(ui.draggable).attr('data-drag');
				var $this = $(this);
				var lid = $this.parents('ul.items').attr('data-listing-id');
				var pid = $this.parents('ul.items').attr('data-project-id');
				var id = $this.attr('data-id');
				var url = '' + hd.itemUpdateAction({
					project_id: pid, listing_id: lid, item_id: id
				});
				$.post(url, {data: data}, function(response, status, xhr){
					var id = $this.attr('id');
					$this.replaceWith(response);
					var $newel = $('#'+id).addClass('item-drop-active', 2000, function(){
						$(this).removeClass('item-drop-active', 2000);
					});
					$.gritter.add({
						title: 'Success',
						class_name: 'success',
						text: "Item <em>" + $newel.find('.title').text() + "</em> has been updated!" 
					});
					bindEventToItem($newel.get());
				});
			}
	    });
	};
	
	function processItemListings(){
		/** Quick Add  **/
		  $('form#quick-add-form').submit(function(){
			 var $this = $(this);			 
			 $.post($this.attr('action'), $this.serialize(), function(response, status, xhr){
				 $('ul.items').prepend(response);
				 var $newel = $('ul.items li.item').eq(0);
				 $.gritter.add({
					title: 'Success',
					class_name: 'success',
					text: "Item <em>" + $newel.find('.title').text() + "</em> has been created!" 
				});
				bindEventToItem($newel.get());
				$this.find('input.item-quick-add').val('');
			 });
			 return false;
		  });
		  
		  $('ul.items li.item').each(function(){
			  bindEventToItem(this); 
		  });
	};
	
	function processFilterBoxes(){
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
		});
		
		/** Drag and Drop to change item field **/
		$('.draggable').draggable({revert:"valid"});
	};
	
	function processItemForm(){
		/** CURRENCY AUTO COMPLETE **/
		$('.item_field_currency').autocomplete({
			source: currencies
		});
		var categoriesSourceUrl = $('.field-category input.category').attr('data-source');
		if( categoriesSourceUrl ){
			$.get(categoriesSourceUrl, function(data, status, xhr){
				var categories = data;
				console.log(categories);
				$('.field-category input.category').autocomplete({
					source: categories,
					minLength: 0
				});
			});
		};	
		
		/** WYM EDITOR **/
		$('.wymeditor').wymeditor({
			skin: 'compact',
			logoHtml: '',
			toolsItems: [
	             {'name': 'Bold', 'title': 'Strong', 'css': 'wym_tools_strong'}, 
	             {'name': 'Italic', 'title': 'Emphasis', 'css': 'wym_tools_emphasis'},
	             {'name': 'Superscript', 'title': 'Superscript',
	                 'css': 'wym_tools_superscript'},
	             {'name': 'Subscript', 'title': 'Subscript',
	                 'css': 'wym_tools_subscript'},
	             {'name': 'InsertOrderedList', 'title': 'Ordered_List',
	                 'css': 'wym_tools_ordered_list'},
	             {'name': 'InsertUnorderedList', 'title': 'Unordered_List',
	                 'css': 'wym_tools_unordered_list'},           
	             
	             {'name': 'CreateLink', 'title': 'Link', 'css': 'wym_tools_link'},
	             {'name': 'Unlink', 'title': 'Unlink', 'css': 'wym_tools_unlink'},
	             {'name': 'InsertImage', 'title': 'Image', 'css': 'wym_tools_image'},	             
	             {'name': 'Paste', 'title': 'Paste_From_Word',
	                 'css': 'wym_tools_paste'},
	             {'name': 'ToggleHtml', 'title': 'HTML', 'css': 'wym_tools_html'},
	             {'name': 'Preview', 'title': 'Preview', 'css': 'wym_tools_preview'}
	         ],
	         classesHtml: '',
	         containersHtml: ''
		});
		
		/** Combo box **/
		$('.select-create-new').click(function(){
			var $this = $(this).hide();
			var $selected = $this.siblings('select.editable-select');
			$selected.val('').attr('name', '');
			var $input = $this.siblings('input');
			$input.show().removeAttr('disabled').focus();
			return false;
		});
		$('.editable-select').change(function(){
			var $this = $(this);			
			if($this.val() !== '' ){
				var $input = $this.siblings('input');
				$input.hide().attr('disabled', 'disabled');
				$this.attr('name', $input.attr('name'));
				$this.siblings('a.select-create-new').show();
			};
		});
		
		/** Save AND Create action **/
		$('.saveAndCreate').click(function(){
			$('#saveAndCreate').val('true');
		});		
	};
	
	function processPage(){
		processDefault();
		processListingConfigurationPage();
		processProjectDashboard();
		processItemListings();
		processFilterBoxes();
		processItemForm();
	};
	
	processPage();
});

