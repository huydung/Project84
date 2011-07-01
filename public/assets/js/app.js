var currencies = ["AED", "ANG", "ARS", "AUD", "BDT", "BGN", "BHD", "BND", "BOB", "BRL", "BWP", "CAD", "CHF", "CLP", "CNY", "COP", "CRC", "CZK", "DKK", "DOP", "DZD", "EEK", "EGP", "EUR", "FJD", "GBP", "HKD", "HNL", "HRK", "HUF", "IDR", "ILS", "INR", "ISK", "JMD", "JOD", "JPY", "KES", "KRW", "KWD", "KYD", "KZT", "LBP", "LKR", "LTL", "LVL", "MAD", "MDL", "MKD", "MUR", "MVR", "MXN", "MYR", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG", "QAR", "RON", "RSD", "RUB", "SAR", "SCR", "SEK", "SGD", "SKK", "SLL", "SVC", "THB", "TND", "TRY", "TTD", "TWD", "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "VEF", "VND", "XOF", "YER", "ZAR", "ZMK"];
/** Go To Top Link Plugins * */
jQuery.fn.topLink = function(settings) {
  settings = jQuery.extend({
    min: 1,
    fadeSpeed: 200
  }, settings);
  return this.each(function() {
    // listen for scroll
    var el = $(this);
    el.hide(); // in case the user forgot
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
	
	/** default ajax callback * */
	var successAjax = function(data, status, xhr){
		$.gritter.add({
			title: 'Success',
			text: data,
			class_name: 'success'
		});
	};
	
	function processListingConfigurationPage(){
		/** sortable * */
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
		
		/** PROJECT LISTINGS SORTING * */
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
		
		/** Icon Chooser Components * */
		// Fills Icons in the modal-dialog
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
		
		/** Checkbox interactivity in Project Configuration page * */
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
		
		/** Default gritter options * */
		$.gritter.options = {position: 'bottom-right',time: 4000};
				
		/** Convert all flash messages to gritter * */
		$('#success,#warning,#info,#error').each(function(){
			var id = $(this).attr('id');
			$.gritter.add({
				title: id,
				text: $(this).html(),
				class_name: id,						
			});
		}).remove();
		
		/** Project switcher * */
		  $('.project_switcher select').change(function(){
		  		window.location.href = $(this).val();
		  });
		  
		  /** Enhance the menu in case of too many listings * */
		  var $menu = $('#menu');
		  var $menuul = $menu.find('ul.group');
		  var $menulis = $menuul.find('li');
		  
		  $menuul.width( ($menulis.width()) * $menulis.length );
		  var margin = (($menu.width() - $menuul.width()) / ($menulis.length - 1));
		  // lay lis over each other to fit the room
		  if( margin < 0 ){ 
			  $menulis.each(function(){
				  if( !$(this).is('.first') ){
					  $(this).css('margin-left', margin);
				  };		  
			  });
		  };
		
		/** Date Picker * */
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
		
		
		/** Fixed header of permissions table * */
		$('.permissions table').fixedtableheader();
		  
		/** Accordion & Tabs * */
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
		
		$('#form-item #accordions,.listing_features #accordions').accordion("activate", 0);
		$('#tabs').tabs();
		
		
		/** Hide elements that will be enhanced or reveal by Javascript * */
		$('.enhanced, .reveal').hide();
		$('.reveal-toggle').click(function(){
			$target = $($(this).attr('href'));
			$target.is(':visible') ? $target.hide() :  $target.show();
			return false;
		});		
		
		/** Allow to click on label to select * */
		$('label,.label').css({cursor:'pointer'}).click(function(){
			$(this).next('input[type="checkbox"]').click().end()
					.prev('input[type="checkbox"]').click();
			return false;
		});
		
		/** Convert delete action link to have confirmation * */
		$('.need-confirm').confirm({
			timeout:5000,
			wrapper:'<span class="confirmation fright"></span>'
		});		
		
		/** SHOW ALL/HIDE ALL * */
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
		
		/** Convert modal actions in widget to use AJAX * */
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
		/** Make all portlets equal in height * */
		var portlet_max_height = 0;
		$('#portlets .widget-column-1').each(function(){
			var h = $(this).height();
			portlet_max_height = portlet_max_height < h ? h : portlet_max_height;
		}).height(portlet_max_height + 30);
	};
	
	function bindEventToItem(el, id){
		var $item = $(el);
		var id = id || $item.attr('id');
		var item_id = id.substr(5);

		var updateSuccess = function(response, status, xhr){
			$('#' + id).replaceWith(response);
			var $newel = $('#' + id);
			$newel.addClass('item-drop-active', 2000, function(){
				$(this).removeClass('item-drop-active', 2000);
			});
			$.gritter.add({
				title: 'Success',
				class_name: 'success',
				text: "Item <em>" + $newel.find('.title').text() + "</em> has been updated!" 
			});
			bindEventToItem($newel.get());
		 };		
		
		/** INLINE EDIT * */
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
		
		/** INLINE EDIT SUBMIT * */
		$('.inline-edit-form', $item).submit(function(){
			 var $this = $(this); 
			 $.post($this.attr('action'), $this.serialize(), updateSuccess);
			 return false;			  
		});
		
		/** When item is checked * */
		var afterChecked = function(){
			var $this = $(this);
			var checked = $this.is(':checked');
			$.post(
				hd.itemCheckAction({
					item_id: item_id, checked: checked
				}), null,successAjax
			);
			if( checked ){
				$item.addClass('checked');
			}else{
				$item.removeClass('checked');
			};
		};
		$('input[type=checkbox]', $item).click(afterChecked);
		
		/** CLICK TITLE TO TOGGLE SUBINFO * */
		$('.title', $item).click(function(){
			var si = $(this).siblings('.subinfo');
			if( si.is(':visible') ){
				si.hide();
			}else{
				si.show();
			}					
		});
		
		/** DROPPABLE * */
		var afterDrop = function( event, ui ) {
			var $dropped = $(ui.draggable);
			var data = $dropped.attr('data-drag');
			if( !data ){
				var classes = $dropped.parent('td').attr('class').split(' ');
				console.log(classes);
				for( var i = 0; i < classes.length; i++ ){					
					if( classes[i].indexOf('draggable-data-') == 0 ){
						data = classes[i].replace('draggable-data-', '');						
						break;
					}
				};
			};
			if( data ){
				var url = '' + hd.itemUpdateAction({
					item_id: item_id
				});
				$.post(url, {data: data}, updateSuccess);
			};	
		};
		
		$item.droppable({
		  	hoverClass: "item-drop-active",
			drop: afterDrop
	    });
		/** Draggable for Item to be deleted by dragging on recycle bin **/
		var item_name = $item.find('.title').text();
		$item.draggable({revert:true,
			helper: function( event ){
				return $( '<div class="highlight" style="width:64px;height:64px;font-size:10px">' + item_name + '</div>' );
			},
			cursorAt: {cursor:'move', top: 10, left: 10},
			zIndex: 50,
			appendTo : 'body'
		});
	};
	
	function processItemListings(){
		/** Quick Add * */
		  $('form#quick-add-form').submit(function(){
			 var $this = $(this);			 
			 $.post($this.attr('action'), $this.serialize(), function(response, status, xhr){
				 $('ul.items').prepend(response);
				 $('.no-result').remove();
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
		  
		 /** DRAG AND DROP DATE ON ITEM **/
		$('#inline_date_picker').datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: $('#inline_date_picker').attr("data-format"),
			beforeShowDay: function(date){
				return [true, 'draggable-date-cell draggable-data-date:' + $.datepicker.formatDate(hd.dateFormat, date) ,''];
			}
		});
		
		$('#inline_date_picker').delegate('.draggable-date-cell a', 'mouseenter', function(){
			$(this).draggable({
				revert:true,
				cursorAt: {cursor:'move', top: 10, left: 10},
				zIndex: 100,
				helper: 'clone', 
				appendTo : 'body'
			});
		});
		
		/** Drag and Drop File Upload **/
		hd.fileUploads = 0;
		hd.fileResponsed = 0;
		var $uploader = $('#file-uploader');
		$uploader.fileupload({
			dropZone: $('#file-uploader'),
			url: $uploader.attr('data-url'),
			fileInput: null,
			singleFileUploads: true,
			multipart: true
		}).bind('fileuploadalways', function (e, data) {
			hd.fileResponsed ++;
			if( hd.fileResponsed == hd.fileUploads ){				
				hd.fileResponsed = 0;
				hd.fileUploads = 0;
				$uploader.text( 'Reloading this page to display newly uploaded items...' );
				window.location.href = window.location.href;
			}
		}).bind('fileuploadprogressall', function (e, data) {
			var progress = parseInt(data.loaded / data.total * 100, 10);
			$uploader.text('Uploading ' + hd.fileUploads + ' files: ' + progress + '%');
		}).bind('fileuploaddrop', function (e, data) {
			$.each(data.files, function (index, file) {
		        hd.fileUploads ++;
		    });
			$uploader
				.css('background-color', '#FFFFAA');
	
		}).bind('fileuploaddragover', function (e) {
			$uploader.css('background-color', '#FF0084');
		});
		
		/** Drag and Drop to delete **/
		$recyclebin = $('#recycle_bin');
		$recyclebin.droppable({
			hoverClass: "item-drop-active",
			accept: 'li.item',
			drop: function(event, ui){
				var $dropped = $(ui.draggable);
				var id = $dropped.attr('data-id');
				if( id ){
					var url = '' + hd.itemDeleteAction({
						item_id: id
					});
					$.post(url, null, function(response, status, xhr){
						$('#item-' + id).remove();
						$.gritter.add({
							title: 'Deleted',
							class_name: 'success',
							text: response 
						});
					});
				};	
			}
		});
	};
	
	function processFilterBoxes(){
		/** CHECK-UNCHECK ALL * */
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
		
		/** Drag and Drop to change item field * */
		$('.draggable').draggable({
			revert:true,
			cursorAt: {cursor:'move', top: 10, left: 10},
			zIndex: 100,
			helper: 'clone', 
			appendTo : 'body'
		});
	};
	
	function processItemForm(){
		/** CURRENCY AUTO COMPLETE * */
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
		
		/** WYM EDITOR * */		
		$('.wymeditor').wysiwyg({
			resizeOptions: {},
			controls: {
				h1: { visible: false },
				h2: { visible: false },
				h3: { visible: false }
			},
			autoGrow: true,
			maxHeight: 500,
			formWidth: 600,
			initialContent: ''
		});
		/** Combo box * */
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
		
		/** Save AND Create action * */
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

