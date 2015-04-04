;
/**
 * @file
 * Disabling dropdowns main menu parent link
 * 
 */
 
jQuery(document).ready(function() {
  jQuery('.menuparent > ul').prev('a').click(function(e) {  
    //e.preventDefault()
    return false;
  }); 
});;
