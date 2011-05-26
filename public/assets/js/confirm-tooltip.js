(function( $ ){
  $.fn.confirmTooltip = function() {
    return this.each(function() {
    	//add some CSS to this element
    	this.css({position: 'relative', overflow: 'visible'})
    		.addClass('needConfirm').
    		.append(
    			'<div class="confirmTooltip" ' 
    				+ 'style="display: none; position: absolute; height: 25px; top: -25px; left: 0;">'
    				+ 'Really??? <a href="#" class="yes">Yes!</a> | <a href="#" class="no">Ooops, No no.</a>'
    			+'</div>'	
    		);
    	this.click(function(){
    		$(this).find('.confirmTooltip').show();
    		return false;
    	});
    	this.find('.no').click(function(){
    		$(this).parent('.confirmTooltip').hide();
    		return false;
    	});
    	this.find('.yes').click(function(){
    		var parent = $(this).parent();
    		var href = parent.attr('href');
    		if( href != '' && href != '#' ){
    			window.location.href = href;
    		}else{
    			var form = parent.parents('form');
    			if( form.length > 0 ){
    				form.eq(0).submit();
    			}
    		}
    		return false;
    	});
    });
  };
})( jQuery );